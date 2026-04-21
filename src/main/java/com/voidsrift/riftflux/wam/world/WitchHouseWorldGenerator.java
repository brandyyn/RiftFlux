package com.voidsrift.riftflux.wam.world;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.wam.WAMMobFactory;
import com.voidsrift.riftflux.wheatfield.BlockWheatfieldBarley;
import com.voidsrift.riftflux.wheatfield.WheatfieldContent;
import cpw.mods.fml.common.IWorldGenerator;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLiving;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.storage.MapStorage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Set;

public class WitchHouseWorldGenerator implements IWorldGenerator {
    private static final String TEMPLATE_RESOURCE = "/assets/riftflux/data/wam/witch_house_template.tsv";
    private static final int TEMPLATE_CENTER_X = 12;
    private static final int TEMPLATE_CENTER_Z = 12;
    private static final int EDGE_BLEND_RADIUS = 8;
    private static final int SITE_CANDIDATE_ATTEMPTS = 6;
    private static final int SITE_CHECK_PADDING = 2;
    private static final int SITE_SAMPLE_STEP = 2;
    private static final int MIN_SITE_BIOME_EDGE_DISTANCE = 12;
    private static final int PREFERRED_SITE_BIOME_EDGE_DISTANCE = 20;
    private static final int WATER_EXCLUSION_RADIUS = 10;
    private static final int MAX_SITE_HEIGHT_VARIATION = 4;
    private static final int MAX_SITE_ORIGIN_OFFSET = 2;
    private static final int MAX_SITE_NEIGHBOR_STEP = 2;
    private static final int MAX_BLEND_STEP = 1;
    private static final int HOUSE_CORE_MIN_X = 1;
    private static final int HOUSE_CORE_MAX_X = 24;
    private static final int HOUSE_CORE_MIN_Z = 2;
    private static final int HOUSE_CORE_MAX_Z = 22;
    private static final TemplateData TEMPLATE = loadTemplate();

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
        if (world == null || world.provider == null || world.provider.dimensionId != 0) {
            return;
        }
        if (!ModConfig.enableWitchesAndMoreModule || !ModConfig.enableWitchHouseStructure || !ModConfig.enableWheatfieldBiome) {
            return;
        }
        if (WheatfieldContent.wheatfieldBiome == null || ModConfig.witchHouseChunkChance <= 0 || TEMPLATE.blocks.isEmpty()) {
            return;
        }
        if (random.nextInt(ModConfig.witchHouseChunkChance) != 0) {
            return;
        }

        PlacementCandidate placement = findBestPlacement(world, random, chunkX, chunkZ);
        if (placement == null) {
            return;
        }
        long minSpacingSq = getMinHouseSpacingSq();
        if (minSpacingSq > 0L
                && WitchHousePlacementData.hasPlacementWithin(world, placement.centerX, placement.centerZ, minSpacingSq)) {
            return;
        }

        int centerX = placement.centerX;
        int centerZ = placement.centerZ;
        int originY = placement.originY;

