/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.IWorldGenerator
 *  net.minecraft.block.Block
 *  net.minecraft.init.Blocks
 *  net.minecraft.init.Items
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.tileentity.TileEntityChest
 *  net.minecraft.world.World
 *  net.minecraft.world.chunk.IChunkProvider
 */
package zairus.worldexplorer.core.world.gen.feature;

import cpw.mods.fml.common.IWorldGenerator;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import zairus.worldexplorer.archery.items.WEArcheryItems;

public class WorldGenBurial
implements IWorldGenerator {
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
        if (world.provider.dimensionId == 0 && random.nextInt(100) > 80) {
            int x = random.nextInt(16) + chunkX * 16;
            int y = random.nextInt(128);
            int z = random.nextInt(16) + chunkZ * 16;
            boolean generate = true;
            if (world.getBlock(x, y, z).getMaterial().isSolid()) {
                int depth = 6;
                for (int iy = y + 1; iy < y + depth; ++iy) {
                    if (world.getBlock(x, iy, z).getMaterial().isSolid()) continue;
                    generate = false;
                }
                if (!world.isAirBlock(x, y + depth, z)) {
                    generate = false;
                }
                if (generate) {
                    world.setBlock(x, y + depth - 1, z, Blocks.dirt, 1, 1);
                    world.setBlock(x + 1, y + depth - 1, z, Blocks.dirt, 1, 1);
                    world.setBlock(x, y, z, (Block)Blocks.chest, 0, 2);
                    world.setBlock(x + 1, y, z, (Block)Blocks.chest, 0, 2);
                    TileEntityChest tileentitychest = (TileEntityChest)world.getTileEntity(x, y, z);
                    if (tileentitychest != null) {
                        tileentitychest.func_145976_a("Explorer's Tomb");
                        for (int ic = 0; ic < tileentitychest.getSizeInventory(); ++ic) {
                            int loot = random.nextInt(100);
                            if (loot > 80) {
                                tileentitychest.setInventorySlotContents(ic, new ItemStack(Items.bone, random.nextInt(5) + 1));
                            }
                            if (loot > 85) {
                                tileentitychest.setInventorySlotContents(ic, new ItemStack(Items.rotten_flesh, random.nextInt(5) + 1));
                            }
                            if (loot > 95) {
                                tileentitychest.setInventorySlotContents(ic, new ItemStack(Items.book, random.nextInt(2) + 1));
                            }
                            if (loot <= 98) continue;
                            tileentitychest.setInventorySlotContents(ic, new ItemStack((Item)WEArcheryItems.boomerang, 1));
                        }
                    }
                }
            }
        }
    }
}

