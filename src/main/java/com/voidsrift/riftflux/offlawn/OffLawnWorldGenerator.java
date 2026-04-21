package com.voidsrift.riftflux.offlawn;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.util.ConfigResolver;
import com.voidsrift.riftflux.wheatfield.WheatfieldContent;
import cpw.mods.fml.common.IWorldGenerator;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.common.BiomeDictionary;

import java.util.HashSet;
import java.util.Locale;
import java.util.Random;
import java.util.Set;

public class OffLawnWorldGenerator implements IWorldGenerator {
    private static final int CHUNK_SIZE = 16;
    private static final int PATCH_CELL_CHUNKS = 32;
    private static final int PATCH_CELL_BLOCKS = PATCH_CELL_CHUNKS * CHUNK_SIZE;
    private static final int MIN_GROUPED_PATCH_PLACEMENTS = 24;
    private static final int MAX_GROUPED_PATCH_PLACEMENTS = 44;
    private static final float GOLDEN_ANGLE = 2.3999631F;
    private static final long NORMAL_PATCH_SALT = 0x13579BDFL;
    private static final long BRIGHT_PATCH_SALT = 0x2468ACE1L;
    private static final long MIXED_PATCH_SALT = 0x10293847L;

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
        if (world == null || world.provider == null || world.provider.dimensionId != 0) {
            return;
        }
        if (!OffLawnContent.isEnabled() || !OffLawnContent.hasSunflowerBushes()) {
            return;
        }

