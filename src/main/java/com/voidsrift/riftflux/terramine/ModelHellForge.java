package com.voidsrift.riftflux.terramine;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

public class ModelHellForge extends ModelBase implements TerraModelRenderable {
    private final ModelRenderer shape1;
    private final ModelRenderer shape2;
    private final ModelRenderer shape3;
    private final ModelRenderer shape4;
    private final ModelRenderer shape5;
    private final ModelRenderer shape6;
    private final ModelRenderer shape7;
    private final ModelRenderer shape8;
    private final ModelRenderer shape9;
    private final ModelRenderer shape10;
    private final ModelRenderer shape11;
    private final ModelRenderer shape12;
    private final ModelRenderer shape13;
    private final ModelRenderer shape14;
    private final ModelRenderer shape15;
    private final ModelRenderer shape16;
    private final ModelRenderer shape17;
    private final ModelRenderer shape18;
    private final ModelRenderer shape19;

    public ModelHellForge() {
        this.textureWidth = 128;
        this.textureHeight = 64;

        this.shape1 = new ModelRenderer(this, 0, 0);
        this.shape1.addBox(0.0F, 0.0F, 0.0F, 16, 3, 16);
        this.shape1.setRotationPoint(-8.0F, 21.0F, -8.0F);
        this.shape1.setTextureSize(128, 64);
        this.shape1.mirror = true;
        this.setRotation(this.shape1, 0.0F, 0.0F, 0.0F);

        this.shape2 = new ModelRenderer(this, 64, 0);
        this.shape2.addBox(0.0F, 0.0F, 0.0F, 3, 10, 12);
        this.shape2.setRotationPoint(-6.0F, 11.0F, -6.0F);
        this.shape2.setTextureSize(128, 64);
        this.shape2.mirror = true;
        this.setRotation(this.shape2, 0.0F, 0.0F, 0.0F);

        this.shape3 = new ModelRenderer(this, 30, 48);
        this.shape3.addBox(0.0F, 0.0F, 0.0F, 1, 2, 14);
        this.shape3.setRotationPoint(-8.0F, 19.0F, -7.0F);
        this.shape3.setTextureSize(128, 64);
        this.shape3.mirror = true;
        this.setRotation(this.shape3, 0.0F, 0.0F, 0.0F);

        this.shape4 = new ModelRenderer(this, 0, 45);
        this.shape4.addBox(0.0F, 0.0F, 0.0F, 16, 2, 1);
        this.shape4.setRotationPoint(-8.0F, 19.0F, -8.0F);
        this.shape4.setTextureSize(128, 64);
        this.shape4.mirror = true;
        this.setRotation(this.shape4, 0.0F, 0.0F, 0.0F);

        this.shape5 = new ModelRenderer(this, 34, 43);
        this.shape5.addBox(0.0F, 0.0F, 0.0F, 16, 3, 2);
        this.shape5.setRotationPoint(-8.0F, 18.0F, 6.0F);
        this.shape5.setTextureSize(128, 64);
        this.shape5.mirror = true;
        this.setRotation(this.shape5, 0.0F, 0.0F, 0.0F);

        this.shape6 = new ModelRenderer(this, 0, 23);
        this.shape6.addBox(0.0F, 0.0F, 0.0F, 3, 10, 12);
        this.shape6.setRotationPoint(3.0F, 11.0F, -6.0F);
        this.shape6.setTextureSize(128, 64);
        this.shape6.mirror = true;
        this.setRotation(this.shape6, 0.0F, 0.0F, 0.0F);

        this.shape7 = new ModelRenderer(this, 60, 50);
        this.shape7.addBox(0.0F, 0.0F, 0.0F, 6, 2, 12);
        this.shape7.setRotationPoint(-3.0F, 11.0F, -6.0F);
        this.shape7.setTextureSize(128, 64);
        this.shape7.mirror = true;
        this.setRotation(this.shape7, 0.0F, 0.0F, 0.0F);

        this.shape8 = new ModelRenderer(this, 48, 0);
        this.shape8.addBox(0.0F, 0.0F, 0.0F, 6, 8, 1);
        this.shape8.setRotationPoint(-3.0F, 13.0F, 5.0F);
        this.shape8.setTextureSize(128, 64);
        this.shape8.mirror = true;
        this.setRotation(this.shape8, 0.0F, 0.0F, 0.0F);

        this.shape9 = new ModelRenderer(this, 94, 0);
        this.shape9.addBox(0.0F, 0.0F, 0.0F, 6, 1, 10);
        this.shape9.setRotationPoint(-3.0F, 13.0F, -4.0F);
        this.shape9.setTextureSize(128, 64);
        this.shape9.mirror = true;
        this.setRotation(this.shape9, 0.0F, 0.0F, 0.0F);

        this.shape10 = new ModelRenderer(this, 94, 11);
        this.shape10.addBox(0.0F, 0.0F, 0.0F, 6, 1, 8);
        this.shape10.setRotationPoint(-3.0F, 14.0F, -2.0F);
        this.shape10.setTextureSize(128, 64);
        this.shape10.mirror = true;
        this.setRotation(this.shape10, 0.0F, 0.0F, 0.0F);

        this.shape11 = new ModelRenderer(this, 96, 58);
        this.shape11.addBox(0.0F, 0.0F, 0.0F, 6, 1, 5);
        this.shape11.setRotationPoint(-3.0F, 20.0F, -7.0F);
        this.shape11.setTextureSize(128, 64);
        this.shape11.mirror = true;
        this.setRotation(this.shape11, 0.0F, 0.0F, 0.0F);

        this.shape12 = new ModelRenderer(this, 96, 55);
        this.shape12.addBox(0.0F, 0.0F, 0.0F, 6, 1, 2);
        this.shape12.setRotationPoint(-3.0F, 19.0F, -7.0F);
        this.shape12.setTextureSize(128, 64);
        this.shape12.mirror = true;
        this.setRotation(this.shape12, 0.0F, 0.0F, 0.0F);

        this.shape13 = new ModelRenderer(this, 96, 50);
        this.shape13.addBox(0.0F, 0.0F, 0.0F, 4, 1, 4);
        this.shape13.setRotationPoint(-2.0F, 20.0F, -1.0F);
        this.shape13.setTextureSize(128, 64);
        this.shape13.mirror = true;
        this.setRotation(this.shape13, 0.0F, 0.0F, 0.0F);

        this.shape14 = new ModelRenderer(this, 0, 19);
        this.shape14.addBox(-2.0F, 0.0F, 0.0F, 4, 4, 0);
        this.shape14.setRotationPoint(0.0F, 16.0F, 1.0F);
        this.shape14.setTextureSize(128, 64);
        this.shape14.mirror = true;
        this.setRotation(this.shape14, 0.0F, -0.7853982F, 0.0F);

        this.shape15 = new ModelRenderer(this, 0, 19);
        this.shape15.addBox(-2.0F, 0.0F, 0.0F, 4, 4, 0);
        this.shape15.setRotationPoint(0.0F, 16.0F, 1.0F);
        this.shape15.setTextureSize(128, 64);
        this.shape15.mirror = true;
        this.setRotation(this.shape15, 0.0F, 0.7853982F, 0.0F);

        this.shape16 = new ModelRenderer(this, 30, 25);
        this.shape16.addBox(0.0F, 0.0F, 0.0F, 2, 5, 13);
        this.shape16.setRotationPoint(6.0F, 16.0F, -5.0F);
        this.shape16.setTextureSize(128, 64);
        this.shape16.mirror = true;
        this.setRotation(this.shape16, 0.0F, 0.0F, 0.0F);

        this.shape17 = new ModelRenderer(this, 112, 53);
        this.shape17.addBox(0.0F, 0.0F, 0.0F, 4, 3, 2);
        this.shape17.setRotationPoint(4.0F, 18.0F, -7.0F);
        this.shape17.setTextureSize(128, 64);
        this.shape17.mirror = true;
        this.setRotation(this.shape17, 0.0F, 0.0F, 0.0F);

        this.shape18 = new ModelRenderer(this, 8, 19);
        this.shape18.addBox(0.0F, 0.0F, 0.0F, 2, 1, 2);
        this.shape18.setRotationPoint(6.0F, 17.0F, -7.0F);
        this.shape18.setTextureSize(128, 64);
        this.shape18.mirror = true;
        this.setRotation(this.shape18, 0.0F, 0.0F, 0.0F);

        this.shape19 = new ModelRenderer(this, 16, 19);
        this.shape19.addBox(0.0F, 0.0F, 0.0F, 2, 1, 2);
        this.shape19.setRotationPoint(-1.0F, 19.53333F, 0.0F);
        this.shape19.setTextureSize(128, 64);
        this.shape19.mirror = true;
        this.setRotation(this.shape19, 0.0F, 0.0F, 0.0F);
    }

    @Override
    public void renderAll() {
        this.shape1.render(0.0625F);
        this.shape2.render(0.0625F);
        this.shape3.render(0.0625F);
        this.shape4.render(0.0625F);
        this.shape5.render(0.0625F);
        this.shape6.render(0.0625F);
        this.shape7.render(0.0625F);
        this.shape8.render(0.0625F);
        this.shape9.render(0.0625F);
        this.shape10.render(0.0625F);
        this.shape11.render(0.0625F);
        this.shape12.render(0.0625F);
        this.shape13.render(0.0625F);
        this.shape14.render(0.0625F);
        this.shape15.render(0.0625F);
        this.shape16.render(0.0625F);
        this.shape17.render(0.0625F);
        this.shape18.render(0.0625F);
        this.shape19.render(0.0625F);
    }

    private void setRotation(ModelRenderer model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }
}
