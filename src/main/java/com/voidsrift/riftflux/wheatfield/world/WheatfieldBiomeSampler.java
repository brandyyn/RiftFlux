package com.voidsrift.riftflux.wheatfield.world;

import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

public final class WheatfieldBiomeSampler {
    private static final int REGION_HAS_WHEATFIELD = 1;
    private static final int REGION_HAS_OTHER = 2;

    private final int minX;
    private final int minZ;
    private final int size;
    private final boolean[] wheatfieldMask;

    private WheatfieldBiomeSampler(int minX, int minZ, int size, boolean[] wheatfieldMask) {
        this.minX = minX;
        this.minZ = minZ;
        this.size = size;
        this.wheatfieldMask = wheatfieldMask;
    }

    public static WheatfieldBiomeSampler forChunk(World world, int chunkX, int chunkZ, int radius) {
        if (!WheatfieldTerrainUtil.isOverworld(world) || radius < 0) {
            return null;
        }

        int startX = (chunkX << 4) - radius;
        int startZ = (chunkZ << 4) - radius;
        int size = 16 + radius * 2;
        BiomeGenBase[] biomes = world.getWorldChunkManager().loadBlockGeneratorData(null, startX, startZ, size, size);
        if (biomes == null || biomes.length == 0) {
            return null;
        }

        boolean[] wheatfieldMask = new boolean[biomes.length];
        for (int i = 0; i < biomes.length; i++) {
            wheatfieldMask[i] = WheatfieldTerrainUtil.isWheatfieldBiome(biomes[i]);
        }
        return new WheatfieldBiomeSampler(startX, startZ, size, wheatfieldMask);
    }

    public boolean isWheatfield(int worldX, int worldZ) {
        int localX = worldX - this.minX;
        int localZ = worldZ - this.minZ;
        if (localX < 0 || localZ < 0 || localX >= this.size || localZ >= this.size) {
            return false;
        }
        return this.wheatfieldMask[index(localX, localZ)];
    }

    public boolean hasWheatfieldInRegion(int minWorldX, int minWorldZ, int maxWorldXExclusive, int maxWorldZExclusive) {
        return (scanRegion(minWorldX, minWorldZ, maxWorldXExclusive, maxWorldZExclusive)
                & REGION_HAS_WHEATFIELD) != 0;
    }

    public boolean hasMixedBiomesInRegion(int minWorldX, int minWorldZ, int maxWorldXExclusive, int maxWorldZExclusive) {
        return scanRegion(minWorldX, minWorldZ, maxWorldXExclusive, maxWorldZExclusive)
                == (REGION_HAS_WHEATFIELD | REGION_HAS_OTHER);
    }

    public float distanceToNearestBoundary(int worldX, int worldZ, int maxRadius) {
        int localX = worldX - this.minX;
        int localZ = worldZ - this.minZ;
        if (!isWithinBounds(localX, localZ)) {
            return maxRadius + 1.0F;
        }

        boolean wheatfield = this.wheatfieldMask[index(localX, localZ)];
        int bestSq = Integer.MAX_VALUE;
        for (int dz = -maxRadius; dz <= maxRadius; dz++) {
            int sampleZ = localZ + dz;
            if (sampleZ < 0 || sampleZ >= this.size) {
                continue;
            }
            for (int dx = -maxRadius; dx <= maxRadius; dx++) {
                int sampleX = localX + dx;
                if (sampleX < 0 || sampleX >= this.size) {
                    continue;
                }
                int distanceSq = dx * dx + dz * dz;
                if (distanceSq == 0 || distanceSq >= bestSq || distanceSq > maxRadius * maxRadius) {
                    continue;
                }
                if (this.wheatfieldMask[index(sampleX, sampleZ)] != wheatfield) {
                    bestSq = distanceSq;
                }
            }
        }

        return bestSq == Integer.MAX_VALUE ? maxRadius + 1.0F : MathHelper.sqrt_float(bestSq);
    }

    public float distanceToNearestWheatfield(int worldX, int worldZ, int maxRadius) {
        int localX = worldX - this.minX;
        int localZ = worldZ - this.minZ;
        if (!isWithinBounds(localX, localZ)) {
            return maxRadius + 1.0F;
        }
        if (this.wheatfieldMask[index(localX, localZ)]) {
            return 0.0F;
        }

        int bestSq = Integer.MAX_VALUE;
        for (int dz = -maxRadius; dz <= maxRadius; dz++) {
            int sampleZ = localZ + dz;
            if (sampleZ < 0 || sampleZ >= this.size) {
                continue;
            }
            for (int dx = -maxRadius; dx <= maxRadius; dx++) {
                int sampleX = localX + dx;
                if (sampleX < 0 || sampleX >= this.size) {
                    continue;
                }
                int distanceSq = dx * dx + dz * dz;
                if (distanceSq >= bestSq || distanceSq > maxRadius * maxRadius) {
                    continue;
                }
                if (this.wheatfieldMask[index(sampleX, sampleZ)]) {
                    bestSq = distanceSq;
                }
            }
        }

        return bestSq == Integer.MAX_VALUE ? maxRadius + 1.0F : MathHelper.sqrt_float(bestSq);
    }

    private boolean isWithinBounds(int localX, int localZ) {
        return localX >= 0 && localZ >= 0 && localX < this.size && localZ < this.size;
    }

    private int index(int localX, int localZ) {
        return localX + localZ * this.size;
    }

    private int scanRegion(int minWorldX, int minWorldZ, int maxWorldXExclusive, int maxWorldZExclusive) {
        int startX = Math.max(0, minWorldX - this.minX);
        int startZ = Math.max(0, minWorldZ - this.minZ);
        int endX = Math.min(this.size, maxWorldXExclusive - this.minX);
        int endZ = Math.min(this.size, maxWorldZExclusive - this.minZ);
        if (startX >= endX || startZ >= endZ) {
            return 0;
        }

        int state = 0;
        for (int localZ = startZ; localZ < endZ; localZ++) {
            int rowIndex = localZ * this.size;
            for (int localX = startX; localX < endX; localX++) {
                if (this.wheatfieldMask[rowIndex + localX]) {
                    state |= REGION_HAS_WHEATFIELD;
                } else {
                    state |= REGION_HAS_OTHER;
                }

                if (state == (REGION_HAS_WHEATFIELD | REGION_HAS_OTHER)) {
                    return state;
                }
            }
        }

        return state;
    }
}
