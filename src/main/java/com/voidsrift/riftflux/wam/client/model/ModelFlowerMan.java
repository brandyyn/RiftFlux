package com.voidsrift.riftflux.wam.client.model;

import com.voidsrift.riftflux.wam.entity.EntityFlowerMan;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;

@SideOnly(Side.CLIENT)public class ModelFlowerMan
extends ModelBase {
    ModelRenderer body;
    ModelRenderer rightleg;
    ModelRenderer leftleg;
    ModelRenderer leftarm;
    ModelRenderer rightarm;
    ModelRenderer neck;
    ModelRenderer head;
    ModelRenderer Petals;
    ModelRenderer face;

    public ModelFlowerMan() {
        this.textureWidth = 128;
        this.textureHeight = 128;
        this.body = new ModelRenderer(this, 0, 18);
        this.body.addBox(-1.0f, 0.0f, -1.0f, 2, 5, 2);
        this.body.setRotationPoint(1.0f, 12.0f, 1.0f);
        this.body.setTextureSize(128, 128);
        this.body.mirror = true;
        this.setRotation(this.body, 0.0f, 0.0f, 0.0f);
        this.rightleg = new ModelRenderer(this, 12, 18);
        this.rightleg.addBox(-1.0f, 0.0f, 0.0f, 1, 7, 2);
        this.rightleg.setRotationPoint(1.0f, 17.0f, 0.0f);
        this.rightleg.setTextureSize(128, 128);
        this.rightleg.mirror = true;
        this.setRotation(this.rightleg, 0.0f, 0.0f, 0.0f);
        this.leftleg = new ModelRenderer(this, 12, 18);
        this.leftleg.addBox(0.0f, 0.0f, 0.0f, 1, 7, 2);
        this.leftleg.setRotationPoint(1.0f, 17.0f, 0.0f);
        this.leftleg.setTextureSize(128, 128);
        this.leftleg.mirror = true;
        this.setRotation(this.leftleg, 0.0f, 0.0f, 0.0f);
        this.leftarm = new ModelRenderer(this, 22, 18);
        this.leftarm.addBox(0.0f, -1.0f, -1.0f, 1, 6, 2);
        this.leftarm.setRotationPoint(2.0f, 13.0f, 1.0f);
        this.leftarm.setTextureSize(128, 128);
        this.leftarm.mirror = true;
        this.setRotation(this.leftarm, 0.0f, 0.0f, 0.0f);
        this.rightarm = new ModelRenderer(this, 22, 18);
        this.rightarm.addBox(-1.0f, -1.0f, -1.0f, 1, 6, 2);
        this.rightarm.setRotationPoint(0.0f, 13.0f, 1.0f);
        this.rightarm.setTextureSize(128, 128);
        this.rightarm.mirror = true;
        this.setRotation(this.rightarm, 0.0f, 0.0f, 0.0f);
        this.neck = new ModelRenderer(this, 0, 8);
        this.neck.addBox(-0.5f, -4.0f, -0.5f, 1, 5, 1);
        this.neck.setRotationPoint(1.0f, 12.0f, 1.0f);
        this.neck.setTextureSize(128, 128);
        this.neck.mirror = true;
        this.setRotation(this.neck, 0.0f, 0.0f, 0.0f);
        this.head = new ModelRenderer(this, 15, 8);
        this.head.addBox(-2.0f, -6.0f, -2.0f, 4, 4, 3);
        this.head.setRotationPoint(1.0f, 12.0f, 1.0f);
        this.head.setTextureSize(128, 128);
        this.head.mirror = true;
        this.setRotation(this.head, 0.0f, 0.0f, 0.0f);
        this.Petals = new ModelRenderer(this, 0, 29);
        this.Petals.addBox(-6.0f, -10.0f, -2.0f, 12, 12, 0);
        this.Petals.setRotationPoint(1.0f, 12.0f, 1.0f);
        this.Petals.setTextureSize(128, 128);
        this.Petals.mirror = true;
        this.setRotation(this.Petals, 0.0f, 0.0f, 0.0f);
        this.face = new ModelRenderer(this, 19, 0);
        this.face.addBox(-2.0f, -6.0f, -2.5f, 4, 4, 1);
        this.face.setRotationPoint(1.0f, 12.0f, 1.0f);
        this.face.setTextureSize(128, 128);
        this.face.mirror = true;
        this.setRotation(this.face, 0.0f, 0.0f, 0.0f);
    }

    public void render(Entity var1, float var2, float var3, float var4, float var5, float var6, float var7) {
        this.setRotationAngles(var2, var3, var4, var5, var6, var7, var1);
        this.body.render(var7);
        this.rightleg.render(var7);
        this.leftleg.render(var7);
        this.leftarm.render(var7);
        this.rightarm.render(var7);
        this.neck.render(var7);
        this.head.render(var7);
        this.Petals.render(var7);
        this.face.render(var7);
    }

    private void setRotation(ModelRenderer var1, float var2, float var3, float var4) {
        var1.rotateAngleX = var2;
        var1.rotateAngleY = var3;
        var1.rotateAngleZ = var4;
    }

    public void setRotationAngles(float var1, float var2, float var3, float var4, float var5, float var6, Entity entity) {
        this.head.rotateAngleY = var4 / 57.295776f;
        this.head.rotateAngleX = var5 / 57.295776f;
        this.face.rotateAngleY = this.head.rotateAngleY;
        this.face.rotateAngleX = this.head.rotateAngleX;
        this.neck.rotateAngleY = this.head.rotateAngleY;
        this.neck.rotateAngleX = this.head.rotateAngleX;
        this.Petals.rotateAngleY = this.head.rotateAngleY;
        this.Petals.rotateAngleX = this.head.rotateAngleX;
        this.rightarm.rotateAngleX = MathHelper.cos((float)(var1 * 0.6662f + (float)Math.PI)) * 2.0f * var2 * 0.5f;
        this.leftarm.rotateAngleX = MathHelper.cos((float)(var1 * 0.6662f)) * 2.0f * var2 * 0.5f;
        this.rightarm.rotateAngleZ = 0.0f;
        this.leftarm.rotateAngleZ = 0.0f;
        this.rightleg.rotateAngleX = MathHelper.cos((float)(var1 * 0.6662f)) * 1.4f * var2;
        this.leftleg.rotateAngleX = MathHelper.cos((float)(var1 * 0.6662f + (float)Math.PI)) * 1.4f * var2;
        this.rightleg.rotateAngleY = 0.0f;
        this.leftleg.rotateAngleY = 0.0f;
        this.rightarm.rotateAngleZ += MathHelper.cos((float)(var3 * 0.09f)) * 0.05f + 0.05f;
        this.leftarm.rotateAngleZ -= MathHelper.cos((float)(var3 * 0.09f)) * 0.05f + 0.05f;
        this.rightarm.rotateAngleX += MathHelper.sin((float)(var3 * 0.067f)) * 0.05f;
        this.leftarm.rotateAngleX -= MathHelper.sin((float)(var3 * 0.067f)) * 0.05f;
    }
}

