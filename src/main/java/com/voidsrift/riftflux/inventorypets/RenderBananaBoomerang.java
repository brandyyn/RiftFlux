package com.voidsrift.riftflux.inventorypets;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class RenderBananaBoomerang extends Render {
    private static final ResourceLocation TEXTURE = new ResourceLocation("riftflux:textures/entity/inventorypets/nana.png");
    private final ModelBananaBoomerang bananaModel = new ModelBananaBoomerang();

    private float interpolateRotation(float previous, float current, float partialTick) {
        float delta;
        for (delta = current - previous; delta < -180.0F; delta += 360.0F) {
        }
        while (delta >= 180.0F) {
            delta -= 360.0F;
        }
        return previous + partialTick * delta;
    }

    @Override
    public void doRender(Entity entity, double x, double y, double z, float yaw, float partialTicks) {
        EntityBananaBoomerang banana = (EntityBananaBoomerang) entity;
        GL11.glPushMatrix();
        GL11.glDisable(GL11.GL_CULL_FACE);
        float renderYaw = this.interpolateRotation(banana.prevRotationYaw, banana.rotationYaw, 0.0F);
        float renderPitch = banana.prevRotationPitch + (banana.rotationPitch - banana.prevRotationPitch) * 0.0F;
        GL11.glTranslatef((float) x, (float) y, (float) z);
        float scale = 0.0625F;
        GL11.glEnable(32826);
        GL11.glScalef(-1.0F, -1.0F, 1.0F);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        float time = Minecraft.getMinecraft().theWorld.getWorldTime();
        GL11.glRotatef(time * 90.0F * -1.0F, 0.0F, 0.0F, 0.5F);
        this.bindEntityTexture(banana);
        this.bananaModel.render(banana, 0.0F, 0.0F, 0.0F, renderYaw, renderPitch, scale);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glMatrixMode(GL11.GL_TEXTURE);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glPopMatrix();
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        return TEXTURE;
    }
}
