package com.voidsrift.riftflux.mixin.late.geostrata;

import Reika.DragonAPI.Base.ISBRH;
import Reika.DragonAPI.Instantiable.Rendering.RotatedQuad;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.Rendering.ReikaColorAPI;
import Reika.GeoStrata.Blocks.BlockDecoGen;
import Reika.GeoStrata.Rendering.DecoGenRenderer;
import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.client.render.FullWaterBlockRenderer;
import com.voidsrift.riftflux.client.render.WaterloggedBlockRenderAccess;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.Field;

@Mixin(value = DecoGenRenderer.class, remap = false)
public abstract class MixinDecoGenRenderer_CrystalSpikeWater {
    @Unique
    private static Field riftflux$renderPassField;

    @Unique
    private static final int riftflux$ORIENTATION_MASK = 14;
    @Unique
    private static final int riftflux$ORIENTATION_NORTH = 2;
    @Unique
    private static final int riftflux$ORIENTATION_SOUTH = 4;
    @Unique
    private static final int riftflux$ORIENTATION_WEST = 6;
    @Unique
    private static final int riftflux$ORIENTATION_DOWN = 8;
    @Unique
    private static final int riftflux$ORIENTATION_EAST = 10;

    @Inject(method = "renderWorldBlock", at = @At("HEAD"), cancellable = true)
    private void riftflux$renderCeilingCrystalSpike(IBlockAccess world, int x, int y, int z, Block block,
                                                    int modelId, RenderBlocks renderer,
                                                    CallbackInfoReturnable<Boolean> cir) {
        int meta = world.getBlockMetadata(x, y, z);
        int orientation = riftflux$getOrientation(meta);
        boolean oriented = orientation != 0;
        boolean customUpward = !oriented
                && riftflux$getBaseMeta(meta) == 0
                && this.riftflux$usesCustomCrystalSpikeRender();
        if (!oriented && !customUpward) {
            return;
        }

        int renderPass = this.riftflux$getRenderPass();
        Tessellator tessellator = Tessellator.instance;
        if (renderPass == 0) {
            if (orientation == riftflux$ORIENTATION_DOWN) {
                this.riftflux$renderDownwardCrystalSpike(world, x, y, z, block, tessellator);
            } else if (riftflux$isWallOrientation(orientation)) {
                this.riftflux$renderWallCrystalSpike(world, x, y, z, block, orientation, tessellator);
            } else {
                this.riftflux$renderUpwardCrystalSpike(world, x, y, z, block, tessellator);
            }
        } else if (renderPass == 1 && ModConfig.fixGeoStrataCrystalSpikeWaterlogging) {
            this.riftflux$renderWaterloggedCrystalSpike(world, x, y, z, block, renderer);
        }
        tessellator.addVertex(0.0D, 0.0D, 0.0D);
        tessellator.addVertex(0.0D, 0.0D, 0.0D);
        tessellator.addVertex(0.0D, 0.0D, 0.0D);
        tessellator.addVertex(0.0D, 0.0D, 0.0D);
        cir.setReturnValue(true);
    }

    @Inject(method = "renderWorldBlock", at = @At("TAIL"))
    private void riftflux$renderWaterloggedCrystalSpike(IBlockAccess world, int x, int y, int z, Block block,
                                                        int modelId, RenderBlocks renderer,
                                                        CallbackInfoReturnable<Boolean> cir) {
        if (ModConfig.fixGeoStrataCrystalSpikeWaterlogging
                && this.riftflux$getRenderPass() == 1
                && riftflux$getBaseMeta(world.getBlockMetadata(x, y, z)) == 0) {
            this.riftflux$renderWaterloggedCrystalSpike(world, x, y, z, block, renderer);
        }
    }

    @Unique
    private void riftflux$renderWaterloggedCrystalSpike(IBlockAccess world, int x, int y, int z, Block block,
                                                       RenderBlocks renderer) {
        WaterloggedBlockRenderAccess waterAccess = new WaterloggedBlockRenderAccess(world);
        if (!waterAccess.isWaterloggedAt(x, y, z)) {
            return;
        }

        FullWaterBlockRenderer.render(renderer, waterAccess, x, y, z);
    }

