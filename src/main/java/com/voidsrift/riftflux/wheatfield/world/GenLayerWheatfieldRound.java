package com.voidsrift.riftflux.wheatfield.world;

import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;

public final class GenLayerWheatfieldRound extends GenLayer {
    private static final int DENSITY_RADIUS = 2;
    private static final float EDGE_NOISE_RANGE = 0.14F;
    private final int wheatfieldBiomeId;

    public GenLayerWheatfieldRound(long seed, GenLayer parent, int wheatfieldBiomeId) {
        super(seed);
        this.parent = parent;
        this.wheatfieldBiomeId = wheatfieldBiomeId;
    }

    @Override
    public int[] getInts(int areaX, int areaY, int areaWidth, int areaHeight) {
        int border = DENSITY_RADIUS < 1 ? 1 : DENSITY_RADIUS;
        int parentX = areaX - border;
        int parentY = areaY - border;
        int parentWidth = areaWidth + border * 2;
        int parentHeight = areaHeight + border * 2;
        int[] parentInts = parent.getInts(parentX, parentY, parentWidth, parentHeight);
        int[] out = IntCache.getIntCache(areaWidth * areaHeight);

        for (int localY = 0; localY < areaHeight; localY++) {
            for (int localX = 0; localX < areaWidth; localX++) {
                int centerX = localX + border;
                int centerY = localY + border;
                int centerIndex = centerX + centerY * parentWidth;
                int center = parentInts[centerIndex];

                if (GenLayer.isBiomeOceanic(center) || WheatfieldTerrainUtil.isRiverBiome(center)) {
                    out[localX + localY * areaWidth] = center;
                    continue;
                }

                int wheatfieldCardinal = 0;
                int wheatfieldDiagonal = 0;
                int dominantNonWheatfield = center;
                int dominantNonWheatfieldCount = 0;

                for (int offsetY = -1; offsetY <= 1; offsetY++) {
                    for (int offsetX = -1; offsetX <= 1; offsetX++) {
                        if (offsetX == 0 && offsetY == 0) {
                            continue;
                        }
                        int sample = parentInts[centerX + offsetX + (centerY + offsetY) * parentWidth];
                        if (sample == wheatfieldBiomeId) {
                            if (offsetX == 0 || offsetY == 0) {
                                wheatfieldCardinal++;
                            } else {
                                wheatfieldDiagonal++;
                            }
                        } else if (!GenLayer.isBiomeOceanic(sample) && !WheatfieldTerrainUtil.isRiverBiome(sample)) {
                            int sampleCount = countBiomeInNeighborhood(parentInts, parentWidth, centerX, centerY, sample);
                            if (sampleCount > dominantNonWheatfieldCount) {
                                dominantNonWheatfieldCount = sampleCount;
                                dominantNonWheatfield = sample;
                            }
                        }
                    }
                }

                this.initChunkSeed(areaX + localX, areaY + localY);
                int wheatfieldNear = countBiomeInRadius(parentInts, parentWidth, centerX, centerY, wheatfieldBiomeId, DENSITY_RADIUS);
                int landNear = countLandInRadius(parentInts, parentWidth, centerX, centerY, DENSITY_RADIUS);
                float localWheatfieldDensity = landNear <= 0 ? 0.0F : (float) wheatfieldNear / (float) landNear;
                float edgeNoise = ((float) this.nextInt(1000) / 999.0F - 0.5F) * EDGE_NOISE_RANGE;

                if (center == wheatfieldBiomeId) {
                    float keepScore = localWheatfieldDensity
                            + wheatfieldCardinal * 0.09F
                            + wheatfieldDiagonal * 0.06F
                            + edgeNoise;
                    boolean erode = (wheatfieldCardinal <= 1 && wheatfieldDiagonal <= 1)
                            || keepScore < 0.53F;
                    out[localX + localY * areaWidth] = erode ? dominantNonWheatfield : center;
                } else {
                    float growScore = localWheatfieldDensity
                            + wheatfieldCardinal * 0.11F
                            + wheatfieldDiagonal * 0.08F
                            + edgeNoise;
                    boolean growCore = growScore >= 0.70F;
                    boolean growTendril = localWheatfieldDensity >= 0.34F
                            && wheatfieldCardinal >= 1
                            && wheatfieldDiagonal >= 1
                            && this.nextInt(6) == 0;
                    boolean expand = growCore || growTendril;
                    out[localX + localY * areaWidth] = expand ? wheatfieldBiomeId : center;
                }
            }
        }

        return out;
    }

    private static int countBiomeInNeighborhood(int[] parentInts, int parentWidth, int centerX, int centerY, int biomeId) {
        int count = 0;
        for (int offsetY = -1; offsetY <= 1; offsetY++) {
            for (int offsetX = -1; offsetX <= 1; offsetX++) {
                int sample = parentInts[centerX + offsetX + (centerY + offsetY) * parentWidth];
                if (sample == biomeId) {
                    count++;
                }
            }
        }
        return count;
    }

    private static int countBiomeInRadius(int[] parentInts, int parentWidth, int centerX, int centerY, int biomeId, int radius) {
        int count = 0;
        for (int offsetY = -radius; offsetY <= radius; offsetY++) {
            for (int offsetX = -radius; offsetX <= radius; offsetX++) {
                int sample = parentInts[centerX + offsetX + (centerY + offsetY) * parentWidth];
                if (sample == biomeId) {
                    count++;
                }
            }
        }
        return count;
    }

    private static int countLandInRadius(int[] parentInts, int parentWidth, int centerX, int centerY, int radius) {
        int count = 0;
        for (int offsetY = -radius; offsetY <= radius; offsetY++) {
            for (int offsetX = -radius; offsetX <= radius; offsetX++) {
                int sample = parentInts[centerX + offsetX + (centerY + offsetY) * parentWidth];
                if (!GenLayer.isBiomeOceanic(sample) && !WheatfieldTerrainUtil.isRiverBiome(sample)) {
                    count++;
                }
            }
        }
        return count;
    }
}