        clearBoundingBox(world, centerX, originY, centerZ);
        placeTemplate(world, random, centerX, originY, centerZ);
        blendStructureEdges(world, random, centerX, originY, centerZ);
        refreshStructureBarley(world, centerX, centerZ);
        spawnConfiguredInsideMobs(world, random, centerX, originY, centerZ);
        spawnConfiguredOutsideMobs(world, random, centerX, originY, centerZ);
        WitchHousePlacementData.recordPlacement(world, centerX, centerZ);
    }

    private PlacementCandidate findBestPlacement(World world, Random random, int chunkX, int chunkZ) {
        PlacementCandidate best = null;

        for (int attempt = 0; attempt < SITE_CANDIDATE_ATTEMPTS; attempt++) {
            int centerX = chunkX * 16 + 2 + random.nextInt(12);
            int centerZ = chunkZ * 16 + 2 + random.nextInt(12);
            PlacementCandidate candidate = evaluatePlacement(world, centerX, centerZ);
            if (candidate == null) {
                continue;
            }
            if (best == null || candidate.score < best.score) {
                best = candidate;
            }
        }

        return best;
    }

    private static long getMinHouseSpacingSq() {
        int minSpacing = Math.max(0, ModConfig.witchHouseMinDistanceBlocks);
        return (long) minSpacing * (long) minSpacing;
    }

    private PlacementCandidate evaluatePlacement(World world, int centerX, int centerZ) {
        if (world.getBiomeGenForCoords(centerX, centerZ) != WheatfieldContent.wheatfieldBiome) {
            return null;
        }

        int originY = findTerrainSurfaceY(world, centerX, centerZ);
        if (originY < 62) {
            return null;
        }

        int minGround = Integer.MAX_VALUE;
        int maxGround = Integer.MIN_VALUE;
        int totalGround = 0;
        int sampleCount = 0;
        int roughness = 0;
        int sampleColumns = ((TEMPLATE.maxX - TEMPLATE.minX + SITE_CHECK_PADDING * 2) / SITE_SAMPLE_STEP) + 1;
        int[] previousRow = new int[sampleColumns];
        Arrays.fill(previousRow, Integer.MIN_VALUE);
        int maxNeighborStep = 0;

        for (int localZ = TEMPLATE.minZ - SITE_CHECK_PADDING; localZ <= TEMPLATE.maxZ + SITE_CHECK_PADDING; localZ += SITE_SAMPLE_STEP) {
            int previousInRow = Integer.MIN_VALUE;
            int sampleColumn = 0;
            for (int localX = TEMPLATE.minX - SITE_CHECK_PADDING; localX <= TEMPLATE.maxX + SITE_CHECK_PADDING; localX += SITE_SAMPLE_STEP) {
                int worldX = centerX + localX - TEMPLATE_CENTER_X;
                int worldZ = centerZ + localZ - TEMPLATE_CENTER_Z;
                int groundY = findTerrainSurfaceY(world, worldX, worldZ);
                if (groundY <= 0) {
                    return null;
                }
                Block ground = world.getBlock(worldX, groundY, worldZ);
                BiomeGenBase biome = world.getBiomeGenForCoords(worldX, worldZ);

                if (biome != WheatfieldContent.wheatfieldBiome) {
                    return null;
                }
                if (ground == null || ground.getMaterial().isLiquid() || !ground.getMaterial().isSolid()) {
                    return null;
                }

                minGround = Math.min(minGround, groundY);
                maxGround = Math.max(maxGround, groundY);
                totalGround += groundY;
                sampleCount++;

                if (previousInRow != Integer.MIN_VALUE) {
                    int delta = Math.abs(groundY - previousInRow);
                    maxNeighborStep = Math.max(maxNeighborStep, delta);
                    roughness += delta;
                }
                if (previousRow[sampleColumn] != Integer.MIN_VALUE) {
                    int delta = Math.abs(groundY - previousRow[sampleColumn]);
                    maxNeighborStep = Math.max(maxNeighborStep, delta);
                    roughness += delta;
                }

                previousInRow = groundY;
                previousRow[sampleColumn] = groundY;
                sampleColumn++;
            }
        }

        if (sampleCount <= 0) {
            return null;
        }

        int averageGround = Math.round(totalGround / (float) sampleCount);
        int heightVariation = maxGround - minGround;
        int originOffset = Math.abs(originY - averageGround);

        if (heightVariation > MAX_SITE_HEIGHT_VARIATION
                || originOffset > MAX_SITE_ORIGIN_OFFSET
                || maxNeighborStep > MAX_SITE_NEIGHBOR_STEP) {
            return null;
        }

        int biomeEdgeDistance = getNearestNonWheatfieldDistance(world, centerX, centerZ, PREFERRED_SITE_BIOME_EDGE_DISTANCE);
        if (biomeEdgeDistance < MIN_SITE_BIOME_EDGE_DISTANCE) {
            return null;
        }
        if (hasNearbyLiquid(world, centerX, centerZ, WATER_EXCLUSION_RADIUS)) {
            return null;
        }

        int centerPenalty = Math.max(0, PREFERRED_SITE_BIOME_EDGE_DISTANCE - biomeEdgeDistance) * 12;
        int score = heightVariation * 16 + originOffset * 12 + maxNeighborStep * 10 + roughness + centerPenalty;
        return new PlacementCandidate(centerX, centerZ, originY, score);
    }

    private int getNearestNonWheatfieldDistance(World world, int centerX, int centerZ, int maxDistance) {
        int minWorldX = centerX + HOUSE_CORE_MIN_X - TEMPLATE_CENTER_X;
        int maxWorldX = centerX + HOUSE_CORE_MAX_X - TEMPLATE_CENTER_X;
        int minWorldZ = centerZ + HOUSE_CORE_MIN_Z - TEMPLATE_CENTER_Z;
        int maxWorldZ = centerZ + HOUSE_CORE_MAX_Z - TEMPLATE_CENTER_Z;
        double nearestDistance = maxDistance + 1.0D;

        for (int worldX = minWorldX - maxDistance; worldX <= maxWorldX + maxDistance; worldX++) {
            for (int worldZ = minWorldZ - maxDistance; worldZ <= maxWorldZ + maxDistance; worldZ++) {
                if (world.getBiomeGenForCoords(worldX, worldZ) == WheatfieldContent.wheatfieldBiome) {
                    continue;
                }

                double distance = distanceOutsideRectangle(worldX, worldZ, minWorldX, maxWorldX, minWorldZ, maxWorldZ);
                if (distance < nearestDistance) {
                    nearestDistance = distance;
                    if (nearestDistance < MIN_SITE_BIOME_EDGE_DISTANCE) {
                        return (int) Math.floor(nearestDistance);
                    }
                }
            }
        }

        return nearestDistance > maxDistance ? maxDistance + 1 : (int) Math.floor(nearestDistance);
    }

    private boolean hasNearbyLiquid(World world, int centerX, int centerZ, int radius) {
        int minWorldX = centerX + HOUSE_CORE_MIN_X - TEMPLATE_CENTER_X;
        int maxWorldX = centerX + HOUSE_CORE_MAX_X - TEMPLATE_CENTER_X;
        int minWorldZ = centerZ + HOUSE_CORE_MIN_Z - TEMPLATE_CENTER_Z;
        int maxWorldZ = centerZ + HOUSE_CORE_MAX_Z - TEMPLATE_CENTER_Z;
        int minScanX = minWorldX - radius;
        int maxScanX = maxWorldX + radius;
        int minScanZ = minWorldZ - radius;
        int maxScanZ = maxWorldZ + radius;

        for (int worldX = minScanX; worldX <= maxScanX; worldX++) {
            for (int worldZ = minScanZ; worldZ <= maxScanZ; worldZ++) {
                if (distanceOutsideRectangle(worldX, worldZ, minWorldX, maxWorldX, minWorldZ, maxWorldZ) > radius) {
                    continue;
                }
                if (isLiquidColumnNearby(world, worldX, worldZ)) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean isLiquidColumnNearby(World world, int x, int z) {
        int topY = Math.min(world.getHeightValue(x, z), world.getActualHeight() - 1);
        int minY = Math.max(1, topY - 4);

        for (int y = topY; y >= minY; y--) {
            Block block = world.getBlock(x, y, z);
            if (block == null || block.isAir(world, x, y, z)) {
                continue;
            }

            Material material = block.getMaterial();
            if (material != null && material.isLiquid()) {
                return true;
            }

            if (!isBlendReplaceable(block)) {
                return false;
            }
        }

        return false;
    }

    private void clearBoundingBox(World world, int centerX, int originY, int centerZ) {
        for (int localX = TEMPLATE.minX; localX <= TEMPLATE.maxX; localX++) {
            int worldX = centerX + localX - TEMPLATE_CENTER_X;
            for (int localZ = TEMPLATE.minZ; localZ <= TEMPLATE.maxZ; localZ++) {
                int worldZ = centerZ + localZ - TEMPLATE_CENTER_Z;
                for (int localY = TEMPLATE.minY; localY <= TEMPLATE.maxY; localY++) {
                    int worldY = originY + localY;
                    if (worldY <= 0 || worldY >= world.getActualHeight()) {
                        continue;
                    }
                    world.setBlock(worldX, worldY, worldZ, Blocks.air, 0, 2);
                }
            }
        }
    }

    private void placeTemplate(World world, Random random, int centerX, int originY, int centerZ) {
        List<ChestLootEntry> chestLoot = parseChestLootEntries(ModConfig.witchHouseChestLootEntries);
        for (TemplateEntry entry : TEMPLATE.blocks) {
            Block block = resolveBlock(entry.blockKey);
            if (block == null) {
                continue;
            }

            int worldX = centerX + entry.localX - TEMPLATE_CENTER_X;
            int worldY = originY + entry.localY;
            int worldZ = centerZ + entry.localZ - TEMPLATE_CENTER_Z;
            if (worldY <= 0 || worldY >= world.getActualHeight()) {
                continue;
            }

            int meta = entry.meta;
            if (block == Blocks.chest && meta == 0) {
                meta = 3;
            }
            world.setBlock(worldX, worldY, worldZ, block, meta, 2);
            if (block == Blocks.chest) {
                populateChest(world, random, worldX, worldY, worldZ, chestLoot);
            }
        }
    }

    private void blendStructureEdges(World world, Random random, int centerX, int originY, int centerZ) {
        int minWorldX = centerX + TEMPLATE.minX - TEMPLATE_CENTER_X;
        int maxWorldX = centerX + TEMPLATE.maxX - TEMPLATE_CENTER_X;
        int minWorldZ = centerZ + TEMPLATE.minZ - TEMPLATE_CENTER_Z;
        int maxWorldZ = centerZ + TEMPLATE.maxZ - TEMPLATE_CENTER_Z;
        int minBlendX = minWorldX - EDGE_BLEND_RADIUS;
        int minBlendZ = minWorldZ - EDGE_BLEND_RADIUS;
        int blendWidth = maxWorldX - minWorldX + 1 + EDGE_BLEND_RADIUS * 2;
        int blendDepth = maxWorldZ - minWorldZ + 1 + EDGE_BLEND_RADIUS * 2;
        int[][] targetHeights = new int[blendWidth][blendDepth];
        boolean[][] blendMask = new boolean[blendWidth][blendDepth];
        double[][] blendDistances = new double[blendWidth][blendDepth];

        for (int localX = 0; localX < blendWidth; localX++) {
            int worldX = minBlendX + localX;
            for (int localZ = 0; localZ < blendDepth; localZ++) {
                int worldZ = minBlendZ + localZ;
                double distance = distanceOutsideRectangle(worldX, worldZ, minWorldX, maxWorldX, minWorldZ, maxWorldZ);
                if (distance <= 0.0D || distance > EDGE_BLEND_RADIUS) {
                    continue;
                }

                int terrainY = findTerrainSurfaceY(world, worldX, worldZ);
                if (terrainY <= 0) {
                    continue;
                }

                double edgeWeight = 1.0D - distance / (EDGE_BLEND_RADIUS + 0.5D);
                edgeWeight = clamp01(edgeWeight);
                edgeWeight = smoothStep(edgeWeight);

                targetHeights[localX][localZ] = interpolateHeight(terrainY, originY, edgeWeight);
                blendMask[localX][localZ] = true;
                blendDistances[localX][localZ] = distance;
            }
        }

        int[][] smoothedHeights = smoothBlendHeights(targetHeights, blendMask);
        relaxBlendGradient(smoothedHeights, blendMask, MAX_BLEND_STEP);
        for (int localX = 0; localX < blendWidth; localX++) {
            int worldX = minBlendX + localX;
            for (int localZ = 0; localZ < blendDepth; localZ++) {
                if (!blendMask[localX][localZ]) {
                    continue;
                }

                int worldZ = minBlendZ + localZ;
                int targetY = smoothedHeights[localX][localZ];
                if (!reshapeBlendColumn(world, worldX, worldZ, targetY)) {
                    continue;
                }

            }
        }
    }

    private void refreshStructureBarley(World world, int centerX, int centerZ) {
        clearFloatingFoliageInArea(world, centerX, centerZ);
        clearBarleyInArea(world, centerX, centerZ);
        coverStructureAndBlendSurfaceWithBarley(world, centerX, centerZ);
    }

    private void seedBlendBarley(World world, int centerX, int centerZ) {
        int minWorldX = centerX + TEMPLATE.minX - TEMPLATE_CENTER_X;
        int maxWorldX = centerX + TEMPLATE.maxX - TEMPLATE_CENTER_X;
        int minWorldZ = centerZ + TEMPLATE.minZ - TEMPLATE_CENTER_Z;
        int maxWorldZ = centerZ + TEMPLATE.maxZ - TEMPLATE_CENTER_Z;

        for (int worldX = minWorldX - EDGE_BLEND_RADIUS; worldX <= maxWorldX + EDGE_BLEND_RADIUS; worldX++) {
            for (int worldZ = minWorldZ - EDGE_BLEND_RADIUS; worldZ <= maxWorldZ + EDGE_BLEND_RADIUS; worldZ++) {
                if (isInsideRectangle(worldX, worldZ, minWorldX, maxWorldX, minWorldZ, maxWorldZ)) {
                    continue;
                }
                if (distanceOutsideRectangle(worldX, worldZ, minWorldX, maxWorldX, minWorldZ, maxWorldZ) > EDGE_BLEND_RADIUS) {
                    continue;
                }

                seedBarleyOnSurfaceColumn(world, worldX, worldZ, true);
            }
        }
    }

    private void clearBarleyInArea(World world, int centerX, int centerZ) {
        if (WheatfieldContent.wheatfieldBarley == null) {
            return;
        }

        int minWorldX = centerX + TEMPLATE.minX - TEMPLATE_CENTER_X - EDGE_BLEND_RADIUS;
        int maxWorldX = centerX + TEMPLATE.maxX - TEMPLATE_CENTER_X + EDGE_BLEND_RADIUS;
        int minWorldZ = centerZ + TEMPLATE.minZ - TEMPLATE_CENTER_Z - EDGE_BLEND_RADIUS;
        int maxWorldZ = centerZ + TEMPLATE.maxZ - TEMPLATE_CENTER_Z + EDGE_BLEND_RADIUS;

        for (int worldX = minWorldX; worldX <= maxWorldX; worldX++) {
            for (int worldZ = minWorldZ; worldZ <= maxWorldZ; worldZ++) {
                for (int worldY = 1; worldY < world.getActualHeight(); worldY++) {
                    if (world.getBlock(worldX, worldY, worldZ) == WheatfieldContent.wheatfieldBarley) {
                        world.setBlock(worldX, worldY, worldZ, Blocks.air, 0, 2);
                    }
                }
            }
        }
    }

    private void clearFloatingFoliageInArea(World world, int centerX, int centerZ) {
        int minWorldX = centerX + TEMPLATE.minX - TEMPLATE_CENTER_X - EDGE_BLEND_RADIUS;
        int maxWorldX = centerX + TEMPLATE.maxX - TEMPLATE_CENTER_X + EDGE_BLEND_RADIUS;
        int minWorldZ = centerZ + TEMPLATE.minZ - TEMPLATE_CENTER_Z - EDGE_BLEND_RADIUS;
        int maxWorldZ = centerZ + TEMPLATE.maxZ - TEMPLATE_CENTER_Z + EDGE_BLEND_RADIUS;

        for (int worldX = minWorldX; worldX <= maxWorldX; worldX++) {
            for (int worldZ = minWorldZ; worldZ <= maxWorldZ; worldZ++) {
                clearFloatingFoliageAboveSurface(world, worldX, worldZ);
            }
        }
    }

    private void clearFloatingFoliageAboveSurface(World world, int x, int z) {
        int surfaceY = findTerrainSurfaceY(world, x, z);
        if (surfaceY < 0) {
            return;
        }

        int maxY = world.getHeightValue(x, z) - 1;
        for (int y = maxY; y > surfaceY; y--) {
            Block block = world.getBlock(x, y, z);
            if (isFloatingFoliage(block)) {
                world.setBlock(x, y, z, Blocks.air, 0, 2);
            }
        }
    }

    private void coverStructureAndBlendSurfaceWithBarley(World world, int centerX, int centerZ) {
        int minWorldX = centerX + TEMPLATE.minX - TEMPLATE_CENTER_X;
        int maxWorldX = centerX + TEMPLATE.maxX - TEMPLATE_CENTER_X;
        int minWorldZ = centerZ + TEMPLATE.minZ - TEMPLATE_CENTER_Z;
        int maxWorldZ = centerZ + TEMPLATE.maxZ - TEMPLATE_CENTER_Z;

        for (int worldX = minWorldX - EDGE_BLEND_RADIUS; worldX <= maxWorldX + EDGE_BLEND_RADIUS; worldX++) {
            for (int worldZ = minWorldZ - EDGE_BLEND_RADIUS; worldZ <= maxWorldZ + EDGE_BLEND_RADIUS; worldZ++) {
                coverTopSurfaceWithBarley(world, worldX, worldZ);
            }
        }
    }

    private void coverTopSurfaceWithBarley(World world, int x, int z) {
        int surfaceY = findBarleySurfaceY(world, x, z);
        if (surfaceY <= 0 || surfaceY >= world.getActualHeight() - 1) {
            return;
        }

        Block surface = world.getBlock(x, surfaceY, z);
        if (surface == Blocks.dirt) {
            world.setBlock(x, surfaceY, z, Blocks.grass, 0, 2);
            surface = Blocks.grass;
        }

        Block above = world.getBlock(x, surfaceY + 1, z);
        if (surface == Blocks.grass
                && (world.isAirBlock(x, surfaceY + 1, z)
                || isFloatingFoliage(above)
                || (above != null && !above.getMaterial().isSolid()))) {
            if (!world.isAirBlock(x, surfaceY + 1, z)) {
                world.setBlock(x, surfaceY + 1, z, Blocks.air, 0, 2);
            }
            tryPlaceBarley(world, x, surfaceY + 1, z);
        }
    }

    private int findBarleySurfaceY(World world, int x, int z) {
        int maxY = Math.min(world.getHeightValue(x, z), world.getActualHeight()) - 1;
        for (int y = maxY; y > 0; y--) {
            Block block = world.getBlock(x, y, z);
            if (block == null || block.isAir(world, x, y, z) || isFloatingFoliage(block)) {
                continue;
            }

            if (block != Blocks.grass && block != Blocks.dirt) {
                continue;
            }

            Block above = world.getBlock(x, y + 1, z);
            if (world.isAirBlock(x, y + 1, z)
                    || isFloatingFoliage(above)
                    || (above != null && !above.getMaterial().isSolid())) {
                return y;
            }
        }
        return -1;
    }

    private void spawnConfiguredInsideMobs(World world, Random random, int centerX, int originY, int centerZ) {
        if (ModConfig.witchHouseInsideMobIds == null || ModConfig.witchHouseInsideMobIds.length == 0
                || ModConfig.witchHouseInsideMobCount <= 0) {
            return;
        }

        List<TemplateEntry> anchors = TEMPLATE.spawnerMarkers.isEmpty() ? Collections.singletonList(
                new TemplateEntry("marker", 12, 1, 12, "air", 0, 0)) : TEMPLATE.spawnerMarkers;

        for (int i = 0; i < ModConfig.witchHouseInsideMobCount; i++) {
            String id = ModConfig.witchHouseInsideMobIds[random.nextInt(ModConfig.witchHouseInsideMobIds.length)];
            EntityLiving mob = WAMMobFactory.createMobByKey(world, id);
            if (mob == null) {
                continue;
            }

            TemplateEntry anchor = anchors.get(random.nextInt(anchors.size()));
            double spawnX = centerX + anchor.localX - TEMPLATE_CENTER_X + 0.5D;
            double spawnY = originY + anchor.localY + 0.1D;
            double spawnZ = centerZ + anchor.localZ - TEMPLATE_CENTER_Z + 0.5D;
            mob.setLocationAndAngles(spawnX, spawnY, spawnZ, random.nextFloat() * 360.0F, 0.0F);
            mob.onSpawnWithEgg(null);
            mob.func_110163_bv();
            world.spawnEntityInWorld(mob);
        }
    }

    private void spawnConfiguredOutsideMobs(World world, Random random, int centerX, int originY, int centerZ) {
        if (ModConfig.witchHouseOutsideMobIds == null || ModConfig.witchHouseOutsideMobIds.length == 0
                || ModConfig.witchHouseOutsideMobCount <= 0) {
            return;
        }

        double radius = Math.max(HOUSE_CORE_MAX_X - TEMPLATE_CENTER_X, HOUSE_CORE_MAX_Z - TEMPLATE_CENTER_Z) + 5.0D;

        for (int i = 0; i < ModConfig.witchHouseOutsideMobCount; i++) {
            String id = ModConfig.witchHouseOutsideMobIds[random.nextInt(ModConfig.witchHouseOutsideMobIds.length)];
            EntityLiving mob = WAMMobFactory.createMobByKey(world, id);
            if (mob == null) {
                continue;
            }

            double angle = random.nextDouble() * Math.PI * 2.0D;
            double distance = radius + random.nextDouble() * 4.0D;
            double spawnX = centerX + Math.cos(angle) * distance;
            double spawnZ = centerZ + Math.sin(angle) * distance;
            int spawnY = world.getHeightValue((int) Math.floor(spawnX), (int) Math.floor(spawnZ));
            mob.setLocationAndAngles(spawnX + 0.5D, spawnY, spawnZ + 0.5D, random.nextFloat() * 360.0F, 0.0F);
            mob.onSpawnWithEgg(null);
            mob.func_110163_bv();
            world.spawnEntityInWorld(mob);
        }
    }

    private void populateChest(World world, Random random, int x, int y, int z, List<ChestLootEntry> lootEntries) {
        if (lootEntries == null || lootEntries.isEmpty()) {
            return;
        }

        TileEntity tileEntity = world.getTileEntity(x, y, z);
        if (!(tileEntity instanceof TileEntityChest)) {
            return;
        }

        TileEntityChest chest = (TileEntityChest) tileEntity;
        for (ChestLootEntry lootEntry : lootEntries) {
            if (lootEntry == null || lootEntry.stack == null || random.nextFloat() >= lootEntry.chance) {
                continue;
            }

            int slot = findRandomEmptySlot(chest, random);
            if (slot < 0) {
                break;
            }
            chest.setInventorySlotContents(slot, lootEntry.stack.copy());
        }
        chest.markDirty();
    }

    private static int findRandomEmptySlot(TileEntityChest chest, Random random) {
        int size = chest.getSizeInventory();
        if (size <= 0) {
            return -1;
        }

        int start = random.nextInt(size);
        for (int offset = 0; offset < size; offset++) {
            int slot = (start + offset) % size;
            if (chest.getStackInSlot(slot) == null) {
                return slot;
            }
        }
        return -1;
    }

    private static List<ChestLootEntry> parseChestLootEntries(String[] entries) {
        List<ChestLootEntry> lootEntries = new ArrayList<ChestLootEntry>();
        if (entries == null || entries.length == 0) {
            return lootEntries;
        }

        for (String rawEntry : entries) {
            if (rawEntry == null) {
                continue;
            }

            String entry = rawEntry.trim();
            if (entry.isEmpty()) {
                continue;
            }

            String itemPart = entry;
            String chancePart = null;
            int pipeIndex = entry.indexOf('|');
            if (pipeIndex >= 0) {
                itemPart = entry.substring(0, pipeIndex).trim();
                chancePart = entry.substring(pipeIndex + 1).trim();
            }

            float chance = parseChance(chancePart, 1.0F);
            if (chance <= 0.0F) {
                continue;
            }

            int count = 1;
            int meta = 0;
            int stackIndex = itemPart.indexOf('*');
            if (stackIndex >= 0) {
                count = parseInt(itemPart.substring(stackIndex + 1), 1);
                itemPart = itemPart.substring(0, stackIndex).trim();
            }

            int metaIndex = itemPart.indexOf('@');
            if (metaIndex >= 0) {
                meta = parseInt(itemPart.substring(metaIndex + 1), 0);
                itemPart = itemPart.substring(0, metaIndex).trim();
            }

            Item item = resolveLootItem(itemPart);
            if (item == null) {
                continue;
            }

            lootEntries.add(new ChestLootEntry(new ItemStack(item, Math.max(1, count), Math.max(0, meta)), chance));
        }

        return lootEntries;
    }

    private static Item resolveLootItem(String itemId) {
        if (itemId == null || itemId.isEmpty()) {
            return null;
        }

        String[] split = itemId.split(":", 2);
        Item item = null;
        if (split.length == 2) {
            item = GameRegistry.findItem(split[0].toLowerCase(Locale.ROOT), split[1]);
        }
        if (item == null) {
            Object registryObject = Item.itemRegistry.getObject(itemId);
            if (registryObject instanceof Item) {
                item = (Item) registryObject;
            }
        }
        return item;
    }

    private static float parseChance(String value, float fallback) {
        if (value == null || value.trim().isEmpty()) {
            return fallback;
        }
        try {
            float chance = Float.parseFloat(value.trim());
            if (chance > 1.0F) {
                chance /= 100.0F;
            }
            if (chance < 0.0F) {
                return 0.0F;
            }
            return Math.min(chance, 1.0F);
        } catch (NumberFormatException ignored) {
            return fallback;
        }
    }

    private static int parseInt(String value, int fallback) {
        if (value == null || value.trim().isEmpty()) {
            return fallback;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException ignored) {
            return fallback;
        }
    }

    private static Block resolveBlock(String blockKey) {
        if ("grass".equals(blockKey)) {
            return Blocks.grass;
        }
        if ("dirt".equals(blockKey)) {
            return Blocks.dirt;
        }
        if ("farmland".equals(blockKey)) {
            return Blocks.farmland;
        }
        if ("wheat".equals(blockKey)) {
            return Blocks.wheat;
        }
        if ("planks".equals(blockKey)) {
            return Blocks.planks;
        }
        if ("stone_brick_stairs".equals(blockKey)) {
            return Blocks.stone_brick_stairs;
        }
        if ("cobblestone".equals(blockKey)) {
            return Blocks.cobblestone;
        }
        if ("fence".equals(blockKey)) {
            return Blocks.fence;
        }
        if ("fence_gate".equals(blockKey)) {
            return Blocks.fence_gate;
        }
        if ("glass_pane".equals(blockKey)) {
            return Blocks.glass_pane;
        }
        if ("stone_slab".equals(blockKey)) {
            return Blocks.stone_slab;
        }
        if ("stonebrick".equals(blockKey)) {
            return Blocks.stonebrick;
        }
        if ("tall_grass".equals(blockKey)) {
            return Blocks.tallgrass;
        }
        if ("pumpkin".equals(blockKey)) {
            return Blocks.pumpkin;
        }
        if ("lit_pumpkin".equals(blockKey)) {
            return Blocks.lit_pumpkin;
        }
        if ("torch".equals(blockKey)) {
            return Blocks.torch;
        }
        if ("vine".equals(blockKey)) {
            return Blocks.vine;
        }
        if ("yellow_flower".equals(blockKey)) {
            return Blocks.yellow_flower;
        }
        if ("red_flower".equals(blockKey)) {
            return Blocks.red_flower;
        }
        if ("web".equals(blockKey)) {
            return Blocks.web;
        }
        if ("chest".equals(blockKey)) {
            return Blocks.chest;
        }
        if ("cauldron".equals(blockKey)) {
            return Blocks.cauldron;
        }
        if ("brewing_stand".equals(blockKey)) {
            return Blocks.brewing_stand;
        }
        if ("enchanting_table".equals(blockKey)) {
            return Blocks.enchanting_table;
        }
        if ("brown_mushroom".equals(blockKey)) {
            return Blocks.brown_mushroom;
        }
        if ("red_mushroom".equals(blockKey)) {
            return Blocks.red_mushroom;
        }
        if ("wooden_door".equals(blockKey)) {
            return Blocks.wooden_door;
        }
        return null;
    }

    private static TemplateData loadTemplate() {
        InputStream stream = WitchHouseWorldGenerator.class.getResourceAsStream(TEMPLATE_RESOURCE);
        if (stream == null) {
            return new TemplateData(Collections.<TemplateEntry>emptyList(), Collections.<TemplateEntry>emptyList(), 0, 0, 0, 0, 0, 0);
        }

        List<TemplateEntry> blocks = new ArrayList<TemplateEntry>();
        List<TemplateEntry> spawnerMarkers = new ArrayList<TemplateEntry>();
        int minX = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxY = Integer.MIN_VALUE;
        int minZ = Integer.MAX_VALUE;
        int maxZ = Integer.MIN_VALUE;

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
            try {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.isEmpty() || line.startsWith("kind\t")) {
                        continue;
                    }

                    String[] parts = line.split("\t");
                    if (parts.length < 7) {
                        continue;
                    }

                    TemplateEntry entry = new TemplateEntry(
                            parts[0],
                            Integer.parseInt(parts[1]),
                            Integer.parseInt(parts[2]),
                            Integer.parseInt(parts[3]),
                            parts[4],
                            Integer.parseInt(parts[5]),
                            Integer.parseInt(parts[6]));
                    minX = Math.min(minX, entry.localX);
                    maxX = Math.max(maxX, entry.localX);
                    minY = Math.min(minY, entry.localY);
                    maxY = Math.max(maxY, entry.localY);
                    minZ = Math.min(minZ, entry.localZ);
                    maxZ = Math.max(maxZ, entry.localZ);

                    if ("spawner".equals(entry.kind)) {
                        spawnerMarkers.add(entry);
                    } else {
                        blocks.add(entry);
                    }
                }
            } finally {
                reader.close();
            }
        } catch (IOException e) {
            return new TemplateData(Collections.<TemplateEntry>emptyList(), Collections.<TemplateEntry>emptyList(), 0, 0, 0, 0, 0, 0);
        }

        if (minX == Integer.MAX_VALUE) {
            return new TemplateData(Collections.<TemplateEntry>emptyList(), Collections.<TemplateEntry>emptyList(), 0, 0, 0, 0, 0, 0);
        }

        return new TemplateData(blocks, spawnerMarkers, minX, maxX, minY, maxY, minZ, maxZ);
    }

    private void tryPlaceBarley(World world, int x, int y, int z) {
        if (WheatfieldContent.wheatfieldBarley == null || !canPlaceBarleyAt(world, x, y, z)) {
            return;
        }
        world.setBlock(x, y, z, WheatfieldContent.wheatfieldBarley, 0, 2);
    }

    private void seedBarleyOnSurfaceColumn(World world, int x, int z, boolean requireStableSurface) {
        int surfaceY = findTerrainSurfaceY(world, x, z);
        if (surfaceY <= 0 || surfaceY >= world.getActualHeight() - 1) {
            return;
        }
        if (world.getBlock(x, surfaceY, z) != Blocks.grass || !world.isAirBlock(x, surfaceY + 1, z)) {
            return;
        }
        if (requireStableSurface && !isStableBarleySurface(world, x, surfaceY, z)) {
            return;
        }

        tryPlaceBarley(world, x, surfaceY + 1, z);
    }

    private static boolean canPlaceBarleyAt(World world, int x, int y, int z) {
        if (world == null || y <= 0 || y >= world.getActualHeight()) {
            return false;
        }

        if (!(WheatfieldContent.wheatfieldBarley instanceof BlockWheatfieldBarley) || !world.isAirBlock(x, y, z)) {
            return false;
        }

        Block below = world.getBlock(x, y - 1, z);
        if (below != Blocks.grass && below != Blocks.dirt) {
            return false;
        }

        return ((BlockWheatfieldBarley) WheatfieldContent.wheatfieldBarley).canBlockStay(world, x, y, z);
    }

    private boolean isStableBarleySurface(World world, int x, int y, int z) {
        int levelSupport = 0;

        for (int[] offset : new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}}) {
            int neighborX = x + offset[0];
            int neighborZ = z + offset[1];
            int neighborSurfaceY = findTerrainSurfaceY(world, neighborX, neighborZ);
            if (neighborSurfaceY < y - 1) {
                return false;
            }
            if (neighborSurfaceY == y
                    && world.getBlock(neighborX, y, neighborZ) == Blocks.grass
                    && world.isAirBlock(neighborX, y + 1, neighborZ)) {
                levelSupport++;
            }
        }

        return levelSupport >= 2;
    }

    private static boolean isInsideRectangle(int x, int z, int minX, int maxX, int minZ, int maxZ) {
        return x >= minX && x <= maxX && z >= minZ && z <= maxZ;
    }

    private static double distanceOutsideRectangle(int x, int z, int minX, int maxX, int minZ, int maxZ) {
        int dx = 0;
        int dz = 0;

        if (x < minX) {
            dx = minX - x;
        } else if (x > maxX) {
            dx = x - maxX;
        }

        if (z < minZ) {
            dz = minZ - z;
        } else if (z > maxZ) {
            dz = z - maxZ;
        }

        if (dx == 0) {
            return dz;
        }
        if (dz == 0) {
            return dx;
        }
        return Math.sqrt(dx * dx + dz * dz);
    }

    private static int[][] smoothBlendHeights(int[][] sourceHeights, boolean[][] blendMask) {
        int width = sourceHeights.length;
        int depth = sourceHeights[0].length;
        int[][] smoothed = new int[width][depth];

        for (int x = 0; x < width; x++) {
            for (int z = 0; z < depth; z++) {
                if (!blendMask[x][z]) {
                    continue;
                }

                int sum = sourceHeights[x][z] * 4;
                int weight = 4;
                for (int offsetX = -1; offsetX <= 1; offsetX++) {
                    for (int offsetZ = -1; offsetZ <= 1; offsetZ++) {
                        if (offsetX == 0 && offsetZ == 0) {
                            continue;
                        }

                        int neighborX = x + offsetX;
                        int neighborZ = z + offsetZ;
                        if (neighborX < 0 || neighborX >= width || neighborZ < 0 || neighborZ >= depth
                                || !blendMask[neighborX][neighborZ]) {
                            continue;
                        }

                        int neighborWeight = offsetX == 0 || offsetZ == 0 ? 2 : 1;
                        sum += sourceHeights[neighborX][neighborZ] * neighborWeight;
                        weight += neighborWeight;
                    }
                }
                smoothed[x][z] = Math.round(sum / (float) weight);
            }
        }

        return smoothed;
    }

    private static void relaxBlendGradient(int[][] heights, boolean[][] blendMask, int maxStep) {
        int width = heights.length;
        int depth = heights[0].length;

        for (int pass = 0; pass < 3; pass++) {
            for (int x = 0; x < width; x++) {
                for (int z = 0; z < depth; z++) {
                    if (!blendMask[x][z]) {
                        continue;
                    }

                    int minAllowed = Integer.MIN_VALUE;
                    int maxAllowed = Integer.MAX_VALUE;

                    if (x > 0 && blendMask[x - 1][z]) {
                        minAllowed = Math.max(minAllowed, heights[x - 1][z] - maxStep);
                        maxAllowed = Math.min(maxAllowed, heights[x - 1][z] + maxStep);
                    }
                    if (x + 1 < width && blendMask[x + 1][z]) {
                        minAllowed = Math.max(minAllowed, heights[x + 1][z] - maxStep);
                        maxAllowed = Math.min(maxAllowed, heights[x + 1][z] + maxStep);
                    }
                    if (z > 0 && blendMask[x][z - 1]) {
                        minAllowed = Math.max(minAllowed, heights[x][z - 1] - maxStep);
                        maxAllowed = Math.min(maxAllowed, heights[x][z - 1] + maxStep);
                    }
                    if (z + 1 < depth && blendMask[x][z + 1]) {
                        minAllowed = Math.max(minAllowed, heights[x][z + 1] - maxStep);
                        maxAllowed = Math.min(maxAllowed, heights[x][z + 1] + maxStep);
                    }

                    if (heights[x][z] < minAllowed) {
                        heights[x][z] = minAllowed;
                    } else if (heights[x][z] > maxAllowed) {
                        heights[x][z] = maxAllowed;
                    }
                }
            }
        }
    }

    private boolean reshapeBlendColumn(World world, int x, int z, int targetY) {
        if (targetY <= 0 || targetY >= world.getActualHeight() - 1) {
            return false;
        }

        int currentTopY = findTerrainSurfaceY(world, x, z);
        if (currentTopY <= 0) {
            return false;
        }
        if (hasWaterInBlendRange(world, x, z, currentTopY, targetY)) {
            return false;
        }

        clearNonTerrainAbove(world, x, z, targetY);

        if (currentTopY < targetY) {
            for (int y = currentTopY + 1; y <= targetY; y++) {
                Block fill = y == targetY ? Blocks.grass : Blocks.dirt;
                world.setBlock(x, y, z, fill, 0, 2);
            }
        } else if (currentTopY > targetY) {
            for (int y = currentTopY; y > targetY; y--) {
                world.setBlock(x, y, z, Blocks.air, 0, 2);
            }
        }

        ensureSoilCap(world, x, targetY, z);
        return world.getBlock(x, targetY, z) == Blocks.grass && world.isAirBlock(x, targetY + 1, z);
    }

    private boolean hasWaterInBlendRange(World world, int x, int z, int currentTopY, int targetY) {
        int scanTopY = Math.max(world.getHeightValue(x, z) - 1, targetY);
        for (int y = Math.max(1, currentTopY + 1); y <= scanTopY; y++) {
            Block block = world.getBlock(x, y, z);
            if (block != null) {
                Material material = block.getMaterial();
                if (material != null && material.isLiquid()) {
                    return true;
                }
            }
        }
        return false;
    }

    private void clearNonTerrainAbove(World world, int x, int z, int targetY) {
        int maxY = world.getHeightValue(x, z) - 1;
        for (int y = maxY; y > targetY; y--) {
            Block block = world.getBlock(x, y, z);
            if (block == null || block.isAir(world, x, y, z)) {
                continue;
            }

            if (isBlendReplaceable(block)) {
                world.setBlock(x, y, z, Blocks.air, 0, 2);
            }
        }
    }

    private void ensureSoilCap(World world, int x, int y, int z) {
        if (y <= 0) {
            return;
        }

        if (world.isAirBlock(x, y - 1, z)) {
            world.setBlock(x, y - 1, z, Blocks.dirt, 0, 2);
        }

        Block topBlock = world.getBlock(x, y, z);
        if (topBlock == Blocks.dirt) {
            world.setBlock(x, y, z, Blocks.grass, 0, 2);
        } else if (topBlock != Blocks.grass) {
            world.setBlock(x, y, z, Blocks.grass, 0, 2);
        }
    }

    private int findTerrainSurfaceY(World world, int x, int z) {
        int y = world.getHeightValue(x, z) - 1;
        while (y > 0) {
            Block block = world.getBlock(x, y, z);
            if (block == null || block.isAir(world, x, y, z) || isBlendReplaceable(block)) {
                y--;
                continue;
            }
            return y;
        }
        return -1;
    }

    private static boolean isBlendReplaceable(Block block) {
        if (block == null) {
            return true;
        }
        if (block == Blocks.leaves || block == Blocks.leaves2 || block == Blocks.log || block == Blocks.log2) {
            return true;
        }

        Material material = block.getMaterial();
        return !material.isSolid()
                || material == Material.plants
                || material == Material.vine
                || material == Material.web
                || material == Material.leaves;
    }

    private static boolean isFloatingFoliage(Block block) {
        if (block == WheatfieldContent.wheatfieldBarley) {
            return true;
        }
        return isBlendReplaceable(block);
    }

    private static int interpolateHeight(int from, int to, double weight) {
        return (int) Math.round(from + (to - from) * weight);
    }

    private static double smoothStep(double value) {
        return value * value * (3.0D - 2.0D * value);
    }

    private static double clamp01(double value) {
        if (value < 0.0D) {
            return 0.0D;
        }
        if (value > 1.0D) {
            return 1.0D;
        }
        return value;
    }

    private static final class TemplateData {
        final List<TemplateEntry> blocks;
        final List<TemplateEntry> spawnerMarkers;
        final int minX;
        final int maxX;
        final int minY;
        final int maxY;
        final int minZ;
        final int maxZ;

        private TemplateData(List<TemplateEntry> blocks, List<TemplateEntry> spawnerMarkers, int minX, int maxX, int minY, int maxY, int minZ, int maxZ) {
            this.blocks = blocks;
            this.spawnerMarkers = spawnerMarkers;
            this.minX = minX;
            this.maxX = maxX;
            this.minY = minY;
            this.maxY = maxY;
            this.minZ = minZ;
            this.maxZ = maxZ;
        }
    }

    private static final class PlacementCandidate {
        final int centerX;
        final int centerZ;
        final int originY;
        final int score;

        private PlacementCandidate(int centerX, int centerZ, int originY, int score) {
            this.centerX = centerX;
            this.centerZ = centerZ;
            this.originY = originY;
            this.score = score;
        }
    }

    private static final class ChestLootEntry {
        final ItemStack stack;
        final float chance;

        private ChestLootEntry(ItemStack stack, float chance) {
            this.stack = stack;
            this.chance = chance;
        }
    }

    private static final class TemplateEntry {
        final String kind;
        final int localX;
        final int localY;
        final int localZ;
        final String blockKey;
        final int meta;
        final int extra;

        private TemplateEntry(String kind, int localX, int localY, int localZ, String blockKey, int meta, int extra) {
            this.kind = kind;
            this.localX = localX;
            this.localY = localY;
            this.localZ = localZ;
            this.blockKey = blockKey;
            this.meta = meta;
            this.extra = extra;
        }
    }

    private static final class WitchHousePlacementData extends WorldSavedData {
        private static final String NAME = "riftflux_witch_houses";
        private static final String TAG_PLACEMENTS = "Placements";

        private final Set<String> placements = new HashSet<String>();

        private WitchHousePlacementData(String name) {
            super(name);
        }

        static WitchHousePlacementData get(World world) {
            if (world == null || world.mapStorage == null) {
                return null;
            }

            MapStorage storage = world.mapStorage;
            WitchHousePlacementData data = (WitchHousePlacementData) storage.loadData(WitchHousePlacementData.class, NAME);
            if (data == null) {
                data = new WitchHousePlacementData(NAME);
                storage.setData(NAME, data);
            }
            return data;
        }

        static boolean hasPlacementWithin(World world, int x, int z, long minDistanceSq) {
            WitchHousePlacementData data = get(world);
            if (data == null) {
                return false;
            }

            for (String entry : data.placements) {
                int[] coords = parse(entry);
                long dx = (long) coords[0] - x;
                long dz = (long) coords[1] - z;
                if (dx * dx + dz * dz < minDistanceSq) {
                    return true;
                }
            }
            return false;
        }

        static void recordPlacement(World world, int x, int z) {
            WitchHousePlacementData data = get(world);
            if (data == null) {
                return;
            }
            if (data.placements.add(key(x, z))) {
                data.markDirty();
            }
        }

        @Override
        public void readFromNBT(NBTTagCompound tag) {
            placements.clear();
            NBTTagList list = tag.getTagList(TAG_PLACEMENTS, 8);
            for (int i = 0; i < list.tagCount(); i++) {
                String entry = list.getStringTagAt(i);
                if (entry != null && !entry.isEmpty()) {
                    placements.add(entry);
                }
            }
        }

        @Override
        public void writeToNBT(NBTTagCompound tag) {
            NBTTagList list = new NBTTagList();
            for (String entry : placements) {
                list.appendTag(new NBTTagString(entry));
            }
            tag.setTag(TAG_PLACEMENTS, list);
        }

        private static String key(int x, int z) {
            return x + ":" + z;
        }

        private static int[] parse(String value) {
            String[] parts = value == null ? new String[0] : value.split(":");
            if (parts.length != 2) {
                return new int[]{0, 0};
            }

            try {
                return new int[]{Integer.parseInt(parts[0]), Integer.parseInt(parts[1])};
            } catch (NumberFormatException ignored) {
                return new int[]{0, 0};
            }
        }
    }
}
