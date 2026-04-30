package com.voidsrift.riftflux.duckling;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;
import software.bernie.geckolib3.core.util.Color;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class RenderSootSprite extends GeoEntityRenderer<EntitySootSprite> {
    private static final float FLAT_ITEM_CLOCKWISE_ROLL = -25.0F;
    private static final float FLAT_ITEM_DEPTH = 0.0625F;
    private static final ResourceLocation ENCHANTED_ITEM_GLINT =
            new ResourceLocation("textures/misc/enchanted_item_glint.png");

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
            this.renderCustomName(entity, x, y, z);
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

    private void renderCustomName(Entity entity, double x, double y, double z) {
        if (entity instanceof EntityLiving && ((EntityLiving) entity).hasCustomNameTag()) {
            this.func_147906_a(entity, ((EntityLiving) entity).getCustomNameTag(), x, y, z, 64);
        }
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

        DucklingRenderState.Snapshot snapshot = DucklingRenderState.capture();
        int brightness = this.resolveHeldItemBrightness(sprite, partialTicks);
        float itemBrightnessX = (float)(brightness & 65535);
        float itemBrightnessY = (float)(brightness >> 16);
        DucklingRenderState.pushRenderAttribs();
        DucklingRenderState.pushRenderClientAttribs();
        DucklingRenderState.pushRenderMatrices();
        DucklingRenderState.prepareForRender();
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

            boolean blockModel = this.shouldRenderHeldBlockModel(held);
            GL11.glTranslatef(0.0F, 0.48F, 0.0F);
            if (!blockModel) {
                GL11.glRotatef(FLAT_ITEM_CLOCKWISE_ROLL, 0.0F, 0.0F, 1.0F);
                GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(25.5F, 1.0F, 0.0F, 0.0F);
                GL11.glTranslatef(0.0F, -0.025F, 0.0F);
            }
            GL11.glScalef(0.36F, 0.36F, 0.36F);
            if (blockModel) {
                GL11.glScalef(0.75F, 0.75F, 0.75F);
            }
            DucklingRenderState.prepareTexturedLightmap(itemBrightnessX, itemBrightnessY);
            DucklingRenderState.prepareForRender();
            RenderHelper.enableStandardItemLighting();
            if (blockModel) {
                RenderManager.instance.itemRenderer.renderItem(sprite, held, 0, IItemRenderer.ItemRenderType.ENTITY);
            } else {
                this.renderFlatHeldItem(sprite, held, brightness);
            }
            RenderHelper.disableStandardItemLighting();
        } finally {
            DucklingRenderState.popRenderMatrices();
            DucklingRenderState.popRenderClientAttribs();
            DucklingRenderState.popRenderAttribs();
            DucklingRenderState.restoreAfterRender(snapshot);
        }
    }

    private boolean shouldRenderHeldBlockModel(ItemStack held) {
        if (held == null || !(held.getItem() instanceof ItemBlock)) {
            return false;
        }

        Block block = Block.getBlockFromItem(held.getItem());
        return block != null && block != Blocks.air && RenderBlocks.renderItemIn3d(block.getRenderType());
    }

    private int resolveHeldItemBrightness(EntitySootSprite sprite, float partialTicks) {
        if (sprite.worldObj == null) {
            return sprite.getBrightnessForRender(partialTicks);
        }

        int x = MathHelper.floor_double(sprite.posX);
        int y = MathHelper.floor_double(sprite.posY + (double) (sprite.height * 0.5F));
        int z = MathHelper.floor_double(sprite.posZ);
        return sprite.worldObj.getLightBrightnessForSkyBlocks(x, y, z, 0);
    }

    private void renderFlatHeldItem(EntitySootSprite sprite, ItemStack held, int brightness) {
        TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();
        textureManager.bindTexture(textureManager.getResourceLocation(held.getItemSpriteNumber()));
        TextureUtil.func_152777_a(false, false, 1.0F);
        Tessellator tessellator = Tessellator.instance;
        int passes = held.getItem().requiresMultipleRenderPasses()
                ? held.getItem().getRenderPasses(held.getItemDamage())
                : 1;

        GL11.glPushMatrix();
        GL11.glTranslatef(-0.5F, -0.5F, 0.0F);
        for (int pass = 0; pass < passes; pass++) {
            IIcon icon = this.resolveFlatItemIcon(sprite, held, pass);
            if (icon == null) {
                continue;
            }

            int color = held.getItem().getColorFromItemStack(held, pass);
            float red = (float) (color >> 16 & 255) / 255.0F;
            float green = (float) (color >> 8 & 255) / 255.0F;
            float blue = (float) (color & 255) / 255.0F;
            GL11.glColor4f(red, green, blue, 1.0F);
            this.renderFlatHeldItemIn2D(
                    tessellator,
                    icon.getMaxU(),
                    icon.getMinV(),
                    icon.getMinU(),
                    icon.getMaxV(),
                    icon.getIconWidth(),
                    icon.getIconHeight(),
                    FLAT_ITEM_DEPTH,
                    brightness
            );
        }

        if (held.hasEffect(0)) {
            this.renderFlatHeldItemGlint(textureManager, tessellator);
        }

        GL11.glPopMatrix();
        TextureUtil.func_147945_b();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private IIcon resolveFlatItemIcon(EntitySootSprite sprite, ItemStack held, int pass) {
        IIcon icon = sprite.getItemIcon(held, pass);
        if (icon == null && held.getItem() != null) {
            icon = held.getItem().getIcon(held, pass);
        }
        if (icon == null) {
            icon = held.getIconIndex();
        }
        return icon;
    }

    private void renderFlatHeldItemIn2D(
            Tessellator tessellator,
            float maxU,
            float minV,
            float minU,
            float maxV,
            int width,
            int height,
            float depth,
            int brightness
    ) {
        this.startLitItemQuad(tessellator, brightness, 0.0F, 0.0F, 1.0F);
        tessellator.addVertexWithUV(0.0D, 0.0D, 0.0D, maxU, maxV);
        tessellator.addVertexWithUV(1.0D, 0.0D, 0.0D, minU, maxV);
        tessellator.addVertexWithUV(1.0D, 1.0D, 0.0D, minU, minV);
        tessellator.addVertexWithUV(0.0D, 1.0D, 0.0D, maxU, minV);
        tessellator.draw();

        this.startLitItemQuad(tessellator, brightness, 0.0F, 0.0F, -1.0F);
        tessellator.addVertexWithUV(0.0D, 1.0D, -depth, maxU, minV);
        tessellator.addVertexWithUV(1.0D, 1.0D, -depth, minU, minV);
        tessellator.addVertexWithUV(1.0D, 0.0D, -depth, minU, maxV);
        tessellator.addVertexWithUV(0.0D, 0.0D, -depth, maxU, maxV);
        tessellator.draw();

        float texelU = 0.5F * (maxU - minU) / (float) width;
        float texelV = 0.5F * (maxV - minV) / (float) height;

        this.startLitItemQuad(tessellator, brightness, -1.0F, 0.0F, 0.0F);
        for (int index = 0; index < width; index++) {
            float edge = (float) index / (float) width;
            float u = maxU + (minU - maxU) * edge - texelU;
            tessellator.addVertexWithUV(edge, 0.0D, -depth, u, maxV);
            tessellator.addVertexWithUV(edge, 0.0D, 0.0D, u, maxV);
            tessellator.addVertexWithUV(edge, 1.0D, 0.0D, u, minV);
            tessellator.addVertexWithUV(edge, 1.0D, -depth, u, minV);
        }
        tessellator.draw();

        this.startLitItemQuad(tessellator, brightness, 1.0F, 0.0F, 0.0F);
        for (int index = 0; index < width; index++) {
            float edge = (float) index / (float) width;
            float u = maxU + (minU - maxU) * edge - texelU;
            edge += 1.0F / (float) width;
            tessellator.addVertexWithUV(edge, 1.0D, -depth, u, minV);
            tessellator.addVertexWithUV(edge, 1.0D, 0.0D, u, minV);
            tessellator.addVertexWithUV(edge, 0.0D, 0.0D, u, maxV);
            tessellator.addVertexWithUV(edge, 0.0D, -depth, u, maxV);
        }
        tessellator.draw();

        this.startLitItemQuad(tessellator, brightness, 0.0F, 1.0F, 0.0F);
        for (int index = 0; index < height; index++) {
            float edge = (float) index / (float) height;
            float v = maxV + (minV - maxV) * edge - texelV;
            edge += 1.0F / (float) height;
            tessellator.addVertexWithUV(0.0D, edge, 0.0D, maxU, v);
            tessellator.addVertexWithUV(1.0D, edge, 0.0D, minU, v);
            tessellator.addVertexWithUV(1.0D, edge, -depth, minU, v);
            tessellator.addVertexWithUV(0.0D, edge, -depth, maxU, v);
        }
        tessellator.draw();

        this.startLitItemQuad(tessellator, brightness, 0.0F, -1.0F, 0.0F);
        for (int index = 0; index < height; index++) {
            float edge = (float) index / (float) height;
            float v = maxV + (minV - maxV) * edge - texelV;
            tessellator.addVertexWithUV(1.0D, edge, 0.0D, minU, v);
            tessellator.addVertexWithUV(0.0D, edge, 0.0D, maxU, v);
            tessellator.addVertexWithUV(0.0D, edge, -depth, maxU, v);
            tessellator.addVertexWithUV(1.0D, edge, -depth, minU, v);
        }
        tessellator.draw();
    }

    private void startLitItemQuad(Tessellator tessellator, int brightness, float normalX, float normalY, float normalZ) {
        tessellator.startDrawingQuads();
        tessellator.setBrightness(brightness);
        tessellator.setNormal(normalX, normalY, normalZ);
    }

    private void renderFlatHeldItemGlint(TextureManager textureManager, Tessellator tessellator) {
        GL11.glDepthFunc(GL11.GL_EQUAL);
        GL11.glDisable(GL11.GL_LIGHTING);
        textureManager.bindTexture(ENCHANTED_ITEM_GLINT);
        GL11.glEnable(GL11.GL_BLEND);
        OpenGlHelper.glBlendFunc(768, 1, 1, 0);
        GL11.glColor4f(0.38F, 0.19F, 0.61F, 1.0F);
        GL11.glMatrixMode(GL11.GL_TEXTURE);
        GL11.glPushMatrix();
        GL11.glScalef(0.125F, 0.125F, 0.125F);
        float scroll = (float) (Minecraft.getSystemTime() % 3000L) / 3000.0F * 8.0F;
        GL11.glTranslatef(scroll, 0.0F, 0.0F);
        GL11.glRotatef(-50.0F, 0.0F, 0.0F, 1.0F);
        ItemRenderer.renderItemIn2D(tessellator, 0.0F, 0.0F, 1.0F, 1.0F, 256, 256, 0.0625F);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glScalef(0.125F, 0.125F, 0.125F);
        scroll = (float) (Minecraft.getSystemTime() % 4873L) / 4873.0F * 8.0F;
        GL11.glTranslatef(-scroll, 0.0F, 0.0F);
        GL11.glRotatef(10.0F, 0.0F, 0.0F, 1.0F);
        ItemRenderer.renderItemIn2D(tessellator, 0.0F, 0.0F, 1.0F, 1.0F, 256, 256, 0.0625F);
        GL11.glPopMatrix();
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glDepthFunc(GL11.GL_LEQUAL);
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
