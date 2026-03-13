/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.model.ModelBase
 *  net.minecraft.client.model.ModelRenderer
 *  net.minecraft.entity.Entity
 */
package tk.nukeduck.hearts.renderer;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelHeart
extends ModelBase {
    ModelRenderer shape1 = new ModelRenderer((ModelBase)this, 0, 0);
    ModelRenderer shape2;
    ModelRenderer shape3;
    ModelRenderer shape4;
    ModelRenderer shape5;
    ModelRenderer shape6;
    ModelRenderer shape7;
    ModelRenderer shape8;
    ModelRenderer shape9;
    ModelRenderer shape10;
    ModelRenderer shape11;
    ModelRenderer shape12;
    ModelRenderer shape13;
    ModelRenderer shape14;
    ModelRenderer shape15;
    ModelRenderer shape16;
    ModelRenderer shape17;
    ModelRenderer shape18;
    ModelRenderer shape19;
    ModelRenderer shape20;

    public ModelHeart() {
        this.shape1.addBox(0.0f, 0.0f, 0.0f, 4, 1, 2);
        this.shape1.setRotationPoint(-2.0f, 23.0f, -1.0f);
        this.shape1.setTextureSize(64, 32);
        this.shape1.mirror = true;
        this.setRotation(this.shape1, 0.0f, 0.0f, 0.0f);
        this.shape2 = new ModelRenderer((ModelBase)this, 0, 0);
        this.shape2.addBox(0.0f, 0.0f, 0.0f, 8, 8, 6);
        this.shape2.setRotationPoint(-4.0f, 12.0f, -3.0f);
        this.shape2.setTextureSize(64, 32);
        this.shape2.mirror = true;
        this.setRotation(this.shape2, 0.0f, 0.0f, 0.0f);
        this.shape3 = new ModelRenderer((ModelBase)this, 0, 0);
        this.shape3.addBox(0.0f, 0.0f, 0.0f, 12, 6, 4);
        this.shape3.setRotationPoint(-6.0f, 13.0f, -2.0f);
        this.shape3.setTextureSize(64, 32);
        this.shape3.mirror = true;
        this.setRotation(this.shape3, 0.0f, 0.0f, 0.0f);
        this.shape4 = new ModelRenderer((ModelBase)this, 0, 0);
        this.shape4.addBox(0.0f, 0.0f, 0.0f, 10, 5, 6);
        this.shape4.setRotationPoint(-5.0f, 13.0f, -3.0f);
        this.shape4.setTextureSize(64, 32);
        this.shape4.mirror = true;
        this.setRotation(this.shape4, 0.0f, 0.0f, 0.0f);
        this.shape5 = new ModelRenderer((ModelBase)this, 0, 0);
        this.shape5.addBox(0.0f, 0.0f, 0.0f, 3, 3, 4);
        this.shape5.setRotationPoint(2.0f, 10.0f, -2.0f);
        this.shape5.setTextureSize(64, 32);
        this.shape5.mirror = true;
        this.setRotation(this.shape5, 0.0f, 0.0f, 0.0f);
        this.shape6 = new ModelRenderer((ModelBase)this, 0, 0);
        this.shape6.addBox(0.0f, 0.0f, 0.0f, 3, 3, 4);
        this.shape6.setRotationPoint(-5.0f, 10.0f, -2.0f);
        this.shape6.setTextureSize(64, 32);
        this.shape6.mirror = true;
        this.setRotation(this.shape6, 0.0f, 0.0f, 0.0f);
        this.shape7 = new ModelRenderer((ModelBase)this, 0, 0);
        this.shape7.addBox(0.0f, 0.0f, 0.0f, 6, 4, 8);
        this.shape7.setRotationPoint(-3.0f, 14.0f, -4.0f);
        this.shape7.setTextureSize(64, 32);
        this.shape7.mirror = true;
        this.setRotation(this.shape7, 0.0f, 0.0f, 0.0f);
        this.shape8 = new ModelRenderer((ModelBase)this, 0, 0);
        this.shape8.addBox(0.0f, 0.0f, 0.0f, 4, 6, 8);
        this.shape8.setRotationPoint(-2.0f, 13.0f, -4.0f);
        this.shape8.setTextureSize(64, 32);
        this.shape8.mirror = true;
        this.setRotation(this.shape8, 0.0f, 0.0f, 0.0f);
        this.shape9 = new ModelRenderer((ModelBase)this, 0, 0);
        this.shape9.addBox(0.0f, 0.0f, 0.0f, 10, 2, 2);
        this.shape9.setRotationPoint(-5.0f, 19.0f, -1.0f);
        this.shape9.setTextureSize(64, 32);
        this.shape9.mirror = true;
        this.setRotation(this.shape9, 0.0f, 0.0f, 0.0f);
        this.shape10 = new ModelRenderer((ModelBase)this, 0, 0);
        this.shape10.addBox(0.0f, 0.0f, 0.0f, 12, 2, 2);
        this.shape10.setRotationPoint(-6.0f, 11.0f, -1.0f);
        this.shape10.setTextureSize(64, 32);
        this.shape10.mirror = true;
        this.setRotation(this.shape10, 0.0f, 0.0f, 0.0f);
        this.shape11 = new ModelRenderer((ModelBase)this, 0, 0);
        this.shape11.addBox(0.0f, 0.0f, 0.0f, 2, 1, 2);
        this.shape11.setRotationPoint(2.0f, 9.0f, -1.0f);
        this.shape11.setTextureSize(64, 32);
        this.shape11.mirror = true;
        this.setRotation(this.shape11, 0.0f, 0.0f, 0.0f);
        this.shape12 = new ModelRenderer((ModelBase)this, 0, 0);
        this.shape12.addBox(0.0f, 0.0f, 0.0f, 2, 1, 2);
        this.shape12.setRotationPoint(-4.0f, 9.0f, -1.0f);
        this.shape12.setTextureSize(64, 32);
        this.shape12.mirror = true;
        this.setRotation(this.shape12, 0.0f, 0.0f, 0.0f);
        this.shape13 = new ModelRenderer((ModelBase)this, 0, 0);
        this.shape13.addBox(0.0f, 0.0f, 0.0f, 1, 2, 2);
        this.shape13.setRotationPoint(1.0f, 10.0f, -1.0f);
        this.shape13.setTextureSize(64, 32);
        this.shape13.mirror = true;
        this.setRotation(this.shape13, 0.0f, 0.0f, 0.0f);
        this.shape14 = new ModelRenderer((ModelBase)this, 0, 0);
        this.shape14.addBox(0.0f, 0.0f, 0.0f, 1, 2, 2);
        this.shape14.setRotationPoint(-2.0f, 10.0f, -1.0f);
        this.shape14.setTextureSize(64, 32);
        this.shape14.mirror = true;
        this.setRotation(this.shape14, 0.0f, 0.0f, 0.0f);
        this.shape15 = new ModelRenderer((ModelBase)this, 0, 0);
        this.shape15.addBox(0.0f, 0.0f, 0.0f, 1, 1, 4);
        this.shape15.setRotationPoint(1.0f, 11.0f, -2.0f);
        this.shape15.setTextureSize(64, 32);
        this.shape15.mirror = true;
        this.setRotation(this.shape15, 0.0f, 0.0f, 0.0f);
        this.shape16 = new ModelRenderer((ModelBase)this, 0, 0);
        this.shape16.addBox(0.0f, 0.0f, 0.0f, 1, 1, 4);
        this.shape16.setRotationPoint(-2.0f, 11.0f, -2.0f);
        this.shape16.setTextureSize(64, 32);
        this.shape16.mirror = true;
        this.setRotation(this.shape16, 0.0f, 0.0f, 0.0f);
        this.shape17 = new ModelRenderer((ModelBase)this, 0, 0);
        this.shape17.addBox(0.0f, 0.0f, 0.0f, 6, 4, 4);
        this.shape17.setRotationPoint(-3.0f, 19.0f, -2.0f);
        this.shape17.setTextureSize(64, 32);
        this.shape17.mirror = true;
        this.setRotation(this.shape17, 0.0f, 0.0f, 0.0f);
        this.shape18 = new ModelRenderer((ModelBase)this, 0, 0);
        this.shape18.addBox(0.0f, 0.0f, 0.0f, 4, 2, 6);
        this.shape18.setRotationPoint(-2.0f, 20.0f, -3.0f);
        this.shape18.setTextureSize(64, 32);
        this.shape18.mirror = true;
        this.setRotation(this.shape18, 0.0f, 0.0f, 0.0f);
        this.shape19 = new ModelRenderer((ModelBase)this, 0, 0);
        this.shape19.addBox(0.0f, 0.0f, 0.0f, 2, 1, 6);
        this.shape19.setRotationPoint(-4.0f, 11.0f, -3.0f);
        this.shape19.setTextureSize(64, 32);
        this.shape19.mirror = true;
        this.setRotation(this.shape19, 0.0f, 0.0f, 0.0f);
        this.shape20 = new ModelRenderer((ModelBase)this, 0, 0);
        this.shape20.addBox(0.0f, 0.0f, 0.0f, 2, 1, 6);
        this.shape20.setRotationPoint(2.0f, 11.0f, -3.0f);
        this.shape20.setTextureSize(64, 32);
        this.shape20.mirror = true;
        this.setRotation(this.shape20, 0.0f, 0.0f, 0.0f);
    }

    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        this.shape1.render(f5);
        this.shape2.render(f5);
        this.shape3.render(f5);
        this.shape4.render(f5);
        this.shape5.render(f5);
        this.shape6.render(f5);
        this.shape7.render(f5);
        this.shape8.render(f5);
        this.shape9.render(f5);
        this.shape10.render(f5);
        this.shape11.render(f5);
        this.shape12.render(f5);
        this.shape13.render(f5);
        this.shape14.render(f5);
        this.shape15.render(f5);
        this.shape16.render(f5);
        this.shape17.render(f5);
        this.shape18.render(f5);
        this.shape19.render(f5);
        this.shape20.render(f5);
    }

    private void setRotation(ModelRenderer model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }
}

