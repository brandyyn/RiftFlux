package com.voidsrift.riftflux.wheatfield.world;

import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;

public final class GenLayerWheatfieldRoundStacked extends GenLayer {
    private static final int PASS_COUNT = 7;
    private static final long FIRST_PASS_SEED = 6100L;
    private static final int DENSITY_RADIUS = 5;
    private static final int CARDINAL_ARM_LENGTH = 5;
    private static final int DIAGONAL_ARM_LENGTH = 4;
    private static final int PASS_INFLUENCE_RADIUS = Math.max(DENSITY_RADIUS, CARDINAL_ARM_LENGTH);
    private static final float EDGE_NOISE_RANGE = 0.08F;
    private static final int[] DIR_X = new int[] { 1, -1, 0, 0, 1, -1, 1, -1 };
    private static final int[] DIR_Y = new int[] { 0, 0, 1, -1, 1, -1, -1, 1 };

    private final int wheatfieldBiomeId;
    private final PassRandom[] passRandoms;

    public GenLayerWheatfieldRoundStacked(GenLayer parent, int wheatfieldBiomeId) {
        super(FIRST_PASS_SEED + PASS_COUNT - 1L);
        this.parent = parent;
        this.wheatfieldBiomeId = wheatfieldBiomeId;
        this.passRandoms = new PassRandom[PASS_COUNT];

        for (int i = 0; i < PASS_COUNT; i++) {
            this.passRandoms[i] = new PassRandom(FIRST_PASS_SEED + i);
        }
    }

    @Override
    public void initWorldGenSeed(long seed) {
        super.initWorldGenSeed(seed);

        for (PassRandom passRandom : this.passRandoms) {
            passRandom.initWorldGenSeed(seed);
        }
    }

    @Override
    public int[] getInts(int areaX, int areaY, int areaWidth, int areaHeight) {
        int totalBorder = DENSITY_RADIUS * PASS_COUNT;
        int currentX = areaX - totalBorder;
        int currentY = areaY - totalBorder;
        int currentWidth = areaWidth + totalBorder * 2;
        int currentHeight = areaHeight + totalBorder * 2;
        int[] current = parent.getInts(currentX, currentY, currentWidth, currentHeight);
        WheatfieldBounds bounds = findWheatfieldBounds(current, currentWidth, currentHeight);
        if (bounds == null) {
            int[] out = IntCache.getIntCache(areaWidth * areaHeight);
            copyCenter(current, currentWidth, out, areaWidth, areaHeight, totalBorder);
            return out;
        }

        int remainingPasses = PASS_COUNT;
        for (PassRandom passRandom : this.passRandoms) {
            PassResult passResult = applyPass(passRandom, current, currentX, currentY, currentWidth, currentHeight, bounds);
            current = passResult.data;
            bounds = passResult.bounds;
            currentX += DENSITY_RADIUS;
            currentY += DENSITY_RADIUS;
            currentWidth -= DENSITY_RADIUS * 2;
            currentHeight -= DENSITY_RADIUS * 2;
            remainingPasses--;

            if (remainingPasses > 0 && bounds == null) {
                int[] out = IntCache.getIntCache(areaWidth * areaHeight);
                copyCenter(current, currentWidth, out, areaWidth, areaHeight, DENSITY_RADIUS * remainingPasses);
                return out;
            }
        }

        return current;
    }

