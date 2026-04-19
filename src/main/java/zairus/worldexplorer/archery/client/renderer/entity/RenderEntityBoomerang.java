/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.block.Block
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.ItemRenderer
 *  net.minecraft.client.renderer.OpenGlHelper
 *  net.minecraft.client.renderer.RenderBlocks
 *  net.minecraft.client.renderer.Tessellator
 *  net.minecraft.client.renderer.entity.Render
 *  net.minecraft.client.renderer.entity.RenderItem
 *  net.minecraft.client.renderer.texture.TextureManager
 *  net.minecraft.client.renderer.texture.TextureMap
 *  net.minecraft.client.renderer.texture.TextureUtil
 *  net.minecraft.entity.Entity
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemBlock
 *  net.minecraft.item.ItemCloth
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.IIcon
 *  net.minecraft.util.ResourceLocation
 *  net.minecraftforge.client.IItemRenderer
 *  net.minecraftforge.client.IItemRenderer$ItemRenderType
 *  net.minecraftforge.client.IItemRenderer$ItemRendererHelper
 *  net.minecraftforge.client.MinecraftForgeClient
 *  org.lwjgl.opengl.GL11
 */
package zairus.worldexplorer.archery.client.renderer.entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemCloth;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;
import org.lwjgl.opengl.GL11;
import zairus.worldexplorer.archery.entity.EntityBoomerang;
import zairus.worldexplorer.archery.items.WEArcheryItems;

