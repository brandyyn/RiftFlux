package com.voidsrift.riftflux.wheatfield;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.offlawn.OffLawnContent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenTallGrass;
import net.minecraft.world.gen.feature.WorldGenerator;

import java.util.Random;

public class BiomeGenWheatfield extends BiomeGenBase {
    private static final int PASTURE_GRASS_COLOR = 13166666;
    private static final Height WHEATFIELD_HEIGHT = new Height(0.0009F, 0.00006F);
    private static final int EDGE_SAMPLE_RADIUS = 9;
    private static final float EDGE_BLEND_START = 0.08F;
    private static final float FULL_BARLEY_COVERAGE_START = 0.46F;
    private static final int BARLEY_NOISE_CELL = 4;
    private static final int BARLEY_COARSE_NOISE_CELL = 12;
    private static final int BARLEY_TENDRIL_NOISE_CELL = 24;
    private static final int EDGE_DITHER_DEPTH = 8;
    private static final int EDGE_BOUNDARY_SCAN_HALO = 0;
    private static final int EDGE_FRINGE_DEPTH = 6;
    private static final int EDGE_TENDRIL_MIN_LENGTH = 8;
    private static final int EDGE_TENDRIL_MAX_LENGTH = 24;
    private static final int EDGE_TENDRIL_BOUNDARY_RADIUS = 5;

    private final WorldGenerator barleyGen;
    private final BlockWheatfieldBarley barleyBlock;

    public BiomeGenWheatfield(int biomeId, BlockWheatfieldBarley barleyBlock) {
        super(biomeId);
        this.barleyBlock = barleyBlock;

        setColor(16176475);
        setBiomeName("Wheatfield");
        setTemperatureRainfall(0.8F, 0.4F);
        setHeight(WHEATFIELD_HEIGHT);

        theBiomeDecorator.treesPerChunk = 0;
        theBiomeDecorator.flowersPerChunk = -999;
        theBiomeDecorator.grassPerChunk = 0;
        theBiomeDecorator.reedsPerChunk = 0;
        theBiomeDecorator.deadBushPerChunk = 0;
        theBiomeDecorator.mushroomsPerChunk = 0;

        spawnableCreatureList.clear();
        barleyGen = new WorldGenTallGrass(barleyBlock, 0);
    }

    @Override
    public WorldGenerator getRandomWorldGenForGrass(Random random) {
        return barleyGen;
    }

    @Override
    public WorldGenAbstractTree func_150567_a(Random random) {
        return worldGeneratorBigTree;
    }

    @Override
    public void decorate(World world, Random random, int chunkX, int chunkZ) {
        super.decorate(world, random, chunkX, chunkZ);
    }

