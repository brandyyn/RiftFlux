package com.voidsrift.riftflux.combat;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraft.init.Items;

import com.voidsrift.riftflux.ModConfig;

public class StickTooltipHandler {

    @SubscribeEvent
    public void onTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.itemStack;
        if (stack == null) return;

        if (stack.getItem() != Items.stick) return;
        if (!ModConfig.enableStickDamageBonus) return;

        float bonus = ModConfig.stickDamageBonus;
        if (bonus <= 0) return;

        event.toolTip.add(
                EnumChatFormatting.GRAY + "+" +
                        (bonus % 1 == 0 ? Integer.toString((int) bonus) : Float.toString(bonus)) +
                        " Melee Damage"
        );
    }
}
