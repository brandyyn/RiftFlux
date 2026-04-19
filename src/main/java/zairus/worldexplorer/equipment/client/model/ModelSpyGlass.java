/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.model.ModelBase
 *  net.minecraft.client.model.ModelRenderer
 *  net.minecraft.entity.Entity
 */
package zairus.worldexplorer.equipment.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelSpyGlass
extends ModelBase {
    ModelRenderer eyelid;
    ModelRenderer handle;
    ModelRenderer magnifier;

    public ModelSpyGlass() {
        this.textureWidth = 64;
        this.textureHeight = 32;
        this.eyelid = new ModelRenderer((ModelBase)this, 0, 14);
        this.eyelid.addBox(0.0f, 0.0f, 0.0f, 1, 3, 1);
        this.eyelid.setRotationPoint(0.0f, 21.0f, 0.0f);
        this.eyelid.setTextureSize(64, 32);
        this.setRotation(this.eyelid, 0.0f, 0.0f, 0.0f);
        this.handle = new ModelRenderer((ModelBase)this, 0, 8);
        this.handle.addBox(0.0f, 0.0f, 0.0f, 2, 4, 2);
        this.handle.setRotationPoint(-0.5f, 17.0f, -0.5f);
        this.handle.setTextureSize(64, 32);
        this.setRotation(this.handle, 0.0f, 0.0f, 0.0f);
        this.magnifier = new ModelRenderer((ModelBase)this, 0, 0);
        this.magnifier.addBox(0.0f, 0.0f, 0.0f, 3, 5, 3);
        this.magnifier.setRotationPoint(-1.0f, 12.0f, -1.0f);
        this.magnifier.setTextureSize(64, 32);
        this.magnifier.mirror = true;
        this.setRotation(this.magnifier, 0.0f, 0.0f, 0.0f);
    }

    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        super.render(entity, f, f1, f2, f3, f4, f5);
        this.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
        this.eyelid.render(f5);
        this.handle.render(f5);
        this.magnifier.render(f5);
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

