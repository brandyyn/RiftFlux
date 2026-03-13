package com.voidsrift.riftflux.terramine;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class BlockMagicIce extends Block {
    public BlockMagicIce() {
        super(Material.ice);
        this.setBlockName("magic_ice");
        this.setBlockTextureName("riftflux:magic_ice");
        this.setHardness(0.4F);
        this.setStepSound(soundTypeGlass);
        this.setLightOpacity(0);
        this.setCreativeTab(net.minecraft.creativetab.CreativeTabs.tabBlock);
        this.slipperiness = 0.98F;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getRenderBlockPass() {
        return 1;
    }
}
