package com.voidsrift.riftflux.terramine;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class RenderDestroyerLaser extends Render {
    private static final ResourceLocation LASER_RED = new ResourceLocation("riftflux:textures/entities/destroyer/laser.png");
    private static final ResourceLocation LASER_BLUE = new ResourceLocation("riftflux:textures/entities/destroyer/blueLaser.png");

    @Override
    public void doRender(Entity entity, double x, double y, double z, float yaw, float partialTicks) {
        GL11.glPushMatrix();
        try {
            this.bindEntityTexture(entity);
            GL11.glTranslatef((float) x, (float) y, (float) z);
            GL11.glRotatef(entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks - 90.0F, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks, 0.0F, 0.0F, 1.0F);

            Tessellator tessellator = Tessellator.instance;
            float uMin = 0.0F;
            float uMax = 0.5F;
            float sideVMin = 0.0F;
            float sideVMax = 0.15625F;
            float frontVMin = 0.15625F;
            float frontVMax = 0.3125F;
            float size = 0.125F;

            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
            if (entity instanceof EntityDestroyerLaser) {
                float shake = ((EntityDestroyerLaser) entity).getArrowShake() - partialTicks;
                if (shake > 0.0F) {
                    float roll = -MathHelper.sin(shake * 3.0F) * shake;
                    GL11.glRotatef(roll, 0.0F, 0.0F, 1.0F);
                }
            }

            GL11.glRotatef(45.0F, 1.0F, 0.0F, 0.0F);
            GL11.glScalef(size, size, size);
            GL11.glTranslatef(-4.0F, 0.0F, 0.0F);
            GL11.glNormal3f(size, 0.0F, 0.0F);
            tessellator.startDrawingQuads();
            tessellator.addVertexWithUV(-7.0D, -2.0D, -2.0D, sideVMin, frontVMin);
            tessellator.addVertexWithUV(-7.0D, -2.0D, 2.0D, sideVMax, frontVMin);
            tessellator.addVertexWithUV(-7.0D, 2.0D, 2.0D, sideVMax, frontVMax);
            tessellator.addVertexWithUV(-7.0D, 2.0D, -2.0D, sideVMin, frontVMax);
            tessellator.draw();

            GL11.glNormal3f(-size, 0.0F, 0.0F);
            tessellator.startDrawingQuads();
            tessellator.addVertexWithUV(-7.0D, 2.0D, -2.0D, sideVMin, frontVMin);
            tessellator.addVertexWithUV(-7.0D, 2.0D, 2.0D, sideVMax, frontVMin);
            tessellator.addVertexWithUV(-7.0D, -2.0D, 2.0D, sideVMax, frontVMax);
            tessellator.addVertexWithUV(-7.0D, -2.0D, -2.0D, sideVMin, frontVMax);
            tessellator.draw();

            for (int i = 0; i < 4; ++i) {
                GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
                GL11.glNormal3f(0.0F, 0.0F, size);
                tessellator.startDrawingQuads();
                tessellator.addVertexWithUV(-8.0D, -2.0D, 0.0D, uMin, sideVMin);
                tessellator.addVertexWithUV(8.0D, -2.0D, 0.0D, uMax, sideVMin);
                tessellator.addVertexWithUV(8.0D, 2.0D, 0.0D, uMax, sideVMax);
                tessellator.addVertexWithUV(-8.0D, 2.0D, 0.0D, uMin, sideVMax);
                tessellator.draw();
            }
        } finally {
            GL11.glDisable(GL12.GL_RESCALE_NORMAL);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glPopMatrix();
        }
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        return entity instanceof EntityDestroyerLaser && ((EntityDestroyerLaser) entity).isStrong() ? LASER_BLUE : LASER_RED;
    }
}