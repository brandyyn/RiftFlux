package com.voidsrift.riftflux.furniture.client.render;

import com.voidsrift.riftflux.furniture.block.BlockFurnitureFence;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.init.Blocks;
import net.minecraft.world.IBlockAccess;

public class RenderFurnitureFence implements ISimpleBlockRenderingHandler {
    private final int renderId;

    public RenderFurnitureFence(int renderId) {
        this.renderId = renderId;
    }

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
        BlockFurnitureFence fence = (BlockFurnitureFence) block;
        renderer.setOverrideBlockTexture(
                fence.isDark()
                        ? Blocks.anvil.getBlockTextureFromSide(2)
                        : Blocks.quartz_block.getBlockTextureFromSide(0)
        );

        renderer.setRenderBounds(0.4375D, 0.0D, 0.4375D, 0.5625D, 1.1D, 0.5625D);
        renderer.renderStandardBlock(block, x, y, z);

        if (fence.connectsTo(world, x + 1, y, z)) {
            renderer.setRenderBounds(0.75D, 0.0D, 0.46875D, 0.875D, 1.0D, 0.53125D);
            renderer.renderStandardBlock(block, x, y, z);
            renderer.setRenderBounds(0.5D, 0.125D, 0.46875D, 1.0D, 0.25D, 0.53125D);
            renderer.renderStandardBlock(block, x, y, z);
            renderer.setRenderBounds(0.5D, 0.75D, 0.46875D, 1.0D, 0.875D, 0.53125D);
            renderer.renderStandardBlock(block, x, y, z);
        }

        if (fence.connectsTo(world, x - 1, y, z)) {
            renderer.setRenderBounds(0.125D, 0.0D, 0.46875D, 0.25D, 1.0D, 0.53125D);
            renderer.renderStandardBlock(block, x, y, z);
            renderer.setRenderBounds(0.0D, 0.125D, 0.46875D, 0.5D, 0.25D, 0.53125D);
            renderer.renderStandardBlock(block, x, y, z);
            renderer.setRenderBounds(0.0D, 0.75D, 0.46875D, 0.5D, 0.875D, 0.53125D);
            renderer.renderStandardBlock(block, x, y, z);
        }

        if (fence.connectsTo(world, x, y, z + 1)) {
            renderer.setRenderBounds(0.46875D, 0.0D, 0.75D, 0.53125D, 1.0D, 0.875D);
            renderer.renderStandardBlock(block, x, y, z);
            renderer.setRenderBounds(0.46875D, 0.125D, 0.5D, 0.53125D, 0.25D, 1.0D);
            renderer.renderStandardBlock(block, x, y, z);
            renderer.setRenderBounds(0.46875D, 0.75D, 0.5D, 0.53125D, 0.875D, 1.0D);
            renderer.renderStandardBlock(block, x, y, z);
        }

        if (fence.connectsTo(world, x, y, z - 1)) {
            renderer.setRenderBounds(0.46875D, 0.0D, 0.125D, 0.53125D, 1.0D, 0.25D);
            renderer.renderStandardBlock(block, x, y, z);
            renderer.setRenderBounds(0.46875D, 0.125D, 0.0D, 0.53125D, 0.25D, 0.5D);
            renderer.renderStandardBlock(block, x, y, z);
            renderer.setRenderBounds(0.46875D, 0.75D, 0.0D, 0.53125D, 0.875D, 0.5D);
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
