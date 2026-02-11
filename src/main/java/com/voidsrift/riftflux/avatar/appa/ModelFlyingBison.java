package com.voidsrift.riftflux.avatar.appa;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;

public class ModelFlyingBison extends ModelBase {
    ModelRenderer head;
    ModelRenderer body;
    ModelRenderer leg1;
    ModelRenderer leg2;
    ModelRenderer leg3;
    ModelRenderer leg4;
    ModelRenderer leg5;
    ModelRenderer leg6;
    ModelRenderer body2;
    ModelRenderer Shape1;
    ModelRenderer Shape2;

    public ModelFlyingBison() {
        this.textureWidth = 256;
        this.textureHeight = 64;
        this.head = new ModelRenderer(this, 108, 0);
        this.head.addBox(-4.0f, -4.0f, -6.0f, 16, 9, 10);
        this.head.setRotationPoint(2.0f, 1.0f, -12.0f);
        this.head.setTextureSize(256, 64);
        this.head.mirror = true;
        this.setRotation(this.head, 0.0011085f, 0.0f, 0.0f);
        this.body = new ModelRenderer(this, 30, 0);
        this.body.addBox(-6.0f, -10.0f, -7.0f, 24, 36, 13);
        this.body.setRotationPoint(0.0f, 5.0f, 2.0f);
        this.body.setTextureSize(256, 64);
        this.body.mirror = true;
        this.setRotation(this.body, 1.570796f, 0.0f, 0.0f);
        this.leg1 = new ModelRenderer(this, 0, 0);
        this.leg1.addBox(-3.0f, 0.0f, -2.0f, 7, 14, 7);
        this.leg1.setRotationPoint(-3.0f, 12.0f, 20.0f);
        this.leg1.setTextureSize(256, 64);
        this.leg1.mirror = true;
        this.setRotation(this.leg1, 0.0f, 0.0f, 0.0f);
        this.leg2 = new ModelRenderer(this, 0, 0);
        this.leg2.addBox(-1.0f, 0.0f, -2.0f, 7, 14, 7);
        this.leg2.setRotationPoint(12.0f, 12.0f, 7.0f);
        this.leg2.setTextureSize(256, 64);
        this.leg2.mirror = true;
        this.setRotation(this.leg2, 0.0f, 0.0f, 0.0f);
        this.leg3 = new ModelRenderer(this, 0, 0);
        this.leg3.addBox(-3.0f, 0.0f, -3.0f, 7, 14, 7);
        this.leg3.setRotationPoint(-3.0f, 12.0f, -5.0f);
        this.leg3.setTextureSize(256, 64);
        this.leg3.mirror = true;
        this.setRotation(this.leg3, 0.0f, 0.0f, 0.0f);
        this.leg4 = new ModelRenderer(this, 0, 0);
        this.leg4.addBox(-1.0f, 0.0f, -3.0f, 7, 14, 7);
        this.leg4.setRotationPoint(12.0f, 12.0f, -5.0f);
        this.leg4.setTextureSize(256, 64);
        this.leg4.mirror = true;
        this.setRotation(this.leg4, 0.0f, 0.0f, 0.0f);
        this.leg5 = new ModelRenderer(this, 0, 0);
        this.leg5.addBox(-3.0f, 0.0f, -2.0f, 7, 14, 7);
        this.leg5.setRotationPoint(-3.0f, 12.0f, 7.0f);
        this.leg5.setTextureSize(256, 64);
        this.leg5.mirror = true;
        this.setRotation(this.leg5, 0.0f, 0.0f, 0.0f);
        this.leg6 = new ModelRenderer(this, 0, 0);
        this.leg6.addBox(-1.0f, 0.0f, -2.0f, 7, 14, 7);
        this.leg6.setRotationPoint(12.0f, 12.0f, 20.0f);
        this.leg6.setTextureSize(256, 64);
        this.leg6.mirror = true;
        this.setRotation(this.leg6, 0.0f, 0.0f, 0.0f);
        this.body2 = new ModelRenderer(this, 107, 28);
        this.body2.addBox(0.0f, 0.0f, 0.0f, 15, 28, 5);
        this.body2.setRotationPoint(-1.533333f, 3.333333f, 24.0f);
        this.body2.setTextureSize(256, 64);
        this.body2.mirror = true;
        this.setRotation(this.body2, 0.7479983f, 0.0f, 0.0f);
        this.Shape1 = new ModelRenderer(this, 0, 22);
        this.Shape1.addBox(0.0f, 0.0f, 0.0f, 2, 6, 3);
        this.Shape1.setRotationPoint(14.0f, -6.0f, -12.0f);
        this.Shape1.setTextureSize(256, 64);
        this.Shape1.mirror = true;
        this.setRotation(this.Shape1, -0.7853982f, 0.0f, 0.0f);
        this.Shape2 = new ModelRenderer(this, 0, 22);
        this.Shape2.addBox(0.0f, 0.0f, 0.0f, 2, 6, 3);
        this.Shape2.setRotationPoint(-4.0f, -6.0f, -12.0f);
        this.Shape2.setTextureSize(256, 64);
        this.Shape2.mirror = true;
        this.setRotation(this.Shape2, -0.7853982f, 0.0f, 0.0f);
    }

    public void render(Entity par1Entity, float par2, float par3, float par4, float par5, float par6, float par7) {
        float scale = 2.0f;
        GL11.glScalef(scale, scale, scale);
        GL11.glTranslatef(-0.4f, -1.0f, -0.4f);
        super.render(par1Entity, par2, par3, par4, par5, par6, par7);
        this.setRotationAngles(par2, par3, par4, par5, par6, par7, par1Entity);
        this.head.render(par7);
        this.body.render(par7);
        this.leg1.render(par7);
        this.leg2.render(par7);
        this.leg3.render(par7);
        this.leg4.render(par7);
        this.leg5.render(par7);
        this.leg6.render(par7);
        this.body2.render(par7);
        this.Shape1.render(par7);
        this.Shape2.render(par7);
    }

    private void setRotation(ModelRenderer model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }

    public void setRotationAngles(float par1, float par2, float par3, float par4, float par5, float par6, Entity par7Entity) {
        this.head.rotateAngleX = par5 / 57.295776f;
        this.head.rotateAngleY = par4 / 57.295776f;
        this.body.rotateAngleX = 1.5707964f;
        this.leg2.rotateAngleX = MathHelper.cos(par1 * 0.6662f) * 1.4f * par2;
        this.leg1.rotateAngleX = MathHelper.cos(par1 * 0.6662f) * 1.4f * par2;
        this.leg6.rotateAngleX = MathHelper.cos(par1 * 0.6662f + (float) Math.PI) * 1.4f * par2;
        this.leg4.rotateAngleX = MathHelper.cos(par1 * 0.6662f) * 1.4f * par2;
        this.leg5.rotateAngleX = MathHelper.cos(par1 * 0.6662f + (float) Math.PI) * 1.4f * par2;
        this.leg3.rotateAngleX = MathHelper.cos(par1 * 0.6662f + (float) Math.PI) * 1.4f * par2;
        this.body2.rotateAngleX = (float) (0.7479982972145081 + Math.toRadians(((EntityBison) par7Entity).tailAngle));
    }
}