    public void populateBarleyForChunk(World world, int chunkX, int chunkZ) {
        if (world == null) {
            return;
        }
        populateBarleyArea(world, chunkX, chunkZ);
        populateBoundaryTendrils(world, chunkX, chunkZ);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getBiomeGrassColor(int x, int y, int z) {
        return PASTURE_GRASS_COLOR;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getBiomeFoliageColor(int x, int y, int z) {
        return PASTURE_GRASS_COLOR;
    }

    private void populateBarleyArea(World world, int chunkX, int chunkZ) {
        int configured = Math.max(0, ModConfig.wheatfieldBarleyPerChunk);
        if (configured <= 0) {
            return;
        }

        float configuredDensity = clamp01(configured / 999.0F);
        for (int x = chunkX; x < chunkX + 16; x++) {
            for (int z = chunkZ; z < chunkZ + 16; z++) {
                float wheatfieldInfluence = computeWheatfieldInfluence(world, x, z);
                if (wheatfieldInfluence <= EDGE_BLEND_START) {
                    continue;
                }

                float coverage = computeBarleyCoverage(world, x, z, wheatfieldInfluence, configuredDensity);
                if (coverage <= 0.0F) {
                    continue;
                }
                if (coverage < 0.999F && !shouldPlaceBarley(world.getSeed(), x, z, coverage)) {
                    continue;
                }

                int y = findSurfaceSoilY(world, x, z);
                if (y < 0) {
                    continue;
                }

                tryPlaceBarley(world, x, y + 1, z);
            }
        }

    }

    private int findSurfaceSoilY(World world, int x, int z) {
        int topY = Math.min(world.getHeightValue(x, z), world.getActualHeight() - 2);
        for (int y = topY; y >= Math.max(1, topY - 8); y--) {
            Block ground = world.getBlock(x, y, z);
            if ((ground == Blocks.grass || ground == Blocks.dirt) && world.isAirBlock(x, y + 1, z)) {
                return y;
            }
        }
        return -1;
    }

    private float computeWheatfieldInfluence(World world, int x, int z) {
        float totalWeight = 0.0F;
        float wheatfieldWeight = 0.0F;
        for (int dx = -EDGE_SAMPLE_RADIUS; dx <= EDGE_SAMPLE_RADIUS; dx++) {
            for (int dz = -EDGE_SAMPLE_RADIUS; dz <= EDGE_SAMPLE_RADIUS; dz++) {
                int distanceSq = dx * dx + dz * dz;
                if (distanceSq > EDGE_SAMPLE_RADIUS * EDGE_SAMPLE_RADIUS) {
                    continue;
                }

                float distance = (float) Math.sqrt(distanceSq);
                float weight = 1.0F - distance / ((float) EDGE_SAMPLE_RADIUS + 0.5F);
                if (weight <= 0.0F) {
                    continue;
                }

                totalWeight += weight;
                if (world.getBiomeGenForCoords(x + dx, z + dz) == this) {
                    wheatfieldWeight += weight;
                }
            }
        }

        if (totalWeight <= 0.0F) {
            return 0.0F;
        }
        return wheatfieldWeight / totalWeight;
    }

    private float computeBarleyCoverage(World world, int x, int z, float wheatfieldInfluence, float configuredDensity) {
        if (world.getBiomeGenForCoords(x, z) != this) {
            return 0.0F;
        }

        float clampedDensity = clamp01(configuredDensity);
        float coreCoverage = smoothstep(FULL_BARLEY_COVERAGE_START, 1.0F, wheatfieldInfluence) * clampedDensity;
        if (coreCoverage >= 0.999F) {
            coreCoverage = 1.0F;
        }

        float edgeCoverage = smoothstep(EDGE_BLEND_START, FULL_BARLEY_COVERAGE_START, wheatfieldInfluence);
        if (edgeCoverage <= 0.0F) {
            return 0.0F;
        }

        float tendrilMask = computeTendrilMask(world.getSeed(), x, z);
        float coverage = edgeCoverage * tendrilMask * clampedDensity;
        float borderProximity = computeInteriorBorderProximity(world, x, z);
        if (borderProximity > 0.0F) {
            float borderCap = lerp(1.0F, clamp01(0.48F + tendrilMask * 0.52F), borderProximity);
            coreCoverage = Math.min(coreCoverage, borderCap);
            coverage = Math.max(coverage, edgeCoverage * clamp01(0.24F + tendrilMask * (0.58F + borderProximity * 0.26F)));
        }
        return Math.max(coreCoverage, clamp01(coverage));
    }

    private float computeInteriorBorderProximity(World world, int x, int z) {
        if (world.getBiomeGenForCoords(x, z) != this) {
            return 0.0F;
        }

        float nearest = EDGE_DITHER_DEPTH + 1.0F;
        for (int dx = -EDGE_DITHER_DEPTH; dx <= EDGE_DITHER_DEPTH; dx++) {
            for (int dz = -EDGE_DITHER_DEPTH; dz <= EDGE_DITHER_DEPTH; dz++) {
                if (dx == 0 && dz == 0) {
                    continue;
                }
                int distanceSq = dx * dx + dz * dz;
                if (distanceSq > EDGE_DITHER_DEPTH * EDGE_DITHER_DEPTH) {
                    continue;
                }
                if (world.getBiomeGenForCoords(x + dx, z + dz) == this) {
                    continue;
                }

                float distance = (float) Math.sqrt(distanceSq);
                if (distance < nearest) {
                    nearest = distance;
                }
            }
        }

        if (nearest > EDGE_DITHER_DEPTH) {
            return 0.0F;
        }
        return clamp01(1.0F - nearest / (EDGE_DITHER_DEPTH + 0.5F));
    }

    private void tryPlaceBarley(World world, int x, int y, int z) {
        Block block = world.getBlock(x, y, z);
        if (OffLawnContent.sunflowerBush != null) {
            if (block == OffLawnContent.sunflowerBush
                    || world.getBlock(x, y - 1, z) == OffLawnContent.sunflowerBush
                    || world.getBlock(x, y + 1, z) == OffLawnContent.sunflowerBush) {
                return;
            }
        }

        if (!block.isReplaceable(world, x, y, z) && block != barleyBlock) {
            return;
        }

        if (barleyBlock.canBlockStay(world, x, y, z)) {
            world.setBlock(x, y, z, barleyBlock, 0, 2);
        }
    }

    private void populateBoundaryTendrils(World world, int chunkX, int chunkZ) {
        for (int x = chunkX - EDGE_BOUNDARY_SCAN_HALO; x < chunkX + 16 + EDGE_BOUNDARY_SCAN_HALO; x++) {
            for (int z = chunkZ - EDGE_BOUNDARY_SCAN_HALO; z < chunkZ + 16 + EDGE_BOUNDARY_SCAN_HALO; z++) {
                if (!isImmediateBoundaryCell(world, x, z)) {
                    continue;
                }
                double[] direction = resolveOutwardBoundaryDirection(world, x, z);
                if (direction == null) {
                    continue;
                }

                long seed = world.getSeed() ^ (long) x * 341873128712L ^ (long) z * 132897987541L ^ 0x7A4C2D91B8F3L;
                placeBoundaryFringe(world, x, z, direction[0], direction[1], seed);

                if (((x ^ z) & 1) != 0) {
                    continue;
                }

                float ribbon = computeRidge(sampleBarleyNoise(seed ^ 0x6C8E9CF5L, x + (int)(direction[0] * 23.0D), z + (int)(direction[1] * 23.0D), 18));
                float anchorNoise = sampleBarleyNoise(seed ^ 0x2B992DDFA232L, x - z, z + x, 7);
                if (ribbon * 0.72F + anchorNoise * 0.28F < 0.42F) {
                    continue;
                }

                int length = EDGE_TENDRIL_MIN_LENGTH + Math.round(sampleBarleyNoise(seed ^ 0x55AA55AAL, x, z, 9)
                        * (EDGE_TENDRIL_MAX_LENGTH - EDGE_TENDRIL_MIN_LENGTH));
                emitBoundaryTendril(world, x, z, direction[0], direction[1], length, seed);
            }
        }
    }

    private boolean isImmediateBoundaryCell(World world, int x, int z) {
        if (world.getBiomeGenForCoords(x, z) != this) {
            return false;
        }

        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                if (dx == 0 && dz == 0) {
                    continue;
                }
                if (world.getBiomeGenForCoords(x + dx, z + dz) != this) {
                    return true;
                }
            }
        }
        return false;
    }

