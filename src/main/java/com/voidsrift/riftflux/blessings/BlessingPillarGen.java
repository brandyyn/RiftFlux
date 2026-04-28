package com.voidsrift.riftflux.blessings;

import com.voidsrift.riftflux.ModConfig;
import cpw.mods.fml.common.IWorldGenerator;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;

import java.util.Random;

public class BlessingPillarGen implements IWorldGenerator {
    private static final int CHUNK_BORDER = 1;
    private static final int CHUNK_SAFE_SPAN = 16 - CHUNK_BORDER * 2;
    private static final int MIN_GROUND_Y = 30;
    private static final int ATTEMPTS_PER_PILLAR = 24;
    private static final int UNDERGROUND_SEARCH_TRIES = 10;
    private static final int CHANCE_MULTIPLIER = 32;

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world,
                         IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
        if (world == null || world.provider == null) {
            return;
        }
        if (!ModConfig.blessingsEnabled || !ModConfig.blessingsPillarGenEnabled) {
            return;
        }
        if (world.provider.dimensionId != 0) {
            return;
        }
        if (ModConfig.blessingsPillarGenChance <= 0) {
            return;
        }
        int effectiveChance = Math.max(1, ModConfig.blessingsPillarGenChance * CHANCE_MULTIPLIER);
        if (random.nextInt(effectiveChance) != 0) {
            return;
        }

        Block pillar = BlessingContent.blessingPillar;
        if (pillar == null) {
            return;
        }

        int max = Math.max(1, ModConfig.blessingsPillarMaxPerChunk);
        int toPlace = random.nextInt(max) + 1;
        int added = 0;
        int originX = chunkX * 16;
        int originZ = chunkZ * 16;

        for (int n = 0; n < toPlace; n++) {
            for (int attempt = 0; attempt < ATTEMPTS_PER_PILLAR; attempt++) {
                int x = originX + CHUNK_BORDER + random.nextInt(CHUNK_SAFE_SPAN);
                int z = originZ + CHUNK_BORDER + random.nextInt(CHUNK_SAFE_SPAN);
                int y = random.nextBoolean() ? findSurfacePlacementY(world, x, z) : findUndergroundPlacementY(world, random, x, z);
                if (y < MIN_GROUND_Y) {
                    y = findSurfacePlacementY(world, x, z);
                }
                if (y < MIN_GROUND_Y) {
                    y = findUndergroundPlacementY(world, random, x, z);
                }
                if (y < MIN_GROUND_Y || !canPlacePillarAt(world, pillar, x, y, z)) {
                    continue;
                }

                world.setBlock(x, y + 1, z, pillar, 0, 2);
                world.setBlock(x, y + 2, z, pillar, 1, 2);
                added++;
                break;
            }
        }
    }

    private static int findSurfacePlacementY(World world, int x, int z) {
        int y = world.getHeightValue(x, z) - 1;
        return y > MIN_GROUND_Y ? y : -1;
    }

    private static int findUndergroundPlacementY(World world, Random random, int x, int z) {
        int surfaceY = world.getHeightValue(x, z) - 1;
        if (surfaceY <= MIN_GROUND_Y + 2) {
            return -1;
        }
        int maxY = surfaceY - 2;
        for (int i = 0; i < UNDERGROUND_SEARCH_TRIES; i++) {
            int y = MIN_GROUND_Y + random.nextInt(Math.max(1, maxY - MIN_GROUND_Y + 1));
            if (y >= surfaceY - 1) {
                continue;
            }
            if (world.canBlockSeeTheSky(x, y + 1, z)) {
                continue;
            }
            if (canPlacePillarAt(world, BlessingContent.blessingPillar, x, y, z)) {
                return y;
            }
        }
        return -1;
    }

    private static boolean canPlacePillarAt(World world, Block pillar, int x, int y, int z) {
        if (world == null || pillar == null || y <= MIN_GROUND_Y || world.isAirBlock(x, y, z)) {
            return false;
        }
        Block ground = world.getBlock(x, y, z);
        if (ground == null || ground.isReplaceable(world, x, y, z) || ground == Blocks.leaves) {
            return false;
        }
        if (!world.isAirBlock(x, y + 1, z) || !world.isAirBlock(x, y + 2, z)) {
            return false;
        }
        return pillar.canPlaceBlockAt(world, x, y + 1, z);
    }
}
