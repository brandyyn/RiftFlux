package com.voidsrift.riftflux.heartcrystal;

import com.voidsrift.riftflux.ModConfig;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import tk.nukeduck.hearts.HeartCrystal;

public final class HeartCrystalContent {
    private static HeartCrystal module;
    private static boolean preInited;
    private static boolean initialized;
    private static boolean postInited;
    private static boolean activated;

    private HeartCrystalContent() {
    }

    public static void preInit(FMLPreInitializationEvent event) {
        if (preInited) {
            return;
        }
        preInited = true;

        if (!ModConfig.enableHeartCrystalModule) {
            return;
        }

        if (Loader.isModLoaded(HeartCrystal.MODID)) {
            activated = true;
            return;
        }

        ensureProxy();
        if (HeartCrystal.instance == null) {
            HeartCrystal.instance = new HeartCrystal();
        }
        module = HeartCrystal.instance;
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

    public static void postInit(FMLPostInitializationEvent event) {
        if (!activated || postInited || module == null) {
            return;
        }
        postInited = true;
        module.postInit(event);
    }

    private static void ensureProxy() {
        if (HeartCrystal.proxy != null) {
            return;
        }
        Side side = FMLCommonHandler.instance().getSide();
        if (side == Side.CLIENT) {
            HeartCrystal.proxy = new tk.nukeduck.hearts.network.ClientProxy();
        } else {
            HeartCrystal.proxy = new tk.nukeduck.hearts.network.CommonProxy();
        }
    }
}
