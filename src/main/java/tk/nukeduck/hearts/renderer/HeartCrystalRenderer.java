/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.ItemRenderer
 *  net.minecraft.client.renderer.Tessellator
 *  net.minecraft.client.renderer.texture.TextureManager
 *  net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
 *  net.minecraft.item.ItemStack
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.IIcon
 *  net.minecraft.util.ResourceLocation
 *  org.lwjgl.opengl.GL11
 *  org.lwjgl.opengl.GL12
 */
package tk.nukeduck.hearts.renderer;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import net.minecraft.world.World;
import tk.nukeduck.hearts.HeartCrystal;
import tk.nukeduck.hearts.block.TileEntityHeartCrystal;
import tk.nukeduck.hearts.registry.HeartsBlocks;

public class HeartCrystalRenderer
extends TileEntitySpecialRenderer {
    static final float TOOLTIP_PREVIEW_CRYSTAL_SCALE = 1.35f;
    static final float TOOLTIP_PREVIEW_LANTERN_SCALE = 1.4f;
    static final float TOOLTIP_PREVIEW_LANTERN_Y_OFFSET = 0.5f;
    private static final String WDMLA_ROOT = "com.gtnewhorizons.wdmla.";
    private static final String WAILA_ROOT = "mcp.mobius.waila.";
    private static final float FRAME_ITEM_SCALE = 0.5128205f;
    private static final float FRAME_ITEM_Y_OFFSET = -0.05f;
    private static final float FRAME_ITEM_HALF_WIDTH = 0.5f;
    private static final float FRAME_ITEM_BASE_Y = 0.25f;
    private static final float FRAME_ITEM_DEPTH = 0.0625f;
    private static final float FRAME_ITEM_SPACING = 0.021875f;
    private static final float FRAME_ITEM_RENDER_OFFSET_Z = (FRAME_ITEM_DEPTH + FRAME_ITEM_SPACING) * 0.5f;
    private static final float FRAME_ITEM_BOB_Y = 0.1f;
    private static final float MODERN_CRYSTAL_Y_OFFSET = 0.1125f;
    private static final float MODERN_CRYSTAL_Z_OFFSET = 0.03125f;
    private static final float MODERN_CRYSTAL_XY_SCALE = 1.75f;
    private static final float MODERN_CRYSTAL_Z_SCALE = 7.0f;
    private static final float LANTERN_MODERN_CRYSTAL_Y_OFFSET = -1.41875f;
    private static final float LANTERN_MODERN_CRYSTAL_Z_OFFSET = 0.0f;
    private static final float LANTERN_MODERN_CRYSTAL_XY_SCALE = 0.7f;
    private static final float LANTERN_MODERN_CRYSTAL_Z_SCALE = 5.0f;
    protected static final ResourceLocation oldTexture = new ResourceLocation("hearts", "textures/models/heart_crystal.png");
    private final EntityItem item;
    private final ItemStack crystalStack;
    private final ModelHeart model;

    public HeartCrystalRenderer() {
        this.item = new EntityItem((World)Minecraft.getMinecraft().theWorld, 0.0, 0.0, 0.0, new ItemStack((Block)HeartsBlocks.crystal));
        this.item.hoverStart = 0.0f;
        this.crystalStack = new ItemStack((Block)HeartsBlocks.crystal);
        this.model = new ModelHeart();
    }

    public void renderTileEntityAt(TileEntity tileentity, double d0, double d1, double d2, float f) {
        if (!(tileentity instanceof TileEntityHeartCrystal)) {
            return;
        }
        if (HeartCrystal.config.getOldModel()) {
            this.renderOldCrystal(tileentity, d0, d1, d2, f);
            return;
        }
        this.renderModernCrystal(tileentity, d0, d1, d2, f);
    }

    protected void renderModernCrystal(TileEntity tileentity, double x, double y, double z, float partialTicks) {
        GL11.glPushMatrix();
        GL11.glTranslated((double)(x + 0.5), (double)(y + (double)MODERN_CRYSTAL_Y_OFFSET), (double)(z + 0.5));
        if (isTooltipPreviewRender()) {
            GL11.glScalef((float)TOOLTIP_PREVIEW_CRYSTAL_SCALE, (float)TOOLTIP_PREVIEW_CRYSTAL_SCALE, (float)TOOLTIP_PREVIEW_CRYSTAL_SCALE);
        }
        GL11.glRotatef((float)this.getRotation(tileentity, partialTicks), (float)0.0f, (float)1.0f, (float)0.0f);
        GL11.glTranslatef((float)0.0f, (float)0.0f, (float)MODERN_CRYSTAL_Z_OFFSET);
        GL11.glScalef((float)MODERN_CRYSTAL_XY_SCALE, (float)MODERN_CRYSTAL_XY_SCALE, (float)MODERN_CRYSTAL_Z_SCALE);
        this.renderModernCrystalGeometry(this.crystalStack, FRAME_ITEM_DEPTH * 0.5f);
        GL11.glPopMatrix();
    }

    protected void renderModernCrystalInLantern(TileEntity tileentity, float partialTicks) {
        GL11.glPushMatrix();
        GL11.glTranslatef((float)0.0f, (float)LANTERN_MODERN_CRYSTAL_Y_OFFSET, (float)LANTERN_MODERN_CRYSTAL_Z_OFFSET);
        GL11.glRotatef((float)this.getRotation(tileentity, partialTicks), (float)0.0f, (float)1.0f, (float)0.0f);
        GL11.glScalef((float)LANTERN_MODERN_CRYSTAL_XY_SCALE, (float)LANTERN_MODERN_CRYSTAL_XY_SCALE, (float)LANTERN_MODERN_CRYSTAL_Z_SCALE);
        this.renderModernCrystalGeometry(this.crystalStack, FRAME_ITEM_DEPTH * 0.5f);
        GL11.glPopMatrix();
    }

    protected void renderOldCrystalInLantern(TileEntity tileentity, float partialTicks) {
        GL11.glPushMatrix();
        GL11.glEnable((int)3042);
        GL11.glBlendFunc((int)770, (int)771);
        GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)0.5f);
        GL11.glTranslated((double)0.0, (double)-0.85, (double)0.0);
        GL11.glScaled((double)0.4, (double)0.4, (double)0.4);
        GL11.glRotatef((float)180.0f, (float)0.0f, (float)0.0f, (float)1.0f);
        GL11.glRotatef((float)this.getRotation(tileentity, partialTicks), (float)0.0f, (float)1.0f, (float)0.0f);
        this.bindTexture(oldTexture);
        this.model.render(null, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0625f);
        GL11.glDisable((int)3042);
        GL11.glPopMatrix();
    }

    public void renderCrystalItemModel(ItemStack stack, float rotation) {
        if (HeartCrystal.config.getOldModel()) {
            this.renderOldCrystalItem(rotation);
            return;
        }
        GL11.glPushMatrix();
        GL11.glRotatef((float)rotation, (float)0.0f, (float)1.0f, (float)0.0f);
        GL11.glTranslatef((float)0.0f, (float)0.0f, (float)MODERN_CRYSTAL_Z_OFFSET);
        GL11.glScalef((float)MODERN_CRYSTAL_XY_SCALE, (float)MODERN_CRYSTAL_XY_SCALE, (float)MODERN_CRYSTAL_Z_SCALE);
        this.renderModernCrystalItem(stack);
        GL11.glPopMatrix();
    }

    public void renderLanternCrystalItem(float rotation) {
        if (HeartCrystal.config.getOldModel()) {
            this.renderOldLanternCrystalItem(rotation);
            return;
        }
        GL11.glPushMatrix();
        GL11.glTranslatef((float)0.0f, (float)LANTERN_MODERN_CRYSTAL_Y_OFFSET, (float)LANTERN_MODERN_CRYSTAL_Z_OFFSET);
        GL11.glRotatef((float)rotation, (float)0.0f, (float)1.0f, (float)0.0f);
        GL11.glScalef((float)LANTERN_MODERN_CRYSTAL_XY_SCALE, (float)LANTERN_MODERN_CRYSTAL_XY_SCALE, (float)LANTERN_MODERN_CRYSTAL_Z_SCALE);
        this.renderModernCrystalGeometry(this.crystalStack, FRAME_ITEM_DEPTH * 0.5f);
        GL11.glPopMatrix();
    }

    public float getItemRotation() {
        return (float)(Minecraft.getSystemTime() / 20L % 360L);
    }

    private void renderOldCrystal(TileEntity tileentity, double x, double y, double z, float partialTicks) {
        GL11.glPushMatrix();
        GL11.glEnable((int)3042);
        GL11.glBlendFunc((int)770, (int)771);
        GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)0.5f);
        GL11.glTranslatef((float)((float)x + 0.5f), (float)((float)y + 1.5f), (float)((float)z + 0.5f));
        if (isTooltipPreviewRender()) {
            GL11.glScalef((float)TOOLTIP_PREVIEW_CRYSTAL_SCALE, (float)TOOLTIP_PREVIEW_CRYSTAL_SCALE, (float)TOOLTIP_PREVIEW_CRYSTAL_SCALE);
        }
        GL11.glRotatef((float)180.0f, (float)0.0f, (float)0.0f, (float)1.0f);
        GL11.glRotatef((float)this.getRotation(tileentity, partialTicks), (float)0.0f, (float)1.0f, (float)0.0f);
        this.bindTexture(oldTexture);
        this.model.render(null, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0625f);
        GL11.glDisable((int)3042);
        GL11.glPopMatrix();
    }

    private void renderOldCrystalItem(float rotation) {
        GL11.glPushMatrix();
        GL11.glEnable((int)3042);
        GL11.glBlendFunc((int)770, (int)771);
        GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)0.5f);
        GL11.glTranslatef((float)0.0f, (float)0.6f, (float)0.0f);
        GL11.glRotatef((float)180.0f, (float)0.0f, (float)0.0f, (float)1.0f);
        GL11.glRotatef((float)rotation, (float)0.0f, (float)1.0f, (float)0.0f);
        this.bindTexture(oldTexture);
        this.model.render(null, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0625f);
        GL11.glDisable((int)3042);
        GL11.glPopMatrix();
    }

    private void renderOldLanternCrystalItem(float rotation) {
        GL11.glPushMatrix();
        GL11.glEnable((int)3042);
        GL11.glBlendFunc((int)770, (int)771);
        GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)0.5f);
        GL11.glTranslated((double)0.0, (double)-0.85, (double)0.0);
        GL11.glScaled((double)0.4, (double)0.4, (double)0.4);
        GL11.glRotatef((float)180.0f, (float)0.0f, (float)0.0f, (float)1.0f);
        GL11.glRotatef((float)rotation, (float)0.0f, (float)1.0f, (float)0.0f);
        this.bindTexture(oldTexture);
        this.model.render(null, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0625f);
        GL11.glDisable((int)3042);
        GL11.glPopMatrix();
    }

    private void renderModernCrystalItem(ItemStack stack) {
        ItemStack renderStack = this.getRenderStack(stack).copy();
        renderStack.stackSize = 1;
        this.item.setEntityItemStack(renderStack);
        boolean disableCull = !Minecraft.getMinecraft().gameSettings.fancyGraphics;
        if (disableCull) {
            GL11.glDisable((int)2884);
        }
        boolean previousRenderInFrame = RenderItem.renderInFrame;
        RenderItem.renderInFrame = true;
        RenderManager.instance.renderEntityWithPosYaw((Entity)this.item, 0.0, 0.0, 0.0, 0.0f, 0.0f);
        RenderItem.renderInFrame = previousRenderInFrame;
        if (disableCull) {
            GL11.glEnable((int)2884);
        }
    }

    private void renderModernCrystalGeometry(ItemStack stack, float zOffset) {
        ItemStack renderStack = this.getRenderStack(stack);
        if (renderStack.getItem() == null) {
            return;
        }
        IIcon icon = renderStack.getItem().getIcon(renderStack, 0);
        if (icon == null) {
            return;
        }
        TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();
        textureManager.bindTexture(textureManager.getResourceLocation(renderStack.getItemSpriteNumber()));
        int color = renderStack.getItem().getColorFromItemStack(renderStack, 0);
        float red = (float)(color >> 16 & 255) / 255.0f;
        float green = (float)(color >> 8 & 255) / 255.0f;
        float blue = (float)(color & 255) / 255.0f;
        GL11.glPushMatrix();
        GL11.glEnable((int)GL12.GL_RESCALE_NORMAL);
        GL11.glColor4f((float)red, (float)green, (float)blue, (float)1.0f);
        GL11.glTranslatef((float)0.0f, (float)FRAME_ITEM_BOB_Y, (float)0.0f);
        GL11.glScalef((float)FRAME_ITEM_SCALE, (float)FRAME_ITEM_SCALE, (float)FRAME_ITEM_SCALE);
        GL11.glTranslatef((float)0.0f, (float)FRAME_ITEM_Y_OFFSET, (float)0.0f);
        GL11.glRotatef((float)180.0f, (float)0.0f, (float)1.0f, (float)0.0f);
        GL11.glTranslatef((float)(0.0f - FRAME_ITEM_HALF_WIDTH), (float)(0.0f - FRAME_ITEM_BASE_Y), (float)zOffset);
        ItemRenderer.renderItemIn2D((Tessellator)Tessellator.instance, (float)icon.getMaxU(), (float)icon.getMinV(), (float)icon.getMinU(), (float)icon.getMaxV(), (int)icon.getIconWidth(), (int)icon.getIconHeight(), (float)FRAME_ITEM_DEPTH);
        GL11.glDisable((int)GL12.GL_RESCALE_NORMAL);
        GL11.glPopMatrix();
    }

    private ItemStack getRenderStack(ItemStack stack) {
        return stack != null ? stack : this.crystalStack;
    }

    static boolean isTooltipPreviewRender() {
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

    protected float getRotation(TileEntity tileentity, float partialTicks) {
        long ticks = 0L;
        if (Minecraft.getMinecraft().theWorld != null) {
            ticks = Minecraft.getMinecraft().theWorld.getTotalWorldTime();
        } else if (tileentity.getWorldObj() != null) {
            ticks = tileentity.getWorldObj().getTotalWorldTime();
        }
        long seed = (long)tileentity.xCoord * 31L + (long)tileentity.yCoord * 17L + (long)tileentity.zCoord * 13L;
        return (float)((ticks * 2L + seed) % 360L) + partialTicks * 2.0f;
    }
}
