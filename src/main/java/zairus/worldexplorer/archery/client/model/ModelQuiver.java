/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.model.ModelBase
 *  net.minecraft.client.model.ModelRenderer
 *  net.minecraft.entity.Entity
 */
package zairus.worldexplorer.archery.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelQuiver
extends ModelBase {
    ModelRenderer QuiverBody;
    ModelRenderer QuiverBottom;
    ModelRenderer QuiverTop;

    public ModelQuiver() {
        this.textureWidth = 64;
        this.textureHeight = 32;
        this.QuiverBody = new ModelRenderer((ModelBase)this, 0, 5);
        this.QuiverBody.addBox(0.0f, -4.0f, 0.0f, 4, 6, 2);
        this.QuiverBody.setRotationPoint(-2.0f, 6.0f, 2.0f);
        this.QuiverBody.setTextureSize(64, 32);
        this.setRotation(this.QuiverBody, 0.0f, 0.0f, 0.0f);
        this.QuiverBottom = new ModelRenderer((ModelBase)this, 0, 13);
        this.QuiverBottom.addBox(0.0f, 0.0f, 0.0f, 3, 2, 2);
        this.QuiverBottom.setRotationPoint(-1.5f, 8.0f, 2.0f);
        this.QuiverBottom.setTextureSize(64, 32);
        this.setRotation(this.QuiverBottom, 0.0f, 0.0f, 0.0f);
        this.QuiverTop = new ModelRenderer((ModelBase)this, 0, 0);
        this.QuiverTop.addBox(0.0f, 0.0f, 0.0f, 5, 2, 3);
        this.QuiverTop.setRotationPoint(-2.5f, 1.0f, 1.5f);
        this.QuiverTop.setTextureSize(64, 32);
        this.setRotation(this.QuiverTop, 0.0f, 0.0f, 0.0f);
    }

    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        super.render(entity, f, f1, f2, f3, f4, f5);
        this.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
        this.QuiverBody.render(f5);
        this.QuiverBottom.render(f5);
        this.QuiverTop.render(f5);
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

