package com.voidsrift.riftflux.pumpkinpastures.client.render;

import com.voidsrift.riftflux.Constants;
import net.minecraft.client.renderer.entity.RenderZombie;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class RenderPumpkinZombie extends RenderZombie {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(Constants.MODID, "textures/entity/pumpkinpastures/pumpkin_zombie.png");

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        return TEXTURE;
    }
}
