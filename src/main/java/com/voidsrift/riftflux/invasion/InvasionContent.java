package com.voidsrift.riftflux.invasion;

import com.voidsrift.riftflux.ModConfig;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import invmod.common.ProxyCommon;
import invmod.common.mod_Invasion;

/** RiftFlux lifecycle bridge for the integrated Invasion module. */
public final class InvasionContent {
    private static mod_Invasion module;
    private static boolean preInitialized;
    private static boolean initialized;
    private static boolean postInitialized;

    private InvasionContent() {
    }

    public static boolean isEnabled() {
        return ModConfig.enableInvasionModule;
    }

    public static void preInit(FMLPreInitializationEvent event) {
        if (preInitialized || !isEnabled()) {
            return;
        }
        preInitialized = true;

        mod_Invasion.proxy = createProxy(event.getSide().isClient());
        module = new mod_Invasion();
        module.preInit(event);
    }

    public static void init(FMLInitializationEvent event) {
        if (initialized || !isEnabled() || module == null) {
            return;
        }
        initialized = true;
        module.load(event);
    }

    public static void postInit(FMLPostInitializationEvent event) {
        if (postInitialized || !isEnabled() || module == null) {
            return;
        }
        postInitialized = true;
        module.postInitialise(event);
    }

    public static void serverStarting(FMLServerStartingEvent event) {
        if (isEnabled() && module != null) {
            module.onServerStart(event);
        }
    }

    private static ProxyCommon createProxy(boolean client) {
        if (!client) {
            return new ProxyCommon();
        }
        try {
            return (ProxyCommon) Class.forName("invmod.client.ProxyClient").newInstance();
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Unable to initialize Invasion client proxy", e);
        }
    }
}
