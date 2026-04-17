package com.voidsrift.riftflux.offlawn;

import com.voidsrift.riftflux.ModConfig;
import cpw.mods.fml.common.IWorldGenerator;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.IChunkProvider;

import java.util.Random;

public class OffLawnWorldGenerator implements IWorldGenerator {
    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
        if (world == null || world.provider == null || world.provider.dimensionId != 0) {
            return;
        }
        if (!OffLawnContent.isEnabled() || OffLawnContent.sunflowerBush == null) {
            return;
        }

        int oneInN = Math.max(1, ModConfig.offLawnSunflowerPatchChance);
        if (oneInN > 1 && random.nextInt(oneInN) != 0) {
            return;
        }

        int centerX = (chunkX << 4) + 8;
        int centerZ = (chunkZ << 4) + 8;
        BiomeGenBase biome = world.getBiomeGenForCoords(centerX, centerZ);
        if (!isWindsweptEquivalent(biome)) {
            return;
        }

        int attempts = Math.max(0, ModConfig.offLawnSunflowerAttemptsPerChunk);
        for (int i = 0; i < attempts; i++) {
            int x = (chunkX << 4) + random.nextInt(16);
            int z = (chunkZ << 4) + random.nextInt(16);
            int y = world.getHeightValue(x, z);
            if (y < 1 || y >= world.getActualHeight() - 1) {
                continue;
            }
            if (!world.isAirBlock(x, y, z)) {
                continue;
            }
            if (!world.isAirBlock(x, y + 1, z) || !OffLawnContent.sunflowerBush.canPlaceBlockAt(world, x, y, z)) {
                continue;
            }
            if (OffLawnContent.sunflowerBush instanceof BlockOffLawnSunflowerBush) {
                ((BlockOffLawnSunflowerBush) OffLawnContent.sunflowerBush).placeAt(world, x, y, z, 2);
            } else {
                world.setBlock(x, y, z, OffLawnContent.sunflowerBush, 0, 2);
            }
        }
    }

    private static boolean isWindsweptEquivalent(BiomeGenBase biome) {
        if (biome == null) {
            return false;
        }
        if (biome == BiomeGenBase.extremeHills || biome == BiomeGenBase.extremeHillsEdge || biome == BiomeGenBase.extremeHillsPlus) {
            return true;
        }
        String name = biome.biomeName == null ? "" : biome.biomeName.toLowerCase();
        return name.contains("windswept") || name.contains("extreme hills");
    }
}
