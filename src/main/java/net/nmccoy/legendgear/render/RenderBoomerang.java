/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.ItemRenderer
 *  net.minecraft.client.renderer.Tessellator
 *  net.minecraft.client.renderer.entity.Render
 *  net.minecraft.entity.Entity
 *  net.minecraft.util.IIcon
 *  net.minecraft.util.ResourceLocation
 *  org.lwjgl.opengl.GL11
 */
package net.nmccoy.legendgear.render;

import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.nmccoy.legendgear.LegendGear2;
import org.lwjgl.opengl.GL11;

public class RenderBoomerang
extends Render {
    public void doRender(Entity var1, double x, double y, double z, float var8x, float var9x) {
        Tessellator var5 = Tessellator.instance;
        GL11.glPushMatrix();
        GL11.glTranslatef((float)((float)x), (float)((float)y), (float)((float)z));
        GL11.glRotatef((float)(var1.prevRotationYaw + (var1.rotationYaw - var1.prevRotationYaw) * var9x - 90.0f), (float)0.0f, (float)1.0f, (float)0.0f);
        GL11.glRotatef((float)(var1.prevRotationPitch + (var1.rotationPitch - var1.prevRotationPitch) * var9x), (float)0.0f, (float)0.0f, (float)1.0f);
        GL11.glTranslatef((float)-0.5f, (float)-0.5f, (float)0.0f);
        IIcon icon = LegendGear2.magicBoomerang.getIconFromDamage(0);
        this.bindEntityTexture(var1);
        float f = icon.getMinU();
        float f1 = icon.getMaxU();
        float f2 = icon.getMinV();
        float f3 = icon.getMaxV();
        ItemRenderer.renderItemIn2D((Tessellator)Tessellator.instance, (float)f1, (float)f2, (float)f, (float)f3, (int)icon.getIconWidth(), (int)icon.getIconHeight(), (float)0.0625f);
        GL11.glPopMatrix();
    }

    protected ResourceLocation getEntityTexture(Entity p_110775_1_) {
        return this.renderManager.renderEngine.getResourceLocation(LegendGear2.magicBoomerang.getSpriteNumber());
    }
}

