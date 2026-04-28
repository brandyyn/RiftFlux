package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.util.TorchRenderRules;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Locale;

@Mixin(RenderBlocks.class)
public abstract class MixinRenderBlocks_TorchAllFaces {
    @Shadow
    public IIcon overrideBlockTexture;

    @Shadow
    public abstract IIcon getBlockIconFromSideAndMetadata(Block block, int side, int meta);

    @Shadow
    public abstract boolean hasOverrideBlockTexture();

    @Inject(method = "renderTorchAtAngle", at = @At("HEAD"), cancellable = true)
    private void riftflux$renderDoubleSidedTorch(Block block, double x, double y, double z, double xTilt, double zTilt, int meta, CallbackInfo ci) {
        boolean useModernTorchModel = ModConfig.modernTorchRendering && TorchRenderRules.isModernTorchRenderingEligible(block, meta);
        boolean billboardTorchEnabled = ModConfig.billboardTorchRendering;
        boolean useBillboardTorch = billboardTorchEnabled && TorchRenderRules.shouldUseBillboardTorchRendering(block, meta, useModernTorchModel);
        boolean useLegacyDoubleSided = ModConfig.doubleSidedTorchRendering && !billboardTorchEnabled;
        if (!useModernTorchModel && !useBillboardTorch && !useLegacyDoubleSided) {
            return;
        }

        if (useBillboardTorch) {
            ci.cancel();
            return;
        } else if (useModernTorchModel) {
            this.riftflux$renderTorchAtAngleModern(block, x, y, z, xTilt, zTilt, meta);
        } else {
            this.riftflux$renderTorchAtAngleDoubleSided(block, x, y, z, xTilt, zTilt, meta);
        }
        ci.cancel();
    }

    @Unique
    private boolean riftflux$isModernTorchRenderingEligible(Block block, int meta) {
        if (block == null || block.getRenderType() != Blocks.redstone_torch.getRenderType()) {
            return false;
        }
        return riftflux$torchFilterAllows(
                block,
                ModConfig.modernTorchRenderingWhitelist,
                ModConfig.modernTorchRenderingBlacklist
        );
    }

    @Unique
    private boolean riftflux$shouldUseBillboardTorchRendering(Block block, int meta, boolean useModernTorchModel) {
        if (block == null || block.getRenderType() != Blocks.redstone_torch.getRenderType()) {
            return false;
        }
        if (riftflux$matchesTorchFilter(block, ModConfig.billboardTorchRenderingWhitelist)) {
            return true;
        }
        if (riftflux$matchesTorchFilter(block, ModConfig.billboardTorchRenderingBlacklist)) {
            return false;
        }
        return !useModernTorchModel;
    }

    @Unique
    private static boolean riftflux$torchFilterAllows(Block block, String[] whitelist, String[] blacklist) {
        if (riftflux$matchesTorchFilter(block, whitelist)) {
            return true;
        }
        return !riftflux$matchesTorchFilter(block, blacklist);
    }

    @Unique
    private static boolean riftflux$matchesTorchFilter(Block block, String[] entries) {
        if (block == null || entries == null || entries.length == 0) {
            return false;
        }

        String registryName = riftflux$lower(Block.blockRegistry.getNameForObject(block));
        String modId = "";
        String blockPath = registryName;
        int separator = registryName.indexOf(':');
        if (separator >= 0) {
            modId = registryName.substring(0, separator);
            blockPath = registryName.substring(separator + 1);
        }

        String unlocalizedName = riftflux$lower(block.getUnlocalizedName());
        String className = riftflux$lower(block.getClass().getName());
        String simpleClassName = riftflux$lower(block.getClass().getSimpleName());

        for (String rawEntry : entries) {
            if (rawEntry == null) {
                continue;
            }
            String entry = rawEntry.trim().toLowerCase(Locale.ROOT);
            if (entry.isEmpty()) {
                continue;
            }

            if (entry.endsWith(":*")) {
                if (entry.substring(0, entry.length() - 2).equals(modId)) {
                    return true;
                }
                continue;
            }

            if (entry.indexOf(':') >= 0) {
                if (entry.equals(registryName)) {
                    return true;
                }
                continue;
            }

            if (entry.equals(modId)
                    || entry.equals(blockPath)
                    || entry.equals(unlocalizedName)
                    || unlocalizedName.endsWith("." + entry)
                    || entry.equals(className)
                    || entry.equals(simpleClassName)) {
                return true;
            }
        }
        return false;
    }

