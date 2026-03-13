/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.block.Block
 *  net.minecraft.block.material.Material
 *  net.minecraft.world.IBlockAccess
 */
package net.nmccoy.legendgear.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.world.IBlockAccess;
import net.nmccoy.legendgear.LegendGear2;
import net.nmccoy.legendgear.client.ClientProxy;

public class StarglassBlock
extends Block {
    public StarglassBlock() {
        super(Material.glass);
        this.setBlockTextureName("legendgear:starglassAnim");
        this.setLightLevel(0.2f);
        this.setBlockName("starGlass");
        this.setStepSound(soundTypeGlass);
        this.setCreativeTab(LegendGear2.legendgearTab);
        this.setHardness(1.5f);
        this.setHarvestLevel("pickaxe", 1);
    }

    public boolean renderAsNormalBlock() {
        return false;
    }

    public boolean isOpaqueCube() {
        return false;
    }

    public int getRenderBlockPass() {
        return 1;
    }

    @SideOnly(value=Side.CLIENT)
    public int getRenderType() {
        return ClientProxy.starglassRenderID;
    }

    @SideOnly(value=Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockAccess p_149646_1_, int p_149646_2_, int p_149646_3_, int p_149646_4_, int p_149646_5_) {
        Block block = p_149646_1_.getBlock(p_149646_2_, p_149646_3_, p_149646_4_);
        if (block == this) {
            return false;
        }
        return super.shouldSideBeRendered(p_149646_1_, p_149646_2_, p_149646_3_, p_149646_4_, p_149646_5_);
    }
}

