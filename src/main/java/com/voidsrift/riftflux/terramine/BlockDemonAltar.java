package com.voidsrift.riftflux.terramine;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockDemonAltar extends BlockTerraModel {
    public BlockDemonAltar() {
        super(Material.rock);
        setBlockName("demon_altar");
        setBlockTextureName("riftflux:demon_altar");
        setStepSound(soundTypeStone);
        setHardness(5.0F);
        setResistance(2000.0F);
        setLightOpacity(0);
        setCreativeTab(CreativeTabs.tabDecorations);
        setBlockBounds(0.075F, 0.0F, 0.075F, 0.925F, 0.85F, 0.925F);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata) {
        return new TileEntityDemonAltar();
    }
}
