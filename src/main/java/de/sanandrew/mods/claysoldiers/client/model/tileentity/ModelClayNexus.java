/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.opengl.GL11
 */
package de.sanandrew.mods.claysoldiers.client.model.tileentity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.sanandrew.core.manpack.util.javatuples.Triplet;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import org.lwjgl.opengl.GL11;

@SideOnly(value=Side.CLIENT)
public class ModelClayNexus
extends ModelBase {
    public ModelRenderer base1;
    public ModelRenderer base2;
    public ModelRenderer base3;
    public ModelRenderer armBR1;
    public ModelRenderer armTR1;
    public ModelRenderer armBL1;
    public ModelRenderer armTL1;
    public ModelRenderer armBR2;
    public ModelRenderer armTR2;
    public ModelRenderer armBL2;
    public ModelRenderer armTL2;
    public ModelRenderer armBR3;
    public ModelRenderer armTR3;
    public ModelRenderer armBL3;
    public ModelRenderer armTL3;

    public ModelClayNexus() {
        this.textureWidth = 32;
        this.textureHeight = 32;
        float degree45 = 0.7853982f;
        this.base1 = new ModelRenderer(this, 0, 0);
        this.base1.addBox(-4.0f, 0.0f, -4.0f, 8, 1, 8);
        this.base1.setRotationPoint(0.0f, 23.0f, 0.0f);
        this.base1.setTextureSize(32, 32);
        ModelClayNexus.setRotation(this.base1, 0.0f, 0.0f, 0.0f);
        this.base2 = new ModelRenderer(this, 0, 9);
        this.base2.addBox(-2.0f, -1.0f, -2.0f, 4, 1, 4);
        this.base2.setRotationPoint(0.0f, 23.0f, 0.0f);
        this.base2.setTextureSize(32, 32);
        ModelClayNexus.setRotation(this.base2, 0.0f, 0.0f, 0.0f);
        this.base3 = new ModelRenderer(this, 0, 21);
        this.base3.addBox(-0.5f, -2.0f, -0.5f, 1, 2, 1);
        this.base3.setRotationPoint(0.0f, 22.0f, 0.0f);
        this.base3.setTextureSize(32, 32);
        ModelClayNexus.setRotation(this.base3, 0.0f, 0.0f, 0.0f);
        this.armBR1 = new ModelRenderer(this, 0, 14);
        this.armBR1.addBox(-0.5f, -5.0f, -0.5f, 1, 6, 1);
        this.armBR1.setRotationPoint(3.0f, 23.0f, -3.0f);
        this.armBR1.setTextureSize(32, 32);
        ModelClayNexus.setRotation(this.armBR1, degree45, -degree45, 0.0f);
        this.armTR1 = new ModelRenderer(this, 4, 14);
        this.armTR1.addBox(-0.5f, -5.0f, -0.5f, 1, 6, 1);
        this.armTR1.setRotationPoint(3.0f, 23.0f, 3.0f);
        this.armTR1.setTextureSize(32, 32);
        ModelClayNexus.setRotation(this.armTR1, -degree45, degree45, 0.0f);
        this.armBL1 = new ModelRenderer(this, 8, 14);
        this.armBL1.addBox(-0.5f, -5.0f, -0.5f, 1, 6, 1);
        this.armBL1.setRotationPoint(-3.0f, 23.0f, -3.0f);
        this.armBL1.setTextureSize(32, 32);
        ModelClayNexus.setRotation(this.armBL1, degree45, degree45, 0.0f);
        this.armTL1 = new ModelRenderer(this, 12, 14);
        this.armTL1.addBox(-0.5f, -5.0f, -0.5f, 1, 6, 1);
        this.armTL1.setRotationPoint(-3.0f, 23.0f, 3.0f);
        this.armTL1.setTextureSize(32, 32);
        ModelClayNexus.setRotation(this.armTL1, -degree45, -degree45, 0.0f);
        this.armBR2 = new ModelRenderer(this, 16, 9);
        this.armBR2.addBox(-0.5f, -5.0f, -0.45f, 1, 7, 1);
        this.armBR2.setRotationPoint(5.0f, 18.0f, -5.0f);
        this.armBR2.setTextureSize(32, 32);
        ModelClayNexus.setRotation(this.armBR2, -degree45, -degree45, 0.0f);
        this.armTR2 = new ModelRenderer(this, 20, 9);
        this.armTR2.addBox(-0.5f, -5.0f, -0.55f, 1, 7, 1);
        this.armTR2.setRotationPoint(5.0f, 18.0f, 5.0f);
        this.armTR2.setTextureSize(32, 32);
        ModelClayNexus.setRotation(this.armTR2, degree45, degree45, 0.0f);
        this.armBL2 = new ModelRenderer(this, 24, 9);
        this.armBL2.addBox(-0.5f, -5.0f, -0.45f, 1, 7, 1);
        this.armBL2.setRotationPoint(-5.0f, 18.0f, -5.0f);
        this.armBL2.setTextureSize(32, 32);
        ModelClayNexus.setRotation(this.armBL2, -degree45, degree45, 0.0f);
        this.armTL2 = new ModelRenderer(this, 28, 9);
        this.armTL2.addBox(-0.5f, -5.0f, -0.55f, 1, 7, 1);
        this.armTL2.setRotationPoint(-5.0f, 18.0f, 5.0f);
        this.armTL2.setTextureSize(32, 32);
        ModelClayNexus.setRotation(this.armTL2, degree45, -degree45, 0.0f);
        this.armBR3 = new ModelRenderer(this, 16, 17);
        this.armBR3.addBox(-0.5f, 1.05f, -0.55f, 1, 2, 1);
        this.armBR3.setRotationPoint(3.0f, 13.0f, -3.0f);
        this.armBR3.setTextureSize(32, 32);
        ModelClayNexus.setRotation(this.armBR3, degree45, -degree45, 0.0f);
        this.armTR3 = new ModelRenderer(this, 20, 17);
        this.armTR3.addBox(-0.5f, 1.05f, -0.45f, 1, 2, 1);
        this.armTR3.setRotationPoint(3.0f, 13.0f, 3.0f);
        this.armTR3.setTextureSize(32, 32);
        ModelClayNexus.setRotation(this.armTR3, -degree45, degree45, 0.0f);
        this.armBL3 = new ModelRenderer(this, 24, 17);
        this.armBL3.addBox(-0.5f, 1.05f, -0.55f, 1, 2, 1);
        this.armBL3.setRotationPoint(-3.0f, 13.0f, -3.0f);
        this.armBL3.setTextureSize(32, 32);
        ModelClayNexus.setRotation(this.armBL3, degree45, degree45, 0.0f);
        this.armTL3 = new ModelRenderer(this, 28, 17);
        this.armTL3.addBox(-0.5f, 1.05f, -0.45f, 1, 2, 1);
        this.armTL3.setRotationPoint(-3.0f, 13.0f, 3.0f);
        this.armTL3.setTextureSize(32, 32);
        ModelClayNexus.setRotation(this.armTL3, -degree45, -degree45, 0.0f);
    }

    public void renderTileEntity() {
        this.base1.render(0.0625f);
        this.base2.render(0.0625f);
        this.base3.render(0.0625f);
        this.armBR1.render(0.0625f);
        this.armTR1.render(0.0625f);
        this.armBL1.render(0.0625f);
        this.armTL1.render(0.0625f);
        this.armBR2.render(0.0625f);
        this.armTR2.render(0.0625f);
        this.armBL2.render(0.0625f);
        this.armTL2.render(0.0625f);
        this.armBR3.render(0.0625f);
        this.armTR3.render(0.0625f);
        this.armBL3.render(0.0625f);
        this.armTL3.render(0.0625f);
    }

    public void renderTileEntityGlowmap() {
        ModelClayNexus.renderScaledBox(this.base1, 0.0625f);
        ModelClayNexus.renderScaledBox(this.base2, 0.0625f);
        ModelClayNexus.renderScaledBox(this.base3, 0.0625f);
        ModelClayNexus.renderScaledBox(this.armBR1, 0.0625f);
        ModelClayNexus.renderScaledBox(this.armTR1, 0.0625f);
        ModelClayNexus.renderScaledBox(this.armBL1, 0.0625f);
        ModelClayNexus.renderScaledBox(this.armTL1, 0.0625f);
        ModelClayNexus.renderScaledBox(this.armBR2, 0.0625f);
        ModelClayNexus.renderScaledBox(this.armTR2, 0.0625f);
        ModelClayNexus.renderScaledBox(this.armBL2, 0.0625f);
        ModelClayNexus.renderScaledBox(this.armTL2, 0.0625f);
        ModelClayNexus.renderScaledBox(this.armBR3, 0.0625f);
        ModelClayNexus.renderScaledBox(this.armTR3, 0.0625f);
        ModelClayNexus.renderScaledBox(this.armBL3, 0.0625f);
        ModelClayNexus.renderScaledBox(this.armTL3, 0.0625f);
    }

    private static void renderScaledBox(ModelRenderer box, float scaleFactor) {
        Triplet<Float, Float, Float> rot = Triplet.with(Float.valueOf(box.rotateAngleX), Float.valueOf(box.rotateAngleY), Float.valueOf(box.rotateAngleZ));
        Triplet<Float, Float, Float> point = Triplet.with(Float.valueOf(box.rotationPointX), Float.valueOf(box.rotationPointY), Float.valueOf(box.rotationPointZ));
        ModelClayNexus.setRotation(box, 0.0f, 0.0f, 0.0f);
        box.rotationPointX = 0.0f;
        box.rotationPointY = 0.0f;
        box.rotationPointZ = 0.0f;
        GL11.glPushMatrix();
        GL11.glTranslatef((float)(point.getValue0().floatValue() * scaleFactor), (float)(point.getValue1().floatValue() * scaleFactor), (float)(point.getValue2().floatValue() * scaleFactor));
        GL11.glRotatef((float)(rot.getValue2().floatValue() * 57.295776f), (float)0.0f, (float)0.0f, (float)1.0f);
        GL11.glRotatef((float)(rot.getValue1().floatValue() * 57.295776f), (float)0.0f, (float)1.0f, (float)0.0f);
        GL11.glRotatef((float)(rot.getValue0().floatValue() * 57.295776f), (float)1.0f, (float)0.0f, (float)0.0f);
        GL11.glScalef((float)1.01f, (float)1.01f, (float)1.01f);
        GL11.glTranslatef((float)0.0f, (float)-7.5E-4f, (float)0.0f);
        box.render(scaleFactor);
        GL11.glPopMatrix();
        ModelClayNexus.setRotation(box, rot.getValue0().floatValue(), rot.getValue1().floatValue(), rot.getValue2().floatValue());
        box.rotationPointX = point.getValue0().floatValue();
        box.rotationPointY = point.getValue1().floatValue();
        box.rotationPointZ = point.getValue2().floatValue();
    }

    private static void setRotation(ModelRenderer model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }
}

