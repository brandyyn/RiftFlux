package com.voidsrift.riftflux.wam.client.render;

import com.voidsrift.riftflux.wam.client.model.ModelBlackWidow;
import com.voidsrift.riftflux.wam.entity.EntityBlackWidow;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderBlackWidow extends RenderLiving {
    private final ModelBlackWidow model;

    public RenderBlackWidow() {
        super(new ModelBlackWidow(), 1.0F);
        model = (ModelBlackWidow) mainModel;
        setRenderPassModel(model);
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        return WAMTextures.BLACK_WIDOW;
    }

    @Override
    protected void preRenderCallback(EntityLivingBase entity, float partialTickTime) {
        if (!(entity instanceof EntityBlackWidow)) {
            return;
        }

        EntityBlackWidow widow = (EntityBlackWidow) entity;
        float scale = widow.widowScaleAmount();
        GL11.glScalef(scale, scale, scale);
        if (widow.isThreadAmbushPose()) {
            GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
            return;
        }

        if (widow.isClimbingWallPose()) {
            switch (widow.getClimbFace()) {
                case 1:
                    GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
                    break;
                case 2:
                    GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
                    break;
                case 3:
                    GL11.glRotatef(-90.0F, 0.0F, 0.0F, 1.0F);
                    break;
                case 4:
                    GL11.glRotatef(90.0F, 0.0F, 0.0F, 1.0F);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected float getDeathMaxRotation(EntityLivingBase entity) {
        return 180.0F;
    }

    @Override
    protected int shouldRenderPass(EntityLivingBase entity, int pass, float partialTickTime) {
        if (pass != 0) {
            return -1;
        }

        bindTexture(WAMTextures.BLACK_WIDOW_EYES);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);
        GL11.glDepthMask(true);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        return 1;
    }
}
