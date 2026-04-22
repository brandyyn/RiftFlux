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
        int densityWindow = DENSITY_RADIUS * 2 + 1;
        int parentX = areaX - border;
        int parentY = areaY - border;
        int parentWidth = areaWidth + border * 2;
        int parentHeight = areaHeight + border * 2;
        int[] parentInts = parent.getInts(parentX, parentY, parentWidth, parentHeight);
        int[] out = IntCache.getIntCache(areaWidth * areaHeight);
        if (!containsWheatfield(parentInts)) {
            copyCenter(parentInts, parentWidth, out, areaWidth, areaHeight, border);
            return out;
        }

        int[] directionalSupport = new int[DIR_X.length];
        int[] wheatfieldColumnCounts = new int[parentWidth];
        int[] landColumnCounts = new int[parentWidth];

        initDensityColumns(parentInts, parentWidth, wheatfieldColumnCounts, landColumnCounts, densityWindow);

        for (int localY = 0; localY < areaHeight; localY++) {
            if (localY > 0) {
                updateDensityColumns(
                        parentInts,
                        parentWidth,
                        wheatfieldColumnCounts,
                        landColumnCounts,
                        localY - 1,
                        localY + densityWindow - 1);
            }

            int wheatfieldNear = 0;
            int landNear = 0;
            for (int sampleX = 0; sampleX < densityWindow; sampleX++) {
                wheatfieldNear += wheatfieldColumnCounts[sampleX];
                landNear += landColumnCounts[sampleX];
            }

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
                        } else if (isLand(sample)) {
                            int sampleCount = countBiomeInNeighborhood(parentInts, parentWidth, centerX, centerY, sample);
                            if (sampleCount > dominantNonWheatfieldCount) {
                                dominantNonWheatfieldCount = sampleCount;
                                dominantNonWheatfield = sample;
                            }
                        }
                    }
                }

                this.initChunkSeed(areaX + localX, areaY + localY);
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

                if (localX < areaWidth - 1) {
                    wheatfieldNear += wheatfieldColumnCounts[localX + densityWindow] - wheatfieldColumnCounts[localX];
                    landNear += landColumnCounts[localX + densityWindow] - landColumnCounts[localX];
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

    private void initDensityColumns(int[] parentInts, int parentWidth, int[] wheatfieldColumnCounts, int[] landColumnCounts, int densityWindow) {
        for (int x = 0; x < parentWidth; x++) {
            int wheatfieldCount = 0;
            int landCount = 0;
            int index = x;
            for (int sampleY = 0; sampleY < densityWindow; sampleY++) {
                int sample = parentInts[index];
                if (sample == wheatfieldBiomeId) {
                    wheatfieldCount++;
                }
                if (isLand(sample)) {
                    landCount++;
                }
                index += parentWidth;
            }

            wheatfieldColumnCounts[x] = wheatfieldCount;
            landColumnCounts[x] = landCount;
        }
    }

    private void updateDensityColumns(
            int[] parentInts,
            int parentWidth,
            int[] wheatfieldColumnCounts,
            int[] landColumnCounts,
            int leavingRow,
            int enteringRow) {
        int leavingBase = leavingRow * parentWidth;
        int enteringBase = enteringRow * parentWidth;

        for (int x = 0; x < parentWidth; x++) {
            int leavingSample = parentInts[leavingBase + x];
            if (leavingSample == wheatfieldBiomeId) {
                wheatfieldColumnCounts[x]--;
            }
            if (isLand(leavingSample)) {
                landColumnCounts[x]--;
            }

            int enteringSample = parentInts[enteringBase + x];
            if (enteringSample == wheatfieldBiomeId) {
                wheatfieldColumnCounts[x]++;
            }
            if (isLand(enteringSample)) {
                landColumnCounts[x]++;
            }
        }
    }

    private static boolean isLand(int biomeId) {
        return !GenLayer.isBiomeOceanic(biomeId) && !WheatfieldTerrainUtil.isRiverBiome(biomeId);
    }

    private boolean containsWheatfield(int[] parentInts) {
        for (int biomeId : parentInts) {
            if (biomeId == wheatfieldBiomeId) {
                return true;
            }
        }
        return false;
    }

    private static void copyCenter(int[] parentInts, int parentWidth, int[] out, int areaWidth, int areaHeight, int border) {
        for (int localY = 0; localY < areaHeight; localY++) {
            int srcIndex = (localY + border) * parentWidth + border;
            int dstIndex = localY * areaWidth;
            System.arraycopy(parentInts, srcIndex, out, dstIndex, areaWidth);
        }
    }
}
