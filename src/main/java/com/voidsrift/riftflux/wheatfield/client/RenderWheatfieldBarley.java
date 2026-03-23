package com.voidsrift.riftflux.wheatfield.client;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.IBlockAccess;

public class RenderWheatfieldBarley implements ISimpleBlockRenderingHandler {
    private final int renderId;

    public RenderWheatfieldBarley(int renderId) {
        this.renderId = renderId;
    }

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
        Tessellator tessellator = Tessellator.instance;
        tessellator.setBrightness(block.getMixedBrightnessForBlock(world, x, y, z));
        tessellator.setColorOpaque_F(1.0F, 1.0F, 1.0F);

        long hash = (long) (x * 3129871) ^ (long) z * 116129781L ^ (long) y;
        hash = hash * hash * 42317861L + hash * 11L;

        double renderX = x + (((double) ((float) (hash >> 16 & 15L) / 15.0F)) - 0.5D) * 0.125D;
        double renderZ = z + (((double) ((float) (hash >> 24 & 15L) / 15.0F)) - 0.5D) * 0.125D;
        renderer.renderBlockCropsImpl(block, world.getBlockMetadata(x, y, z), renderX, y - 0.0625D, renderZ);
        return true;
    }

    @Override
    public boolean shouldRender3DInInventory(int modelId) {
        return false;
    }

    @Override
    public int getRenderId() {
        return renderId;
    }
}
