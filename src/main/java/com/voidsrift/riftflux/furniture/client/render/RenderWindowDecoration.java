package com.voidsrift.riftflux.furniture.client.render;

import com.voidsrift.riftflux.furniture.block.BlockFurnitureWindowDecoration;
import com.voidsrift.riftflux.furniture.util.FurnitureRenderHelper;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.init.Blocks;
import net.minecraft.world.IBlockAccess;

public class RenderWindowDecoration implements ISimpleBlockRenderingHandler {
    private final int renderId;

    public RenderWindowDecoration(int renderId) {
        this.renderId = renderId;
    }

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
        BlockFurnitureWindowDecoration decoration = (BlockFurnitureWindowDecoration) block;
        int metadata = world.getBlockMetadata(x, y, z);
        int rotation = decoration.getRotation(metadata);
        boolean closed = decoration.isClosed(metadata);

        if (decoration.isBlind()) {
            this.renderBlinds(world, x, y, z, block, renderer, rotation, closed);
        } else {
            renderer.setOverrideBlockTexture(Blocks.planks.getIcon(0, 0));
            FurnitureRenderHelper.setRenderBounds(renderer, rotation, 0.875D, 0.875D, 0.0D, 1.0D, 1.0D, 1.0D);
            FurnitureRenderHelper.renderBlock(renderer, block, x, y, z);
            this.renderCurtains(world, x, y, z, block, renderer, rotation, closed);
        }