    @Unique
    private static String riftflux$lower(Object value) {
        return value == null ? "" : String.valueOf(value).toLowerCase(Locale.ROOT);
    }

    @Unique
    private void riftflux$renderTorchAtAngleDoubleSided(Block block, double x, double y, double z, double xTilt, double zTilt, int meta) {
        this.riftflux$renderTorchAtAngleDoubleSided(block, x, y, z, xTilt, zTilt, meta, false);
    }

    @Unique
    private void riftflux$renderTorchAtAngleBillboard(Block block, double x, double y, double z, double xTilt, double zTilt, int meta) {
        Tessellator tessellator = Tessellator.instance;
        IIcon icon = this.getBlockIconFromSideAndMetadata(block, 0, meta);

        if (this.hasOverrideBlockTexture()) {
            icon = this.overrideBlockTexture;
        }

        x += 0.5D;
        z += 0.5D;

        double bottomX = x + xTilt;
        double bottomY = y;
        double bottomZ = z + zTilt;
        double topX = x;
        double topY = y + 1.0D;
        double topZ = z;
        double centerX = (bottomX + topX) * 0.5D;
        double centerZ = (bottomZ + topZ) * 0.5D;
        double[] billboardSide = this.riftflux$getBillboardSideVector(centerX, centerZ, 0.5D);
        double sideX = billboardSide[0];
        double sideZ = billboardSide[1];

        this.riftflux$addDoubleSidedQuad(
                tessellator,
                topX - sideX, topY, topZ - sideZ, icon.getMinU(), icon.getMinV(),
                bottomX - sideX, bottomY, bottomZ - sideZ, icon.getMinU(), icon.getMaxV(),
                bottomX + sideX, bottomY, bottomZ + sideZ, icon.getMaxU(), icon.getMaxV(),
                topX + sideX, topY, topZ + sideZ, icon.getMaxU(), icon.getMinV()
        );
    }

    @Unique
    private double[] riftflux$getBillboardSideVector(double centerX, double centerZ, double halfWidth) {
        Minecraft minecraft = Minecraft.getMinecraft();
        Entity camera = minecraft == null ? null : minecraft.renderViewEntity;
        if (camera == null && minecraft != null) {
            camera = minecraft.thePlayer;
        }
        if (camera == null) {
            return new double[]{halfWidth, 0.0D};
        }

        double toCameraX = camera.posX - centerX;
        double toCameraZ = camera.posZ - centerZ;
        double length = Math.sqrt(toCameraX * toCameraX + toCameraZ * toCameraZ);
        if (length < 1.0E-6D) {
            return new double[]{halfWidth, 0.0D};
        }

        return new double[]{
                -toCameraZ / length * halfWidth,
                toCameraX / length * halfWidth
        };
    }

