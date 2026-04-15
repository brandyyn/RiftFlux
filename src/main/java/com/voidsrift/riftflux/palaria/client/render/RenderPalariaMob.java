package com.voidsrift.riftflux.palaria.client.render;

import com.voidsrift.riftflux.Constants;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class RenderPalariaMob extends RenderLiving {
    private final ResourceLocation texture;

    public RenderPalariaMob(ModelBase model, float shadowSize, String texturePath) {
        super(model != null ? model : new ModelBiped(), shadowSize);
        this.texture = new ResourceLocation(Constants.MODID, texturePath);
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        return texture;
    }
}
