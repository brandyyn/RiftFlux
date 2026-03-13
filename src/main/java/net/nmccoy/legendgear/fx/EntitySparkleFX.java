/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.particle.EntityFX
 *  net.minecraft.client.renderer.Tessellator
 *  net.minecraft.world.World
 */
package net.nmccoy.legendgear.fx;

import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.World;

public class EntitySparkleFX
extends EntityFX {
    double hue;
    float baseScale;

    public EntitySparkleFX(World par1World, double par2, double par4, double par6, double par8, double par10, double par12, float scale) {
        this(par1World, par2, par4, par6, par8, par10, par12);
        this.baseScale = scale;
    }

    public EntitySparkleFX(World par1World, double par2, double par4, double par6, double par8, double par10, double par12) {
        super(par1World, par2, par4, par6, par8, par10, par12);
        this.motionX = par8;
        this.motionY = par10;
        this.motionZ = par12;
        this.posX = par2;
        this.posY = par4;
        this.posZ = par6;
        this.setParticleTextureIndex(149);
        this.noClip = true;
        this.particleMaxAge = 6;
        this.particleAge = 0;
        this.hue = Math.random() * Math.PI * 2.0;
        this.particleScale = 1.5f;
    }

    public float getBrightness(float par1) {
        return 1.0f;
    }

    public void renderParticle(Tessellator par1Tessellator, float par2, float par3, float par4, float par5, float par6, float par7) {
        super.renderParticle(par1Tessellator, par2, par3, par4, par5, par6, par7);
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
        this.particleAlpha = 1.0f;
        this.particleRed = 1.0f;
        this.particleGreen = 1.0f;
        this.particleBlue = freshness;
        this.particleScale = freshness * this.baseScale;
    }

    public int getBrightnessForRender(float par1) {
        return 240;
    }
}

