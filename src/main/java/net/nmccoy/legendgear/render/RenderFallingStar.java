/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.Tessellator
 *  net.minecraft.client.renderer.entity.Render
 *  net.minecraft.entity.Entity
 *  net.minecraft.util.ResourceLocation
 *  org.lwjgl.opengl.GL11
 */
package net.nmccoy.legendgear.render;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.inventorypets.ModelBananaBoomerang;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.nmccoy.legendgear.entity.EntityFallingStar;
import net.nmccoy.legendgear.render.Rainbow;
import org.lwjgl.opengl.GL11;

import java.util.Calendar;

public class RenderFallingStar
extends Render {
    private static final ResourceLocation starTexture = new ResourceLocation("legendgear", "textures/star.png");
    private static final ResourceLocation beamTexture = new ResourceLocation("legendgear", "textures/beam.png");
    private static final ResourceLocation bananaTexture = new ResourceLocation("riftflux", "textures/entity/inventorypets/nana.png");
    private final ModelBananaBoomerang bananaModel = new ModelBananaBoomerang();

    public ResourceLocation getEntityTexture(Entity e) {
        return useAprilFoolsBanana() ? bananaTexture : starTexture;
    }

    public RenderFallingStar() {
        System.out.println("renderer constructed");
    }

    public void doRender(Entity par1Entity, double par2, double par4, double par6, float par8, float par9) {
        if (useAprilFoolsBanana()) {
            renderBanana(par1Entity, par2, par4, par6);
            return;
        }
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
        EntityFallingStar star = (EntityFallingStar)par1Entity;
        float phase = (float)(Minecraft.getSystemTime() % 1000L) / 1000.0f;
        float r = Rainbow.r(phase);
        float g = Rainbow.g(phase);
        float b = Rainbow.b(phase);
        float scale = (float)star.dwindle_timer * 3.0f / (float)EntityFallingStar.DWINDLE_TIME;
        if (star.dwindle_timer > EntityFallingStar.DWINDLE_TIME - 10) {
            GL11.glPushMatrix();
            var10.startDrawingQuads();
            var10.setColorRGBA_F(r, g, b, 1.0f);
            var10.setBrightness(240);
            GL11.glRotatef((float)(180.0f - this.renderManager.playerViewY), (float)0.0f, (float)1.0f, (float)0.0f);
            double w = (double)scale * 0.3;
            float sc = star.dwindle_timer - (EntityFallingStar.DWINDLE_TIME - 10);
            GL11.glScalef((float)(sc *= 0.1f), (float)sc, (float)sc);
            var10.addVertexWithUV(-w, 30.0, 0.0, 0.0, 0.0);
            var10.addVertexWithUV(w, 30.0, 0.0, 1.0, 0.0);
            var10.addVertexWithUV(w, 0.0, 0.0, 1.0, 1.0);
            var10.addVertexWithUV(-w, 0.0, 0.0, 0.0, 1.0);
            var10.addVertexWithUV(w, 30.0, 0.0, 0.0, 0.0);
            var10.addVertexWithUV(-w, 30.0, 0.0, 1.0, 0.0);
            var10.addVertexWithUV(-w, 0.0, 0.0, 1.0, 1.0);
            var10.addVertexWithUV(w, 0.0, 0.0, 0.0, 1.0);
            var10.draw();
            GL11.glPopMatrix();
        }
        this.bindTexture(starTexture);
        var10.startDrawingQuads();
        var10.setColorRGBA_F(r, g, b, 1.0f);
        var10.setBrightness(240);
        GL11.glScalef((float)scale, (float)scale, (float)scale);
        this.billboard(var10, 180.0f * phase, 0.0f);
        var10.startDrawingQuads();
        var10.setColorRGBA_F(g, b, r, 1.0f);
        var10.setBrightness(240);
        GL11.glScalef((float)0.8f, (float)0.8f, (float)0.8f);
        this.billboard(var10, -270.0f * phase, 0.0f);
        var10.startDrawingQuads();
        var10.setColorRGBA_F(b, r, g, 1.0f);
        var10.setBrightness(240);
        GL11.glScalef((float)0.8f, (float)0.8f, (float)0.8f);
        this.billboard(var10, 90.0f * phase, 0.0f);
        GL11.glDisable((int)32826);
        GL11.glDisable((int)3042);
        GL11.glEnable((int)2896);
        GL11.glAlphaFunc((int)516, (float)0.1f);
        GL11.glDepthMask((boolean)true);
        GL11.glPopMatrix();
    }

    private void renderBanana(Entity entity, double x, double y, double z) {
        EntityFallingStar star = (EntityFallingStar) entity;
        float scale = (float)star.dwindle_timer * 0.375f / (float)EntityFallingStar.DWINDLE_TIME;
        float phase = (float)(Minecraft.getSystemTime() % 1000L) / 1000.0f;
        GL11.glPushMatrix();
        GL11.glTranslatef((float)x, (float)y, (float)z);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glEnable(32826);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glScalef(-1.0F, -1.0F, 1.0F);
        GL11.glScalef(scale, scale, scale);
        GL11.glRotatef(phase * -360.0f, 0.0f, 0.0f, 1.0f);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.bindTexture(bananaTexture);
        this.bananaModel.render(entity, 0.0F, 0.0F, phase, 0.0F, 0.0F, 0.0625F);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glPopMatrix();
    }

    private static boolean useAprilFoolsBanana() {
        if (!ModConfig.legendGearAprilFoolsBananaFallingStars) {
            return false;
        }
        Calendar now = Calendar.getInstance();
        return now.get(Calendar.MONTH) == Calendar.APRIL && now.get(Calendar.DAY_OF_MONTH) == 1;
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
