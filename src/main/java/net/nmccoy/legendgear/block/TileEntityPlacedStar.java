package net.nmccoy.legendgear.block;

import net.minecraft.tileentity.TileEntity;

public class TileEntityPlacedStar extends TileEntity {

    @Override
    public boolean shouldRenderInPass(int pass) {
        return pass == 1;
    }

    @Override
    public double getMaxRenderDistanceSquared() {
        return 65536.0;
    }

    @Override
    public boolean canUpdate() {
        return false;
    }
}