    @Unique
    private void riftflux$renderDownwardCrystalSpike(IBlockAccess world, int x, int y, int z, Block block,
                                                     Tessellator tessellator) {
        int meta = world.getBlockMetadata(x, y, z);
        int baseMeta = riftflux$getBaseMeta(meta);
        RotatedQuad bottom = DecoGenRenderer.getCrystalShape(x, y - 1, z);
        RotatedQuad top = DecoGenRenderer.getCrystalShape(x, y, z);
        IIcon icon = block.getIcon(0, baseMeta);
        float minU = icon.getMinU();
        float minV = icon.getMinV();
        float maxU = icon.getMaxU();
        float maxV = icon.getMaxV();

        double b0x = bottom.getPosX(0);
        double b1x = bottom.getPosX(1);
        double b2x = bottom.getPosX(2);
        double b3x = bottom.getPosX(3);
        double t0x = top.getPosX(0);
        double t1x = top.getPosX(1);
        double t2x = top.getPosX(2);
        double t3x = top.getPosX(3);
        double b0z = bottom.getPosZ(0);
        double b1z = bottom.getPosZ(1);
        double b2z = bottom.getPosZ(2);
        double b3z = bottom.getPosZ(3);
        double t0z = top.getPosZ(0);
        double t1z = top.getPosZ(1);
        double t2z = top.getPosZ(2);
        double t3z = top.getPosZ(3);

        if (!this.riftflux$isSameDownwardSpike(world, x, y - 1, z)) {
            double shrink = 0.125D;
            b0x *= shrink;
            b1x *= shrink;
            b2x *= shrink;
            b3x *= shrink;
            b0z *= shrink;
            b1z *= shrink;
            b2z *= shrink;
            b3z *= shrink;
        }

        if (!this.riftflux$isSameDownwardSpike(world, x, y + 1, z)) {
            double spread = 0.75D;
            t0x = this.riftflux$spreadBase(t0x, spread);
            t1x = this.riftflux$spreadBase(t1x, spread);
            t2x = this.riftflux$spreadBase(t2x, spread);
            t3x = this.riftflux$spreadBase(t3x, spread);
            t0z = this.riftflux$spreadBase(t0z, spread);
            t1z = this.riftflux$spreadBase(t1z, spread);
            t2z = this.riftflux$spreadBase(t2z, spread);
            t3z = this.riftflux$spreadBase(t3z, spread);
        }

        tessellator.addTranslation((float) x, (float) y, (float) z);
        tessellator.setBrightness(this.riftflux$getCrystalBrightness(world, x, y, z, block));
        tessellator.setColorOpaque_I(this.riftflux$getCrystalColor(world, x, y, z, riftflux$ORIENTATION_DOWN));
        tessellator.addTranslation(0.5F, 0.0F, 0.5F);

        this.riftflux$addSpikeQuads(tessellator,
                b0x, b1x, b2x, b3x, t0x, t1x, t2x, t3x,
                b0z, b1z, b2z, b3z, t0z, t1z, t2z, t3z,
                minU, minV, maxU, maxV);

        tessellator.addTranslation(-0.5F, 0.0F, -0.5F);
        tessellator.addTranslation((float) -x, (float) -y, (float) -z);
    }

