package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.ModConfig;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
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
        boolean useModernTorchModel = ModConfig.modernTorchRendering && this.riftflux$isModernTorchRenderingEligible(block, meta);
        if (!ModConfig.doubleSidedTorchRendering && !useModernTorchModel) {
            return;
        }

        if (useModernTorchModel) {
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
        return riftflux$modernTorchFilterAllows(block);
    }

    @Unique
    private static boolean riftflux$modernTorchFilterAllows(Block block) {
        boolean matched = riftflux$matchesModernTorchFilter(block);
        return ModConfig.modernTorchRenderingWhitelistMode ? matched : !matched;
    }

    @Unique
    private static boolean riftflux$matchesModernTorchFilter(Block block) {
        String[] entries = ModConfig.modernTorchRenderingFilter;
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

        this.riftflux$addDoubleSidedQuad(
                tessellator,
                topOffsetX - halfWidth, y + topHeight, topOffsetZ - halfWidth, capMinU, capMinV,
                topOffsetX - halfWidth, y + topHeight, topOffsetZ + halfWidth, capMinU, capMaxV,
                topOffsetX + halfWidth, y + topHeight, topOffsetZ + halfWidth, capMaxU, capMaxV,
                topOffsetX + halfWidth, y + topHeight, topOffsetZ - halfWidth, capMaxU, capMinV
        );

        this.riftflux$addDoubleSidedQuad(
                tessellator,
                x + halfWidth + xTilt, y, z - halfWidth + zTilt, stemMaxU, stemMinV,
                x + halfWidth + xTilt, y, z + halfWidth + zTilt, stemMaxU, stemMaxV,
                x - halfWidth + xTilt, y, z + halfWidth + zTilt, stemMinU, stemMaxV,
                x - halfWidth + xTilt, y, z - halfWidth + zTilt, stemMinU, stemMinV
        );

        this.riftflux$addDoubleSidedQuad(
                tessellator,
                x - halfWidth, y + 1.0D, zMin, minU, minV,
                x - halfWidth + xTilt, y, zMin + zTilt, minU, maxV,
                x - halfWidth + xTilt, y, zMax + zTilt, maxU, maxV,
                x - halfWidth, y + 1.0D, zMax, maxU, minV
        );

        this.riftflux$addDoubleSidedQuad(
                tessellator,
                x + halfWidth, y + 1.0D, zMax, minU, minV,
                x + xTilt + halfWidth, y, zMax + zTilt, minU, maxV,
                x + xTilt + halfWidth, y, zMin + zTilt, maxU, maxV,
                x + halfWidth, y + 1.0D, zMin, maxU, minV
        );

        this.riftflux$addDoubleSidedQuad(
                tessellator,
                xMin, y + 1.0D, z + halfWidth, minU, minV,
                xMin + xTilt, y, z + halfWidth + zTilt, minU, maxV,
                xMax + xTilt, y, z + halfWidth + zTilt, maxU, maxV,
                xMax, y + 1.0D, z + halfWidth, maxU, minV
        );

        this.riftflux$addDoubleSidedQuad(
                tessellator,
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