    @Unique
    private void riftflux$renderTorchAtAngleDoubleSided(Block block, double x, double y, double z, double xTilt, double zTilt, int meta, boolean cameraSelectiveBackFaces) {
        Tessellator tessellator = Tessellator.instance;
        IIcon icon = this.getBlockIconFromSideAndMetadata(block, 0, meta);

        if (this.hasOverrideBlockTexture()) {
            icon = this.overrideBlockTexture;
        }

        double minU = icon.getMinU();
        double minV = icon.getMinV();
        double maxU = icon.getMaxU();
        double maxV = icon.getMaxV();
        double capMinU = icon.getInterpolatedU(7.0D);
        double capMinV = icon.getInterpolatedV(6.0D);
        double capMaxU = icon.getInterpolatedU(9.0D);
        double capMaxV = icon.getInterpolatedV(8.0D);
        double stemMinU = icon.getInterpolatedU(7.0D);
        double stemMinV = icon.getInterpolatedV(13.0D);
        double stemMaxU = icon.getInterpolatedU(9.0D);
        double stemMaxV = icon.getInterpolatedV(15.0D);

        x += 0.5D;
        z += 0.5D;

        double xMin = x - 0.5D;
        double xMax = x + 0.5D;
        double zMin = z - 0.5D;
        double zMax = z + 0.5D;
        double halfWidth = 0.0625D;
        double topHeight = 0.625D;

        double topOffsetX = x + xTilt * (1.0D - topHeight);
        double topOffsetZ = z + zTilt * (1.0D - topHeight);

        this.riftflux$addDoubleSidedTorchQuad(
                tessellator,
                cameraSelectiveBackFaces,
                topOffsetX - halfWidth, y + topHeight, topOffsetZ - halfWidth, capMinU, capMinV,
                topOffsetX - halfWidth, y + topHeight, topOffsetZ + halfWidth, capMinU, capMaxV,
                topOffsetX + halfWidth, y + topHeight, topOffsetZ + halfWidth, capMaxU, capMaxV,
                topOffsetX + halfWidth, y + topHeight, topOffsetZ - halfWidth, capMaxU, capMinV
        );

        this.riftflux$addDoubleSidedTorchQuad(
                tessellator,
                cameraSelectiveBackFaces,
                x + halfWidth + xTilt, y, z - halfWidth + zTilt, stemMaxU, stemMinV,
                x + halfWidth + xTilt, y, z + halfWidth + zTilt, stemMaxU, stemMaxV,
                x - halfWidth + xTilt, y, z + halfWidth + zTilt, stemMinU, stemMaxV,
                x - halfWidth + xTilt, y, z - halfWidth + zTilt, stemMinU, stemMinV
        );

        this.riftflux$addDoubleSidedTorchQuad(
                tessellator,
                cameraSelectiveBackFaces,
                x - halfWidth, y + 1.0D, zMin, minU, minV,
                x - halfWidth + xTilt, y, zMin + zTilt, minU, maxV,
                x - halfWidth + xTilt, y, zMax + zTilt, maxU, maxV,
                x - halfWidth, y + 1.0D, zMax, maxU, minV
        );

        this.riftflux$addDoubleSidedTorchQuad(
                tessellator,
                cameraSelectiveBackFaces,
                x + halfWidth, y + 1.0D, zMax, minU, minV,
                x + xTilt + halfWidth, y, zMax + zTilt, minU, maxV,
                x + xTilt + halfWidth, y, zMin + zTilt, maxU, maxV,
                x + halfWidth, y + 1.0D, zMin, maxU, minV
        );

        this.riftflux$addDoubleSidedTorchQuad(
                tessellator,
                cameraSelectiveBackFaces,
                xMin, y + 1.0D, z + halfWidth, minU, minV,
                xMin + xTilt, y, z + halfWidth + zTilt, minU, maxV,
                xMax + xTilt, y, z + halfWidth + zTilt, maxU, maxV,
                xMax, y + 1.0D, z + halfWidth, maxU, minV
        );

        this.riftflux$addDoubleSidedTorchQuad(
                tessellator,
                cameraSelectiveBackFaces,
                xMax, y + 1.0D, z - halfWidth, minU, minV,
                xMax + xTilt, y, z - halfWidth + zTilt, minU, maxV,
                xMin + xTilt, y, z - halfWidth + zTilt, maxU, maxV,
                xMin, y + 1.0D, z - halfWidth, maxU, minV
        );
    }

    @Unique
    private void riftflux$renderTorchAtAngleModern(Block block, double x, double y, double z, double xTilt, double zTilt, int meta) {
        Tessellator tessellator = Tessellator.instance;
        IIcon icon = this.getBlockIconFromSideAndMetadata(block, 0, meta);

        if (this.hasOverrideBlockTexture()) {
            icon = this.overrideBlockTexture;
        }

        double stemSideMinU = icon.getInterpolatedU(7.0D);
        double stemSideMinV = icon.getInterpolatedV(6.0D);
        double stemSideMaxU = icon.getInterpolatedU(9.0D);
        double stemSideMaxV = icon.getInterpolatedV(16.0D);
        double capMinU = icon.getInterpolatedU(7.0D);
        double capMinV = icon.getInterpolatedV(6.0D);
        double capMaxU = icon.getInterpolatedU(9.0D);
        double capMaxV = icon.getInterpolatedV(8.0D);
        x += 0.5D;
        z += 0.5D;

        this.riftflux$renderSlantedRectangularCuboid(
                tessellator,
                x, y, z,
                xTilt, zTilt,
                0.0D, 0.625D,
                0.0625D, 0.0625D,
                stemSideMinU, stemSideMinV, stemSideMaxU, stemSideMaxV,
                capMinU, capMinV, capMaxU, capMaxV,
                true, true, false
        );
        this.riftflux$renderModernRedstoneTorchCap(tessellator, icon, x, y, z, xTilt, zTilt);
    }

