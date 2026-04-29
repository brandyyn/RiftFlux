package com.voidsrift.riftflux.axolotl;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.entity.RiftFluxEntityRegistry;
import com.voidsrift.riftflux.riftflux;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.Item;

public final class AxolotlContent {
    public static Item axolotlBucket;
    public static Item axolotlSpawnEgg;

    private static boolean preInited;
    private static boolean initialized;
    private static boolean clientInited;

    private AxolotlContent() {
    }

    public static boolean isEnabled() {
        return ModConfig.enableAxolotlModule;
    }

    public static void preInit(FMLPreInitializationEvent event) {
        if (preInited) {
            return;
        }
        preInited = true;

        if (!isEnabled()) {
            return;
        }

        axolotlBucket = new ItemAxolotlBucket();
        axolotlSpawnEgg = new ItemAxolotlSpawnEgg();

        GameRegistry.registerItem(axolotlBucket, "axolotl_bucket");
        GameRegistry.registerItem(axolotlSpawnEgg, "axolotl_spawn_egg");

        registerEntities();
    }

    public static void init(FMLInitializationEvent event) {
        if (initialized) {
            return;
        }
        initialized = true;

        if (!isEnabled()) {
            return;
        }

    }

    public static void initClient() {
        if (clientInited) {
            return;
        }
        clientInited = true;

        if (!isEnabled()) {
            return;
        }

        RenderingRegistry.registerEntityRenderingHandler(EntityAxolotl.class, new RenderAxolotl());
    }

    public static void postInit(FMLPostInitializationEvent event) {
    }

    private static void registerEntities() {
        RiftFluxEntityRegistry.registerModEntity(EntityAxolotl.class, "Axolotl", riftflux.instance, 64, 3, true);
    }
}
