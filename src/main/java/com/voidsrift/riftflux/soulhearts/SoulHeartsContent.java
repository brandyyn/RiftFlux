package com.voidsrift.riftflux.soulhearts;

import com.voidsrift.riftflux.ModConfig;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import inurosen.healaltar.common.HealingAltar;

public final class SoulHeartsContent {
    private static HealingAltar module;
    private static boolean preInited;
    private static boolean initialized;
    private static boolean postInited;
    private static boolean activated;

    private SoulHeartsContent() {
    }

    public static void preInit(FMLPreInitializationEvent event) {
        if (preInited) {
            return;
        }
        preInited = true;

        if (!ModConfig.enableSoulHeartsModule) {
            return;
        }

        if (Loader.isModLoaded("HealingAltar") || Loader.isModLoaded("healingaltar")) {
            activated = true;
            return;
        }

        ensureProxy();
        module = new HealingAltar();
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
        if (HealingAltar.proxy != null) {
            return;
        }
        Side side = FMLCommonHandler.instance().getSide();
        if (side == Side.CLIENT) {
            HealingAltar.proxy = new inurosen.healaltar.common.core.ClientProxy();
        } else {
            HealingAltar.proxy = new inurosen.healaltar.common.core.CommonProxy();
        }
    }
}
