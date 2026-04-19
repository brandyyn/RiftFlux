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

public class ModelBlowPipe
extends ModelBase {
    ModelRenderer MainStraw;
    ModelRenderer Binding1;
    ModelRenderer Binding2;

    public ModelBlowPipe() {
        this.textureWidth = 32;
        this.textureHeight = 32;
        this.MainStraw = new ModelRenderer((ModelBase)this, 0, 3);
        this.MainStraw.addBox(0.0f, 0.0f, 0.0f, 1, 16, 1);
        this.MainStraw.setRotationPoint(0.0f, 8.0f, 0.0f);
        this.MainStraw.setTextureSize(32, 32);
        this.setRotation(this.MainStraw, 0.0f, 0.0f, 0.0f);
        this.Binding1 = new ModelRenderer((ModelBase)this, 0, 0);
        this.Binding1.addBox(0.0f, 0.0f, 0.0f, 2, 1, 2);
        this.Binding1.setRotationPoint(-0.5f, 21.0f, -0.5f);
        this.Binding1.setTextureSize(32, 32);
        this.setRotation(this.Binding1, 0.0f, 0.0f, 0.0f);
        this.Binding2 = new ModelRenderer((ModelBase)this, 0, 0);
        this.Binding2.addBox(0.0f, 0.0f, 0.0f, 2, 1, 2);
        this.Binding2.setRotationPoint(-0.5f, 17.0f, -0.5f);
        this.Binding2.setTextureSize(32, 32);
        this.setRotation(this.Binding2, 0.0f, 0.0f, 0.0f);
    }

    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        super.render(entity, f, f1, f2, f3, f4, f5);
        this.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
        this.MainStraw.render(f5);
        this.Binding1.render(f5);
        this.Binding2.render(f5);
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

