/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.opengl.GL11
 */
package de.sanandrew.core.manpack.util.client.helpers;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

@SideOnly(value=Side.CLIENT)
public final class GuiUtils {
    private static RenderItem itemRenderer = new RenderItem();

    public static void drawGradientRect(int x1, int y1, int x2, int y2, int color1, int color2, float zLevel) {
        float a1 = (float)(color1 >> 24 & 0xFF) / 255.0f;
        float r1 = (float)(color1 >> 16 & 0xFF) / 255.0f;
        float g1 = (float)(color1 >> 8 & 0xFF) / 255.0f;
        float b1 = (float)(color1 & 0xFF) / 255.0f;
        float a2 = (float)(color2 >> 24 & 0xFF) / 255.0f;
        float r2 = (float)(color2 >> 16 & 0xFF) / 255.0f;
        float g2 = (float)(color2 >> 8 & 0xFF) / 255.0f;
        float b2 = (float)(color2 & 0xFF) / 255.0f;
        GL11.glDisable((int)3553);
        GL11.glEnable((int)3042);
        GL11.glDisable((int)3008);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GL11.glShadeModel((int)7425);
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.setColorRGBA_F(r2, g2, b2, a2);
        tessellator.addVertex(x2, y1, zLevel);
        tessellator.setColorRGBA_F(r1, g1, b1, a1);
        tessellator.addVertex(x1, y1, zLevel);
        tessellator.addVertex(x1, y2, zLevel);
        tessellator.setColorRGBA_F(r2, g2, b2, a2);
        tessellator.addVertex(x2, y2, zLevel);
        tessellator.draw();
        GL11.glShadeModel((int)7424);
        GL11.glDisable((int)3042);
        GL11.glEnable((int)3008);
        GL11.glEnable((int)3553);
    }

    public static void drawOutlinedString(FontRenderer renderer, String s, int x, int y, int foreColor, int frameColor) {
        if (renderer.getUnicodeFlag()) {
            GL11.glTranslatef((float)0.0f, (float)0.5f, (float)0.0f);
            renderer.drawString(s, x, y, frameColor);
            GL11.glTranslatef((float)0.0f, (float)-1.0f, (float)0.0f);
            renderer.drawString(s, x, y, frameColor);
            GL11.glTranslatef((float)0.5f, (float)0.5f, (float)0.0f);
            renderer.drawString(s, x, y, frameColor);
            GL11.glTranslatef((float)-1.0f, (float)0.0f, (float)0.0f);
            renderer.drawString(s, x, y, frameColor);
            GL11.glTranslatef((float)0.5f, (float)0.0f, (float)0.0f);
        } else {
            renderer.drawString(s, x - 1, y, frameColor);
            renderer.drawString(s, x + 1, y, frameColor);
            renderer.drawString(s, x, y - 1, frameColor);
            renderer.drawString(s, x, y + 1, frameColor);
        }
        renderer.drawString(s, x, y, foreColor);
    }

    public static void doGlScissor(int x, int y, int width, int height) {
        Minecraft mc = Minecraft.getMinecraft();
        int scaleFactor = 1;
        int k = mc.gameSettings.guiScale;
        if (k == 0) {
            k = 1000;
        }
        while (scaleFactor < k && mc.displayWidth / (scaleFactor + 1) >= 320 && mc.displayHeight / (scaleFactor + 1) >= 240) {
            ++scaleFactor;
        }
        GL11.glScissor((int)(x * scaleFactor), (int)(mc.displayHeight - (y + height) * scaleFactor), (int)(width * scaleFactor), (int)(height * scaleFactor));
    }

    public static void drawGuiIcon(IIcon icon, int posX, int posY) {
        GL11.glPushMatrix();
        GL11.glTranslatef((float)0.0f, (float)0.0f, (float)32.0f);
        GuiUtils.itemRenderer.zLevel = 200.0f;
        GL11.glEnable((int)3042);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GL11.glEnable((int)3008);
        ResourceLocation resourcelocation = Minecraft.getMinecraft().getTextureManager().getResourceLocation(1);
        Minecraft.getMinecraft().getTextureManager().bindTexture(resourcelocation);
        if (icon == null) {
            icon = ((TextureMap)Minecraft.getMinecraft().getTextureManager().getTexture(resourcelocation)).getAtlasSprite("missingno");
        }
        itemRenderer.renderIcon(posX, posY, icon, 16, 16);
        GL11.glDisable((int)3008);
        GL11.glDisable((int)3042);
        GuiUtils.itemRenderer.zLevel = 0.0f;
        GL11.glPopMatrix();
    }

    public static void drawGuiStack(ItemStack stack, int posX, int posY) {
        Item itm = stack.getItem();
        if (itm.requiresMultipleRenderPasses()) {
            int max = itm.getRenderPasses(stack.getItemDamage());
            for (int i = 0; i < max; ++i) {
                int k1 = itm.getColorFromItemStack(stack, i);
                float red = (float)(k1 >> 16 & 0xFF) / 255.0f;
                float green = (float)(k1 >> 8 & 0xFF) / 255.0f;
                float blue = (float)(k1 & 0xFF) / 255.0f;
                GL11.glColor4f((float)(1.0f * red), (float)(1.0f * green), (float)(1.0f * blue), (float)1.0f);
                GuiUtils.drawGuiIcon(itm.getIcon(stack, i), posX, posY);
            }
            GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        } else {
            int k1 = itm.getColorFromItemStack(stack, 0);
            float red = (float)(k1 >> 16 & 0xFF) / 255.0f;
            float green = (float)(k1 >> 8 & 0xFF) / 255.0f;
            float blue = (float)(k1 & 0xFF) / 255.0f;
            GL11.glColor4f((float)(1.0f * red), (float)(1.0f * green), (float)(1.0f * blue), (float)1.0f);
            GuiUtils.drawGuiIcon(stack.getIconIndex(), posX, posY);
            GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        }
    }

    public static boolean isMouseInRect(int mouseX, int mouseY, int rectX, int rectY, int width, int height) {
        return mouseX >= rectX && mouseX < rectX + width && mouseY >= rectY && mouseY < rectY + height;
    }
}