    private double[] resolveOutwardBoundaryDirection(World world, int x, int z) {
        if (world.getBiomeGenForCoords(x, z) != this) {
            return null;
        }

        double sumX = 0.0D;
        double sumZ = 0.0D;
        for (int dx = -EDGE_TENDRIL_BOUNDARY_RADIUS; dx <= EDGE_TENDRIL_BOUNDARY_RADIUS; dx++) {
            for (int dz = -EDGE_TENDRIL_BOUNDARY_RADIUS; dz <= EDGE_TENDRIL_BOUNDARY_RADIUS; dz++) {
                if (dx == 0 && dz == 0) {
                    continue;
                }
                int distanceSq = dx * dx + dz * dz;
                if (distanceSq > EDGE_TENDRIL_BOUNDARY_RADIUS * EDGE_TENDRIL_BOUNDARY_RADIUS) {
                    continue;
                }
                if (world.getBiomeGenForCoords(x + dx, z + dz) == this) {
                    continue;
                }

                double distance = Math.sqrt(distanceSq);
                double weight = 1.0D / (distance + 0.35D);
                sumX += dx * weight;
                sumZ += dz * weight;
            }
        }

        double length = Math.sqrt(sumX * sumX + sumZ * sumZ);
        if (length < 0.001D) {
            return null;
        }
        return new double[] { sumX / length, sumZ / length };
    }

