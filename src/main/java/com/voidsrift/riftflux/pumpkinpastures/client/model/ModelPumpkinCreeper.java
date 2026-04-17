package com.voidsrift.riftflux.pumpkinpastures.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;

public class ModelPumpkinCreeper extends ModelBase {
    private final ModelRenderer body;
    private final ModelRenderer stem;
    private final ModelRenderer leg1;
    private final ModelRenderer leg2;
    private final ModelRenderer leg3;
    private final ModelRenderer leg4;

    public ModelPumpkinCreeper() {
        this.textureWidth = 64;
        this.textureHeight = 64;

        this.body = new ModelRenderer(this, 0, 0);
        this.body.addBox(-8.0F, -14.0F, -8.2F, 16, 16, 16);
        this.body.setRotationPoint(0.0F, 19.0F, 0.0F);

        this.stem = new ModelRenderer(this, 20, 44);
        this.stem.addBox(-0.5F, -3.0F, -0.5F, 1, 3, 1);
        this.stem.setRotationPoint(1.0F, -14.0F, 1.0F);
        this.body.addChild(this.stem);

        this.leg1 = new ModelRenderer(this, 0, 32);
        this.leg1.addBox(-3.0F, -1.0F, -3.0F, 5, 7, 5);
        this.leg1.setRotationPoint(8.0F, 18.0F, -7.0F);

        this.leg2 = new ModelRenderer(this, 20, 32);
        this.leg2.addBox(-2.0F, -1.0F, -2.0F, 5, 7, 5);
        this.leg2.setRotationPoint(-8.0F, 18.0F, -8.0F);

        this.leg3 = new ModelRenderer(this, 40, 32);
        this.leg3.addBox(-3.0F, -1.0F, -2.0F, 5, 7, 5);
        this.leg3.setRotationPoint(-7.0F, 18.0F, 7.0F);

        this.leg4 = new ModelRenderer(this, 0, 44);
        this.leg4.addBox(-2.0F, -1.0F, -1.0F, 5, 7, 5);
        this.leg4.setRotationPoint(7.0F, 18.0F, 6.0F);
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entity);
        this.body.render(scale);
        this.leg1.render(scale);
        this.leg2.render(scale);
        this.leg3.render(scale);
        this.leg4.render(scale);
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
        float walk = limbSwingAmount * 0.8F;
        this.leg1.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 0.7F * walk;
        this.leg2.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 0.7F * walk;
        this.leg3.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 0.7F * walk;
        this.leg4.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 0.7F * walk;
    }
}
