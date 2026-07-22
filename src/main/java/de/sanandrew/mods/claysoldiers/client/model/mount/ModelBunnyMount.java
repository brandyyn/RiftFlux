/*
 * Decompiled with CFR 0.152.
 */
package de.sanandrew.mods.claysoldiers.client.model.mount;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;

@SideOnly(value=Side.CLIENT)
public class ModelBunnyMount
extends ModelBase {
    public ModelRenderer head = new ModelRenderer(this, 0, 0);
    public ModelRenderer body;
    public ModelRenderer leftLegFront;
    public ModelRenderer rightLegFront;
    public ModelRenderer leftLegBack;
    public ModelRenderer rightLegBack;
    public ModelRenderer earRight;
    public ModelRenderer earLeft;
    public ModelRenderer tail;

    public ModelBunnyMount() {
        this.head.addBox(-1.0f, -1.0f, -2.0f, 2, 2, 2);
        this.head.setRotationPoint(0.0f, 21.5f, -1.0f);
        this.body = new ModelRenderer(this, 0, 4);
        this.body.addBox(-1.5f, -2.0f, -1.5f, 3, 3, 2);
        this.body.setRotationPoint(0.0f, 21.5f, 1.0f);
        ModelBunnyMount.setRotation(this.body, 1.570796f, 0.0f, 0.0f);
        this.leftLegFront = new ModelRenderer(this, 0, 9);
        this.leftLegFront.addBox(-1.0f, 0.0f, 0.0f, 1, 1, 1);
        this.leftLegFront.setRotationPoint(-0.5f, 23.0f, 1.0f);
        this.rightLegFront = new ModelRenderer(this, 0, 9);
        this.rightLegFront.addBox(0.0f, 0.0f, 0.0f, 1, 1, 1);
        this.rightLegFront.setRotationPoint(0.5f, 23.0f, 1.0f);
        this.rightLegFront.mirror = true;
        this.leftLegBack = new ModelRenderer(this, 0, 9);
        this.leftLegBack.addBox(-1.0f, 0.0f, -1.0f, 1, 1, 1);
        this.leftLegBack.setRotationPoint(-0.5f, 23.0f, 0.0f);
        this.rightLegBack = new ModelRenderer(this, 0, 9);
        this.rightLegBack.addBox(0.0f, 0.0f, -1.0f, 1, 1, 1);
        this.rightLegBack.setRotationPoint(0.5f, 23.0f, 0.0f);
        this.rightLegBack.mirror = true;
        this.earRight = new ModelRenderer(this, 8, 0);
        this.earRight.addBox(0.0f, -3.1f, -1.0f, 1, 3, 1);
        this.earRight.setRotationPoint(0.0f, 22.0f, -1.0f);
        ModelBunnyMount.setRotation(this.earRight, 0.0f, 0.0f, -0.6981317f);
        this.earLeft = new ModelRenderer(this, 8, 0);
        this.earLeft.addBox(-1.0f, -3.1f, -1.0f, 1, 3, 1);
        this.earLeft.setRotationPoint(0.0f, 22.0f, -1.0f);
        this.earLeft.mirror = true;
        ModelBunnyMount.setRotation(this.earLeft, 0.0f, 0.0f, 0.6981317f);
        this.tail = new ModelRenderer(this, 4, 9);
        this.tail.addBox(-0.5f, 0.0f, -0.5f, 1, 1, 1);
        this.tail.setRotationPoint(0.0f, 21.5f, 2.0f);
        ModelBunnyMount.setRotation(this.tail, 1.747395f, 0.0f, 0.0f);
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float rotFloat, float rotYaw, float rotPitch, float partTicks) {
        this.setRotationAngles(limbSwing, limbSwingAmount, rotFloat, rotYaw, rotPitch, partTicks, entity);
        this.head.render(partTicks);
        this.body.render(partTicks);
        this.rightLegFront.render(partTicks);
        this.leftLegFront.render(partTicks);
        this.rightLegBack.render(partTicks);
        this.leftLegBack.render(partTicks);
        this.earLeft.render(partTicks);
        this.earRight.render(partTicks);
        this.tail.render(partTicks);
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float rotFloat, float rotYaw, float rotPitch, float partTicks, Entity entity) {
        this.rightLegFront.rotateAngleX = this.leftLegFront.rotateAngleX = MathHelper.cos(limbSwing * 0.6662f) * 0.5f * limbSwingAmount;
        this.rightLegBack.rotateAngleX = this.leftLegBack.rotateAngleX = MathHelper.cos(limbSwing * 0.6662f + 3.141593f) * 0.5f * limbSwingAmount;
        this.leftLegFront.rotateAngleY = 0.0f;
        this.rightLegFront.rotateAngleY = 0.0f;
        this.leftLegBack.rotateAngleY = 0.0f;
        this.rightLegBack.rotateAngleY = 0.0f;
    }

    private static void setRotation(ModelRenderer model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }
}