    private PassResult applyPass(
            PassRandom passRandom,
            int[] parentInts,
            int inputX,
            int inputY,
            int inputWidth,
            int inputHeight,
            WheatfieldBounds bounds) {
        int border = DENSITY_RADIUS < 1 ? 1 : DENSITY_RADIUS;
        int areaX = inputX + border;
        int areaY = inputY + border;
        int areaWidth = inputWidth - border * 2;
        int areaHeight = inputHeight - border * 2;
        int densityWindow = DENSITY_RADIUS * 2 + 1;
        int[] out = IntCache.getIntCache(areaWidth * areaHeight);
        int processMinX = Math.max(0, bounds.minX - PASS_INFLUENCE_RADIUS - border);
        int processMaxX = Math.min(areaWidth - 1, bounds.maxX + PASS_INFLUENCE_RADIUS - border);
        int processMinY = Math.max(0, bounds.minY - PASS_INFLUENCE_RADIUS - border);
        int processMaxY = Math.min(areaHeight - 1, bounds.maxY + PASS_INFLUENCE_RADIUS - border);
        if (processMinX > processMaxX || processMinY > processMaxY) {
            copyCenter(parentInts, inputWidth, out, areaWidth, areaHeight, border);
            return new PassResult(out, null);
        }

        int processColumnMin = processMinX;
        int processColumnMaxExclusive = processMaxX + densityWindow;
        int[] directionalSupport = new int[DIR_X.length];
        int[] neighborhoodBiomeIds = new int[9];
        int[] neighborhoodCounts = new int[9];
        int[] wheatfieldColumnCounts = new int[inputWidth];
        int[] landColumnCounts = new int[inputWidth];

        initDensityColumns(
                parentInts,
                inputWidth,
                wheatfieldColumnCounts,
                landColumnCounts,
                densityWindow,
                processColumnMin,
                processColumnMaxExclusive);

        int nextMinX = areaWidth;
        int nextMinY = areaHeight;
        int nextMaxX = -1;
        int nextMaxY = -1;
        for (int localY = 0; localY < areaHeight; localY++) {
            if (localY > 0) {
                updateDensityColumns(
                        parentInts,
                        inputWidth,
                        wheatfieldColumnCounts,
                        landColumnCounts,
                        localY - 1,
                        localY + densityWindow - 1,
                        processColumnMin,
                        processColumnMaxExclusive);
            }

            int srcIndex = (localY + border) * inputWidth + border;
            int dstIndex = localY * areaWidth;
            System.arraycopy(parentInts, srcIndex, out, dstIndex, areaWidth);

            if (localY < processMinY || localY > processMaxY) {
                for (int localX = processMinX; localX <= processMaxX; localX++) {
                    if (out[dstIndex + localX] == wheatfieldBiomeId) {
                        if (localX < nextMinX) {
                            nextMinX = localX;
                        }
                        if (localX > nextMaxX) {
                            nextMaxX = localX;
                        }
                        if (localY < nextMinY) {
                            nextMinY = localY;
                        }
                        if (localY > nextMaxY) {
                            nextMaxY = localY;
                        }
                    }
                }
                continue;
            }

            int wheatfieldNear = 0;
            int landNear = 0;
            for (int sampleX = processMinX; sampleX < processMinX + densityWindow; sampleX++) {
                wheatfieldNear += wheatfieldColumnCounts[sampleX];
                landNear += landColumnCounts[sampleX];
            }

            for (int localX = processMinX; localX <= processMaxX; localX++) {
                int centerX = localX + border;
                int centerY = localY + border;
                int center = parentInts[centerX + centerY * inputWidth];

                if (GenLayer.isBiomeOceanic(center) || WheatfieldTerrainUtil.isRiverBiome(center)) {
                    continue;
                }

                int wheatfieldCardinal = 0;
                int wheatfieldDiagonal = 0;
                int dominantNonWheatfield = center;
                int dominantNonWheatfieldCount = 0;
                int strongestArm = 0;
                int armCount = 0;
                int totalDirectionalSupport = 0;
                int neighborhoodBiomeCount = 0;

                for (int dir = 0; dir < DIR_X.length; dir++) {
                    directionalSupport[dir] = countDirectionalSupport(parentInts, inputWidth, centerX, centerY, DIR_X[dir], DIR_Y[dir]);
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
                        int sample = parentInts[centerX + offsetX + (centerY + offsetY) * inputWidth];
                        if (offsetX == 0 && offsetY == 0) {
                            if (sample != wheatfieldBiomeId && isLand(sample)) {
                                neighborhoodBiomeCount = addNeighborhoodBiome(
                                        neighborhoodBiomeIds,
                                        neighborhoodCounts,
                                        neighborhoodBiomeCount,
                                        sample);
                            }
                            continue;
                        }

                        if (sample == wheatfieldBiomeId) {
                            if (offsetX == 0 || offsetY == 0) {
                                wheatfieldCardinal++;
                            } else {
                                wheatfieldDiagonal++;
                            }
                        } else if (isLand(sample)) {
                            neighborhoodBiomeCount = addNeighborhoodBiome(
                                    neighborhoodBiomeIds,
                                    neighborhoodCounts,
                                    neighborhoodBiomeCount,
                                    sample);
                        }
                    }
                }

                for (int i = 0; i < neighborhoodBiomeCount; i++) {
                    int sampleCount = neighborhoodCounts[i];
                    if (sampleCount > dominantNonWheatfieldCount) {
                        dominantNonWheatfieldCount = sampleCount;
                        dominantNonWheatfield = neighborhoodBiomeIds[i];
                    }
                    neighborhoodCounts[i] = 0;
                }
                for (int i = 0; i < neighborhoodBiomeCount; i++) {
                    neighborhoodBiomeIds[i] = 0;
                }

                if (dominantNonWheatfieldCount <= 0 && center != wheatfieldBiomeId && isLand(center)) {
                    dominantNonWheatfield = center;
                }

                passRandom.initChunkSeed(areaX + localX, areaY + localY);
                float localWheatfieldDensity = landNear <= 0 ? 0.0F : (float) wheatfieldNear / (float) landNear;
                float edgeNoise = ((float) passRandom.nextIntBounded(1000) / 999.0F - 0.5F) * EDGE_NOISE_RANGE;

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
                            && passRandom.nextIntBounded(strongestArm >= 9 ? 2 : 3) == 0;
                    boolean growBridge = strongestPair >= 10
                            && localWheatfieldDensity >= 0.18F
                            && passRandom.nextIntBounded(4) != 0;
                    boolean growBranch = armCount >= 2
                            && localWheatfieldDensity >= 0.16F
                            && passRandom.nextIntBounded(5) == 0;
                    boolean expand = growCore || growTendril || growBridge || growBranch;
                    out[localX + localY * areaWidth] = expand ? wheatfieldBiomeId : center;
                }

                if (localX < processMaxX) {
                    wheatfieldNear += wheatfieldColumnCounts[localX + densityWindow] - wheatfieldColumnCounts[localX];
                    landNear += landColumnCounts[localX + densityWindow] - landColumnCounts[localX];
                }
            }

