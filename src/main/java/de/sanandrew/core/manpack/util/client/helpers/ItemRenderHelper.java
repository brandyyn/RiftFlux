/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.client.ForgeHooksClient
 *  net.minecraftforge.client.IItemRenderer
 *  net.minecraftforge.client.IItemRenderer$ItemRenderType
 *  net.minecraftforge.client.MinecraftForgeClient
 *  org.lwjgl.opengl.GL11
 */
package de.sanandrew.core.manpack.util.client.helpers;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemCloth;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;
import org.lwjgl.opengl.GL11;

@SideOnly(value=Side.CLIENT)
public final class ItemRenderHelper {
    private static final ResourceLocation GLINT_PNG = new ResourceLocation("textures/misc/enchanted_item_glint.png");
    protected static RenderItem itemRender = new RenderItem();
    protected static RenderBlocks renderBlocksRi = new RenderBlocks();

    public static void renderItemInGui(Minecraft mc, ItemStack stack, int x, int y) {
        if (stack != null) {
            GL11.glTranslatef((float)0.0f, (float)0.0f, (float)32.0f);
            ItemRenderHelper.itemRender.zLevel = 200.0f;
            itemRender.renderItemIntoGUI(null, mc.getTextureManager(), stack, x, y);
            ItemRenderHelper.itemRender.zLevel = 0.0f;
        }
    }

    public static void renderIconIn3D(IIcon icon, boolean isBlock, boolean hasAlpha, int color) {
        GL11.glPushMatrix();
        GL11.glEnable((int)32826);
        if (isBlock) {
            Minecraft.getMinecraft().renderEngine.bindTexture(Minecraft.getMinecraft().renderEngine.getResourceLocation(0));
            if (hasAlpha) {
                GL11.glAlphaFunc((int)516, (float)0.1f);
                GL11.glEnable((int)3042);
                OpenGlHelper.glBlendFunc(770, 771, 1, 0);
            }
            GL11.glTranslatef((float)0.5f, (float)0.5f, (float)0.0f);
            GL11.glScalef((float)0.5f, (float)0.5f, (float)0.5f);
            GL11.glPushMatrix();
            Tessellator tessellator = Tessellator.instance;
            float red = (float)(color >> 16 & 0xFF) / 255.0f;
            float green = (float)(color >> 8 & 0xFF) / 255.0f;
            float blue = (float)(color & 0xFF) / 255.0f;
            GL11.glColor4f((float)red, (float)green, (float)blue, (float)1.0f);
            Blocks.stone.setBlockBoundsForItemRender();
            renderBlocksRi.setRenderBoundsFromBlock(Blocks.stone);
            GL11.glRotatef((float)90.0f, (float)0.0f, (float)1.0f, (float)0.0f);
            GL11.glTranslatef((float)-0.5f, (float)-0.5f, (float)-0.5f);
            tessellator.startDrawingQuads();
            tessellator.setNormal(0.0f, -1.0f, 0.0f);
            renderBlocksRi.renderFaceYNeg(Blocks.stone, 0.0, 0.0, 0.0, icon);
            tessellator.draw();
            tessellator.startDrawingQuads();
            tessellator.setNormal(0.0f, 1.0f, 0.0f);
            renderBlocksRi.renderFaceYPos(Blocks.stone, 0.0, 0.0, 0.0, icon);
            tessellator.draw();
            tessellator.startDrawingQuads();
            tessellator.setNormal(0.0f, 0.0f, -1.0f);
            renderBlocksRi.renderFaceZNeg(Blocks.stone, 0.0, 0.0, 0.0, icon);
            tessellator.draw();
            tessellator.startDrawingQuads();
            tessellator.setNormal(0.0f, 0.0f, 1.0f);
            renderBlocksRi.renderFaceZPos(Blocks.stone, 0.0, 0.0, 0.0, icon);
            tessellator.draw();
            tessellator.startDrawingQuads();
            tessellator.setNormal(-1.0f, 0.0f, 0.0f);
            renderBlocksRi.renderFaceXNeg(Blocks.stone, 0.0, 0.0, 0.0, icon);
            tessellator.draw();
            tessellator.startDrawingQuads();
            tessellator.setNormal(1.0f, 0.0f, 0.0f);
            renderBlocksRi.renderFaceXPos(Blocks.stone, 0.0, 0.0, 0.0, icon);
            tessellator.draw();
            GL11.glPopMatrix();
            if (hasAlpha) {
                GL11.glDisable((int)3042);
            }
        } else {
            if (hasAlpha) {
                GL11.glAlphaFunc((int)516, (float)0.1f);
                GL11.glEnable((int)3042);
                OpenGlHelper.glBlendFunc(770, 771, 1, 0);
            }
            float red = (float)(color >> 16 & 0xFF) / 255.0f;
            float green = (float)(color >> 8 & 0xFF) / 255.0f;
            float blue = (float)(color & 0xFF) / 255.0f;
            GL11.glColor4f((float)red, (float)green, (float)blue, (float)1.0f);
            ItemRenderHelper.renderItemIn3D(icon, false, 1);
            if (hasAlpha) {
                GL11.glDisable((int)3042);
            }
        }
        GL11.glDisable((int)32826);
        GL11.glPopMatrix();
        TextureUtil.func_147945_b();
    }

