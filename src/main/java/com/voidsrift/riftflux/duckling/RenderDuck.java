package com.voidsrift.riftflux.duckling;

import net.geckominecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import software.bernie.geckolib3.core.util.Color;
import software.bernie.geckolib3.geo.render.built.GeoCube;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class RenderDuck extends GeoEntityRenderer<EntityDuck> {
    private static final boolean RIFTFLUX_HAS_BEDDIUM =
            hasClass("com.ventooth.beddium.modules.TerrainRendering.CeleritasWorldRenderer");

    private int riftflux$currentBeddiumBrightness = -1;

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
        super.renderEarly(model, duck, partialTicks, red, green, blue, alpha);
        if (duck.isChild()) {
            GlStateManager.scale(0.5F, 0.5F, 0.5F);
        }
    }

    @Override
    public void doRender(Entity entity, double x, double y, double z, float yaw, float partialTicks) {
        DucklingRenderState.Snapshot snapshot = DucklingRenderState.capture();
        DucklingRenderState.pushRenderAttribs();
        DucklingRenderState.pushRenderClientAttribs();
        DucklingRenderState.pushRenderMatrices();
        DucklingRenderState.prepareForEntityRender(entity, partialTicks);
        this.riftflux$currentBeddiumBrightness = RIFTFLUX_HAS_BEDDIUM && entity != null
                ? entity.getBrightnessForRender(partialTicks)
                : -1;
        try {
            super.doRender(entity, x, y, z, yaw, partialTicks);
            if (entity instanceof EntityLiving) {
                GeoLeashRenderer.renderLeash((EntityLiving) entity, x, y, z, partialTicks);
            }
            this.renderCustomName(entity, x, y, z);
        } finally {
            DucklingRenderState.popRenderMatrices();
            DucklingRenderState.popRenderClientAttribs();
            DucklingRenderState.popRenderAttribs();
            DucklingRenderState.restoreAfterRender(snapshot);
            this.riftflux$currentBeddiumBrightness = -1;
        }
    }

    @Override
    public void renderCube(Tessellator tessellator, GeoCube cube, float red, float green, float blue, float alpha) {
        if (this.riftflux$currentBeddiumBrightness >= 0) {
            tessellator.setBrightness(this.riftflux$currentBeddiumBrightness);
        }
        super.renderCube(tessellator, cube, red, green, blue, alpha);
    }

    @Override
    protected void renderLeash(EntityLiving entity, double x, double y, double z, float entityYaw, float partialTicks) {
        // Rendered manually after GeoEntityRenderer finishes so it uses vanilla world-space transforms.
    }

    private void renderCustomName(Entity entity, double x, double y, double z) {
        if (entity instanceof EntityLiving && ((EntityLiving) entity).hasCustomNameTag()) {
            this.func_147906_a(entity, ((EntityLiving) entity).getCustomNameTag(), x, y, z, 64);
        }
    }

    private static boolean hasClass(String className) {
        try {
            ClassLoader loader = RenderDuck.class.getClassLoader();
            return loader.getResource(className.replace('.', '/') + ".class") != null;
        } catch (Throwable ignored) {
            return false;
        }
    }

}
