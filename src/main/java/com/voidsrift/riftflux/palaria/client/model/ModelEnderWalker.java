package com.voidsrift.riftflux.palaria.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;

public class ModelEnderWalker
extends ModelBase {
    ModelRenderer body;
    ModelRenderer rightfrontleg1;
    ModelRenderer leftfrontleg1;
    ModelRenderer rightbackleg1;
    ModelRenderer leftbackleg1;
    ModelRenderer rightfrontleg2;
    ModelRenderer leftfrontleg2;
    ModelRenderer leftbackleg2;
    ModelRenderer rightbackleg2;
    ModelRenderer something;

    public ModelEnderWalker() {
        this.textureWidth = 64;
        this.textureHeight = 64;
        this.body = new ModelRenderer(this, 22, 6);
        this.body.addBox(-3.0f, -3.0f, -3.0f, 6, 6, 6);
        this.body.setRotationPoint(0.0f, 17.0f, 0.0f);
        this.body.setTextureSize(64, 64);
        this.body.mirror = true;
        this.setRotation(this.body, 0.0f, 0.0f, 0.0f);
        this.rightfrontleg1 = new ModelRenderer(this, 10, 10);
        this.rightfrontleg1.addBox(-0.5f, -8.0f, -0.5f, 1, 8, 1);
        this.rightfrontleg1.setRotationPoint(-2.5f, 15.0f, -2.0f);
        this.rightfrontleg1.setTextureSize(64, 64);
        this.rightfrontleg1.mirror = true;
        this.setRotation(this.rightfrontleg1, 0.3490659f, 0.0f, -0.6108652f);
        this.leftfrontleg1 = new ModelRenderer(this, 10, 10);
        this.leftfrontleg1.addBox(-0.5f, -8.0f, -0.5f, 1, 8, 1);
        this.leftfrontleg1.setRotationPoint(2.5f, 15.0f, -2.0f);
        this.leftfrontleg1.setTextureSize(64, 64);
        this.leftfrontleg1.mirror = true;
        this.setRotation(this.leftfrontleg1, 0.3490659f, 0.0f, 0.6108652f);
        this.rightbackleg1 = new ModelRenderer(this, 10, 10);
        this.rightbackleg1.addBox(-0.5f, -8.0f, -0.5f, 1, 8, 1);
        this.rightbackleg1.setRotationPoint(-2.5f, 15.0f, 2.0f);
        this.rightbackleg1.setTextureSize(64, 64);
        this.rightbackleg1.mirror = true;
        this.setRotation(this.rightbackleg1, -0.3490659f, 0.0f, -0.6108652f);
        this.leftbackleg1 = new ModelRenderer(this, 10, 10);
        this.leftbackleg1.addBox(-0.5333334f, -8.0f, -0.4666667f, 1, 8, 1);
        this.leftbackleg1.setRotationPoint(2.5f, 15.0f, 2.0f);
        this.leftbackleg1.setTextureSize(64, 64);
        this.leftbackleg1.mirror = true;
        this.setRotation(this.leftbackleg1, -0.3490659f, 0.0f, 0.6108652f);
        this.rightfrontleg2 = new ModelRenderer(this, 10, 10);
        this.rightfrontleg2.addBox(-7.0f, -1.0f, -4.0f, 1, 22, 1);
        this.rightfrontleg2.setRotationPoint(-2.5f, 15.0f, -2.0f);
        this.rightfrontleg2.setTextureSize(64, 64);
        this.rightfrontleg2.mirror = true;
        this.setRotation(this.rightfrontleg2, -0.6108652f, 0.0f, 0.6108652f);
        this.leftfrontleg2 = new ModelRenderer(this, 10, 10);
        this.leftfrontleg2.addBox(6.0f, -1.0f, -4.0f, 1, 22, 1);
        this.leftfrontleg2.setRotationPoint(2.5f, 15.0f, -2.0f);
        this.leftfrontleg2.setTextureSize(64, 64);
        this.leftfrontleg2.mirror = true;
        this.setRotation(this.leftfrontleg2, -0.6108652f, 0.0f, -0.6108652f);
        this.leftbackleg2 = new ModelRenderer(this, 10, 10);
        this.leftbackleg2.addBox(6.0f, -1.0f, 3.0f, 1, 22, 1);
        this.leftbackleg2.setRotationPoint(2.5f, 15.0f, 2.0f);
        this.leftbackleg2.setTextureSize(64, 64);
        this.leftbackleg2.mirror = true;
        this.setRotation(this.leftbackleg2, 0.6108652f, 0.0f, -0.6108652f);
        this.rightbackleg2 = new ModelRenderer(this, 10, 10);
        this.rightbackleg2.addBox(-7.0f, -1.0f, 3.0f, 1, 22, 1);
        this.rightbackleg2.setRotationPoint(-2.5f, 15.0f, 2.0f);
        this.rightbackleg2.setTextureSize(64, 64);
        this.rightbackleg2.mirror = true;
        this.setRotation(this.rightbackleg2, 0.6108652f, 0.0f, 0.6108652f);
        this.something = new ModelRenderer(this, 30, 22);
        this.something.addBox(-1.0f, 3.0f, -1.0f, 2, 2, 2);
        this.something.setRotationPoint(0.0f, 17.0f, 0.0f);
        this.something.setTextureSize(64, 64);
        this.something.mirror = true;
        this.setRotation(this.something, 0.0f, 0.0f, 0.0f);
    }

    public void render(Entity par1Entity, float par2, float par3, float par4, float par5, float par6, float par7) {
        super.render(par1Entity, par2, par3, par4, par5, par6, par7);
        this.setRotationAngles(par2, par3, par4, par5, par6, par7, par1Entity);
        this.body.render(par7);
        this.rightfrontleg1.render(par7);
        this.leftfrontleg1.render(par7);
        this.rightbackleg1.render(par7);
        this.leftbackleg1.render(par7);
        this.rightfrontleg2.render(par7);
        this.leftfrontleg2.render(par7);
        this.leftbackleg2.render(par7);
        this.rightbackleg2.render(par7);
        this.something.render(par7);
    }

    private void setRotation(ModelRenderer model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }

    public void setRotationAngles(float par1, float par2, float par3, float par4, float par5, float par6, Entity par7Entity) {
        super.setRotationAngles(par1, par2, par3, par4, par5, par6, par7Entity);
        this.rightfrontleg2.rotateAngleY = MathHelper.cos(par1 * 0.6662f) * 0.5f * par2;
        this.leftfrontleg2.rotateAngleY = MathHelper.cos(par1 * 0.6662f + (float)Math.PI) * 0.5f * par2;
        this.leftbackleg2.rotateAngleY = MathHelper.cos(par1 * 0.6662f + (float)Math.PI) * 0.5f * par2;
        this.rightbackleg2.rotateAngleY = MathHelper.cos(par1 * 0.6662f) * 0.5f * par2;
    }
}


