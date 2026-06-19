package com.voidsrift.riftflux.vortex.block;

import com.voidsrift.riftflux.ModConfig;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.world.IBlockAccess;

public class BlockVoidFlux extends Block {

    public BlockVoidFlux() {
        super(Material.rock);
    }

    @Override
    public int getLightValue(IBlockAccess world, int x, int y, int z) {
        return ModConfig.voidFluxLightLevel;
    }

    @Override
    public int getLightValue() {
        return ModConfig.voidFluxLightLevel;
    }
}