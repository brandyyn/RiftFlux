package com.voidsrift.riftflux.axolotl;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.riftflux;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.item.Item;
import net.minecraft.world.biome.BiomeGenBase;

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

        if (ModConfig.enableAxolotlNaturalSpawning && ModConfig.axolotlSpawnWeight > 0) {
            BiomeGenBase[] biomes = getSpawnBiomes();
            if (biomes.length > 0) {
                EntityRegistry.addSpawn(
                        EntityAxolotl.class,
                        ModConfig.axolotlSpawnWeight,
                        1,
                        3,
                        EnumCreatureType.waterCreature,
                        biomes
                );
            }
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

    public static boolean isSpawnBiome(BiomeGenBase biome) {
        if (biome == null) {
            return false;
        }
        String name = biome.biomeName == null ? "" : biome.biomeName.toLowerCase(Locale.ROOT);
        return name.contains("swamp")
                || name.contains("river")
                || name.contains("marsh")
                || name.contains("bog")
                || name.contains("wetland")
                || name.contains("mangrove");
    }

    private static BiomeGenBase[] getSpawnBiomes() {
        List<BiomeGenBase> out = new ArrayList<BiomeGenBase>();
        BiomeGenBase[] all = BiomeGenBase.getBiomeGenArray();
        if (all == null) {
            return new BiomeGenBase[0];
        }

        for (BiomeGenBase biome : all) {
            if (isSpawnBiome(biome)) {
                out.add(biome);
            }
        }
        return out.toArray(new BiomeGenBase[out.size()]);
    }

    private static void registerEntities() {
        int id = 220;
        EntityRegistry.registerModEntity(EntityAxolotl.class, "Axolotl", id, riftflux.instance, 64, 3, true);
        registerEntityEgg(EntityAxolotl.class, "Axolotl", 0xA8D1D1, 0xF3A4D5);
    }

    private static void registerEntityEgg(Class<? extends Entity> entityClass, String entityName, int primaryColor, int secondaryColor) {
        int entityId = EntityRegistry.findGlobalUniqueEntityId();
        EntityRegistry.registerGlobalEntityID(entityClass, entityName, entityId);
        EntityList.entityEggs.put(entityId, new EntityList.EntityEggInfo(entityId, primaryColor, secondaryColor));
    }
}
