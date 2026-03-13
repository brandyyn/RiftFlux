package com.voidsrift.riftflux.armoroverlay;

import com.voidsrift.riftflux.ModConfig;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import theoldone822.ArmorOverlay.ArmorOverlay;

public final class ArmorOverlayContent {
    private static ArmorOverlay module;
    private static boolean preInited;
    private static boolean initialized;
    private static boolean activated;

    private ArmorOverlayContent() {
    }

    public static void preInit(FMLPreInitializationEvent event) {
        if (preInited) {
            return;
        }
        preInited = true;

        if (!ModConfig.enableArmorOverlayModule) {
            return;
        }

        if (Loader.isModLoaded("armoroverlay")) {
            activated = true;
            return;
        }

        module = new ArmorOverlay();
        module.preInit(event);
        activated = true;
    }

    public static void init(FMLInitializationEvent event) {
        if (!activated || initialized || module == null) {
            return;
        }
        initialized = true;
        module.init(event);
    }
}
