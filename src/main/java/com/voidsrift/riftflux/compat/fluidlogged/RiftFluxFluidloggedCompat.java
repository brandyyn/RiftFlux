package com.voidsrift.riftflux.compat.fluidlogged;

import com.voidsrift.riftflux.waterlogging.RiftFluxFluidloggedLookup;
import mega.fluidlogged.api.FLBlockAccess;
import mega.fluidlogged.api.world.WorldDriver;
import net.minecraft.block.Block;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fluids.Fluid;

public final class RiftFluxFluidloggedCompat {
    private static boolean initialized;

    private RiftFluxFluidloggedCompat() {
    }

    public static void init() {
        if (initialized) {
            return;
        }
        initialized = true;
        WorldDriver.register(new WorldDriver() {
            @Override
            public boolean canBeFluidLogged(Block block, int metadata, Fluid fluid) {
                return fluid != null
                        && fluid.getBlock() != null
                        && RiftFluxFluidloggedLookup.isSupportedFluidHost(block, metadata);
            }
        });
    }

    public static Block getStoredFluidBlock(IBlockAccess world, int x, int y, int z) {
        if (!(world instanceof FLBlockAccess)) {
            return null;
        }
        Fluid fluid = ((FLBlockAccess) world).fl$getFluid(x, y, z);
        return fluid != null ? fluid.getBlock() : null;
    }
}
