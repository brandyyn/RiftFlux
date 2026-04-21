package com.voidsrift.riftflux.duckling;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.EntityLiving;
import org.lwjgl.opengl.GL11;

final class GeoLeashRenderer {
    private static final double DEGREES_TO_RADIANS = 0.01745329238474369D;
    private static final double LEASH_START_Y_OFFSET = 0.5D;

    private GeoLeashRenderer() {
    }

    public static void renderLeash(EntityLiving entity, double x, double y, double z, float partialTicks) {
        Entity leashHolder = entity.getLeashedToEntity();
        if (leashHolder == null) {
            return;
        }

        y -= (1.6D - (double)entity.height) * 0.5D;
        Tessellator tessellator = Tessellator.instance;

        double leashYaw = interpolateValue((double)leashHolder.prevRotationYaw, (double)leashHolder.rotationYaw, (double)(partialTicks * 0.5F)) * DEGREES_TO_RADIANS;
        double leashPitch = interpolateValue((double)leashHolder.prevRotationPitch, (double)leashHolder.rotationPitch, (double)(partialTicks * 0.5F)) * DEGREES_TO_RADIANS;
        double leashCosYaw = Math.cos(leashYaw);
        double leashSinYaw = Math.sin(leashYaw);
        double leashSinPitch = Math.sin(leashPitch);

        if (leashHolder instanceof EntityHanging) {
            leashCosYaw = 0.0D;
            leashSinYaw = 0.0D;
            leashSinPitch = -1.0D;
        }

        double leashCosPitch = Math.cos(leashPitch);
        double holderX = interpolateValue(leashHolder.prevPosX, leashHolder.posX, (double)partialTicks)
                - leashCosYaw * 0.7D
                - leashSinYaw * 0.5D * leashCosPitch;
        double holderY = interpolateValue(
                leashHolder.prevPosY + (double)leashHolder.getEyeHeight() * 0.7D,
                leashHolder.posY + (double)leashHolder.getEyeHeight() * 0.7D,
                (double)partialTicks)
                - leashSinPitch * 0.5D
                - 0.25D;
        double holderZ = interpolateValue(leashHolder.prevPosZ, leashHolder.posZ, (double)partialTicks)
                - leashSinYaw * 0.7D
                + leashCosYaw * 0.5D * leashCosPitch;

        double bodyYaw = interpolateValue((double)entity.prevRenderYawOffset, (double)entity.renderYawOffset, (double)partialTicks) * DEGREES_TO_RADIANS + (Math.PI / 2D);
        double startOffsetX = Math.cos(bodyYaw) * (double)entity.width * 0.4D;
        double startOffsetZ = Math.sin(bodyYaw) * (double)entity.width * 0.4D;
        double entityX = interpolateValue(entity.prevPosX, entity.posX, (double)partialTicks) + startOffsetX;
        double entityY = interpolateValue(entity.prevPosY, entity.posY, (double)partialTicks) - LEASH_START_Y_OFFSET;
        double entityZ = interpolateValue(entity.prevPosZ, entity.posZ, (double)partialTicks) + startOffsetZ;

        x += startOffsetX;
        y -= LEASH_START_Y_OFFSET;
        z += startOffsetZ;

        double deltaX = (double)((float)(holderX - entityX));
        double deltaY = (double)((float)(holderY - entityY));
        double deltaZ = (double)((float)(holderZ - entityZ));

        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_CULL_FACE);

        tessellator.startDrawing(5);
        for (int i = 0; i <= 24; ++i) {
            float red = 0.5F;
            float green = 0.4F;
            float blue = 0.3F;

            if (i % 2 == 0) {
                red *= 0.7F;
                green *= 0.7F;
                blue *= 0.7F;
            }

            float progress = (float)i / 24.0F;
            tessellator.setColorRGBA_F(red, green, blue, 1.0F);
            tessellator.addVertex(
                    x + deltaX * (double)progress,
                    y + deltaY * (double)(progress * progress + progress) * 0.5D + (double)((24.0F - (float)i) / 18.0F + 0.125F),
                    z + deltaZ * (double)progress);
            tessellator.setColorRGBA_F(red, green, blue, 1.0F);
            tessellator.addVertex(
                    x + deltaX * (double)progress + 0.025D,
                    y + deltaY * (double)(progress * progress + progress) * 0.5D + (double)((24.0F - (float)i) / 18.0F + 0.125F) + 0.025D,
                    z + deltaZ * (double)progress);
        }
        tessellator.draw();

        tessellator.startDrawing(5);
        for (int i = 0; i <= 24; ++i) {
            float red = 0.5F;
            float green = 0.4F;
            float blue = 0.3F;

            if (i % 2 == 0) {
                red *= 0.7F;
                green *= 0.7F;
                blue *= 0.7F;
            }

            float progress = (float)i / 24.0F;
            tessellator.setColorRGBA_F(red, green, blue, 1.0F);
            tessellator.addVertex(
                    x + deltaX * (double)progress,
                    y + deltaY * (double)(progress * progress + progress) * 0.5D + (double)((24.0F - (float)i) / 18.0F + 0.125F) + 0.025D,
                    z + deltaZ * (double)progress);
            tessellator.setColorRGBA_F(red, green, blue, 1.0F);
            tessellator.addVertex(
                    x + deltaX * (double)progress + 0.025D,
                    y + deltaY * (double)(progress * progress + progress) * 0.5D + (double)((24.0F - (float)i) / 18.0F + 0.125F),
                    z + deltaZ * (double)progress + 0.025D);
        }
        tessellator.draw();

        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_CULL_FACE);
    }

    private static double interpolateValue(double previous, double current, double partialTicks) {
        return previous + (current - previous) * partialTicks;
    }
}
