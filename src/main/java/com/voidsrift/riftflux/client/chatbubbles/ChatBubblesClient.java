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
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ChatBubblesClient {
    private static final ChatBubblesClient INSTANCE = new ChatBubblesClient();

    private final ArrayList<ChatBubbleMessage> messages = new ArrayList<ChatBubbleMessage>();
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

        String[] authorText = parseLine(pare(scrubCodes(message)));
        if (authorText[0].isEmpty() || authorText[1].isEmpty()) {
            return;
        }

        String[] messageLines = formatMessage(authorText[1], Math.max(8, ModConfig.chatBubblesMaxLineLength));
        messages.add(0, new ChatBubbleMessage(authorText[0], messageLines, mc.ingameGUI.getUpdateCounter()));
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

        List<ChatBubbleMessage> relevant = getMessagesByAuthor(author);
        if (relevant.isEmpty()) {
            return;
        }

        ChatBubbleRenderer.render(
                player,
                event.x,
                event.y,
                event.z,
                relevant,
                Math.max(20, ModConfig.chatBubblesMessageLifetimeSeconds * 20)
        );
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
        return string.replaceAll("(\u00A7.)", "");
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

    private ArrayList<ChatBubbleMessage> getMessagesByAuthor(String author) {
        ArrayList<ChatBubbleMessage> relevantMessages = new ArrayList<ChatBubbleMessage>();
        for (ChatBubbleMessage message : messages) {
            if (message.getAuthor().equals(author)) {
                relevantMessages.add(message);
            }
        }
        return relevantMessages;
    }

    private void clearTransientClientState() {
        messages.clear();
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
        File parent = settingsFile.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }

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
            }
        } catch (Exception e) {
            System.out.println("ChatBubbles regex load error: " + e.getLocalizedMessage());
        }

        try {
            PrintWriter out = new PrintWriter(new FileWriter(settingsFile));
            try {
                Set<Map.Entry<String, ChatParseLine>> lines = customParseLines.entrySet();
                for (Map.Entry<String, ChatParseLine> entry : lines) {
                    ChatParseLine line = entry.getValue();
                    StringBuilder nameRefs = new StringBuilder();
                    int[] refs = line.getNameRefs();
                    for (int i = 0; i < refs.length; i++) {
                        if (i > 0) {
                            nameRefs.append(',');
                        }
                        nameRefs.append(refs[i]);
                    }
                    out.println(entry.getKey() + " " + line.getRegex() + " " + nameRefs + " " + line.getTextRef());
                }
            } finally {
                out.close();
            }
        } catch (Exception e) {
            System.out.println("ChatBubbles regex write error: " + e.getLocalizedMessage());
        }
    }

    private static String pare(String string) {
        return string == null ? "" : string.replaceAll("(.)\\1{6,}+", "$1$1$1$1$1$1");
    }

    private String[] parseLine(String chatText) {
        String[] authorText = new String[]{"", ""};

        Pattern pattern = Pattern.compile("^(?:\\[[^\\]]*\\]\\s*)*<(?:[^>]*[^>\\w])?(\\w{2,16})(?:\\s*\\([^\\)]*\\))?>+(.*)");
        Matcher matcher = pattern.matcher(chatText);
        if (matcher.find()) {
            authorText[0] = matcher.group(1);
            authorText[1] = matcher.group(2);
        } else {
            pattern = Pattern.compile("^(?:(?:\\[[^\\]]*\\]|(?:([^\\w\\s]?)([^\\w\\s])(?:(?!\\2).)+\\2\\1))\\s*)*(\\w{2,16})(?:\\s*\\([^\\)]*\\))?:(.*)");
            matcher = pattern.matcher(chatText);
            if (matcher.find()) {
                authorText[0] = matcher.group(3);
                authorText[1] = matcher.group(4);
            } else {
                pattern = Pattern.compile("^(?:(?:\\[[^\\]]*\\]|(?:([^\\w\\s]?)([^\\w\\s])(?:(?!\\2).)+.*\\2\\1))\\s*)*([\\W&&\\S])(\\w{2,16})\\3+(?:\\s*\\([^\\)]*\\))?:?(.*)");
                matcher = pattern.matcher(chatText);
                if (matcher.find()) {
                    authorText[0] = matcher.group(4);
                    authorText[1] = matcher.group(5);
                } else {
                    pattern = Pattern.compile("^([^\\*]?)[\\*]*\\w*\\s*(?:\\1\\[[^\\]]*\\])?[\\s]*(\\w{2,16})(?::|(?:\\s*>))(.*)");
                    matcher = pattern.matcher(chatText);
                    if (matcher.find()) {
                        authorText[0] = matcher.group(2);
                        authorText[1] = matcher.group(3);
                    } else {
                        pattern = Pattern.compile("(?:[^:]*[^:\\w])?(\\w{2,16})(?:\\s*\\([^\\)]*\\))?(?::|(?:\\s*>))(.*)");
                        matcher = pattern.matcher(chatText);
                        if (matcher.find()) {
                            authorText[0] = matcher.group(1);
                            authorText[1] = matcher.group(2);
                        }
                    }
                }
            }
        }

        if (customChatParseLine != null) {
            pattern = Pattern.compile(customChatParseLine.getRegex());
            matcher = pattern.matcher(chatText);
            if (matcher.find()) {
                int[] possibleAuthorRefs = customChatParseLine.getNameRefs();
                for (int i = 0; i < possibleAuthorRefs.length; i++) {
                    String possibleAuthor = matcher.group(possibleAuthorRefs[i]);
                    if (possibleAuthor != null) {
                        authorText[0] = possibleAuthor;
                    }
                }
                authorText[1] = matcher.group(customChatParseLine.getTextRef());
            }
        }

        authorText[0] = authorText[0] == null ? "" : authorText[0].trim();
        authorText[1] = authorText[1] == null ? "" : authorText[1].trim();
        return authorText;
    }

    private static String[] formatMessage(String message, int maxLineLength) {
        StringTokenizer tokenizer = new StringTokenizer(message, " ");
        StringBuilder output = new StringBuilder(message.length());
        int lineLen = 0;

        while (tokenizer.hasMoreTokens()) {
            String word = tokenizer.nextToken();
            if (lineLen + word.length() > maxLineLength) {
                if (lineLen != 0) {
                    output.append("~break~");
                    lineLen = 0;
                }
                while (lineLen == 0 && word.length() > maxLineLength) {
                    output.append(word.substring(0, maxLineLength));
                    output.append("-~break~");
                    word = word.substring(maxLineLength);
                }
            }
            if (lineLen != 0) {
                output.append(' ');
                lineLen++;
            }
            output.append(word);
            lineLen += word.length();
        }

        return output.toString().split("~break~");
    }
}