    @Unique
    private void riftflux$renderUpwardCrystalSpike(IBlockAccess world, int x, int y, int z, Block block,
                                                   Tessellator tessellator) {
        int meta = world.getBlockMetadata(x, y, z);
        int baseMeta = riftflux$getBaseMeta(meta);
        RotatedQuad bottom = DecoGenRenderer.getCrystalShape(x, y, z);
        RotatedQuad top = DecoGenRenderer.getCrystalShape(x, y + 1, z);
        IIcon icon = block.getIcon(0, baseMeta);
        float minU = icon.getMinU();
        float minV = icon.getMinV();
        float maxU = icon.getMaxU();
        float maxV = icon.getMaxV();

        double b0x = bottom.getPosX(0);
        double b1x = bottom.getPosX(1);
        double b2x = bottom.getPosX(2);
        double b3x = bottom.getPosX(3);
        double t0x = top.getPosX(0);
        double t1x = top.getPosX(1);
        double t2x = top.getPosX(2);
        double t3x = top.getPosX(3);
        double b0z = bottom.getPosZ(0);
        double b1z = bottom.getPosZ(1);
        double b2z = bottom.getPosZ(2);
        double b3z = bottom.getPosZ(3);
        double t0z = top.getPosZ(0);
        double t1z = top.getPosZ(1);
        double t2z = top.getPosZ(2);
        double t3z = top.getPosZ(3);

        if (!this.riftflux$isSameUpwardSpike(world, x, y + 1, z)) {
            double shrink = 0.125D;
            t0x *= shrink;
            t1x *= shrink;
            t2x *= shrink;
            t3x *= shrink;
            t0z *= shrink;
            t1z *= shrink;
            t2z *= shrink;
            t3z *= shrink;
        }

        if (!this.riftflux$isSameUpwardSpike(world, x, y - 1, z)) {
            double spread = 0.75D;
            b0x = this.riftflux$spreadBase(b0x, spread);
            b1x = this.riftflux$spreadBase(b1x, spread);
            b2x = this.riftflux$spreadBase(b2x, spread);
            b3x = this.riftflux$spreadBase(b3x, spread);
            b0z = this.riftflux$spreadBase(b0z, spread);
            b1z = this.riftflux$spreadBase(b1z, spread);
            b2z = this.riftflux$spreadBase(b2z, spread);
            b3z = this.riftflux$spreadBase(b3z, spread);
        }

        tessellator.addTranslation((float) x, (float) y, (float) z);
        tessellator.setBrightness(this.riftflux$getCrystalBrightness(world, x, y, z, block));
        tessellator.setColorOpaque_I(this.riftflux$getCrystalColor(world, x, y, z, 0));
        tessellator.addTranslation(0.5F, 0.0F, 0.5F);

        this.riftflux$addSpikeQuads(tessellator,
                b0x, b1x, b2x, b3x, t0x, t1x, t2x, t3x,
                b0z, b1z, b2z, b3z, t0z, t1z, t2z, t3z,
                minU, minV, maxU, maxV);

        tessellator.addTranslation(-0.5F, 0.0F, -0.5F);
        tessellator.addTranslation((float) -x, (float) -y, (float) -z);
    }

    @Unique
    private void riftflux$renderWallCrystalSpike(IBlockAccess world, int x, int y, int z, Block block,
                                                 int orientation, Tessellator tessellator) {
        int baseMeta = riftflux$getBaseMeta(world.getBlockMetadata(x, y, z));
        RotatedQuad base = DecoGenRenderer.getCrystalShape(x, y, z);
        RotatedQuad tip = DecoGenRenderer.getCrystalShape(x + riftflux$getOffsetX(orientation), y, z + riftflux$getOffsetZ(orientation));
        IIcon icon = block.getIcon(0, baseMeta);
        float minU = icon.getMinU();
        float minV = icon.getMinV();
        float maxU = icon.getMaxU();
        float maxV = icon.getMaxV();

        double b0a = base.getPosX(0);
        double b1a = base.getPosX(1);
        double b2a = base.getPosX(2);
        double b3a = base.getPosX(3);
        double b0b = base.getPosZ(0);
        double b1b = base.getPosZ(1);
        double b2b = base.getPosZ(2);
        double b3b = base.getPosZ(3);
        double t0a = tip.getPosX(0);
        double t1a = tip.getPosX(1);
        double t2a = tip.getPosX(2);
        double t3a = tip.getPosX(3);
        double t0b = tip.getPosZ(0);
        double t1b = tip.getPosZ(1);
        double t2b = tip.getPosZ(2);
        double t3b = tip.getPosZ(3);

        int dx = riftflux$getOffsetX(orientation);
        int dz = riftflux$getOffsetZ(orientation);
        if (!this.riftflux$isSameOrientedSpike(world, x + dx, y, z + dz, orientation)) {
            double shrink = 0.125D;
            t0a *= shrink;
            t1a *= shrink;
            t2a *= shrink;
            t3a *= shrink;
            t0b *= shrink;
            t1b *= shrink;
            t2b *= shrink;
            t3b *= shrink;
        }

        if (!this.riftflux$isSameOrientedSpike(world, x - dx, y, z - dz, orientation)) {
            double spread = 0.75D;
            b0a = this.riftflux$spreadBase(b0a, spread);
            b1a = this.riftflux$spreadBase(b1a, spread);
            b2a = this.riftflux$spreadBase(b2a, spread);
            b3a = this.riftflux$spreadBase(b3a, spread);
            b0b = this.riftflux$spreadBase(b0b, spread);
            b1b = this.riftflux$spreadBase(b1b, spread);
            b2b = this.riftflux$spreadBase(b2b, spread);
            b3b = this.riftflux$spreadBase(b3b, spread);
        }

        double[] b0 = this.riftflux$getWallVertex(orientation, true, b0a, b0b);
        double[] b1 = this.riftflux$getWallVertex(orientation, true, b1a, b1b);
        double[] b2 = this.riftflux$getWallVertex(orientation, true, b2a, b2b);
        double[] b3 = this.riftflux$getWallVertex(orientation, true, b3a, b3b);
        double[] t0 = this.riftflux$getWallVertex(orientation, false, t0a, t0b);
        double[] t1 = this.riftflux$getWallVertex(orientation, false, t1a, t1b);
        double[] t2 = this.riftflux$getWallVertex(orientation, false, t2a, t2b);
        double[] t3 = this.riftflux$getWallVertex(orientation, false, t3a, t3b);

        tessellator.addTranslation((float) x, (float) y, (float) z);
        tessellator.setBrightness(this.riftflux$getCrystalBrightness(world, x, y, z, block));
        tessellator.setColorOpaque_I(this.riftflux$getCrystalColor(world, x, y, z, orientation));

        this.riftflux$addSpikeQuads3D(tessellator, b0, b1, b2, b3, t0, t1, t2, t3, minU, minV, maxU, maxV);

        tessellator.addTranslation((float) -x, (float) -y, (float) -z);
    }

