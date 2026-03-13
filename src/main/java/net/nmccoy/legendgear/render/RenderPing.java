/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.Tessellator
 *  net.minecraft.client.renderer.entity.Render
 *  net.minecraft.entity.Entity
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.Vec3
 *  org.lwjgl.opengl.GL11
 */
package net.nmccoy.legendgear.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.nmccoy.legendgear.entity.EntityPing;
import net.nmccoy.legendgear.render.Rainbow;
import org.lwjgl.opengl.GL11;

public class RenderPing
extends Render {
    private static final ResourceLocation starTexture = new ResourceLocation("legendgear", "textures/addRipple.png");
    private static final ResourceLocation beamTexture = new ResourceLocation("legendgear", "textures/beam.png");

    public ResourceLocation getEntityTexture(Entity e) {
        return starTexture;
    }

    public RenderPing() {
        System.out.println("renderer constructed");
    }

    public void doRender(Entity par1Entity, double par2, double par4, double par6, float par8, float subf) {
        Vec3 towards;
        double distance;
        float scale;
        GL11.glPushMatrix();
        GL11.glTranslatef((float)((float)par2), (float)((float)par4), (float)((float)par6));
        GL11.glEnable((int)32826);
        GL11.glDisable((int)2896);
        GL11.glEnable((int)3042);
        GL11.glBlendFunc((int)770, (int)1);
        GL11.glAlphaFunc((int)516, (float)0.0f);
        GL11.glDepthMask((boolean)false);
        this.bindTexture(beamTexture);
        Tessellator var10 = Tessellator.instance;
        EntityPing ping = (EntityPing)par1Entity;
        float phase = (float)(Minecraft.getSystemTime() % 1000L) / 1000.0f;
        float r = Rainbow.r(phase);
        float g = Rainbow.g(phase);
        float b = Rainbow.b(phase);
        float fresh = (8.0f - ((float)ping.age + subf)) / 8.0f;
        if (fresh < 0.0f) {
            fresh = 0.0f;
        }
        if ((double)(scale = 2.0f) < (distance = (towards = Vec3.createVectorHelper((double)(ping.posX - this.renderManager.viewerPosX), (double)(ping.posY - this.renderManager.viewerPosY), (double)(ping.posZ - this.renderManager.viewerPosZ))).lengthVector()) / 20.0) {
            scale = (float)distance / 20.0f;
        }
        scale *= (1.0f - fresh) * (1.0f + fresh * fresh * 12.0f);
        float fade = (float)ping.energy * 0.01f;
        if (fade > 1.0f) {
            fade = 1.0f;
        }
        this.bindTexture(starTexture);
        GL11.glDepthFunc((int)519);
        var10.startDrawingQuads();
        var10.setColorRGBA_F(r + fresh, g + fresh, b + fresh, fade);
        var10.setBrightness(240);
        GL11.glScalef((float)scale, (float)scale, (float)scale);
        this.billboard(var10, 0.0f * phase, 0.0f);
        var10.startDrawingQuads();
        var10.setColorRGBA_F(g + fresh, b + fresh, r + fresh, fade * (1.0f - phase));
        var10.setBrightness(240);
        GL11.glScalef((float)phase, (float)phase, (float)phase);
        this.billboard(var10, 0.0f * phase, 0.0f);
        GL11.glDisable((int)32826);
        GL11.glDisable((int)3042);
        GL11.glEnable((int)2896);
        GL11.glDepthFunc((int)515);
        GL11.glAlphaFunc((int)516, (float)0.1f);
        GL11.glDepthMask((boolean)true);
        GL11.glPopMatrix();
    }

    private void billboard(Tessellator par1Tessellator, float angle, float depth) {
        float var3 = 0.0f;
        float var4 = 1.0f;
        float var5 = 0.0f;
        float var6 = 1.0f;
        float var7 = 1.0f;
        float var8 = 0.0f;
        float var9 = 0.0f;
        GL11.glPushMatrix();
        GL11.glRotatef((float)(180.0f - this.renderManager.playerViewY), (float)0.0f, (float)1.0f, (float)0.0f);
        GL11.glRotatef((float)(-this.renderManager.playerViewX), (float)1.0f, (float)0.0f, (float)0.0f);
        GL11.glRotatef((float)angle, (float)0.0f, (float)0.0f, (float)1.0f);
        GL11.glTranslatef((float)-0.5f, (float)-0.5f, (float)depth);
        par1Tessellator.setNormal(0.0f, 1.0f, 0.0f);
        par1Tessellator.addVertexWithUV((double)(0.0f - var8), (double)(0.0f - var9), 0.0, (double)var3, (double)var6);
        par1Tessellator.addVertexWithUV((double)(var7 - var8), (double)(0.0f - var9), 0.0, (double)var4, (double)var6);
        par1Tessellator.addVertexWithUV((double)(var7 - var8), (double)(var7 - var9), 0.0, (double)var4, (double)var5);
        par1Tessellator.addVertexWithUV((double)(0.0f - var8), (double)(var7 - var9), 0.0, (double)var3, (double)var5);
        par1Tessellator.draw();
        GL11.glPopMatrix();
    }
}

