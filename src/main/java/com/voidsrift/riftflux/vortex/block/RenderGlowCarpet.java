package com.voidsrift.riftflux.vortex.block;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.IBlockAccess;
import org.lwjgl.opengl.GL11;

public class RenderGlowCarpet implements ISimpleBlockRenderingHandler {

    private final int renderId;

    public RenderGlowCarpet(int renderId) {
        this.renderId = renderId;
    }

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
        block.setBlockBoundsForItemRender();
        renderer.setRenderBoundsFromBlock(block);

        Tessellator tessellator = Tessellator.instance;
        GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
        GL11.glTranslatef(-0.5F, -0.5F, -0.5F);

        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, -1.0F, 0.0F);
        renderer.renderFaceYNeg(block, 0.0D, 0.0D, 0.0D, block.getIcon(0, metadata));
        tessellator.draw();

        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 1.0F, 0.0F);
        renderer.renderFaceYPos(block, 0.0D, 0.0D, 0.0D, block.getIcon(1, metadata));
        tessellator.draw();

        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 0.0F, -1.0F);
        renderer.renderFaceZNeg(block, 0.0D, 0.0D, 0.0D, block.getIcon(2, metadata));
        tessellator.draw();

        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 0.0F, 1.0F);
        renderer.renderFaceZPos(block, 0.0D, 0.0D, 0.0D, block.getIcon(3, metadata));
        tessellator.draw();

        tessellator.startDrawingQuads();
        tessellator.setNormal(-1.0F, 0.0F, 0.0F);
        renderer.renderFaceXNeg(block, 0.0D, 0.0D, 0.0D, block.getIcon(4, metadata));
        tessellator.draw();

        tessellator.startDrawingQuads();
        tessellator.setNormal(1.0F, 0.0F, 0.0F);
        renderer.renderFaceXPos(block, 0.0D, 0.0D, 0.0D, block.getIcon(5, metadata));
        tessellator.draw();

        GL11.glTranslatef(0.5F, 0.5F, 0.5F);
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
        if (modelId != this.renderId) {
            return false;
        }

        int rotation = getRotation(x, y, z);
        int oldEast = renderer.uvRotateEast;
        int oldWest = renderer.uvRotateWest;
        int oldSouth = renderer.uvRotateSouth;
        int oldNorth = renderer.uvRotateNorth;
        int oldTop = renderer.uvRotateTop;
        int oldBottom = renderer.uvRotateBottom;

        renderer.uvRotateEast = rotation;
        renderer.uvRotateWest = rotation;
        renderer.uvRotateSouth = rotation;
        renderer.uvRotateNorth = rotation;
        renderer.uvRotateTop = rotation;
        renderer.uvRotateBottom = rotation;

        boolean rendered = renderer.renderStandardBlock(block, x, y, z);

        renderer.uvRotateEast = oldEast;
        renderer.uvRotateWest = oldWest;
        renderer.uvRotateSouth = oldSouth;
        renderer.uvRotateNorth = oldNorth;
        renderer.uvRotateTop = oldTop;
        renderer.uvRotateBottom = oldBottom;
        return rendered;
    }

    private static int getRotation(int x, int y, int z) {
        int hash = x * 73428767 ^ y * 912931 ^ z * 42317861;
        hash ^= hash >>> 16;
        return hash & 3;
    }

    @Override
    public boolean shouldRender3DInInventory(int modelId) {
        return true;
    }

    @Override
    public int getRenderId() {
        return this.renderId;
    }
}