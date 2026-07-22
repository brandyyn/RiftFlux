/*
 * Decompiled with CFR 0.152.
 */
package de.sanandrew.core.manpack.util.client;

import de.sanandrew.core.manpack.mod.client.particle.SAPEffectRenderer;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.world.World;

public class EntityParticle
extends EntityFX {
    protected int brightness = -1;

    public EntityParticle(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    public EntityParticle(World world, double x, double y, double z, double motX, double motY, double motZ) {
        super(world, x, y, z, motX, motY, motZ);
    }

    @Override
    public int getFXLayer() {
        return SAPEffectRenderer.INSTANCE.getDefaultFxLayer();
    }

    @Override
    public void setParticleTextureIndex(int index) {
        this.particleTextureIndexX = index % 16;
        this.particleTextureIndexY = index / 16;
    }

    public void setParticleColor(float red, float green, float blue) {
        this.particleRed = red;
        this.particleGreen = green;
        this.particleBlue = blue;
    }

    public void setParticleColorRNG(float red, float green, float blue) {
        float shade = this.rand.nextFloat() * 0.2f;
        this.particleRed = Math.max(red - 0.2f, 0.0f) + shade;
        this.particleGreen = Math.max(green - 0.2f, 0.0f) + shade;
        this.particleBlue = Math.max(blue - 0.2f, 0.0f) + shade;
    }

    @Override
    public int getBrightnessForRender(float partTicks) {
        return this.brightness < 0 ? super.getBrightnessForRender(partTicks) : this.brightness;
    }

    public void setBrightness(int bright) {
        this.brightness = bright;
    }
}

