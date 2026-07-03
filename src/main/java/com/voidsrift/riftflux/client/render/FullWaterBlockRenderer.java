package com.voidsrift.riftflux.client.render;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.util.ForgeDirection;

public final class FullWaterBlockRenderer {
    private static final double STILL_WATER_TOP = 8.0D / 9.0D;
    private static final double LIQUID_TOP_EPSILON = 0.0010000000474974513D;

    private FullWaterBlockRenderer() {
    }

    public static boolean render(RenderBlocks renderer, WaterloggedBlockAccess waterAccess, int x, int y, int z) {
        IBlockAccess previous = renderer.blockAccess;
        renderer.blockAccess = waterAccess;
        waterAccess.setRenderTarget(x, y, z);
        try {
            return renderer.renderBlockLiquid(Blocks.water, x, y, z);
        } finally {
            waterAccess.clearRenderTarget();
            renderer.blockAccess = previous;
        }
    }

    public static boolean renderStillSurface(WaterloggedBlockAccess waterAccess, int x, int y, int z) {
        waterAccess.setRenderTarget(x, y, z);
        try {
            if (!waterAccess.isWaterloggedAt(x, y, z)
                    || waterAccess.isRealWaterAt(x, y + 1, z)
                    || waterAccess.isWaterloggedAt(x, y + 1, z)) {
                return false;
            }

            int[] source = waterAccess.findNearestRealWater(x, y, z);
            int colorX = source == null ? x : source[0];
            int colorY = source == null ? y : source[1];
            int colorZ = source == null ? z : source[2];
            Block water = Blocks.water;
            int color = water.colorMultiplier(waterAccess, colorX, colorY, colorZ);
            float red = (float) (color >> 16 & 255) / 255.0F;
            float green = (float) (color >> 8 & 255) / 255.0F;
            float blue = (float) (color & 255) / 255.0F;

            IIcon icon = BlockLiquid.getLiquidIcon("water_still");
            double minU = icon.getInterpolatedU(0.0D);
            double maxU = icon.getInterpolatedU(16.0D);
            double minV = icon.getInterpolatedV(0.0D);
            double maxV = icon.getInterpolatedV(16.0D);
            double top00 = getCornerWaterSurfaceTop(waterAccess, x, y, z, 0, 0);
            double top01 = getCornerWaterSurfaceTop(waterAccess, x, y, z, 0, 1);
            double top11 = getCornerWaterSurfaceTop(waterAccess, x, y, z, 1, 1);
            double top10 = getCornerWaterSurfaceTop(waterAccess, x, y, z, 1, 0);

            Tessellator tessellator = Tessellator.instance;
            tessellator.setBrightness(water.getMixedBrightnessForBlock(waterAccess, colorX, colorY, colorZ));
            tessellator.setColorOpaque_F(red, green, blue);
            tessellator.addVertexWithUV((double) x, top00, (double) z, minU, minV);
            tessellator.addVertexWithUV((double) x, top01, (double) (z + 1), minU, maxV);
            tessellator.addVertexWithUV((double) (x + 1), top11, (double) (z + 1), maxU, maxV);
            tessellator.addVertexWithUV((double) (x + 1), top10, (double) z, maxU, minV);
            tessellator.addVertexWithUV((double) (x + 1), top10, (double) z, maxU, minV);
            tessellator.addVertexWithUV((double) (x + 1), top11, (double) (z + 1), maxU, maxV);
            tessellator.addVertexWithUV((double) x, top01, (double) (z + 1), minU, maxV);
            tessellator.addVertexWithUV((double) x, top00, (double) z, minU, minV);
            return true;
        } finally {
            waterAccess.clearRenderTarget();
        }
    }

    private static double getCornerWaterSurfaceTop(WaterloggedBlockAccess waterAccess, int x, int y, int z,
                                                   int offsetX, int offsetZ) {
        return (double) y + getVanillaCornerWaterHeight(waterAccess, x + offsetX, y, z + offsetZ) - LIQUID_TOP_EPSILON;
    }

    private static double getVanillaCornerWaterHeight(WaterloggedBlockAccess waterAccess, int x, int y, int z) {
        int count = 0;
        double total = 0.0D;
        for (int corner = 0; corner < 4; ++corner) {
            int sampleX = x - (corner & 1);
            int sampleZ = z - (corner >> 1 & 1);
            if (waterAccess.isRealWaterAt(sampleX, y + 1, sampleZ)
                    || waterAccess.isWaterloggedAsWater(sampleX, y + 1, sampleZ)) {
                return 1.0D;
            }

            Material material = waterAccess.getBlock(sampleX, y, sampleZ).getMaterial();
            if (material == Material.water) {
                int meta = waterAccess.getBlockMetadata(sampleX, y, sampleZ);
                if (meta >= 8 || meta == 0) {
                    total += (double) BlockLiquid.getLiquidHeightPercent(meta) * 10.0D;
                    count += 10;
                }
                total += BlockLiquid.getLiquidHeightPercent(meta);
                ++count;
            } else if (!material.isSolid()) {
                total += 1.0D;
                ++count;
            }
        }
        return count == 0 ? STILL_WATER_TOP : 1.0D - total / (double) count;
    }

    public static abstract class WaterloggedBlockAccess implements IBlockAccess {
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
            int[] source = this.findNearestRealWater(x, y, z);
            if (source != null) {
                return this.delegate.getLightBrightnessForSkyBlocks(source[0], source[1], source[2], defaultLight);
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
            return this.isWaterloggedAt(x, y, z);
        }

        protected int getWaterloggedMetadata(int x, int y, int z) {
            return 0;
        }

        protected int getRealWaterMetadata(int x, int y, int z) {
            return 0;
        }

        protected abstract boolean isWaterloggedAt(int x, int y, int z);
    }
}
