package com.voidsrift.riftflux.palaria.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;

public class ModelCreeptile
extends ModelBase {
    ModelRenderer body;
    ModelRenderer head;
    ModelRenderer tail1;
    ModelRenderer tail2;
    ModelRenderer tail3;
    ModelRenderer tail4;
    ModelRenderer eye1;
    ModelRenderer eye2;
    ModelRenderer frontleftleg;
    ModelRenderer frontleftfoot;
    ModelRenderer frontrightleg;
    ModelRenderer frontrightfoot;
    ModelRenderer backleftleg;
    ModelRenderer backleftfoot;
    ModelRenderer backrightleg;
    ModelRenderer backrightfoot;

    public ModelCreeptile() {
        this(0.0f);
    }

    public ModelCreeptile(float par1) {
        this.textureWidth = 128;
        this.textureHeight = 128;
        this.body = new ModelRenderer(this, 42, 52);
        this.body.addBox(0.0f, 0.0f, 0.0f, 7, 6, 13);
        this.body.setRotationPoint(-3.5f, 13.0f, -6.0f);
        this.body.setTextureSize(128, 128);
        this.body.mirror = true;
        this.setRotation(this.body, 0.0f, 0.0f, 0.0f);
        this.head = new ModelRenderer(this, 46, 18);
        this.head.addBox(-3.5f, -3.0f, -9.0f, 7, 5, 9);
        this.head.setRotationPoint(0.0f, 13.5f, -5.0f);
        this.head.setTextureSize(128, 128);
        this.head.mirror = true;
        this.setRotation(this.head, 0.0f, 0.0f, 0.0f);
        this.tail1 = new ModelRenderer(this, 52, 78);
        this.tail1.addBox(-2.5f, -1.0f, 0.0f, 5, 3, 6);
        this.tail1.setRotationPoint(0.0f, 14.5f, 6.0f);
        this.tail1.setTextureSize(128, 128);
        this.tail1.mirror = true;
        this.setRotation(this.tail1, 0.0f, 0.0f, 0.0f);
        this.tail2 = new ModelRenderer(this, 48, 59);
        this.tail2.addBox(-2.0f, -1.0f, 0.0f, 4, 2, 10);
        this.tail2.setRotationPoint(0.0f, 15.0f, 7.0f);
        this.tail2.setTextureSize(128, 128);
        this.tail2.mirror = true;
        this.setRotation(this.tail2, 0.0f, 0.0f, 0.0f);
        this.tail3 = new ModelRenderer(this, 42, 57);
        this.tail3.addBox(-1.5f, -0.5f, 0.0f, 3, 1, 13);
        this.tail3.setRotationPoint(0.0f, 15.0f, 7.0f);
        this.tail3.setTextureSize(128, 128);
        this.tail3.mirror = true;
        this.setRotation(this.tail3, 0.0f, 0.0f, 0.0f);
        this.tail4 = new ModelRenderer(this, 45, 55);
        this.tail4.addBox(-0.5f, -0.5f, 0.0f, 1, 1, 15);
        this.tail4.setRotationPoint(0.0f, 15.5f, 7.0f);
        this.tail4.setTextureSize(128, 128);
        this.tail4.mirror = true;
        this.setRotation(this.tail4, 0.0f, 0.0f, 0.0f);
        this.eye1 = new ModelRenderer(this, 47, 10);
        this.eye1.addBox(1.0f, -4.0f, -4.0f, 2, 2, 2);
        this.eye1.setRotationPoint(0.0f, 13.0f, -5.0f);
        this.eye1.setTextureSize(128, 128);
        this.eye1.mirror = true;
        this.setRotation(this.eye1, 0.0f, 0.0f, 0.0f);
        this.eye2 = new ModelRenderer(this, 69, 10);
        this.eye2.addBox(-3.0f, -4.0f, -4.0f, 2, 2, 2);
        this.eye2.setRotationPoint(0.0f, 13.0f, -5.0f);
        this.eye2.setTextureSize(128, 128);
        this.eye2.mirror = true;
        this.setRotation(this.eye2, 0.0f, 0.0f, 0.0f);
        this.frontleftleg = new ModelRenderer(this, 22, 52);
        this.frontleftleg.addBox(0.0f, 0.0f, 0.0f, 4, 4, 4);
        this.frontleftleg.setRotationPoint(3.0f, 16.0f, -4.0f);
        this.frontleftleg.setTextureSize(128, 128);
        this.frontleftleg.mirror = true;
        this.setRotation(this.frontleftleg, 0.0f, 0.0f, 0.0f);
        this.frontleftfoot = new ModelRenderer(this, 11, 39);
        this.frontleftfoot.addBox(-2.0f, 0.0f, -2.0f, 4, 4, 4);
        this.frontleftfoot.setRotationPoint(5.0f, 20.0f, -4.0f);
        this.frontleftfoot.setTextureSize(128, 128);
        this.frontleftfoot.mirror = true;
        this.setRotation(this.frontleftfoot, 0.0f, 0.0f, 0.0f);
        this.frontrightleg = new ModelRenderer(this, 86, 52);
        this.frontrightleg.addBox(-4.0f, 0.0f, 0.0f, 4, 4, 4);
        this.frontrightleg.setRotationPoint(-3.0f, 16.0f, -4.0f);
        this.frontrightleg.setTextureSize(128, 128);
        this.frontrightleg.mirror = true;
        this.setRotation(this.frontrightleg, 0.0f, 0.0f, 0.0f);
        this.frontrightfoot = new ModelRenderer(this, 98, 39);
        this.frontrightfoot.addBox(-2.0f, 0.0f, -2.0f, 4, 4, 4);
        this.frontrightfoot.setRotationPoint(-5.0f, 20.0f, -4.0f);
        this.frontrightfoot.setTextureSize(128, 128);
        this.frontrightfoot.mirror = true;
        this.setRotation(this.frontrightfoot, 0.0f, 0.0f, 0.0f);
        this.backleftleg = new ModelRenderer(this, 22, 69);
        this.backleftleg.addBox(0.0f, 0.0f, 0.0f, 4, 4, 4);
        this.backleftleg.setRotationPoint(3.0f, 16.0f, 3.0f);
        this.backleftleg.setTextureSize(128, 128);
        this.backleftleg.mirror = true;
        this.setRotation(this.backleftleg, 0.0f, 0.0f, 0.0f);
        this.backleftfoot = new ModelRenderer(this, 11, 81);
        this.backleftfoot.addBox(-2.0f, 0.0f, -2.0f, 4, 4, 4);
        this.backleftfoot.setRotationPoint(5.0f, 20.0f, 7.0f);
        this.backleftfoot.setTextureSize(128, 128);
        this.backleftfoot.mirror = true;
        this.setRotation(this.backleftfoot, 0.0f, 0.0f, 0.0f);
        this.backrightleg = new ModelRenderer(this, 87, 68);
        this.backrightleg.addBox(-4.0f, 0.0f, 0.0f, 4, 4, 4);
        this.backrightleg.setRotationPoint(-3.0f, 16.0f, 3.0f);
        this.backrightleg.setTextureSize(128, 128);
        this.backrightleg.mirror = true;
        this.setRotation(this.backrightleg, 0.0f, 0.0f, 0.0f);
        this.backrightfoot = new ModelRenderer(this, 98, 80);
        this.backrightfoot.addBox(-2.0f, 0.0f, -2.0f, 4, 4, 4);
        this.backrightfoot.setRotationPoint(-5.0f, 20.0f, 7.0f);
        this.backrightfoot.setTextureSize(128, 128);
        this.backrightfoot.mirror = true;
        this.setRotation(this.backrightfoot, 0.0f, 0.0f, 0.0f);
    }

    public void render(Entity par1Entity, float par2, float par3, float par4, float par5, float par6, float par7) {
        super.render(par1Entity, par2, par3, par4, par5, par6, par7);
        this.setRotationAngles(par2, par3, par4, par5, par6, par7, par1Entity);
        this.body.render(par7);
        this.head.render(par7);
        this.tail1.render(par7);
        this.tail2.render(par7);
        this.tail3.render(par7);
        this.tail4.render(par7);
        this.eye1.render(par7);
        this.eye2.render(par7);
        this.frontleftleg.render(par7);
        this.frontleftfoot.render(par7);
        this.frontrightleg.render(par7);
        this.frontrightfoot.render(par7);
        this.backleftleg.render(par7);
        this.backleftfoot.render(par7);
        this.backrightleg.render(par7);
        this.backrightfoot.render(par7);
    }

    private void setRotation(ModelRenderer model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }

    public void setRotationAngles(float par1, float par2, float par3, float par4, float par5, float par6, Entity par7Entity) {
        super.setRotationAngles(par1, par2, par3, par4, par5, par6, par7Entity);
        this.head.rotateAngleY = par4 / 57.295776f;
        this.head.rotateAngleX = par5 / 57.295776f;
        this.eye1.rotateAngleY = par4 / 57.295776f;
        this.eye1.rotateAngleX = par5 / 57.295776f;
        this.eye2.rotateAngleY = par4 / 57.295776f;
        this.eye2.rotateAngleX = par5 / 57.295776f;
        this.frontleftfoot.rotateAngleX = MathHelper.cos(par1 * 0.6662f) * 1.4f * par2;
        this.frontrightfoot.rotateAngleX = MathHelper.cos(par1 * 0.6662f + (float)Math.PI) * 1.4f * par2;
        this.backleftfoot.rotateAngleX = MathHelper.cos(par1 * 0.6662f + (float)Math.PI) * 1.4f * par2;
        this.backrightfoot.rotateAngleX = MathHelper.cos(par1 * 0.6662f) * 1.4f * par2;
        this.tail1.rotateAngleY = MathHelper.cos(par1 * 0.6662f) * 0.3f * par2;
        this.tail1.rotateAngleX = MathHelper.cos(par1 * 0.6662f) * 0.1f * par2;
        this.tail2.rotateAngleY = MathHelper.cos(par1 * 0.6662f) * 0.4f * par2;
        this.tail2.rotateAngleX = MathHelper.cos(par1 * 0.6662f) * 0.1f * par2;
        this.tail3.rotateAngleY = MathHelper.cos(par1 * 0.6662f) * 0.5f * par2;
        this.tail3.rotateAngleX = MathHelper.cos(par1 * 0.6662f) * 0.1f * par2;
        this.tail4.rotateAngleY = MathHelper.cos(par1 * 0.6662f) * 0.6f * par2;
        this.tail4.rotateAngleX = MathHelper.cos(par1 * 0.6662f) * 0.1f * par2;
    }
}


