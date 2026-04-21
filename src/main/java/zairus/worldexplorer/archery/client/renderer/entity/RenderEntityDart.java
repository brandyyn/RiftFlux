/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.client.renderer.Tessellator
 *  net.minecraft.client.renderer.entity.Render
 *  net.minecraft.entity.Entity
 *  net.minecraft.util.ResourceLocation
 *  org.lwjgl.opengl.GL11
 */
package zairus.worldexplorer.archery.client.renderer.entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import zairus.worldexplorer.archery.entity.EntityDart;

@SideOnly(value=Side.CLIENT)
public class RenderEntityDart
extends Render {
    private static final ResourceLocation arrowTextures = new ResourceLocation("worldexplorer", "textures/model/dart_textures.png");

    public void doRender(EntityDart entity, double x, double y, double z, float p_76986_8_, float p_76986_9_) {
        GL11.glPushMatrix();
        this.bindTexture(arrowTextures);
        GL11.glTranslatef((float)((float)x), (float)((float)y), (float)((float)z));
        GL11.glEnable((int)32826);
        float var10 = 0.5f;
        GL11.glScalef((float)(var10 / 1.0f), (float)(var10 / 1.0f), (float)(var10 / 1.0f));
        GL11.glRotatef((float)(entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * p_76986_9_ - 90.0f), (float)0.0f, (float)1.0f, (float)0.0f);
        GL11.glRotatef((float)(entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * p_76986_9_), (float)0.0f, (float)0.0f, (float)1.0f);
        GL11.glRotatef((float)-90.0f, (float)0.0f, (float)0.0f, (float)1.0f);
        this.renderFace();
        GL11.glRotatef((float)90.0f, (float)0.0f, (float)1.0f, (float)0.0f);
        this.renderFace();
        GL11.glDisable((int)32826);
        GL11.glPopMatrix();
    }

    private void renderFace() {
        Tessellator var12 = Tessellator.instance;
        float var13 = 0.0f;
        float var14 = 1.0f;
        float var15 = 0.0f;
        float var16 = 1.0f;
        float minX = -0.5f;
        float maxX = 0.5f;
        float minY = -0.5f;
        float maxY = 0.5f;
        var12.startDrawingQuads();
        var12.setNormal(0.0f, 1.0f, 0.0f);
        var12.addVertexWithUV((double)minX, (double)minY, 0.0, (double)var13, (double)var16);
        var12.addVertexWithUV((double)maxX, (double)minY, 0.0, (double)var14, (double)var16);
        var12.addVertexWithUV((double)maxX, (double)maxY, 0.0, (double)var14, (double)var15);
        var12.addVertexWithUV((double)minX, (double)maxY, 0.0, (double)var13, (double)var15);
        var12.draw();
        var12.startDrawingQuads();
        var12.setNormal(0.0f, 1.0f, 0.0f);
        var12.addVertexWithUV((double)maxX, (double)minY, 0.0, (double)var14, (double)var16);
        var12.addVertexWithUV((double)minX, (double)minY, 0.0, (double)var13, (double)var16);
        var12.addVertexWithUV((double)minX, (double)maxY, 0.0, (double)var13, (double)var15);
        var12.addVertexWithUV((double)maxX, (double)maxY, 0.0, (double)var14, (double)var15);
        var12.draw();
    }

    protected ResourceLocation getEntityTexture(EntityDart p_110775_1_) {
        return arrowTextures;
    }

    protected ResourceLocation getEntityTexture(Entity p_110775_1_) {
        return this.getEntityTexture((EntityDart)p_110775_1_);
    }

    public void doRender(Entity p_76986_1_, double p_76986_2_, double p_76986_4_, double p_76986_6_, float p_76986_8_, float p_76986_9_) {
        this.doRender((EntityDart)p_76986_1_, p_76986_2_, p_76986_4_, p_76986_6_, p_76986_8_, p_76986_9_);
    }
}
