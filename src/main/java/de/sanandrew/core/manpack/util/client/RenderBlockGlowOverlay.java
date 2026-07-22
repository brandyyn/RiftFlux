/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler
 *  org.lwjgl.opengl.GL11
 */
package de.sanandrew.core.manpack.util.client;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.sanandrew.core.manpack.util.client.IGlowBlockOverlay;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.IBlockAccess;
import org.lwjgl.opengl.GL11;

@SideOnly(value=Side.CLIENT)
public class RenderBlockGlowOverlay
implements ISimpleBlockRenderingHandler {
    public static int renderID = 0;

    public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {
        Tessellator tessellator = Tessellator.instance;
        IGlowBlockOverlay blockOverlay = block instanceof IGlowBlockOverlay ? (IGlowBlockOverlay)((Object)block) : null;
        block.setBlockBoundsForItemRender();
        renderer.setRenderBoundsFromBlock(block);
        GL11.glRotatef((float)90.0f, (float)0.0f, (float)1.0f, (float)0.0f);
        GL11.glTranslatef((float)-0.5f, (float)-0.5f, (float)-0.5f);
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0f, -1.0f, 0.0f);
        renderer.renderFaceYNeg(block, 0.0, 0.0, 0.0, renderer.getBlockIconFromSideAndMetadata(block, 0, metadata));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0f, 1.0f, 0.0f);
        renderer.renderFaceYPos(block, 0.0, 0.0, 0.0, renderer.getBlockIconFromSideAndMetadata(block, 1, metadata));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0f, 0.0f, -1.0f);
        renderer.renderFaceZNeg(block, 0.0, 0.0, 0.0, renderer.getBlockIconFromSideAndMetadata(block, 2, metadata));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0f, 0.0f, 1.0f);
        renderer.renderFaceZPos(block, 0.0, 0.0, 0.0, renderer.getBlockIconFromSideAndMetadata(block, 3, metadata));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(-1.0f, 0.0f, 0.0f);
        renderer.renderFaceXNeg(block, 0.0, 0.0, 0.0, renderer.getBlockIconFromSideAndMetadata(block, 4, metadata));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(1.0f, 0.0f, 0.0f);
        renderer.renderFaceXPos(block, 0.0, 0.0, 0.0, renderer.getBlockIconFromSideAndMetadata(block, 5, metadata));
        tessellator.draw();
        if (blockOverlay != null) {
            float lastBrightX = OpenGlHelper.lastBrightnessX;
            float lastBrightY = OpenGlHelper.lastBrightnessY;
            int bright = 240;
            float brightX = bright % 65536;
            float brightY = bright / 65536;
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, brightX, brightY);
            tessellator.startDrawingQuads();
            tessellator.setNormal(0.0f, -1.0f, 0.0f);
            renderer.renderFaceYNeg(block, 0.0, 0.0, 0.0, blockOverlay.getOverlayInvTexture(0, metadata));
            tessellator.draw();
            tessellator.startDrawingQuads();
            tessellator.setNormal(0.0f, 1.0f, 0.0f);
            renderer.renderFaceYPos(block, 0.0, 0.0, 0.0, blockOverlay.getOverlayInvTexture(1, metadata));
            tessellator.draw();
            tessellator.startDrawingQuads();
            tessellator.setNormal(0.0f, 0.0f, -1.0f);
            renderer.renderFaceZNeg(block, 0.0, 0.0, 0.0, blockOverlay.getOverlayInvTexture(2, metadata));
            tessellator.draw();
            tessellator.startDrawingQuads();
            tessellator.setNormal(0.0f, 0.0f, 1.0f);
            renderer.renderFaceZPos(block, 0.0, 0.0, 0.0, blockOverlay.getOverlayInvTexture(3, metadata));
            tessellator.draw();
            tessellator.startDrawingQuads();
            tessellator.setNormal(-1.0f, 0.0f, 0.0f);
            renderer.renderFaceXNeg(block, 0.0, 0.0, 0.0, blockOverlay.getOverlayInvTexture(4, metadata));
            tessellator.draw();
            tessellator.startDrawingQuads();
            tessellator.setNormal(1.0f, 0.0f, 0.0f);
            renderer.renderFaceXPos(block, 0.0, 0.0, 0.0, blockOverlay.getOverlayInvTexture(5, metadata));
            tessellator.draw();
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lastBrightX, lastBrightY);
        }
        GL11.glTranslatef((float)0.5f, (float)0.5f, (float)0.5f);
    }

    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
        renderer.renderStandardBlockWithColorMultiplier(block, x, y, z, 1.0f, 1.0f, 1.0f);
        if (block instanceof IGlowBlockOverlay) {
            IGlowBlockOverlay icn = (IGlowBlockOverlay)((Object)block);
            Tessellator.instance.setBrightness(240);
            Tessellator.instance.setColorOpaque(255, 255, 255);
            renderer.renderFaceYNeg(block, x, y, z, icn.getOverlayTexture(world, x, y, z, 0));
            renderer.renderFaceYPos(block, x, y, z, icn.getOverlayTexture(world, x, y, z, 1));
            renderer.renderFaceZNeg(block, x, y, z, icn.getOverlayTexture(world, x, y, z, 2));
            renderer.renderFaceZPos(block, x, y, z, icn.getOverlayTexture(world, x, y, z, 3));
            renderer.renderFaceXNeg(block, x, y, z, icn.getOverlayTexture(world, x, y, z, 4));
            renderer.renderFaceXPos(block, x, y, z, icn.getOverlayTexture(world, x, y, z, 5));
            Tessellator.instance.setBrightness(block.getMixedBrightnessForBlock(world, x, y, z));
        }
        return true;
    }

    public boolean shouldRender3DInInventory(int modelId) {
        return true;
    }

    public int getRenderId() {
        return 0;
    }
}

