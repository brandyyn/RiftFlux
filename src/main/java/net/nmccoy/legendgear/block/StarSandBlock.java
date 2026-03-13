/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.registry.GameRegistry
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockFalling
 *  net.minecraft.block.material.Material
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.world.World
 */
package net.nmccoy.legendgear.block;

import cpw.mods.fml.common.registry.GameRegistry;
import java.util.ArrayList;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.nmccoy.legendgear.LegendGear2;

public class StarSandBlock
extends BlockFalling {
    public StarSandBlock() {
        super(Material.sand);
        this.setBlockTextureName("legendgear:starSandAnim");
        this.setBlockName("starSand");
        this.setHardness(0.5f);
        this.setStepSound(soundTypeSand);
        this.setLightLevel(0.0f);
        this.setCreativeTab(LegendGear2.legendgearTab);
    }

    public boolean canSilkHarvest(World world, EntityPlayer player, int x, int y, int z, int metadata) {
        return true;
    }

    public void addRecipes() {
        GameRegistry.addShapelessRecipe((ItemStack)new ItemStack((Block)this), (Object[])new Object[]{new ItemStack((Block)Blocks.sand), new ItemStack((Item)LegendGear2.starDust, 1, 0)});
        GameRegistry.addShapelessRecipe((ItemStack)new ItemStack((Block)this), (Object[])new Object[]{new ItemStack((Block)Blocks.sand), new ItemStack((Item)LegendGear2.starDust, 1, 3)});
    }

    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int meta, int fortune) {
        ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
        ret.add(new ItemStack((Block)Blocks.sand));
        if (world.rand.nextInt(6) <= 2 + fortune) {
            ret.add(new ItemStack((Item)LegendGear2.starDust, 1, 0));
        }
        return ret;
    }

    public boolean renderAsNormalBlock() {
        return false;
    }

    public boolean isOpaqueCube() {
        return true;
    }
}

