package com.voidsrift.riftflux.duckling;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import software.bernie.geckolib3.core.util.Color;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class RenderQuackling extends GeoEntityRenderer<EntityQuackling> {
    private static final String WDMLA_ROOT = "com.gtnewhorizons.wdmla.";
    private static final String WAILA_ROOT = "mcp.mobius.waila.";

    private final ModelQuackling quacklingModel;

    public RenderQuackling() {
        this(new ModelQuackling());
    }

    private RenderQuackling(ModelQuackling model) {
        super(model);
        this.quacklingModel = model;
        this.shadowSize = 0.35F;
    }

    @Override
    public ResourceLocation getTextureLocation(EntityQuackling quackling) {
        return this.quacklingModel.getTextureLocation(quackling);
    }

    @Override
    public Color getRenderColor(EntityQuackling quackling, float partialTicks) {
        if (quackling != null && (quackling.hurtTime > 0 || quackling.deathTime > 0)) {
            return Color.ofRGBA(255, 150, 150, 255);
        }
        return Color.WHITE;
    }

    @Override
    public void doRender(Entity entity, double x, double y, double z, float yaw, float partialTicks) {
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT | GL11.GL_LIGHTING_BIT | GL11.GL_CURRENT_BIT);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        if (entity instanceof EntityQuackling) {
            this.applyEntityLight((EntityQuackling)entity, partialTicks);
        }
        try {
            super.doRender(entity, x, y, z, yaw, partialTicks);
            if (entity instanceof EntityLiving) {
                GeoLeashRenderer.renderLeash((EntityLiving)entity, x, y, z, partialTicks);
            }
        } finally {
            GL11.glPopAttrib();
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    @Override
    public void renderEarly(GeoModel model, EntityQuackling quackling, float partialTicks, float red, float green, float blue, float alpha) {
        super.renderEarly(model, quackling, partialTicks, red, green, blue, alpha);
        this.applyEntityLight(quackling, partialTicks);
        if (quackling.isChild()) {
            GL11.glScalef(0.5F, 0.5F, 0.5F);
        }
    }

    @Override
    public void renderAfter(GeoModel model, EntityQuackling quackling, float partialTicks, float red, float green, float blue, float alpha) {
        super.renderAfter(model, quackling, partialTicks, red, green, blue, alpha);
        if (!quackling.isFishing() || this.isTooltipPreviewRender()) {
            return;
        }
        ItemStack rod = new ItemStack(Items.fishing_rod);
        Optional<GeoBone> rodBone = model.getBone("rod");
        if (rodBone.isPresent()) {
            this.renderFishingRod(quackling, rodBone.get(), rod, partialTicks);
        }
    }

    private void renderFishingRod(EntityQuackling quackling, GeoBone rodBone, ItemStack rod, float partialTicks) {
        GL11.glPushMatrix();

        GeoBone[] path = this.getPathFromRoot(rodBone);
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

        GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
        GL11.glTranslatef(0.0F, 0.45F, -0.3375F);
        GL11.glRotatef(-45.0F, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
        this.applyEntityLight(quackling, partialTicks, false);
        RenderManager.instance.itemRenderer.renderItem(quackling, rod, 0, IItemRenderer.ItemRenderType.EQUIPPED);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glPopMatrix();
    }

    private void applyEntityLight(EntityQuackling quackling, float partialTicks) {
        this.applyEntityLight(quackling, partialTicks, true);
    }

    private void applyEntityLight(EntityQuackling quackling, float partialTicks, boolean applyHurtTint) {
        int brightness = quackling.getBrightnessForRender(partialTicks);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)(brightness & 65535), (float)(brightness >> 16));
        if (applyHurtTint && (quackling.hurtTime > 0 || quackling.deathTime > 0)) {
            GL11.glColor4f(1.0F, 0.35F, 0.35F, 1.0F);
            return;
        }
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    public GeoBone[] getPathFromRoot(GeoBone bone) {
        List<GeoBone> path = new ArrayList<GeoBone>();
        while (bone != null) {
            path.add(0, bone);
            bone = bone.parent;
        }
        return path.toArray(new GeoBone[path.size()]);
    }

    @Override
    protected void renderLeash(EntityLiving entity, double x, double y, double z, float yaw, float partialTicks) {
        // Rendered manually after GeoEntityRenderer finishes so it uses vanilla world-space transforms.
    }

    private boolean isTooltipPreviewRender() {
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        for (StackTraceElement element : stack) {
            String className = element.getClassName();
            if (className == null) {
                continue;
            }
            if (className.startsWith(WDMLA_ROOT) || className.startsWith(WAILA_ROOT)) {
                return true;
            }
        }
        return false;
    }
}
