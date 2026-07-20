package com.voidsrift.riftflux.waterlogging;

import com.voidsrift.riftflux.ModConfig;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.IBlockAccess;

public final class RiftFluxFluidloggedLookup {
    private static final int GEOSTRATA_CRYSTAL_ORIENTATION_MASK = 14;

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
        Block fluid = getFluidBlock(world, x, y, z);
        if (fluid != null) {
            return fluid;
        }
        Block block = world.getBlock(x, y, z);
        return isSupportedFluidBlock(world, x, y, z, block) ? Blocks.water : block;
    }

    public static int getSupportedFluidMetaOrBlockMeta(IBlockAccess world, int x, int y, int z,
                                                        int defaultFluidMeta) {
        if (getFluidBlock(world, x, y, z) != null) {
            return defaultFluidMeta;
        }
        Block block = world.getBlock(x, y, z);
        return isSupportedFluidBlock(world, x, y, z, block)
                ? defaultFluidMeta : world.getBlockMetadata(x, y, z);
    }

    public static Block getFluidBlock(IBlockAccess world, int x, int y, int z) {
        return world instanceof RiftFluxFluidloggedAccess
                ? ((RiftFluxFluidloggedAccess) world).riftflux$getFluidBlock(x, y, z)
                : null;
    }

    public static Block getSupportedFluidBlock(IBlockAccess world, int x, int y, int z) {
        if (world == null) {
            return null;
        }

        Block block = world.getBlock(x, y, z);
        return isSupportedFluidBlock(world, x, y, z, block) ? Blocks.water : null;
    }

    private static boolean isSupportedFluidBlock(IBlockAccess world, int x, int y, int z, Block block) {
        if (block == Blocks.reeds) {
            return ModConfig.allowSugarcaneInWater && hasAdjacentWater(world, x, y, z);
        }

        return ModConfig.fixGeoStrataCrystalSpikeWaterlogging
                && isGeoStrataCrystalSpike(block, world.getBlockMetadata(x, y, z))
                && hasAdjacentWater(world, x, y, z);
    }

    public static boolean hasSupportedFluidBlock(IBlockAccess world, int x, int y, int z) {
        return getSupportedFluidBlock(world, x, y, z) != null;
    }

    private static boolean hasAdjacentWater(IBlockAccess world, int x, int y, int z) {
        return isRealWater(world.getBlock(x, y - 1, z))
                || isRealWater(world.getBlock(x, y + 1, z))
                || isRealWater(world.getBlock(x - 1, y, z))
                || isRealWater(world.getBlock(x + 1, y, z))
                || isRealWater(world.getBlock(x, y, z - 1))
                || isRealWater(world.getBlock(x, y, z + 1));
    }

    private static boolean isGeoStrataCrystalSpike(Block block, int meta) {
        return block != null
                && block instanceof RiftFluxCrystalSpikeAccess
                && (meta & ~GEOSTRATA_CRYSTAL_ORIENTATION_MASK) == 0;
    }

    private static boolean isRealWater(Block block) {
        return block != null && block.getMaterial() == Material.water;
    }
}
