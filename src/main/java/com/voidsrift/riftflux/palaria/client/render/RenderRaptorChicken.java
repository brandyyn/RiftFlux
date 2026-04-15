package com.voidsrift.riftflux.palaria.client.render;

import com.voidsrift.riftflux.Constants;
import com.voidsrift.riftflux.palaria.entity.EntityAbstractRaptorChicken;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public class RenderRaptorChicken extends RenderLiving {
    private final ResourceLocation texture;

    public RenderRaptorChicken(ModelBase model, float shadowSize, String texturePath) {
        super(model != null ? model : new ModelBiped(), shadowSize);
        this.texture = new ResourceLocation(Constants.MODID, texturePath);
    }

    @Override
    protected float handleRotationFloat(EntityLivingBase entity, float partialTicks) {
        EntityAbstractRaptorChicken raptor = (EntityAbstractRaptorChicken) entity;
        float wing = raptor.field_70888_h + (raptor.field_70886_e - raptor.field_70888_h) * partialTicks;
        float dest = raptor.field_70884_g + (raptor.destPos - raptor.field_70884_g) * partialTicks;
        return (MathHelper.sin(wing) + 1.0F) * dest;
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        return texture;
    }
}
