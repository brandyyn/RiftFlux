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
import net.nmccoy.legendgear.LegendGear2;
import net.nmccoy.legendgear.block.TileEntityStarwell;
import net.nmccoy.legendgear.render.Rainbow;
import org.lwjgl.opengl.GL11;

public class TileEntityStarwellRender
extends TileEntitySpecialRenderer {
    private static final ResourceLocation beamTexture = new ResourceLocation("legendgear", "textures/beam.png");
    private static final ResourceLocation rippleTexture = new ResourceLocation("legendgear", "textures/spikeySpark.png");

    public void renderTileEntityAt(TileEntity var1, double var2, double var4, double var6, float var8) {
        int i;
        GL11.glPushMatrix();
        GL11.glTranslatef((float)((float)var2 + 0.5f), (float)((float)var4 + 0.5f), (float)((float)var6 + 0.5f));
        this.bindTexture(beamTexture);
        Tessellator tess = Tessellator.instance;
        GL11.glEnable((int)32826);
        GL11.glDisable((int)2896);
        GL11.glAlphaFunc((int)516, (float)0.0f);
        GL11.glEnable((int)3042);
        GL11.glBlendFunc((int)770, (int)1);
        GL11.glDepthMask((boolean)false);
        GL11.glDisable((int)2884);
        TileEntityStarwell beam = (TileEntityStarwell)var1;
        float phase = (float)(Minecraft.getSystemTime() % 30000L) / 30000.0f;
        if (beam.blockmode == LegendGear2.skylensBlock) {
            phase = (float)(Minecraft.getSystemTime() % 3000L) / 3000.0f;
        }
        float t = phase;
        float power = 1.0f;
        float width = 0.7f;
        float height = beam.beamHeight;
        if (height > 0.0f) {
            for (i = 0; i < 8; ++i) {
                GL11.glPushMatrix();
                GL11.glTranslatef((float)((float)Math.cos(2.356194490192345 * (double)i + Math.PI * 2 * (double)t) * 0.5f * width), (float)0.0f, (float)((float)Math.sin(2.356194490192345 * (double)i + Math.PI * 2 * (double)t) * 0.5f * width));
                GL11.glRotatef((float)(180.0f - this.field_147501_a.field_147562_h), (float)0.0f, (float)1.0f, (float)0.0f);
                tess.startDrawingQuads();
                tess.setColorRGBA_F(Rainbow.r(phase * 2.0f), Rainbow.g(phase * 2.0f), Rainbow.b(phase * 2.0f), 0.5f * (1.0f - phase));
                if (beam.blockmode == LegendGear2.skylensBlock) {
                    if (beam.flightCharge > 0) {
                        float flash = 0.5f + 0.5f * (float)Math.sin((double)phase * Math.PI * 12.0);
                        tess.setColorRGBA_F(0.2f + 0.8f * flash, 0.7f + 0.3f * flash, 1.0f, 1.0f - phase);
                    } else {
                        tess.setColorRGBA_F(1.2f - phase * 2.0f, 2.0f - phase * 2.0f, 1.0f, 0.5f * (1.0f - phase));
                    }
                }
                tess.setBrightness(240);
                float w = 2.0f;
                if (beam.blockmode == LegendGear2.skylensBlock) {
                    w = 3.0f;
                }
                float h = phase * height;
                tess.addVertexWithUV((double)(-w), (double)h, 0.0, 0.0, 0.0);
                tess.addVertexWithUV((double)w, (double)h, 0.0, 1.0, 0.0);
                tess.addVertexWithUV((double)w, 0.0, 0.0, 1.0, 1.0);
                tess.addVertexWithUV((double)(-w), 0.0, 0.0, 0.0, 1.0);
                tess.draw();
                phase = (float)((double)phase + 0.125);
                if (phase > 1.0f) {
                    phase -= 1.0f;
                }
                GL11.glPopMatrix();
            }
        }
        if (var1.getWorldObj().getBlock(var1.xCoord, var1.yCoord + 1, var1.zCoord) == LegendGear2.skylensBlock) {
            GL11.glDisable((int)3553);
            GL11.glLineWidth((float)2.0f);
            phase = (float)(Minecraft.getSystemTime() % 500L) / 500.0f;
            for (i = 0; i < 4; ++i) {
                float fadestep = 0.25f * (float)i + phase * 0.25f;
                GL11.glColor4d((double)(1.0f - fadestep), (double)(1.0 - (double)fadestep * 0.5), (double)1.0, (double)(Math.sin((double)fadestep * Math.PI) * 0.15));
                GL11.glBegin((int)7);
                GL11.glVertex3d((double)(-1.5 + (double)(fadestep * 0.0f)), (double)(1.5 + (double)fadestep), (double)(-1.5 + (double)(fadestep * 0.0f)));
                GL11.glVertex3d((double)(1.5 - (double)(fadestep * 0.0f)), (double)(1.5 + (double)fadestep), (double)(-1.5 + (double)(fadestep * 0.0f)));
                GL11.glVertex3d((double)(1.5 - (double)(fadestep * 0.0f)), (double)(1.5 + (double)fadestep), (double)(1.5 - (double)(fadestep * 0.0f)));
                GL11.glVertex3d((double)(-1.5 + (double)(fadestep * 0.0f)), (double)(1.5 + (double)fadestep), (double)(1.5 - (double)(fadestep * 0.0f)));
                GL11.glEnd();
            }
            GL11.glEnable((int)3553);
            GL11.glPushMatrix();
            GL11.glTranslatef((float)0.0f, (float)(beam.beamHeight / 1.5f - 2.5f), (float)0.0f);
            GL11.glRotatef((float)(180.0f - this.field_147501_a.field_147562_h), (float)0.0f, (float)1.0f, (float)0.0f);
            GL11.glRotatef((float)(-this.field_147501_a.field_147563_i), (float)1.0f, (float)0.0f, (float)0.0f);
            phase = (float)(Minecraft.getSystemTime() % 2000L) / 2000.0f;
            GL11.glRotatef((float)(phase * 180.0f), (float)0.0f, (float)0.0f, (float)1.0f);
            float scale = (float)Math.sin((double)phase * Math.PI * 2.0) + 5.0f;
            GL11.glScalef((float)scale, (float)scale, (float)scale);
            tess.startDrawingQuads();
            TileEntityStarwell well = (TileEntityStarwell)var1;
            if (well.flightCharge > 0) {
                float flash = 0.5f + 0.5f * (float)Math.sin((double)phase * Math.PI * 12.0);
                tess.setColorRGBA_F(0.2f + 0.8f * flash, 0.7f + 0.3f * flash, 1.0f, 0.5f);
            } else {
                tess.setColorRGBA_F(0.2f, 0.7f, 1.0f, 0.5f);
            }
            tess.setBrightness(240);
            this.bindTexture(rippleTexture);
            tess.addVertexWithUV(-1.0, 1.0, 0.0, 0.0, 0.0);
            tess.addVertexWithUV(1.0, 1.0, 0.0, 1.0, 0.0);
            tess.addVertexWithUV(1.0, -1.0, 0.0, 1.0, 1.0);
            tess.addVertexWithUV(-1.0, -1.0, 0.0, 0.0, 1.0);
            tess.addVertexWithUV(-1.0, 1.0, 0.0, 1.0, 0.0);
            tess.addVertexWithUV(1.0, 1.0, 0.0, 0.0, 0.0);
            tess.addVertexWithUV(1.0, -1.0, 0.0, 0.0, 1.0);
            tess.addVertexWithUV(-1.0, -1.0, 0.0, 1.0, 1.0);
            tess.draw();
            GL11.glPopMatrix();
        }
        GL11.glDisable((int)32826);
        GL11.glDisable((int)3042);
        GL11.glEnable((int)2896);
        GL11.glAlphaFunc((int)516, (float)0.1f);
        GL11.glDepthMask((boolean)true);
        GL11.glEnable((int)2884);
        GL11.glPopMatrix();
    }
}