    @Unique
    private void riftflux$renderModernRedstoneTorchCap(
            Tessellator tessellator,
            IIcon icon,
            double x, double y, double z,
            double xTilt, double zTilt) {
        double lowerHeight = 7.5D / 16.0D;
        double upperHeight = 10.5D / 16.0D;
        double capHalfWidth = 1.5D / 16.0D;

        double lowerCenterX = x + xTilt * (1.0D - lowerHeight);
        double lowerCenterZ = z + zTilt * (1.0D - lowerHeight);
        double upperCenterX = x + xTilt * (1.0D - upperHeight);
        double upperCenterZ = z + zTilt * (1.0D - upperHeight);
        double lowerY = y + lowerHeight;
        double upperY = y + upperHeight;

        double lowerMinX = lowerCenterX - capHalfWidth;
        double lowerMaxX = lowerCenterX + capHalfWidth;
        double lowerMinZ = lowerCenterZ - capHalfWidth;
        double lowerMaxZ = lowerCenterZ + capHalfWidth;
        double upperMinX = upperCenterX - capHalfWidth;
        double upperMaxX = upperCenterX + capHalfWidth;
        double upperMinZ = upperCenterZ - capHalfWidth;
        double upperMaxZ = upperCenterZ + capHalfWidth;

        double lowerCapMinU = icon.getInterpolatedU(8.0D);
        double lowerCapMinV = icon.getInterpolatedV(5.0D);
        double lowerCapMaxU = icon.getInterpolatedU(9.0D);
        double lowerCapMaxV = icon.getInterpolatedV(6.0D);
        double upperCapMinU = icon.getInterpolatedU(7.0D);
        double upperCapMinV = icon.getInterpolatedV(5.0D);
        double upperCapMaxU = icon.getInterpolatedU(8.0D);
        double upperCapMaxV = icon.getInterpolatedV(6.0D);
        double northMinU = icon.getInterpolatedU(6.0D);
        double northMinV = icon.getInterpolatedV(6.0D);
        double northMaxU = icon.getInterpolatedU(7.0D);
        double northMaxV = icon.getInterpolatedV(7.0D);
        double southMinU = icon.getInterpolatedU(9.0D);
        double southMinV = icon.getInterpolatedV(6.0D);
        double southMaxU = icon.getInterpolatedU(10.0D);
        double southMaxV = icon.getInterpolatedV(7.0D);
        double westMinU = icon.getInterpolatedU(6.0D);
        double westMinV = icon.getInterpolatedV(7.0D);
        double westMaxU = icon.getInterpolatedU(7.0D);
        double westMaxV = icon.getInterpolatedV(8.0D);
        double eastMinU = icon.getInterpolatedU(9.0D);
        double eastMinV = icon.getInterpolatedV(7.0D);
        double eastMaxU = icon.getInterpolatedU(10.0D);
        double eastMaxV = icon.getInterpolatedV(8.0D);

        this.riftflux$addQuad(
                tessellator,
                lowerMaxX, lowerY, lowerMaxZ, lowerCapMaxU, lowerCapMaxV,
                lowerMaxX, lowerY, lowerMinZ, lowerCapMaxU, lowerCapMinV,
                lowerMinX, lowerY, lowerMinZ, lowerCapMinU, lowerCapMinV,
                lowerMinX, lowerY, lowerMaxZ, lowerCapMinU, lowerCapMaxV
        );
        this.riftflux$addQuad(
                tessellator,
                upperMinX, upperY, upperMaxZ, upperCapMinU, upperCapMaxV,
                upperMinX, upperY, upperMinZ, upperCapMinU, upperCapMinV,
                upperMaxX, upperY, upperMinZ, upperCapMaxU, upperCapMinV,
                upperMaxX, upperY, upperMaxZ, upperCapMaxU, upperCapMaxV
        );

        this.riftflux$addQuad(
                tessellator,
                upperMinX, upperY, upperMinZ, southMinU, southMinV,
                lowerMinX, lowerY, lowerMinZ, southMinU, southMaxV,
                lowerMaxX, lowerY, lowerMinZ, southMaxU, southMaxV,
                upperMaxX, upperY, upperMinZ, southMaxU, southMinV
        );
        this.riftflux$addQuad(
                tessellator,
                upperMinX, upperY, upperMaxZ, northMinU, northMinV,
                upperMaxX, upperY, upperMaxZ, northMaxU, northMinV,
                lowerMaxX, lowerY, lowerMaxZ, northMaxU, northMaxV,
                lowerMinX, lowerY, lowerMaxZ, northMinU, northMaxV
        );
        this.riftflux$addQuad(
                tessellator,
                lowerMinX, lowerY, lowerMaxZ, eastMinU, eastMaxV,
                lowerMinX, lowerY, lowerMinZ, eastMaxU, eastMaxV,
                upperMinX, upperY, upperMinZ, eastMaxU, eastMinV,
                upperMinX, upperY, upperMaxZ, eastMinU, eastMinV
        );
        this.riftflux$addQuad(
                tessellator,
                upperMaxX, upperY, upperMaxZ, westMinU, westMinV,
                upperMaxX, upperY, upperMinZ, westMaxU, westMinV,
                lowerMaxX, lowerY, lowerMinZ, westMaxU, westMaxV,
                lowerMaxX, lowerY, lowerMaxZ, westMinU, westMaxV
        );
    }

