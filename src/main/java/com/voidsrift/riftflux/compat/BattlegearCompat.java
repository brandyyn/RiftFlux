package com.voidsrift.riftflux.compat;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Optional;
import mods.battlegear2.api.core.IInventoryPlayerBattle;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public final class BattlegearCompat {
    private static Boolean classPresentCache;

    private BattlegearCompat() {
    }

    public static boolean isAvailable() {
        if (Loader.isModLoaded("battlegear2")) {
            return true;
        }
        if (classPresentCache != null) {
            return classPresentCache.booleanValue();
        }
        boolean present;
        try {
            Class.forName("mods.battlegear2.api.core.IInventoryPlayerBattle", false, BattlegearCompat.class.getClassLoader());
            present = true;
        } catch (Throwable ignored) {
            present = false;
        }
        classPresentCache = Boolean.valueOf(present);
        return present;
    }

    public static boolean isBattlemode(EntityPlayer player) {
        if (!isAvailable() || player == null) {
            return false;
        }
        return isBattlemodeInternal(player);
    }

    public static ItemStack getOffhandItem(EntityPlayer player) {
        if (!isAvailable() || player == null) {
            return null;
        }
        return getOffhandItemInternal(player);
    }

    @Optional.Method(modid = "battlegear2")
    private static boolean isBattlemodeInternal(EntityPlayer player) {
        if (player.inventory instanceof IInventoryPlayerBattle) {
            return ((IInventoryPlayerBattle) player.inventory).battlegear2$isBattlemode();
        }
        return false;
    }

    @Optional.Method(modid = "battlegear2")
    private static ItemStack getOffhandItemInternal(EntityPlayer player) {
        if (player.inventory instanceof IInventoryPlayerBattle) {
            return ((IInventoryPlayerBattle) player.inventory).battlegear2$getCurrentOffhandWeapon();
        }
        return null;
    }
}
