package com.voidsrift.riftflux.compat;

import cpw.mods.fml.common.Loader;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

import java.lang.reflect.Method;

public final class OpenBlocksGliderCompat {
    private static final String ENTITY_HANG_GLIDER_CLASS_NAME = "openblocks.common.entity.EntityHangGlider";

    private static Boolean availableCache;
    private static Method getEntityHangGliderMethod;
    private static Method isGliderDeployedMethod;

    private OpenBlocksGliderCompat() {
    }

    public static boolean isAvailable() {
        if (Loader.isModLoaded("OpenBlocks") || Loader.isModLoaded("openblocks")) {
            initialize();
            return availableCache != null && availableCache.booleanValue();
        }
        if (availableCache != null) {
            return availableCache.booleanValue();
        }
        initialize();
        return availableCache != null && availableCache.booleanValue();
    }

    public static boolean isGliderActive(EntityPlayer player) {
        if (player == null || !isAvailable()) {
            return false;
        }

        Entity gliderEntity = getGliderEntity(player);
        if (gliderEntity != null && !gliderEntity.isDead) {
            return true;
        }

        if (isGliderDeployedMethod == null) {
            return false;
        }

        try {
            return ((Boolean) isGliderDeployedMethod.invoke(null, player)).booleanValue();
        } catch (Throwable ignored) {
            return false;
        }
    }

    public static Entity getGliderEntity(EntityPlayer player) {
        if (player == null || !isAvailable() || getEntityHangGliderMethod == null) {
            return null;
        }
        try {
            Object result = getEntityHangGliderMethod.invoke(null, player);
            return result instanceof Entity ? (Entity) result : null;
        } catch (Throwable ignored) {
            return null;
        }
    }

    private static void initialize() {
        if (availableCache != null) {
            return;
        }

        boolean available = false;
        try {
            ClassLoader loader = OpenBlocksGliderCompat.class.getClassLoader();
            Class<?> entityHangGliderClass = Class.forName(ENTITY_HANG_GLIDER_CLASS_NAME, false, loader);
            getEntityHangGliderMethod = entityHangGliderClass.getMethod("getEntityHangGlider", Entity.class);
            isGliderDeployedMethod = entityHangGliderClass.getMethod("isGliderDeployed", Entity.class);
            available = true;
        } catch (Throwable ignored) {
            getEntityHangGliderMethod = null;
            isGliderDeployedMethod = null;
        }

        availableCache = Boolean.valueOf(available);
    }
}
