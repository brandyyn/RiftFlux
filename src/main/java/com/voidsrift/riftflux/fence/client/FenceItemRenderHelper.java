package com.voidsrift.riftflux.fence.client;

import com.voidsrift.riftflux.fence.FenceRenderSupport;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public final class FenceItemRenderHelper {
    private FenceItemRenderHelper() {
    }

    public static boolean renderFenceItem(RenderBlocks renderer, BlockFence block, int metadata, float x, float y, float z) {
        FenceRenderSupport.FenceRenderTextures textures = FenceRenderSupport.getItemTextures(block, metadata);
        if (textures == null) {
            return false;
        }

        float postHalf = 0.125F;
        float railHalf = 0.0625F;

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        block.setBlockBounds(0.5F - postHalf, 0.0F, 0.0F, 0.5F + postHalf, 1.0F, postHalf * 2.0F);
        renderer.setRenderBoundsFromBlock(block);
        renderPart(renderer, block, textures.postIcon, x, y, z);

        block.setBlockBounds(0.5F - postHalf, 0.0F, 1.0F - postHalf * 2.0F, 0.5F + postHalf, 1.0F, 1.0F);
        renderer.setRenderBoundsFromBlock(block);
        renderPart(renderer, block, textures.postIcon, x, y, z);

        block.setBlockBounds(0.5F - railHalf, 1.0F - railHalf * 3.0F, -railHalf * 2.0F, 0.5F + railHalf, 1.0F - railHalf, 1.0F + railHalf * 2.0F);
        renderer.setRenderBoundsFromBlock(block);
        renderPart(renderer, block, textures.railIcon, x, y, z);

        block.setBlockBounds(0.5F - railHalf, 0.5F - railHalf * 3.0F, -railHalf * 2.0F, 0.5F + railHalf, 0.5F - railHalf, 1.0F + railHalf * 2.0F);
        renderer.setRenderBoundsFromBlock(block);
        renderPart(renderer, block, textures.railIcon, x, y, z);

        block.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        renderer.setRenderBoundsFromBlock(block);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        return true;
    }

    private static void renderPart(RenderBlocks renderer, Block block, IIcon icon, float x, float y, float z) {
        Tessellator tessellator = Tessellator.instance;

        GL11.glTranslatef(-0.5F, -0.5F, -0.5F);

        tessellator.startDrawingQuads();
        tessellator.setColorOpaque_F(1.0F, 1.0F, 1.0F);
        tessellator.setNormal(0.0F, -1.0F, 0.0F);
        renderer.renderFaceYNeg(block, x, y, z, icon);
        tessellator.draw();

        tessellator.startDrawingQuads();
        tessellator.setColorOpaque_F(1.0F, 1.0F, 1.0F);
        tessellator.setNormal(0.0F, 1.0F, 0.0F);
        renderer.renderFaceYPos(block, x, y, z, icon);
        tessellator.draw();

        tessellator.startDrawingQuads();
        tessellator.setColorOpaque_F(1.0F, 1.0F, 1.0F);
        tessellator.setNormal(0.0F, 0.0F, -1.0F);
        renderer.renderFaceZNeg(block, x, y, z, icon);
        tessellator.draw();

        tessellator.startDrawingQuads();
        tessellator.setColorOpaque_F(1.0F, 1.0F, 1.0F);
        tessellator.setNormal(0.0F, 0.0F, 1.0F);
        renderer.renderFaceZPos(block, x, y, z, icon);
        tessellator.draw();

        tessellator.startDrawingQuads();
        tessellator.setColorOpaque_F(1.0F, 1.0F, 1.0F);
        tessellator.setNormal(-1.0F, 0.0F, 0.0F);
        renderer.renderFaceXNeg(block, x, y, z, icon);
        tessellator.draw();

        tessellator.startDrawingQuads();
        tessellator.setColorOpaque_F(1.0F, 1.0F, 1.0F);
        tessellator.setNormal(1.0F, 0.0F, 0.0F);
        renderer.renderFaceXPos(block, x, y, z, icon);
        tessellator.draw();

        GL11.glTranslatef(0.5F, 0.5F, 0.5F);
    }
}
