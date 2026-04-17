package com.voidsrift.riftflux.pumpkinpastures.client.render;

import com.voidsrift.riftflux.Constants;
import net.minecraft.client.renderer.entity.RenderSkeleton;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class RenderPumpkinSkeleton extends RenderSkeleton {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(Constants.MODID, "textures/entity/pumpkinpastures/pumpkin_skeleton.png");

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        return TEXTURE;
    }
}