        generateConfiguredPatches(
                world,
                chunkX,
                chunkZ,
                new PatchSettings(
                        ModConfig.offLawnEnableSunflowerWorldgen,
                        ModConfig.offLawnSunflowerPatchChance,
                        ModConfig.offLawnSunflowerAttemptsPerChunk,
                        ModConfig.offLawnSunflowerPatchRadiusBlocks,
                        ModConfig.offLawnSunflowerBiomeTypes,
                        ModConfig.offLawnSunflowerBiomeList
                ),
                PatchMode.NORMAL,
                NORMAL_PATCH_SALT
        );
        generateConfiguredPatches(
                world,
                chunkX,
                chunkZ,
                new PatchSettings(
                        ModConfig.offLawnEnableBrightSunflowerWorldgen,
                        ModConfig.offLawnBrightSunflowerPatchChance,
                        ModConfig.offLawnBrightSunflowerAttemptsPerChunk,
                        ModConfig.offLawnBrightSunflowerPatchRadiusBlocks,
                        ModConfig.offLawnBrightSunflowerBiomeTypes,
                        ModConfig.offLawnBrightSunflowerBiomeList
                ),
                PatchMode.BRIGHT,
                BRIGHT_PATCH_SALT
        );
        generateConfiguredPatches(
                world,
                chunkX,
                chunkZ,
                new PatchSettings(
                        ModConfig.offLawnEnableMixedSunflowerWorldgen,
                        ModConfig.offLawnMixedSunflowerPatchChance,
                        ModConfig.offLawnMixedSunflowerAttemptsPerChunk,
                        ModConfig.offLawnMixedSunflowerPatchRadiusBlocks,
                        ModConfig.offLawnMixedSunflowerBiomeTypes,
                        ModConfig.offLawnMixedSunflowerBiomeList
                ),
                PatchMode.MIXED,
                MIXED_PATCH_SALT
        );
    }

    private static void generateConfiguredPatches(World world, int chunkX, int chunkZ, PatchSettings settings, PatchMode mode, long seedSalt) {
        if (settings == null || !settings.enabled || !hasPatchBlock(mode)) {
            return;
        }

        int configuredPlacements = Math.max(0, settings.attemptsPerPatch);
        if (configuredPlacements <= 0) {
            return;
        }

        int maxRadius = Math.max(16, settings.patchRadiusBlocks) + Math.max(6, settings.patchRadiusBlocks / 3);
        int cellRadius = Math.max(1, (maxRadius + PATCH_CELL_BLOCKS - 1) / PATCH_CELL_BLOCKS + 1);
        int cellX = floorDiv(chunkX, PATCH_CELL_CHUNKS);
        int cellZ = floorDiv(chunkZ, PATCH_CELL_CHUNKS);
        for (int sx = cellX - cellRadius; sx <= cellX + cellRadius; sx++) {
            for (int sz = cellZ - cellRadius; sz <= cellZ + cellRadius; sz++) {
                generatePatchSlice(world, chunkX, chunkZ, sx, sz, settings, mode, seedSalt);
            }
        }
    }

    private static void generatePatchSlice(World world, int chunkX, int chunkZ, int seedCellX, int seedCellZ, PatchSettings settings, PatchMode mode, long seedSalt) {
        Random patchRandom = new Random(mixSeed(world.getSeed() ^ seedSalt, seedCellX, seedCellZ));
        int configuredOneInN = Math.max(1, settings.patchChance);
        if (configuredOneInN > 1 && patchRandom.nextInt(configuredOneInN) != 0) {
            return;
        }

        int patchCenterX = seedCellX * PATCH_CELL_BLOCKS + patchRandom.nextInt(PATCH_CELL_BLOCKS);
        int patchCenterZ = seedCellZ * PATCH_CELL_BLOCKS + patchRandom.nextInt(PATCH_CELL_BLOCKS);
        if (!isTargetBiome(world.getBiomeGenForCoords(patchCenterX, patchCenterZ), settings)) {
            return;
        }

        int baseRadius = Math.max(16, settings.patchRadiusBlocks);
        int radiusJitter = Math.max(4, baseRadius / 3);
        int patchRadius = clamp(baseRadius + patchRandom.nextInt(radiusJitter + 1) - radiusJitter / 2, 16, 96);
        if (!patchOverlapsChunk(patchCenterX, patchCenterZ, patchRadius, chunkX, chunkZ)) {
            return;
        }

        int targetPlacements = clamp(settings.attemptsPerPatch, MIN_GROUPED_PATCH_PLACEMENTS, MAX_GROUPED_PATCH_PLACEMENTS);
        int maxCandidates = Math.min(640, targetPlacements * 16);
        float phase = patchRandom.nextFloat() * (float) Math.PI * 2.0F;
        double spiralTurns = 2.75D + patchRandom.nextDouble() * 1.25D;
        double armWidth = Math.max(3.5D, patchRadius * 0.16D);
        double radialJitterScale = Math.max(1.5D, patchRadius * 0.04D);
        Set<Long> triedPositions = new HashSet<Long>();
        int placed = 0;
        for (int i = 0; i < maxCandidates && placed < targetPlacements; i++) {
            double t = maxCandidates <= 1 ? 0.0D : ((double) i + patchRandom.nextDouble() * 0.35D) / (double) (maxCandidates - 1);
            double spiralRadius = 2.0D + Math.sqrt(t) * (double) patchRadius;
            double angle = phase + t * spiralTurns * Math.PI * 2.0D + Math.sin(t * Math.PI * 3.0D) * 0.18D;
            double baseX = Math.cos(angle) * spiralRadius;
            double baseZ = Math.sin(angle) * spiralRadius;
            double tangentAngle = angle + Math.PI * 0.5D;
            double armOffset = (patchRandom.nextDouble() - 0.5D) * armWidth * 2.0D;
            double radialJitter = (patchRandom.nextDouble() - 0.5D) * radialJitterScale * 2.0D;
            int offsetX = Math.round((float) (baseX + Math.cos(tangentAngle) * armOffset + Math.cos(angle) * radialJitter));
            int offsetZ = Math.round((float) (baseZ + Math.sin(tangentAngle) * armOffset + Math.sin(angle) * radialJitter));
            if (offsetX * offsetX + offsetZ * offsetZ > patchRadius * patchRadius) {
                continue;
            }

            int x = patchCenterX + offsetX;
            int z = patchCenterZ + offsetZ;
            if (!isInChunk(x, z, chunkX, chunkZ)) {
                continue;
            }

            long positionKey = (((long) x) << 32) ^ (z & 0xFFFFFFFFL);
            if (!triedPositions.add(positionKey)) {
                continue;
            }
            if (tryPlaceSunflower(world, x, z, patchRandom, settings, mode)) {
                placed++;
            }
        }
    }

    private static boolean tryPlaceSunflower(World world, int x, int z, Random random, PatchSettings settings, PatchMode mode) {
        if (!isTargetBiome(world.getBiomeGenForCoords(x, z), settings)) {
            return false;
        }
        int y = world.getHeightValue(x, z);
        if (y < 1 || y >= world.getActualHeight() - 1) {
            return false;
        }
        if (!isValidGround(world, x, y - 1, z)) {
            return false;
        }
        boolean replacingBarley = WheatfieldContent.wheatfieldBarley != null && world.getBlock(x, y, z) == WheatfieldContent.wheatfieldBarley;
        if (!world.isAirBlock(x, y, z) && !replacingBarley) {
            return false;
        }
        if (!world.isAirBlock(x, y + 1, z)) {
            return false;
        }
        if (!hasPlacementClearance(world, x, y, z)) {
            return false;
        }
        if (replacingBarley) {
            world.setBlockToAir(x, y, z);
        }
        Block sunflower = getSunflowerBlockForMode(mode, random);
        if (sunflower == null) {
            return false;
        }
        if (!sunflower.canPlaceBlockAt(world, x, y, z)) {
            if (replacingBarley && WheatfieldContent.wheatfieldBarley != null && WheatfieldContent.wheatfieldBarley.canBlockStay(world, x, y, z)) {
                world.setBlock(x, y, z, WheatfieldContent.wheatfieldBarley, 0, 2);
            }
            return false;
        }
        if (sunflower instanceof BlockOffLawnSunflowerBush) {
            ((BlockOffLawnSunflowerBush) sunflower).placeAt(world, x, y, z, 2);
        } else {
            world.setBlock(x, y, z, sunflower, 0, 2);
        }
        return true;
    }

    private static boolean hasPlacementClearance(World world, int x, int y, int z) {
        return isOpenNeighborRing(world, x, y, z) && isOpenNeighborRing(world, x, y + 1, z);
    }

    private static boolean isOpenNeighborRing(World world, int x, int y, int z) {
        return isNonBlockingNeighbor(world, x + 1, y, z)
                && isNonBlockingNeighbor(world, x - 1, y, z)
                && isNonBlockingNeighbor(world, x, y, z + 1)
                && isNonBlockingNeighbor(world, x, y, z - 1);
    }

    private static boolean isNonBlockingNeighbor(World world, int x, int y, int z) {
        Block block = world.getBlock(x, y, z);
        if (block == null || block == Blocks.air) {
            return true;
        }
        Material material = block.getMaterial();
        return !block.isOpaqueCube() && !material.blocksMovement();
    }

    private static boolean patchOverlapsChunk(int patchCenterX, int patchCenterZ, int patchRadius, int chunkX, int chunkZ) {
        int minX = chunkX * CHUNK_SIZE;
        int minZ = chunkZ * CHUNK_SIZE;
        int maxX = minX + CHUNK_SIZE - 1;
        int maxZ = minZ + CHUNK_SIZE - 1;
        int closestX = clamp(patchCenterX, minX, maxX);
        int closestZ = clamp(patchCenterZ, minZ, maxZ);
        int dx = patchCenterX - closestX;
        int dz = patchCenterZ - closestZ;
        return dx * dx + dz * dz <= patchRadius * patchRadius;
    }

    private static boolean isInChunk(int x, int z, int chunkX, int chunkZ) {
        return x >> 4 == chunkX && z >> 4 == chunkZ;
    }

    private static boolean isTargetBiome(BiomeGenBase biome, PatchSettings settings) {
        if (biome == null) {
            return false;
        }
        if (matchesConfiguredBiomeList(biome, settings)) {
            return true;
        }
        return matchesConfiguredBiomeType(biome, settings);
    }

    private static boolean matchesConfiguredBiomeList(BiomeGenBase biome, PatchSettings settings) {
        String[] entries = settings == null ? null : settings.biomeList;
        if (entries == null || entries.length == 0) {
            return false;
        }

        for (String entry : entries) {
            if (ConfigResolver.matchesBiomeEntry(biome, entry)) {
                return true;
            }
        }
        return false;
    }

    private static boolean matchesConfiguredBiomeType(BiomeGenBase biome, PatchSettings settings) {
        String[] entries = settings == null ? null : settings.biomeTypes;
        if (entries == null || entries.length == 0) {
            return false;
        }

        for (String entry : entries) {
            if (entry == null) {
                continue;
            }
            String trimmed = entry.trim();
            if (trimmed.isEmpty()) {
                continue;
            }
            int prefix = trimmed.indexOf(':');
            String typeName = prefix >= 0 ? trimmed.substring(prefix + 1).trim() : trimmed;
            try {
                BiomeDictionary.Type type = BiomeDictionary.Type.valueOf(typeName.toUpperCase(Locale.ROOT));
                if (BiomeDictionary.isBiomeOfType(biome, type)) {
                    return true;
                }
            } catch (IllegalArgumentException ignored) {
                String normalized = ConfigResolver.normalizeToken(typeName);
                String biomeName = ConfigResolver.normalizeToken(biome.biomeName);
                if (!normalized.isEmpty() && biomeName.contains(normalized)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean isValidGround(World world, int x, int y, int z) {
        Block support = world.getBlock(x, y, z);
        if (support == null || support == Blocks.air) {
            return false;
        }
        if (support == Blocks.grass || support == Blocks.dirt) {
            return true;
        }
        Material material = support.getMaterial();
        return material == Material.grass || material == Material.ground;
    }

    private static int clamp(int value, int min, int max) {
        if (value < min) {
            return min;
        }
        if (value > max) {
            return max;
        }
        return value;
    }

    private static boolean hasPatchBlock(PatchMode mode) {
        switch (mode) {
            case NORMAL:
                return OffLawnContent.sunflowerBush != null;
            case BRIGHT:
                return OffLawnContent.brightSunflower != null;
            default:
                return OffLawnContent.sunflowerBush != null || OffLawnContent.brightSunflower != null;
        }
    }

    private static Block getSunflowerBlockForMode(PatchMode mode, Random random) {
        switch (mode) {
            case NORMAL:
                return OffLawnContent.sunflowerBush;
            case BRIGHT:
                return OffLawnContent.brightSunflower;
            default:
                return OffLawnContent.getMixedSunflowerVariant(random);
        }
    }

    private static int floorDiv(int value, int divisor) {
        int result = value / divisor;
        if ((value ^ divisor) < 0 && result * divisor != value) {
            result--;
        }
        return result;
    }

    private static long mixSeed(long worldSeed, int x, int z) {
        long seed = worldSeed;
        seed ^= (long) x * 341873128712L;
        seed ^= (long) z * 132897987541L;
        seed ^= (seed >>> 13);
        seed *= 0x5DEECE66DL;
        seed ^= (seed >>> 17);
        return seed;
    }

    private enum PatchMode {
        NORMAL,
        BRIGHT,
        MIXED
    }

    private static final class PatchSettings {
        private final boolean enabled;
        private final int patchChance;
        private final int attemptsPerPatch;
        private final int patchRadiusBlocks;
        private final String[] biomeTypes;
        private final String[] biomeList;

        private PatchSettings(boolean enabled, int patchChance, int attemptsPerPatch, int patchRadiusBlocks, String[] biomeTypes, String[] biomeList) {
            this.enabled = enabled;
            this.patchChance = patchChance;
            this.attemptsPerPatch = attemptsPerPatch;
            this.patchRadiusBlocks = patchRadiusBlocks;
            this.biomeTypes = biomeTypes;
            this.biomeList = biomeList;
        }
    }
}
