/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.opengl.GL11
 */
package de.sanandrew.mods.claysoldiers.client.particle;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.sanandrew.mods.claysoldiers.client.util.Textures;
import java.util.ArrayDeque;
import java.util.Queue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

@SideOnly(value=Side.CLIENT)
public class ParticleNexusFX
extends EntityFX {
    public static Queue<ParticleNexusFX> s_queuedRenders = new ArrayDeque<ParticleNexusFX>();
    float f;
    float f1;
    float f2;
    float f3;
    float f4;
    float f5;
    float moteParticleScale;
    int moteHalfLife;

    public ParticleNexusFX(World world, double x, double y, double z, float size, float red, float green, float blue) {
        super(world, x, y, z, 0.0, 0.0, 0.0);
        this.particleRed = red;
        this.particleGreen = green;
        this.particleBlue = blue;
        this.particleGravity = 0.0f;
        this.motionZ = 0.0;
        this.motionY = 0.0;
        this.motionX = 0.0;
        this.particleScale *= size;
        this.moteParticleScale = this.particleScale;
        this.particleMaxAge = (int)(28.0 / (Math.random() * 0.3 + 0.7) * 1.0);
        this.moteHalfLife = this.particleMaxAge / 2;
        this.noClip = true;
        this.setSize(0.01f, 0.01f);
        EntityLivingBase renderentity = Minecraft.getMinecraft().renderViewEntity;
        int visibleDistance = 50;
        if (!Minecraft.getMinecraft().gameSettings.fancyGraphics) {
            visibleDistance = 25;
        }
        if (renderentity == null || renderentity.getDistance(this.posX, this.posY, this.posZ) > (double)visibleDistance) {
            this.particleMaxAge = 0;
        }
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
    }

    public static void dispatchQueuedRenders(Tessellator tessellator) {
        GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)0.75f);
        Minecraft.getMinecraft().renderEngine.bindTexture(Textures.NEXUS_PARTICLE);
        tessellator.startDrawingQuads();
        for (ParticleNexusFX wisp : s_queuedRenders) {
            wisp.renderQueued(tessellator);
        }
        tessellator.draw();
        s_queuedRenders.clear();
    }

    @Override
    public void onUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        if (this.particleAge++ >= this.particleMaxAge) {
            this.setDead();
        }
        this.motionY -= 0.04 * (double)this.particleGravity;
        this.posX += this.motionX;
        this.posY += this.motionY;
        this.posZ += this.motionZ;
        this.motionX *= 0.98;
        this.motionY *= 0.98;
        this.motionZ *= 0.98;
    }

    @Override
    public void renderParticle(Tessellator tessellator, float f, float f1, float f2, float f3, float f4, float f5) {
        this.f = f;
        this.f1 = f1;
        this.f2 = f2;
        this.f3 = f3;
        this.f4 = f4;
        this.f5 = f5;
        s_queuedRenders.add(this);
    }

    private void renderQueued(Tessellator tessellator) {
        float agescale = (float)this.particleAge / (float)this.moteHalfLife;
        if (agescale > 1.0f) {
            agescale = 2.0f - agescale;
        }
        this.particleScale = this.moteParticleScale * agescale;
        float f10 = 0.5f * this.particleScale;
        float f11 = (float)(this.prevPosX + (this.posX - this.prevPosX) * (double)this.f - interpPosX);
        float f12 = (float)(this.prevPosY + (this.posY - this.prevPosY) * (double)this.f - interpPosY);
        float f13 = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * (double)this.f - interpPosZ);
        tessellator.setBrightness(240);
        tessellator.setColorRGBA_F(this.particleRed, this.particleGreen, this.particleBlue, 0.5f);
        tessellator.addVertexWithUV(f11 - this.f1 * f10 - this.f4 * f10, f12 - this.f2 * f10, f13 - this.f3 * f10 - this.f5 * f10, 0.0, 1.0);
        tessellator.addVertexWithUV(f11 - this.f1 * f10 + this.f4 * f10, f12 + this.f2 * f10, f13 - this.f3 * f10 + this.f5 * f10, 1.0, 1.0);
        tessellator.addVertexWithUV(f11 + this.f1 * f10 + this.f4 * f10, f12 + this.f2 * f10, f13 + this.f3 * f10 + this.f5 * f10, 1.0, 0.0);
        tessellator.addVertexWithUV(f11 + this.f1 * f10 - this.f4 * f10, f12 - this.f2 * f10, f13 + this.f3 * f10 - this.f5 * f10, 0.0, 0.0);
    }
}

