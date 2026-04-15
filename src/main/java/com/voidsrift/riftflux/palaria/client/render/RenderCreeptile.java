package com.voidsrift.riftflux.palaria.client.render;

import com.voidsrift.riftflux.Constants;
import com.voidsrift.riftflux.palaria.entity.EntityCreeptile;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderCreeptile extends RenderLiving {
    private static final ResourceLocation TEXTURE = new ResourceLocation(Constants.MODID, "textures/entity/palaria/creeptile.png");

    public RenderCreeptile(ModelBase model, float shadowSize) {
        super(model != null ? model : new ModelBiped(), shadowSize);
    }

    @Override
    protected void preRenderCallback(EntityLivingBase entity, float partialTicks) {
        EntityCreeptile creeptile = (EntityCreeptile) entity;
        float flash = creeptile.getCreeptileFlashIntensity(partialTicks);
        float pulse = 1.0F + MathHelper.sin(flash * 100.0F) * flash * 0.01F;
        if (flash < 0.0F) flash = 0.0F;
        if (flash > 1.0F) flash = 1.0F;
        flash *= flash;
        flash *= flash;
        float scaleXZ = (1.0F + flash * 0.4F) * pulse;
        float scaleY = (1.0F + flash * 0.1F) / pulse;
        GL11.glScalef(scaleXZ, scaleY, scaleXZ);
    }

    @Override
    protected int getColorMultiplier(EntityLivingBase entity, float light, float partialTicks) {
        float flash = ((EntityCreeptile) entity).getCreeptileFlashIntensity(partialTicks);
        if ((int) (flash * 10.0F) % 2 == 0) {
            return 0;
        }
        int alpha = (int) (flash * 0.2F * 255.0F);
        if (alpha < 0) alpha = 0;
        if (alpha > 255) alpha = 255;
        return alpha << 24 | 0xFFFFFF;
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        return TEXTURE;
    }
}
