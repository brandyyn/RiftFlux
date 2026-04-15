package com.voidsrift.riftflux.palaria.client.render;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class RenderPalariaSeat extends Render {
    private static final ResourceLocation EMPTY = new ResourceLocation("minecraft", "textures/misc/unknown_pack.png");

    @Override
    public void doRender(Entity entity, double x, double y, double z, float yaw, float partialTicks) {
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        return EMPTY;
    }
}
