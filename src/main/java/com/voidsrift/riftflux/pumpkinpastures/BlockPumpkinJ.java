package com.voidsrift.riftflux.pumpkinpastures;

import net.minecraft.block.BlockPumpkin;
import net.minecraft.creativetab.CreativeTabs;

public class BlockPumpkinJ extends BlockPumpkin {
    public BlockPumpkinJ() {
        super(true);
        setBlockName("pumpkin_j");
        setBlockTextureName("riftflux:pumpkinpastures/pumpkin_j");
        setStepSound(soundTypeWood);
        setHardness(1.0F);
        setLightLevel(1.0F);
        setCreativeTab(CreativeTabs.tabBlock);
    }
}