    private void placeBoundaryFringe(World world, int anchorX, int anchorZ, double dirX, double dirZ, long seed) {
        double sideX = -dirZ;
        double sideZ = dirX;

        for (int step = 0; step <= EDGE_FRINGE_DEPTH; step++) {
            float progress = step / (float) EDGE_FRINGE_DEPTH;
            float wobble = sampleBarleyNoise(seed ^ 0x25A01E75L, anchorX + step * 5, anchorZ - step * 3, 4) - 0.5F;
            double lateral = wobble * (1.0D + progress * 2.25D);
            int centerX = MathHelper.floor_double(anchorX + dirX * (step + 1) + sideX * lateral + 0.5D);
            int centerZ = MathHelper.floor_double(anchorZ + dirZ * (step + 1) + sideZ * lateral + 0.5D);
            float radius = 1.05F + (1.0F - progress) * 1.65F;
            int blockRadius = Math.max(1, (int)Math.ceil(radius));

            for (int dx = -blockRadius; dx <= blockRadius; dx++) {
                for (int dz = -blockRadius; dz <= blockRadius; dz++) {
                    float distance = (float)Math.sqrt(dx * dx + dz * dz);
                    if (distance > radius) {
                        continue;
                    }

                    int x = centerX + dx;
                    int z = centerZ + dz;
                    float body = clamp01(1.0F - distance / (radius + 0.35F));
                    float dither = sampleBarleyNoise(seed ^ 0x6D2B79F5L, x * 2 - z, z * 2 + x, 3);
                    float ragged = sampleBarleyNoise(seed ^ 0x1234ABCDL, x + 19, z - 31, 7);
                    float density = body * (0.78F - progress * 0.34F) + ragged * 0.14F;
                    if (dither <= density) {
                        placeBarleyAtSurface(world, x, z);
                    }
                }
            }
        }
    }

    private void emitBoundaryTendril(World world, int anchorX, int anchorZ, double dirX, double dirZ, int length, long seed) {
        double sideX = -dirZ;
        double sideZ = dirX;
        double drift = 0.0D;

        for (int step = 1; step <= length; step++) {
            float progress = step / (float) length;
            float driftNoise = sampleBarleyNoise(seed ^ 0x3AD8025FL, anchorX + step * 3, anchorZ - step * 2, 5) - 0.5F;
            drift += driftNoise * (0.55D + progress * 1.45D);

            int centerX = MathHelper.floor_double(anchorX + dirX * step + sideX * drift + 0.5D);
            int centerZ = MathHelper.floor_double(anchorZ + dirZ * step + sideZ * drift + 0.5D);
            placeBoundaryTendrilCluster(world, centerX, centerZ, progress, seed ^ step * 341873128712L);

            if (progress > 0.22F && progress < 0.78F) {
                float branchNoise = sampleBarleyNoise(seed ^ 0x41C64E6DL, centerX + step, centerZ - step, 6);
                if (branchNoise > 0.82F) {
                    int branchSide = branchNoise > 0.91F ? 1 : -1;
                    int branchX = MathHelper.floor_double(centerX + sideX * branchSide * (2.0D + branchNoise * 4.0D) + 0.5D);
                    int branchZ = MathHelper.floor_double(centerZ + sideZ * branchSide * (2.0D + branchNoise * 4.0D) + 0.5D);
                    placeBoundaryTendrilCluster(world, branchX, branchZ, progress + 0.08F, seed ^ 0x1B56C4E9L ^ step);
                }
            }
        }
    }

    private void placeBoundaryTendrilCluster(World world, int centerX, int centerZ, float progress, long seed) {
        float taper = clamp01(1.0F - progress);
        float radius = 0.75F + taper * 2.65F;
        int blockRadius = Math.max(1, (int)Math.ceil(radius));

        for (int dx = -blockRadius; dx <= blockRadius; dx++) {
            for (int dz = -blockRadius; dz <= blockRadius; dz++) {
                float distance = (float)Math.sqrt(dx * dx + dz * dz);
                if (distance > radius) {
                    continue;
                }

                int x = centerX + dx;
                int z = centerZ + dz;
                float body = clamp01(1.0F - distance / (radius + 0.35F));
                float dither = sampleBarleyNoise(seed ^ 0x0F1E2D3CL, x * 2 - z, z * 2 + x, 3);
                float raggedEdge = sampleBarleyNoise(seed ^ 0xA54FF53AL, x + 41, z - 29, 9);
                float density = body * (0.95F - progress * 0.62F) + raggedEdge * 0.16F;
                if (dither <= density) {
                    placeBarleyAtSurface(world, x, z);
                }
            }
        }
    }

    private void placeBarleyAtSurface(World world, int x, int z) {
        int y = findSurfaceSoilY(world, x, z);
        if (y < 0) {
            return;
        }
        tryPlaceBarley(world, x, y + 1, z);
    }

    private static boolean shouldPlaceBarley(long worldSeed, int x, int z, float density) {
        float fineNoise = sampleBarleyNoise(worldSeed, x, z, BARLEY_NOISE_CELL);
        float coarseNoise = sampleBarleyNoise(worldSeed ^ 0x5F3759DFL, x, z, BARLEY_COARSE_NOISE_CELL);
        float blendedNoise = fineNoise * 0.72F + coarseNoise * 0.28F;
        return blendedNoise < density;
    }

