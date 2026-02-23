package com.voidsrift.riftflux.avatar.glider;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.avatar.util.FileLocation;
import com.voidsrift.riftflux.avatar.glider.GliderState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;

public class RenderGliderInHand implements IItemRenderer {
    private final ModelAirStaffClosed model = new ModelAirStaffClosed();
    private static final ResourceLocation RES_ITEM_GLINT =
            new ResourceLocation("textures/misc/enchanted_item_glint.png");

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        return type == ItemRenderType.EQUIPPED || type == ItemRenderType.EQUIPPED_FIRST_PERSON;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType t, ItemStack i, ItemRendererHelper h) {
        return false;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        EntityLivingBase living = null;
        if (data.length > 1 && data[1] instanceof Entity) {
            Entity entity = (Entity) data[1];
            if (entity instanceof net.minecraft.entity.player.EntityPlayer) {
                net.minecraft.entity.player.EntityPlayer player = (net.minecraft.entity.player.EntityPlayer) entity;
                boolean gliderActive = GliderState.isPlayerGliding(player.getDisplayName());
                boolean shouldGlide = gliderActive && !player.onGround && !player.isInWater();
                if (gliderActive || shouldGlide) {
                    return;
                }
                living = player;
            } else if (entity instanceof EntityLivingBase) {
                living = (EntityLivingBase) entity;
            }
        }

        if (ModConfig.gliderUseItemInHand) {
            renderFlatItem(item, living);
            return;
        }

        GL11.glRotatef(-15.0f, 2.0f, 3.0f, -5.0f);
        GL11.glTranslatef(0.3f, -0.3f, -0.1f);
        GL11.glScalef(2.0f, 2.0f, 1.0f);
        Minecraft.getMinecraft().renderEngine.bindTexture(
                new ResourceLocation(FileLocation.ENTITYTEXTURE + "Airbending staff closed.png"));
        this.model.render((Entity) data[1], 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0625f);
    }

    private void renderFlatItem(ItemStack item, EntityLivingBase living) {
        if (item == null) {
            return;
        }
        Minecraft mc = Minecraft.getMinecraft();
        TextureManager textureManager = mc.getTextureManager();
        textureManager.bindTexture(textureManager.getResourceLocation(item.getItemSpriteNumber()));
        TextureUtil.func_152777_a(false, false, 1.0f);
        Tessellator tessellator = Tessellator.instance;

        int passes = item.getItem().requiresMultipleRenderPasses()
                ? item.getItem().getRenderPasses(item.getItemDamage())
                : 1;

        for (int pass = 0; pass < passes; pass++) {
            IIcon icon = living != null ? living.getItemIcon(item, pass) : item.getItem().getIcon(item, pass);
            if (icon == null) {
                continue;
            }
            int color = item.getItem().getColorFromItemStack(item, pass);
            float r = (float) (color >> 16 & 255) / 255.0f;
            float g = (float) (color >> 8 & 255) / 255.0f;
            float b = (float) (color & 255) / 255.0f;
            GL11.glColor4f(r, g, b, 1.0f);
            ItemRenderer.renderItemIn2D(
                    tessellator,
                    icon.getMaxU(),
                    icon.getMinV(),
                    icon.getMinU(),
                    icon.getMaxV(),
                    icon.getIconWidth(),
                    icon.getIconHeight(),
                    0.0625f
            );
        }

        if (item.hasEffect(0)) {
            renderEnchantmentGlint(textureManager, tessellator);
        }
        TextureUtil.func_147945_b();
    }

    private void renderEnchantmentGlint(TextureManager textureManager, Tessellator tessellator) {
        GL11.glDepthFunc(GL11.GL_EQUAL);
        GL11.glDisable(GL11.GL_LIGHTING);
        textureManager.bindTexture(RES_ITEM_GLINT);
        GL11.glEnable(GL11.GL_BLEND);
        OpenGlHelper.glBlendFunc(768, 1, 1, 0);
        float tint = 0.76f;
        GL11.glColor4f(0.5f * tint, 0.25f * tint, 0.8f * tint, 1.0f);
        GL11.glMatrixMode(GL11.GL_TEXTURE);
        GL11.glPushMatrix();
        float scale = 0.125f;
        GL11.glScalef(scale, scale, scale);
        float scroll = (float) (Minecraft.getSystemTime() % 3000L) / 3000.0f * 8.0f;
        GL11.glTranslatef(scroll, 0.0f, 0.0f);
        GL11.glRotatef(-50.0f, 0.0f, 0.0f, 1.0f);
        ItemRenderer.renderItemIn2D(tessellator, 0.0f, 0.0f, 1.0f, 1.0f, 256, 256, 0.0625f);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glScalef(scale, scale, scale);
        scroll = (float) (Minecraft.getSystemTime() % 4873L) / 4873.0f * 8.0f;
        GL11.glTranslatef(-scroll, 0.0f, 0.0f);
        GL11.glRotatef(10.0f, 0.0f, 0.0f, 1.0f);
        ItemRenderer.renderItemIn2D(tessellator, 0.0f, 0.0f, 1.0f, 1.0f, 256, 256, 0.0625f);
        GL11.glPopMatrix();
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glDepthFunc(GL11.GL_LEQUAL);
    }
}
