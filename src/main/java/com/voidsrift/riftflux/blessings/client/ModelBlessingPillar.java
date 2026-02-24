package com.voidsrift.riftflux.blessings.client;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelBlessingPillar extends ModelBase {
    private final ModelRenderer shape1;
    private final ModelRenderer shape2;
    private final ModelRenderer shape3;

    public ModelBlessingPillar() {
        textureWidth = 64;
        textureHeight = 64;

        shape1 = new ModelRenderer(this, 0, 0);
        shape1.addBox(0.0F, 0.0F, 0.0F, 12, 26, 12);
        shape1.setRotationPoint(-6.0F, -2.0F, -6.0F);
        shape1.setTextureSize(64, 64);
        shape1.mirror = true;
        setRotation(shape1, 0.0F, 0.0F, 0.0F);

        shape2 = new ModelRenderer(this, 0, 38);
        shape2.addBox(0.0F, 0.0F, 0.0F, 10, 2, 10);
        shape2.setRotationPoint(-5.0F, -4.0F, -5.0F);
        shape2.setTextureSize(64, 64);
        shape2.mirror = true;
        setRotation(shape2, 0.0F, 0.0F, 0.0F);

        shape3 = new ModelRenderer(this, 0, 50);
        shape3.addBox(0.0F, 0.0F, 0.0F, 8, 2, 8);
        shape3.setRotationPoint(-4.0F, -6.0F, -4.0F);
        shape3.setTextureSize(64, 64);
        shape3.mirror = true;
        setRotation(shape3, 0.0F, 0.0F, 0.0F);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float scale) {
        super.render(entity, f, f1, f2, f3, f4, scale);
        setRotationAngles(f, f1, f2, f3, f4, scale, entity);
        shape1.render(scale);
        shape2.render(scale);
        shape3.render(scale);
    }

    private void setRotation(ModelRenderer model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }
}
