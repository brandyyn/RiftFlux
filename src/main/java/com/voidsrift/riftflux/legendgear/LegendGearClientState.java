package com.voidsrift.riftflux.legendgear;

import baubles.api.BaublesApi;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.nmccoy.legendgear.magic.IMana;

public final class LegendGearClientState {
    private LegendGearClientState() {
    }

    public static boolean shouldForceShow(EntityPlayer player) {
        if (player == null || player.worldObj == null) {
            return false;
        }
        ItemStack inUse = player.getItemInUse();
        if (isManaItem(inUse)) {
            return true;
        }

        ItemStack held = player.getHeldItem();
        if (isManaItem(held)) {
            return true;
        }

        try {
            IInventory baubles = BaublesApi.getBaubles(player);
            if (baubles != null) {
                for (int i = 0; i < baubles.getSizeInventory(); i++) {
                    ItemStack stack = baubles.getStackInSlot(i);
                    if (isManaItem(stack)) {
                        return true;
                    }
                }
            }
        } catch (Throwable ignored) {
        }

        return false;
    }

    private static boolean isManaItem(ItemStack stack) {
        if (stack == null || stack.getItem() == null) {
            return false;
        }
        if (stack.getItem() instanceof IMana) {
            return true;
        }
        return stack.getItem().getClass().getName().startsWith("net.nmccoy.legendgear");
    }
}
