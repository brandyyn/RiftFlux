/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.BlockContainer
 *  net.minecraft.block.material.Material
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.MovingObjectPosition
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 */
package net.nmccoy.legendgear.block;

import java.util.Random;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.nmccoy.legendgear.LegendGear2;
import net.nmccoy.legendgear.block.TileEntityStarstone;

public class InfusedStarstoneBlock
extends BlockContainer {
    public InfusedStarstoneBlock() {
        super(Material.iron);
        this.setBlockTextureName("legendgear:starstoneBlockAnim");
        this.setLightLevel(1.0f);
        this.setBlockName("infusedStarstoneBlock");
        this.setHardness(5.0f);
        this.setHarvestLevel("pickaxe", 2);
    }

    public int getRenderType() {
        return 0;
    }

    public boolean renderAsNormalBlock() {
        return false;
    }

    public boolean isOpaqueCube() {
        return true;
    }

    public TileEntity createNewTileEntity(World var1, int par2) {
        return new TileEntityStarstone();
    }

    public boolean isBeaconBase(IBlockAccess worldObj, int x, int y, int z, int beaconX, int beaconY, int beaconZ) {
        return true;
    }

    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z) {
        return new ItemStack((Item)LegendGear2.starDust, 1, 5);
    }

    public Item getItemDropped(int p_149650_1_, Random p_149650_2_, int p_149650_3_) {
        return LegendGear2.starDust;
    }

    public int damageDropped(int p_149692_1_) {
        return 5;
    }
}

