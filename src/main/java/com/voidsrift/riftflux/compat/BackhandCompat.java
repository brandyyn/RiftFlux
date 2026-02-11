package com.voidsrift.riftflux.compat;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Optional;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import xonin.backhand.api.core.BackhandUtils;

public final class BackhandCompat {
    private BackhandCompat() {
    }

    public static boolean isAvailable() {
        return Loader.isModLoaded("backhand");
    }

    public static ItemStack getOffhandItem(EntityPlayer player) {
        if (!isAvailable() || player == null) {
            return null;
        }
        return getOffhandItemInternal(player);
    }

    public static boolean isUsingOffhand(EntityPlayer player) {
        if (!isAvailable() || player == null) {
            return false;
        }
        return isUsingOffhandInternal(player);
    }

    @Optional.Method(modid = "backhand")
    private static ItemStack getOffhandItemInternal(EntityPlayer player) {
        return BackhandUtils.getOffhandItem(player);
    }

    @Optional.Method(modid = "backhand")
    private static boolean isUsingOffhandInternal(EntityPlayer player) {
        return BackhandUtils.isUsingOffhand(player);
    }
}