    @Unique
    private void riftflux$addQuad(
            Tessellator tessellator,
            double x1, double y1, double z1, double u1, double v1,
            double x2, double y2, double z2, double u2, double v2,
            double x3, double y3, double z3, double u3, double v3,
            double x4, double y4, double z4, double u4, double v4) {
        tessellator.addVertexWithUV(x1, y1, z1, u1, v1);
        tessellator.addVertexWithUV(x2, y2, z2, u2, v2);
        tessellator.addVertexWithUV(x3, y3, z3, u3, v3);
        tessellator.addVertexWithUV(x4, y4, z4, u4, v4);
    }

    @Unique
    private void riftflux$addReversedQuad(
            Tessellator tessellator,
            double x1, double y1, double z1, double u1, double v1,
            double x2, double y2, double z2, double u2, double v2,
            double x3, double y3, double z3, double u3, double v3,
            double x4, double y4, double z4, double u4, double v4) {
        tessellator.addVertexWithUV(x4, y4, z4, u4, v4);
        tessellator.addVertexWithUV(x3, y3, z3, u3, v3);
        tessellator.addVertexWithUV(x2, y2, z2, u2, v2);
        tessellator.addVertexWithUV(x1, y1, z1, u1, v1);
    }

    @Unique
    private void riftflux$addDoubleSidedQuad(
            Tessellator tessellator,
            double x1, double y1, double z1, double u1, double v1,
            double x2, double y2, double z2, double u2, double v2,
            double x3, double y3, double z3, double u3, double v3,
            double x4, double y4, double z4, double u4, double v4) {
        this.riftflux$addQuad(tessellator, x1, y1, z1, u1, v1, x2, y2, z2, u2, v2, x3, y3, z3, u3, v3, x4, y4, z4, u4, v4);
        tessellator.addVertexWithUV(x4, y4, z4, u4, v4);
        tessellator.addVertexWithUV(x3, y3, z3, u3, v3);
        tessellator.addVertexWithUV(x2, y2, z2, u2, v2);
        tessellator.addVertexWithUV(x1, y1, z1, u1, v1);
    }

    @Unique
    private void riftflux$addDoubleSidedTorchQuad(
            Tessellator tessellator,
            boolean cameraSelectiveBackFaces,
            double x1, double y1, double z1, double u1, double v1,
            double x2, double y2, double z2, double u2, double v2,
            double x3, double y3, double z3, double u3, double v3,
            double x4, double y4, double z4, double u4, double v4) {
        if (cameraSelectiveBackFaces) {
            this.riftflux$addCameraSelectiveDoubleSidedQuad(
                    tessellator,
                    x1, y1, z1, u1, v1,
                    x2, y2, z2, u2, v2,
                    x3, y3, z3, u3, v3,
                    x4, y4, z4, u4, v4
            );
            return;
        }
        this.riftflux$addDoubleSidedQuad(
                tessellator,
                x1, y1, z1, u1, v1,
                x2, y2, z2, u2, v2,
                x3, y3, z3, u3, v3,
                x4, y4, z4, u4, v4
        );
    }

