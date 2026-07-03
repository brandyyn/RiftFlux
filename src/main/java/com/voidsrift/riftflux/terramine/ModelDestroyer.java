package com.voidsrift.riftflux.terramine;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelDestroyer extends ModelBase {
    private ModelRenderer base;
    private ModelRenderer shape1;
    private ModelRenderer shape2;
    private ModelRenderer shape3;
    private ModelRenderer shape4;
    private ModelRenderer shape5;
    private ModelRenderer shape6;
    private ModelRenderer shape7;
    private ModelRenderer shape8;

    public ModelDestroyer() {
        this(false);
    }

    public ModelDestroyer(boolean hasAntennae) {
        this.textureWidth = 64;
        this.textureHeight = hasAntennae ? 64 : 32;

        this.base = new ModelRenderer(this, 0, 0);
        this.base.addBox(-8.0F, -8.0F, -8.0F, 16, 16, 16);
        this.base.setRotationPoint(0.0F, 16.0F, 0.0F);

        if (hasAntennae) {
            this.base.setTextureSize(64, 64);

            this.shape1 = new ModelRenderer(this, 12, 32);
            this.shape1.addBox(-1.0F, -1.0F, -1.0F, 10, 2, 2);
            this.shape1.setRotationPoint(-7.0F, 8.0F, 0.0F);
            this.shape1.setTextureSize(64, 64);
            this.setRotation(this.shape1, 0.0F, 0.0F, 2.356194F);

            this.shape3 = new ModelRenderer(this, 0, 32);
            this.shape3.addBox(19.0F, -1.5F, -5.0F, 1, 2, 10);
            this.shape3.setTextureSize(64, 64);
            this.setRotation(this.shape3, 0.0F, 0.0F, 2.356194F);

            this.shape2 = new ModelRenderer(this, 0, 32);
            this.shape2.addBox(20.0F, -1.0F, -5.0F, 1, 2, 10);
            this.shape2.setTextureSize(64, 64);
            this.setRotation(this.shape2, 0.0F, 0.0F, 0.7853982F);

            this.shape4 = new ModelRenderer(this, 12, 32);
            this.shape4.addBox(-1.0F, -1.0F, -1.0F, 10, 2, 2);
            this.shape4.setRotationPoint(8.0F, 8.0F, 0.0F);
            this.shape4.setTextureSize(64, 64);
            this.setRotation(this.shape4, 0.0F, 0.0F, 0.7853982F);

            this.shape5 = new ModelRenderer(this, 12, 32);
            this.shape5.addBox(-1.0F, -1.0F, -1.0F, 10, 2, 2);
            this.shape5.setRotationPoint(8.0F, -8.0F, 0.0F);
            this.shape5.setTextureSize(64, 64);
            this.setRotation(this.shape5, 0.0F, 0.0F, -0.7853982F);

            this.shape6 = new ModelRenderer(this, 0, 32);
            this.shape6.addBox(20.0F, -1.0F, -5.0F, 1, 2, 10);
            this.shape6.setTextureSize(64, 64);
            this.setRotation(this.shape6, 0.0F, 0.0F, -0.7853982F);

            this.shape7 = new ModelRenderer(this, 12, 32);
            this.shape7.addBox(-1.0F, -1.0F, -1.0F, 10, 2, 2);
            this.shape7.setRotationPoint(-7.0F, -8.0F, 0.0F);
            this.shape7.setTextureSize(64, 64);
            this.setRotation(this.shape7, 0.0F, 0.0F, -2.356194F);

            this.shape8 = new ModelRenderer(this, 0, 32);
            this.shape8.addBox(19.0F, -0.5F, -5.0F, 1, 2, 10);
            this.shape8.setTextureSize(64, 64);
            this.setRotation(this.shape8, 0.0F, 0.0F, -2.356194F);

            this.base.addChild(this.shape1);
            this.base.addChild(this.shape2);
            this.base.addChild(this.shape3);
            this.base.addChild(this.shape4);
            this.base.addChild(this.shape5);
            this.base.addChild(this.shape6);
            this.base.addChild(this.shape7);
            this.base.addChild(this.shape8);
        }
    }

    private void setRotation(ModelRenderer model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entity);
        this.base.render(scale);
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale, Entity entity) {
        this.base.rotateAngleX = headPitch / 57.295776F;
        super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entity);
    }
}