@SideOnly(value=Side.CLIENT)
public class RenderEntityBoomerang
extends Render {
    private static final ResourceLocation boomerangTextures = new ResourceLocation("worldexplorer", "textures/entity/entity_boomerang.png");
    private RenderBlocks renderBlocksRi = new RenderBlocks();
    private Random random = new Random();
    public boolean renderWithColor = true;
    public static boolean renderInFrame = false;
    private float boomerangRotate = 1.0f;

    public void doRender(EntityBoomerang boomerang, double p_76986_2_, double p_76986_4_, double p_76986_6_, float p_76986_8_, float p_76986_9_) {
        ItemStack itemstack;
        ItemStack itemStack = itemstack = boomerang.getThrownBoomerang() == null ? new ItemStack((Item)WEArcheryItems.boomerang) : boomerang.getThrownBoomerang();
        if (itemstack.getItem() != null) {
            float f4;
            this.bindEntityTexture(boomerang);
            TextureUtil.func_152777_a((boolean)false, (boolean)false, (float)1.0f);
            this.random.setSeed(187L);
            GL11.glPushMatrix();
            float f2 = 0.0f;
            float f3 = ((0.0f + p_76986_9_) / 20.0f + 0.0f) * 57.295776f;
            int b0 = 1;
            GL11.glTranslatef((float)((float)p_76986_2_), (float)((float)p_76986_4_ + f2), (float)((float)p_76986_6_));
            GL11.glEnable((int)32826);
            GL11.glRotatef((float)90.0f, (float)1.0f, (float)0.0f, (float)0.0f);
            this.boomerangRotate -= 20.0f;
            GL11.glRotatef((float)this.boomerangRotate, (float)0.0f, (float)0.0f, (float)1.0f);
            if (!RenderEntityBoomerang.renderEBoomerang(boomerang, itemstack, f2, f3, this.random, this.renderManager.renderEngine, this.field_147909_c, b0)) {
                float f7;
                float f6;
                int k;
                if (itemstack.getItemSpriteNumber() == 0 && itemstack.getItem() instanceof ItemBlock && RenderBlocks.renderItemIn3d((int)Block.getBlockFromItem((Item)itemstack.getItem()).getRenderType())) {
                    Block block = Block.getBlockFromItem((Item)itemstack.getItem());
                    GL11.glRotatef((float)f3, (float)0.0f, (float)1.0f, (float)0.0f);
                    if (renderInFrame) {
                        GL11.glScalef((float)1.25f, (float)1.25f, (float)1.25f);
                        GL11.glTranslatef((float)0.0f, (float)0.05f, (float)0.0f);
                        GL11.glRotatef((float)-90.0f, (float)0.0f, (float)1.0f, (float)0.0f);
                    }
                    float f9 = 0.25f;
                    k = block.getRenderType();
                    if (k == 1 || k == 19 || k == 12 || k == 2) {
                        f9 = 0.5f;
                    }
                    if (block.getRenderBlockPass() > 0) {
                        GL11.glAlphaFunc((int)516, (float)0.1f);
                        GL11.glEnable((int)3042);
                        OpenGlHelper.glBlendFunc((int)770, (int)771, (int)1, (int)0);
                    }
                    GL11.glScalef((float)f9, (float)f9, (float)f9);
                    for (int l = 0; l < b0; ++l) {
                        GL11.glPushMatrix();
                        if (l > 0) {
                            f6 = (this.random.nextFloat() * 2.0f - 1.0f) * 0.2f / f9;
                            f7 = (this.random.nextFloat() * 2.0f - 1.0f) * 0.2f / f9;
                            float f8 = (this.random.nextFloat() * 2.0f - 1.0f) * 0.2f / f9;
                            GL11.glTranslatef((float)f6, (float)f7, (float)f8);
                        }
                        this.renderBlocksRi.renderBlockAsItem(block, itemstack.getItemDamage(), 1.0f);
                        GL11.glPopMatrix();
                    }
                    if (block.getRenderBlockPass() > 0) {
                        GL11.glDisable((int)3042);
                    }
                } else if (itemstack.getItem().requiresMultipleRenderPasses()) {
                    if (renderInFrame) {
                        GL11.glScalef((float)0.5128205f, (float)0.5128205f, (float)0.5128205f);
                        GL11.glTranslatef((float)0.0f, (float)-0.05f, (float)0.0f);
                    } else {
                        GL11.glScalef((float)0.5f, (float)0.5f, (float)0.5f);
                    }
                    for (int j = 0; j < itemstack.getItem().getRenderPasses(itemstack.getItemDamage()); ++j) {
                        this.random.setSeed(187L);
                        IIcon iicon1 = itemstack.getItem().getIcon(itemstack, j);
                        if (this.renderWithColor) {
                            k = itemstack.getItem().getColorFromItemStack(itemstack, j);
                            float f5 = (float)(k >> 16 & 0xFF) / 255.0f;
                            f6 = (float)(k >> 8 & 0xFF) / 255.0f;
                            f7 = (float)(k & 0xFF) / 255.0f;
                            GL11.glColor4f((float)f5, (float)f6, (float)f7, (float)1.0f);
                            this.renderDroppedItem(boomerang, iicon1, b0, p_76986_9_, f5, f6, f7, j);
                            continue;
                        }
                        this.renderDroppedItem(boomerang, iicon1, b0, p_76986_9_, 1.0f, 1.0f, 1.0f, j);
                    }
                } else {
                    if (itemstack != null && itemstack.getItem() instanceof ItemCloth) {
                        GL11.glAlphaFunc((int)516, (float)0.1f);
                        GL11.glEnable((int)3042);
                        OpenGlHelper.glBlendFunc((int)770, (int)771, (int)1, (int)0);
                    }
                    if (renderInFrame) {
                        GL11.glScalef((float)0.5128205f, (float)0.5128205f, (float)0.5128205f);
                        GL11.glTranslatef((float)0.0f, (float)-0.05f, (float)0.0f);
                    } else {
                        GL11.glScalef((float)0.5f, (float)0.5f, (float)0.5f);
                    }
                    IIcon iicon = itemstack.getIconIndex();
                    if (this.renderWithColor) {
                        int i = itemstack.getItem().getColorFromItemStack(itemstack, 0);
                        f4 = (float)(i >> 16 & 0xFF) / 255.0f;
                        float f5 = (float)(i >> 8 & 0xFF) / 255.0f;
                        f6 = (float)(i & 0xFF) / 255.0f;
                        this.renderDroppedItem(boomerang, iicon, b0, p_76986_9_, f4, f5, f6);
                    } else {
                        this.renderDroppedItem(boomerang, iicon, b0, p_76986_9_, 1.0f, 1.0f, 1.0f);
                    }
                    if (itemstack != null && itemstack.getItem() instanceof ItemCloth) {
                        GL11.glDisable((int)3042);
                    }
                }
            }
            if (boomerang.getInventory().size() > 0) {
                Tessellator tessellator = Tessellator.instance;
                float f9 = 0.0625f;
                for (int i = 0; i < boomerang.getInventory().size(); ++i) {
                    IIcon stackIcon = boomerang.getInventory().get(i).getItem().getIconFromDamage(0);
                    float f14 = stackIcon.getMinU();
                    float f15 = stackIcon.getMaxU();
                    f4 = stackIcon.getMinV();
                    float f5 = stackIcon.getMaxV();
                    ItemRenderer.renderItemIn2D((Tessellator)tessellator, (float)f15, (float)f4, (float)f14, (float)f5, (int)stackIcon.getIconWidth(), (int)stackIcon.getIconHeight(), (float)f9);
                }
            }
            GL11.glDisable((int)32826);
            GL11.glPopMatrix();
            this.bindEntityTexture(boomerang);
            TextureUtil.func_147945_b();
        }
    }

    public void doRender(Entity entity, double p_76986_2_, double p_76986_4_, double p_76986_6_, float p_76986_8_, float p_76986_9_) {
        this.doRender((EntityBoomerang)entity, p_76986_2_, p_76986_4_, p_76986_6_, p_76986_8_, p_76986_9_);
    }

    protected ResourceLocation getEntityTexture(EntityBoomerang boomerang) {
        return boomerangTextures;
    }

    protected ResourceLocation getEntityTexture(Entity entity) {
        return this.getEntityTexture((EntityBoomerang)entity);
    }

    private void renderDroppedItem(EntityBoomerang boomerang, IIcon icon, int renderPass, float angle, float colorRed, float colorGreen, float colorBlue) {
        this.renderDroppedItem(boomerang, icon, renderPass, angle, colorRed, colorGreen, colorBlue, 0);
    }

    private void renderDroppedItem(EntityBoomerang boomerang, IIcon icon, int renderPasses, float angle, float colorRed, float colorGreen, float colorBlue, int pass) {
        this.renderDroppedItem(icon, renderPasses, angle, colorRed, colorGreen, colorBlue, pass);
    }

    private void renderDroppedItem(IIcon icon, int renderPasses, float angle, float colorRed, float colorGreen, float colorBlue, int pass) {
        Tessellator tessellator = Tessellator.instance;
        if (icon == null) {
            TextureManager texturemanager = Minecraft.getMinecraft().getTextureManager();
            ResourceLocation resourcelocation = boomerangTextures;
            icon = ((TextureMap)texturemanager.getTexture(resourcelocation)).getAtlasSprite("missingno");
        }
        float f14 = icon.getMinU();
        float f15 = icon.getMaxU();
        float f4 = icon.getMinV();
        float f5 = icon.getMaxV();
        float f6 = 1.0f;
        float f7 = 0.5f;
        float f8 = 0.25f;
        if (this.renderManager.options.fancyGraphics) {
            GL11.glPushMatrix();
            if (renderInFrame) {
                GL11.glRotatef((float)180.0f, (float)0.0f, (float)1.0f, (float)0.0f);
            } else {
                GL11.glRotatef((float)(((0.0f + angle) / 20.0f + 0.0f) * 57.295776f), (float)0.0f, (float)1.0f, (float)0.0f);
            }
            float f9 = 0.0625f;
            float f10 = 0.021875f;
            ItemStack itemstack = new ItemStack((Item)WEArcheryItems.boomerang);
            int b0 = 1;
            GL11.glTranslatef((float)(-f7), (float)(-f8), (float)(-((f9 + f10) * (float)b0 / 2.0f)));
            for (int k = 0; k < b0; ++k) {
                if (k > 0) {
                    float x = (this.random.nextFloat() * 2.0f - 1.0f) * 0.3f / 0.5f;
                    float y = (this.random.nextFloat() * 2.0f - 1.0f) * 0.3f / 0.5f;
                    GL11.glTranslatef((float)x, (float)y, (float)(f9 + f10));
                } else {
                    GL11.glTranslatef((float)0.0f, (float)0.0f, (float)(f9 + f10));
                }
                if (itemstack.getItemSpriteNumber() == 0) {
                    this.bindTexture(TextureMap.locationBlocksTexture);
                } else {
                    this.bindTexture(TextureMap.locationItemsTexture);
                }
                GL11.glColor4f((float)colorRed, (float)colorGreen, (float)colorBlue, (float)1.0f);
                ItemRenderer.renderItemIn2D((Tessellator)tessellator, (float)f15, (float)f4, (float)f14, (float)f5, (int)icon.getIconWidth(), (int)icon.getIconHeight(), (float)f9);
                if (!itemstack.hasEffect(pass)) continue;
                GL11.glDepthFunc((int)514);
                GL11.glDisable((int)2896);
                this.renderManager.renderEngine.bindTexture(boomerangTextures);
                GL11.glEnable((int)3042);
                GL11.glBlendFunc((int)768, (int)1);
                float f11 = 0.76f;
                GL11.glColor4f((float)(0.5f * f11), (float)(0.25f * f11), (float)(0.8f * f11), (float)1.0f);
                GL11.glMatrixMode((int)5890);
                GL11.glPushMatrix();
                float f12 = 0.125f;
                GL11.glScalef((float)f12, (float)f12, (float)f12);
                float f13 = (float)(Minecraft.getSystemTime() % 3000L) / 3000.0f * 8.0f;
                GL11.glTranslatef((float)f13, (float)0.0f, (float)0.0f);
                GL11.glRotatef((float)-50.0f, (float)0.0f, (float)0.0f, (float)1.0f);
                ItemRenderer.renderItemIn2D((Tessellator)tessellator, (float)0.0f, (float)0.0f, (float)1.0f, (float)1.0f, (int)255, (int)255, (float)f9);
                GL11.glPopMatrix();
                GL11.glPushMatrix();
                GL11.glScalef((float)f12, (float)f12, (float)f12);
                f13 = (float)(Minecraft.getSystemTime() % 4873L) / 4873.0f * 8.0f;
                GL11.glTranslatef((float)(-f13), (float)0.0f, (float)0.0f);
                GL11.glRotatef((float)10.0f, (float)0.0f, (float)0.0f, (float)1.0f);
                ItemRenderer.renderItemIn2D((Tessellator)tessellator, (float)0.0f, (float)0.0f, (float)1.0f, (float)1.0f, (int)255, (int)255, (float)f9);
                GL11.glPopMatrix();
                GL11.glMatrixMode((int)5888);
                GL11.glDisable((int)3042);
                GL11.glEnable((int)2896);
                GL11.glDepthFunc((int)515);
            }
            GL11.glPopMatrix();
        } else {
            for (int l = 0; l < renderPasses; ++l) {
                GL11.glPushMatrix();
                if (l > 0) {
                    float f10 = (this.random.nextFloat() * 2.0f - 1.0f) * 0.3f;
                    float f16 = (this.random.nextFloat() * 2.0f - 1.0f) * 0.3f;
                    float f17 = (this.random.nextFloat() * 2.0f - 1.0f) * 0.3f;
                    GL11.glTranslatef((float)f10, (float)f16, (float)f17);
                }
                if (!renderInFrame) {
                    GL11.glRotatef((float)(180.0f - this.renderManager.playerViewY), (float)0.0f, (float)1.0f, (float)0.0f);
                }
                GL11.glColor4f((float)colorRed, (float)colorGreen, (float)colorBlue, (float)1.0f);
                tessellator.startDrawingQuads();
                tessellator.setNormal(0.0f, 1.0f, 0.0f);
                tessellator.addVertexWithUV((double)(0.0f - f7), (double)(0.0f - f8), 0.0, (double)f14, (double)f5);
                tessellator.addVertexWithUV((double)(f6 - f7), (double)(0.0f - f8), 0.0, (double)f15, (double)f5);
                tessellator.addVertexWithUV((double)(f6 - f7), (double)(1.0f - f8), 0.0, (double)f15, (double)f4);
                tessellator.addVertexWithUV((double)(0.0f - f7), (double)(1.0f - f8), 0.0, (double)f14, (double)f4);
                tessellator.draw();
                GL11.glPopMatrix();
            }
        }
    }

    public static boolean renderEBoomerang(EntityBoomerang entity, ItemStack item, float bobing, float rotation, Random random, TextureManager engine, RenderBlocks renderBlocks, int count) {
        Block block;
        IItemRenderer customRenderer = MinecraftForgeClient.getItemRenderer((ItemStack)(entity.getThrownBoomerang() == null ? item : entity.getThrownBoomerang()), (IItemRenderer.ItemRenderType)IItemRenderer.ItemRenderType.ENTITY);
        if (customRenderer == null) {
            return false;
        }
        if (customRenderer.shouldUseRenderHelper(IItemRenderer.ItemRenderType.ENTITY, item, IItemRenderer.ItemRendererHelper.ENTITY_ROTATION)) {
            GL11.glRotatef((float)rotation, (float)0.0f, (float)1.0f, (float)0.0f);
        }
        if (!customRenderer.shouldUseRenderHelper(IItemRenderer.ItemRenderType.ENTITY, item, IItemRenderer.ItemRendererHelper.ENTITY_BOBBING)) {
            GL11.glTranslatef((float)0.0f, (float)(-bobing), (float)0.0f);
        }
        boolean is3D = customRenderer.shouldUseRenderHelper(IItemRenderer.ItemRenderType.ENTITY, item, IItemRenderer.ItemRendererHelper.BLOCK_3D);
        engine.bindTexture(item.getItemSpriteNumber() == 0 ? TextureMap.locationBlocksTexture : TextureMap.locationItemsTexture);
        Block block2 = block = item.getItem() instanceof ItemBlock ? Block.getBlockFromItem((Item)item.getItem()) : null;
        if (is3D || block != null && RenderBlocks.renderItemIn3d((int)block.getRenderType())) {
            boolean blend;
            int renderType = block != null ? block.getRenderType() : 1;
            float scale = renderType == 1 || renderType == 19 || renderType == 12 || renderType == 2 ? 0.5f : 0.25f;
            boolean bl = blend = block != null && block.getRenderBlockPass() > 0;
            if (RenderItem.renderInFrame) {
                GL11.glScalef((float)1.25f, (float)1.25f, (float)1.25f);
                GL11.glTranslatef((float)0.0f, (float)0.05f, (float)0.0f);
                GL11.glRotatef((float)-90.0f, (float)0.0f, (float)1.0f, (float)0.0f);
            }
            if (blend) {
                GL11.glAlphaFunc((int)516, (float)0.1f);
                GL11.glEnable((int)3042);
                OpenGlHelper.glBlendFunc((int)770, (int)771, (int)1, (int)0);
            }
            GL11.glScalef((float)scale, (float)scale, (float)scale);
            for (int j = 0; j < count; ++j) {
                GL11.glPushMatrix();
                if (j > 0) {
                    GL11.glTranslatef((float)((random.nextFloat() * 2.0f - 1.0f) * 0.2f / scale), (float)((random.nextFloat() * 2.0f - 1.0f) * 0.2f / scale), (float)((random.nextFloat() * 2.0f - 1.0f) * 0.2f / scale));
                }
                customRenderer.renderItem(IItemRenderer.ItemRenderType.ENTITY, item, new Object[]{renderBlocks, entity});
                GL11.glPopMatrix();
            }
            if (blend) {
                GL11.glDisable((int)3042);
            }
        } else {
            GL11.glScalef((float)0.5f, (float)0.5f, (float)0.5f);
            customRenderer.renderItem(IItemRenderer.ItemRenderType.ENTITY, item, new Object[]{renderBlocks, entity});
        }
        return true;
    }
}

