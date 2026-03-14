package com.voidsrift.riftflux.furniture.client.render;

import com.voidsrift.riftflux.furniture.util.FurnitureRenderHelper;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.init.Blocks;
import net.minecraft.world.IBlockAccess;

public class RenderCabinet implements ISimpleBlockRenderingHandler {
    private final int renderId;

    public RenderCabinet(int renderId) {
        this.renderId = renderId;
    }

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
        int metadata = world.getBlockMetadata(x, y, z);
        renderer.setOverrideBlockTexture(Blocks.planks.getIcon(0, 1));
        FurnitureRenderHelper.setRenderBounds(renderer, metadata, 0.125D, 0.0625D, 0.0625D, 0.1875D, 0.9375D, 0.9375D);
        FurnitureRenderHelper.renderBlock(renderer, block, x, y, z);
        renderer.setOverrideBlockTexture(Blocks.iron_block.getBlockTextureFromSide(1));
        FurnitureRenderHelper.setRenderBounds(renderer, metadata, 0.0D, 0.28125D, 0.1875D, 0.0625D, 0.71875D, 0.25D);
        FurnitureRenderHelper.renderBlock(renderer, block, x, y, z);
        FurnitureRenderHelper.setRenderBounds(renderer, metadata, 0.0625D, 0.65625D, 0.1875D, 0.125D, 0.71875D, 0.25D);
        FurnitureRenderHelper.renderBlock(renderer, block, x, y, z);
        FurnitureRenderHelper.setRenderBounds(renderer, metadata, 0.0625D, 0.28125D, 0.1875D, 0.125D, 0.34375D, 0.25D);
        FurnitureRenderHelper.renderBlock(renderer, block, x, y, z);
        renderer.setOverrideBlockTexture(Blocks.planks.getBlockTextureFromSide(0));
        FurnitureRenderHelper.setRenderBounds(renderer, metadata, 0.1875D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
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
