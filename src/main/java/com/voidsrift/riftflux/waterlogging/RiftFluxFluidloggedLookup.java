package com.voidsrift.riftflux.waterlogging;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.compat.fluidlogged.RiftFluxFluidloggedCompat;
import gravestone.block.BlockGSGraveStone;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

public final class RiftFluxFluidloggedLookup {
    private static final int GEOSTRATA_CRYSTAL_ORIENTATION_MASK = 14;
    private static final boolean FLUIDLOGGED_AVAILABLE = hasClass("mega.fluidlogged.api.FLBlockAccess");

    private RiftFluxFluidloggedLookup() {
    }

    public static Block getFluidOrBlock(IBlockAccess world, int x, int y, int z) {
        Block fluid = getFluidBlock(world, x, y, z);
        return fluid != null ? fluid : world.getBlock(x, y, z);
    }

    public static int getFluidMetaOrBlockMeta(IBlockAccess world, int x, int y, int z, int defaultFluidMeta) {
        return getFluidBlock(world, x, y, z) != null
                ? defaultFluidMeta : world.getBlockMetadata(x, y, z);
    }

    public static Block getSupportedFluidOrBlock(IBlockAccess world, int x, int y, int z) {
        Block fluid = getSupportedFluidBlock(world, x, y, z);
        return fluid != null ? fluid : world.getBlock(x, y, z);
    }

    public static int getSupportedFluidMetaOrBlockMeta(IBlockAccess world, int x, int y, int z,
                                                        int defaultFluidMeta) {
        return getSupportedFluidBlock(world, x, y, z) != null
                ? defaultFluidMeta : world.getBlockMetadata(x, y, z);
    }

    public static Block getFluidBlock(IBlockAccess world, int x, int y, int z) {
        if (world instanceof RiftFluxFluidloggedAccess) {
            Block fluid = ((RiftFluxFluidloggedAccess) world).riftflux$getFluidBlock(x, y, z);
            if (fluid != null) {
                return fluid;
            }
        }
        return getStoredFluidBlock(world, x, y, z);
    }

    public static Block getSupportedFluidBlock(IBlockAccess world, int x, int y, int z) {
        if (world == null) {
            return null;
        }

        Block block = world.getBlock(x, y, z);
        if (!isSupportedFluidHost(block, world.getBlockMetadata(x, y, z))) {
            return null;
        }

        Block storedFluid = getStoredFluidBlock(world, x, y, z);
        return storedFluid != null ? storedFluid : findAdjacentFluidBlock(world, x, y, z);
    }

    public static boolean hasStoredFluidBlock(IBlockAccess world, int x, int y, int z) {
        return getStoredFluidBlock(world, x, y, z) != null;
    }

    public static boolean isSupportedFluidHost(Block block, int metadata) {
        if (block == Blocks.reeds) {
            return ModConfig.allowSugarcaneInWater;
        }
        if (block instanceof BlockGSGraveStone) {
            return ModConfig.enableGravestoneModule
                    && ModConfig.waterlogGravestones;
        }

        return ModConfig.fixGeoStrataCrystalSpikeWaterlogging
                && isGeoStrataCrystalSpike(block, metadata);
    }

    public static boolean hasSupportedFluidBlock(IBlockAccess world, int x, int y, int z) {
        return getSupportedFluidBlock(world, x, y, z) != null;
    }

    public static boolean isFluidBlock(Block block) {
        return block != null
                && (FluidRegistry.lookupFluidForBlock(block) != null || block.getMaterial().isLiquid());
    }

    public static boolean isSameFluid(Block first, Block second) {
        if (first == second) {
            return true;
        }
        if (first == null || second == null) {
            return false;
        }
        Fluid firstFluid = FluidRegistry.lookupFluidForBlock(first);
        Fluid secondFluid = FluidRegistry.lookupFluidForBlock(second);
        return firstFluid != null && firstFluid == secondFluid;
    }

    private static Block getStoredFluidBlock(IBlockAccess world, int x, int y, int z) {
        return FLUIDLOGGED_AVAILABLE
                ? RiftFluxFluidloggedCompat.getStoredFluidBlock(world, x, y, z)
                : null;
    }

    private static Block findAdjacentFluidBlock(IBlockAccess world, int x, int y, int z) {
        Block fluid = normalizeFluidBlock(world.getBlock(x, y + 1, z));
        if (fluid != null) {
            return fluid;
        }
        fluid = normalizeFluidBlock(world.getBlock(x - 1, y, z));
        if (fluid != null) {
            return fluid;
        }
        fluid = normalizeFluidBlock(world.getBlock(x + 1, y, z));
        if (fluid != null) {
            return fluid;
        }
        fluid = normalizeFluidBlock(world.getBlock(x, y, z - 1));
        if (fluid != null) {
            return fluid;
        }
        fluid = normalizeFluidBlock(world.getBlock(x, y, z + 1));
        return fluid != null ? fluid : normalizeFluidBlock(world.getBlock(x, y - 1, z));
    }

    private static Block normalizeFluidBlock(Block block) {
        if (!isFluidBlock(block)) {
            return null;
        }
        Fluid fluid = FluidRegistry.lookupFluidForBlock(block);
        return fluid != null && fluid.getBlock() != null ? fluid.getBlock() : block;
    }

    private static boolean isGeoStrataCrystalSpike(Block block, int meta) {
        return block != null
                && block instanceof RiftFluxCrystalSpikeAccess
                && (meta & ~GEOSTRATA_CRYSTAL_ORIENTATION_MASK) == 0;
    }

    private static boolean hasClass(String className) {
        try {
            return RiftFluxFluidloggedLookup.class.getClassLoader()
                    .getResource(className.replace('.', '/') + ".class") != null;
        } catch (Throwable ignored) {
            return false;
        }
    }
}
