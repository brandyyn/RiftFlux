package com.voidsrift.riftflux.client;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.pets.PetKnockdownTimeout;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

public class PetKnockdownTimeoutRenderer {
    private static final PetKnockdownTimeoutRenderer INSTANCE = new PetKnockdownTimeoutRenderer();
    private static final double MAX_DISTANCE = 64.0D;
    private static final float LABEL_SCALE = 0.02666667F;
    private static final int ATTRIB_MASK = GL11.GL_ENABLE_BIT
            | GL11.GL_CURRENT_BIT
            | GL11.GL_COLOR_BUFFER_BIT
            | GL11.GL_DEPTH_BUFFER_BIT
            | GL11.GL_LIGHTING_BIT
            | GL11.GL_TEXTURE_BIT;
    private final List<DeferredReviveLabel> deferredLabels = new ArrayList<DeferredReviveLabel>();

    public static void bootstrap() {
        MinecraftForge.EVENT_BUS.register(INSTANCE);
    }

    public static void beginDeferredRenderFrame() {
        INSTANCE.deferredLabels.clear();
    }

    public static void renderDeferred() {
        for (DeferredReviveLabel deferred : INSTANCE.deferredLabels) {
            if (!deferred.entity.isDead) {
                INSTANCE.renderReviveLabel(
                        deferred.entity,
                        deferred.x,
                        deferred.y,
                        deferred.z
                );
            }
        }
        INSTANCE.deferredLabels.clear();
    }

    @SubscribeEvent
    public void onRenderLivingPost(RenderLivingEvent.Post event) {
        if (!ModConfig.showPetKnockdownTimeoutAboveHead || !(event.entity instanceof EntityLivingBase)) {
            return;
        }

        EntityLivingBase entity = (EntityLivingBase) event.entity;
        if (PostProcessRenderer.shouldDeferBloomExcludedWorldOverlays()) {
            this.deferredLabels.add(new DeferredReviveLabel(entity, event.x, event.y, event.z));
            return;
        }
        renderReviveLabel(entity, event.x, event.y, event.z);
    }

    private void renderReviveLabel(EntityLivingBase entity, double x, double y, double z) {
        int remainingTicks = PetKnockdownTimeout.getClientRemainingTicks(entity);
        if (remainingTicks < 0) {
            return;
        }

        Minecraft mc = Minecraft.getMinecraft();
        if (mc == null || mc.thePlayer == null) {
            return;
        }
        Entity view = mc.renderViewEntity != null ? mc.renderViewEntity : mc.thePlayer;
        if (entity.getDistanceSqToEntity(view) > MAX_DISTANCE * MAX_DISTANCE) {
            return;
        }

        renderLabel(
                "Revive: " + formatRemainingTime(remainingTicks),
                x,
                y + (double) entity.height + (double) ModConfig.petKnockdownReviveTextHeightOffset,
                z
        );
    }

    private static String formatRemainingTime(int remainingTicks) {
        int totalSeconds = Math.max(0, (remainingTicks + 19) / 20);
        int hours = totalSeconds / 3600;
        int minutes = totalSeconds % 3600 / 60;
        int seconds = totalSeconds % 60;
        if (hours > 0) {
            return String.format("%d:%02d:%02d", hours, minutes, seconds);
        }
        return String.format("%d:%02d", minutes, seconds);
    }

    private static void renderLabel(String text, double x, double y, double z) {
        Minecraft mc = Minecraft.getMinecraft();
        FontRenderer font = mc.fontRenderer;
        RenderManager renderManager = RenderManager.instance;
        int previousActiveTexture = GL11.glGetInteger(GL13.GL_ACTIVE_TEXTURE);
        int previousMatrixMode = GL11.glGetInteger(GL11.GL_MATRIX_MODE);
        float previousBrightnessX = OpenGlHelper.lastBrightnessX;
        float previousBrightnessY = OpenGlHelper.lastBrightnessY;

        OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glPushAttrib(ATTRIB_MASK);
        GL11.glPushMatrix();
        try {
            GL11.glTranslated(x, y, z);
            GL11.glNormal3f(0.0F, 1.0F, 0.0F);
            GL11.glRotatef(-renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
            GL11.glScalef(-LABEL_SCALE, -LABEL_SCALE, LABEL_SCALE);
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

            int halfWidth = font.getStringWidth(text) / 2;
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            GL11.glDepthMask(false);
            if (ModConfig.showPetKnockdownTimeoutTextBackground) {
                GL11.glDisable(GL11.GL_TEXTURE_2D);
                Tessellator tessellator = Tessellator.instance;
                tessellator.startDrawingQuads();
                tessellator.setColorRGBA_F(0.0F, 0.0F, 0.0F, 0.35F);
                tessellator.addVertex(-halfWidth - 2, -2, 0.0D);
                tessellator.addVertex(-halfWidth - 2, 9, 0.0D);
                tessellator.addVertex(halfWidth + 2, 9, 0.0D);
                tessellator.addVertex(halfWidth + 2, -2, 0.0D);
                tessellator.draw();
                GL11.glEnable(GL11.GL_TEXTURE_2D);
            }

            font.drawString(text, -halfWidth, 0, 0x80FFFFFF);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glDepthMask(true);
            font.drawString(text, -halfWidth, 0, 0xFFFFFFFF);
        } finally {
            GL11.glPopMatrix();
            GL11.glPopAttrib();
            OpenGlHelper.setLightmapTextureCoords(
                    OpenGlHelper.lightmapTexUnit,
                    previousBrightnessX,
                    previousBrightnessY
            );
            OpenGlHelper.setActiveTexture(previousActiveTexture);
            GL11.glMatrixMode(previousMatrixMode);
        }
    }

    private static final class DeferredReviveLabel {
        private final EntityLivingBase entity;
        private final double x;
        private final double y;
        private final double z;

        private DeferredReviveLabel(EntityLivingBase entity, double x, double y, double z) {
            this.entity = entity;
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }
}
