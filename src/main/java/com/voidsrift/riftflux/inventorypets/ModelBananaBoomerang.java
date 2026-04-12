package com.voidsrift.riftflux.inventorypets;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelBananaBoomerang extends ModelBase {
    private final ModelRenderer shape1;

    public ModelBananaBoomerang() {
        this.textureWidth = 64;
        this.textureHeight = 32;

        this.shape1 = new ModelRenderer(this, 0, 0);
        this.shape1.addBox(0.0F, 0.0F, 0.0F, 6, 1, 1);
        this.shape1.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.shape1.setTextureSize(64, 32);
        this.shape1.mirror = true;
        this.setRotation(this.shape1, -1.570796F, 0.0F, 0.0F);

        ModelRenderer shape2 = new ModelRenderer(this, 0, 3);
        shape2.addBox(-2.0F, 0.0F, 0.0F, 10, 1, 1);
        shape2.setRotationPoint(0.0F, 0.0F, 1.0F);
        shape2.setTextureSize(64, 32);
        shape2.mirror = true;
        this.setRotation(shape2, -1.570796F, 0.0F, 0.0F);

        ModelRenderer shape3 = new ModelRenderer(this, 0, 9);
        shape3.addBox(0.0F, 0.0F, 0.0F, 12, 1, 1);
        shape3.setRotationPoint(-3.0F, 0.0F, 2.0F);
        shape3.setTextureSize(64, 32);
        shape3.mirror = true;
        this.setRotation(shape3, -1.570796F, 0.0F, 0.0F);

        ModelRenderer shape4 = new ModelRenderer(this, 0, 12);
        shape4.addBox(0.0F, 0.0F, 0.0F, 14, 3, 1);
        shape4.setRotationPoint(-4.0F, 0.0F, 5.0F);
        shape4.setTextureSize(64, 32);
        shape4.mirror = true;
        this.setRotation(shape4, -1.570796F, 0.0F, 0.0F);

        ModelRenderer shape5 = new ModelRenderer(this, 0, 16);
        shape5.addBox(0.0F, 0.0F, 0.0F, 1, 6, 1);
        shape5.setRotationPoint(-5.0F, 0.0F, 10.0F);
        shape5.setTextureSize(64, 32);
        shape5.mirror = true;
        this.setRotation(shape5, -1.570796F, 0.0F, 0.0F);

        ModelRenderer shape6 = new ModelRenderer(this, 5, 16);
        shape6.addBox(0.0F, 0.0F, 0.0F, 4, 6, 1);
        shape6.setRotationPoint(-4.0F, 0.0F, 11.0F);
        shape6.setTextureSize(64, 32);
        shape6.mirror = true;
        this.setRotation(shape6, -1.570796F, 0.0F, 0.0F);

        ModelRenderer shape7 = new ModelRenderer(this, 16, 19);
        shape7.addBox(0.0F, 0.0F, 0.0F, 1, 1, 3);
        shape7.setRotationPoint(0.0F, 0.0F, 7.0F);
        shape7.setTextureSize(64, 32);
        shape7.mirror = true;

        ModelRenderer shape8 = new ModelRenderer(this, 31, 3);
        shape8.addBox(0.0F, 0.0F, 0.0F, 2, 1, 2);
        shape8.setRotationPoint(0.0F, 0.0F, 5.0F);
        shape8.setTextureSize(64, 32);
        shape8.mirror = true;

        ModelRenderer shape9 = new ModelRenderer(this, 45, 8);
        shape9.addBox(0.0F, 0.0F, 0.0F, 2, 1, 1);
        shape9.setRotationPoint(2.0F, 0.0F, 5.0F);
        shape9.setTextureSize(64, 32);
        shape9.mirror = true;

        ModelRenderer shape10 = new ModelRenderer(this, 33, 8);
        shape10.addBox(0.0F, 0.0F, 0.0F, 3, 1, 1);
        shape10.setRotationPoint(-3.0F, 0.0F, 12.0F);
        shape10.setTextureSize(64, 32);
        shape10.mirror = true;
        this.setRotation(shape10, -1.570796F, 0.0F, 0.0F);

        ModelRenderer shape11 = new ModelRenderer(this, 33, 12);
        shape11.addBox(0.0F, 0.0F, 0.0F, 3, 2, 1);
        shape11.setRotationPoint(-2.0F, 0.0F, 14.0F);
        shape11.setTextureSize(64, 32);
        shape11.mirror = true;
        this.setRotation(shape11, -1.570796F, 0.0F, 0.0F);

        this.shape1.addChild(shape2);
        this.shape1.addChild(shape3);
        this.shape1.addChild(shape4);
        this.shape1.addChild(shape5);
        this.shape1.addChild(shape6);
        this.shape1.addChild(shape7);
        this.shape1.addChild(shape8);
        this.shape1.addChild(shape9);
        this.shape1.addChild(shape10);
        this.shape1.addChild(shape11);
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        super.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entity);
        this.shape1.render(scale);
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entity) {
        this.shape1.rotateAngleX = 2.0F + 0.025F * this.triangleWave(1.0F, 110.0F);
        this.shape1.rotateAngleY = 0.0F;
    }

    private void setRotation(ModelRenderer model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }

    private float triangleWave(float value, float period) {
        return (Math.abs(value % period - period * 0.5F) - period * 0.25F) / (period * 0.25F);
    }
}
