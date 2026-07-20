package com.voidsrift.riftflux.hotsprings;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenTaiga1;
import net.minecraft.world.gen.feature.WorldGenTaiga2;

import java.util.Random;

final class WorldGenHotSpringsSpruce extends WorldGenAbstractTree {
    private final WorldGenAbstractTree delegate;

    WorldGenHotSpringsSpruce(boolean broadCanopy) {
        super(false);
        delegate = broadCanopy ? new WorldGenTaiga2(false) : new WorldGenTaiga1();
    }

    @Override
    public boolean generate(World world, Random random, int x, int y, int z) {
        Block originalSoil = world.getBlock(x, y - 1, z);
        boolean convertedStone = originalSoil == Blocks.stone;
        if (convertedStone) {
            world.setBlock(x, y - 1, z, Blocks.dirt, 0, 2);
        }

        boolean generated = delegate.generate(world, random, x, y, z);
        if (!generated && convertedStone && world.getBlock(x, y - 1, z) == Blocks.dirt) {
            world.setBlock(x, y - 1, z, Blocks.stone, 0, 2);
        }
        return generated;
    }
}
