package com.voidsrift.riftflux.client.chatbubbles;

import com.voidsrift.riftflux.Constants;
import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.chatbubbles.ChatBubbleColorManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.util.List;

public final class ChatBubbleRenderer {
    private static final ResourceLocation BUBBLE_TEXTURE =
            new ResourceLocation(Constants.MODID, "textures/chatbubbles/chatBubble.png");
    private static final ResourceLocation TAIL_TEXTURE =
            new ResourceLocation(Constants.MODID, "textures/chatbubbles/chatBubbleTail.png");
    private static final int LIGHTMAP_BRIGHT = 0xF000F0;
    private static final int BORDER_WIDTH = 4;
    private static final float HORIZONTAL_PADDING = 5.0F;
    private static final float TAIL_HALF_WIDTH = 4.0F;
    private static final int LINE_HEIGHT = 9;

    private ChatBubbleRenderer() {
    }

    public static void render(EntityPlayer player, double x, double y, double z, List<ChatBubbleMessage> messages, int messageLifetimeTicks) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc == null || mc.thePlayer == null || player == null || messages == null || messages.isEmpty()) {
            return;
        }
        if (player == mc.thePlayer && (!ModConfig.chatBubblesShowOwnMessages || mc.gameSettings.thirdPersonView == 0)) {
            return;
        }

        double distanceSq = player.getDistanceSqToEntity((Entity) mc.thePlayer);
        float maxDistance = player.isSneaking() ? 32.0F : 64.0F;
        if (distanceSq > maxDistance * maxDistance) {
            return;
        }

        String playerName = ChatBubblesClient.scrubCodes(player.getCommandSenderName());
        if (playerName == null || playerName.isEmpty()) {
            return;
        }

        int bubbleColor = ChatBubbleColorManager.resolveRenderColor(player, player == mc.thePlayer);
        float red = ((bubbleColor >> 16) & 255) / 255.0F;
        float green = ((bubbleColor >> 8) & 255) / 255.0F;
        float blue = (bubbleColor & 255) / 255.0F;
        int textColor = ChatBubbleColorManager.resolveRenderTextColor(player, player == mc.thePlayer);

        int currentTime = mc.ingameGUI.getUpdateCounter();
        int lines = 2;
        int messageGap = Math.max(0, ModConfig.chatBubblesMessageGap);
        for (ChatBubbleMessage message : messages) {
            String[] messageLines = message.getMessageLines();
            float remainingTime = messageLifetimeTicks - (currentTime - message.getUpdateCounterCreated());
            renderMessage(x, y, z, lines, messageLines, remainingTime, red, green, blue, textColor);
            lines += messageLines.length + messageGap;
        }
    }

    private static void renderMessage(double x, double y, double z, int lines, String[] messageLines,
                                      float remainingTime, float red, float green, float blue, int textRgb) {
        Minecraft mc = Minecraft.getMinecraft();
        FontRenderer fontRenderer = mc.fontRenderer;
        RenderManager renderManager = RenderManager.instance;

        int brightnessX = LIGHTMAP_BRIGHT % 65536;
        int brightnessY = LIGHTMAP_BRIGHT / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, brightnessX, brightnessY);

        int[] lineWidths = getLineWidths(fontRenderer, messageLines);
        int maxTextWidth = getMaxWidth(lineWidths);
        float halfWidth = Math.max(maxTextWidth * 0.5F + HORIZONTAL_PADDING, TAIL_HALF_WIDTH + 2.0F);
        float left = -halfWidth;
        float right = halfWidth;
        int top = -(lines + messageLines.length - 1) * LINE_HEIGHT + 2;
        int bottom = -lines * LINE_HEIGHT + 6;

        float alphaFadeFactor = 1.0F;
        if (remainingTime < 20.0F) {
            alphaFadeFactor = remainingTime / 20.0F;
            alphaFadeFactor *= alphaFadeFactor;
        }

        GL11.glPushMatrix();
        GL11.glTranslatef((float) x, (float) y + 2.3F, (float) z);
        GL11.glNormal3f(0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        float scale = 0.02666667F * ModConfig.clampChatBubblesTextScale(ModConfig.chatBubblesTextScale);
        GL11.glScalef(-scale, -scale, scale);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_FOG);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_TEXTURE_2D);

        if (ModConfig.chatBubblesShowBackground) {
            GL11.glDepthMask(false);
            GL11.glEnable(GL11.GL_POLYGON_OFFSET_FILL);
            GL11.glPolygonOffset(1.0F, 5.0F);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            drawBubble(1.0F, 1.0F, 1.0F, Math.min(alphaFadeFactor, 0.25F), top, bottom, left, right, 5.0F);

            GL11.glPolygonOffset(1.0F, 3.0F);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glDepthMask(true);
            drawBubble(red, green, blue, alphaFadeFactor, top, bottom, left, right, 3.0F);
        }

        int textAlpha = Math.max(4, Math.min(255, (int) (alphaFadeFactor * 255.0F)));
        int shadowAlpha = Math.min(200, Math.max(24, (int) (textAlpha * 0.75F)));

        for (int i = messageLines.length - 1; i >= 0; i--) {
            int textY = -lines * LINE_HEIGHT;
            int textX = -lineWidths[i] / 2;

            GL11.glDisable(GL11.GL_DEPTH_TEST);
            GL11.glDepthMask(false);
            if (ModConfig.chatBubblesBlackTextBackground) {
                GL11.glDisable(GL11.GL_TEXTURE_2D);
                float barAlpha = alphaFadeFactor * ModConfig.clampChatBubblesBlackTextBackgroundOpacity(ModConfig.chatBubblesBlackTextBackgroundOpacity);
                drawSolidQuad(0.0F, 0.0F, 0.0F, barAlpha,
                        textX - 3.0F,
                        textY - 1.0F,
                        textX + lineWidths[i] + 3.0F,
                        textY + LINE_HEIGHT);
                GL11.glEnable(GL11.GL_TEXTURE_2D);
            }
            fontRenderer.drawString(messageLines[i], textX + 1, textY + 1, composeArgb(0x000000, shadowAlpha));

            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glDepthMask(true);
            fontRenderer.drawString(messageLines[i], textX, textY, composeArgb(textRgb, textAlpha));
            lines++;
        }

        GL11.glDisable(GL11.GL_POLYGON_OFFSET_FILL);
        GL11.glEnable(GL11.GL_FOG);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glPopMatrix();
    }

    private static void drawBubble(float red, float green, float blue, float alpha, float top, float bottom, float left, float right, float offset) {
        bind(BUBBLE_TEXTURE);
        drawQuad(red, green, blue, alpha, left, top, left, bottom, right, bottom, right, top,
                0.0625, 0.125, 0.0625, 0.875, 0.9375, 0.875, 0.9375, 0.125);
        drawQuad(red, green, blue, alpha, left - BORDER_WIDTH, top, left - BORDER_WIDTH, bottom, left, bottom, left, top,
                0.0, 0.125, 0.0, 0.875, 0.0625, 0.875, 0.0625, 0.125);
        drawQuad(red, green, blue, alpha, right, top, right, bottom, right + BORDER_WIDTH, bottom, right + BORDER_WIDTH, top,
                0.9375, 0.125, 0.9375, 0.875, 1.0, 0.875, 1.0, 0.125);
        drawQuad(red, green, blue, alpha, left, top - BORDER_WIDTH, left, top, right, top, right, top - BORDER_WIDTH,
                0.0625, 0.0, 0.0625, 0.125, 0.9375, 0.125, 0.9375, 0.0);
        drawQuad(red, green, blue, alpha, left, bottom, left, bottom + BORDER_WIDTH, -TAIL_HALF_WIDTH, bottom + BORDER_WIDTH, -TAIL_HALF_WIDTH, bottom,
                0.0625, 0.875, 0.0625, 1.0, 0.5, 1.0, 0.5, 0.875);
        drawQuad(red, green, blue, alpha, -TAIL_HALF_WIDTH, bottom, -TAIL_HALF_WIDTH, bottom + BORDER_WIDTH, TAIL_HALF_WIDTH, bottom + BORDER_WIDTH, TAIL_HALF_WIDTH, bottom,
                0.5, 0.875, 0.5, 1.0, 0.625, 1.0, 0.625, 0.875);
        drawQuad(red, green, blue, alpha, TAIL_HALF_WIDTH, bottom, TAIL_HALF_WIDTH, bottom + BORDER_WIDTH, right, bottom + BORDER_WIDTH, right, bottom,
                0.625, 0.875, 0.625, 1.0, 0.9375, 1.0, 0.9375, 0.875);
        drawQuad(red, green, blue, alpha, left - BORDER_WIDTH, top - BORDER_WIDTH, left - BORDER_WIDTH, top, left, top, left, top - BORDER_WIDTH,
                0.0, 0.0, 0.0, 0.125, 0.0625, 0.125, 0.0625, 0.0);
        drawQuad(red, green, blue, alpha, right, top - BORDER_WIDTH, right, top, right + BORDER_WIDTH, top, right + BORDER_WIDTH, top - BORDER_WIDTH,
                0.9375, 0.0, 0.9375, 0.125, 1.0, 0.125, 1.0, 0.0);
        drawQuad(red, green, blue, alpha, left - BORDER_WIDTH, bottom, left - BORDER_WIDTH, bottom + BORDER_WIDTH, left, bottom + BORDER_WIDTH, left, bottom,
                0.0, 0.875, 0.0, 1.0, 0.0625, 1.0, 0.0625, 0.875);
        drawQuad(red, green, blue, alpha, right, bottom, right, bottom + BORDER_WIDTH, right + BORDER_WIDTH, bottom + BORDER_WIDTH, right + BORDER_WIDTH, bottom,
                0.9375, 0.875, 0.9375, 1.0, 1.0, 1.0, 1.0, 0.875);

        GL11.glPolygonOffset(1.0F, offset - 1.0F);
        bind(TAIL_TEXTURE);
        drawQuad(red, green, blue, alpha, -TAIL_HALF_WIDTH, bottom + BORDER_WIDTH, -TAIL_HALF_WIDTH, bottom + BORDER_WIDTH + 8.0F,
                TAIL_HALF_WIDTH, bottom + BORDER_WIDTH + 8.0F, TAIL_HALF_WIDTH, bottom + BORDER_WIDTH,
                0.0, 0.0, 0.0, 1.0, 1.0, 1.0, 1.0, 0.0);
    }

    private static void bind(ResourceLocation resource) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(resource);
    }

    private static void drawQuad(float red, float green, float blue, float alpha,
                                 float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4,
                                 double u1, double v1, double u2, double v2, double u3, double v3, double u4, double v4) {
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.setColorRGBA_F(red, green, blue, alpha);
        tessellator.addVertexWithUV(x1, y1, 0.0D, u1, v1);
        tessellator.addVertexWithUV(x2, y2, 0.0D, u2, v2);
        tessellator.addVertexWithUV(x3, y3, 0.0D, u3, v3);
        tessellator.addVertexWithUV(x4, y4, 0.0D, u4, v4);
        tessellator.draw();
    }

    private static void drawSolidQuad(float red, float green, float blue, float alpha,
                                      float left, float top, float right, float bottom) {
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.setColorRGBA_F(red, green, blue, alpha);
        tessellator.addVertex(left, top, 0.0D);
        tessellator.addVertex(left, bottom, 0.0D);
        tessellator.addVertex(right, bottom, 0.0D);
        tessellator.addVertex(right, top, 0.0D);
        tessellator.draw();
    }

    private static int[] getLineWidths(FontRenderer fontRenderer, String[] messageLines) {
        int[] widths = new int[messageLines.length];
        for (int i = 0; i < messageLines.length; i++) {
            widths[i] = fontRenderer.getStringWidth(messageLines[i]);
        }
        return widths;
    }

    private static int getMaxWidth(int[] widths) {
        int maxWidth = 0;
        for (int width : widths) {
            if (width > maxWidth) {
                maxWidth = width;
            }
        }
        return maxWidth;
    }

    private static int composeArgb(int rgb, int alpha) {
        return ((alpha & 255) << 24) | (rgb & 0xFFFFFF);
    }
}
