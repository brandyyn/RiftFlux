package com.voidsrift.riftflux.client;

import net.minecraft.entity.EntityLivingBase;

public interface EntityNameTagRenderBridge {
    void riftflux$renderDeferredNameTag(
            EntityLivingBase entity,
            double x,
            double y,
            double z
    );
}
