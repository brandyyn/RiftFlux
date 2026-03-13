/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.IWorldGenerator
 *  net.minecraft.block.Block
 *  net.minecraft.init.Blocks
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 *  net.minecraft.world.chunk.IChunkProvider
 */
package net.nmccoy.legendgear;

import cpw.mods.fml.common.IWorldGenerator;
import java.util.ArrayList;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.nmccoy.legendgear.LegendGear2;

public class StarwellGenerator
implements IWorldGenerator {
    static final int INV_CHANCE = 40;
    public static ArrayList<Block> replaceable_ground;

    public StarwellGenerator() {
        replaceable_ground = new ArrayList();
        replaceable_ground.add((Block)Blocks.grass);
        replaceable_ground.add(Blocks.dirt);
        replaceable_ground.add((Block)Blocks.sand);
        replaceable_ground.add(Blocks.gravel);
        replaceable_ground.add((Block)Blocks.mycelium);
        replaceable_ground.add(Blocks.stone);
    }

    private boolean isValidChunk(int x, int z) {
        return (x + 2 * z) % 5 == 0;
    }

    private boolean suitableSurface(World world, int x, int y, int z) {
        boolean ok = true;
        for (int i = 0; i < 9; ++i) {
            Block ground;
            int dx = i % 3 - 1;
            int dz = i / 3 - 1;
            if (world.getPrecipitationHeight(x + dx, z + dz) != y) {
                ok = false;
            }
            if (!world.getBlock(x + dx, y, z + dz).isReplaceable((IBlockAccess)world, x + dx, y, z + dz)) {
                ok = false;
            }
            if (!replaceable_ground.contains(ground = world.getBlock(x + dx, y - 1, z + dz))) {
                ok = false;
            }
            if (!ok) break;
        }
        return ok;
    }

    public void buildWell(World world, int x, int y, int z) {
        for (int i = 0; i < 9; ++i) {
            int dx = i % 3 - 1;
            int dz = i / 3 - 1;
            if (dx != 0 || dz != 0) {
                world.setBlock(x + dx, y - 1, z + dz, (Block)LegendGear2.starwellFrameBlock);
            }
            world.setBlock(x + dx, y, z + dz, Blocks.air);
        }
        world.setBlock(x, y - 1, z, Blocks.air);
        world.setBlock(x - 1, y - 2, z, (Block)LegendGear2.starwellFrameBlock);
        world.setBlock(x + 1, y - 2, z, (Block)LegendGear2.starwellFrameBlock);
        world.setBlock(x, y - 2, z - 1, (Block)LegendGear2.starwellFrameBlock);
        world.setBlock(x, y - 2, z + 1, (Block)LegendGear2.starwellFrameBlock);
        world.setBlock(x, y - 2, z, (Block)LegendGear2.starwellBlock, 1, 3);
    }

    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
        if (!this.isValidChunk(chunkX, chunkZ)) {
            return;
        }
        if (random.nextInt(40) != 0) {
            return;
        }
        int tx = 0;
        int ty = 0;
        int tz = 0;
        boolean success = false;
        for (int attempt = 0; attempt < LegendGear2.CONFIG_MAX_STARWELL_ATTEMPTS && !success; ++attempt) {
            tx = random.nextInt(16) + chunkX * 16;
            if (!this.suitableSurface(world, tx, ty = world.getPrecipitationHeight(tx, tz = random.nextInt(16) + chunkZ * 16), tz)) continue;
            this.buildWell(world, tx, ty, tz);
            success = true;
        }
    }
}

