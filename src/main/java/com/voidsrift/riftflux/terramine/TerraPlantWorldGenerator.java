package com.voidsrift.riftflux.terramine;

import com.voidsrift.riftflux.ModConfig;
import cpw.mods.fml.common.IWorldGenerator;
import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.common.BiomeDictionary;

import java.util.Random;

public class TerraPlantWorldGenerator implements IWorldGenerator {
    private static final int CHUNK_BORDER = 1;
    private static final int CHUNK_SAFE_SPAN = 16 - CHUNK_BORDER * 2;
    private static final int UNDERGROUND_MIN_Y = 6;
    private static final int UNDERGROUND_SPAN = 50;

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world,
                         IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
        if (world == null || world.provider == null || world.provider.dimensionId != 0) {
            return;
        }
        if (TerrariaContent.daybloomBlock == null
                || TerrariaContent.terraMushroomBlock == null
                || TerrariaContent.blinkrootBlock == null
                || TerrariaContent.waterleafBlock == null
                || TerrariaContent.deathweedBlock == null
                || TerrariaContent.fireblossomBlock == null
                || TerrariaContent.jungleSporeBlock == null
                || TerrariaContent.moonglowBlock == null) {
            return;
        }

        final int originX = chunkX * 16;
        final int originZ = chunkZ * 16;

        if (shouldAttemptChunk(random, ModConfig.daybloomSpawnAttempts)) {
            generateSurfacePlants(world, random, originX, originZ, TerrariaContent.daybloomBlock, 1);
        }
        if (shouldAttemptChunk(random, ModConfig.terraMushroomSpawnAttempts)) {
            generateSurfacePlants(world, random, originX, originZ, TerrariaContent.terraMushroomBlock, 1);
        }

        if (shouldAttemptChunk(random, ModConfig.blinkrootSpawnAttempts)) {
            generateUndergroundPlants(world, random, originX, originZ, TerrariaContent.blinkrootBlock, 1, false);
        }
        if (shouldAttemptChunk(random, ModConfig.waterleafSpawnAttempts)) {
            generateUndergroundPlants(world, random, originX, originZ, TerrariaContent.waterleafBlock, 1, false);
        }
        if (shouldAttemptChunk(random, ModConfig.deathweedSpawnAttempts)) {
            generateUndergroundPlants(world, random, originX, originZ, TerrariaContent.deathweedBlock, 1, false);
        }
        if (shouldAttemptChunk(random, ModConfig.fireblossomSpawnAttempts)) {
            generateUndergroundPlants(world, random, originX, originZ, TerrariaContent.fireblossomBlock, 1, false);
        }

        if (shouldAttemptChunk(random, ModConfig.jungleSporeSpawnAttempts)) {
            generateUndergroundPlants(world, random, originX, originZ, TerrariaContent.jungleSporeBlock, 1, true);
        }
        if (shouldAttemptChunk(random, ModConfig.moonglowSpawnAttempts)) {
            generateUndergroundPlants(world, random, originX, originZ, TerrariaContent.moonglowBlock, 1, true);
        }
    }

    private static void generateSurfacePlants(World world, Random random, int originX, int originZ, Block plant, int attempts) {
        if (attempts <= 0 || plant == null) {
            return;
        }
        int maxY = world.getActualHeight() - 1;
        for (int i = 0; i < attempts; i++) {
            int x = originX + CHUNK_BORDER + random.nextInt(CHUNK_SAFE_SPAN);
            int z = originZ + CHUNK_BORDER + random.nextInt(CHUNK_SAFE_SPAN);
            int surfaceY = world.getHeightValue(x, z) - 1;
            if (surfaceY < 1 || surfaceY >= maxY) {
                continue;
            }
            if (!world.isAirBlock(x, surfaceY + 1, z)) {
                continue;
            }
            if (canPlacePlant(world, x, surfaceY + 1, z, plant)) {
                world.setBlock(x, surfaceY + 1, z, plant, 0, 2);
            }
        }
    }

    private static void generateUndergroundPlants(World world, Random random, int originX, int originZ, Block plant, int attempts, boolean jungleOnly) {
        if (attempts <= 0 || plant == null) {
            return;
        }
        for (int i = 0; i < attempts; i++) {
            int x = originX + CHUNK_BORDER + random.nextInt(CHUNK_SAFE_SPAN);
            int z = originZ + CHUNK_BORDER + random.nextInt(CHUNK_SAFE_SPAN);
            int y = UNDERGROUND_MIN_Y + random.nextInt(UNDERGROUND_SPAN);

            int surfaceY = world.getHeightValue(x, z);
            if (y >= surfaceY - 2) {
                continue;
            }

            if (jungleOnly) {
                BiomeGenBase biome = world.getBiomeGenForCoords(x, z);
                if (!isJungleBiome(biome)) {
                    continue;
                }
            }

            if (!world.isAirBlock(x, y + 1, z)) {
                continue;
            }
            if (canPlacePlant(world, x, y + 1, z, plant)) {
                world.setBlock(x, y + 1, z, plant, 0, 2);
            }
        }
    }

    private static boolean canPlacePlant(World world, int x, int y, int z, Block plant) {
        return plant != null && plant.canPlaceBlockAt(world, x, y, z);
    }

    private static boolean isJungleBiome(BiomeGenBase biome) {
        if (biome == null) {
            return false;
        }
        if (BiomeDictionary.isBiomeOfType(biome, BiomeDictionary.Type.JUNGLE)) {
            return true;
        }
        String name = biome.biomeName == null ? "" : biome.biomeName.toLowerCase();
        return name.contains("jungle");
    }

    private static boolean shouldAttemptChunk(Random random, int oneInN) {
        if (oneInN <= 0) {
            return false;
        }
        if (oneInN == 1) {
            return true;
        }
        return random.nextInt(oneInN) == 0;
    }
}
