package com.voidsrift.riftflux.compat.chromaticraft;

import Reika.ChromatiCraft.Registry.ChromaTiles;
import com.voidsrift.riftflux.ModConfig;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;

public class ChromatiCraftItemFabricatorTooltipHandler {
    @SubscribeEvent
    public void onTooltip(ItemTooltipEvent event) {
        if (!ModConfig.disableChromatiCraftItemFabricator || event == null || event.itemStack == null) {
            return;
        }
        ItemStack fabricator = getFabricatorStack();
        if (fabricator == null || !matches(event.itemStack, fabricator)) {
            return;
        }

        String disabled = EnumChatFormatting.RED + "Disabled";
        if (!event.toolTip.contains(disabled)) {
            event.toolTip.add(disabled);
        }
    }

    private static ItemStack getFabricatorStack() {
        try {
            return ChromaTiles.FABRICATOR.getCraftedProduct();
        } catch (Throwable ignored) {
            return null;
        }
    }

    private static boolean matches(ItemStack stack, ItemStack target) {
        return stack.getItem() == target.getItem() && stack.getItemDamage() == target.getItemDamage();
    }
}
