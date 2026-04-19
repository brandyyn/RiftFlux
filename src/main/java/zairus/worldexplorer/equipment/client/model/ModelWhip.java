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

public class ModelWhip
extends ModelBase {
    public ModelRenderer modelRenderer;

    public ModelWhip() {
        this(0, 0, 32, 32);
    }

    public ModelWhip(int textureOffsetX, int textureOffsetY, int textureWidth, int textureHeight) {
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
        this.modelRenderer = new ModelRenderer((ModelBase)this, textureOffsetX, textureOffsetY);
        this.modelRenderer.addBox(0.0f, 0.0f, 0.0f, 2, 10, 2, 0.0f);
        this.modelRenderer.setRotationPoint(0.0f, 0.0f, 0.0f);
    }

    public ModelWhip(WhipPart part) {
        switch (part) {
            case partBody: {
                this.textureWidth = 32;
                this.textureHeight = 32;
                this.modelRenderer = new ModelRenderer((ModelBase)this, 16, 0);
                this.modelRenderer.addBox(0.0f, 0.0f, 0.0f, 1, 10, 1, 0.0f);
                this.modelRenderer.setRotationPoint(0.0f, 0.0f, 0.0f);
                break;
            }
            default: {
                this.textureWidth = 32;
                this.textureHeight = 32;
                this.modelRenderer = new ModelRenderer((ModelBase)this, 0, 0);
                this.modelRenderer.addBox(0.0f, 0.0f, 0.0f, 2, 10, 2, 0.0f);
                this.modelRenderer.setRotationPoint(0.0f, 0.0f, 0.0f);
            }
        }
    }

    public void render(Entity p_78088_1_, float p_78088_2_, float p_78088_3_, float p_78088_4_, float p_78088_5_, float p_78088_6_, float p_78088_7_) {
        this.setRotationAngles(p_78088_2_, p_78088_3_, p_78088_4_, p_78088_5_, p_78088_6_, p_78088_7_, p_78088_1_);
        this.modelRenderer.render(p_78088_7_);
    }

    public void setRotationAngles(float p_78087_1_, float p_78087_2_, float p_78087_3_, float p_78087_4_, float p_78087_5_, float p_78087_6_, Entity p_78087_7_) {
        super.setRotationAngles(p_78087_1_, p_78087_2_, p_78087_3_, p_78087_4_, p_78087_5_, p_78087_6_, p_78087_7_);
        this.modelRenderer.rotateAngleY = p_78087_4_ / 57.295776f;
        this.modelRenderer.rotateAngleX = p_78087_5_ / 57.295776f;
    }

    public static enum WhipPart {
        partHandle,
        partBody;

    }
}

