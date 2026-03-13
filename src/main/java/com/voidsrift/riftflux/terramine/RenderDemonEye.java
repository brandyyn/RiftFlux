package com.voidsrift.riftflux.terramine;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class RenderDemonEye extends RenderLiving {
    private static final ResourceLocation DEMON_EYE = new ResourceLocation("riftflux:textures/entities/demon_eye.png");

    public RenderDemonEye(ModelBase model, float shadowSize) {
        super(model, shadowSize);
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        return DEMON_EYE;
    }
}
