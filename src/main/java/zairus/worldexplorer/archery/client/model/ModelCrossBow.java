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

public class ModelCrossBow
extends ModelBase {
    ModelRenderer Handle;
    ModelRenderer MiddleConnection;
    ModelRenderer BowRight;
    ModelRenderer BowLeft;
    ModelRenderer Hook;
    ModelRenderer Trigger;
    ModelRenderer StringLeft;
    ModelRenderer StringRight;

    public ModelCrossBow() {
        this.textureWidth = 64;
        this.textureHeight = 32;
        this.Handle = new ModelRenderer((ModelBase)this, 0, 0);
        this.Handle.addBox(0.0f, 0.0f, 0.0f, 2, 16, 2);
        this.Handle.setRotationPoint(-1.0f, 8.0f, 0.0f);
        this.Handle.setTextureSize(64, 32);
        this.setRotation(this.Handle, 0.0f, 0.0f, 0.0f);
        this.MiddleConnection = new ModelRenderer((ModelBase)this, 14, 0);
        this.MiddleConnection.addBox(0.0f, 0.0f, 0.0f, 4, 1, 1);
        this.MiddleConnection.setRotationPoint(-2.0f, 7.0f, -1.0f);
        this.MiddleConnection.setTextureSize(64, 32);
        this.setRotation(this.MiddleConnection, 0.0f, 0.0f, 0.0f);
        this.BowRight = new ModelRenderer((ModelBase)this, 14, 2);
        this.BowRight.addBox(0.0f, 0.0f, 0.0f, 8, 1, 1);
        this.BowRight.setRotationPoint(2.0f, 7.0f, -1.0f);
        this.BowRight.setTextureSize(64, 32);
        this.setRotation(this.BowRight, 0.0f, 0.0f, 0.5235988f);
        this.BowLeft = new ModelRenderer((ModelBase)this, 14, 2);
        this.BowLeft.addBox(-8.0f, 0.0f, 0.0f, 8, 1, 1);
        this.BowLeft.setRotationPoint(-2.0f, 7.0f, -1.0f);
        this.BowLeft.setTextureSize(64, 32);
        this.setRotation(this.BowLeft, 0.0f, 0.0f, -0.5235988f);
        this.Hook = new ModelRenderer((ModelBase)this, 0, 18);
        this.Hook.addBox(0.0f, 0.0f, 0.0f, 1, 1, 1);
        this.Hook.setRotationPoint(-0.5f, 17.0f, -1.0f);
        this.Hook.setTextureSize(64, 32);
        this.setRotation(this.Hook, 0.0f, 0.0f, 0.0f);
        this.Trigger = new ModelRenderer((ModelBase)this, 8, 0);
        this.Trigger.addBox(0.0f, 0.0f, 0.0f, 1, 10, 2);
        this.Trigger.setRotationPoint(-0.5f, 13.0f, 2.0f);
        this.Trigger.setTextureSize(64, 32);
        this.setRotation(this.Trigger, 0.0f, 0.0f, 0.0f);
        this.StringLeft = new ModelRenderer((ModelBase)this, 14, 4);
        this.StringLeft.addBox(0.0f, 0.0f, 0.0f, 8, 1, 1);
        this.StringLeft.setRotationPoint(-8.0f, 11.0f, -1.0f);
        this.StringLeft.setTextureSize(64, 32);
        this.setRotation(this.StringLeft, 0.0f, 0.0f, 0.0f);
        this.StringRight = new ModelRenderer((ModelBase)this, 14, 4);
        this.StringRight.addBox(-8.0f, 0.0f, 0.0f, 8, 1, 1);
        this.StringRight.setRotationPoint(8.0f, 11.0f, -1.0f);
        this.StringRight.setTextureSize(64, 32);
        this.setRotation(this.StringRight, 0.0f, 0.0f, 0.0f);
    }

    public void setStage(float stagePercent) {
        float bowAngle = 0.5235988f + 0.3f * stagePercent;
        float stringAngle = 0.6f * stagePercent;
        this.setRotation(this.BowRight, 0.0f, 0.0f, bowAngle);
        this.setRotation(this.BowLeft, 0.0f, 0.0f, bowAngle * -1.0f);
        this.setRotation(this.StringRight, 0.0f, 0.0f, stringAngle * -1.0f);
        this.setRotation(this.StringLeft, 0.0f, 0.0f, stringAngle);
        this.StringRight.offsetX = -0.1f * stagePercent;
        this.StringLeft.offsetX = 0.1f * stagePercent;
        this.StringRight.offsetY = 0.1f * stagePercent;
        this.StringLeft.offsetY = 0.1f * stagePercent;
    }

    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        super.render(entity, f, f1, f2, f3, f4, f5);
        this.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
        this.Handle.render(f5);
        this.MiddleConnection.render(f5);
        this.BowRight.render(f5);
        this.BowLeft.render(f5);
        this.Hook.render(f5);
        this.Trigger.render(f5);
        this.StringLeft.render(f5);
        this.StringRight.render(f5);
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

