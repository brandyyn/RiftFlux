/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.Tessellator
 *  net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.ResourceLocation
 *  org.lwjgl.opengl.GL11
 */
package net.nmccoy.legendgear.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class TileEntityStarstoneRender
extends TileEntitySpecialRenderer {
    private static final ResourceLocation starTexture = new ResourceLocation("legendgear", "textures/chaosrainbow2.png");

    public void renderTileEntityAt(TileEntity te, double var2, double var4, double var6, float var8) {
        float phase = (float)(Minecraft.getSystemTime() % 8000L) / 8000.0f;
        float uphase = (float)(Minecraft.getSystemTime() % 11000L) / 11000.0f;
        int sp = 64;
        float u = (float)(((te.xCoord + te.zCoord + te.yCoord) % sp + sp) % sp) / (1.0f * (float)sp) - uphase;
        float v = (float)(((te.xCoord + te.zCoord + te.yCoord) % sp + sp) % sp) / (1.0f * (float)sp) + phase;
        float ds = 0.015625f;
        float dsu = 0.03125f;
        Tessellator tess = Tessellator.instance;
        GL11.glPushMatrix();
        GL11.glTranslatef((float)((float)var2), (float)((float)var4), (float)((float)var6));
        this.bindTexture(starTexture);
        GL11.glDepthMask((boolean)false);
        GL11.glEnable((int)32826);
        GL11.glDisable((int)2896);
        GL11.glEnable((int)3042);
        GL11.glBlendFunc((int)775, (int)1);
        GL11.glEnable((int)32823);
        GL11.glPolygonOffset((float)-2.0f, (float)-5.0f);
        tess.startDrawingQuads();
        tess.setColorRGBA_F(0.6f, 0.6f, 0.6f, 0.3f);
        if (!te.getWorldObj().getBlock(te.xCoord, te.yCoord + 1, te.zCoord).isOpaqueCube()) {
            tess.addVertexWithUV(0.0, 1.0, 0.0, (double)(u + dsu), (double)(v + ds));
            tess.addVertexWithUV(0.0, 1.0, 1.0, (double)(u + dsu * 2.0f), (double)(v + ds * 2.0f));
            tess.addVertexWithUV(1.0, 1.0, 1.0, (double)(u + dsu * 3.0f), (double)(v + ds * 3.0f));
            tess.addVertexWithUV(1.0, 1.0, 0.0, (double)(u + dsu * 2.0f), (double)(v + ds * 2.0f));
        }
        if (!te.getWorldObj().getBlock(te.xCoord, te.yCoord - 1, te.zCoord).isOpaqueCube()) {
            tess.addVertexWithUV(0.0, 0.0, 0.0, (double)u, (double)v);
            tess.addVertexWithUV(1.0, 0.0, 0.0, (double)(u + dsu), (double)(v + ds));
            tess.addVertexWithUV(1.0, 0.0, 1.0, (double)(u + dsu * 2.0f), (double)(v + ds * 2.0f));
            tess.addVertexWithUV(0.0, 0.0, 1.0, (double)(u + dsu), (double)(v + ds));
        }
        if (!te.getWorldObj().getBlock(te.xCoord - 1, te.yCoord, te.zCoord).isOpaqueCube()) {
            tess.addVertexWithUV(0.0, 0.0, 1.0, (double)(u + dsu), (double)(v + ds));
            tess.addVertexWithUV(0.0, 1.0, 1.0, (double)(u + dsu * 2.0f), (double)(v + ds * 2.0f));
            tess.addVertexWithUV(0.0, 1.0, 0.0, (double)(u + dsu), (double)(v + ds));
            tess.addVertexWithUV(0.0, 0.0, 0.0, (double)u, (double)v);
        }
        if (!te.getWorldObj().getBlock(te.xCoord + 1, te.yCoord, te.zCoord).isOpaqueCube()) {
            tess.addVertexWithUV(1.0, 1.0, 0.0, (double)(u + dsu * 2.0f), (double)(v + ds * 2.0f));
            tess.addVertexWithUV(1.0, 1.0, 1.0, (double)(u + dsu * 3.0f), (double)(v + ds * 3.0f));
            tess.addVertexWithUV(1.0, 0.0, 1.0, (double)(u + dsu * 2.0f), (double)(v + ds * 2.0f));
            tess.addVertexWithUV(1.0, 0.0, 0.0, (double)(u + dsu), (double)(v + ds));
        }
        if (!te.getWorldObj().getBlock(te.xCoord, te.yCoord, te.zCoord + 1).isOpaqueCube()) {
            tess.addVertexWithUV(1.0, 0.0, 1.0, (double)(u + dsu * 2.0f), (double)(v + ds * 2.0f));
            tess.addVertexWithUV(1.0, 1.0, 1.0, (double)(u + dsu * 3.0f), (double)(v + ds * 3.0f));
            tess.addVertexWithUV(0.0, 1.0, 1.0, (double)(u + dsu * 2.0f), (double)(v + ds * 2.0f));
            tess.addVertexWithUV(0.0, 0.0, 1.0, (double)(u + dsu), (double)(v + ds));
        }
        if (!te.getWorldObj().getBlock(te.xCoord, te.yCoord, te.zCoord - 1).isOpaqueCube()) {
            tess.addVertexWithUV(0.0, 1.0, 0.0, (double)(u + dsu), (double)(v + ds));
            tess.addVertexWithUV(1.0, 1.0, 0.0, (double)(u + dsu * 2.0f), (double)(v + ds * 2.0f));
            tess.addVertexWithUV(1.0, 0.0, 0.0, (double)(u + dsu), (double)(v + ds));
            tess.addVertexWithUV(0.0, 0.0, 0.0, (double)u, (double)v);
        }
        tess.draw();
        GL11.glPolygonOffset((float)0.0f, (float)0.0f);
        GL11.glDisable((int)3042);
        GL11.glDisable((int)32826);
        GL11.glEnable((int)2896);
        GL11.glDepthMask((boolean)true);
        GL11.glPopMatrix();
    }
}

