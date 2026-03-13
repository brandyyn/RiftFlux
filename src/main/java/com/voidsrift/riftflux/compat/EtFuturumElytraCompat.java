package com.voidsrift.riftflux.compat;

import cpw.mods.fml.common.Loader;
import net.minecraft.entity.player.EntityPlayer;

import java.lang.reflect.Method;

public final class EtFuturumElytraCompat {
    private static final String API_INTERFACE_NAME = "ganymedes01.etfuturum.api.elytra.IElytraPlayer";
    private static final String IMPL_INTERFACE_NAME = "ganymedes01.etfuturum.elytra.IElytraPlayer";

    private static Boolean availableCache;
    private static Class<?> apiInterfaceClass;
    private static Class<?> implInterfaceClass;
    private static Method isElytraFlyingMethod;
    private static Method setElytraFlyingMethod;

    private EtFuturumElytraCompat() {
    }

    public static boolean isAvailable() {
        if (Loader.isModLoaded("etfuturum") || Loader.isModLoaded("EtFuturum")
                || Loader.isModLoaded("etfuturumrequiem") || Loader.isModLoaded("EtFuturumRequiem")) {
            initialize();
            return availableCache != null && availableCache.booleanValue();
        }
        if (availableCache != null) {
            return availableCache.booleanValue();
        }
        initialize();
        return availableCache != null && availableCache.booleanValue();
    }

    public static boolean isElytraFlying(EntityPlayer player) {
        if (player == null || !isAvailable() || apiInterfaceClass == null || isElytraFlyingMethod == null) {
            return false;
        }
        if (!apiInterfaceClass.isInstance(player)) {
            return false;
        }
        try {
            return ((Boolean) isElytraFlyingMethod.invoke(player)).booleanValue();
        } catch (Throwable ignored) {
            return false;
        }
    }

    public static boolean setElytraFlying(EntityPlayer player, boolean flying) {
        if (player == null || !isAvailable() || implInterfaceClass == null || setElytraFlyingMethod == null) {
            return false;
        }
        if (!implInterfaceClass.isInstance(player)) {
            return false;
        }
        try {
            setElytraFlyingMethod.invoke(player, Boolean.valueOf(flying));
            return true;
        } catch (Throwable ignored) {
            return false;
        }
    }

    public static boolean clearElytraFlight(EntityPlayer player) {
        if (!isElytraFlying(player)) {
            return false;
        }
        return setElytraFlying(player, false);
    }

    private static void initialize() {
        if (availableCache != null) {
            return;
        }

        boolean available = false;
        try {
            ClassLoader loader = EtFuturumElytraCompat.class.getClassLoader();
            apiInterfaceClass = Class.forName(API_INTERFACE_NAME, false, loader);
            implInterfaceClass = Class.forName(IMPL_INTERFACE_NAME, false, loader);
            isElytraFlyingMethod = apiInterfaceClass.getMethod("etfu$isElytraFlying");
            setElytraFlyingMethod = implInterfaceClass.getMethod("etfu$setElytraFlying", Boolean.TYPE);
            available = true;
        } catch (Throwable ignored) {
            apiInterfaceClass = null;
            implInterfaceClass = null;
            isElytraFlyingMethod = null;
            setElytraFlyingMethod = null;
        }

        availableCache = Boolean.valueOf(available);
    }
}
