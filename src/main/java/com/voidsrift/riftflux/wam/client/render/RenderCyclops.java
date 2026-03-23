package com.voidsrift.riftflux.wam.client.render;

import com.voidsrift.riftflux.wam.client.model.ModelCyclops;
import com.voidsrift.riftflux.wam.entity.EntityCyclops;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderCyclops extends RenderLiving {
    private final ModelCyclops model;

    public RenderCyclops() {
        super(new ModelCyclops(), 0.5F);
        model = (ModelCyclops) mainModel;
    }

    @Override
    public void doRender(Entity entity, double x, double y, double z, float yaw, float partialTicks) {
        if (entity instanceof EntityCyclops) {
            model.hasEye = ((EntityCyclops) entity).hasEye();
        }
        super.doRender(entity, x, y, z, yaw, partialTicks);
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        return WAMTextures.CYCLOPS;
    }

    @Override
    protected void rotateCorpse(EntityLivingBase entity, float animationPosition, float rotationYaw, float partialTicks) {
        super.rotateCorpse(entity, animationPosition, rotationYaw, partialTicks);
        applyWalkSway(entity, partialTicks);
    }

    @Override
    protected void preRenderCallback(EntityLivingBase entity, float partialTickTime) {
        GL11.glTranslatef(0.0F, 0.0F, -0.5F);
    }

    private static void applyWalkSway(EntityLivingBase entity, float partialTicks) {
        if (entity.limbSwingAmount < 0.01F) {
            return;
        }

        float period = 13.0F;
        float swing = entity.limbSwing - entity.limbSwingAmount * (1.0F - partialTicks) + 6.0F;
        float offset = (Math.abs(swing % period - period * 0.5F) - period * 0.25F) / (period * 0.25F);
        GL11.glRotatef(6.5F * offset, 0.0F, 0.0F, 1.0F);
    }
}
