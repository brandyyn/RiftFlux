/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.ItemRenderer
 *  net.minecraft.client.renderer.Tessellator
 *  net.minecraft.client.renderer.entity.Render
 *  net.minecraft.entity.Entity
 *  net.minecraft.util.ResourceLocation
 *  org.lwjgl.opengl.GL11
 */
package net.nmccoy.legendgear.render;

import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.nmccoy.legendgear.entity.EntityHeart;
import org.lwjgl.opengl.GL11;

public class RenderHeart
extends Render {
    private static final ResourceLocation heartTexture = new ResourceLocation("legendgear", "textures/items/itemHeart.png");

    public RenderHeart() {
        this.shadowSize = 0.15f;
        this.shadowOpaque = 0.75f;
    }

    public void doRender(Entity p_76986_1_, double x, double y, double z, float p_76986_8_, float subtick) {
        Tessellator tess = Tessellator.instance;
        EntityHeart eh = (EntityHeart)p_76986_1_;
        if (eh.lifeTicksLeft < 70 && eh.lifeTicksLeft % 2 < 1) {
            return;
        }
        GL11.glPushMatrix();
        GL11.glTranslated((double)x, (double)y, (double)z);
        GL11.glRotated((double)((subtick - (float)eh.lifeTicksLeft) * 5.0f), (double)0.0, (double)1.0, (double)0.0);
        float f9 = 0.0625f;
        GL11.glScaled((double)0.5, (double)0.5, (double)0.5);
        GL11.glTranslated((double)(-0.5 - (double)(f9 / 2.0f)), (double)0.0, (double)(f9 / 2.0f));
        this.bindTexture(heartTexture);
        ItemRenderer.renderItemIn2D((Tessellator)tess, (float)0.0f, (float)0.0f, (float)1.0f, (float)1.0f, (int)32, (int)32, (float)f9);
        GL11.glPopMatrix();
    }

    protected ResourceLocation getEntityTexture(Entity p_110775_1_) {
        return heartTexture;
    }
}

