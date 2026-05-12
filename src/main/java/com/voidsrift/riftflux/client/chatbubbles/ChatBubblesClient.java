package com.voidsrift.riftflux.client.chatbubbles;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.chatbubbles.ChatBubbleColorManager;
import com.voidsrift.riftflux.net.MsgSetChatBubbleColor;
import com.voidsrift.riftflux.net.MsgSetChatBubbleTextColor;
import com.voidsrift.riftflux.net.RFNetwork;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.common.MinecraftForge;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;

public final class ChatBubblesClient {
    private static final ChatBubblesClient INSTANCE = new ChatBubblesClient();
    private static final ChatParseLine[] DEFAULT_PARSE_LINES = new ChatParseLine[]{
            new ChatParseLine("^(?:\\[[^\\]]*\\]\\s*)*<(?:[^>]*[^>\\w])?(\\w{2,16})(?:\\s*\\([^\\)]*\\))?>+(.*)"),
            new ChatParseLine("^(?:(?:\\[[^\\]]*\\]|(?:([^\\w\\s]?)([^\\w\\s])(?:(?!\\2).)+\\2\\1))\\s*)*(\\w{2,16})(?:\\s*\\([^\\)]*\\))?:(.*)", 3, 4),
            new ChatParseLine("^(?:(?:\\[[^\\]]*\\]|(?:([^\\w\\s]?)([^\\w\\s])(?:(?!\\2).)+.*\\2\\1))\\s*)*([\\W&&\\S])(\\w{2,16})\\3+(?:\\s*\\([^\\)]*\\))?:?(.*)", 4, 5),
            new ChatParseLine("^([^\\*]?)[\\*]*\\w*\\s*(?:\\1\\[[^\\]]*\\])?[\\s]*(\\w{2,16})(?::|(?:\\s*>))(.*)", 2, 3),
            new ChatParseLine("(?:[^:]*[^:\\w])?(\\w{2,16})(?:\\s*\\([^\\)]*\\))?(?::|(?:\\s*>))(.*)")
    };

    private final ArrayList<ChatBubbleMessage> messages = new ArrayList<ChatBubbleMessage>();
    private final ArrayList<ChatBubbleMessage> relevantMessages = new ArrayList<ChatBubbleMessage>();
    private final TreeMap<String, ChatParseLine> customParseLines =
            new TreeMap<String, ChatParseLine>(String.CASE_INSENSITIVE_ORDER);

    private boolean bootstrapped;
    private boolean advertisedConfiguredColor;
    private boolean advertisedConfiguredTextColor;
    private String serverName = "";
    private ChatParseLine customChatParseLine;
    private boolean lastAdvertisedConfiguredColorSet;
    private int lastAdvertisedConfiguredColor = Integer.MIN_VALUE;
    private boolean lastAdvertisedConfiguredTextColorSet;
    private int lastAdvertisedConfiguredTextColor = Integer.MIN_VALUE;
    private int worldIdentity;

    private ChatBubblesClient() {
    }

    public static void bootstrap() {
        if (INSTANCE.bootstrapped) {
            return;
        }
        INSTANCE.bootstrapped = true;
        INSTANCE.loadCustomParseLines();
        MinecraftForge.EVENT_BUS.register(INSTANCE);
        FMLCommonHandler.instance().bus().register(INSTANCE);
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (!ModConfig.enableChatBubblesModule || event.phase != TickEvent.Phase.END) {
            return;
        }

        Minecraft mc = Minecraft.getMinecraft();
        if (mc == null || mc.ingameGUI == null) {
            return;
        }

        refreshWorldState(mc);
        refreshServerState(mc);
        advertiseConfiguredColor(mc);
        advertiseConfiguredTextColor(mc);
        pruneExpiredMessages(mc.ingameGUI.getUpdateCounter());
    }

