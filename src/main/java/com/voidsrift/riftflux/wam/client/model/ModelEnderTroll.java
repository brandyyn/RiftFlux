package com.voidsrift.riftflux.wam.client.model;

import com.voidsrift.riftflux.wam.entity.EntityEnderTroll;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;

@SideOnly(Side.CLIENT)public class ModelEnderTroll
extends ModelBase {
    ModelRenderer ClubArm;
    ModelRenderer Club;
    ModelRenderer Head;
    ModelRenderer Belly;
    ModelRenderer Body;
    ModelRenderer RightArm;
    ModelRenderer LeftArm;
    ModelRenderer RightLeg;
    ModelRenderer LeftLeg;

    public ModelEnderTroll() {
        this.textureWidth = 128;
        this.textureHeight = 64;
        this.ClubArm = new ModelRenderer(this, 108, 25);
        this.ClubArm.addBox(-1.0f, 17.0f, -5.0f, 2, 2, 6);
        this.ClubArm.setRotationPoint(-5.0f, -12.0f, 0.0f);
        this.ClubArm.setTextureSize(128, 64);
        this.ClubArm.mirror = true;
        this.setRotation(this.ClubArm, 0.0f, 0.0f, 0.0f);
        this.Club = new ModelRenderer(this, 81, 0);
        this.Club.addBox(-3.0f, 15.0f, -20.0f, 6, 6, 16);
        this.Club.setRotationPoint(-5.0f, -12.0f, 0.0f);
        this.Club.setTextureSize(128, 64);
        this.Club.mirror = true;
        this.setRotation(this.Club, 0.0f, 0.0f, 0.0f);
        this.Head = new ModelRenderer(this, 0, 0);
        this.Head.addBox(-4.0f, -8.0f, -4.0f, 8, 9, 8);
        this.Head.setRotationPoint(0.0f, -14.0f, 0.0f);
        this.Head.setTextureSize(128, 64);
        this.Head.mirror = true;
        this.setRotation(this.Head, 0.0f, 0.0f, 0.0f);
        this.Belly = new ModelRenderer(this, 0, 19);
        this.Belly.addBox(-4.0f, 7.0f, -5.0f, 8, 8, 8);
        this.Belly.setRotationPoint(0.0f, -14.0f, 0.0f);
        this.Belly.setTextureSize(128, 64);
        this.Belly.mirror = true;
        this.setRotation(this.Belly, 0.0f, 0.0f, 0.0f);
        this.Body = new ModelRenderer(this, 0, 40);
        this.Body.addBox(-4.0f, 1.0f, -2.0f, 8, 16, 4);
        this.Body.setRotationPoint(0.0f, -14.0f, 0.0f);
        this.Body.setTextureSize(128, 64);
        this.Body.mirror = true;
        this.setRotation(this.Body, 0.0f, 0.0f, 0.0f);
        this.RightArm = new ModelRenderer(this, 43, 0);
        this.RightArm.addBox(-1.0f, -2.0f, -1.0f, 2, 21, 2);
        this.RightArm.setRotationPoint(-5.0f, -12.0f, 0.0f);
        this.RightArm.setTextureSize(128, 64);
        this.RightArm.mirror = true;
        this.setRotation(this.RightArm, 0.0f, 0.0f, 0.0f);
        this.LeftArm = new ModelRenderer(this, 43, 0);
        this.LeftArm.addBox(-1.0f, -2.0f, -1.0f, 2, 21, 2);
        this.LeftArm.setRotationPoint(5.0f, -12.0f, 0.0f);
        this.LeftArm.setTextureSize(128, 64);
        this.LeftArm.mirror = true;
        this.setRotation(this.LeftArm, 0.0f, 0.0f, 0.0f);
        this.RightLeg = new ModelRenderer(this, 56, 0);
        this.RightLeg.addBox(-1.0f, 0.0f, -1.0f, 2, 21, 2);
        this.RightLeg.setRotationPoint(-2.0f, 3.0f, 0.0f);
        this.RightLeg.setTextureSize(128, 64);
        this.RightLeg.mirror = true;
        this.setRotation(this.RightLeg, 0.0f, 0.0f, 0.0f);
        this.LeftLeg = new ModelRenderer(this, 56, 0);
        this.LeftLeg.addBox(-1.0f, 0.0f, -1.0f, 2, 21, 2);
        this.LeftLeg.setRotationPoint(2.0f, 3.0f, 0.0f);
        this.LeftLeg.setTextureSize(128, 64);
        this.LeftLeg.mirror = true;
        this.setRotation(this.LeftLeg, 0.0f, 0.0f, 0.0f);
    }

    public void render(Entity var1, float var2, float var3, float var4, float var5, float var6, float var7) {
        this.setRotationAngles(var2, var3, var4, var5, var6, var7, var1);
        this.Head.render(var7);
        this.Body.render(var7);
        this.Belly.render(var7);
        this.RightArm.render(var7);
        this.LeftArm.render(var7);
        this.RightLeg.render(var7);
        this.LeftLeg.render(var7);
        this.ClubArm.render(var7);
        this.Club.render(var7);
    }

    private void setRotation(ModelRenderer var1, float var2, float var3, float var4) {
        var1.rotateAngleX = var2;
        var1.rotateAngleY = var3;
        var1.rotateAngleZ = var4;
    }

    public void setRotationAngles(float var1, float var2, float var3, float var4, float var5, float var6, Entity par7Entity) {
        this.Head.rotateAngleY = var4 / 57.295776f;
        this.Head.rotateAngleX = var5 / 57.295776f;
        this.RightLeg.rotateAngleX = -1.5f * this.func_48228_a(var1, 13.0f) * var2;
        this.LeftLeg.rotateAngleX = 1.5f * this.func_48228_a(var1, 13.0f) * var2;
        this.RightLeg.rotateAngleY = 0.0f;
        this.LeftLeg.rotateAngleY = 0.0f;
        this.ClubArm.rotateAngleZ = this.RightArm.rotateAngleZ = 0.0f;
        this.Club.rotateAngleZ = this.RightArm.rotateAngleZ;
        this.LeftArm.rotateAngleZ = 0.0f;
        this.RightArm.rotateAngleZ += MathHelper.cos((float)(var3 * 0.09f)) * 0.05f + 0.05f;
        this.ClubArm.rotateAngleZ += MathHelper.cos((float)(var3 * 0.09f)) * 0.05f + 0.05f;
        this.Club.rotateAngleZ += MathHelper.cos((float)(var3 * 0.09f)) * 0.05f + 0.05f;
        this.LeftArm.rotateAngleZ -= MathHelper.cos((float)(var3 * 0.09f)) * 0.05f + 0.05f;
        this.RightArm.rotateAngleX += MathHelper.sin((float)(var3 * 0.067f)) * 0.05f;
        this.ClubArm.rotateAngleX += MathHelper.sin((float)(var3 * 0.067f)) * 0.05f;
        this.Club.rotateAngleX += MathHelper.sin((float)(var3 * 0.067f)) * 0.05f;
        this.LeftArm.rotateAngleX -= MathHelper.sin((float)(var3 * 0.067f)) * 0.05f;
    }

    public void setLivingAnimations(EntityLivingBase var1, float var2, float var3, float var4) {
        int var6 = var1 instanceof EntityEnderTroll ? ((EntityEnderTroll)var1).getAttackTimer() : 0;
        if (var6 > 0) {
            this.RightArm.rotateAngleX = -2.0f + 1.5f * this.func_48228_a((float)var6 - var4, 10.0f);
            this.LeftArm.rotateAngleX = -1.0f + 0.5f * this.func_48228_a((float)var6 - var4, 10.0f);
            this.ClubArm.rotateAngleX = this.RightArm.rotateAngleX;
            this.Club.rotateAngleX = this.RightArm.rotateAngleX;
        } else {
            this.RightArm.rotateAngleX = (-0.2f + 1.5f * this.func_48228_a(var2, 13.0f)) * var3;
            this.LeftArm.rotateAngleX = (-0.1f - 0.5f * this.func_48228_a(var2, 13.0f)) * var3;
            this.ClubArm.rotateAngleX = this.RightArm.rotateAngleX;
            this.Club.rotateAngleX = this.RightArm.rotateAngleX;
        }
    }

    private float func_48228_a(float var1, float var2) {
        return (Math.abs(var1 % var2 - var2 * 0.5f) - var2 * 0.25f) / (var2 * 0.25f);
    }
}

