package com.voidsrift.riftflux.avatar.glider;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelAirStaffClosed extends ModelBase {
    ModelRenderer Shaft;
    ModelRenderer Knob1;
    ModelRenderer Knob2;
    ModelRenderer Knob3;
    ModelRenderer Knob4;
    ModelRenderer Knob5;
    ModelRenderer Knob6;
    ModelRenderer Knob7;
    ModelRenderer Knob8;

    public ModelAirStaffClosed() {
        this.textureWidth = 64;
        this.textureHeight = 64;
        this.Shaft = new ModelRenderer(this, 0, 0);
        this.Shaft.addBox(0.0f, 0.0f, 0.0f, 1, 40, 1);
        this.Shaft.setRotationPoint(0.0f, -16.0f, 0.0f);
        this.Shaft.setTextureSize(64, 64);
        this.Shaft.mirror = true;
        this.setRotation(this.Shaft, 0.0f, 0.0f, 0.0f);
        this.Knob1 = new ModelRenderer(this, 0, 0);
        this.Knob1.addBox(0.0f, 0.0f, 0.0f, 1, 2, 1);
        this.Knob1.setRotationPoint(0.0f, -7.9f, 0.0f);
        this.Knob1.setTextureSize(64, 64);
        this.Knob1.mirror = true;
        this.setRotation(this.Knob1, 0.0f, 0.0f, 0.4833219f);
        this.Knob2 = new ModelRenderer(this, 0, 0);
        this.Knob2.addBox(0.0f, 0.0f, 0.0f, 1, 2, 1);
        this.Knob2.setRotationPoint(-0.9f, -6.1f, 0.0f);
        this.Knob2.setTextureSize(64, 64);
        this.Knob2.mirror = true;
        this.setRotation(this.Knob2, 0.0f, 0.0f, -0.4833219f);
        this.Knob3 = new ModelRenderer(this, 0, 0);
        this.Knob3.addBox(-1.0f, 0.0f, 0.0f, 1, 2, 1);
        this.Knob3.setRotationPoint(1.0f, -7.9f, 0.0f);
        this.Knob3.setTextureSize(64, 64);
        this.Knob3.mirror = true;
        this.setRotation(this.Knob3, 0.0f, 0.0f, -0.4833219f);
        this.Knob4 = new ModelRenderer(this, 0, 0);
        this.Knob4.addBox(-1.0f, 0.0f, 0.0f, 1, 2, 1);
        this.Knob4.setRotationPoint(1.9f, -6.1f, 0.0f);
        this.Knob4.setTextureSize(64, 64);
        this.Knob4.mirror = true;
        this.setRotation(this.Knob4, 0.0f, 0.0f, 0.4833219f);
        this.Knob5 = new ModelRenderer(this, 0, 0);
        this.Knob5.addBox(0.0f, 0.0f, 0.0f, 1, 2, 1);
        this.Knob5.setRotationPoint(0.0f, 14.1f, 0.0f);
        this.Knob5.setTextureSize(64, 64);
        this.Knob5.mirror = true;
        this.setRotation(this.Knob5, 0.0f, 0.0f, 0.4833219f);
        this.Knob6 = new ModelRenderer(this, 0, 0);
        this.Knob6.addBox(0.0f, 0.0f, 0.0f, 1, 2, 1);
        this.Knob6.setRotationPoint(-0.9f, 15.9f, 0.0f);
        this.Knob6.setTextureSize(64, 64);
        this.Knob6.mirror = true;
        this.setRotation(this.Knob6, 0.0f, 0.0f, -0.4833219f);
        this.Knob7 = new ModelRenderer(this, 0, 0);
        this.Knob7.addBox(-1.0f, 0.0f, 0.0f, 1, 2, 1);
        this.Knob7.setRotationPoint(1.0f, 14.1f, 0.0f);
        this.Knob7.setTextureSize(64, 64);
        this.Knob7.mirror = true;
        this.setRotation(this.Knob7, 0.0f, 0.0f, -0.4833219f);
        this.Knob8 = new ModelRenderer(this, 0, 0);
        this.Knob8.addBox(-1.0f, 0.0f, 0.0f, 1, 2, 1);
        this.Knob8.setRotationPoint(1.9f, 15.9f, 0.0f);
        this.Knob8.setTextureSize(64, 64);
        this.Knob8.mirror = true;
        this.setRotation(this.Knob8, 0.0f, 0.0f, 0.4833219f);
    }

    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        super.render(entity, f, f1, f2, f3, f4, f5);
        this.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
        this.Shaft.render(f5);
        this.Knob1.render(f5);
        this.Knob2.render(f5);
        this.Knob3.render(f5);
        this.Knob4.render(f5);
        this.Knob5.render(f5);
        this.Knob6.render(f5);
        this.Knob7.render(f5);
        this.Knob8.render(f5);
    }

    private void setRotation(ModelRenderer model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }

    public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity entity) {
        super.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
    }
}