    private static float computeTendrilMask(long worldSeed, int x, int z) {
        float ribbonA = computeRidge(sampleBarleyNoise(worldSeed ^ 0x6A09E667L, x * 2 + z, z - x, BARLEY_TENDRIL_NOISE_CELL));
        float ribbonB = computeRidge(sampleBarleyNoise(worldSeed ^ 0xBB67AE85L, x - z * 2, x + z * 2, BARLEY_TENDRIL_NOISE_CELL + 10));
        float ribbonC = computeRidge(sampleBarleyNoise(worldSeed ^ 0x3C6EF372L, x * 3 - z, z * 2 + x, BARLEY_TENDRIL_NOISE_CELL - 6));
        float dominantRibbon = Math.max(ribbonA, Math.max(ribbonB * 0.93F, ribbonC * 0.78F));
        float weave = sampleBarleyNoise(worldSeed ^ 0xA54FF53AL, x + 41, z - 29, BARLEY_COARSE_NOISE_CELL + 4);
        float body = sampleBarleyNoise(worldSeed ^ 0x510E527FL, x, z, BARLEY_COARSE_NOISE_CELL + 10);
        float breakup = sampleBarleyNoise(worldSeed ^ 0x9B05688CL, x * 2 - z, z * 2 - x, BARLEY_NOISE_CELL + 2);
        float mask = dominantRibbon * (0.82F + weave * 0.26F) + body * 0.18F - 0.12F + breakup * 0.08F;
        mask = clamp01((mask - 0.12F) / 0.78F);
        return mask * mask * (0.88F + weave * 0.12F);
    }

    private static float computeRidge(float noise) {
        float ridge = 1.0F - Math.abs(noise * 2.0F - 1.0F);
        return clamp01((ridge - 0.16F) / 0.84F);
    }

    private static float sampleBarleyNoise(long worldSeed, int x, int z, int cellSize) {
        int cellX = floorDiv(x, cellSize);
        int cellZ = floorDiv(z, cellSize);
        float fracX = (float) positiveMod(x, cellSize) / (float) cellSize;
        float fracZ = (float) positiveMod(z, cellSize) / (float) cellSize;

        float n00 = sampleNoiseCorner(worldSeed, cellX, cellZ);
        float n10 = sampleNoiseCorner(worldSeed, cellX + 1, cellZ);
        float n01 = sampleNoiseCorner(worldSeed, cellX, cellZ + 1);
        float n11 = sampleNoiseCorner(worldSeed, cellX + 1, cellZ + 1);

        float smoothX = fracX * fracX * (3.0F - 2.0F * fracX);
        float smoothZ = fracZ * fracZ * (3.0F - 2.0F * fracZ);
        float nx0 = lerp(n00, n10, smoothX);
        float nx1 = lerp(n01, n11, smoothX);
        return lerp(nx0, nx1, smoothZ);
    }

    private static float sampleNoiseCorner(long worldSeed, int cellX, int cellZ) {
        long seed = worldSeed;
        seed ^= (long) cellX * 341873128712L;
        seed ^= (long) cellZ * 132897987541L;
        seed ^= (seed >>> 13);
        seed *= 0x5DEECE66DL;
        seed ^= (seed >>> 17);
        return (float) ((seed & 0xFFFFFFL) / (double) 0x1000000L);
    }

    private static float lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }

    private static float smoothstep(float edge0, float edge1, float value) {
        if (edge1 <= edge0) {
            return value >= edge1 ? 1.0F : 0.0F;
        }
        float t = clamp01((value - edge0) / (edge1 - edge0));
        if (t <= 0.0F || t >= 1.0F) {
            return t;
        }
        return t * t * (3.0F - 2.0F * t);
    }

    private static float clamp01(float value) {
        if (value < 0.0F) {
            return 0.0F;
        }
        if (value > 1.0F) {
            return 1.0F;
        }
        return value;
    }

    private static int floorDiv(int value, int divisor) {
        int result = value / divisor;
        if ((value ^ divisor) < 0 && result * divisor != value) {
            result--;
        }
        return result;
    }

    private static int positiveMod(int value, int divisor) {
        int mod = value % divisor;
        return mod < 0 ? mod + divisor : mod;
    }
}
