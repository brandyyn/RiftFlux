/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.IWorldGenerator
 *  net.minecraft.block.Block
 *  net.minecraft.init.Blocks
 *  net.minecraft.world.World
 *  net.minecraft.world.chunk.IChunkProvider
 *  net.minecraftforge.common.util.ForgeDirection
 */
package net.nmccoy.legendgear;

import cpw.mods.fml.common.IWorldGenerator;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.common.util.ForgeDirection;
import net.nmccoy.legendgear.LegendGear2;
import net.nmccoy.legendgear.block.AzuriteOreBlock;

public class AzuriteGenerator
implements IWorldGenerator {
    public static final int attempts = 1;
    public static final int minAltitude = 100;
    public static final int maxAltitude = 110;
    public static final float density = 0.5f;

    private boolean canBlockBreathe(World world, int x, int y, int z) {
        if (world.isAirBlock(x + 1, y, z)) {
            return true;
        }
        if (world.isAirBlock(x - 1, y, z)) {
            return true;
        }
        if (world.isAirBlock(x, y + 1, z)) {
            return true;
        }
        if (world.isAirBlock(x, y - 1, z)) {
            return true;
        }
        if (world.isAirBlock(x, y, z + 1)) {
            return true;
        }
        return world.isAirBlock(x, y, z - 1);
    }

    private void generateOres(Random random, World world, int coreX, int coreY, int coreZ, ForgeDirection airDirection) {
        int y;
        AzuriteOreBlock azurineOre = LegendGear2.azuriteOreBlock;
        Block azurineCrystal = Blocks.air;
        int r = 2;
        int h = 2;
        if (airDirection == ForgeDirection.EAST || airDirection == ForgeDirection.WEST) {
            int x = coreX;
            for (y = coreY - h; y <= coreY + h; ++y) {
                for (int z = coreZ - r; z <= coreZ + r; ++z) {
                    if (!(random.nextFloat() < 0.5f) || world.getBlock(x, y, z) != Blocks.stone || !world.isAirBlock(x + airDirection.offsetX, y, z)) continue;
                    world.setBlock(x, y, z, (Block)azurineOre);
                    world.setBlock(x + airDirection.offsetX, y, z, azurineCrystal);
                }
            }
        }
        if (airDirection == ForgeDirection.NORTH || airDirection == ForgeDirection.SOUTH) {
            int z = coreZ;
            for (y = coreY - h; y <= coreY + h; ++y) {
                for (int x = coreX - r; x <= coreX + r; ++x) {
                    if (!(random.nextFloat() < 0.5f) || world.getBlock(x, y, z) != Blocks.stone || !world.isAirBlock(x, y, z + airDirection.offsetZ)) continue;
                    world.setBlock(x, y, z, (Block)azurineOre);
                    world.setBlock(x, y, z + airDirection.offsetZ, azurineCrystal);
                }
            }
        }
    }

    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
        if (world.provider.dimensionId == 0) {
            for (int i = 0; i < 1; ++i) {
                Block b;
                Block a;
                int z;
                int x;
                int localZ;
                int y = random.nextInt(11) + 100;
                int localX = random.nextInt(16);
                for (localZ = 0; localZ < 16; ++localZ) {
                    x = localX + chunkX * 16;
                    z = localZ + chunkZ * 16;
                    a = world.getBlock(x, y, z);
                    b = world.getBlock(x, y, z + 1);
                    if (a == Blocks.stone && b == Blocks.air) {
                        this.generateOres(random, world, x, y, z, ForgeDirection.SOUTH);
                    }
                    if (a != Blocks.air || b != Blocks.stone) continue;
                    this.generateOres(random, world, x, y, z + 1, ForgeDirection.NORTH);
                }
                localZ = random.nextInt(16);
                for (localX = 0; localX < 16; ++localX) {
                    x = localX + chunkX * 16;
                    z = localZ + chunkZ * 16;
                    a = world.getBlock(x, y, z);
                    b = world.getBlock(x + 1, y, z);
                    if (a == Blocks.stone && b == Blocks.air) {
                        this.generateOres(random, world, x, y, z, ForgeDirection.EAST);
                    }
                    if (a != Blocks.air || b != Blocks.stone) continue;
                    this.generateOres(random, world, x + 1, y, z, ForgeDirection.WEST);
                }
            }
        }
    }
}

