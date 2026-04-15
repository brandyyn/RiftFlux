package com.voidsrift.riftflux.palaria.client.render;

import com.voidsrift.riftflux.Constants;
import com.voidsrift.riftflux.palaria.entity.EntityNimatin;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderNimatin extends RenderLiving {
    private static final ResourceLocation WILD = new ResourceLocation(Constants.MODID, "textures/entity/palaria/nimatin.png");
    private static final ResourceLocation ANGRY = new ResourceLocation(Constants.MODID, "textures/entity/palaria/nimatin_angry.png");
    private static final ResourceLocation TAME = new ResourceLocation(Constants.MODID, "textures/entity/palaria/nimatin_tame.png");

    public RenderNimatin(ModelBase model, float shadowSize) {
        super(model != null ? model : new ModelBiped(), shadowSize);
        setRenderPassModel(this.mainModel);
    }

    @Override
    protected int shouldRenderPass(EntityLivingBase entity, int pass, float partialTicks) {
        EntityNimatin nimatin = (EntityNimatin) entity;
        if (pass == 0 && nimatin.getWolfShaking()) {
            float shade = nimatin.getBrightness(partialTicks) * nimatin.getShadingWhileShaking(partialTicks);
            bindTexture(getEntityTexture(nimatin));
            GL11.glColor3f(shade, shade, shade);
            return 1;
        }
        GL11.glColor3f(1.0F, 1.0F, 1.0F);
        return -1;
    }

    @Override
    protected void renderModel(EntityLivingBase entity, float limbSwing, float limbSwingAmount, float ageInTicks,
                               float netHeadYaw, float headPitch, float scaleFactor) {
        GL11.glColor3f(1.0F, 1.0F, 1.0F);
        super.renderModel(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
        GL11.glColor3f(1.0F, 1.0F, 1.0F);
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        EntityNimatin nimatin = (EntityNimatin) entity;
        return nimatin.isTamed() ? TAME : nimatin.isAngry() ? ANGRY : WILD;
    }
}
