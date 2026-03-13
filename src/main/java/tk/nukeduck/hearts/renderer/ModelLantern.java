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

public class ModelLantern
extends ModelBase {
    ModelRenderer body;
    ModelRenderer lid;
    ModelRenderer chainA;
    ModelRenderer chainB;

    public ModelLantern() {
        this.textureWidth = 32;
        this.textureHeight = 32;
        this.body = new ModelRenderer((ModelBase)this, 0, 0);
        this.body.addBox(0.0f, 0.0f, 0.0f, 6, 8, 6);
        this.body.setRotationPoint(-3.0f, 16.0f, -3.0f);
        this.body.setTextureSize(32, 32);
        this.body.mirror = true;
        this.setRotation(this.body, 0.0f, 0.0f, 0.0f);
        this.lid = new ModelRenderer((ModelBase)this, 0, 14);
        this.lid.addBox(0.0f, 0.0f, 0.0f, 4, 3, 4);
        this.lid.setRotationPoint(-2.0f, 14.0f, -2.0f);
        this.lid.setTextureSize(32, 32);
        this.lid.mirror = true;
        this.setRotation(this.lid, 0.0f, 0.0f, 0.0f);
        this.chainA = new ModelRenderer((ModelBase)this, 0, 18);
        this.chainA.addBox(0.0f, 0.0f, 0.0f, 0, 6, 3);
        this.chainA.setRotationPoint(0.0f, 8.0f, -1.5f);
        this.chainA.setTextureSize(32, 32);
        this.chainA.mirror = true;
        this.setRotation(this.chainA, 0.0f, 0.0f, 0.0f);
        this.chainB = new ModelRenderer((ModelBase)this, 6, 21);
        this.chainB.addBox(0.0f, 0.0f, 0.0f, 3, 6, 0);
        this.chainB.setRotationPoint(-1.5f, 8.0f, 0.0f);
        this.chainB.setTextureSize(32, 32);
        this.chainB.mirror = true;
        this.setRotation(this.chainB, 0.0f, 0.0f, 0.0f);
    }

    public void render(int metadata, float f5) {
        this.render(null, metadata, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, f5);
    }

    public void render(Entity entity, int metadata, float f, float f1, float f2, float f3, float f4, float f5) {
        super.render(entity, f, f1, f2, f3, f4, f5);
        this.body.render(f5);
        this.lid.render(f5);
        if (metadata / 4 == 1) {
            this.chainA.render(f5);
            this.chainB.render(f5);
        }
    }

    private void setRotation(ModelRenderer model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }
}

