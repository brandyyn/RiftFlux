package com.voidsrift.riftflux.terramine;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelDemonEye extends ModelBase {
    ModelRenderer eye;
    ModelRenderer eyetop;
    ModelRenderer eyebottom;
    ModelRenderer eyefront;
    ModelRenderer eyerigth;
    ModelRenderer eyeleft;
    ModelRenderer eyeback;
    ModelRenderer tail1;
    ModelRenderer tail2;
    ModelRenderer tail3;
    ModelRenderer tail4;

    public ModelDemonEye() {
        this.textureWidth = 64;
        this.textureHeight = 32;
        this.eye = new ModelRenderer(this, 0, 0);
        this.eye.addBox(-2.0F, -2.0F, -2.0F, 6, 6, 7);
        this.eye.setRotationPoint(-1.0F, 19.0F, -1.0F);
        this.eye.setTextureSize(64, 32);
        this.eye.mirror = true;
        this.setRotation(this.eye, 0.0F, 0.0F, 0.0F);
        this.eyetop = new ModelRenderer(this, 26, 0);
        this.eyetop.addBox(-1.0F, 0.0F, 0.0F, 4, 1, 5);
        this.eyetop.setRotationPoint(-1.0F, 16.0F, -2.0F);
        this.eyetop.setTextureSize(64, 32);
        this.eyetop.mirror = true;
        this.setRotation(this.eyetop, 0.0F, 0.0F, 0.0F);
        this.eyebottom = new ModelRenderer(this, 26, 0);
        this.eyebottom.addBox(0.0F, 0.0F, 0.0F, 4, 1, 5);
        this.eyebottom.setRotationPoint(-2.0F, 23.0F, -2.0F);
        this.eyebottom.setTextureSize(64, 32);
        this.eyebottom.mirror = true;
        this.setRotation(this.eyebottom, 0.0F, 0.0F, 0.0F);
        this.eyefront = new ModelRenderer(this, 0, 13);
        this.eyefront.addBox(-2.0F, -2.0F, -1.0F, 4, 4, 1);
        this.eyefront.setRotationPoint(0.0F, 20.0F, -3.0F);
        this.eyefront.setTextureSize(64, 32);
        this.eyefront.mirror = true;
        this.setRotation(this.eyefront, 0.0F, 0.0F, 0.0F);
        this.eyerigth = new ModelRenderer(this, 10, 13);
        this.eyerigth.addBox(-1.0F, -2.0F, -2.0F, 1, 4, 5);
        this.eyerigth.setRotationPoint(-3.0F, 20.0F, 0.0F);
        this.eyerigth.setTextureSize(64, 32);
        this.eyerigth.mirror = true;
        this.setRotation(this.eyerigth, 0.0F, 0.0F, 0.0F);
        this.eyeleft = new ModelRenderer(this, 10, 13);
        this.eyeleft.addBox(0.0F, -2.0F, -2.0F, 1, 4, 5);
        this.eyeleft.setRotationPoint(3.0F, 20.0F, 0.0F);
        this.eyeleft.setTextureSize(64, 32);
        this.eyeleft.mirror = true;
        this.setRotation(this.eyeleft, 0.0F, 0.0F, 0.0F);
        this.eyeback = new ModelRenderer(this, 0, 18);
        this.eyeback.addBox(0.0F, 0.0F, 0.0F, 4, 4, 1);
        this.eyeback.setRotationPoint(-2.0F, 18.0F, 4.0F);
        this.eyeback.setTextureSize(64, 32);
        this.eyeback.mirror = true;
        this.setRotation(this.eyeback, 0.0F, 0.0F, 0.0F);
        this.tail1 = new ModelRenderer(this, 0, 24);
        this.tail1.addBox(-1.0F, 0.0F, 0.0F, 0, 2, 6);
        this.tail1.setRotationPoint(3.0F, 17.0F, 4.0F);
        this.tail1.setTextureSize(64, 32);
        this.tail1.mirror = true;
        this.setRotation(this.tail1, 0.0F, 0.0F, 0.0F);
        this.tail2 = new ModelRenderer(this, 0, 22);
        this.tail2.addBox(0.0F, 0.0F, 0.0F, 0, 2, 6);
        this.tail2.setRotationPoint(2.0F, 21.0F, 4.0F);
        this.tail2.setTextureSize(64, 32);
        this.tail2.mirror = true;
        this.setRotation(this.tail2, 0.0F, 0.0F, 0.0F);
        this.tail3 = new ModelRenderer(this, 0, 24);
        this.tail3.addBox(0.0F, 0.0F, 0.0F, 0, 2, 6);
        this.tail3.setRotationPoint(-2.0F, 17.0F, 4.0F);
        this.tail3.setTextureSize(64, 32);
        this.tail3.mirror = true;
        this.setRotation(this.tail3, 0.0F, 0.0F, 0.0F);
        this.tail4 = new ModelRenderer(this, 0, 22);
        this.tail4.addBox(0.0F, 0.0F, 0.0F, 0, 2, 6);
        this.tail4.setRotationPoint(-2.0F, 21.0F, 4.0F);
        this.tail4.setTextureSize(64, 32);
        this.tail4.mirror = true;
        this.setRotation(this.tail4, 0.0F, 0.0F, 0.0F);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        super.render(entity, f, f1, f2, f3, f4, f5);
        this.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
        this.eye.render(f5);
        this.eyetop.render(f5);
        this.eyebottom.render(f5);
        this.eyefront.render(f5);
        this.eyerigth.render(f5);
        this.eyeleft.render(f5);
        this.eyeback.render(f5);
        this.tail1.render(f5);
        this.tail2.render(f5);
        this.tail3.render(f5);
        this.tail4.render(f5);
    }

    private void setRotation(ModelRenderer model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }
}