    @Unique
    private int riftflux$getCrystalColor(IBlockAccess world, int x, int y, int z, int orientation) {
        int n = 0;
        if (!ModConfig.disableGeoStrataCrystalSpikeHeightDarkening) {
            if (orientation == riftflux$ORIENTATION_DOWN) {
                n = this.riftflux$isSameDownwardSpike(world, x, y - 1, z) ? 1 : 0;
            } else if (orientation == 0) {
                n = this.riftflux$isSameUpwardSpike(world, x, y + 1, z) ? 1 : 0;
            } else {
                int dx = riftflux$getOffsetX(orientation);
                int dz = riftflux$getOffsetZ(orientation);
                n = this.riftflux$isSameOrientedSpike(world, x + dx, y, z + dz, orientation) ? 1 : 0;
            }
        }
        int grayscale = Math.max(
                32 + (int) (16.0D * Math.sin((double) (x + y * 8 + z * 2) / 8.0D)),
                255 - 6 * ReikaMathLibrary.intpow2(n + 1, 2));
        grayscale = this.riftflux$applyCrystalBrightness(grayscale);
        return ReikaColorAPI.GStoHex(grayscale);
    }

    @Unique
    private void riftflux$addSpikeQuads(Tessellator tessellator,
                                        double b0x, double b1x, double b2x, double b3x,
                                        double t0x, double t1x, double t2x, double t3x,
                                        double b0z, double b1z, double b2z, double b3z,
                                        double t0z, double t1z, double t2z, double t3z,
                                        float minU, float minV, float maxU, float maxV) {
        tessellator.addVertexWithUV(b0x, 0.0D, b0z, minU, minV);
        tessellator.addVertexWithUV(b1x, 0.0D, b1z, maxU, minV);
        tessellator.addVertexWithUV(b2x, 0.0D, b2z, maxU, maxV);
        tessellator.addVertexWithUV(b3x, 0.0D, b3z, minU, maxV);
        tessellator.addVertexWithUV(t3x, 1.0D, t3z, minU, maxV);
        tessellator.addVertexWithUV(t2x, 1.0D, t2z, maxU, maxV);
        tessellator.addVertexWithUV(t1x, 1.0D, t1z, maxU, minV);
        tessellator.addVertexWithUV(t0x, 1.0D, t0z, minU, minV);
        tessellator.addVertexWithUV(t0x, 1.0D, t0z, minU, maxV);
        tessellator.addVertexWithUV(t1x, 1.0D, t1z, maxU, maxV);
        tessellator.addVertexWithUV(b1x, 0.0D, b1z, maxU, minV);
        tessellator.addVertexWithUV(b0x, 0.0D, b0z, minU, minV);
        tessellator.addVertexWithUV(b3x, 0.0D, b3z, minU, minV);
        tessellator.addVertexWithUV(b2x, 0.0D, b2z, maxU, minV);
        tessellator.addVertexWithUV(t2x, 1.0D, t2z, maxU, maxV);
        tessellator.addVertexWithUV(t3x, 1.0D, t3z, minU, maxV);
        tessellator.addVertexWithUV(t1x, 1.0D, t1z, minU, maxV);
        tessellator.addVertexWithUV(t2x, 1.0D, t2z, maxU, maxV);
        tessellator.addVertexWithUV(b2x, 0.0D, b2z, maxU, minV);
        tessellator.addVertexWithUV(b1x, 0.0D, b1z, minU, minV);
        tessellator.addVertexWithUV(b0x, 0.0D, b0z, minU, minV);
        tessellator.addVertexWithUV(b3x, 0.0D, b3z, maxU, minV);
        tessellator.addVertexWithUV(t3x, 1.0D, t3z, maxU, maxV);
        tessellator.addVertexWithUV(t0x, 1.0D, t0z, minU, maxV);
    }

