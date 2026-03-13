/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.BlockContainer
 *  net.minecraft.block.material.Material
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.world.World
 */
package net.nmccoy.legendgear.block;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.nmccoy.legendgear.LegendGear2;
import net.nmccoy.legendgear.block.TileEntityRitual;

public class RitualBlock
extends BlockContainer {
    public RitualBlock() {
        super(Material.rock);
        this.setHardness(1.5f);
        this.setHarvestLevel("pickaxe", 1);
        this.setStepSound(soundTypePiston);
        this.setBlockName("ritualBlock");
        this.setBlockTextureName("legendgear:ritualBlock");
        this.setCreativeTab(LegendGear2.legendgearTab);
    }

    public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
        return new TileEntityRitual();
    }

    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int p_149727_6_, float p_149727_7_, float p_149727_8_, float p_149727_9_) {
        TileEntityRitual teRitual = (TileEntityRitual)world.getTileEntity(x, y, z);
        return false;
    }
}

