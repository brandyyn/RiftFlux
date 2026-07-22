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
public class ModelTurtleMount
extends ModelBase {
    public ModelRenderer head = new ModelRenderer(this, 0, 0);
    public ModelRenderer shellMain;
    public ModelRenderer shellTop;
    public ModelRenderer rightLegFront;
    public ModelRenderer leftLegFront;
    public ModelRenderer rightLegBack;
    public ModelRenderer leftLegBack;

    public ModelTurtleMount() {
        this.head.addBox(-1.0f, -1.0f, -2.0f, 2, 2, 2);
        this.head.setRotationPoint(0.0f, 21.0f, -5.0f);
        this.shellMain = new ModelRenderer(this, 8, 0);
        this.shellMain.addBox(-3.0f, 0.0f, -2.0f, 6, 2, 7);
        this.shellMain.setRotationPoint(0.0f, 20.0f, -3.0f);
        this.shellTop = new ModelRenderer(this, 8, 9);
        this.shellTop.addBox(-2.0f, -1.0f, -1.0f, 4, 1, 5);
        this.shellTop.setRotationPoint(0.0f, 20.0f, -3.0f);
        this.rightLegFront = new ModelRenderer(this, 0, 4);
        this.rightLegFront.addBox(-1.0f, 0.0f, -1.0f, 1, 3, 1);
        this.rightLegFront.setRotationPoint(-2.0f, 22.0f, -3.0f);
        ModelTurtleMount.setRotation(this.rightLegFront, 0.0f, 0.0f, 1.003822f);
        this.leftLegFront = new ModelRenderer(this, 0, 4);
        this.leftLegFront.addBox(0.0f, 0.0f, -1.0f, 1, 3, 1);
        this.leftLegFront.setRotationPoint(2.0f, 22.0f, -3.0f);
        this.leftLegFront.mirror = true;
        ModelTurtleMount.setRotation(this.leftLegFront, 0.0f, 0.0f, -1.003822f);
        this.rightLegBack = new ModelRenderer(this, 4, 4);
        this.rightLegBack.addBox(-1.0f, 0.0f, 0.0f, 1, 3, 1);
        this.rightLegBack.setRotationPoint(-2.0f, 22.0f, 0.0f);
        ModelTurtleMount.setRotation(this.rightLegBack, 0.0f, 0.0f, 1.003822f);
        this.leftLegBack = new ModelRenderer(this, 4, 4);
        this.leftLegBack.addBox(0.0f, 0.0f, 0.0f, 1, 3, 1);
        this.leftLegBack.setRotationPoint(2.0f, 22.0f, 0.0f);
        this.leftLegBack.mirror = true;
        ModelTurtleMount.setRotation(this.leftLegBack, 0.0f, 0.0f, -1.003822f);
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float rotFloat, float rotYaw, float rotPitch, float partTicks) {
        this.setRotationAngles(limbSwing, limbSwingAmount, rotFloat, rotYaw, rotPitch, partTicks, entity);
        this.head.render(partTicks);
        this.shellMain.render(partTicks);
        this.rightLegFront.render(partTicks);
        this.leftLegFront.render(partTicks);
        this.rightLegBack.render(partTicks);
        this.leftLegBack.render(partTicks);
        this.shellTop.render(partTicks);
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float rotFloat, float rotYaw, float rotPitch, float partTicks, Entity entity) {
        this.rightLegFront.rotateAngleX = MathHelper.cos(limbSwing * 0.6662f + 3.141593f) * 0.5f * limbSwingAmount;
        this.leftLegFront.rotateAngleX = MathHelper.cos(limbSwing * 0.6662f) * 0.5f * limbSwingAmount;
        this.rightLegFront.rotateAngleY = 0.0f;
        this.leftLegFront.rotateAngleY = 0.0f;
        this.rightLegBack.rotateAngleX = MathHelper.cos(limbSwing * 0.6662f) * 0.5f * limbSwingAmount;
        this.leftLegBack.rotateAngleX = MathHelper.cos(limbSwing * 0.6662f + 3.141593f) * 0.5f * limbSwingAmount;
        this.rightLegBack.rotateAngleY = 0.0f;
        this.leftLegBack.rotateAngleY = 0.0f;
    }

    private static void setRotation(ModelRenderer model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }
}

