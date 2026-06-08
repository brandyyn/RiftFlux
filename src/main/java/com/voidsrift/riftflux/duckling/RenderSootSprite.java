package com.voidsrift.riftflux.duckling;

import java.util.Optional;
import net.minecraft.block.Block;
import net.minecraft.block.BlockGrass;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
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
import net.minecraftforge.client.MinecraftForgeClient;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import software.bernie.geckolib3.core.util.Color;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.geo.render.built.GeoCube;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class RenderSootSprite extends GeoEntityRenderer<EntitySootSprite> {
    private static final float FLAT_ITEM_CLOCKWISE_ROLL = -25.0F;
    private static final float FLAT_ITEM_DEPTH = 0.0625F;
    private static final double CUSTOM_NAME_Y_OFFSET = -0.4D;
    private static final RenderBlocks HELD_BLOCK_RENDERER = new RenderBlocks();
    private static final ResourceLocation ENCHANTED_ITEM_GLINT =
            new ResourceLocation("textures/misc/enchanted_item_glint.png");
    private static final boolean RIFTFLUX_HAS_BEDDIUM =
            hasClass("com.ventooth.beddium.modules.TerrainRendering.CeleritasWorldRenderer");

    private final ModelSootSprite sootModel;
    private int riftflux$currentBeddiumBrightness = -1;

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
        DucklingRenderState.pushEntityRenderContext();
        DucklingRenderState.Snapshot snapshot = DucklingRenderState.capture();
        DucklingRenderState.pushRenderAttribs();
        DucklingRenderState.pushRenderClientAttribs();
        DucklingRenderState.pushRenderMatrices();
        DucklingRenderState.prepareForEntityRender(entity, partialTicks);
        this.riftflux$currentBeddiumBrightness = RIFTFLUX_HAS_BEDDIUM && entity != null
                ? DucklingRenderState.resolveEntityBrightness(entity, partialTicks)
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
            DucklingRenderState.popEntityRenderContext();
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
        if (DucklingRenderState.shouldRenderCustomName(entity)) {
            this.func_147906_a(entity, ((EntityLiving) entity).getCustomNameTag(), x, y + CUSTOM_NAME_Y_OFFSET, z, 64);
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
            try {
                if (blockModel) {
                    this.renderHeldBlockItem(sprite, held, brightness, partialTicks);
                } else if (this.hasCustomEntityItemRenderer(held)) {
                    this.renderStandardHeldItem(sprite, held);
                } else {
                    this.renderFlatHeldItem(sprite, held, brightness, partialTicks);
                }
            } finally {
                RenderHelper.disableStandardItemLighting();
            }
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

    private void renderHeldBlockItem(EntitySootSprite sprite, ItemStack held, int brightness, float partialTicks) {
        if (this.hasCustomEntityItemRenderer(held)) {
            this.renderStandardHeldItem(sprite, held);
            return;
        }

        Block block = Block.getBlockFromItem(held.getItem());
        if (block == null || block == Blocks.air) {
            this.renderStandardHeldItem(sprite, held);
            return;
        }

        if (DucklingRenderState.usesPackedLightmap() && this.shouldRenderLitCubeBlockItem(block)) {
            this.renderLitHeldCubeBlockItem(sprite, block, this.normalizeHeldBlockMetadata(block, held.getItemDamage()), brightness, partialTicks);
            return;
        }

        this.renderStandardHeldItem(sprite, held);
    }

    private boolean hasCustomEntityItemRenderer(ItemStack held) {
        return held != null
                && held.getItem() != null
                && MinecraftForgeClient.getItemRenderer(held, IItemRenderer.ItemRenderType.ENTITY) != null;
    }

    private void renderStandardHeldItem(EntitySootSprite sprite, ItemStack held) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        RenderManager.instance.itemRenderer.renderItem(sprite, held, 0, IItemRenderer.ItemRenderType.ENTITY);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glDepthFunc(GL11.GL_LEQUAL);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private boolean shouldRenderLitCubeBlockItem(Block block) {
        int renderType = block.getRenderType();
        return renderType == 0 || renderType == 16 || renderType == 26 || renderType == 31 || renderType == 39;
    }

    private int normalizeHeldBlockMetadata(Block block, int metadata) {
        if (block == Blocks.dispenser || block == Blocks.dropper || block == Blocks.furnace) {
            return 3;
        }
        if (block.getRenderType() == 16) {
            return 1;
        }
        return metadata;
    }

    private void renderLitHeldCubeBlockItem(EntitySootSprite sprite, Block block, int metadata, int brightness, float partialTicks) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
        float sceneBrightness = this.resolveHeldItemColorBrightness(sprite, partialTicks);

        GL11.glPushMatrix();
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL11.GL_LIGHTING);
        try {
            block.setBlockBoundsForItemRender();
            HELD_BLOCK_RENDERER.setRenderBoundsFromBlock(block);
            GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
            GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
            this.renderLitHeldCubeFaces(block, metadata, brightness, sceneBrightness);
        } finally {
            GL11.glDisable(GL12.GL_RESCALE_NORMAL);
            GL11.glPopMatrix();
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    private void renderLitHeldCubeFaces(Block block, int metadata, int brightness, float sceneBrightness) {
        boolean grass = block == Blocks.grass;
        int color = grass ? 0xFFFFFF : block.getRenderColor(metadata);
        float red = (float) (color >> 16 & 255) / 255.0F * sceneBrightness;
        float green = (float) (color >> 8 & 255) / 255.0F * sceneBrightness;
        float blue = (float) (color & 255) / 255.0F * sceneBrightness;

        this.renderLitHeldBlockFace(block, metadata, brightness, 0, 0.0F, -1.0F, 0.0F, red * 0.5F, green * 0.5F, blue * 0.5F);

        if (grass) {
            int topColor = block.getRenderColor(metadata);
            red = (float) (topColor >> 16 & 255) / 255.0F * sceneBrightness;
            green = (float) (topColor >> 8 & 255) / 255.0F * sceneBrightness;
            blue = (float) (topColor & 255) / 255.0F * sceneBrightness;
        }

        this.renderLitHeldBlockFace(block, metadata, brightness, 1, 0.0F, 1.0F, 0.0F, red, green, blue);

        if (grass) {
            red = sceneBrightness;
            green = sceneBrightness;
            blue = sceneBrightness;
        }

        this.renderLitHeldBlockFace(block, metadata, brightness, 2, 0.0F, 0.0F, -1.0F, red * 0.8F, green * 0.8F, blue * 0.8F);
        this.renderLitHeldBlockFace(block, metadata, brightness, 3, 0.0F, 0.0F, 1.0F, red * 0.8F, green * 0.8F, blue * 0.8F);
        this.renderLitHeldBlockFace(block, metadata, brightness, 4, -1.0F, 0.0F, 0.0F, red * 0.6F, green * 0.6F, blue * 0.6F);
        this.renderLitHeldBlockFace(block, metadata, brightness, 5, 1.0F, 0.0F, 0.0F, red * 0.6F, green * 0.6F, blue * 0.6F);

        if (grass) {
            this.renderLitHeldGrassOverlay(block, metadata, brightness, sceneBrightness);
        }
    }

    private void renderLitHeldGrassOverlay(Block block, int metadata, int brightness, float sceneBrightness) {
        int color = block.getRenderColor(metadata);
        float red = (float) (color >> 16 & 255) / 255.0F * sceneBrightness;
        float green = (float) (color >> 8 & 255) / 255.0F * sceneBrightness;
        float blue = (float) (color & 255) / 255.0F * sceneBrightness;
        IIcon overlay = BlockGrass.getIconSideOverlay();

        this.renderLitHeldBlockFace(block, metadata, brightness, 2, 0.0F, 0.0F, -1.0F, red * 0.8F, green * 0.8F, blue * 0.8F, overlay);
        this.renderLitHeldBlockFace(block, metadata, brightness, 3, 0.0F, 0.0F, 1.0F, red * 0.8F, green * 0.8F, blue * 0.8F, overlay);
        this.renderLitHeldBlockFace(block, metadata, brightness, 4, -1.0F, 0.0F, 0.0F, red * 0.6F, green * 0.6F, blue * 0.6F, overlay);
        this.renderLitHeldBlockFace(block, metadata, brightness, 5, 1.0F, 0.0F, 0.0F, red * 0.6F, green * 0.6F, blue * 0.6F, overlay);
    }

    private void renderLitHeldBlockFace(
            Block block,
            int metadata,
            int brightness,
            int side,
            float normalX,
            float normalY,
            float normalZ,
            float red,
            float green,
            float blue
    ) {
        this.renderLitHeldBlockFace(
                block,
                metadata,
                brightness,
                side,
                normalX,
                normalY,
                normalZ,
                red,
                green,
                blue,
                HELD_BLOCK_RENDERER.getBlockIconFromSideAndMetadata(block, side, metadata)
        );
    }

    private void renderLitHeldBlockFace(
            Block block,
            int metadata,
            int brightness,
            int side,
            float normalX,
            float normalY,
            float normalZ,
            float red,
            float green,
            float blue,
            IIcon icon
    ) {
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.setBrightness(brightness);
        tessellator.setColorOpaque_F(red, green, blue);
        tessellator.setNormal(normalX, normalY, normalZ);
        switch (side) {
            case 0:
                HELD_BLOCK_RENDERER.renderFaceYNeg(block, 0.0D, 0.0D, 0.0D, icon);
                break;
            case 1:
                HELD_BLOCK_RENDERER.renderFaceYPos(block, 0.0D, 0.0D, 0.0D, icon);
                break;
            case 2:
                HELD_BLOCK_RENDERER.renderFaceZNeg(block, 0.0D, 0.0D, 0.0D, icon);
                break;
            case 3:
                HELD_BLOCK_RENDERER.renderFaceZPos(block, 0.0D, 0.0D, 0.0D, icon);
                break;
            case 4:
                HELD_BLOCK_RENDERER.renderFaceXNeg(block, 0.0D, 0.0D, 0.0D, icon);
                break;
            case 5:
                HELD_BLOCK_RENDERER.renderFaceXPos(block, 0.0D, 0.0D, 0.0D, icon);
                break;
            default:
                break;
        }
        tessellator.draw();
    }

    private float resolveHeldItemColorBrightness(EntitySootSprite sprite, float partialTicks) {
        if (sprite.worldObj == null) {
            return sprite.getBrightness(partialTicks);
        }

        int x = MathHelper.floor_double(sprite.posX);
        int y = MathHelper.floor_double(sprite.posY + (double) (sprite.height * 0.5F));
        int z = MathHelper.floor_double(sprite.posZ);
        return sprite.worldObj.getLightBrightness(x, y, z);
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

    private void renderFlatHeldItem(EntitySootSprite sprite, ItemStack held, int brightness, float partialTicks) {
        TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();
        textureManager.bindTexture(textureManager.getResourceLocation(held.getItemSpriteNumber()));
        TextureUtil.func_152777_a(false, false, 1.0F);
        Tessellator tessellator = Tessellator.instance;
        float sceneBrightness = DucklingRenderState.usesPackedLightmap()
                ? this.resolveHeldItemColorBrightness(sprite, partialTicks)
                : 1.0F;
        int passes = held.getItem().requiresMultipleRenderPasses()
                ? held.getItem().getRenderPasses(held.getItemDamage())
                : 1;

        GL11.glPushMatrix();
        try {
            GL11.glTranslatef(-0.5F, -0.5F, 0.0F);
            for (int pass = 0; pass < passes; pass++) {
                IIcon icon = this.resolveFlatItemIcon(sprite, held, pass);
                if (icon == null) {
                    continue;
                }

                int color = held.getItem().getColorFromItemStack(held, pass);
                float red = (float) (color >> 16 & 255) / 255.0F * sceneBrightness;
                float green = (float) (color >> 8 & 255) / 255.0F * sceneBrightness;
                float blue = (float) (color & 255) / 255.0F * sceneBrightness;
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
        } finally {
            GL11.glPopMatrix();
            TextureUtil.func_147945_b();
            GL11.glMatrixMode(GL11.GL_MODELVIEW);
            GL11.glDepthFunc(GL11.GL_LEQUAL);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        }
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
        int count = 0;
        for (GeoBone current = bone; current != null; current = current.parent) {
            count++;
        }
        GeoBone[] path = new GeoBone[count];
        for (GeoBone current = bone; current != null; current = current.parent) {
            path[--count] = current;
        }
        return path;
    }

    private static boolean hasClass(String className) {
        try {
            ClassLoader loader = RenderSootSprite.class.getClassLoader();
            return loader.getResource(className.replace('.', '/') + ".class") != null;
        } catch (Throwable ignored) {
            return false;
        }
    }
}
