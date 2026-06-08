package com.voidsrift.riftflux.client;

import com.voidsrift.riftflux.ModConfig;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;

import java.util.List;

public class UniversalDurabilityTooltipHandler {
    @SubscribeEvent
    public void onTooltip(ItemTooltipEvent event) {
        if (event == null || !ModConfig.enableUniversalDurabilityTooltip) {
            return;
        }

        ItemStack stack = event.itemStack;
        if (stack == null || stack.getItem() == null) {
            return;
        }

        if (isTConstructItem(stack)) {
            return;
        }

        int max = stack.getMaxDamage();
        if (max <= 0) {
            return;
        }

        List toolTip = event.toolTip;
        if (toolTip != null) {
            for (int i = 0; i < toolTip.size(); i++) {
                Object lineObj = toolTip.get(i);
                if (!(lineObj instanceof String)) {
                    continue;
                }
                if (isDurabilityLine((String) lineObj)) {
                    return;
                }
            }
        }

        int remaining = Math.max(0, max - stack.getItemDamage());
        event.toolTip.add(EnumChatFormatting.GRAY + "Durability: " + remaining + "/" + max);
    }

    private static boolean isDurabilityLine(String text) {
        if (text == null) {
            return false;
        }
        int length = text.length();
        int i = skipWhitespaceAndFormatting(text, 0, length);
        String label = "durability";
        for (int j = 0; j < label.length(); j++) {
            if (i >= length || Character.toLowerCase(text.charAt(i)) != label.charAt(j)) {
                return false;
            }
            i++;
        }
        i = skipWhitespaceAndFormatting(text, i, length);
        if (i >= length) {
            return false;
        }
        char colon = text.charAt(i);
        if (colon != ':' && colon != '\uFF1A') {
            return false;
        }
        i = skipWhitespaceAndFormatting(text, i + 1, length);
        int firstDigits = 0;
        while (i < length && Character.isDigit(text.charAt(i))) {
            i++;
            firstDigits++;
        }
        if (firstDigits <= 0) {
            return false;
        }
        i = skipWhitespaceAndFormatting(text, i, length);
        if (i >= length || text.charAt(i) != '/') {
            return false;
        }
        i = skipWhitespaceAndFormatting(text, i + 1, length);
        int secondDigits = 0;
        while (i < length && Character.isDigit(text.charAt(i))) {
            i++;
            secondDigits++;
        }
        if (secondDigits <= 0) {
            return false;
        }
        return skipWhitespaceAndFormatting(text, i, length) >= length;
    }

    private static int skipWhitespaceAndFormatting(String text, int index, int length) {
        int i = index;
        while (i < length) {
            char c = text.charAt(i);
            if (c == '\u00A7' && i + 1 < length) {
                i += 2;
            } else if (Character.isWhitespace(c)) {
                i++;
            } else {
                break;
            }
        }
        return i;
    }

    private static boolean isTConstructItem(ItemStack stack) {
        Item item = stack.getItem();
        if (item == null) {
            return false;
        }

        for (Class<?> cls = item.getClass(); cls != null; cls = cls.getSuperclass()) {
            String className = cls.getName();
            if (className != null && className.startsWith("tconstruct.")) {
                return true;
            }
        }

        NBTTagCompound tag = stack.getTagCompound();
        return tag != null && tag.hasKey("InfiTool");
    }
}
