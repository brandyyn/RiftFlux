package net.nmccoy.legendgear;

import cpw.mods.fml.common.IWorldGenerator;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;

public class CaltropsUndergroundGenerator implements IWorldGenerator {
    private static final int MAX_SPAWN_ATTEMPTS_PER_CHUNK = 256;
    private static final int MAX_VERTICAL_PROBE_DISTANCE = 2;

    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
        if (world == null || world.provider == null || world.provider.dimensionId != 0) {
            return;
        }
        if (!LegendGear2.CONFIG_CALTROPS_UNDERGROUND_GEN_ENABLED || LegendGear2.caltropsBlock == null) {
            return;
        }

        int spawnAttempts = Math.max(0, Math.min(MAX_SPAWN_ATTEMPTS_PER_CHUNK, LegendGear2.CONFIG_CALTROPS_UNDERGROUND_SPAWN_CHANCE));
        if (spawnAttempts <= 0) {
            return;
        }

        int maxBuildY = Math.max(2, world.getActualHeight() - 2);
        int minY = Math.max(1, Math.min(maxBuildY, LegendGear2.CONFIG_CALTROPS_UNDERGROUND_MIN_Y));
        int maxY = Math.max(1, Math.min(maxBuildY, LegendGear2.CONFIG_CALTROPS_UNDERGROUND_MAX_Y));
        if (maxY < minY) {
            int swap = minY;
            minY = maxY;
            maxY = swap;
        }

        int worldXBase = chunkX * 16;
        int worldZBase = chunkZ * 16;
        for (int attempt = 0; attempt < spawnAttempts; ++attempt) {
            int x = worldXBase + random.nextInt(16);
            int z = worldZBase + random.nextInt(16);
            int startY = minY + random.nextInt(maxY - minY + 1);
            int y = this.findPlacementY(world, x, z, minY, maxY, startY);
            if (y < 0) {
                continue;
            }
            world.setBlock(x, y, z, (Block) LegendGear2.caltropsBlock, 0, 2);
        }
    }

    private int findPlacementY(World world, int x, int z, int minY, int maxY, int startY) {
        if (this.canPlaceUndergroundCaltrops(world, x, startY, z)) {
            return startY;
        }

        for (int offset = 1; offset <= MAX_VERTICAL_PROBE_DISTANCE; ++offset) {
            int lowerY = startY - offset;
            if (lowerY >= minY && this.canPlaceUndergroundCaltrops(world, x, lowerY, z)) {
                return lowerY;
            }

            int upperY = startY + offset;
            if (upperY <= maxY && this.canPlaceUndergroundCaltrops(world, x, upperY, z)) {
                return upperY;
            }
        }
        return -1;
    }

    private boolean canPlaceUndergroundCaltrops(World world, int x, int y, int z) {
        if (world.canBlockSeeTheSky(x, y, z)) {
            return false;
        }
        if (!world.isAirBlock(x, y, z) || !world.isAirBlock(x, y + 1, z)) {
            return false;
        }

        Block floor = world.getBlock(x, y - 1, z);
        Material material = floor.getMaterial();
        boolean naturalFloor = material == Material.rock
                || material == Material.ground
                || material == Material.sand
                || floor == Blocks.gravel
                || floor == Blocks.cobblestone
                || floor == Blocks.mossy_cobblestone;
        if (!naturalFloor) {
            return false;
        }

        return LegendGear2.caltropsBlock.canPlaceBlockAt(world, x, y, z);
    }
}
