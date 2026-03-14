package com.voidsrift.riftflux.compat;

import cpw.mods.fml.common.Loader;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import java.lang.reflect.Method;

public final class BattlegearCompat {
    private static Boolean availableCache;

    private BattlegearCompat() {
    }

    public static boolean isAvailable() {
        if (availableCache != null) {
            return availableCache.booleanValue();
        }

        boolean present = Loader.isModLoaded("battlegear2");
        try {
            Class.forName(
                    "mods.battlegear2.api.core.IInventoryPlayerBattle",
                    false,
                    BattlegearCompat.class.getClassLoader()
            );
            present = true;
        } catch (Throwable ignored) {
            // If the mod is present but its API class is missing, reflection below still fails closed.
        }
        availableCache = Boolean.valueOf(present);
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

    private static boolean isBattlemodeInternal(EntityPlayer player) {
        Object result = invokeInventoryMethod(player, "battlegear2$isBattlemode");
        return result instanceof Boolean && ((Boolean) result).booleanValue();
    }

    private static ItemStack getOffhandItemInternal(EntityPlayer player) {
        Object result = invokeInventoryMethod(player, "battlegear2$getCurrentOffhandWeapon");
        return result instanceof ItemStack ? (ItemStack) result : null;
    }

    private static Object invokeInventoryMethod(EntityPlayer player, String methodName) {
        if (player == null || player.inventory == null) {
            return null;
        }

        try {
            Method method = player.inventory.getClass().getMethod(methodName);
            return method.invoke(player.inventory);
        } catch (Throwable ignored) {
            return null;
        }
    }
}
