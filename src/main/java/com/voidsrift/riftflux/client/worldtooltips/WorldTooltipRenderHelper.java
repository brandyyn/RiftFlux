package com.voidsrift.riftflux.client.worldtooltips;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.Tessellator;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public final class WorldTooltipRenderHelper {
    private WorldTooltipRenderHelper() {
    }

    public static void drawTooltipText(FontRenderer fontRenderer, WorldTooltip tooltip, int drawX, int drawY, int alphaBits) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        for (int i = 0; i < tooltip.size(); i++) {
            String line = tooltip.getLine(i);
            if (i == 0) {
                line = tooltip.getRarityFormatting() + line;
            }
            fontRenderer.drawString(line, drawX, drawY, 0xFFFFFF | alphaBits, true);
            if (i == 0) {
                drawY += 2;
            }
            drawY += 10;
        }
    }

    public static void drawTooltipBackground(int x, int y, int width, int height, int colorPrimary, int colorOutline, int colorSecondary) {
        drawGradientRect(x - 3, y - 4, width + 6, 1, colorPrimary, colorPrimary);
        drawGradientRect(x - 3, y + height + 3, width + 6, 1, colorPrimary, colorPrimary);
        drawGradientRect(x - 3, y - 3, width + 6, height + 6, colorPrimary, colorPrimary);
        drawGradientRect(x - 4, y - 3, 1, height + 6, colorPrimary, colorPrimary);
        drawGradientRect(x + width + 3, y - 3, 1, height + 6, colorPrimary, colorPrimary);
        drawGradientRect(x - 3, y - 2, 1, height + 4, colorOutline, colorSecondary);
        drawGradientRect(x + width + 2, y - 2, 1, height + 4, colorOutline, colorSecondary);
        drawGradientRect(x - 3, y - 3, width + 6, 1, colorOutline, colorOutline);
        drawGradientRect(x - 3, y + height + 2, width + 6, 1, colorSecondary, colorSecondary);
    }

    public static void drawGradientRect(int x, int y, int width, int height, int colorTop, int colorBottom) {
        int right = x + width;
        int bottom = y + height;
        float alphaTop = (float) (colorTop >> 24 & 0xFF) / 255.0F;
        float redTop = (float) (colorTop >> 16 & 0xFF) / 255.0F;
        float greenTop = (float) (colorTop >> 8 & 0xFF) / 255.0F;
        float blueTop = (float) (colorTop & 0xFF) / 255.0F;
        float alphaBottom = (float) (colorBottom >> 24 & 0xFF) / 255.0F;
        float redBottom = (float) (colorBottom >> 16 & 0xFF) / 255.0F;
        float greenBottom = (float) (colorBottom >> 8 & 0xFF) / 255.0F;
        float blueBottom = (float) (colorBottom & 0xFF) / 255.0F;

        Tessellator tessellator = Tessellator.instance;
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glShadeModel(GL11.GL_SMOOTH);

        tessellator.startDrawingQuads();
        tessellator.setColorRGBA_F(redTop, greenTop, blueTop, alphaTop);
        tessellator.addVertex(right, y, 0.0D);
        tessellator.addVertex(x, y, 0.0D);
        tessellator.setColorRGBA_F(redBottom, greenBottom, blueBottom, alphaBottom);
        tessellator.addVertex(x, bottom, 0.0D);
        tessellator.addVertex(right, bottom, 0.0D);
        tessellator.draw();

        GL11.glShadeModel(GL11.GL_FLAT);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }
}
