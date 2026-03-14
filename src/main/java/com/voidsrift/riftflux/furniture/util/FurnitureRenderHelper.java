package com.voidsrift.riftflux.furniture.util;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;

public final class FurnitureRenderHelper {
    public static final int UP = 0;
    public static final int DOWN = 1;
    public static final int LEFT = 2;
    public static final int RIGHT = 3;

    private FurnitureRenderHelper() {
    }

    public static void setRenderBounds(RenderBlocks renderer,
                                       int blockMetadata,
                                       double x1,
                                       double y1,
                                       double z1,
                                       double x2,
                                       double y2,
                                       double z2) {
        double[] bounds = fixRotation(blockMetadata & 3, x1, z1, x2, z2);
        renderer.setRenderBounds(bounds[0], y1, bounds[1], bounds[2], y2, bounds[3]);
    }

    public static void renderBlock(RenderBlocks renderer, Block block, int x, int y, int z) {
        renderer.renderStandardBlock(block, x, y, z);
    }

    public static double[] fixRotation(int metadata, double x1, double z1, double x2, double z2) {
        switch (metadata & 3) {
            case 0: {
                double oldX1 = x1;
                double oldZ1 = z1;
                double oldX2 = x2;
                x1 = 1.0D - z2;
                z1 = oldX1;
                x2 = 1.0D - oldZ1;
                z2 = oldX2;
                break;
            }
            case 1: {
                double oldX1 = x1;
                double oldZ1 = z1;
                x1 = 1.0D - x2;
                z1 = 1.0D - z2;
                x2 = 1.0D - oldX1;
                z2 = 1.0D - oldZ1;
                break;
            }
            case 2: {
                double oldX1 = x1;
                x1 = z1;
                z1 = 1.0D - x2;
                x2 = z2;
                z2 = 1.0D - oldX1;
                break;
            }
            default:
                break;
        }
        return new double[] { x1, z1, x2, z2 };
    }

    public static Block getBlock(IBlockAccess world, int x, int y, int z, int metadata, int rotation) {
        switch (rotation) {
            case UP:
                if ((metadata & 3) == 3) return world.getBlock(x + 1, y, z);
                if ((metadata & 3) == 1) return world.getBlock(x - 1, y, z);
                if ((metadata & 3) == 2) return world.getBlock(x, y, z - 1);
                if ((metadata & 3) == 0) return world.getBlock(x, y, z + 1);
                break;
            case DOWN:
                if ((metadata & 3) == 3) return world.getBlock(x - 1, y, z);
                if ((metadata & 3) == 1) return world.getBlock(x + 1, y, z);
                if ((metadata & 3) == 2) return world.getBlock(x, y, z + 1);
                if ((metadata & 3) == 0) return world.getBlock(x, y, z - 1);
                break;
            case LEFT:
                if ((metadata & 3) == 2) return world.getBlock(x - 1, y, z);
                if ((metadata & 3) == 0) return world.getBlock(x + 1, y, z);
                if ((metadata & 3) == 3) return world.getBlock(x, y, z - 1);
                if ((metadata & 3) == 1) return world.getBlock(x, y, z + 1);
                break;
            case RIGHT:
                if ((metadata & 3) == 2) return world.getBlock(x + 1, y, z);
                if ((metadata & 3) == 0) return world.getBlock(x - 1, y, z);
                if ((metadata & 3) == 3) return world.getBlock(x, y, z + 1);
                if ((metadata & 3) == 1) return world.getBlock(x, y, z - 1);
                break;
            default:
                break;
        }
        return world.getBlock(x, y, z);
    }

    public static int getMetadata(IBlockAccess world, int x, int y, int z, int metadata, int rotation) {
        switch (rotation) {
            case UP:
                if ((metadata & 3) == 3) return world.getBlockMetadata(x + 1, y, z);
                if ((metadata & 3) == 1) return world.getBlockMetadata(x - 1, y, z);
                if ((metadata & 3) == 2) return world.getBlockMetadata(x, y, z - 1);
                if ((metadata & 3) == 0) return world.getBlockMetadata(x, y, z + 1);
                break;
            case DOWN:
                if ((metadata & 3) == 3) return world.getBlockMetadata(x - 1, y, z);
                if ((metadata & 3) == 1) return world.getBlockMetadata(x + 1, y, z);
                if ((metadata & 3) == 2) return world.getBlockMetadata(x, y, z + 1);
                if ((metadata & 3) == 0) return world.getBlockMetadata(x, y, z - 1);
                break;
            case LEFT:
                if ((metadata & 3) == 2) return world.getBlockMetadata(x - 1, y, z);
                if ((metadata & 3) == 0) return world.getBlockMetadata(x + 1, y, z);
                if ((metadata & 3) == 3) return world.getBlockMetadata(x, y, z - 1);
                if ((metadata & 3) == 1) return world.getBlockMetadata(x, y, z + 1);
                break;
            case RIGHT:
                if ((metadata & 3) == 2) return world.getBlockMetadata(x + 1, y, z);
                if ((metadata & 3) == 0) return world.getBlockMetadata(x - 1, y, z);
                if ((metadata & 3) == 3) return world.getBlockMetadata(x, y, z + 1);
                if ((metadata & 3) == 1) return world.getBlockMetadata(x, y, z - 1);
                break;
            default:
                break;
        }
        return world.getBlockMetadata(x, y, z);
    }

    public static int getRotation(IBlockAccess world, int x, int y, int z, int metadata, int rotation) {
        int blockMetadata = getMetadata(world, x, y, z, metadata, rotation) & 3;
        metadata &= 3;
        if (metadata == 3) {
            if (blockMetadata == 3) return 1;
            if (blockMetadata == 1) return 0;
            if (blockMetadata == 2) return 3;
            if (blockMetadata == 0) return 2;
        }
        if (metadata == 1) {
            if (blockMetadata == 3) return 0;
            if (blockMetadata == 1) return 1;
            if (blockMetadata == 2) return 2;
            if (blockMetadata == 0) return 3;
        }
        if (metadata == 2) {
            if (blockMetadata == 3) return 2;
            if (blockMetadata == 1) return 3;
            if (blockMetadata == 2) return 1;
            if (blockMetadata == 0) return 0;
        }
        if (metadata == 0) {
            if (blockMetadata == 3) return 3;
            if (blockMetadata == 1) return 2;
            if (blockMetadata == 2) return 0;
            if (blockMetadata == 0) return 1;
        }
        return 0;
    }
}
