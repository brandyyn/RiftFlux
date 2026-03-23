package com.voidsrift.riftflux.wheatfield.world;

import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;

public final class GenLayerWheatfieldRound extends GenLayer {
    private final int wheatfieldBiomeId;

    public GenLayerWheatfieldRound(long seed, GenLayer parent, int wheatfieldBiomeId) {
        super(seed);
        this.parent = parent;
        this.wheatfieldBiomeId = wheatfieldBiomeId;
    }

    @Override
    public int[] getInts(int areaX, int areaY, int areaWidth, int areaHeight) {
        int parentX = areaX - 1;
        int parentY = areaY - 1;
        int parentWidth = areaWidth + 2;
        int parentHeight = areaHeight + 2;
        int[] parentInts = parent.getInts(parentX, parentY, parentWidth, parentHeight);
        int[] out = IntCache.getIntCache(areaWidth * areaHeight);

        for (int localY = 0; localY < areaHeight; localY++) {
            for (int localX = 0; localX < areaWidth; localX++) {
                int centerIndex = localX + 1 + (localY + 1) * parentWidth;
                int center = parentInts[centerIndex];

                if (GenLayer.isBiomeOceanic(center) || WheatfieldTerrainUtil.isRiverBiome(center)) {
                    out[localX + localY * areaWidth] = center;
                    continue;
                }

                int wheatfieldNeighbors = 0;
                int dominantNonWheatfield = center;
                int dominantNonWheatfieldCount = 0;

                for (int offsetY = -1; offsetY <= 1; offsetY++) {
                    for (int offsetX = -1; offsetX <= 1; offsetX++) {
                        int sample = parentInts[localX + 1 + offsetX + (localY + 1 + offsetY) * parentWidth];
                        if (sample == wheatfieldBiomeId) {
                            wheatfieldNeighbors++;
                        } else if (!GenLayer.isBiomeOceanic(sample)) {
                            int sampleCount = countBiomeInNeighborhood(parentInts, parentWidth, localX, localY, sample);
                            if (sampleCount > dominantNonWheatfieldCount) {
                                dominantNonWheatfieldCount = sampleCount;
                                dominantNonWheatfield = sample;
                            }
                        }
                    }
                }

                if (center == wheatfieldBiomeId) {
                    out[localX + localY * areaWidth] = wheatfieldNeighbors <= 2 ? dominantNonWheatfield : center;
                } else {
                    out[localX + localY * areaWidth] = wheatfieldNeighbors >= 6 ? wheatfieldBiomeId : center;
                }
            }
        }

        return out;
    }

    private static int countBiomeInNeighborhood(int[] parentInts, int parentWidth, int localX, int localY, int biomeId) {
        int count = 0;
        for (int offsetY = -1; offsetY <= 1; offsetY++) {
            for (int offsetX = -1; offsetX <= 1; offsetX++) {
                int sample = parentInts[localX + 1 + offsetX + (localY + 1 + offsetY) * parentWidth];
                if (sample == biomeId) {
                    count++;
                }
            }
        }
        return count;
    }
}
