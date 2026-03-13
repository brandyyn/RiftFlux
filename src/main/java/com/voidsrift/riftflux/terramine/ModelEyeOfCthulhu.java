package com.voidsrift.riftflux.terramine;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelEyeOfCthulhu extends ModelBase {
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

    public ModelEyeOfCthulhu() {
        this.textureWidth = 640;
        this.textureHeight = 320;
        this.eye = new ModelRenderer(this, 0, 0);
        this.eye.addBox(-2.0F, -2.0F, -2.0F, 60, 60, 70);
        this.eye.setRotationPoint(28.0F, -44.0F, 49.0F);
        this.eye.setTextureSize(640, 320);
        this.eye.mirror = true;
        this.setRotation(this.eye, 0.0F, (float)Math.PI, 0.0F);
        this.eyetop = new ModelRenderer(this, 260, 0);
        this.eyetop.addBox(-1.0F, 0.0F, 0.0F, 40, 10, 50);
        this.eyetop.setRotationPoint(19.0F, -56.0F, 41.0F);
        this.eyetop.setTextureSize(640, 320);
        this.eyetop.mirror = true;
        this.setRotation(this.eyetop, 0.0F, (float)Math.PI, 0.0F);
        this.eyebottom = new ModelRenderer(this, 260, 0);
        this.eyebottom.addBox(0.0F, 0.0F, 0.0F, 40, 10, 50);
        this.eyebottom.setRotationPoint(20.0F, 14.0F, 41.0F);
        this.eyebottom.setTextureSize(640, 320);
        this.eyebottom.mirror = true;
        this.setRotation(this.eyebottom, 0.0F, (float)Math.PI, 0.0F);
        this.eyefront = new ModelRenderer(this, 0, 130);
        this.eyefront.addBox(-2.0F, -2.0F, -1.0F, 40, 40, 10);
        this.eyefront.setRotationPoint(18.0F, -34.0F, 60.0F);
        this.eyefront.setTextureSize(640, 320);
        this.eyefront.mirror = true;
        this.setRotation(this.eyefront, 0.0F, (float)Math.PI, 0.0F);
        this.eyerigth = new ModelRenderer(this, 100, 130);
        this.eyerigth.addBox(-1.0F, -2.0F, -2.0F, 10, 40, 50);
        this.eyerigth.setRotationPoint(-31.0F, -34.0F, 38.0F);
        this.eyerigth.setTextureSize(640, 320);
        this.eyerigth.mirror = true;
        this.setRotation(this.eyerigth, 0.0F, (float)Math.PI, 0.0F);
        this.eyeleft = new ModelRenderer(this, 100, 130);
        this.eyeleft.addBox(0.0F, -2.0F, -2.0F, 10, 40, 50);
        this.eyeleft.setRotationPoint(40.0F, -34.0F, 39.0F);
        this.eyeleft.setTextureSize(640, 320);
        this.eyeleft.mirror = true;
        this.setRotation(this.eyeleft, 0.0F, (float)Math.PI, 0.0F);
        this.eyeback = new ModelRenderer(this, 0, 180);
        this.eyeback.addBox(0.0F, 0.0F, 0.0F, 40, 40, 10);
        this.eyeback.setRotationPoint(21.0F, -36.0F, -19.0F);
        this.eyeback.setTextureSize(640, 320);
        this.eyeback.mirror = true;
        this.setRotation(this.eyeback, 0.0F, (float)Math.PI, 0.0F);
        this.tail1 = new ModelRenderer(this, 0, 240);
        this.tail1.addBox(-25.0F, -41.0F, 30.0F, 0, 20, 60);
        this.tail1.setRotationPoint(-4.0F, -5.0F, 10.0F);
        this.tail1.setTextureSize(640, 320);
        this.tail1.mirror = true;
        this.setRotation(this.tail1, 0.0F, (float)Math.PI, 0.0F);
        this.tail2 = new ModelRenderer(this, 0, 220);
        this.tail2.addBox(0.0F, 0.0F, 0.0F, 0, 20, 60);
        this.tail2.setRotationPoint(-20.0F, -6.0F, -20.0F);
        this.tail2.setTextureSize(640, 320);
        this.tail2.mirror = true;
        this.setRotation(this.tail2, 0.0F, (float)Math.PI, 0.0F);
        this.tail3 = new ModelRenderer(this, 0, 240);
        this.tail3.addBox(0.0F, 0.0F, 0.0F, 0, 20, 60);
        this.tail3.setRotationPoint(-19.0F, -45.0F, -20.0F);
        this.tail3.setTextureSize(640, 320);
        this.tail3.mirror = true;
        this.setRotation(this.tail3, 0.0F, (float)Math.PI, 0.0F);
        this.tail4 = new ModelRenderer(this, 0, 220);
        this.tail4.addBox(0.0F, 0.0F, 0.0F, 0, 20, 60);
        this.tail4.setRotationPoint(21.0F, -6.0F, -20.0F);
        this.tail4.setTextureSize(640, 320);
        this.tail4.mirror = true;
        this.setRotation(this.tail4, 0.0F, (float)Math.PI, 0.0F);
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
