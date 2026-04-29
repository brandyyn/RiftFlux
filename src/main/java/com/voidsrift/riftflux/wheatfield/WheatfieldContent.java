package com.voidsrift.riftflux.wheatfield;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.wheatfield.client.RenderWheatfieldBarley;
import com.voidsrift.riftflux.wheatfield.world.WheatfieldTerrainHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.common.MinecraftForge;

public final class WheatfieldContent {
    public static Block wheatfieldBarley;
    public static BiomeGenBase wheatfieldBiome;

    private static boolean preInited;
    private static boolean initialized;
    private static boolean clientInitialized;
    private static boolean terrainHooksRegistered;
    private static WheatfieldTerrainHandler terrainHandler;

    private WheatfieldContent() {
    }

    public static boolean isEnabled() {
        return ModConfig.enableWheatfieldBiome;
    }

    public static void preInit(FMLPreInitializationEvent event) {
        if (preInited) {
            return;
        }
        preInited = true;

        if (!isEnabled()) {
            return;
        }

        wheatfieldBarley = new BlockWheatfieldBarley();
        GameRegistry.registerBlock(wheatfieldBarley, ItemBlockWheatfieldBarley.class, "barley");

        int biomeId = resolveBiomeId(ModConfig.wheatfieldBiomeId);
        if (biomeId >= 0) {
            wheatfieldBiome = new BiomeGenWheatfield(biomeId, (BlockWheatfieldBarley) wheatfieldBarley);
        }
    }

    public static void init(FMLInitializationEvent event) {
        if (initialized) {
            return;
        }
        initialized = true;

        if (!isEnabled() || wheatfieldBiome == null) {
            return;
        }

        if (ModConfig.wheatfieldBiomeWeight > 0) {
            BiomeManager.addBiome(
                    BiomeManager.BiomeType.WARM,
                    new BiomeManager.BiomeEntry(wheatfieldBiome, ModConfig.wheatfieldBiomeWeight)
            );
        }
        BiomeDictionary.registerBiomeType(
                wheatfieldBiome,
                BiomeDictionary.Type.PLAINS,
                BiomeDictionary.Type.SPOOKY
        );
        BiomeManager.addSpawnBiome(wheatfieldBiome);
        BiomeManager.addStrongholdBiome(wheatfieldBiome);
        if (ModConfig.wheatfieldAllowVillage) {
            BiomeManager.addVillageBiome(wheatfieldBiome, true);
        }
        if (!terrainHooksRegistered) {
            terrainHooksRegistered = true;
            terrainHandler = new WheatfieldTerrainHandler();
            MinecraftForge.TERRAIN_GEN_BUS.register(terrainHandler);
            MinecraftForge.EVENT_BUS.register(terrainHandler);
        }
    }

    public static void initClient() {
        if (clientInitialized) {
            return;
        }
        clientInitialized = true;

        if (!isEnabled() || wheatfieldBarley == null) {
            return;
        }

        if (WheatfieldRenderIds.barleyRenderId < 0) {
            WheatfieldRenderIds.barleyRenderId = RenderingRegistry.getNextAvailableRenderId();
            RenderingRegistry.registerBlockHandler(new RenderWheatfieldBarley(WheatfieldRenderIds.barleyRenderId));
        }
    }

    private static int resolveBiomeId(int configuredId) {
        BiomeGenBase[] biomes = BiomeGenBase.getBiomeGenArray();
        if (biomes == null || biomes.length == 0) {
            FMLLog.severe("[RiftFlux] Unable to register Wheatfield biome: biome array is unavailable.");
            return -1;
        }

        if (isFreeBiomeId(biomes, configuredId)) {
            return configuredId;
        }

        for (int i = Math.max(0, configuredId + 1); i < biomes.length; i++) {
            if (biomes[i] == null) {
                logBiomeFallback(configuredId, biomes, i);
                return i;
            }
        }

        for (int i = 0; i < biomes.length; i++) {
            if (biomes[i] == null) {
                logBiomeFallback(configuredId, biomes, i);
                return i;
            }
        }

        FMLLog.severe("[RiftFlux] Unable to register Wheatfield biome: no free biome IDs remain.");
        return -1;
    }

    private static void logBiomeFallback(int configuredId, BiomeGenBase[] biomes, int fallbackId) {
        String occupiedBy = "unknown biome";
        if (configuredId >= 0 && configuredId < biomes.length && biomes[configuredId] != null && biomes[configuredId].biomeName != null) {
            occupiedBy = biomes[configuredId].biomeName;
        }
        FMLLog.warning("[RiftFlux] Wheatfield biome ID %d is occupied by %s; using free biome ID %d instead.", configuredId, occupiedBy, fallbackId);
    }

    private static boolean isFreeBiomeId(BiomeGenBase[] biomes, int id) {
        return id >= 0 && id < biomes.length && biomes[id] == null;
    }

}
