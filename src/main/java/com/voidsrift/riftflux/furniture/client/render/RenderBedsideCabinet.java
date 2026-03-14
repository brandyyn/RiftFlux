package com.voidsrift.riftflux.furniture.client.render;

import com.voidsrift.riftflux.furniture.util.FurnitureRenderHelper;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.init.Blocks;
import net.minecraft.world.IBlockAccess;

public class RenderBedsideCabinet implements ISimpleBlockRenderingHandler {
    private final int renderId;

    public RenderBedsideCabinet(int renderId) {
        this.renderId = renderId;
    }

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
        int metadata = world.getBlockMetadata(x, y, z);
        renderer.setOverrideBlockTexture(Blocks.planks.getIcon(0, 1));
        FurnitureRenderHelper.setRenderBounds(renderer, metadata, 0.0D, 0.125D, 0.125D, 0.0625D, 0.4375D, 0.875D);
        FurnitureRenderHelper.renderBlock(renderer, block, x, y, z);
        FurnitureRenderHelper.setRenderBounds(renderer, metadata, 0.0D, 0.5625D, 0.125D, 0.0625D, 0.875D, 0.875D);
        FurnitureRenderHelper.renderBlock(renderer, block, x, y, z);
        renderer.setOverrideBlockTexture(Blocks.planks.getBlockTextureFromSide(0));
        FurnitureRenderHelper.setRenderBounds(renderer, metadata, -0.03D, 0.25D, 0.375D, 0.0D, 0.3125D, 0.625D);
        FurnitureRenderHelper.renderBlock(renderer, block, x, y, z);
        FurnitureRenderHelper.setRenderBounds(renderer, metadata, -0.03D, 0.6875D, 0.375D, 0.0D, 0.75D, 0.625D);
        FurnitureRenderHelper.renderBlock(renderer, block, x, y, z);
        renderer.setOverrideBlockTexture(Blocks.planks.getBlockTextureFromSide(0));
        renderer.setRenderBounds(0.0625D, 0.1D, 0.0625D, 0.9375D, 0.9D, 0.9375D);
        renderer.renderStandardBlock(block, x, y, z);
        renderer.setOverrideBlockTexture(Blocks.log.getBlockTextureFromSide(2));
        renderer.setRenderBounds(0.0D, 0.0D, 0.0D, 1.0D, 0.1D, 1.0D);
        renderer.renderStandardBlock(block, x, y, z);
        renderer.setRenderBounds(0.0D, 0.9D, 0.0D, 1.0D, 1.0D, 1.0D);
        renderer.renderStandardBlock(block, x, y, z);
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