    @Unique
    private void riftflux$addCameraSelectiveDoubleSidedQuad(
            Tessellator tessellator,
            double x1, double y1, double z1, double u1, double v1,
            double x2, double y2, double z2, double u2, double v2,
            double x3, double y3, double z3, double u3, double v3,
            double x4, double y4, double z4, double u4, double v4) {
        this.riftflux$addQuad(
                tessellator,
                x1, y1, z1, u1, v1,
                x2, y2, z2, u2, v2,
                x3, y3, z3, u3, v3,
                x4, y4, z4, u4, v4
        );

        if (!this.riftflux$shouldAddCameraSelectiveBackFace(
                x1, y1, z1,
                x2, y2, z2,
                x3, y3, z3,
                x4, y4, z4)) {
            return;
        }

        tessellator.addVertexWithUV(x4, y4, z4, u4, v4);
        tessellator.addVertexWithUV(x3, y3, z3, u3, v3);
        tessellator.addVertexWithUV(x2, y2, z2, u2, v2);
        tessellator.addVertexWithUV(x1, y1, z1, u1, v1);
    }

    @Unique
    private boolean riftflux$shouldAddCameraSelectiveBackFace(
            double x1, double y1, double z1,
            double x2, double y2, double z2,
            double x3, double y3, double z3,
            double x4, double y4, double z4) {
        double dot = this.riftflux$getCameraFacingDot(
                x1, y1, z1,
                x2, y2, z2,
                x3, y3, z3,
                x4, y4, z4
        );
        return !Double.isNaN(dot) && dot <= 1.0E-7D;
    }

    @Unique
    private double riftflux$getCameraFacingDot(
            double x1, double y1, double z1,
            double x2, double y2, double z2,
            double x3, double y3, double z3,
            double x4, double y4, double z4) {
        Minecraft minecraft = Minecraft.getMinecraft();
        if (minecraft == null) {
            return Double.NaN;
        }

        Entity camera = minecraft.renderViewEntity;
        if (camera == null) {
            camera = minecraft.thePlayer;
        }
        if (camera == null) {
            return Double.NaN;
        }

        double edgeAx = x2 - x1;
        double edgeAy = y2 - y1;
        double edgeAz = z2 - z1;
        double edgeBx = x3 - x2;
        double edgeBy = y3 - y2;
        double edgeBz = z3 - z2;
        double normalX = edgeAy * edgeBz - edgeAz * edgeBy;
        double normalY = edgeAz * edgeBx - edgeAx * edgeBz;
        double normalZ = edgeAx * edgeBy - edgeAy * edgeBx;
        double normalLengthSq = normalX * normalX + normalY * normalY + normalZ * normalZ;
        if (normalLengthSq < 1.0E-12D) {
            return Double.NaN;
        }

        double centerX = (x1 + x2 + x3 + x4) * 0.25D;
        double centerY = (y1 + y2 + y3 + y4) * 0.25D;
        double centerZ = (z1 + z2 + z3 + z4) * 0.25D;
        double cameraY = camera.posY + camera.getEyeHeight();
        double dot = normalX * (camera.posX - centerX)
                + normalY * (cameraY - centerY)
                + normalZ * (camera.posZ - centerZ);
        return dot;
    }

    @Unique
    private void riftflux$renderSlantedCuboid(
            Tessellator tessellator,
            double x, double y, double z,
            double xTilt, double zTilt,
            double minHeight, double maxHeight,
            double halfWidth,
            double sideMinU, double sideMinV, double sideMaxU, double sideMaxV,
            double capMinU, double capMinV, double capMaxU, double capMaxV,
            boolean renderBottom, boolean renderTop) {
        this.riftflux$renderSlantedRectangularCuboid(
                tessellator,
                x, y, z,
                xTilt, zTilt,
                minHeight, maxHeight,
                halfWidth, halfWidth,
                sideMinU, sideMinV, sideMaxU, sideMaxV,
                capMinU, capMinV, capMaxU, capMaxV,
                renderBottom, renderTop, true
        );
    }

