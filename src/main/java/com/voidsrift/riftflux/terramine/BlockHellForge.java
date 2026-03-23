package com.voidsrift.riftflux.terramine;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockHellForge extends BlockTerraModel {
    public BlockHellForge() {
        super(Material.rock);
        setBlockName("hellforge");
        setBlockTextureName("riftflux:hellforge");
        setStepSound(soundTypeStone);
        setHardness(3.5F);
        setResistance(17.5F);
        setLightOpacity(0);
        setLightLevel(0.875F);
        setCreativeTab(CreativeTabs.tabDecorations);
        setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.8F, 1.0F);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata) {
        return new TileEntityHellForge();
    }
}
