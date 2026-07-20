package com.voidsrift.riftflux.hotsprings;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.util.ConfigResolver;
import cpw.mods.fml.common.IWorldGenerator;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenLakes;

import java.util.Random;

public final class HotSpringsLakeWorldGenerator implements IWorldGenerator {
    @Override
    public void generate(
            Random random,
            int chunkX,
            int chunkZ,
            World world,
            IChunkProvider chunkGenerator,
            IChunkProvider chunkProvider) {
        if (world == null
                || world.provider == null
                || HotSpringsContent.springWaterBlock == null
                || !isDimensionAllowed(world.provider.dimensionId)
                || random.nextInt(Math.max(1, ModConfig.hotSpringsLakeRarity)) != 0) {
            return;
        }

        int x = chunkX * 16 + random.nextInt(16) + 8;
        int z = chunkZ * 16 + random.nextInt(16) + 8;
        BiomeGenBase biome = world.getBiomeGenForCoords(x, z);
        if (!isBiomeAllowed(biome)) {
            return;
        }

        int y = world.getHeightValue(x, z);
        if (y <= 0) {
            return;
        }
        new WorldGenLakes(HotSpringsContent.springWaterBlock).generate(world, random, x, y, z);
    }

    private static boolean isDimensionAllowed(int dimensionId) {
        if (contains(ModConfig.hotSpringsLakeDimensionBlacklist, dimensionId)) {
            return false;
        }
        int[] whitelist = ModConfig.hotSpringsLakeDimensionWhitelist;
        return whitelist == null || whitelist.length == 0 || contains(whitelist, dimensionId);
    }

    private static boolean isBiomeAllowed(BiomeGenBase biome) {
        if (matchesAnyBiome(biome, ModConfig.hotSpringsLakeBiomeBlacklist)) {
            return false;
        }
        String[] whitelist = ModConfig.hotSpringsLakeBiomeWhitelist;
        return whitelist == null || whitelist.length == 0 || matchesAnyBiome(biome, whitelist);
    }

    private static boolean matchesAnyBiome(BiomeGenBase biome, String[] entries) {
        if (biome == null || entries == null) {
            return false;
        }
        for (String entry : entries) {
            if (ConfigResolver.matchesBiomeEntry(biome, entry)) {
                return true;
            }
        }
        return false;
    }

    private static boolean contains(int[] values, int target) {
        if (values == null) {
            return false;
        }
        for (int value : values) {
            if (value == target) {
                return true;
            }
        }
        return false;
    }
}
