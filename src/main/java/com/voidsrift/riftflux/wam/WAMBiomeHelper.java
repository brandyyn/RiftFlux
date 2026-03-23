package com.voidsrift.riftflux.wam;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.util.ConfigResolver;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

final class WAMBiomeHelper {
    private WAMBiomeHelper() {
    }

    public static BiomeGenBase[] getAllOverworldBiomes() {
        List<BiomeGenBase> biomes = new ArrayList<BiomeGenBase>();
        BiomeGenBase[] all = BiomeGenBase.getBiomeGenArray();
        if (all == null) {
            return new BiomeGenBase[0];
        }

        for (BiomeGenBase biome : all) {
            if (isOverworldBiome(biome)) {
                biomes.add(biome);
            }
        }

        return biomes.toArray(new BiomeGenBase[biomes.size()]);
    }

    public static BiomeGenBase[] getForestBiomes() {
        List<BiomeGenBase> biomes = new ArrayList<BiomeGenBase>();
        BiomeGenBase[] all = BiomeGenBase.getBiomeGenArray();
        if (all == null) {
            return new BiomeGenBase[0];
        }

        for (BiomeGenBase biome : all) {
            if (isForestBiome(biome)) {
                biomes.add(biome);
            }
        }

        return biomes.toArray(new BiomeGenBase[biomes.size()]);
    }

    public static BiomeGenBase[] getSpawnBiomes(String mobKey) {
        String normalized = ConfigResolver.normalizeToken(mobKey);
        boolean whitelist = usesBiomeWhitelist(normalized);
        BiomeGenBase[] baseBiomes = whitelist || "cyclops".equals(normalized)
                ? getAllOverworldBiomes()
                : getForestBiomes();
        return ConfigResolver.filterBiomes(baseBiomes, getConfiguredBiomeList(normalized), whitelist);
    }

    public static boolean isOverworldBiome(BiomeGenBase biome) {
        return biome != null && biome != BiomeGenBase.hell && biome != BiomeGenBase.sky;
    }

    public static boolean isForestBiome(BiomeGenBase biome) {
        if (!isOverworldBiome(biome)) {
            return false;
        }

        if (BiomeDictionary.isBiomeOfType(biome, BiomeDictionary.Type.FOREST)
                || BiomeDictionary.isBiomeOfType(biome, BiomeDictionary.Type.CONIFEROUS)) {
            return true;
        }

        String name = biome.biomeName == null ? "" : biome.biomeName.toLowerCase(Locale.ROOT);
        return name.contains("forest")
                || name.contains("woods")
                || name.contains("woodland")
                || name.contains("grove")
                || name.contains("thicket")
                || name.contains("timber")
                || name.contains("taiga");
    }

    private static boolean usesBiomeWhitelist(String normalizedKey) {
        if ("cyclops".equals(normalizedKey)) {
            return ModConfig.cyclopsUseBiomeWhitelist;
        }
        if ("flowerman".equals(normalizedKey)) {
            return ModConfig.flowerManUseBiomeWhitelist;
        }
        if ("endertroll".equals(normalizedKey) || "troll".equals(normalizedKey)) {
            return ModConfig.enderTrollUseBiomeWhitelist;
        }
        if ("jaxx".equals(normalizedKey)) {
            return ModConfig.jaxxUseBiomeWhitelist;
        }
        if ("blackwidow".equals(normalizedKey) || "widow".equals(normalizedKey) || "medianwidow".equals(normalizedKey)) {
            return ModConfig.blackWidowUseBiomeWhitelist;
        }
        return false;
    }

    private static String[] getConfiguredBiomeList(String normalizedKey) {
        if ("cyclops".equals(normalizedKey)) {
            return ModConfig.cyclopsBiomeList;
        }
        if ("flowerman".equals(normalizedKey)) {
            return ModConfig.flowerManBiomeList;
        }
        if ("endertroll".equals(normalizedKey) || "troll".equals(normalizedKey)) {
            return ModConfig.enderTrollBiomeList;
        }
        if ("jaxx".equals(normalizedKey)) {
            return ModConfig.jaxxBiomeList;
        }
        if ("blackwidow".equals(normalizedKey) || "widow".equals(normalizedKey) || "medianwidow".equals(normalizedKey)) {
            return ModConfig.blackWidowBiomeList;
        }
        return new String[0];
    }
}
