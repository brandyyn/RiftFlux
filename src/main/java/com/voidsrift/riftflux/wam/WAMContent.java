package com.voidsrift.riftflux.wam;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.entity.RiftFluxEntityRegistry;
import com.voidsrift.riftflux.riftflux;
import com.voidsrift.riftflux.wam.client.render.RenderBlackWidow;
import com.voidsrift.riftflux.wam.client.render.RenderCyclops;
import com.voidsrift.riftflux.wam.client.render.RenderEnderTroll;
import com.voidsrift.riftflux.wam.client.render.RenderFlowerMan;
import com.voidsrift.riftflux.wam.client.render.RenderJaxx;
import com.voidsrift.riftflux.wam.entity.EntityBlackWidow;
import com.voidsrift.riftflux.wam.entity.EntityCyclops;
import com.voidsrift.riftflux.wam.entity.EntityEnderTroll;
import com.voidsrift.riftflux.wam.entity.EntityFlowerMan;
import com.voidsrift.riftflux.wam.entity.EntityJaxx;
import com.voidsrift.riftflux.wam.world.WitchHouseWorldGenerator;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;

public final class WAMContent {
    private static boolean preInited;
    private static boolean initialized;
    private static boolean clientInited;
    private static Item cyclopsSpawnEgg;
    private static Item flowerManSpawnEgg;
    private static Item enderTrollSpawnEgg;
    private static Item jaxxSpawnEgg;
    private static Item blackWidowSpawnEgg;

    private WAMContent() {
    }

    public static boolean isEnabled() {
        return ModConfig.enableWitchesAndMoreModule;
    }

    public static void preInit(FMLPreInitializationEvent event) {
        if (preInited || !isEnabled()) {
            return;
        }
        preInited = true;

        if (ModConfig.enableCyclopsMob) {
            RiftFluxEntityRegistry.registerModEntity(EntityCyclops.class, "Cyclops", riftflux.instance, 80, 3, true);
            cyclopsSpawnEgg = registerSpawnEggItem("cyclops_spawn_egg", "cyclops", 0x8C755F, 0xD6C39A);
        }
        if (ModConfig.enableFlowerManMob) {
            RiftFluxEntityRegistry.registerModEntity(EntityFlowerMan.class, "FlowerMan", riftflux.instance, 64, 3, true);
            flowerManSpawnEgg = registerSpawnEggItem("flower_man_spawn_egg", "flowerman", 0x4E8C3A, 0xEE6CC8);
        }
        if (ModConfig.enableEnderTrollMob) {
            RiftFluxEntityRegistry.registerModEntity(EntityEnderTroll.class, "EnderTroll", riftflux.instance, 80, 3, true);
            enderTrollSpawnEgg = registerSpawnEggItem("ender_troll_spawn_egg", "endertroll", 0x21122E, 0x57C7E3);
        }
        if (ModConfig.enableJaxxMob) {
            RiftFluxEntityRegistry.registerModEntity(EntityJaxx.class, "Jaxx", riftflux.instance, 96, 3, true);
            jaxxSpawnEgg = registerSpawnEggItem("jaxx_spawn_egg", "jaxx", 0xD17527, 0xF7D14C);
        }
        if (ModConfig.enableBlackWidowMob) {
            RiftFluxEntityRegistry.registerModEntity(EntityBlackWidow.class, "BlackWidow", riftflux.instance, 64, 3, true);
            blackWidowSpawnEgg = registerSpawnEggItem("black_widow_spawn_egg", "blackwidow", 0x121212, 0xA10618);
        }
    }

    public static void init(FMLInitializationEvent event) {
        if (initialized || !isEnabled()) {
            return;
        }
        initialized = true;

        if (ModConfig.enableEnderTrollMob) {
            MinecraftForge.EVENT_BUS.register(new WAMEventHandler());
        }
        if (ModConfig.enableWitchHouseStructure) {
            GameRegistry.registerWorldGenerator(new WitchHouseWorldGenerator(), 0);
        }
    }

    public static void initClient() {
        if (clientInited || !isEnabled()) {
            return;
        }
        clientInited = true;

        if (ModConfig.enableCyclopsMob) {
            RenderingRegistry.registerEntityRenderingHandler(EntityCyclops.class, new RenderCyclops());
        }
        if (ModConfig.enableFlowerManMob) {
            RenderingRegistry.registerEntityRenderingHandler(EntityFlowerMan.class, new RenderFlowerMan());
        }
        if (ModConfig.enableEnderTrollMob) {
            RenderingRegistry.registerEntityRenderingHandler(EntityEnderTroll.class, new RenderEnderTroll());
        }
        if (ModConfig.enableJaxxMob) {
            RenderingRegistry.registerEntityRenderingHandler(EntityJaxx.class, new RenderJaxx());
        }
        if (ModConfig.enableBlackWidowMob) {
            RenderingRegistry.registerEntityRenderingHandler(EntityBlackWidow.class, new RenderBlackWidow());
        }
    }

    private static Item registerSpawnEggItem(String registryName, String mobKey, int primaryColor, int secondaryColor) {
        Item item = new ItemWAMSpawnEgg(mobKey, primaryColor, secondaryColor).setUnlocalizedName(registryName);
        GameRegistry.registerItem(item, registryName);
        return item;
    }
}