            for (int localX = processMinX; localX <= processMaxX; localX++) {
                if (out[dstIndex + localX] != wheatfieldBiomeId) {
                    continue;
                }

                if (localX < nextMinX) {
                    nextMinX = localX;
                }
                if (localX > nextMaxX) {
                    nextMaxX = localX;
                }
                if (localY < nextMinY) {
                    nextMinY = localY;
                }
                if (localY > nextMaxY) {
                    nextMaxY = localY;
                }
            }
        }

        WheatfieldBounds nextBounds = nextMaxX >= nextMinX && nextMaxY >= nextMinY
                ? new WheatfieldBounds(nextMinX + border, nextMinY + border, nextMaxX + border, nextMaxY + border)
                : null;
        return new PassResult(out, nextBounds);
    }

    private static void copyCenter(int[] parentInts, int parentWidth, int[] out, int areaWidth, int areaHeight, int border) {
        for (int localY = 0; localY < areaHeight; localY++) {
            int srcIndex = (localY + border) * parentWidth + border;
            int dstIndex = localY * areaWidth;
            System.arraycopy(parentInts, srcIndex, out, dstIndex, areaWidth);
        }
    }

    private WheatfieldBounds findWheatfieldBounds(int[] data, int width, int height) {
        int minX = width;
        int minY = height;
        int maxX = -1;
        int maxY = -1;

        for (int y = 0; y < height; y++) {
            int rowIndex = y * width;
            for (int x = 0; x < width; x++) {
                if (data[rowIndex + x] != wheatfieldBiomeId) {
                    continue;
                }

                if (x < minX) {
                    minX = x;
                }
                if (x > maxX) {
                    maxX = x;
                }
                if (y < minY) {
                    minY = y;
                }
                if (y > maxY) {
                    maxY = y;
                }
            }
        }

        if (maxX < minX || maxY < minY) {
            return null;
        }

        return new WheatfieldBounds(minX, minY, maxX, maxY);
    }

    private int countDirectionalSupport(int[] parentInts, int parentWidth, int centerX, int centerY, int dirX, int dirY) {
        int maxSteps = dirX == 0 || dirY == 0 ? CARDINAL_ARM_LENGTH : DIAGONAL_ARM_LENGTH;
        int straight = 0;
        int lateral = 0;

        for (int step = 1; step <= maxSteps; step++) {
            int sampleX = centerX + dirX * step;
            int sampleY = centerY + dirY * step;
            if (parentInts[sampleX + sampleY * parentWidth] != wheatfieldBiomeId) {
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

    private void initDensityColumns(
            int[] parentInts,
            int parentWidth,
            int[] wheatfieldColumnCounts,
            int[] landColumnCounts,
            int densityWindow,
            int columnMin,
            int columnMaxExclusive) {
        for (int x = columnMin; x < columnMaxExclusive; x++) {
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
            int enteringRow,
            int columnMin,
            int columnMaxExclusive) {
        int leavingBase = leavingRow * parentWidth;
        int enteringBase = enteringRow * parentWidth;

        for (int x = columnMin; x < columnMaxExclusive; x++) {
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

    private static int addNeighborhoodBiome(int[] biomeIds, int[] counts, int size, int biomeId) {
        for (int i = 0; i < size; i++) {
            if (biomeIds[i] == biomeId) {
                counts[i]++;
                return size;
            }
        }

        biomeIds[size] = biomeId;
        counts[size] = 1;
        return size + 1;
    }

    private static final class PassRandom extends GenLayer {
        private PassRandom(long seed) {
            super(seed);
        }

        private int nextIntBounded(int bound) {
            return super.nextInt(bound);
        }

        @Override
        public int[] getInts(int areaX, int areaY, int areaWidth, int areaHeight) {
            throw new UnsupportedOperationException("PassRandom is not a standalone gen layer");
        }
    }

    private static final class WheatfieldBounds {
        private final int minX;
        private final int minY;
        private final int maxX;
        private final int maxY;

        private WheatfieldBounds(int minX, int minY, int maxX, int maxY) {
            this.minX = minX;
            this.minY = minY;
            this.maxX = maxX;
            this.maxY = maxY;
        }
    }

    private static final class PassResult {
        private final int[] data;
        private final WheatfieldBounds bounds;

        private PassResult(int[] data, WheatfieldBounds bounds) {
            this.data = data;
            this.bounds = bounds;
        }
    }
}
