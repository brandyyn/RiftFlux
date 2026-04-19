/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.ItemRenderer
 *  net.minecraft.client.renderer.Tessellator
 *  net.minecraft.client.renderer.texture.TextureManager
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.IIcon
 *  net.minecraft.util.ResourceLocation
 *  org.lwjgl.opengl.GL11
 */
package zairus.worldexplorer.archery.client.renderer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import zairus.worldexplorer.archery.client.model.ModelQuiver;
import zairus.worldexplorer.archery.items.WEArcheryItems;
import zairus.worldexplorer.core.client.IPlayerRenderer;
import zairus.worldexplorer.core.helpers.ColorHelper;
import zairus.worldexplorer.core.player.CorePlayerManager;

@SideOnly(value=Side.CLIENT)
public class PlayerQuiverRenderer
implements IPlayerRenderer {
    private ResourceLocation quiverTextures = new ResourceLocation("worldexplorer", "textures/model/quiver.png");
    private ModelQuiver quiverModel = new ModelQuiver();
    private static final Tessellator tessellator = Tessellator.instance;

    @Override
    public void render(EntityPlayer player) {
        if (!CorePlayerManager.getPlayerEquipmentInventory(player).hasQuiver()) {
            return;
        }
        TextureManager renderManager = Minecraft.getMinecraft().renderEngine;
        float offsetX = 0.15f;
        float offsetY = 0.1f;
        float offsetZ = -0.38f;
        float rotationY = -player.renderYawOffset;
        float rotationZ = 200.0f;
        renderManager.bindTexture(this.quiverTextures);
        GL11.glRotatef((float)rotationY, (float)0.0f, (float)1.0f, (float)0.0f);
        GL11.glRotatef((float)rotationZ, (float)0.0f, (float)0.0f, (float)1.0f);
        GL11.glTranslatef((float)offsetX, (float)offsetY, (float)offsetZ);
        this.quiverModel.render((Entity)player, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0625f);
        GL11.glTranslatef((float)(-offsetX), (float)(-offsetY), (float)(-offsetZ));
        GL11.glRotatef((float)(-rotationZ), (float)0.0f, (float)0.0f, (float)1.0f);
        GL11.glRotatef((float)(-rotationY), (float)0.0f, (float)1.0f, (float)0.0f);
        ItemStack arrow = new ItemStack((Item)WEArcheryItems.specialarrow, 1, 1);
        IIcon icon = arrow.getItem().getIcon(arrow, 0);
        if (icon != null) {
            GL11.glPushMatrix();
            int color = arrow.getItem().getColorFromItemStack(arrow, 0);
            ColorHelper.glSetColor(color, 1.0f);
            offsetX = -0.3f;
            offsetY = -1.0f;
            offsetZ = 0.5f;
            GL11.glDisable((int)2896);
            GL11.glEnable((int)3008);
            GL11.glTranslatef((float)offsetX, (float)offsetY, (float)offsetZ);
            this.drawItem(icon, 0.9f);
            GL11.glTranslatef((float)(-offsetX), (float)(-offsetY), (float)(-offsetZ));
            GL11.glDisable((int)3008);
            GL11.glEnable((int)2896);
            GL11.glPopMatrix();
        }
    }

    private void drawItem(IIcon icon, float thickness) {
        float xStart = icon.getMinU();
        float xEnd = icon.getMaxU();
        float yStart = icon.getMinV();
        float yEnd = icon.getMaxV();
        int height = icon.getIconHeight();
        int width = icon.getIconWidth();
        ItemRenderer.renderItemIn2D((Tessellator)tessellator, (float)xEnd, (float)yStart, (float)xStart, (float)yEnd, (int)width, (int)height, (float)thickness);
    }
}

