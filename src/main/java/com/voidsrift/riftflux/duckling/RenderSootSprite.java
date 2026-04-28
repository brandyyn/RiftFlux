package com.voidsrift.riftflux.duckling;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import software.bernie.geckolib3.core.util.Color;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class RenderSootSprite extends GeoEntityRenderer<EntitySootSprite> {
    private final ModelSootSprite sootModel;

    public RenderSootSprite() {
        this(new ModelSootSprite());
    }

    private RenderSootSprite(ModelSootSprite model) {
        super(model);
        this.sootModel = model;
        this.shadowSize = 0.18F;
    }

    @Override
    public ResourceLocation getTextureLocation(EntitySootSprite sprite) {
        return this.sootModel.getTextureLocation(sprite);
    }

    @Override
    public Color getRenderColor(EntitySootSprite sprite, float partialTicks) {
        if (sprite != null && (sprite.hurtTime > 0 || sprite.deathTime > 0)) {
            return Color.ofRGBA(255, 150, 150, 255);
        }
        return Color.WHITE;
    }

    @Override
    public void doRender(Entity entity, double x, double y, double z, float yaw, float partialTicks) {
        int previousMatrixMode = DucklingRenderState.captureMatrixMode();
        float previousBrightnessX = OpenGlHelper.lastBrightnessX;
        float previousBrightnessY = OpenGlHelper.lastBrightnessY;
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        try {
            super.doRender(entity, x, y, z, yaw, partialTicks);
            if (entity instanceof EntityLiving) {
                GeoLeashRenderer.renderLeash((EntityLiving) entity, x, y, z, partialTicks);
            }
        } finally {
            GL11.glPopAttrib();
            DucklingRenderState.restoreAfterRender(previousMatrixMode);
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, previousBrightnessX, previousBrightnessY);
            OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
            GL11.glMatrixMode(GL11.GL_MODELVIEW);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    @Override
    protected void renderLeash(EntityLiving entity, double x, double y, double z, float entityYaw, float partialTicks) {
        // Rendered manually after GeoEntityRenderer finishes so it uses vanilla world-space transforms.
    }

    @Override
    public void renderEarly(GeoModel model, EntitySootSprite sprite, float partialTicks, float red, float green, float blue, float alpha) {
        super.renderEarly(model, sprite, partialTicks, red, green, blue, alpha);
    }

    @Override
    public void renderAfter(GeoModel model, EntitySootSprite sprite, float partialTicks, float red, float green, float blue, float alpha) {
        super.renderAfter(model, sprite, partialTicks, red, green, blue, alpha);
        if (this.shouldRenderHeldItem(sprite)) {
            this.renderHeldItem(model, sprite, partialTicks);
        }
    }

    private void renderHeldItem(GeoModel model, EntitySootSprite sprite, float partialTicks) {
        ItemStack held = sprite.getHeldItem();
        if (held == null) {
            return;
        }

        Optional<GeoBone> body = model.getBone("body");
        if (!body.isPresent()) {
            return;
        }

        int previousMatrixMode = GL11.glGetInteger(GL11.GL_MATRIX_MODE);
        float previousBrightnessX = OpenGlHelper.lastBrightnessX;
        float previousBrightnessY = OpenGlHelper.lastBrightnessY;
        int brightness = sprite.getBrightnessForRender(partialTicks);
        float itemBrightnessX = (float)(brightness & 65535);
        float itemBrightnessY = (float)(brightness >> 16);
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
        GL11.glMatrixMode(GL11.GL_TEXTURE);
        GL11.glPushMatrix();
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glPushMatrix();
        try {
            GeoBone[] path = this.getPathFromRoot(body.get());
            for (int i = 0; i < path.length; i++) {
                GeoBone bone = path[i];
                float unit = 16.0F;
                GL11.glTranslatef(-bone.getPositionX() / unit, bone.getPositionY() / unit, bone.getPositionZ() / unit);
                GL11.glTranslatef(bone.getPivotX() / unit, bone.getPivotY() / unit, bone.getPivotZ() / unit);
                GL11.glRotatef((float)Math.toDegrees(bone.getRotationZ()), 0.0F, 0.0F, 1.0F);
                GL11.glRotatef((float)Math.toDegrees(bone.getRotationY()), 0.0F, 1.0F, 0.0F);
                GL11.glRotatef((float)Math.toDegrees(bone.getRotationX()), 1.0F, 0.0F, 0.0F);
                GL11.glScalef(bone.getScaleX(), bone.getScaleY(), bone.getScaleZ());
                GL11.glTranslatef(-bone.getPivotX() / unit, -bone.getPivotY() / unit, -bone.getPivotZ() / unit);
            }

            GL11.glTranslatef(0.0F, 0.48F, 0.0F);
            GL11.glRotatef(held.getItem() instanceof ItemBlock ? 0.0F : 220.0F, 0.0F, 1.0F, 0.0F);
            GL11.glScalef(0.36F, 0.36F, 0.36F);
            if (held.getItem() instanceof ItemBlock) {
                GL11.glScalef(0.75F, 0.75F, 0.75F);
            }
            DucklingRenderState.prepareTexturedLightmap(itemBrightnessX, itemBrightnessY);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
            GL11.glDisable(GL11.GL_BLEND);
            RenderHelper.enableStandardItemLighting();
            RenderManager.instance.itemRenderer.renderItem(sprite, held, 0, IItemRenderer.ItemRenderType.ENTITY);
            RenderHelper.disableStandardItemLighting();
        } finally {
            GL11.glMatrixMode(GL11.GL_MODELVIEW);
            GL11.glPopMatrix();
            OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
            GL11.glMatrixMode(GL11.GL_TEXTURE);
            GL11.glPopMatrix();
            GL11.glPopAttrib();
            DucklingRenderState.restoreAfterRender(previousMatrixMode);
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, previousBrightnessX, previousBrightnessY);
            OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
            GL11.glMatrixMode(GL11.GL_MODELVIEW);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    private boolean shouldRenderHeldItem(EntitySootSprite sprite) {
        return sprite != null && sprite.getHeldItem() != null;
    }

    public GeoBone[] getPathFromRoot(GeoBone bone) {
        List<GeoBone> path = new ArrayList<GeoBone>();
        while (bone != null) {
            path.add(0, bone);
            bone = bone.parent;
        }
        return path.toArray(new GeoBone[path.size()]);
    }
}