    @Unique
    private void riftflux$renderSlantedRectangularCuboid(
            Tessellator tessellator,
            double x, double y, double z,
            double xTilt, double zTilt,
            double minHeight, double maxHeight,
            double halfWidthX, double halfWidthZ,
            double sideMinU, double sideMinV, double sideMaxU, double sideMaxV,
            double capMinU, double capMinV, double capMaxU, double capMaxV,
            boolean renderBottom, boolean renderTop,
            boolean doubleSided) {
        double bottomCenterX = x + xTilt * (1.0D - minHeight);
        double bottomCenterZ = z + zTilt * (1.0D - minHeight);
        double topCenterX = x + xTilt * (1.0D - maxHeight);
        double topCenterZ = z + zTilt * (1.0D - maxHeight);

        double bottomY = y + minHeight;
        double topY = y + maxHeight;

        double topMinX = topCenterX - halfWidthX;
        double topMaxX = topCenterX + halfWidthX;
        double topMinZ = topCenterZ - halfWidthZ;
        double topMaxZ = topCenterZ + halfWidthZ;
        double bottomMinX = bottomCenterX - halfWidthX;
        double bottomMaxX = bottomCenterX + halfWidthX;
        double bottomMinZ = bottomCenterZ - halfWidthZ;
        double bottomMaxZ = bottomCenterZ + halfWidthZ;

        if (renderTop) {
            this.riftflux$addTorchQuad(
                    tessellator, doubleSided,
                    topMaxX, topY, topMaxZ, capMaxU, capMaxV,
                    topMaxX, topY, topMinZ, capMaxU, capMinV,
                    topMinX, topY, topMinZ, capMinU, capMinV,
                    topMinX, topY, topMaxZ, capMinU, capMaxV);
        }

        if (renderBottom) {
            this.riftflux$addTorchQuad(
                    tessellator, doubleSided,
                    bottomMinX, bottomY, bottomMaxZ, capMinU, capMaxV,
                    bottomMinX, bottomY, bottomMinZ, capMinU, capMinV,
                    bottomMaxX, bottomY, bottomMinZ, capMaxU, capMinV,
                    bottomMaxX, bottomY, bottomMaxZ, capMaxU, capMaxV);
        }

        this.riftflux$addTorchQuad(
                tessellator, doubleSided,
                topMinX, topY, topMinZ, sideMinU, sideMinV,
                topMaxX, topY, topMinZ, sideMaxU, sideMinV,
                bottomMaxX, bottomY, bottomMinZ, sideMaxU, sideMaxV,
                bottomMinX, bottomY, bottomMinZ, sideMinU, sideMaxV);
        this.riftflux$addTorchQuad(
                tessellator, doubleSided,
                topMinX, topY, topMaxZ, sideMinU, sideMinV,
                bottomMinX, bottomY, bottomMaxZ, sideMinU, sideMaxV,
                bottomMaxX, bottomY, bottomMaxZ, sideMaxU, sideMaxV,
                topMaxX, topY, topMaxZ, sideMaxU, sideMinV);
        this.riftflux$addTorchQuad(
                tessellator, doubleSided,
                topMinX, topY, topMaxZ, sideMinU, sideMinV,
                topMinX, topY, topMinZ, sideMaxU, sideMinV,
                bottomMinX, bottomY, bottomMinZ, sideMaxU, sideMaxV,
                bottomMinX, bottomY, bottomMaxZ, sideMinU, sideMaxV);
        this.riftflux$addTorchQuad(
                tessellator, doubleSided,
                bottomMaxX, bottomY, bottomMaxZ, sideMinU, sideMaxV,
                bottomMaxX, bottomY, bottomMinZ, sideMaxU, sideMaxV,
                topMaxX, topY, topMinZ, sideMaxU, sideMinV,
                topMaxX, topY, topMaxZ, sideMinU, sideMinV);
    }

    @Unique
    private void riftflux$addTorchQuad(
            Tessellator tessellator,
            boolean doubleSided,
            double x1, double y1, double z1, double u1, double v1,
            double x2, double y2, double z2, double u2, double v2,
            double x3, double y3, double z3, double u3, double v3,
            double x4, double y4, double z4, double u4, double v4) {
        if (doubleSided) {
            this.riftflux$addDoubleSidedQuad(tessellator, x1, y1, z1, u1, v1, x2, y2, z2, u2, v2, x3, y3, z3, u3, v3, x4, y4, z4, u4, v4);
            return;
        }
        this.riftflux$addQuad(tessellator, x1, y1, z1, u1, v1, x2, y2, z2, u2, v2, x3, y3, z3, u3, v3, x4, y4, z4, u4, v4);
    }
}
