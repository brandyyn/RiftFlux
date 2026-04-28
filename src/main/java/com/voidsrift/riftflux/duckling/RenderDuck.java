package com.voidsrift.riftflux.duckling;

import net.geckominecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
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
        try {
            super.doRender(entity, x, y, z, yaw, partialTicks);
            if (entity instanceof EntityLiving) {
                GeoLeashRenderer.renderLeash((EntityLiving) entity, x, y, z, partialTicks);
            }
        } finally {
            DucklingRenderState.popRenderMatrices();
            DucklingRenderState.popRenderClientAttribs();
            DucklingRenderState.popRenderAttribs();
            DucklingRenderState.restoreAfterRender(snapshot);
        }
    }

    @Override
    protected void renderLeash(EntityLiving entity, double x, double y, double z, float entityYaw, float partialTicks) {
        // Rendered manually after GeoEntityRenderer finishes so it uses vanilla world-space transforms.
    }

}
