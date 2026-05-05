package com.zyin.zyinhud;

import com.zyin.zyinhud.mods.HealthMonitor;
import com.zyin.zyinhud.mods.Miscellaneous;
import com.zyin.zyinhud.util.ModCompatibility;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.relauncher.Side;
import net.minecraftforge.common.MinecraftForge;

public final class ZyinHUD {
    public static final String VERSION = "1.3.7";
    public static final String MODID = "zyinhud";
    public static final String MODNAME = "Zyin's HUD";

    public static CommonProxy proxy;

    private ZyinHUD() {
    }

    public static void preInit(FMLPreInitializationEvent event) {
        // Config is sourced from RiftFlux and synced during init.
    }

    public static void init(FMLInitializationEvent event) {
        if (FMLCommonHandler.instance().getSide() != Side.CLIENT) {
            return;
        }

        ZyinHUDConfig.LoadConfigSettings();

        proxy = createProxy();
        proxy.registerClientStuff();

        FMLCommonHandler.instance().bus().register(ZyinHUDKeyHandlers.instance);
        MinecraftForge.EVENT_BUS.register(ZyinHUDKeyHandlers.instance);

        MinecraftForge.EVENT_BUS.register(ZyinHUDRenderer.instance);
        MinecraftForge.EVENT_BUS.register(Miscellaneous.instance);
        FMLCommonHandler.instance().bus().register(Miscellaneous.instance);
        FMLCommonHandler.instance().bus().register(HealthMonitor.instance);
    }

    public static void postInit(FMLPostInitializationEvent event) {
        ModCompatibility.TConstruct.isLoaded = Loader.isModLoaded("TConstruct");
    }

    public static void serverStarting(FMLServerStartingEvent event) {
        if (FMLCommonHandler.instance().getSide() != Side.CLIENT) {
            return;
        }
    }

    private static CommonProxy createProxy() {
        if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
            return new ClientProxy();
        }
        return new CommonProxy();
    }
}