    @Unique
    private void riftflux$addSpikeQuads3D(Tessellator tessellator,
                                          double[] b0, double[] b1, double[] b2, double[] b3,
                                          double[] t0, double[] t1, double[] t2, double[] t3,
                                          float minU, float minV, float maxU, float maxV) {
        this.riftflux$addDoubleSidedQuad(tessellator, b0, b1, b2, b3, minU, minV, maxU, minV, maxU, maxV, minU, maxV);
        this.riftflux$addDoubleSidedQuad(tessellator, t3, t2, t1, t0, minU, maxV, maxU, maxV, maxU, minV, minU, minV);
        this.riftflux$addDoubleSidedQuad(tessellator, t0, t1, b1, b0, minU, maxV, maxU, maxV, maxU, minV, minU, minV);
        this.riftflux$addDoubleSidedQuad(tessellator, b3, b2, t2, t3, minU, minV, maxU, minV, maxU, maxV, minU, maxV);
        this.riftflux$addDoubleSidedQuad(tessellator, t1, t2, b2, b1, minU, maxV, maxU, maxV, maxU, minV, minU, minV);
        this.riftflux$addDoubleSidedQuad(tessellator, b0, b3, t3, t0, minU, minV, maxU, minV, maxU, maxV, minU, maxV);
    }

    @Unique
    private void riftflux$addDoubleSidedQuad(Tessellator tessellator,
                                             double[] v0, double[] v1, double[] v2, double[] v3,
                                             float u0, float v0Tex, float u1, float v1Tex,
                                             float u2, float v2Tex, float u3, float v3Tex) {
        this.riftflux$addVertex(tessellator, v0, u0, v0Tex);
        this.riftflux$addVertex(tessellator, v1, u1, v1Tex);
        this.riftflux$addVertex(tessellator, v2, u2, v2Tex);
        this.riftflux$addVertex(tessellator, v3, u3, v3Tex);
        this.riftflux$addVertex(tessellator, v3, u3, v3Tex);
        this.riftflux$addVertex(tessellator, v2, u2, v2Tex);
        this.riftflux$addVertex(tessellator, v1, u1, v1Tex);
        this.riftflux$addVertex(tessellator, v0, u0, v0Tex);
    }

    @Unique
    private void riftflux$addVertex(Tessellator tessellator, double[] vertex, float u, float v) {
        tessellator.addVertexWithUV(vertex[0], vertex[1], vertex[2], u, v);
    }

    @Unique
    private double[] riftflux$getWallVertex(int orientation, boolean base, double a, double b) {
        double axis = base ? this.riftflux$getWallBaseAxis(orientation) : this.riftflux$getWallTipAxis(orientation);
        if (orientation == riftflux$ORIENTATION_EAST || orientation == riftflux$ORIENTATION_WEST) {
            return new double[]{axis, 0.5D + b, 0.5D + a};
        }
        return new double[]{0.5D + a, 0.5D + b, axis};
    }

    @Unique
    private double riftflux$getWallBaseAxis(int orientation) {
        return orientation == riftflux$ORIENTATION_WEST || orientation == riftflux$ORIENTATION_NORTH ? 1.0D : 0.0D;
    }

    @Unique
    private double riftflux$getWallTipAxis(int orientation) {
        return orientation == riftflux$ORIENTATION_WEST || orientation == riftflux$ORIENTATION_NORTH ? 0.0D : 1.0D;
    }

    @Unique
    private double riftflux$spreadBase(double value, double spread) {
        return Math.signum(value) * (1.0D - spread * (1.0D - Math.abs(value)));
    }

