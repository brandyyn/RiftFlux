package com.voidsrift.riftflux.command;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.chatbubbles.ChatBubbleColorManager;
import com.voidsrift.riftflux.net.MsgSetChatBubblesConfig;
import com.voidsrift.riftflux.net.RFNetwork;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class CommandRiftFlux extends CommandBase {
    private enum Mode {
        ROOT,
        CHAT_BUBBLE,
        CHAT_BUBBLE_TEXT,
        CHAT_BUBBLE_SIZE
    }

    private static final String[] ROOT_COMPLETIONS = new String[]{
            "chatbubble",
            "chatbubblestext",
            "cbt",
            "chatbubblesize",
            "chatbubbles",
            "chatbubbleconfig",
            "chatbubblephoto",
            "cbp"
    };
    private static final String[] CHAT_BUBBLE_DIRECT_COMPLETIONS = new String[]{
            "gold",
            "auto",
            "red",
            "green",
            "blue",
            "yellow",
            "orange",
            "black",
            "purple",
            "white",
            "cyan",
            "magenta",
            "gray",
            "light_gray",
            "color",
            "textcolor",
            "randomtextcolor",
            "size",
            "enabled",
            "showown",
            "background",
            "photomode",
            "gap",
            "blackbar",
            "blackbaropacity",
            "lifetime",
            "linelength",
            "config"
    };
    private static final String[] COLOR_COMPLETIONS = new String[]{
            "gold",
            "auto",
            "red",
            "green",
            "blue",
            "yellow",
            "orange",
            "black",
            "purple",
            "white",
            "cyan",
            "magenta",
            "gray",
            "light_gray"
    };
    private static final String[] SIZE_COMPLETIONS = new String[]{
            "0.75",
            "1.0",
            "1.25",
            "1.5",
            "2.0"
    };
    private static final String[] BOOLEAN_COMPLETIONS = new String[]{
            "true",
            "false"
    };
    private static final String[] GAP_COMPLETIONS = new String[]{
            "0",
            "1",
            "2"
    };
    private static final String[] OPACITY_COMPLETIONS = new String[]{
            "0.25",
            "0.5",
            "0.75",
            "1.0"
    };
    private static final String[] LIFETIME_COMPLETIONS = new String[]{
            "6",
            "12",
            "20"
    };
    private static final String[] LINE_LENGTH_COMPLETIONS = new String[]{
            "20",
            "30",
            "45"
    };
    private static final String[] CONFIG_SETTING_COMPLETIONS = new String[]{
            "enabled",
            "showown",
            "background",
            "photomode",
            "color",
            "textcolor",
            "randomtextcolor",
            "size",
            "gap",
            "blackbar",
            "blackbaropacity",
            "lifetime",
            "linelength"
    };

    private final String commandName;
    private final Mode mode;
    private final List aliases;

    public CommandRiftFlux() {
        this("riftflux", Mode.ROOT, "rf");
    }

    private CommandRiftFlux(String commandName, Mode mode, String... aliases) {
        this.commandName = commandName;
        this.mode = mode;
        this.aliases = Arrays.asList(aliases);
    }

    public static CommandRiftFlux chatBubbleCommand() {
        return new CommandRiftFlux("chatbubble", Mode.CHAT_BUBBLE, "cb");
    }

    public static CommandRiftFlux chatBubbleSizeCommand() {
        return new CommandRiftFlux("chatbubblesize", Mode.CHAT_BUBBLE_SIZE, "cbs");
    }

    public static CommandRiftFlux chatBubbleTextCommand() {
        return new CommandRiftFlux("chatbubblestext", Mode.CHAT_BUBBLE_TEXT, "cbt");
    }

    @Override
    public String getCommandName() {
        return commandName;
    }

    @Override
    public List getCommandAliases() {
        return aliases;
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        if (mode == Mode.CHAT_BUBBLE) {
            return "/chatbubble <color|auto> or /chatbubble <setting> <value>";
        }
        if (mode == Mode.CHAT_BUBBLE_TEXT) {
            return "/chatbubblestext <color|auto>";
        }
        if (mode == Mode.CHAT_BUBBLE_SIZE) {
            return "/chatbubblesize <" + ModConfig.CHAT_BUBBLES_TEXT_SCALE_MIN + "-" + ModConfig.CHAT_BUBBLES_TEXT_SCALE_MAX + ">";
        }
        return "/riftflux <chatbubble|chatbubblestext|chatbubblesize|chatbubbles|chatbubblephoto> ...";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (mode == Mode.CHAT_BUBBLE) {
            processChatBubbleCommand(sender, args, getCommandUsage(sender));
        } else if (mode == Mode.CHAT_BUBBLE_TEXT) {
            processChatBubbleTextCommand(sender, args, getCommandUsage(sender));
        } else if (mode == Mode.CHAT_BUBBLE_SIZE) {
            processChatBubbleSizeCommand(sender, args, getCommandUsage(sender));
        } else {
            processRootCommand(sender, args);
        }
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] args) {
        if (args == null) {
            return null;
        }
        if (mode == Mode.CHAT_BUBBLE) {
            return completeChatBubble(args, 0);
        }
        if (mode == Mode.CHAT_BUBBLE_TEXT) {
            return completeChatBubbleText(args, 0);
        }
        if (mode == Mode.CHAT_BUBBLE_SIZE) {
            return completeChatBubbleSize(args, 0);
        }
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, ROOT_COMPLETIONS);
        }
        String subCommand = args[0];
        if (isChatBubbleSubCommand(subCommand)) {
            return completeChatBubble(args, 1);
        }
        if (isChatBubbleTextSubCommand(subCommand)) {
            return completeChatBubbleText(args, 1);
        }
        if (isChatBubbleSizeSubCommand(subCommand)) {
            return completeChatBubbleSize(args, 1);
        }
        if (isChatBubbleConfigSubCommand(subCommand)) {
            return completeChatBubbleConfig(args, 1);
        }
        if (isChatBubblePhotoSubCommand(subCommand)) {
            return completeChatBubblePhoto(args, 1);
        }
        return null;
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    private static void processRootCommand(ICommandSender sender, String[] args) {
        if (args == null || args.length < 1) {
            throw new WrongUsageException("/riftflux <chatbubble|chatbubblestext|chatbubblesize|chatbubbles|chatbubblephoto> ...");
        }

        String subCommand = args[0];
        String[] remaining = dropFirst(args);
        if (isChatBubbleSubCommand(subCommand)) {
            processChatBubbleCommand(sender, remaining, "/riftflux chatbubble <color|auto>");
        } else if (isChatBubbleTextSubCommand(subCommand)) {
            processChatBubbleTextCommand(sender, remaining, "/riftflux chatbubblestext <color|auto>");
        } else if (isChatBubbleSizeSubCommand(subCommand)) {
            processChatBubbleSizeCommand(sender, remaining, "/riftflux chatbubblesize <" + ModConfig.CHAT_BUBBLES_TEXT_SCALE_MIN + "-" + ModConfig.CHAT_BUBBLES_TEXT_SCALE_MAX + ">");
        } else if (isChatBubbleConfigSubCommand(subCommand)) {
            processChatBubbleConfigCommand(sender, remaining, "/riftflux chatbubbles <setting> <value>");
        } else if (isChatBubblePhotoSubCommand(subCommand)) {
            processChatBubblePhotoCommand(sender, remaining, "/riftflux chatbubblephoto <true|false>");
        } else {
            throw new WrongUsageException("/riftflux <chatbubble|chatbubblestext|chatbubblesize|chatbubbles|chatbubblephoto> ...");
        }
    }

    private static void processChatBubbleCommand(ICommandSender sender, String[] args, String usage) {
        if (args == null || args.length < 1 || args[0] == null || args[0].trim().isEmpty()) {
            throw new WrongUsageException(usage);
        }

        String first = args[0].trim();
        if ("config".equalsIgnoreCase(first) || "settings".equalsIgnoreCase(first)) {
            processChatBubbleConfigCommand(sender, dropFirst(args), "/chatbubble config <setting> <value>");
            return;
        }
        if (args.length == 2 && isColorConfigKey(first)) {
            processChatBubbleColorValue(sender, args[1]);
            return;
        }
        if (args.length == 2 && isTextColorConfigKey(first)) {
            processChatBubbleTextColorValue(sender, args[1]);
            return;
        }
        if (args.length == 2 && isSizeConfigKey(first)) {
            processClientConfigCommand(sender, "size", args[1]);
            return;
        }
        if (args.length == 2 && ModConfig.isChatBubblesConfigKey(first)) {
            processChatBubbleConfigCommand(sender, args, "/chatbubble <setting> <value>");
            return;
        }
        if (args.length == 1 && ModConfig.isChatBubblesConfigKey(first)) {
            throw new WrongUsageException("/chatbubble " + first + " <value>");
        }
        if (args.length == 1) {
            processChatBubbleColorValue(sender, first);
            return;
        }

        throw new WrongUsageException(usage);
    }

    private static void processChatBubbleSizeCommand(ICommandSender sender, String[] args, String usage) {
        if (args == null || args.length < 1 || args[0] == null || args[0].trim().isEmpty()) {
            throw new WrongUsageException(usage);
        }
        if (args.length == 1) {
            processClientConfigCommand(sender, "size", args[0]);
            return;
        }
        if (args.length == 2 && isSizeConfigKey(args[0])) {
            processClientConfigCommand(sender, "size", args[1]);
            return;
        }
        throw new WrongUsageException(usage);
    }

    private static void processChatBubbleTextCommand(ICommandSender sender, String[] args, String usage) {
        if (args == null || args.length < 1 || args[0] == null || args[0].trim().isEmpty()) {
            throw new WrongUsageException(usage);
        }
        if (args.length == 1) {
            processChatBubbleTextColorValue(sender, args[0]);
            return;
        }
        if (args.length == 2 && isTextColorConfigKey(args[0])) {
            processChatBubbleTextColorValue(sender, args[1]);
            return;
        }
        throw new WrongUsageException(usage);
    }

    private static void processChatBubbleConfigCommand(ICommandSender sender, String[] args, String usage) {
        if (args == null || args.length != 2 || args[0] == null || args[1] == null) {
            throw new WrongUsageException(usage);
        }

        String setting = args[0].trim();
        String normalized = ModConfig.normalizeChatBubblesConfigKey(setting);
        if (normalized == null) {
            sender.addChatMessage(new ChatComponentText("Unknown chat bubble config: " + setting));
            sender.addChatMessage(new ChatComponentText("Use: enabled, showown, background, photomode, color, textcolor, randomtextcolor, size, gap, blackbar, blackbaropacity, lifetime, or linelength."));
            return;
        }
        if ("color".equals(normalized)) {
            processChatBubbleColorValue(sender, args[1]);
        } else if ("textcolor".equals(normalized)) {
            processChatBubbleTextColorValue(sender, args[1]);
        } else {
            processClientConfigCommand(sender, normalized, args[1]);
        }
    }

    private static void processChatBubblePhotoCommand(ICommandSender sender, String[] args, String usage) {
        if (args == null || args.length != 1 || args[0] == null || args[0].trim().isEmpty()) {
            throw new WrongUsageException(usage);
        }
        processClientConfigCommand(sender, "photomode", args[0]);
    }

    private static void processChatBubbleColorValue(ICommandSender sender, String rawValue) {
        EntityPlayer player = requirePlayer(sender, "set a RiftFlux chat bubble color");
        if (player == null) {
            return;
        }

        String raw = rawValue == null ? "" : rawValue.trim();
        String normalized = raw.toLowerCase(Locale.ROOT);
        if ("auto".equals(normalized) || "default".equals(normalized) || "clear".equals(normalized) || "random".equals(normalized)) {
            ChatBubbleColorManager.clearServerColor(player);
            sendClientConfig(player, "color", "AUTO");
            player.addChatComponentMessage(new ChatComponentText("RiftFlux chat bubble color reset to automatic."));
            return;
        }

        Integer color = ModConfig.parseRgbColorOrNull(raw);
        if (color == null) {
            player.addChatComponentMessage(new ChatComponentText("Unknown chat bubble color: " + raw));
            player.addChatComponentMessage(new ChatComponentText("Use a named color, #RRGGBB, 0xRRGGBB, or auto."));
            return;
        }

        ChatBubbleColorManager.setServerColor(player, color.intValue());
        sendClientConfig(player, "color", raw);
        player.addChatComponentMessage(new ChatComponentText("RiftFlux chat bubble color set to " + ModConfig.describeRgbColorInput(raw, color.intValue()) + "."));
    }

    private static void processChatBubbleTextColorValue(ICommandSender sender, String rawValue) {
        EntityPlayer player = requirePlayer(sender, "set a RiftFlux chat bubble text color");
        if (player == null) {
            return;
        }

        String raw = rawValue == null ? "" : rawValue.trim();
        String normalized = raw.toLowerCase(Locale.ROOT);
        if ("auto".equals(normalized) || "default".equals(normalized) || "clear".equals(normalized) || "random".equals(normalized)) {
            ChatBubbleColorManager.clearServerTextColor(player);
            sendClientConfig(player, "textcolor", "AUTO");
            player.addChatComponentMessage(new ChatComponentText("RiftFlux chat bubble text color reset to automatic."));
            return;
        }
        if ("true".equals(normalized) || "on".equals(normalized) || "yes".equals(normalized) || "1".equals(normalized)) {
            raw = "white";
        } else if ("false".equals(normalized) || "off".equals(normalized) || "no".equals(normalized) || "0".equals(normalized)) {
            raw = "black";
        }

        Integer color = ModConfig.parseRgbColorOrNull(raw);
        if (color == null) {
            player.addChatComponentMessage(new ChatComponentText("Unknown chat bubble text color: " + raw));
            player.addChatComponentMessage(new ChatComponentText("Use a named color, #RRGGBB, 0xRRGGBB, or auto."));
            return;
        }

        ChatBubbleColorManager.setServerTextColor(player, color.intValue());
        sendClientConfig(player, "textcolor", raw);
        player.addChatComponentMessage(new ChatComponentText("RiftFlux chat bubble text color set to " + ModConfig.describeRgbColorInput(raw, color.intValue()) + "."));
    }

    private static void processClientConfigCommand(ICommandSender sender, String setting, String value) {
        EntityPlayer player = requirePlayer(sender, "change RiftFlux chat bubble config");
        if (player == null) {
            return;
        }

        String result;
        try {
            result = ModConfig.validateChatBubblesConfigValue(setting, value);
        } catch (IllegalArgumentException e) {
            player.addChatComponentMessage(new ChatComponentText(e.getMessage()));
            return;
        }

        sendClientConfig(player, setting, value);
        player.addChatComponentMessage(new ChatComponentText("RiftFlux chat bubbles " + result + "."));
    }

    private static void sendClientConfig(EntityPlayer player, String setting, String value) {
        if (player instanceof EntityPlayerMP && RFNetwork.CH != null) {
            RFNetwork.CH.sendTo(new MsgSetChatBubblesConfig(setting, value), (EntityPlayerMP) player);
        } else {
            ModConfig.applyChatBubblesConfigValue(setting, value, true);
        }
    }

    private static EntityPlayer requirePlayer(ICommandSender sender, String action) {
        if (!(sender instanceof EntityPlayer)) {
            sender.addChatMessage(new ChatComponentText("Only players can " + action + "."));
            return null;
        }
        return (EntityPlayer) sender;
    }

    private static List completeChatBubble(String[] args, int offset) {
        int remaining = args.length - offset;
        if (remaining == 1) {
            return getListOfStringsMatchingLastWord(args, CHAT_BUBBLE_DIRECT_COMPLETIONS);
        }
        if (remaining == 2) {
            String first = args[offset];
            if ("config".equalsIgnoreCase(first) || "settings".equalsIgnoreCase(first)) {
                return getListOfStringsMatchingLastWord(args, CONFIG_SETTING_COMPLETIONS);
            }
            return completeConfigValue(args, first);
        }
        if (remaining == 3 && ("config".equalsIgnoreCase(args[offset]) || "settings".equalsIgnoreCase(args[offset]))) {
            return completeConfigValue(args, args[offset + 1]);
        }
        return null;
    }

    private static List completeChatBubbleSize(String[] args, int offset) {
        int remaining = args.length - offset;
        if (remaining == 1) {
            return getListOfStringsMatchingLastWord(args, SIZE_COMPLETIONS);
        }
        if (remaining == 2 && isSizeConfigKey(args[offset])) {
            return getListOfStringsMatchingLastWord(args, SIZE_COMPLETIONS);
        }
        return null;
    }

    private static List completeChatBubbleText(String[] args, int offset) {
        int remaining = args.length - offset;
        if (remaining == 1) {
            return getListOfStringsMatchingLastWord(args, COLOR_COMPLETIONS);
        }
        if (remaining == 2 && isTextColorConfigKey(args[offset])) {
            return getListOfStringsMatchingLastWord(args, COLOR_COMPLETIONS);
        }
        return null;
    }

    private static List completeChatBubbleConfig(String[] args, int offset) {
        int remaining = args.length - offset;
        if (remaining == 1) {
            return getListOfStringsMatchingLastWord(args, CONFIG_SETTING_COMPLETIONS);
        }
        if (remaining == 2) {
            return completeConfigValue(args, args[offset]);
        }
        return null;
    }

    private static List completeChatBubblePhoto(String[] args, int offset) {
        int remaining = args.length - offset;
        if (remaining == 1) {
            return getListOfStringsMatchingLastWord(args, BOOLEAN_COMPLETIONS);
        }
        return null;
    }

    private static List completeConfigValue(String[] args, String setting) {
        String key = ModConfig.normalizeChatBubblesConfigKey(setting);
        if (key == null) {
            return null;
        }
        if ("color".equals(key) || "textcolor".equals(key)) {
            return getListOfStringsMatchingLastWord(args, COLOR_COMPLETIONS);
        }
        if ("size".equals(key)) {
            return getListOfStringsMatchingLastWord(args, SIZE_COMPLETIONS);
        }
        if ("gap".equals(key)) {
            return getListOfStringsMatchingLastWord(args, GAP_COMPLETIONS);
        }
        if ("blackbaropacity".equals(key)) {
            return getListOfStringsMatchingLastWord(args, OPACITY_COMPLETIONS);
        }
        if ("lifetime".equals(key)) {
            return getListOfStringsMatchingLastWord(args, LIFETIME_COMPLETIONS);
        }
        if ("linelength".equals(key)) {
            return getListOfStringsMatchingLastWord(args, LINE_LENGTH_COMPLETIONS);
        }
        return getListOfStringsMatchingLastWord(args, BOOLEAN_COMPLETIONS);
    }

    private static boolean isChatBubbleSubCommand(String value) {
        return "chatbubble".equalsIgnoreCase(value) || "cb".equalsIgnoreCase(value);
    }

    private static boolean isChatBubbleTextSubCommand(String value) {
        return "chatbubblestext".equalsIgnoreCase(value)
                || "chatbubbletext".equalsIgnoreCase(value)
                || "cbt".equalsIgnoreCase(value);
    }

    private static boolean isChatBubbleSizeSubCommand(String value) {
        return "chatbubblesize".equalsIgnoreCase(value) || "cbs".equalsIgnoreCase(value);
    }

    private static boolean isChatBubbleConfigSubCommand(String value) {
        return "chatbubbles".equalsIgnoreCase(value)
                || "chatbubbleconfig".equalsIgnoreCase(value)
                || "chatbubblesconfig".equalsIgnoreCase(value)
                || "config".equalsIgnoreCase(value);
    }

    private static boolean isChatBubblePhotoSubCommand(String value) {
        return "chatbubblephoto".equalsIgnoreCase(value)
                || "cbp".equalsIgnoreCase(value);
    }

    private static boolean isColorConfigKey(String value) {
        return "color".equals(ModConfig.normalizeChatBubblesConfigKey(value));
    }

    private static boolean isTextColorConfigKey(String value) {
        return "textcolor".equals(ModConfig.normalizeChatBubblesConfigKey(value));
    }

    private static boolean isSizeConfigKey(String value) {
        return "size".equals(ModConfig.normalizeChatBubblesConfigKey(value));
    }

    private static String[] dropFirst(String[] args) {
        if (args == null || args.length <= 1) {
            return new String[0];
        }
        String[] out = new String[args.length - 1];
        System.arraycopy(args, 1, out, 0, out.length);
        return out;
    }
}
