package com.voidsrift.riftflux.legendgear;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.nmccoy.legendgear.legacy.LegendGear;

import java.util.List;

public final class WhirlwindBootsTooltipHandler {
    private static final String AIR_SKIMMING_TOOLTIP = "Air-skimming with the power of a whirlwind.";

    @SubscribeEvent
    public void colorWhirlwindBootsTooltip(ItemTooltipEvent event) {
        if (event.itemStack == null || event.itemStack.getItem() != LegendGear.itemWindBoots) {
            return;
        }

        List tooltip = event.toolTip;
        if (!tooltip.isEmpty()) {
            String displayName = EnumChatFormatting.getTextWithoutFormattingCodes(tooltip.get(0).toString());
            tooltip.set(0, EnumChatFormatting.YELLOW + displayName);
        }
        for (int i = 1; i < tooltip.size(); i++) {
            if (AIR_SKIMMING_TOOLTIP.equals(EnumChatFormatting.getTextWithoutFormattingCodes(tooltip.get(i).toString()))) {
                tooltip.set(i, EnumChatFormatting.WHITE + AIR_SKIMMING_TOOLTIP);
            }
        }
    }
}
