/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.opengl.GL11
 */
package de.sanandrew.core.manpack.util.client.helpers;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.sanandrew.core.manpack.mod.client.particle.SAPEffectRenderer;
import de.sanandrew.core.manpack.util.client.EntityParticle;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

@SideOnly(value=Side.CLIENT)
public final class SAPClientUtils {
    public static int registerNewFXLayer(ResourceLocation resource, boolean hasAlpha) {
        return SAPEffectRenderer.INSTANCE.registerFxLayer(resource, hasAlpha);
    }

    public static void spawnParticle(EntityParticle particle) {
        SAPEffectRenderer.INSTANCE.addEffect(particle);
    }

    public static void drawTexturedSquareYPos(double cornerBeginX, double cornerBeginZ, double cornerEndX, double cornerEndZ, double y, double uBegin, double vBegin, double uEnd, double vEnd) {
        Tessellator tess = Tessellator.instance;
        tess.startDrawingQuads();
        tess.setNormal(0.0f, 1.0f, 0.0f);
        tess.addVertexWithUV(cornerBeginX, y, cornerBeginZ, uBegin, vBegin);
        tess.addVertexWithUV(cornerBeginX, y, cornerEndZ, uBegin, vEnd);
        tess.addVertexWithUV(cornerEndX, y, cornerEndZ, uEnd, vEnd);
        tess.addVertexWithUV(cornerEndX, y, cornerBeginZ, uEnd, vBegin);
        tess.draw();
    }

    public static void drawTexturedSquareYNeg(double cornerBeginX, double cornerBeginZ, double cornerEndX, double cornerEndZ, double y, double uBegin, double vBegin, double uEnd, double vEnd) {
        Tessellator tess = Tessellator.instance;
        tess.startDrawingQuads();
        tess.setNormal(0.0f, -1.0f, 0.0f);
        tess.addVertexWithUV(cornerBeginX, y, cornerBeginZ, uBegin, vBegin);
        tess.addVertexWithUV(cornerEndX, y, cornerBeginZ, uEnd, vBegin);
        tess.addVertexWithUV(cornerEndX, y, cornerEndZ, uEnd, vEnd);
        tess.addVertexWithUV(cornerBeginX, y, cornerEndZ, uBegin, vEnd);
        tess.draw();
    }

    public static void drawTexturedSquareXPos(double cornerBeginY, double cornerBeginZ, double cornerEndY, double cornerEndZ, double x, double uBegin, double vBegin, double uEnd, double vEnd) {
        Tessellator tess = Tessellator.instance;
        tess.startDrawingQuads();
        tess.setNormal(1.0f, 0.0f, 0.0f);
        tess.addVertexWithUV(x, cornerBeginY, cornerBeginZ, uEnd, vEnd);
        tess.addVertexWithUV(x, cornerEndY, cornerBeginZ, uEnd, vBegin);
        tess.addVertexWithUV(x, cornerEndY, cornerEndZ, uBegin, vBegin);
        tess.addVertexWithUV(x, cornerBeginY, cornerEndZ, uBegin, vEnd);
        tess.draw();
    }

    public static void drawTexturedSquareXNeg(double cornerBeginY, double cornerBeginZ, double cornerEndY, double cornerEndZ, double x, double uBegin, double vBegin, double uEnd, double vEnd) {
        Tessellator tess = Tessellator.instance;
        tess.startDrawingQuads();
        tess.setNormal(-1.0f, 0.0f, 0.0f);
        tess.addVertexWithUV(x, cornerBeginY, cornerBeginZ, uBegin, vEnd);
        tess.addVertexWithUV(x, cornerBeginY, cornerEndZ, uEnd, vEnd);
        tess.addVertexWithUV(x, cornerEndY, cornerEndZ, uEnd, vBegin);
        tess.addVertexWithUV(x, cornerEndY, cornerBeginZ, uBegin, vBegin);
        tess.draw();
    }

    public static void drawTexturedSquareZPos(double cornerBeginX, double cornerBeginY, double cornerEndX, double cornerEndY, double z, double uBegin, double vBegin, double uEnd, double vEnd) {
        Tessellator tess = Tessellator.instance;
        tess.startDrawingQuads();
        tess.setNormal(0.0f, 0.0f, 1.0f);
        tess.addVertexWithUV(cornerBeginX, cornerBeginY, z, uBegin, vEnd);
        tess.addVertexWithUV(cornerEndX, cornerBeginY, z, uEnd, vEnd);
        tess.addVertexWithUV(cornerEndX, cornerEndY, z, uEnd, vBegin);
        tess.addVertexWithUV(cornerBeginX, cornerEndY, z, uBegin, vBegin);
        tess.draw();
    }

    public static void drawTexturedSquareZNeg(double cornerBeginX, double cornerBeginY, double cornerEndX, double cornerEndY, double z, double uBegin, double vBegin, double uEnd, double vEnd) {
        Tessellator tess = Tessellator.instance;
        tess.startDrawingQuads();
        tess.setNormal(0.0f, 0.0f, -1.0f);
        tess.addVertexWithUV(cornerBeginX, cornerBeginY, z, uEnd, vEnd);
        tess.addVertexWithUV(cornerBeginX, cornerEndY, z, uEnd, vBegin);
        tess.addVertexWithUV(cornerEndX, cornerEndY, z, uBegin, vBegin);
        tess.addVertexWithUV(cornerEndX, cornerBeginY, z, uBegin, vEnd);
        tess.draw();
    }

    public static void drawSquareXNeg(double cornerBeginY, double cornerBeginZ, double cornerEndY, double cornerEndZ, double x) {
        Tessellator tess = Tessellator.instance;
        GL11.glEnable((int)3042);
        GL11.glDisable((int)3553);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        tess.startDrawingQuads();
        tess.setNormal(-1.0f, 0.0f, 0.0f);
        tess.addVertex(x, cornerBeginY, cornerBeginZ);
        tess.addVertex(x, cornerBeginY, cornerEndZ);
        tess.addVertex(x, cornerEndY, cornerEndZ);
        tess.addVertex(x, cornerEndY, cornerBeginZ);
        tess.draw();
        GL11.glEnable((int)3553);
        GL11.glDisable((int)3042);
    }

    public static void drawSquareZPos(double cornerBeginX, double cornerBeginY, double cornerEndX, double cornerEndY, double z) {
        Tessellator tess = Tessellator.instance;
        GL11.glEnable((int)3042);
        GL11.glDisable((int)3553);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        tess.startDrawingQuads();
        tess.setNormal(0.0f, 0.0f, 1.0f);
        tess.addVertex(cornerBeginX, cornerBeginY, z);
        tess.addVertex(cornerEndX, cornerBeginY, z);
        tess.addVertex(cornerEndX, cornerEndY, z);
        tess.addVertex(cornerBeginX, cornerEndY, z);
        tess.draw();
        GL11.glEnable((int)3553);
        GL11.glDisable((int)3042);
    }

    @Deprecated
    public static ModelRenderer createNewBox(ModelBase model, int texX, int texY, boolean mirror, float boxX, float boxY, float boxZ, int sizeX, int sizeY, int sizeZ, float scaleFactor, float rotPointX, float rotPointY, float rotPointZ, float rotX, float rotY, float rotZ) {
        return null;
    }
}