    @SubscribeEvent
    public void onChatReceived(ClientChatReceivedEvent event) {
        if (!ModConfig.enableChatBubblesModule || event == null || event.message == null) {
            return;
        }

        Minecraft mc = Minecraft.getMinecraft();
        if (mc == null || mc.ingameGUI == null) {
            return;
        }

        String message = event.message.getFormattedText();
        if (message == null || message.isEmpty()) {
            return;
        }

        ParsedChatLine parsed = parseLine(pare(scrubCodes(message)));
        if (parsed.author.isEmpty() || parsed.text.isEmpty()) {
            return;
        }

        String[] messageLines = formatMessage(parsed.text, Math.max(8, ModConfig.chatBubblesMaxLineLength));
        messages.add(0, new ChatBubbleMessage(parsed.author, messageLines, mc.ingameGUI.getUpdateCounter()));
    }

    @SubscribeEvent
    public void onRenderLivingPost(RenderLivingEvent.Post event) {
        if (!ModConfig.enableChatBubblesModule || !(event.entity instanceof EntityPlayer)) {
            return;
        }

        EntityPlayer player = (EntityPlayer) event.entity;
        String author = scrubCodes(player.getCommandSenderName());
        if (author == null || author.isEmpty()) {
            return;
        }

        populateMessagesByAuthor(author, relevantMessages);
        if (relevantMessages.isEmpty()) {
            return;
        }

        ChatBubbleRenderer.render(
                player,
                event.x,
                event.y,
                event.z,
                relevantMessages,
                Math.max(20, ModConfig.chatBubblesMessageLifetimeSeconds * 20)
        );
        relevantMessages.clear();
    }

