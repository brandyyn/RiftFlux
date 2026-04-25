package com.voidsrift.riftflux.duckling;

import net.geckominecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.core.util.Color;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class RenderDuck extends GeoEntityRenderer<EntityDuck> {
    public RenderDuck() {
        super(new ModelDuck());
        this.shadowSize = 0.25F;
    }

    @Override
    public ResourceLocation getTextureLocation(EntityDuck duck) {
        return duck.getTextureVariant().getTexture();
    }

    @Override
    public Color getRenderColor(EntityDuck duck, float partialTicks) {
        if (duck != null && (duck.hurtTime > 0 || duck.deathTime > 0)) {
            return Color.ofRGBA(255, 150, 150, 255);
        }
        return Color.WHITE;
    }

    @Override
    public void renderEarly(GeoModel model, EntityDuck duck, float partialTicks, float red, float green, float blue, float alpha) {
        this.applyEntityLight(duck, partialTicks);
        if (duck.isChild()) {
            GlStateManager.scale(0.5F, 0.5F, 0.5F);
        }
    }

    @Override
    public void doRender(Entity entity, double x, double y, double z, float yaw, float partialTicks) {
        super.doRender(entity, x, y, z, yaw, partialTicks);
        if (entity instanceof EntityLiving) {
            GeoLeashRenderer.renderLeash((EntityLiving)entity, x, y, z, partialTicks);
        }
    }

    private void applyEntityLight(EntityDuck duck, float partialTicks) {
        int brightness = duck.getBrightnessForRender(partialTicks);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)(brightness & 65535), (float)(brightness >> 16));
    }

    @Override
    protected void renderLeash(EntityLiving entity, double x, double y, double z, float yaw, float partialTicks) {
        // Rendered manually after GeoEntityRenderer finishes so it uses vanilla world-space transforms.
    }
}
