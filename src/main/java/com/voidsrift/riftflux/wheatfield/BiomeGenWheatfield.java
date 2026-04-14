package com.voidsrift.riftflux.wheatfield;

import com.voidsrift.riftflux.ModConfig;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenTallGrass;
import net.minecraft.world.gen.feature.WorldGenerator;

import java.util.Random;

public class BiomeGenWheatfield extends BiomeGenBase {
    private static final int PASTURE_GRASS_COLOR = 13166666;
    private static final Height WHEATFIELD_HEIGHT = new Height(0.0035F, 0.0008F);
    private static final int TREE_BARLEY_RADIUS = 2;
    private static final int TREE_GROUND_SEARCH_DEPTH = 3;
    private static final int EDGE_SAMPLE_RADIUS = 3;
    private static final float EDGE_BLEND_START = 0.42F;

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
    public void decorate(World world, Random random, int chunkX, int chunkZ) {
        super.decorate(world, random, chunkX, chunkZ);
        seedNaturalBarley(world, random, chunkX, chunkZ);
        seedBarleyAtTreeBases(world, chunkX, chunkZ);
    }

    @Override
    public WorldGenAbstractTree func_150567_a(Random random) {
        return worldGeneratorBigTree;
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

    private void seedBarleyAtTreeBases(World world, int chunkX, int chunkZ) {
        for (int x = chunkX; x < chunkX + 16; x++) {
            for (int z = chunkZ; z < chunkZ + 16; z++) {
                int treeBaseY = findTreeBaseY(world, x, z);

                if (treeBaseY >= 0) {
                    seedBarleyAroundTree(world, x, treeBaseY, z);
                }
            }
        }
    }

    private void seedNaturalBarley(World world, Random random, int chunkX, int chunkZ) {
        int configured = Math.max(0, ModConfig.wheatfieldBarleyPerChunk);
        if (configured <= 0) {
            return;
        }

        int attempts = configured * 5;
        for (int i = 0; i < attempts; i++) {
            int x = chunkX + random.nextInt(16);
            int z = chunkZ + random.nextInt(16);
            if (world.getBiomeGenForCoords(x, z) != this) {
                continue;
            }

            float edgeFactor = computeBiomeCoreFactor(world, x, z);
            if (edgeFactor <= 0.0F || random.nextFloat() > edgeFactor) {
                continue;
            }

            int y = findSurfaceSoilY(world, x, z);
            if (y < 0) {
                continue;
            }

            tryPlaceBarley(world, x, y + 1, z);
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

    private float computeBiomeCoreFactor(World world, int x, int z) {
        int total = 0;
        int wheatfieldCount = 0;

        for (int dx = -EDGE_SAMPLE_RADIUS; dx <= EDGE_SAMPLE_RADIUS; dx++) {
            for (int dz = -EDGE_SAMPLE_RADIUS; dz <= EDGE_SAMPLE_RADIUS; dz++) {
                total++;
                if (world.getBiomeGenForCoords(x + dx, z + dz) == this) {
                    wheatfieldCount++;
                }
            }
        }

        if (total <= 0) {
            return 0.0F;
        }

        float ratio = (float) wheatfieldCount / (float) total;
        if (ratio <= EDGE_BLEND_START) {
            return 0.0F;
        }

        float normalized = (ratio - EDGE_BLEND_START) / (1.0F - EDGE_BLEND_START);
        return normalized * normalized;
    }

    private int findTreeBaseY(World world, int x, int z) {
        int topY = Math.min(world.getHeightValue(x, z) + 6, world.getActualHeight() - 2);

        for (int y = topY; y > 0; y--) {
            Block block = world.getBlock(x, y, z);
            Block below = world.getBlock(x, y - 1, z);

            if (isLogBlock(block) && (below == Blocks.grass || below == Blocks.dirt)) {
                return y;
            }
        }

        return -1;
    }

    private void seedBarleyAroundTree(World world, int treeX, int treeY, int treeZ) {
        for (int dx = -TREE_BARLEY_RADIUS; dx <= TREE_BARLEY_RADIUS; dx++) {
            for (int dz = -TREE_BARLEY_RADIUS; dz <= TREE_BARLEY_RADIUS; dz++) {
                if (dx == 0 && dz == 0) {
                    continue;
                }

                if (dx * dx + dz * dz > TREE_BARLEY_RADIUS * TREE_BARLEY_RADIUS) {
                    continue;
                }

                int groundY = findGroundYNearTree(world, treeX + dx, treeY - 1, treeZ + dz);

                if (groundY >= 0) {
                    tryPlaceBarley(world, treeX + dx, groundY + 1, treeZ + dz);
                }
            }
        }
    }

    private int findGroundYNearTree(World world, int x, int startY, int z) {
        int maxY = Math.min(startY + 1, world.getActualHeight() - 2);
        int minY = Math.max(1, startY - TREE_GROUND_SEARCH_DEPTH);

        for (int y = maxY; y >= minY; y--) {
            Block ground = world.getBlock(x, y, z);

            if ((ground == Blocks.grass || ground == Blocks.dirt) && world.isAirBlock(x, y + 1, z)) {
                return y;
            }
        }

        return -1;
    }

    private void tryPlaceBarley(World world, int x, int y, int z) {
        Block block = world.getBlock(x, y, z);

        if (!block.isReplaceable(world, x, y, z) && block != barleyBlock) {
            return;
        }

        if (barleyBlock.canBlockStay(world, x, y, z)) {
            world.setBlock(x, y, z, barleyBlock, 0, 2);
        }
    }

    private static boolean isLogBlock(Block block) {
        return block == Blocks.log || block == Blocks.log2;
    }
}
