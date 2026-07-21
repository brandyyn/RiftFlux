package com.voidsrift.riftflux.gravestone;

import com.voidsrift.riftflux.ModConfig;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.relauncher.Side;
import gravestone.ModGraveStone;
import gravestone.core.proxy.CommonProxy;

public final class GravestoneContent {
    private static ModGraveStone module;
    private static boolean preInited;
    private static boolean initialized;
    private static boolean postInited;

    private GravestoneContent() {
    }

    public static boolean isEnabled() {
        return ModConfig.enableGravestoneModule;
    }

    public static void preInit(FMLPreInitializationEvent event) {
        if (!isEnabled() || preInited) {
            return;
        }
        preInited = true;
        module = new ModGraveStone();
        ModGraveStone.proxy = createProxy();
        module.preInit(event);
    }

    public static void init(FMLInitializationEvent event) {
        if (!isEnabled() || !preInited || initialized) {
            return;
        }
        initialized = true;
        module.load(event);
    }

    public static void postInit(FMLPostInitializationEvent event) {
        if (!isEnabled() || !initialized || postInited) {
            return;
        }
        postInited = true;
        module.postInit(event);
    }

    public static void serverStarting(FMLServerStartingEvent event) {
        if (!isEnabled() || !initialized) {
            return;
        }
        module.serverStarting(event);
    }

    private static CommonProxy createProxy() {
        if (FMLCommonHandler.instance().getSide() != Side.CLIENT) {
            return new CommonProxy();
        }
        try {
            return (CommonProxy) Class.forName("gravestone.core.proxy.ClientProxy").newInstance();
        } catch (Exception exception) {
            throw new RuntimeException("Failed to create integrated Gravestone client proxy", exception);
        }
    }
}