    @SubscribeEvent
    public void onClientDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        clearTransientClientState();
        serverName = "";
        customChatParseLine = null;
        worldIdentity = 0;
    }

    static String scrubCodes(String string) {
        if (string == null) {
            return "";
        }
        int index = string.indexOf('\u00A7');
        if (index < 0) {
            return string;
        }

        StringBuilder output = new StringBuilder(string.length());
        output.append(string, 0, index);
        for (int i = index; i < string.length(); i++) {
            char c = string.charAt(i);
            if (c == '\u00A7') {
                i++;
            } else {
                output.append(c);
            }
        }
        return output.toString();
    }

    private void refreshServerState(Minecraft mc) {
        String newServerName = "";
        if (!mc.isSingleplayer()) {
            ServerData serverData = mc.func_147104_D();
            if (serverData != null && serverData.serverIP != null) {
                newServerName = serverData.serverIP.toLowerCase();
            }
        }
        if (!serverName.equals(newServerName)) {
            serverName = newServerName;
            customChatParseLine = customParseLines.get(serverName);
            clearTransientClientState();
        }
    }

    private void refreshWorldState(Minecraft mc) {
        int newWorldIdentity = mc.theWorld == null ? 0 : System.identityHashCode(mc.theWorld);
        if (newWorldIdentity != worldIdentity) {
            worldIdentity = newWorldIdentity;
            clearTransientClientState();
        }
    }

    private void advertiseConfiguredColor(Minecraft mc) {
        if (mc.thePlayer == null || RFNetwork.CH == null) {
            return;
        }

        boolean hasColor = ModConfig.chatBubblesUseCustomOwnBubbleColor;
        int color = ModConfig.chatBubblesOwnBubbleColor & 0xFFFFFF;
        if (advertisedConfiguredColor && lastAdvertisedConfiguredColorSet == hasColor
                && (!hasColor || lastAdvertisedConfiguredColor == color)) {
            return;
        }

        RFNetwork.CH.sendToServer(new MsgSetChatBubbleColor(hasColor, color));
        advertisedConfiguredColor = true;
        lastAdvertisedConfiguredColorSet = hasColor;
        lastAdvertisedConfiguredColor = color;
    }

    private void advertiseConfiguredTextColor(Minecraft mc) {
        if (mc.thePlayer == null || RFNetwork.CH == null) {
            return;
        }

        boolean hasColor = ModConfig.chatBubblesUseCustomOwnTextColor;
        int color = ModConfig.chatBubblesOwnTextColor & 0xFFFFFF;
        if (advertisedConfiguredTextColor && lastAdvertisedConfiguredTextColorSet == hasColor
                && (!hasColor || lastAdvertisedConfiguredTextColor == color)) {
            return;
        }

        RFNetwork.CH.sendToServer(new MsgSetChatBubbleTextColor(hasColor, color));
        advertisedConfiguredTextColor = true;
        lastAdvertisedConfiguredTextColorSet = hasColor;
        lastAdvertisedConfiguredTextColor = color;
    }

    private void pruneExpiredMessages(int currentTick) {
        int lifetime = Math.max(20, ModConfig.chatBubblesMessageLifetimeSeconds * 20);
        while (!messages.isEmpty()
                && currentTick - messages.get(messages.size() - 1).getUpdateCounterCreated() >= lifetime) {
            messages.remove(messages.size() - 1);
        }
    }

    private void populateMessagesByAuthor(String author, List<ChatBubbleMessage> output) {
        output.clear();
        for (ChatBubbleMessage message : messages) {
            if (message.getAuthor().equals(author)) {
                output.add(message);
            }
        }
    }

    private void clearTransientClientState() {
        messages.clear();
        relevantMessages.clear();
        ChatBubbleColorManager.clearClientColors();
        advertisedConfiguredColor = false;
        advertisedConfiguredTextColor = false;
        lastAdvertisedConfiguredColor = Integer.MIN_VALUE;
        lastAdvertisedConfiguredTextColor = Integer.MIN_VALUE;
    }

    private void loadCustomParseLines() {
        customParseLines.clear();
        customParseLines.put("mc.thevoxelbox.com", new ChatParseLine(
                "^(?:[\\{\\[\\(<](\\w{2,16})[\\}\\]\\)>]:?|\\*?(\\w{2,16}):)(.*)",
                "1,2",
                3
        ));
        customParseLines.put("play.mc-sg.org", new ChatParseLine("^(?:<[^>]*>\\s*)?(\\w{2,16})\\s*>(.*)"));
        customParseLines.put("play.savagerealms.net", new ChatParseLine("^(?:\\[[^\\]]*\\]\\s*)*\\s*~?(\\w{2,16})\\s*(?:\\[[^\\]]*\\])?:\\s*(.*)"));
        customParseLines.put("rp.fr-minecraft.net", new ChatParseLine("^\\[(?:[^>]+>)?(\\w{2,16})\\|[^\\]]*\\]\\s*(.*)"));

        File settingsFile = new File(Minecraft.getMinecraft().mcDataDir, "mods/chatbubbles/customRegexes.txt");
        try {
            if (settingsFile.exists()) {
                BufferedReader in = new BufferedReader(new FileReader(settingsFile));
                try {
                    String currentLine;
                    while ((currentLine = in.readLine()) != null) {
                        String[] split = currentLine.split(" ");
                        ChatParseLine parseLine = null;
                        if (split.length == 2) {
                            parseLine = new ChatParseLine(split[1], 1, 2);
                        } else if (split.length == 3) {
                            parseLine = new ChatParseLine(split[1], split[2]);
                        } else if (split.length == 4) {
                            parseLine = new ChatParseLine(split[1], split[2], Integer.parseInt(split[3]));
                        }
                        if (parseLine != null) {
                            customParseLines.put(split[0], parseLine);
                        }
                    }
                } finally {
                    in.close();
                }
            } else {
                writeCustomParseLines(settingsFile);
            }
        } catch (Exception e) {
            System.out.println("ChatBubbles regex load error: " + e.getLocalizedMessage());
        }
    }

    private void writeCustomParseLines(File settingsFile) {
        File parent = settingsFile.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(settingsFile));
            try {
                Set<Map.Entry<String, ChatParseLine>> lines = customParseLines.entrySet();
                for (Map.Entry<String, ChatParseLine> entry : lines) {
                    ChatParseLine line = entry.getValue();
                    out.write(entry.getKey());
                    out.write(' ');
                    out.write(line.getRegex());
                    out.write(' ');
                    out.write(formatNameRefs(line.getNameRefs()));
                    out.write(' ');
                    out.write(Integer.toString(line.getTextRef()));
                    out.newLine();
                }
            } finally {
                out.close();
            }
        } catch (Exception e) {
            System.out.println("ChatBubbles regex write error: " + e.getLocalizedMessage());
        }
    }

    private static String pare(String string) {
        if (string == null || string.length() < 7) {
            return string == null ? "" : string;
        }

        StringBuilder output = null;
        char last = 0;
        int runLength = 0;
        for (int i = 0; i < string.length(); i++) {
            char c = string.charAt(i);
            if (i == 0 || c != last) {
                last = c;
                runLength = 1;
            } else {
                runLength++;
            }

            if (runLength <= 6) {
                if (output != null) {
                    output.append(c);
                }
            } else if (output == null) {
                output = new StringBuilder(string.length());
                output.append(string, 0, i);
            }
        }
        return output == null ? string : output.toString();
    }

    private ParsedChatLine parseLine(String chatText) {
        ParsedChatLine authorText = new ParsedChatLine();
        for (int i = 0; i < DEFAULT_PARSE_LINES.length; i++) {
            ChatParseLine parseLine = DEFAULT_PARSE_LINES[i];
            Matcher matcher = parseLine.getPattern().matcher(chatText);
            if (matcher.find()) {
                authorText.author = matcher.group(parseLine.getNameRefs()[0]);
                authorText.text = matcher.group(parseLine.getTextRef());
                break;
            }
        }

        if (customChatParseLine != null) {
            Matcher matcher = customChatParseLine.getPattern().matcher(chatText);
            if (matcher.find()) {
                int[] possibleAuthorRefs = customChatParseLine.getNameRefs();
                for (int i = 0; i < possibleAuthorRefs.length; i++) {
                    String possibleAuthor = matcher.group(possibleAuthorRefs[i]);
                    if (possibleAuthor != null) {
                        authorText.author = possibleAuthor;
                    }
                }
                authorText.text = matcher.group(customChatParseLine.getTextRef());
            }
        }

        authorText.author = authorText.author == null ? "" : authorText.author.trim();
        authorText.text = authorText.text == null ? "" : authorText.text.trim();
        return authorText;
    }

    private static String[] formatMessage(String message, int maxLineLength) {
        ArrayList<String> lines = new ArrayList<String>();
        StringBuilder line = new StringBuilder(Math.min(message.length(), maxLineLength));
        int lineLen = 0;
        int wordStart = -1;

        for (int i = 0; i <= message.length(); i++) {
            if (i < message.length() && message.charAt(i) != ' ') {
                if (wordStart < 0) {
                    wordStart = i;
                }
                continue;
            }

            if (wordStart >= 0) {
                lineLen = appendWord(lines, line, lineLen, message.substring(wordStart, i), maxLineLength);
                wordStart = -1;
            }
        }

        lines.add(line.toString());
        return lines.toArray(new String[lines.size()]);
    }

    private static int appendWord(ArrayList<String> lines, StringBuilder line, int lineLen, String word, int maxLineLength) {
        if (lineLen + word.length() > maxLineLength) {
            if (lineLen != 0) {
                lines.add(line.toString());
                line.setLength(0);
                lineLen = 0;
            }
            while (lineLen == 0 && word.length() > maxLineLength) {
                lines.add(word.substring(0, maxLineLength) + "-");
                word = word.substring(maxLineLength);
            }
        }
        if (lineLen != 0) {
            line.append(' ');
            lineLen++;
        }
        line.append(word);
        return lineLen + word.length();
    }

    private static String formatNameRefs(int[] refs) {
        StringBuilder nameRefs = new StringBuilder();
        for (int i = 0; i < refs.length; i++) {
            if (i > 0) {
                nameRefs.append(',');
            }
            nameRefs.append(refs[i]);
        }
        return nameRefs.toString();
    }

    private static final class ParsedChatLine {
        private String author = "";
        private String text = "";
    }
}
