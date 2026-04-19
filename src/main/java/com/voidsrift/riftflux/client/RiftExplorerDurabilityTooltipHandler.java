package com.voidsrift.riftflux.client;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;

import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

public class RiftExplorerDurabilityTooltipHandler {
    private static final Pattern DURABILITY_PATTERN =
            Pattern.compile("(?i)^durability\\s*[:ï¼š]\\s*\\d+\\s*/\\s*\\d+\\s*$");

    @SubscribeEvent
    public void onTooltip(ItemTooltipEvent event) {
        if (event == null) {
            return;
        }

        ItemStack stack = event.itemStack;
        if (stack == null || stack.getItem() == null) {
            return;
        }

        if (!isRiftExplorerItem(stack)) {
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
                String line = stripFormatting((String) lineObj).trim().toLowerCase(Locale.ROOT);
                if (DURABILITY_PATTERN.matcher(line).matches()) {
                    return;
                }
            }
        }

        int remaining = Math.max(0, max - stack.getItemDamage());
        event.toolTip.add(EnumChatFormatting.GRAY + "Durability: " + remaining + "/" + max);
    }

    private static boolean isRiftExplorerItem(ItemStack stack) {
        String className = stack.getItem().getClass().getName();
        return className.startsWith("zairus.worldexplorer.");
    }

    private static String stripFormatting(String text) {
        if (text == null) {
            return "";
        }
        return text.replaceAll("\u00A7[0-9A-FK-ORa-fk-or]", "");
    }
}
