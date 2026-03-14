package com.voidsrift.riftflux.furniture.client.render;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.init.Blocks;
import net.minecraft.world.IBlockAccess;

public class RenderStonePath implements ISimpleBlockRenderingHandler {
    private final int renderId;

    public RenderStonePath(int renderId) {
        this.renderId = renderId;
    }

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
        renderer.setOverrideBlockTexture(Blocks.stone.getBlockTextureFromSide(0));
        if (world.getBlockMetadata(x, y, z) == 0) {
            renderer.setRenderBounds(0.0625D, 0.0D, 0.0625D, 0.375D, 0.03125D, 0.375D);
            renderer.renderStandardBlock(block, x, y, z);
            renderer.setRenderBounds(0.125D, 0.0D, 0.5D, 0.375D, 0.03125D, 0.75D);
            renderer.renderStandardBlock(block, x, y, z);
            renderer.setRenderBounds(0.0625D, 0.0D, 0.8125D, 0.1875D, 0.03125D, 0.9275D);
            renderer.renderStandardBlock(block, x, y, z);
            renderer.setRenderBounds(0.4375D, 0.0D, 0.25D, 0.75D, 0.03125D, 0.5625D);
            renderer.renderStandardBlock(block, x, y, z);
            renderer.setRenderBounds(0.4375D, 0.0D, 0.75D, 0.625D, 0.03125D, 0.9375D);
            renderer.renderStandardBlock(block, x, y, z);
            renderer.setRenderBounds(0.625D, 0.0D, 0.0625D, 0.75D, 0.03125D, 0.1875D);
            renderer.renderStandardBlock(block, x, y, z);
            renderer.setRenderBounds(0.6875D, 0.0D, 0.625D, 0.875D, 0.03125D, 0.8125D);
            renderer.renderStandardBlock(block, x, y, z);
            renderer.setRenderBounds(0.8125D, 0.0D, 0.25D, 0.9375D, 0.03125D, 0.375D);
            renderer.renderStandardBlock(block, x, y, z);
        } else {
            renderer.setRenderBounds(0.1875D, 0.0D, 0.0625D, 0.375D, 0.03125D, 0.25D);
            renderer.renderStandardBlock(block, x, y, z);
            renderer.setRenderBounds(0.0625D, 0.0D, 0.375D, 0.3125D, 0.03125D, 0.625D);
            renderer.renderStandardBlock(block, x, y, z);
            renderer.setRenderBounds(0.125D, 0.0D, 0.6875D, 0.3125D, 0.03125D, 0.875D);
            renderer.renderStandardBlock(block, x, y, z);
            renderer.setRenderBounds(0.4375D, 0.0D, 0.5625D, 0.5625D, 0.03125D, 0.75D);
            renderer.renderStandardBlock(block, x, y, z);
            renderer.setRenderBounds(0.5D, 0.0D, 0.8125D, 0.625D, 0.03125D, 0.9375D);
            renderer.renderStandardBlock(block, x, y, z);
            renderer.setRenderBounds(0.625D, 0.0D, 0.125D, 0.875D, 0.03125D, 0.375D);
            renderer.renderStandardBlock(block, x, y, z);
            renderer.setRenderBounds(0.6875D, 0.0D, 0.4375D, 0.875D, 0.03125D, 0.625D);
            renderer.renderStandardBlock(block, x, y, z);
            renderer.setRenderBounds(0.6875D, 0.0D, 0.6875D, 0.9375D, 0.03125D, 0.9375D);
            renderer.renderStandardBlock(block, x, y, z);
        }
        renderer.clearOverrideBlockTexture();
        return true;
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
