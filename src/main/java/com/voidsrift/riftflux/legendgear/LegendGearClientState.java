package com.voidsrift.riftflux.legendgear;

import baubles.api.BaublesApi;
import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.compat.BackhandCompat;
import com.voidsrift.riftflux.terramine.TerrariaContent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item;
import net.nmccoy.legendgear.magic.IMana;
import net.nmccoy.legendgear.LegendGear2;

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

        if (BackhandCompat.isAvailable()) {
            ItemStack offhand = BackhandCompat.getOffhandItem(player);
            if (isManaItem(offhand)) {
                return true;
            }
        }

        if (player.inventory != null && player.inventory.armorInventory != null) {
            for (ItemStack stack : player.inventory.armorInventory) {
                if (isManaItem(stack)) {
                    return true;
                }
            }
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

    public static boolean isHoldingIceRodWithLegendGearManaDisabled(EntityPlayer player) {
        if (player == null || ModConfig.iceRodUseLegendGearMana) {
            return false;
        }
        if (isIceRod(player.getItemInUse()) || isIceRod(player.getHeldItem())) {
            return true;
        }
        if (BackhandCompat.isAvailable()) {
            return isIceRod(BackhandCompat.getOffhandItem(player));
        }
        return false;
    }

    private static boolean isManaItem(ItemStack stack) {
        if (stack == null || stack.getItem() == null) {
            return false;
        }
        Item item = stack.getItem();
        if (isWhoopieCushion(stack)) {
            return ModConfig.enableLegendGearModule && Math.max(0.0F, ModConfig.whoopieCushionLegendGearManaCost) > 0.0F;
        }
        if (isIceRod(stack)) {
            return ModConfig.iceRodUseLegendGearMana;
        }
        if (item instanceof IMana) {
            return true;
        }
        return item == LegendGear2.magicRing || item == LegendGear2.charmPendant;
    }

    private static boolean isIceRod(ItemStack stack) {
        return stack != null && stack.getItem() == TerrariaContent.iceRod;
    }

    private static boolean isWhoopieCushion(ItemStack stack) {
        return stack != null && stack.getItem() == TerrariaContent.whoopieCushion;
    }
}
