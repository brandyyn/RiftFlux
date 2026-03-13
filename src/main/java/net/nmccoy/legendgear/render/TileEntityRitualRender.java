/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.OpenGlHelper
 *  net.minecraft.client.renderer.Tessellator
 *  net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
 *  net.minecraft.init.Blocks
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.Vec3
 *  net.minecraft.world.ChunkPosition
 *  org.lwjgl.opengl.GL11
 */
package net.nmccoy.legendgear.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.ChunkPosition;
import net.nmccoy.legendgear.block.TileEntityRitual;
import net.nmccoy.legendgear.ritual.Edge;
import net.nmccoy.legendgear.ritual.RitualGrid;
import org.lwjgl.opengl.GL11;

public class TileEntityRitualRender
extends TileEntitySpecialRenderer {
    private static final ResourceLocation beamTexture = new ResourceLocation("legendgear", "textures/beam.png");
    private static final ResourceLocation sparkTexture = new ResourceLocation("legendgear", "textures/spikeySpark.png");
    private static final ResourceLocation ringTexture = new ResourceLocation("legendgear", "textures/addRipple.png");
    private static final ResourceLocation rainbowTexture = new ResourceLocation("legendgear", "textures/rainbowfade.png");
    private static int PULSE_DURATION = 10;
    private static boolean DRAW_ALL_EDGES = false;

    public float clerp(float input, float scale) {
        if ((input /= scale) < 0.0f) {
            return 0.0f;
        }
        if (input > 1.0f) {
            return 1.0f;
        }
        return input;
    }

    private void billboard(Tessellator par1Tessellator, float angle, float scale) {
        float var3 = 0.0f;
        float var4 = 1.0f;
        float var5 = 0.0f;
        float var6 = 1.0f;
        float var7 = 1.0f;
        float var8 = 0.0f;
        float var9 = 0.0f;
        GL11.glPushMatrix();
        GL11.glScalef((float)scale, (float)scale, (float)scale);
        GL11.glRotatef((float)90.0f, (float)1.0f, (float)0.0f, (float)0.0f);
        GL11.glRotatef((float)angle, (float)0.0f, (float)0.0f, (float)1.0f);
        GL11.glTranslatef((float)-0.5f, (float)-0.5f, (float)0.0f);
        par1Tessellator.startDrawingQuads();
        par1Tessellator.setNormal(0.0f, 1.0f, 0.0f);
        par1Tessellator.addVertexWithUV((double)(0.0f - var8), (double)(0.0f - var9), 0.0, (double)var3, (double)var6);
        par1Tessellator.addVertexWithUV((double)(var7 - var8), (double)(0.0f - var9), 0.0, (double)var4, (double)var6);
        par1Tessellator.addVertexWithUV((double)(var7 - var8), (double)(var7 - var9), 0.0, (double)var4, (double)var5);
        par1Tessellator.addVertexWithUV((double)(0.0f - var8), (double)(var7 - var9), 0.0, (double)var3, (double)var5);
        par1Tessellator.draw();
        GL11.glPopMatrix();
    }

    public void drawOrbAt(double x, double y, double z, float scale) {
        GL11.glPushMatrix();
        this.bindTexture(sparkTexture);
        GL11.glTranslated((double)x, (double)y, (double)z);
        Tessellator tess = Tessellator.instance;
        this.billboard(tess, (float)Minecraft.getSystemTime() * 0.05f, scale);
        this.billboard(tess, (float)Minecraft.getSystemTime() * -0.0072f + (float)z, scale * 0.9f);
        this.billboard(tess, (float)Minecraft.getSystemTime() * 0.0113f + (float)x * 2.7f, scale * 0.8f);
        GL11.glPopMatrix();
    }

    public void drawRippleAt(double x, double y, double z, float scale) {
        GL11.glPushMatrix();
        this.bindTexture(ringTexture);
        GL11.glTranslated((double)x, (double)(y - (double)0.485f), (double)z);
        Tessellator tess = Tessellator.instance;
        GL11.glScalef((float)scale, (float)scale, (float)scale);
        tess.startDrawingQuads();
        tess.addVertexWithUV(-1.0, 0.0, -1.0, 0.0, 0.0);
        tess.addVertexWithUV(1.0, 0.0, -1.0, 1.0, 0.0);
        tess.addVertexWithUV(1.0, 0.0, 1.0, 1.0, 1.0);
        tess.addVertexWithUV(-1.0, 0.0, 1.0, 0.0, 1.0);
        tess.draw();
        GL11.glPopMatrix();
    }

    public void drawRainbowBox(double x, double y, double z, double radius, double height) {
        GL11.glPushMatrix();
        GL11.glTranslated((double)x, (double)y, (double)z);
        GL11.glScaled((double)radius, (double)height, (double)radius);
        this.bindTexture(rainbowTexture);
        Tessellator tess = Tessellator.instance;
        float uShift = 1.0f - (float)(Minecraft.getSystemTime() % 1000L) / 1000.0f;
        for (int i = 0; i < 4; ++i) {
            tess.startDrawingQuads();
            tess.addVertexWithUV(-1.0, 1.0, -1.0, (double)(0.0f + uShift), 0.0);
            tess.addVertexWithUV(-1.0, 1.0, 1.0, (double)(1.0f + uShift), 0.0);
            tess.addVertexWithUV(-1.0, 0.0, 1.0, (double)(1.0f + uShift), 1.0);
            tess.addVertexWithUV(-1.0, 0.0, -1.0, (double)(0.0f + uShift), 1.0);
            tess.draw();
            GL11.glRotated((double)90.0, (double)0.0, (double)1.0, (double)0.0);
        }
        GL11.glPopMatrix();
    }

    public void renderTileEntityAt(TileEntity entity, double x, double y, double z, float maybeSubframe) {
        float fade2;
        float fade1;
        float phase2;
        TileEntityRitual ritualZone = (TileEntityRitual)entity;
        if (!ritualZone.active || ritualZone.grid == null) {
            return;
        }
        RitualGrid grid = ritualZone.grid;
        float wakeupFade = this.clerp((float)ritualZone.awakeTicks + maybeSubframe, 20.0f);
        float darkness = 1.0f - ritualZone.getWorldObj().getCurrentMoonPhaseFactor();
        float moonlight = this.clerp((float)(-Math.cos(ritualZone.getWorldObj().getCelestialAngleRadians(0.0f))) - darkness, 1.0f);
        GL11.glPushMatrix();
        GL11.glTranslatef((float)((float)x + 0.5f), (float)((float)y + 0.5f), (float)((float)z + 0.5f));
        Tessellator tess = Tessellator.instance;
        this.bindTexture(beamTexture);
        GL11.glEnable((int)32826);
        GL11.glDisable((int)2896);
        GL11.glAlphaFunc((int)516, (float)0.0f);
        GL11.glEnable((int)3042);
        GL11.glBlendFunc((int)770, (int)1);
        GL11.glDepthMask((boolean)false);
        GL11.glDisable((int)2884);
        GL11.glEnable((int)3553);
        OpenGlHelper.setLightmapTextureCoords((int)OpenGlHelper.lightmapTexUnit, (float)240.0f, (float)240.0f);
        float sink = -0.45f;
        if (grid.isGridStable(entity.getWorldObj())) {
            float phase1 = (float)(Minecraft.getSystemTime() % 2000L) / 2000.0f;
            phase2 = (float)((1000L + Minecraft.getSystemTime()) % 2000L) / 2000.0f;
            fade1 = (float)Math.sin((double)phase1 * Math.PI) * 0.5f;
            fade2 = (float)Math.sin((double)phase2 * Math.PI) * 0.5f;
            GL11.glColor4d((double)1.0, (double)1.0, (double)1.0, (double)fade1);
            this.drawRainbowBox(0.0, 0.0, 0.0, 0.75 - 0.25 * (double)phase1, 1.0f + phase1);
            GL11.glColor4d((double)1.0, (double)1.0, (double)1.0, (double)fade2);
            this.drawRainbowBox(0.0, 0.0, 0.0, 0.75 - 0.25 * (double)phase2, 1.0f + phase2);
        }
        if (ritualZone.successGoing) {
            float phase = ((float)ritualZone.successEffectTimer + maybeSubframe) / (float)TileEntityRitual.SUCCESS_EFFECT_DURATION;
            GL11.glColor4d((double)1.0, (double)1.0, (double)1.0, (double)Math.min(1.5 - (double)phase * 1.5, 1.0));
            this.drawRainbowBox(0.0, 0.5, 0.0, 0.5625, Math.min(phase * 32.0f, 16.0f));
            this.drawRainbowBox(0.0, 0.5, 0.0, 0.5625 + (double)((1.0f - phase) * (1.0f - phase)), 8.0f * phase * phase);
        }
        this.bindTexture(beamTexture);
        if (moonlight > 0.0f) {
            for (Edge edge : grid.edges) {
                ChunkPosition pos1 = grid.places[edge.first];
                ChunkPosition pos2 = grid.places[edge.second];
                Vec3 begin = Vec3.createVectorHelper((double)(pos1.chunkPosX - ritualZone.xCoord), (double)(pos1.chunkPosY - ritualZone.yCoord), (double)(pos1.chunkPosZ - ritualZone.zCoord));
                Vec3 end = Vec3.createVectorHelper((double)(pos2.chunkPosX - ritualZone.xCoord), (double)(pos2.chunkPosY - ritualZone.yCoord), (double)(pos2.chunkPosZ - ritualZone.zCoord));
                GL11.glLineWidth((float)3.0f);
                boolean onFirst = grid.blockOnPoint(edge.first, ritualZone.getWorldObj()) != Blocks.air;
                boolean onSecond = grid.blockOnPoint(edge.second, ritualZone.getWorldObj()) != Blocks.air;
                float width = 0.25f;
                float v1 = onFirst ? this.clerp(ritualZone.ticksSinceEmpty[edge.first], 10.0f) : 0.01f;
                float v2 = onSecond ? this.clerp(ritualZone.ticksSinceEmpty[edge.second], 10.0f) * 0.99f : 0.01f;
                if (DRAW_ALL_EDGES) {
                    v1 = 1.0f;
                    v2 = 0.99f;
                }
                Vec3 sideways = end.subtract(begin).crossProduct(Vec3.createVectorHelper((double)0.0, (double)1.0, (double)0.0)).normalize();
                sideways.xCoord *= (double)width;
                sideways.zCoord *= (double)width;
                GL11.glColor4d((double)0.85, (double)0.9, (double)1.0, (double)(1.0 * (double)moonlight * (double)wakeupFade));
                tess.startDrawingQuads();
                tess.addVertexWithUV(begin.xCoord - sideways.xCoord, begin.yCoord + (double)sink, begin.zCoord - sideways.zCoord, 0.0, (double)v1);
                tess.addVertexWithUV(begin.xCoord + sideways.xCoord, begin.yCoord + (double)sink, begin.zCoord + sideways.zCoord, 1.0, (double)v1);
                tess.addVertexWithUV(end.xCoord + sideways.xCoord, end.yCoord + (double)sink, end.zCoord + sideways.zCoord, 1.0, (double)v2);
                tess.addVertexWithUV(end.xCoord - sideways.xCoord, end.yCoord + (double)sink, end.zCoord - sideways.zCoord, 0.0, (double)v2);
                tess.draw();
            }
        }
        float phase1 = 1.0f - (float)(Minecraft.getSystemTime() % 2000L) / 2000.0f;
        phase2 = 1.0f - (float)((1000L + Minecraft.getSystemTime()) % 2000L) / 2000.0f;
        fade1 = (float)Math.sin((double)phase1 * Math.PI) * 0.5f;
        fade2 = (float)Math.sin((double)phase2 * Math.PI) * 0.5f;
        if (!ritualZone.dirty) {
            for (int i = 0; i < 8; ++i) {
                double dx = grid.places[i].chunkPosX - ritualZone.xCoord;
                double dy = grid.places[i].chunkPosY - ritualZone.yCoord;
                double dz = grid.places[i].chunkPosZ - ritualZone.zCoord;
                GL11.glColor4d((double)0.8, (double)0.8, (double)1.0, (double)(0.75 * (double)moonlight * (double)wakeupFade));
                if (moonlight > 0.0f) {
                    this.drawOrbAt(dx, dy + (double)sink, dz, 0.8f + (float)Math.sin((double)phase1 * Math.PI * 2.0) * 0.1f);
                }
                GL11.glColor4d((double)0.9, (double)0.7, (double)1.0, (double)(fade1 * wakeupFade));
                this.drawRippleAt(dx, dy, dz, 1.0f * phase1 + 0.3f);
                GL11.glColor4d((double)0.7, (double)0.9, (double)1.0, (double)(fade2 * wakeupFade));
                this.drawRippleAt(dx, dy, dz, 1.0f * phase2 + 0.3f);
                long now = ritualZone.getWorldObj().getTotalWorldTime();
                if (ritualZone.pulseStartTime[i] > now || ritualZone.pulseStartTime[i] + (long)PULSE_DURATION <= now) continue;
                float diff = (float)(now - ritualZone.pulseStartTime[i]) + maybeSubframe;
                float phase = this.clerp(diff, PULSE_DURATION);
                GL11.glColor4d((double)0.3, (double)0.4, (double)0.5, (double)(1.0f - phase));
                this.drawRippleAt(dx, dy, dz, phase * 3.0f);
            }
        }
        GL11.glDisable((int)32826);
        GL11.glDisable((int)3042);
        GL11.glEnable((int)2896);
        GL11.glAlphaFunc((int)516, (float)0.1f);
        GL11.glDepthMask((boolean)true);
        GL11.glEnable((int)2884);
        GL11.glEnable((int)3553);
        GL11.glPopMatrix();
    }
}

