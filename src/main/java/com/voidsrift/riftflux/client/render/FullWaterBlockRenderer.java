package com.voidsrift.riftflux.client.render;

import com.voidsrift.riftflux.waterlogging.RiftFluxFluidloggedAccess;
import com.voidsrift.riftflux.waterlogging.RiftFluxFluidloggedLookup;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.util.ForgeDirection;

public final class FullWaterBlockRenderer {
    private FullWaterBlockRenderer() {
    }

    public static boolean render(RenderBlocks renderer, WaterloggedBlockAccess waterAccess, int x, int y, int z) {
        IBlockAccess previous = renderer.blockAccess;
        boolean previousRenderAllFaces = renderer.renderAllFaces;
        boolean previousRenderFromInside = renderer.renderFromInside;
        renderer.blockAccess = waterAccess;
        renderer.renderAllFaces = false;
        renderer.renderFromInside = false;
        waterAccess.setRenderTarget(x, y, z);
        try {
            if (!hasVisibleWaterFace(waterAccess, x, y, z)) {
                return false;
            }
            return renderer.renderBlockByRenderType(waterAccess.getRenderWaterBlock(x, y, z), x, y, z);
        } finally {
            waterAccess.clearRenderTarget();
            renderer.renderFromInside = previousRenderFromInside;
            renderer.renderAllFaces = previousRenderAllFaces;
            renderer.blockAccess = previous;
        }
    }

    private static boolean hasVisibleWaterFace(WaterloggedBlockAccess waterAccess, int x, int y, int z) {
        Block fluid = waterAccess.riftflux$getFluidBlock(x, y, z);
        return fluid != null
                && (!isFluidForRender(waterAccess, fluid, x, y + 1, z)
                || !isFluidForRender(waterAccess, fluid, x, y, z - 1)
                || !isFluidForRender(waterAccess, fluid, x, y, z + 1)
                || !isFluidForRender(waterAccess, fluid, x - 1, y, z)
                || !isFluidForRender(waterAccess, fluid, x + 1, y, z));
    }

    private static boolean isFluidForRender(WaterloggedBlockAccess waterAccess, Block fluid,
                                            int x, int y, int z) {
        return RiftFluxFluidloggedLookup.isSameFluid(fluid, waterAccess.getBlock(x, y, z));
    }

    public static abstract class WaterloggedBlockAccess implements IBlockAccess, RiftFluxFluidloggedAccess {
        protected final IBlockAccess delegate;
        protected int renderX;
        protected int renderY;
        protected int renderZ;
        protected boolean hasRenderTarget;

        protected WaterloggedBlockAccess(IBlockAccess delegate) {
            this.delegate = delegate;
        }

        public Block getBlock(int x, int y, int z) {
            Block block = this.delegate.getBlock(x, y, z);
            Block fluid = this.getFluidloggedBlock(x, y, z);
            return fluid != null ? fluid : block;
        }

        public int getBlockMetadata(int x, int y, int z) {
            if (this.getFluidloggedBlock(x, y, z) != null) {
                return this.getWaterloggedMetadata(x, y, z);
            }
            return this.delegate.getBlockMetadata(x, y, z);
        }

        public boolean isAirBlock(int x, int y, int z) {
            return this.getFluidloggedBlock(x, y, z) == null && this.delegate.isAirBlock(x, y, z);
        }

        public TileEntity getTileEntity(int x, int y, int z) {
            return this.delegate.getTileEntity(x, y, z);
        }

        public int getLightBrightnessForSkyBlocks(int x, int y, int z, int defaultLight) {
            if (this.getFluidloggedBlock(x, y, z) != null) {
                int[] source = this.findNearestRealFluid(x, y, z);
                if (source != null) {
                    return this.delegate.getLightBrightnessForSkyBlocks(source[0], source[1], source[2], defaultLight);
                }
            }
            return this.delegate.getLightBrightnessForSkyBlocks(x, y, z, defaultLight);
        }

        public int isBlockProvidingPowerTo(int x, int y, int z, int side) {
            return this.delegate.isBlockProvidingPowerTo(x, y, z, side);
        }

        public BiomeGenBase getBiomeGenForCoords(int x, int z) {
            if (this.hasRenderTarget) {
                int[] source = this.findNearestRealFluid(this.renderX, this.renderY, this.renderZ);
                if (source != null) {
                    return this.delegate.getBiomeGenForCoords(source[0], source[2]);
                }
            }
            return this.delegate.getBiomeGenForCoords(x, z);
        }

        public int getHeight() {
            return this.delegate.getHeight();
        }

        public boolean extendedLevelsInChunkCache() {
            return this.delegate.extendedLevelsInChunkCache();
        }

        public boolean isSideSolid(int x, int y, int z, ForgeDirection side, boolean defaultValue) {
            return this.delegate.isSideSolid(x, y, z, side, defaultValue);
        }

        protected void setRenderTarget(int x, int y, int z) {
            this.renderX = x;
            this.renderY = y;
            this.renderZ = z;
            this.hasRenderTarget = true;
        }

        protected void clearRenderTarget() {
            this.hasRenderTarget = false;
        }

        protected int[] findNearestRealFluid(int x, int y, int z) {
            Block fluid = this.getFluidloggedBlock(x, y, z);
            if (fluid == null) {
                return null;
            }
            if (this.isSameRealFluidAt(fluid, x, y, z)) {
                return new int[]{x, y, z};
            }
            if (this.isSameRealFluidAt(fluid, x, y + 1, z)) {
                return new int[]{x, y + 1, z};
            }
            if (this.isSameRealFluidAt(fluid, x - 1, y, z)) {
                return new int[]{x - 1, y, z};
            }
            if (this.isSameRealFluidAt(fluid, x + 1, y, z)) {
                return new int[]{x + 1, y, z};
            }
            if (this.isSameRealFluidAt(fluid, x, y, z - 1)) {
                return new int[]{x, y, z - 1};
            }
            if (this.isSameRealFluidAt(fluid, x, y, z + 1)) {
                return new int[]{x, y, z + 1};
            }
            if (this.isSameRealFluidAt(fluid, x, y - 1, z)) {
                return new int[]{x, y - 1, z};
            }
            return null;
        }

        protected boolean isSameRealFluidAt(Block fluid, int x, int y, int z) {
            return RiftFluxFluidloggedLookup.isSameFluid(fluid, this.delegate.getBlock(x, y, z));
        }

        protected Block getFluidloggedBlock(int x, int y, int z) {
            return this.isWaterloggedAt(x, y, z)
                    ? RiftFluxFluidloggedLookup.getSupportedFluidBlock(this.delegate, x, y, z)
                    : null;
        }

        protected int getWaterloggedMetadata(int x, int y, int z) {
            return 0;
        }

        protected Block getRenderWaterBlock(int x, int y, int z) {
            return this.getFluidloggedBlock(x, y, z);
        }

        public Block riftflux$getFluidBlock(int x, int y, int z) {
            return this.getFluidloggedBlock(x, y, z);
        }

        protected abstract boolean isWaterloggedAt(int x, int y, int z);
    }
}
