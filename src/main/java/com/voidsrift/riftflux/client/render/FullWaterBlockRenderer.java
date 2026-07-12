package com.voidsrift.riftflux.client.render;

import com.voidsrift.riftflux.waterlogging.RiftFluxFluidloggedAccess;
import com.voidsrift.riftflux.waterlogging.RiftFluxFluidloggedLookup;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.init.Blocks;
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
        return !isWaterForRender(waterAccess, x, y + 1, z)
                || !isWaterForRender(waterAccess, x, y, z - 1)
                || !isWaterForRender(waterAccess, x, y, z + 1)
                || !isWaterForRender(waterAccess, x - 1, y, z)
                || !isWaterForRender(waterAccess, x + 1, y, z);
    }

    private static boolean isWaterForRender(WaterloggedBlockAccess waterAccess, int x, int y, int z) {
        Block block = waterAccess.getBlock(x, y, z);
        return block != null && block.getMaterial() == Material.water;
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
            return this.isWaterloggedAsWater(x, y, z) || isRealWater(block) ? Blocks.water : block;
        }

        public int getBlockMetadata(int x, int y, int z) {
            if (this.isWaterloggedAsWater(x, y, z)) {
                return this.getWaterloggedMetadata(x, y, z);
            }
            if (isRealWater(this.delegate.getBlock(x, y, z))) {
                return this.getRealWaterMetadata(x, y, z);
            }
            return this.delegate.getBlockMetadata(x, y, z);
        }

        public boolean isAirBlock(int x, int y, int z) {
            return !this.isWaterloggedAsWater(x, y, z) && this.delegate.isAirBlock(x, y, z);
        }

        public TileEntity getTileEntity(int x, int y, int z) {
            return this.delegate.getTileEntity(x, y, z);
        }

        public int getLightBrightnessForSkyBlocks(int x, int y, int z, int defaultLight) {
            if (this.isWaterloggedAsWater(x, y, z)) {
                int[] source = this.findNearestRealWater(x, y, z);
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
                int[] source = this.findNearestRealWater(this.renderX, this.renderY, this.renderZ);
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

        protected static boolean isRealWater(Block block) {
            return block != null && block.getMaterial() == Material.water;
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

        protected int[] findNearestRealWater(int x, int y, int z) {
            if (this.isRealWaterAt(x, y, z)) {
                return new int[]{x, y, z};
            }
            if (this.isRealWaterAt(x, y + 1, z)) {
                return new int[]{x, y + 1, z};
            }
            if (this.isRealWaterAt(x - 1, y, z)) {
                return new int[]{x - 1, y, z};
            }
            if (this.isRealWaterAt(x + 1, y, z)) {
                return new int[]{x + 1, y, z};
            }
            if (this.isRealWaterAt(x, y, z - 1)) {
                return new int[]{x, y, z - 1};
            }
            if (this.isRealWaterAt(x, y, z + 1)) {
                return new int[]{x, y, z + 1};
            }
            if (this.isRealWaterAt(x, y - 1, z)) {
                return new int[]{x, y - 1, z};
            }
            return null;
        }

        protected boolean isRealWaterAt(int x, int y, int z) {
            return isRealWater(this.delegate.getBlock(x, y, z));
        }

        protected boolean isWaterloggedAsWater(int x, int y, int z) {
            return this.riftflux$getFluidBlock(x, y, z) == Blocks.water;
        }

        protected int getWaterloggedMetadata(int x, int y, int z) {
            return 0;
        }

        protected int getRealWaterMetadata(int x, int y, int z) {
            return this.delegate.getBlockMetadata(x, y, z);
        }

        protected Block getRenderWaterBlock(int x, int y, int z) {
            int[] source = this.findNearestRealWater(x, y, z);
            if (source == null) {
                return Blocks.water;
            }
            Block block = this.delegate.getBlock(source[0], source[1], source[2]);
            return isRealWater(block) ? block : Blocks.water;
        }

        public Block riftflux$getFluidBlock(int x, int y, int z) {
            if (this.isWaterloggedAt(x, y, z)) {
                return Blocks.water;
            }
            return RiftFluxFluidloggedLookup.getSupportedFluidBlock(this.delegate, x, y, z);
        }

        protected abstract boolean isWaterloggedAt(int x, int y, int z);
    }
}
