package com.voidsrift.riftflux.terramine;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.BossStatus;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderDestroyerSegment extends RenderLiving {
    private static final ResourceLocation PROBE_TEXTURE = new ResourceLocation("riftflux:textures/entities/destroyer/probe.png");
    private static final ResourceLocation HEAD_EYE_TEXTURE = new ResourceLocation("riftflux:textures/entities/destroyer/headEye.png");
    private static final ResourceLocation BODY_EYE_TEXTURE = new ResourceLocation("riftflux:textures/entities/destroyer/bodyEye.png");
    private static final ResourceLocation PROBE_EYE_TEXTURE = new ResourceLocation("riftflux:textures/entities/destroyer/probeEye.png");

    private final float scale;

    public RenderDestroyerSegment(ModelBase model, float scale) {
        super(model, 1.0F);
        this.setRenderPassModel(new ModelDestroyer(false));
        this.scale = scale;
    }

    @Override
    public void doRender(Entity entity, double x, double y, double z, float yaw, float partialTicks) {
        if (entity instanceof IBossDisplayData) {
            BossStatus.setBossStatus((IBossDisplayData) entity, false);
        }
        super.doRender(entity, x, y, z, yaw, partialTicks);
        resetLocalRenderState();
    }

    @Override
    protected void preRenderCallback(EntityLivingBase entity, float partialTick) {
        GL11.glScalef(this.scale, this.scale, this.scale);
    }

    @Override
    protected int shouldRenderPass(EntityLivingBase entity, int pass, float partialTick) {
        if (pass != 0) {
            return -1;
        }

        this.bindTexture(this.getEyeTexture(entity));
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);
        int light = 6680;
        int lightX = light % 65536;
        int lightY = light / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) lightX, (float) lightY);
        GL11.glColor4f(2.0F, 2.0F, 2.0F, 1.0F);
        return 1;
    }

    private static void resetLocalRenderState() {
        OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private ResourceLocation getEyeTexture(EntityLivingBase entity) {
        if (entity instanceof EntityDestroyerHead) {
            return HEAD_EYE_TEXTURE;
        }
        if (entity instanceof EntityDestroyerProbe) {
            return PROBE_EYE_TEXTURE;
        }
        return BODY_EYE_TEXTURE;
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        return entity instanceof EntityDestroyerBase ? ((EntityDestroyerBase) entity).getTexture() : PROBE_TEXTURE;
    }
}