package com.voidsrift.riftflux.avatar.glider;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderGliderActive extends Render {
    private ModelGlider2 model = new ModelGlider2();

    @Override
    public void doRender(Entity ent, double d0, double d1, double d2, float f, float f1) {
        EntityGlider entity = (EntityGlider) ent;
        entity.renderDistanceWeight = 300.0;
        Minecraft mc = Minecraft.getMinecraft();
        GL11.glPushMatrix();
        Entity base = entity.ridingEntity != null ? entity.ridingEntity : entity;
        double interpX = base.lastTickPosX + (base.posX - base.lastTickPosX) * f1;
        double interpY = base.lastTickPosY + (base.posY - base.lastTickPosY) * f1;
        double interpZ = base.lastTickPosZ + (base.posZ - base.lastTickPosZ) * f1;
        GL11.glTranslated(
                interpX - this.renderManager.renderPosX,
                interpY - this.renderManager.renderPosY,
                interpZ - this.renderManager.renderPosZ
        );
        float heightOffset = 1.85f - base.height;
        GL11.glTranslatef(0.0f, heightOffset, 0.0f);
        try {
            GL11.glRotatef(180.0f, 0.0f, 0.0f, 1.0f);
            GL11.glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
            float yaw = interpAngle((float) entity.prevRotationYAW, (float) entity.rotationYAW, f1);
            float roll = interpAngle((float) entity.prevRotationRoll, (float) entity.rotationRoll, f1);
            float pitch = interpAngle((float) entity.prevRotationPitch, (float) entity.rotationPitch, f1);
            if (Math.abs(entity.ridingEntity.motionX) < 0.01 && Math.abs(entity.ridingEntity.motionZ) < 0.01) {
                GL11.glRotatef(yaw, 0.0f, 1.0f, 0.0f);
            } else {
                GL11.glRotatef(yaw, 0.0f, 1.0f, 0.0f);
                GL11.glRotatef(roll, 0.0f, 0.0f, 1.0f);
            }
            GL11.glRotatef(pitch, 1.0f, 0.0f, 0.0f);
        } catch (Throwable ex) {
            entity.setDead();
            GL11.glPopMatrix();
            return;
        }
        float errr = -0.03125f;
        GL11.glTranslatef(errr, -0.4f, 0.4f);
        GL11.glScalef(1.2f, 1.2f, 1.2f);
        try {
            mc.renderEngine.bindTexture(new ResourceLocation(((EntityGlider) ent).getTextureFile()));
            this.model.render(entity, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0625f);
        } catch (Throwable ex) {
            // ignore
        }
        GL11.glPopMatrix();
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        return null;
    }

    private static float interpAngle(float prev, float current, float partial) {
        float delta = current - prev;
        while (delta < -180.0f) {
            delta += 360.0f;
        }
        while (delta >= 180.0f) {
            delta -= 360.0f;
        }
        return prev + partial * delta;
    }
}
