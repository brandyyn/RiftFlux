/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.particle.EntityFX
 *  net.minecraft.client.renderer.Tessellator
 *  net.minecraft.client.renderer.texture.TextureManager
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.world.World
 *  org.lwjgl.opengl.GL11
 */
package net.nmccoy.legendgear.fx;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

public class EntityRippleFX
extends EntityFX {
    double hue;
    float baseScale;
    private static final ResourceLocation ringTexture = new ResourceLocation("legendgear", "textures/boostRipple.png");

    public EntityRippleFX(World par1World, double par2, double par4, double par6, double par8, double par10, double par12, float scale) {
        this(par1World, par2, par4, par6, par8, par10, par12);
        this.baseScale = scale;
    }

    public EntityRippleFX(World par1World, double par2, double par4, double par6, double par8, double par10, double par12) {
        super(par1World, par2, par4, par6, par8, par10, par12);
        this.motionX = par8;
        this.motionY = par10;
        this.motionZ = par12;
        this.posX = par2;
        this.posY = par4;
        this.posZ = par6;
        this.setParticleTextureIndex(32);
        this.noClip = true;
        this.particleMaxAge = 6;
        this.particleAge = 0;
        this.hue = Math.random() * Math.PI * 2.0;
        this.particleScale = 1.5f;
    }

    public float getBrightness(float par1) {
        return 1.0f;
    }

    public void renderParticle(Tessellator tess, float par2, float par3, float par4, float par5, float par6, float par7) {
        TextureManager tm = Minecraft.getMinecraft().renderEngine;
        GL11.glPushMatrix();
        tess = new Tessellator();
        tess.startDrawingQuads();
        tess.setColorRGBA_F(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha);
        tess.setBrightness(240);
        tm.bindTexture(ringTexture);
        GL11.glBlendFunc((int)770, (int)1);
        float f4 = 0.1f * this.particleScale;
        float f5 = (float)(this.prevPosX + (this.posX - this.prevPosX) * (double)par2 - interpPosX);
        float f6 = (float)(this.prevPosY + (this.posY - this.prevPosY) * (double)par2 - interpPosY);
        float f7 = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * (double)par2 - interpPosZ);
        float f8 = 1.0f;
        tess.addVertexWithUV((double)(f5 - par3 * f4 - par6 * f4), (double)(f6 - par4 * f4), (double)(f7 - par5 * f4 - par7 * f4), 1.0, 1.0);
        tess.addVertexWithUV((double)(f5 - par3 * f4 + par6 * f4), (double)(f6 + par4 * f4), (double)(f7 - par5 * f4 + par7 * f4), 1.0, 0.0);
        tess.addVertexWithUV((double)(f5 + par3 * f4 + par6 * f4), (double)(f6 + par4 * f4), (double)(f7 + par5 * f4 + par7 * f4), 0.0, 0.0);
        tess.addVertexWithUV((double)(f5 + par3 * f4 - par6 * f4), (double)(f6 - par4 * f4), (double)(f7 + par5 * f4 - par7 * f4), 0.0, 1.0);
        tess.draw();
        GL11.glBlendFunc((int)770, (int)771);
        GL11.glPopMatrix();
    }

    public void onUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        if (this.particleAge++ >= this.particleMaxAge) {
            this.setDead();
        }
        this.moveEntity(this.motionX, this.motionY, this.motionZ);
        this.hue += 0.3;
        float r = (float)Math.sin(this.hue) / 2.0f + 0.5f;
        float g = (float)Math.sin(this.hue + 2.0943951023931953) / 2.0f + 0.5f;
        float b = (float)Math.sin(this.hue - 2.0943951023931953) / 2.0f + 0.5f;
        float freshness = 1.0f - (float)this.particleAge * 1.0f / (float)this.particleMaxAge;
        this.particleAlpha = (float)Math.sin((double)freshness * Math.PI);
        this.particleRed = -0.5f + 0.5f * freshness * 2.0f;
        this.particleGreen = 0.5f + 0.5f * freshness;
        this.particleBlue = 1.0f;
        this.particleScale = (1.0f - freshness * 0.7f) * this.baseScale;
    }

    public int getBrightnessForRender(float par1) {
        return 240;
    }
}