        renderer.clearOverrideBlockTexture();
        return true;
    }

    private void renderBlinds(IBlockAccess world,
                              int x,
                              int y,
                              int z,
                              Block block,
                              RenderBlocks renderer,
                              int rotation,
                              boolean closed) {
        boolean topBlind = this.isTopBlind(world, x, y, z, rotation);

        if (topBlind) {
            renderer.setOverrideBlockTexture(Blocks.log2.getIcon(2, 1));
            FurnitureRenderHelper.setRenderBounds(renderer, rotation, 0.875D, 0.875D, 0.0D, 1.0D, 1.0D, 1.0D);
            FurnitureRenderHelper.renderBlock(renderer, block, x, y, z);
        }

        renderer.setOverrideBlockTexture(Blocks.planks.getBlockTextureFromSide(0));
        if (closed) {
            FurnitureRenderHelper.setRenderBounds(renderer, rotation, 0.9375D, 0.0D, 0.0D, 1.0D, topBlind ? 0.875D : 0.9375D, 1.0D);
            FurnitureRenderHelper.renderBlock(renderer, block, x, y, z);
        } else {
            this.renderBlindSlat(renderer, block, x, y, z, rotation, 0.0D, 0.0625D);
            this.renderBlindSlat(renderer, block, x, y, z, rotation, 0.125D, 0.1875D);
            this.renderBlindSlat(renderer, block, x, y, z, rotation, 0.25D, 0.3125D);
            this.renderBlindSlat(renderer, block, x, y, z, rotation, 0.375D, 0.4375D);
            this.renderBlindSlat(renderer, block, x, y, z, rotation, 0.5D, 0.5625D);
            this.renderBlindSlat(renderer, block, x, y, z, rotation, 0.625D, 0.6875D);
            this.renderBlindSlat(renderer, block, x, y, z, rotation, 0.75D, 0.8125D);
            if (!topBlind) {
                this.renderBlindSlat(renderer, block, x, y, z, rotation, 0.875D, 0.9375D);
            }
        }

        if (topBlind && this.shouldRenderBlindCord(world, x, y, z, rotation)) {
            renderer.setOverrideBlockTexture(Blocks.wool.getIcon(0, 0));
            if (closed) {
                FurnitureRenderHelper.setRenderBounds(renderer, rotation, 0.90625D, 0.5D, 0.046875D, 0.9375D, 0.875D, 0.078125D);
            } else {
                FurnitureRenderHelper.setRenderBounds(renderer, rotation, 0.90625D, 0.124D, 0.04687D, 0.9375D, 0.875D, 0.078125D);
            }
            FurnitureRenderHelper.renderBlock(renderer, block, x, y, z);
        }
    }

    private boolean isTopBlind(IBlockAccess world, int x, int y, int z, int rotation) {
        Block blockAbove = world.getBlock(x, y + 1, z);
        if (!(blockAbove instanceof BlockFurnitureWindowDecoration)) {
            return true;
        }
        BlockFurnitureWindowDecoration decorationAbove = (BlockFurnitureWindowDecoration) blockAbove;
        return !decorationAbove.isBlind() || decorationAbove.getRotation(world.getBlockMetadata(x, y + 1, z)) != rotation;
    }

    private void renderBlindSlat(RenderBlocks renderer,
                                 Block block,
                                 int x,
                                 int y,
                                 int z,
                                 int rotation,
                                 double minY,
                                 double maxY) {
        FurnitureRenderHelper.setRenderBounds(renderer, rotation, 0.9375D, minY, 0.0D, 1.0D, maxY, 1.0D);
        FurnitureRenderHelper.renderBlock(renderer, block, x, y, z);
    }

    private boolean shouldRenderBlindCord(IBlockAccess world, int x, int y, int z, int rotation) {
        Block neighbor = FurnitureRenderHelper.getBlock(world, x, y, z, rotation, FurnitureRenderHelper.LEFT);
        if (!(neighbor instanceof BlockFurnitureWindowDecoration)) {
            return true;
        }
        BlockFurnitureWindowDecoration other = (BlockFurnitureWindowDecoration) neighbor;
        return !other.isBlind()
                || FurnitureRenderHelper.getRotation(world, x, y, z, rotation, FurnitureRenderHelper.LEFT) != 1;
    }

    private void renderCurtains(IBlockAccess world,
                                int x,
                                int y,
                                int z,
                                Block block,
                                RenderBlocks renderer,
                                int rotation,
                                boolean closed) {
        BlockFurnitureWindowDecoration decoration = (BlockFurnitureWindowDecoration) block;
        int curtainColor = decoration.getCurtainColor();

        if (closed) {
            renderer.setOverrideBlockTexture(Blocks.wool.getIcon(0, curtainColor));
            FurnitureRenderHelper.setRenderBounds(renderer, rotation, 0.9375D, 0.0625D, 0.0D, 1.0D, 0.875D, 1.0001D);
            FurnitureRenderHelper.renderBlock(renderer, block, x, y, z);
            renderer.setOverrideBlockTexture(Blocks.gold_block.getBlockTextureFromSide(0));
            this.renderClosedCurtainTrim(block, renderer, x, y, z, rotation);
            return;
        }

        renderer.setOverrideBlockTexture(Blocks.wool.getIcon(0, curtainColor));
        this.renderOpenCurtainSide(world, x, y, z, block, renderer, rotation, FurnitureRenderHelper.LEFT);
        this.renderOpenCurtainSide(world, x, y, z, block, renderer, rotation, FurnitureRenderHelper.RIGHT);

        renderer.setOverrideBlockTexture(Blocks.gold_block.getBlockTextureFromSide(0));
        if (this.shouldRenderOpenCurtainTie(world, x, y, z, rotation, FurnitureRenderHelper.LEFT)) {
            this.renderTrimBand(block, renderer, x, y, z, rotation, 0.25D, 0.3125D, 0.0D, 0.125D);
        }
        if (this.shouldRenderOpenCurtainTie(world, x, y, z, rotation, FurnitureRenderHelper.RIGHT)) {
            this.renderTrimBand(block, renderer, x, y, z, rotation, 0.25D, 0.3125D, 0.875D, 1.0D);
        }
    }

    private void renderClosedCurtainTrim(Block block,
                                         RenderBlocks renderer,
                                         int x,
                                         int y,
                                         int z,
                                         int rotation) {
        this.renderTrimBand(block, renderer, x, y, z, rotation, 0.1875D, 0.25D, 0.0D, 1.0D);
    }

    private void renderTrimBand(Block block,
                                RenderBlocks renderer,
                                int x,
                                int y,
                                int z,
                                int rotation,
                                double minY,
                                double maxY,
                                double minZ,
                                double maxZ) {
        FurnitureRenderHelper.setRenderBounds(renderer, rotation, 0.9345D, minY, minZ, 0.9374D, maxY, maxZ);
        FurnitureRenderHelper.renderBlock(renderer, block, x, y, z);
        FurnitureRenderHelper.setRenderBounds(renderer, rotation, 1.0001D, minY, minZ, 1.0030D, maxY, maxZ);
        FurnitureRenderHelper.renderBlock(renderer, block, x, y, z);
    }

    private void renderOpenCurtainSide(IBlockAccess world,
                                       int x,
                                       int y,
                                       int z,
                                       Block block,
                                       RenderBlocks renderer,
                                       int rotation,
                                       int side) {
        if (!this.shouldRenderOpenCurtainSide(world, x, y, z, rotation, side)) {
            return;
        }

        boolean closedNeighbor = this.hasClosedCurtainNeighbor(world, x, y, z, rotation, side);
        double widthFix = closedNeighbor ? 0.125D : 0.0D;
        double heightFix = closedNeighbor ? 0.0625D : 0.0D;

        if (side == FurnitureRenderHelper.LEFT) {
            FurnitureRenderHelper.setRenderBounds(renderer, rotation, 0.9375D, 0.125D, 0.0D, 1.0D, 0.25D, 0.1875D - widthFix);
            FurnitureRenderHelper.renderBlock(renderer, block, x, y, z);
            FurnitureRenderHelper.setRenderBounds(renderer, rotation, 0.9375D, 0.3125D - heightFix, 0.0D, 1.0D, 0.375D, 0.125D);
            FurnitureRenderHelper.renderBlock(renderer, block, x, y, z);
            FurnitureRenderHelper.setRenderBounds(renderer, rotation, 0.9375D, 0.375D, 0.0D, 1.0D, 0.5D, 0.1875D);
            FurnitureRenderHelper.renderBlock(renderer, block, x, y, z);
            FurnitureRenderHelper.setRenderBounds(renderer, rotation, 0.9375D, 0.5D, 0.0D, 1.0D, 0.6875D, 0.25D);
            FurnitureRenderHelper.renderBlock(renderer, block, x, y, z);
            FurnitureRenderHelper.setRenderBounds(renderer, rotation, 0.9375D, 0.6875D, 0.0D, 1.0D, 0.875D, 0.3125D);
            FurnitureRenderHelper.renderBlock(renderer, block, x, y, z);
            if (!closedNeighbor) {
                FurnitureRenderHelper.setRenderBounds(renderer, rotation, 0.9375D, 0.25D, 0.0D, 1.0D, 0.3125D, 0.125D);
                FurnitureRenderHelper.renderBlock(renderer, block, x, y, z);
            }
        } else {
            FurnitureRenderHelper.setRenderBounds(renderer, rotation, 0.9375D, 0.125D, 0.8125D + widthFix, 1.0D, 0.25D, 1.0D);
            FurnitureRenderHelper.renderBlock(renderer, block, x, y, z);
            FurnitureRenderHelper.setRenderBounds(renderer, rotation, 0.9375D, 0.3125D - heightFix, 0.875D, 1.0D, 0.375D, 1.0D);
            FurnitureRenderHelper.renderBlock(renderer, block, x, y, z);
            FurnitureRenderHelper.setRenderBounds(renderer, rotation, 0.9375D, 0.375D, 0.8125D, 1.0D, 0.5D, 1.0D);
            FurnitureRenderHelper.renderBlock(renderer, block, x, y, z);
            FurnitureRenderHelper.setRenderBounds(renderer, rotation, 0.9375D, 0.5D, 0.75D, 1.0D, 0.6875D, 1.0D);
            FurnitureRenderHelper.renderBlock(renderer, block, x, y, z);
            FurnitureRenderHelper.setRenderBounds(renderer, rotation, 0.9375D, 0.6875D, 0.6875D, 1.0D, 0.875D, 1.0D);
            FurnitureRenderHelper.renderBlock(renderer, block, x, y, z);
            if (!closedNeighbor) {
                FurnitureRenderHelper.setRenderBounds(renderer, rotation, 0.9375D, 0.25D, 0.875D, 1.0D, 0.3125D, 1.0D);
                FurnitureRenderHelper.renderBlock(renderer, block, x, y, z);
            }
        }
    }

    private boolean shouldRenderOpenCurtainSide(IBlockAccess world, int x, int y, int z, int rotation, int side) {
        Block neighbor = FurnitureRenderHelper.getBlock(world, x, y, z, rotation, side);
        if (!(neighbor instanceof BlockFurnitureWindowDecoration)) {
            return true;
        }
        BlockFurnitureWindowDecoration decoration = (BlockFurnitureWindowDecoration) neighbor;
        return decoration.isBlind()
                || FurnitureRenderHelper.getRotation(world, x, y, z, rotation, side) != 1;
    }

    private boolean hasClosedCurtainNeighbor(IBlockAccess world, int x, int y, int z, int rotation, int side) {
        Block neighbor = FurnitureRenderHelper.getBlock(world, x, y, z, rotation, side);
        if (!(neighbor instanceof BlockFurnitureWindowDecoration)) {
            return false;
        }
        BlockFurnitureWindowDecoration decoration = (BlockFurnitureWindowDecoration) neighbor;
        return !decoration.isBlind()
                && decoration.isClosed(FurnitureRenderHelper.getMetadata(world, x, y, z, rotation, side))
                && FurnitureRenderHelper.getRotation(world, x, y, z, rotation, side) == 1;
    }

    private boolean shouldRenderOpenCurtainTie(IBlockAccess world, int x, int y, int z, int rotation, int side) {
        return this.shouldRenderOpenCurtainSide(world, x, y, z, rotation, side)
                && !this.hasClosedCurtainNeighbor(world, x, y, z, rotation, side);
    }

    @Override
    public int getRenderId() {
        return this.renderId;
    }

    @Override
    public boolean shouldRender3DInInventory(int modelId) {
        return false;
    }
}