    public static void renderItemIn3D(ItemStack stack) {
        if (stack.getItem() != null) {
            GL11.glPushMatrix();
            GL11.glEnable((int)32826);
            IItemRenderer customRenderer = MinecraftForgeClient.getItemRenderer((ItemStack)stack, (IItemRenderer.ItemRenderType)IItemRenderer.ItemRenderType.EQUIPPED);
            if (customRenderer != null) {
                Minecraft.getMinecraft().renderEngine.bindTexture(Minecraft.getMinecraft().renderEngine.getResourceLocation(stack.getItemSpriteNumber()));
                GL11.glPushMatrix();
                GL11.glTranslatef((float)0.5f, (float)0.5f, (float)0.0f);
                float blockScale = 0.5f;
                GL11.glScalef((float)blockScale, (float)blockScale, (float)blockScale);
                ForgeHooksClient.renderEquippedItem((IItemRenderer.ItemRenderType)IItemRenderer.ItemRenderType.EQUIPPED, (IItemRenderer)customRenderer, (RenderBlocks)renderBlocksRi, (EntityLivingBase)Minecraft.getMinecraft().thePlayer, (ItemStack)stack);
                GL11.glPopMatrix();
            } else if (stack.getItemSpriteNumber() == 0 && stack.getItem() instanceof ItemBlock && RenderBlocks.renderItemIn3d(Block.getBlockFromItem(stack.getItem()).getRenderType())) {
                Minecraft.getMinecraft().renderEngine.bindTexture(Minecraft.getMinecraft().renderEngine.getResourceLocation(stack.getItemSpriteNumber()));
                Block block = Block.getBlockFromItem(stack.getItem());
                float blockScale = 0.5f;
                int renderType = block.getRenderType();
                if (renderType == 1 || renderType == 19 || renderType == 12 || renderType == 2) {
                    blockScale = 1.0f;
                }
                if (block.getRenderBlockPass() > 0) {
                    GL11.glAlphaFunc((int)516, (float)0.1f);
                    GL11.glEnable((int)3042);
                    OpenGlHelper.glBlendFunc(770, 771, 1, 0);
                }
                GL11.glTranslatef((float)0.5f, (float)0.5f, (float)0.0f);
                GL11.glScalef((float)blockScale, (float)blockScale, (float)blockScale);
                GL11.glPushMatrix();
                renderBlocksRi.renderBlockAsItem(block, stack.getItemDamage(), 1.0f);
                GL11.glPopMatrix();
                if (block.getRenderBlockPass() > 0) {
                    GL11.glDisable((int)3042);
                }
            } else if (stack.getItem().requiresMultipleRenderPasses()) {
                for (int j = 0; j < stack.getItem().getRenderPasses(stack.getItemDamage()); ++j) {
                    IIcon icon = stack.getItem().getIcon(stack, j);
                    int color = stack.getItem().getColorFromItemStack(stack, j);
                    float red = (float)(color >> 16 & 0xFF) / 255.0f;
                    float green = (float)(color >> 8 & 0xFF) / 255.0f;
                    float blue = (float)(color & 0xFF) / 255.0f;
                    GL11.glColor4f((float)red, (float)green, (float)blue, (float)1.0f);
                    ItemRenderHelper.renderItemIn3D(icon, stack.hasEffect(j), 1);
                }
            } else {
                IIcon icon = stack.getItem().getIcon(stack, 0);
                if (stack.getItem() instanceof ItemCloth) {
                    GL11.glAlphaFunc((int)516, (float)0.1f);
                    GL11.glEnable((int)3042);
                    OpenGlHelper.glBlendFunc(770, 771, 1, 0);
                }
                int color = stack.getItem().getColorFromItemStack(stack, 0);
                float red = (float)(color >> 16 & 0xFF) / 255.0f;
                float green = (float)(color >> 8 & 0xFF) / 255.0f;
                float blue = (float)(color & 0xFF) / 255.0f;
                GL11.glColor4f((float)red, (float)green, (float)blue, (float)1.0f);
                ItemRenderHelper.renderItemIn3D(icon, stack.hasEffect(0), 1);
                if (stack.getItem() instanceof ItemCloth) {
                    GL11.glDisable((int)3042);
                }
            }
            GL11.glDisable((int)32826);
            GL11.glPopMatrix();
            TextureUtil.func_147945_b();
        }
    }

