package com.voidsrift.riftflux.wheatfield.world;

import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;

public final class GenLayerWheatfieldRound extends GenLayer {
    private static final int DENSITY_RADIUS = 5;
    private static final int CARDINAL_ARM_LENGTH = 5;
    private static final int DIAGONAL_ARM_LENGTH = 4;
    private static final float EDGE_NOISE_RANGE = 0.08F;
    private static final int[] DIR_X = new int[] { 1, -1, 0, 0, 1, -1, 1, -1 };
    private static final int[] DIR_Y = new int[] { 0, 0, 1, -1, 1, -1, -1, 1 };
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
                int[] directionalSupport = new int[DIR_X.length];
                int strongestArm = 0;
                int armCount = 0;
                int totalDirectionalSupport = 0;
                for (int dir = 0; dir < DIR_X.length; dir++) {
                    directionalSupport[dir] = countDirectionalSupport(parentInts, parentWidth, centerX, centerY, DIR_X[dir], DIR_Y[dir]);
                    strongestArm = Math.max(strongestArm, directionalSupport[dir]);
                    totalDirectionalSupport += directionalSupport[dir];
                    if (directionalSupport[dir] >= 5) {
                        armCount++;
                    }
                }
                int strongestPair = Math.max(
                        Math.max(directionalSupport[0] + directionalSupport[1], directionalSupport[2] + directionalSupport[3]),
                        Math.max(directionalSupport[4] + directionalSupport[5], directionalSupport[6] + directionalSupport[7]));

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
                            + wheatfieldCardinal * 0.07F
                            + wheatfieldDiagonal * 0.05F
                            + strongestArm * 0.028F
                            + strongestPair * 0.012F
                            + edgeNoise;
                    boolean anchoredLine = strongestPair >= 10;
                    boolean anchoredTendril = strongestArm >= 7 || totalDirectionalSupport >= 16;
                    boolean erode = (!anchoredLine && !anchoredTendril && keepScore < 0.44F)
                            || (wheatfieldCardinal == 0 && wheatfieldDiagonal == 0 && strongestArm < 5);
                    out[localX + localY * areaWidth] = erode ? dominantNonWheatfield : center;
                } else {
                    float growScore = localWheatfieldDensity
                            + wheatfieldCardinal * 0.09F
                            + wheatfieldDiagonal * 0.06F
                            + strongestArm * 0.03F
                            + strongestPair * 0.015F
                            + edgeNoise;
                    boolean growCore = growScore >= 0.78F;
                    boolean growTendril = strongestArm >= 7
                            && localWheatfieldDensity >= 0.12F
                            && this.nextInt(strongestArm >= 9 ? 2 : 3) == 0;
                    boolean growBridge = strongestPair >= 10
                            && localWheatfieldDensity >= 0.18F
                            && this.nextInt(4) != 0;
                    boolean growBranch = armCount >= 2
                            && localWheatfieldDensity >= 0.16F
                            && this.nextInt(5) == 0;
                    boolean expand = growCore || growTendril || growBridge || growBranch;
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

    private int countDirectionalSupport(int[] parentInts, int parentWidth, int centerX, int centerY, int dirX, int dirY) {
        int maxSteps = dirX == 0 || dirY == 0 ? CARDINAL_ARM_LENGTH : DIAGONAL_ARM_LENGTH;
        int straight = 0;
        int lateral = 0;
        for (int step = 1; step <= maxSteps; step++) {
            int sampleX = centerX + dirX * step;
            int sampleY = centerY + dirY * step;
            int sample = parentInts[sampleX + sampleY * parentWidth];
            if (sample != wheatfieldBiomeId) {
                break;
            }
            straight++;

            int sideX = dirY;
            int sideY = -dirX;
            if (parentInts[sampleX + sideX + (sampleY + sideY) * parentWidth] == wheatfieldBiomeId) {
                lateral++;
            }
            if (parentInts[sampleX - sideX + (sampleY - sideY) * parentWidth] == wheatfieldBiomeId) {
                lateral++;
            }
        }
        return straight * 2 + lateral;
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
