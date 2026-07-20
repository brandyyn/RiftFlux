package com.voidsrift.riftflux.hotsprings;

import com.voidsrift.riftflux.ModConfig;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenLakes;

import java.util.Random;

public final class BiomeGenHotSprings extends BiomeGenBase {
    private static final int GREEN_PATCHES_PER_CHUNK = 5;
    private static final int GREEN_PATCH_ATTEMPTS = 128;
    private final WorldGenAbstractTree pineTree = new WorldGenHotSpringsSpruce(false);
    private final WorldGenAbstractTree taigaTree = new WorldGenHotSpringsSpruce(true);

    public BiomeGenHotSprings(int biomeId) {
        super(biomeId);
        topBlock = Blocks.stone;
        fillerBlock = Blocks.stone;
        setColor(9371647);
        setBiomeName("Hot Springs");
        setHeight(new Height(0.2F, 0.5F));
        setTemperatureRainfall(0.5F, 0.7F);

        theBiomeDecorator.treesPerChunk = 3;
        theBiomeDecorator.grassPerChunk = -999;
        theBiomeDecorator.flowersPerChunk = 0;
    }

    @Override
    public WorldGenAbstractTree func_150567_a(Random random) {
        return random.nextInt(3) == 0 ? pineTree : taigaTree;
    }

    @Override
    public void decorate(World world, Random random, int chunkX, int chunkZ) {
        super.decorate(world, random, chunkX, chunkZ);

        for (int i = 0; i < GREEN_PATCHES_PER_CHUNK; i++) {
            int x = chunkX + random.nextInt(16) + 8;
            int z = chunkZ + random.nextInt(16) + 8;
            generateGreenPatch(world, random, x, world.getHeightValue(x, z), z);
        }

        for (int i = 0; i < ModConfig.hotSpringsLavaLakesPerChunk; i++) {
            int x = chunkX + random.nextInt(16) + 8;
            int y = nestedLakeY(random);
            int z = chunkZ + random.nextInt(16) + 8;
            new WorldGenLakes(Blocks.lava).generate(world, random, x, y, z);
        }

        for (int i = 0; i < ModConfig.hotSpringsSpringLakesPerChunk; i++) {
            int x = chunkX + random.nextInt(16) + 8;
            int y = nestedLakeY(random);
            int z = chunkZ + random.nextInt(16) + 8;
            new WorldGenLakes(HotSpringsContent.springWaterBlock).generate(world, random, x, y, z);
        }
    }

    private static int nestedLakeY(Random random) {
        return random.nextInt(random.nextInt(random.nextInt(112) + 8) + 8);
    }

    private static void generateGreenPatch(World world, Random random, int originX, int originY, int originZ) {
        int y = originY;
        while (y > 1 && world.isAirBlock(originX, y, originZ)) {
            y--;
        }

        for (int attempt = 0; attempt < GREEN_PATCH_ATTEMPTS; attempt++) {
            int x = originX + random.nextInt(8) - random.nextInt(8);
            int plantY = y + random.nextInt(4) - random.nextInt(4);
            int z = originZ + random.nextInt(8) - random.nextInt(8);
            if (!world.isAirBlock(x, plantY, z)) {
                continue;
            }

            if (world.getBlock(x, plantY - 1, z) == Blocks.stone) {
                world.setBlock(x, plantY - 1, z, Blocks.grass, 0, 2);
            }
            if (world.getBlock(x, plantY - 1, z) == Blocks.grass && random.nextInt(5) != 0) {
                int metadata = random.nextInt(4) == 0 ? 2 : 1;
                world.setBlock(x, plantY, z, Blocks.tallgrass, metadata, 2);
            }
        }
    }
}
