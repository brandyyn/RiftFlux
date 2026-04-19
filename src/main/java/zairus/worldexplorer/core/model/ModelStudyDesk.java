/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.model.ModelBase
 *  net.minecraft.client.model.ModelRenderer
 *  net.minecraft.entity.Entity
 */
package zairus.worldexplorer.core.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelStudyDesk
extends ModelBase {
    ModelRenderer LeftPane;
    ModelRenderer RightPane;
    ModelRenderer RearPane;
    ModelRenderer Surface;
    ModelRenderer Chest;
    ModelRenderer ChestLock;
    ModelRenderer BookBackCover;
    ModelRenderer BookFrontCover;
    ModelRenderer BookPages;

    public ModelStudyDesk() {
        this.textureWidth = 64;
        this.textureHeight = 96;
        this.LeftPane = new ModelRenderer((ModelBase)this, 0, 35);
        this.LeftPane.addBox(0.0f, 0.0f, 0.0f, 2, 12, 16);
        this.LeftPane.setRotationPoint(-8.0f, 12.0f, -8.0f);
        this.LeftPane.setTextureSize(64, 96);
        this.setRotation(this.LeftPane, 0.0f, 0.0f, 0.0f);
        this.RightPane = new ModelRenderer((ModelBase)this, 0, 35);
        this.RightPane.addBox(0.0f, 0.0f, 0.0f, 2, 12, 16);
        this.RightPane.setRotationPoint(6.0f, 12.0f, -8.0f);
        this.RightPane.setTextureSize(64, 96);
        this.setRotation(this.RightPane, 0.0f, 0.0f, 0.0f);
        this.RearPane = new ModelRenderer((ModelBase)this, 36, 49);
        this.RearPane.addBox(0.0f, 0.0f, 0.0f, 12, 12, 2);
        this.RearPane.setRotationPoint(-6.0f, 12.0f, 6.0f);
        this.RearPane.setTextureSize(64, 96);
        this.setRotation(this.RearPane, 0.0f, 0.0f, 0.0f);
        this.Surface = new ModelRenderer((ModelBase)this, 0, 17);
        this.Surface.addBox(0.0f, 0.0f, 0.0f, 16, 2, 16);
        this.Surface.setRotationPoint(-8.0f, 10.0f, -8.0f);
        this.Surface.setTextureSize(64, 96);
        this.setRotation(this.Surface, 0.0f, 0.0f, 0.0f);
        this.Chest = new ModelRenderer((ModelBase)this, 0, 63);
        this.Chest.addBox(0.0f, 0.0f, 0.0f, 10, 10, 10);
        this.Chest.setRotationPoint(-5.0f, 14.0f, -5.0f);
        this.Chest.setTextureSize(64, 96);
        this.setRotation(this.Chest, 0.0f, 0.0f, 0.0f);
        this.ChestLock = new ModelRenderer((ModelBase)this, 0, 0);
        this.ChestLock.addBox(0.0f, 0.0f, 0.0f, 1, 2, 1);
        this.ChestLock.setRotationPoint(-0.5f, 15.5f, -6.0f);
        this.ChestLock.setTextureSize(64, 96);
        this.setRotation(this.ChestLock, 0.0f, 0.0f, 0.0f);
        this.BookBackCover = new ModelRenderer((ModelBase)this, 22, 10);
        this.BookBackCover.addBox(0.0f, 0.0f, 0.0f, 4, 0, 7);
        this.BookBackCover.setRotationPoint(4.0f, 9.9f, 1.0f);
        this.BookBackCover.setTextureSize(64, 96);
        this.setRotation(this.BookBackCover, 0.0f, 0.0f, 0.0f);
        this.BookFrontCover = new ModelRenderer((ModelBase)this, 0, 10);
        this.BookFrontCover.addBox(0.0f, 0.0f, 0.0f, 4, 0, 7);
        this.BookFrontCover.setRotationPoint(4.0f, 8.9f, 1.0f);
        this.BookFrontCover.setTextureSize(64, 96);
        this.setRotation(this.BookFrontCover, 0.0f, 0.0f, 0.0f);
        this.BookPages = new ModelRenderer((ModelBase)this, 0, 3);
        this.BookPages.addBox(0.0f, 0.0f, 0.0f, 4, 1, 6);
        this.BookPages.setRotationPoint(4.0f, 9.0f, 1.5f);
        this.BookPages.setTextureSize(64, 96);
        this.setRotation(this.BookPages, 0.0f, 0.0f, 0.0f);
    }

    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        super.render(entity, f, f1, f2, f3, f4, f5);
        this.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
        this.LeftPane.render(f5);
        this.RightPane.render(f5);
        this.RearPane.render(f5);
        this.Surface.render(f5);
        this.Chest.render(f5);
        this.ChestLock.render(f5);
        this.BookBackCover.render(f5);
        this.BookFrontCover.render(f5);
        this.BookPages.render(f5);
    }

    public void renderModel(float f5) {
        this.LeftPane.render(f5);
        this.RightPane.render(f5);
        this.RearPane.render(f5);
        this.Surface.render(f5);
        this.Chest.render(f5);
        this.ChestLock.render(f5);
        this.BookBackCover.render(f5);
        this.BookFrontCover.render(f5);
        this.BookPages.render(f5);
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