    private static void renderItemIn3D(IIcon icon, boolean withEffect, int spriteIndex) {
        GL11.glPushMatrix();
        if (icon == null) {
            GL11.glPopMatrix();
            return;
        }
        float minU = icon.getMinU();
        float maxU = icon.getMaxU();
        float minV = icon.getMinV();
        float maxV = icon.getMaxV();
        Tessellator tessellator = Tessellator.instance;
        Minecraft.getMinecraft().renderEngine.bindTexture(Minecraft.getMinecraft().renderEngine.getResourceLocation(spriteIndex));
        GL11.glEnable((int)32826);
        ItemRenderHelper.renderItemIn2D(tessellator, maxU, minV, minU, maxV, icon.getIconWidth(), icon.getIconHeight(), 0.0625f, false);
        if (withEffect) {
            float baseClr = 0.76f;
            float glintScale = 0.125f;
            float glintTransX = (float)(Minecraft.getSystemTime() % 3000L) / 3000.0f * 8.0f;
            GL11.glDepthFunc((int)514);
            GL11.glDisable((int)2896);
            Minecraft.getMinecraft().renderEngine.bindTexture(GLINT_PNG);
            GL11.glEnable((int)3042);
            GL11.glBlendFunc((int)768, (int)1);
            GL11.glColor4f((float)(0.5f * baseClr), (float)(0.25f * baseClr), (float)(0.8f * baseClr), (float)1.0f);
            GL11.glMatrixMode((int)5890);
            GL11.glPushMatrix();
            GL11.glScalef((float)glintScale, (float)glintScale, (float)glintScale);
            GL11.glTranslatef((float)glintTransX, (float)0.0f, (float)0.0f);
            GL11.glRotatef((float)-50.0f, (float)0.0f, (float)0.0f, (float)1.0f);
            ItemRenderHelper.renderItemIn2D(tessellator, 0.0f, 0.0f, 1.0f, 1.0f, 256, 256, 0.0625f, false);
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            GL11.glScalef((float)glintScale, (float)glintScale, (float)glintScale);
            glintTransX = (float)(Minecraft.getSystemTime() % 4873L) / 4873.0f * 8.0f;
            GL11.glTranslatef((float)(-glintTransX), (float)0.0f, (float)0.0f);
            GL11.glRotatef((float)10.0f, (float)0.0f, (float)0.0f, (float)1.0f);
            ItemRenderHelper.renderItemIn2D(tessellator, 0.0f, 0.0f, 1.0f, 1.0f, 256, 256, 0.0625f, false);
            GL11.glPopMatrix();
            GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
            GL11.glMatrixMode((int)5888);
            GL11.glDisable((int)3042);
            GL11.glEnable((int)2896);
            GL11.glDepthFunc((int)515);
        }
        GL11.glDisable((int)32826);
        GL11.glPopMatrix();
    }

