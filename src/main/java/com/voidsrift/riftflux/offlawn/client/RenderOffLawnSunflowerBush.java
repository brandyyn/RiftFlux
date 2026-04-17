package com.voidsrift.riftflux.offlawn.client;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.mixin.accessor.ChunkCacheAccessor;
import com.voidsrift.riftflux.mixin.accessor.angelica.WorldSliceAccessor;
import com.voidsrift.riftflux.offlawn.OffLawnRenderIds;
import com.voidsrift.riftflux.util.RFPlantContext;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class RenderOffLawnSunflowerBush implements ISimpleBlockRenderingHandler {
    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
        if (modelId != OffLawnRenderIds.sunflowerBushRenderId) {
            return false;
        }
        Tessellator tessellator = Tessellator.instance;
        int brightness = block.getMixedBrightnessForBlock(world, x, y, z);
        int color = block.colorMultiplier(world, x, y, z);
        float red = ((color >> 16) & 255) / 255.0F;
        float green = ((color >> 8) & 255) / 255.0F;
        float blue = (color & 255) / 255.0F;
        tessellator.setBrightness(brightness);
        tessellator.setColorOpaque_F(red, green, blue);

        IIcon icon = renderer.getBlockIconFromSideAndMetadata(block, 0, world.getBlockMetadata(x, y, z));
        if (icon == null) {
            return true;
        }

        if (ModConfig.directionalCrossedPlantRenderingByPlacement) {
            int anchorY = getFacingAnchorY(world, x, y, z);
            int fallbackFacing = (int) (((long) (x * 73428767) ^ (long) (anchorY * 912367) ^ (long) (z * 1315423911)) & 3L);
            int facing = fallbackFacing;
            if (ModConfig.directionalCrossedPlantFacePlayerOnPlacement) {
                facing = RFPlantContext.getCrossedPlantFacing(resolveWorld(world), x, anchorY, z, fallbackFacing);
            }
            renderDirectionalPlane(tessellator, x, y, z, icon, facing);
        } else {
            renderFixedCross(tessellator, x, y, z, icon);
        }
        return true;
    }

    @Override
    public boolean shouldRender3DInInventory(int modelId) {
        return false;
    }

    @Override
    public int getRenderId() {
        return OffLawnRenderIds.sunflowerBushRenderId;
    }

    private static World resolveWorld(IBlockAccess access) {
        if (access instanceof World) {
            return (World) access;
        }
        if (access instanceof WorldSliceAccessor) {
            return ((WorldSliceAccessor) access).riftflux$getWorld();
        }
        if (access instanceof ChunkCache) {
            try {
                return ((ChunkCacheAccessor) access).riftflux$getWorldObj();
            } catch (Throwable ignored) {
                return null;
            }
        }
        return null;
    }

    private static void renderFixedCross(Tessellator tessellator, int x, int y, int z, IIcon icon) {
        double minU = icon.getMinU();
        double minV = icon.getMinV();
        double maxU = icon.getMaxU();
        double maxV = icon.getMaxV();

        double half = 0.45D;
        double cx = x + 0.5D;
        double cz = z + 0.5D;
        double y0 = y;
        double y1 = y + 1.0D;

        double ax = cx - half;
        double bx = cx + half;
        double az = cz - half;
        double bz = cz + half;

        // Diagonal plane: (ax, az) -> (bx, bz), front.
        addQuad(
                tessellator,
                ax, y1, az, minU, minV,
                ax, y0, az, minU, maxV,
                bx, y0, bz, maxU, maxV,
                bx, y1, bz, maxU, minV
        );
        // Diagonal plane: back (non-mirrored UVs).
        addQuad(
                tessellator,
                bx, y1, bz, maxU, minV,
                bx, y0, bz, maxU, maxV,
                ax, y0, az, minU, maxV,
                ax, y1, az, minU, minV
        );

        // Opposite diagonal plane: (ax, bz) -> (bx, az), front.
        addQuad(
                tessellator,
                ax, y1, bz, minU, minV,
                ax, y0, bz, minU, maxV,
                bx, y0, az, maxU, maxV,
                bx, y1, az, maxU, minV
        );
        // Opposite diagonal plane: back (non-mirrored UVs).
        addQuad(
                tessellator,
                bx, y1, az, maxU, minV,
                bx, y0, az, maxU, maxV,
                ax, y0, bz, minU, maxV,
                ax, y1, bz, minU, minV
        );
    }

    private static void renderDirectionalPlane(Tessellator tessellator, int x, int y, int z, IIcon icon, int facing) {
        double minU = icon.getMinU();
        double minV = icon.getMinV();
        double maxU = icon.getMaxU();
        double maxV = icon.getMaxV();

        double half = 0.45D;
        double centerX = x + 0.5D;
        double centerZ = z + 0.5D;
        double y0 = y;
        double y1 = y + 1.0D;
        double diagonal = Math.sqrt(0.5D);
        int cardinalFacing = facing & 3;
        switch (cardinalFacing) {
            case 0:
                renderTwoSidedPlane(tessellator, centerX, centerZ, y0, y1, half, -diagonal, -diagonal, minU, minV, maxU, maxV);
                renderTwoSidedPlane(tessellator, centerX, centerZ, y0, y1, half, diagonal, -diagonal, minU, minV, maxU, maxV);
                break;
            case 1:
                renderTwoSidedPlane(tessellator, centerX, centerZ, y0, y1, half, diagonal, -diagonal, minU, minV, maxU, maxV);
                renderTwoSidedPlane(tessellator, centerX, centerZ, y0, y1, half, diagonal, diagonal, minU, minV, maxU, maxV);
                break;
            case 2:
                renderTwoSidedPlane(tessellator, centerX, centerZ, y0, y1, half, diagonal, diagonal, minU, minV, maxU, maxV);
                renderTwoSidedPlane(tessellator, centerX, centerZ, y0, y1, half, -diagonal, diagonal, minU, minV, maxU, maxV);
                break;
            default:
                renderTwoSidedPlane(tessellator, centerX, centerZ, y0, y1, half, -diagonal, diagonal, minU, minV, maxU, maxV);
                renderTwoSidedPlane(tessellator, centerX, centerZ, y0, y1, half, -diagonal, -diagonal, minU, minV, maxU, maxV);
                break;
        }
    }

    private static void renderTwoSidedPlane(Tessellator tessellator,
                                            double centerX, double centerZ, double y0, double y1,
                                            double half, double frontX, double frontZ,
                                            double minU, double minV, double maxU, double maxV) {
        double tangentX = frontZ;
        double tangentZ = -frontX;
        double x1 = centerX - tangentX * half;
        double z1 = centerZ - tangentZ * half;
        double x2 = centerX + tangentX * half;
        double z2 = centerZ + tangentZ * half;
        addQuad(
                tessellator,
                x1, y1, z1, minU, minV,
                x1, y0, z1, minU, maxV,
                x2, y0, z2, maxU, maxV,
                x2, y1, z2, maxU, minV
        );
        addQuad(
                tessellator,
                x2, y1, z2, maxU, minV,
                x2, y0, z2, maxU, maxV,
                x1, y0, z1, minU, maxV,
                x1, y1, z1, minU, minV
        );
    }

    private static int getFacingAnchorY(IBlockAccess world, int x, int y, int z) {
        if (world == null) {
            return y;
        }
        try {
            if (world.getBlock(x, y, z) instanceof BlockDoublePlant) {
                int meta = world.getBlockMetadata(x, y, z);
                if (BlockDoublePlant.func_149887_c(meta)) {
                    return y - 1;
                }
            }
        } catch (Throwable ignored) {
        }
        return y;
    }

    private static void addQuad(Tessellator tessellator,
                                double x1, double y1, double z1, double u1, double v1,
                                double x2, double y2, double z2, double u2, double v2,
                                double x3, double y3, double z3, double u3, double v3,
                                double x4, double y4, double z4, double u4, double v4) {
        tessellator.addVertexWithUV(x1, y1, z1, u1, v1);
        tessellator.addVertexWithUV(x2, y2, z2, u2, v2);
        tessellator.addVertexWithUV(x3, y3, z3, u3, v3);
        tessellator.addVertexWithUV(x4, y4, z4, u4, v4);
    }
}
