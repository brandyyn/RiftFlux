/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.OpenGlHelper
 *  net.minecraft.client.renderer.Tessellator
 *  net.minecraft.client.renderer.entity.Render
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.item.EntityXPOrb
 *  net.minecraft.util.ResourceLocation
 *  org.lwjgl.opengl.GL11
 */
package net.nmccoy.legendgear.render;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.util.ResourceLocation;
import net.nmccoy.legendgear.render.Rainbow;
import org.lwjgl.opengl.GL11;

@SideOnly(value=Side.CLIENT)
public class RenderPrismaticXP
extends Render {
    private static final ResourceLocation experienceOrbTextures = new ResourceLocation("legendgear", "textures/xporb.png");

    public RenderPrismaticXP() {
        this.shadowSize = 0.15f;
        this.shadowOpaque = 0.75f;
    }

    protected ResourceLocation getEntityTexture(Entity p_110775_1_) {
        return experienceOrbTextures;
    }

    public void renderTheXPOrb(EntityXPOrb par1EntityXPOrb, double par2, double par4, double par6, float par8, float par9) {
        GL11.glPushMatrix();
        GL11.glTranslatef((float)((float)par2), (float)((float)par4), (float)((float)par6));
        int var10 = par1EntityXPOrb.getTextureByXP();
        this.bindEntityTexture((Entity)par1EntityXPOrb);
        Tessellator var11 = Tessellator.instance;
        float var12 = (float)(var10 % 4 * 16 + 0) / 64.0f;
        float var13 = (float)(var10 % 4 * 16 + 16) / 64.0f;
        float var14 = (float)(var10 / 4 * 16 + 0) / 64.0f;
        float var15 = (float)(var10 / 4 * 16 + 16) / 64.0f;
        float var16 = 1.0f;
        float var17 = 0.5f;
        float var18 = 0.25f;
        int var19 = 240;
        int var20 = var19 % 65536;
        int var21 = var19 / 65536;
        OpenGlHelper.setLightmapTextureCoords((int)OpenGlHelper.lightmapTexUnit, (float)((float)var20 / 1.0f), (float)((float)var21 / 1.0f));
        GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        float var26 = 255.0f;
        float time = (float)(Minecraft.getSystemTime() % 1000000L) / 50.0f;
        float phase = (float)((double)((float)(Minecraft.getSystemTime() % 1000L) / 1000.0f) + (par1EntityXPOrb.posX + par1EntityXPOrb.posZ) * 0.05);
        float r = Rainbow.r(phase);
        float g = Rainbow.g(phase);
        float b = Rainbow.b(phase);
        var21 = (int)(r * var26);
        int var22 = (int)(g * var26);
        int var23 = (int)(b * var26);
        int var24 = var21 << 16 | var22 << 8 | var23;
        GL11.glRotatef((float)(180.0f - this.renderManager.playerViewY), (float)0.0f, (float)1.0f, (float)0.0f);
        GL11.glRotatef((float)(-this.renderManager.playerViewX), (float)1.0f, (float)0.0f, (float)0.0f);
        float var25 = 0.3f;
        GL11.glScalef((float)var25, (float)var25, (float)var25);
        GL11.glDisable((int)2896);
        var11.startDrawingQuads();
        var11.setBrightness(240);
        var11.setColorRGBA_I(var24, 255);
        var11.setNormal(0.0f, 1.0f, 0.0f);
        var11.addVertexWithUV((double)(0.0f - var17), (double)(0.0f - var18), 0.0, (double)var12, (double)var15);
        var11.addVertexWithUV((double)(var16 - var17), (double)(0.0f - var18), 0.0, (double)var13, (double)var15);
        var11.addVertexWithUV((double)(var16 - var17), (double)(1.0f - var18), 0.0, (double)var13, (double)var14);
        var11.addVertexWithUV((double)(0.0f - var17), (double)(1.0f - var18), 0.0, (double)var12, (double)var14);
        var11.draw();
        GL11.glEnable((int)2896);
        GL11.glDisable((int)3042);
        GL11.glDisable((int)32826);
        GL11.glPopMatrix();
    }

    public void doRender(Entity par1Entity, double par2, double par4, double par6, float par8, float par9) {
        this.renderTheXPOrb((EntityXPOrb)par1Entity, par2, par4, par6, par8, par9);
    }
}

