package com.voidsrift.riftflux.terramine;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

@SideOnly(Side.CLIENT)
public class RenderTerraModelItem implements IItemRenderer {
    private final ResourceLocation texture;
    private final TerraModelRenderable model;
    private final float rotation;

    public RenderTerraModelItem(ResourceLocation texture, TerraModelRenderable model, float rotation) {
        this.texture = texture;
        this.model = model;
        this.rotation = rotation;
    }

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        switch (type) {
            case ENTITY:
            case EQUIPPED:
            case EQUIPPED_FIRST_PERSON:
            case INVENTORY:
                return true;
            default:
                return false;
        }
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        switch (type) {
            case ENTITY:
                return helper == ItemRendererHelper.ENTITY_ROTATION
                        || helper == ItemRendererHelper.ENTITY_BOBBING
                        || helper == ItemRendererHelper.BLOCK_3D;
            case EQUIPPED:
            case EQUIPPED_FIRST_PERSON:
                return helper == ItemRendererHelper.EQUIPPED_BLOCK;
            case INVENTORY:
                return helper == ItemRendererHelper.INVENTORY_BLOCK;
            default:
                return false;
        }
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        GL11.glPushMatrix();
        try {
            OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);

            if (type == ItemRenderType.INVENTORY) {
                GL11.glTranslatef(0.5F, 1.5F, 0.5F);
                GL11.glScalef(1.0F, -1.0F, -1.0F);
                Minecraft.getMinecraft().getTextureManager().bindTexture(this.texture);
                this.model.renderAll();
            } else if (type == ItemRenderType.EQUIPPED) {
                GL11.glTranslatef(0.32F, 1.82F, 0.77F);
                GL11.glScalef(1.0F, -1.0F, -1.0F);
                GL11.glRotatef(this.rotation, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(-15.0F, 1.0F, 0.0F, 0.0F);
                Minecraft.getMinecraft().getTextureManager().bindTexture(this.texture);
                this.model.renderAll();
            } else if (type == ItemRenderType.EQUIPPED_FIRST_PERSON) {
                GL11.glTranslatef(0.6F, 0.65F, 0.6F);
                GL11.glScalef(0.9F, 0.9F, 0.9F);
                GL11.glRotatef(this.rotation, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
                GL11.glTranslatef(0.5F, -1.5F, -0.5F);
                Minecraft.getMinecraft().getTextureManager().bindTexture(this.texture);
                this.model.renderAll();
            } else {
                GL11.glTranslatef(0.5F, 1.5F, 0.5F);
                GL11.glScalef(1.0F, -1.0F, -1.0F);
                GL11.glRotatef(this.rotation, 0.0F, 1.0F, 0.0F);
                Minecraft.getMinecraft().getTextureManager().bindTexture(this.texture);
                this.model.renderAll();
            }
        } finally {
            OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL12.GL_RESCALE_NORMAL);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glPopMatrix();
        }
    }
}