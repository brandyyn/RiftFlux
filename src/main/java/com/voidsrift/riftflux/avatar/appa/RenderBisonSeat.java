package com.voidsrift.riftflux.avatar.appa;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class RenderBisonSeat extends Render {
    @Override
    public void doRender(Entity entity, double x, double y, double z, float yaw, float partialTicks) {
        // Intentionally empty: seats are invisible.
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        return null;
    }
}