    @Unique
    private boolean riftflux$isSameDownwardSpike(IBlockAccess world, int x, int y, int z) {
        Block block = world.getBlock(x, y, z);
        return block instanceof BlockDecoGen
                && riftflux$isDownwardCrystalSpikeMeta(world.getBlockMetadata(x, y, z));
    }

    @Unique
    private boolean riftflux$isSameUpwardSpike(IBlockAccess world, int x, int y, int z) {
        Block block = world.getBlock(x, y, z);
        int meta = world.getBlockMetadata(x, y, z);
        return block instanceof BlockDecoGen
                && riftflux$getBaseMeta(meta) == 0
                && riftflux$getOrientation(meta) == 0;
    }

    @Unique
    private boolean riftflux$isSameOrientedSpike(IBlockAccess world, int x, int y, int z, int orientation) {
        Block block = world.getBlock(x, y, z);
        int meta = world.getBlockMetadata(x, y, z);
        return block instanceof BlockDecoGen
                && riftflux$getBaseMeta(meta) == 0
                && riftflux$getOrientation(meta) == orientation;
    }

    @Unique
    private boolean riftflux$usesCustomCrystalSpikeRender() {
        return ModConfig.disableGeoStrataCrystalSpikeHeightDarkening
                || ModConfig.geoStrataCrystalSpikeTextureBrightnessPercent != 100
                || ModConfig.disableGeoStrataCrystalSpikeFullbright;
    }

    @Unique
    private int riftflux$applyCrystalBrightness(int grayscale) {
        int adjusted = Math.round((float) grayscale * (float) ModConfig.geoStrataCrystalSpikeTextureBrightnessPercent / 100.0F);
        if (adjusted < 0) {
            return 0;
        }
        if (adjusted > 255) {
            return 255;
        }
        return adjusted;
    }

    @Unique
    private int riftflux$getCrystalBrightness(IBlockAccess world, int x, int y, int z, Block block) {
        if (!ModConfig.disableGeoStrataCrystalSpikeFullbright) {
            return 240;
        }

        int brightness = block.getMixedBrightnessForBlock(world, x, y, z);
        brightness = Math.max(brightness, block.getMixedBrightnessForBlock(world, x, y - 1, z));
        brightness = Math.max(brightness, block.getMixedBrightnessForBlock(world, x - 1, y, z));
        brightness = Math.max(brightness, block.getMixedBrightnessForBlock(world, x + 1, y, z));
        brightness = Math.max(brightness, block.getMixedBrightnessForBlock(world, x, y, z - 1));
        brightness = Math.max(brightness, block.getMixedBrightnessForBlock(world, x, y, z + 1));
        return brightness;
    }

    @Unique
    private int riftflux$getRenderPass() {
        try {
            if (riftflux$renderPassField == null) {
                riftflux$renderPassField = ISBRH.class.getDeclaredField("renderPass");
                riftflux$renderPassField.setAccessible(true);
            }
            return riftflux$renderPassField.getInt(this);
        } catch (Throwable ignored) {
            return -1;
        }
    }

    @Unique
    private static int riftflux$getBaseMeta(int meta) {
        return meta & ~riftflux$ORIENTATION_MASK;
    }

    @Unique
    private static boolean riftflux$isDownwardCrystalSpikeMeta(int meta) {
        return riftflux$getOrientation(meta) == riftflux$ORIENTATION_DOWN && riftflux$getBaseMeta(meta) == 0;
    }

    @Unique
    private static int riftflux$getOrientation(int meta) {
        return meta & riftflux$ORIENTATION_MASK;
    }

    @Unique
    private static boolean riftflux$isWallOrientation(int orientation) {
        return orientation == riftflux$ORIENTATION_NORTH
                || orientation == riftflux$ORIENTATION_SOUTH
                || orientation == riftflux$ORIENTATION_WEST
                || orientation == riftflux$ORIENTATION_EAST;
    }

    @Unique
    private static int riftflux$getOffsetX(int orientation) {
        if (orientation == riftflux$ORIENTATION_WEST) {
            return -1;
        }
        if (orientation == riftflux$ORIENTATION_EAST) {
            return 1;
        }
        return 0;
    }

    @Unique
    private static int riftflux$getOffsetZ(int orientation) {
        if (orientation == riftflux$ORIENTATION_NORTH) {
            return -1;
        }
        if (orientation == riftflux$ORIENTATION_SOUTH) {
            return 1;
        }
        return 0;
    }
}
