/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemBlockWithMetadata
 *  net.minecraft.item.ItemStack
 */
package net.nmccoy.legendgear.block;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlockWithMetadata;
import net.minecraft.item.ItemStack;
import net.nmccoy.legendgear.LegendGear2;

public class LgItemBlockMeta
extends ItemBlockWithMetadata {
    public LgItemBlockMeta(Block block) {
        super(block, block);
    }

    public String getUnlocalizedName(ItemStack stack) {
        if (Block.getBlockFromItem((Item)stack.getItem()) == LegendGear2.struckGroundBlock) {
            if (stack.getItemDamage() == 0) {
                return "tile.struckSand";
            }
            return "tile.struckDirt";
        }
        if (Block.getBlockFromItem((Item)stack.getItem()) == LegendGear2.starwellBlock) {
            if (stack.getItemDamage() == 0) {
                return "tile.starwellCore";
            }
            return "tile.starwellCoreActive";
        }
        return "tile.mistake";
    }
}