    public static void renderIcon(IIcon icon, int spriteIndex, boolean hasEffect, boolean isGlowing) {
        GL11.glPushMatrix();
        if (icon == null) {
            GL11.glPopMatrix();
            return;
        }
        float minU = icon.getMinU();
        float maxU = icon.getMaxU();
        float minV = icon.getMinV();
        float maxV = icon.getMaxV();
        float transX = 0.0f;
        float transY = 0.3f;
        float scale = 1.5f;
        Tessellator tessellator = Tessellator.instance;
        Minecraft.getMinecraft().renderEngine.bindTexture(Minecraft.getMinecraft().renderEngine.getResourceLocation(spriteIndex));
        GL11.glEnable((int)32826);
        GL11.glTranslatef((float)(-transX), (float)(-transY), (float)0.0f);
        GL11.glScalef((float)scale, (float)scale, (float)scale);
        GL11.glRotatef((float)50.0f, (float)0.0f, (float)1.0f, (float)0.0f);
        GL11.glRotatef((float)335.0f, (float)0.0f, (float)0.0f, (float)1.0f);
        GL11.glTranslatef((float)-0.9375f, (float)-0.0625f, (float)0.0f);
        ItemRenderHelper.renderItemIn2D(tessellator, maxU, minV, minU, maxV, icon.getIconWidth(), icon.getIconHeight(), 0.0625f, isGlowing);
        if (hasEffect) {
            float baseClr = 0.76f;
            float glintScale = 0.125f;
            float glintTransX = (float)(Minecraft.getSystemTime() % 3000L) / 3000.0f * 8.0f;
            GL11.glDepthFunc((int)514);
            GL11.glDisable((int)2896);
            Minecraft.getMinecraft().renderEngine.bindTexture(GLINT_PNG);
            GL11.glEnable((int)3042);
            GL11.glBlendFunc((int)768, (int)1);
            GL11.glColor4f((float)(0.5f * baseClr), (float)(0.25f * baseClr), (float)(0.8f * baseClr), (float)1.0f);
            GL11.glMatrixMode((int)5890);
            GL11.glPushMatrix();
            GL11.glScalef((float)glintScale, (float)glintScale, (float)glintScale);
            GL11.glTranslatef((float)glintTransX, (float)0.0f, (float)0.0f);
            GL11.glRotatef((float)-50.0f, (float)0.0f, (float)0.0f, (float)1.0f);
            ItemRenderHelper.renderItemIn2D(tessellator, 0.0f, 0.0f, 1.0f, 1.0f, 256, 256, 0.0625f, false);
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            GL11.glScalef((float)glintScale, (float)glintScale, (float)glintScale);
            glintTransX = (float)(Minecraft.getSystemTime() % 4873L) / 4873.0f * 8.0f;
            GL11.glTranslatef((float)(-glintTransX), (float)0.0f, (float)0.0f);
            GL11.glRotatef((float)10.0f, (float)0.0f, (float)0.0f, (float)1.0f);
            ItemRenderHelper.renderItemIn2D(tessellator, 0.0f, 0.0f, 1.0f, 1.0f, 256, 256, 0.0625f, false);
            GL11.glPopMatrix();
            GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
            GL11.glMatrixMode((int)5888);
            GL11.glDisable((int)3042);
            GL11.glEnable((int)2896);
            GL11.glDepthFunc((int)515);
        }
        GL11.glDisable((int)32826);
        GL11.glPopMatrix();
    }

    private static void renderItemIn2D(Tessellator tess, float minU, float minV, float maxU, float maxV, int scaleX, int scaleY, float negZLevel, boolean isGlowing) {
        if (isGlowing) {
            GL11.glDisable((int)2896);
            GL11.glDisable((int)16384);
            GL11.glDisable((int)16385);
            GL11.glDisable((int)2903);
            float prevLGTX = OpenGlHelper.lastBrightnessX;
            float prevLGTY = OpenGlHelper.lastBrightnessY;
            int bright = 240;
            int brightX = bright % 65536;
            int brightY = bright / 65536;
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)brightX / 1.0f, (float)brightY / 1.0f);
            ItemRenderer.renderItemIn2D(tess, minU, minV, maxU, maxV, scaleX, scaleY, negZLevel);
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, prevLGTX, prevLGTY);
            GL11.glEnable((int)2896);
            GL11.glEnable((int)16384);
            GL11.glEnable((int)16385);
            GL11.glEnable((int)2903);
        } else {
            ItemRenderer.renderItemIn2D(tess, minU, minV, maxU, maxV, scaleX, scaleY, negZLevel);
        }
    }

    public static IIcon getItemIcon(ItemStack stack, int layer) {
        return stack.getItem().requiresMultipleRenderPasses() ? stack.getItem().getIcon(stack, layer) : stack.getIconIndex();
    }
}

