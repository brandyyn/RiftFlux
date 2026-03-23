package com.voidsrift.riftflux.wam.client.render;

import com.voidsrift.riftflux.wam.client.model.ModelJaxx;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderJaxx extends RenderLiving {
    private final ModelJaxx model;

    public RenderJaxx() {
        super(new ModelJaxx(), 0.5F);
        model = (ModelJaxx) mainModel;
        setRenderPassModel(model);
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        return WAMTextures.JAXX;
    }

    @Override
    protected void rotateCorpse(EntityLivingBase entity, float animationPosition, float rotationYaw, float partialTicks) {
        super.rotateCorpse(entity, animationPosition, rotationYaw, partialTicks);
        applyWalkSway(entity, partialTicks);
    }

    @Override
    protected int shouldRenderPass(EntityLivingBase entity, int pass, float partialTickTime) {
        if (pass != 0) {
            return -1;
        }

        bindTexture(WAMTextures.JAXX_EYES);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        return 1;
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
