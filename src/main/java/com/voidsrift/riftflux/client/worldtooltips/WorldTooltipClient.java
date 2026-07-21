package com.voidsrift.riftflux.client.worldtooltips;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import net.minecraft.item.Item;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.MinecraftForge;

@SideOnly(Side.CLIENT)
public final class WorldTooltipClient {
    private static final Map<String, String> ITEM_ID_TO_MOD_NAME = new HashMap<String, String>();
    private static final Map<EnumChatFormatting, Integer> RARITY_COLOR_CODES =
            new HashMap<EnumChatFormatting, Integer>();
    private static final WorldTooltipRenderHandler RENDER_HANDLER = new WorldTooltipRenderHandler();
    private static boolean bootstrapped;
    private static boolean hideModNameForCompat;

    private WorldTooltipClient() {
    }

    public static void bootstrap() {
        if (bootstrapped) {
            return;
        }
        bootstrapped = true;
        hideModNameForCompat = Loader.isModLoaded("waila")
                || Loader.isModLoaded("nei")
                || Loader.isModLoaded("hwyla");
        populateModNameCache();
        populateRarityColors();
        MinecraftForge.EVENT_BUS.register(RENDER_HANDLER);
    }

    public static String getModName(Item item) {
        if (item == null) {
            return "Unknown";
        }

        String fullName = Item.itemRegistry.getNameForObject(item);
        if (fullName == null) {
            return "Unknown";
        }

        int separator = fullName.indexOf(':');
        if (separator <= 0) {
            return fullName;
        }

        String modId = fullName.substring(0, separator);
        String lowercaseModId = modId.toLowerCase(Locale.ENGLISH);
        String modName = ITEM_ID_TO_MOD_NAME.get(lowercaseModId);
        if (modName == null || modName.trim().isEmpty()) {
            modName = prettifyModId(modId);
            ITEM_ID_TO_MOD_NAME.put(lowercaseModId, modName);
        }
        return modName;
    }

    public static void beginDeferredRenderFrame() {
        RENDER_HANDLER.beginDeferredRenderFrame();
    }

    public static void renderDeferred() {
        RENDER_HANDLER.renderDeferred();
    }

    public static int getRarityColor(EnumChatFormatting formatting, int fallback) {
        Integer color = RARITY_COLOR_CODES.get(formatting);
        return color != null ? color.intValue() : fallback;
    }

    public static boolean shouldHideModName() {
        return hideModNameForCompat;
    }

    private static void populateModNameCache() {
        Map indexedMods = Loader.instance().getIndexedModList();
        if (indexedMods == null) {
            return;
        }

        for (Object entryObject : indexedMods.entrySet()) {
            Map.Entry entry = (Map.Entry) entryObject;
            if (!(entry.getKey() instanceof String) || !(entry.getValue() instanceof ModContainer)) {
                continue;
            }

            String lowercaseId = ((String) entry.getKey()).toLowerCase(Locale.ENGLISH);
            String modName = ((ModContainer) entry.getValue()).getName();
            if (modName != null && !modName.trim().isEmpty()) {
                ITEM_ID_TO_MOD_NAME.put(lowercaseId, modName);
            }
        }
    }

    private static void populateRarityColors() {
        RARITY_COLOR_CODES.put(EnumChatFormatting.BLACK, Integer.valueOf(0x000000));
        RARITY_COLOR_CODES.put(EnumChatFormatting.DARK_BLUE, Integer.valueOf(0x0000AA));
        RARITY_COLOR_CODES.put(EnumChatFormatting.DARK_GREEN, Integer.valueOf(0x00AA00));
        RARITY_COLOR_CODES.put(EnumChatFormatting.DARK_AQUA, Integer.valueOf(0x00AAAA));
        RARITY_COLOR_CODES.put(EnumChatFormatting.DARK_RED, Integer.valueOf(0xAA0000));
        RARITY_COLOR_CODES.put(EnumChatFormatting.DARK_PURPLE, Integer.valueOf(0xAA00AA));
        RARITY_COLOR_CODES.put(EnumChatFormatting.GOLD, Integer.valueOf(0xFFAA00));
        RARITY_COLOR_CODES.put(EnumChatFormatting.GRAY, Integer.valueOf(0xAAAAAA));
        RARITY_COLOR_CODES.put(EnumChatFormatting.DARK_GRAY, Integer.valueOf(0x555555));
        RARITY_COLOR_CODES.put(EnumChatFormatting.BLUE, Integer.valueOf(0x5555FF));
        RARITY_COLOR_CODES.put(EnumChatFormatting.GREEN, Integer.valueOf(0x55FF55));
        RARITY_COLOR_CODES.put(EnumChatFormatting.AQUA, Integer.valueOf(0x55FFFF));
        RARITY_COLOR_CODES.put(EnumChatFormatting.RED, Integer.valueOf(0xFF5555));
        RARITY_COLOR_CODES.put(EnumChatFormatting.LIGHT_PURPLE, Integer.valueOf(0xFF55FF));
        RARITY_COLOR_CODES.put(EnumChatFormatting.YELLOW, Integer.valueOf(0xFFFF55));
        RARITY_COLOR_CODES.put(EnumChatFormatting.WHITE, Integer.valueOf(0xFFFFFF));
    }

    private static String prettifyModId(String modId) {
        if (modId == null || modId.isEmpty()) {
            return "Unknown";
        }

        String normalized = modId.replace('_', ' ').replace('-', ' ').trim();
        if (normalized.isEmpty()) {
            return modId;
        }

        StringBuilder out = new StringBuilder(normalized.length());
        boolean capitalize = true;
        for (int i = 0; i < normalized.length(); i++) {
            char c = normalized.charAt(i);
            if (Character.isWhitespace(c)) {
                capitalize = true;
                out.append(c);
            } else if (capitalize) {
                out.append(Character.toUpperCase(c));
                capitalize = false;
            } else {
                out.append(c);
            }
        }
        return out.toString();
    }
}
