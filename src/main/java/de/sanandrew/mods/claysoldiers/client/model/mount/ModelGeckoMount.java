/*
 * Decompiled with CFR 0.152.
 */
package de.sanandrew.mods.claysoldiers.client.model.mount;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;

public class ModelGeckoMount
extends ModelBase {
    public ModelRenderer head = new ModelRenderer(this, 0, 0);
    public ModelRenderer body;
    public ModelRenderer rightarm;
    public ModelRenderer leftarm;
    public ModelRenderer rightleg;
    public ModelRenderer leftleg;
    public ModelRenderer nose;
    public ModelRenderer tail;

    public ModelGeckoMount() {
        this.head.addBox(-1.0f, 0.0f, -3.0f, 2, 1, 2);
        this.head.setRotationPoint(0.0f, 21.0f, -4.0f);
        ModelGeckoMount.setRotation(this.head, 0.0f, 0.0f, 0.0f);
        this.body = new ModelRenderer(this, 8, 0);
        this.body.addBox(-1.5f, -1.0f, -1.5f, 3, 1, 7);
        this.body.setRotationPoint(0.0f, 22.0f, -2.0f);
        ModelGeckoMount.setRotation(this.body, 0.0f, 0.0f, 0.0f);
        this.rightarm = new ModelRenderer(this, 0, 3);
        this.rightarm.addBox(-1.0f, 0.0f, -1.0f, 1, 4, 1);
        this.rightarm.setRotationPoint(-1.0f, 22.0f, -2.0f);
        this.rightarm.mirror = true;
        ModelGeckoMount.setRotation(this.rightarm, 0.0f, -0.2602503f, 1.226894f);
        this.leftarm = new ModelRenderer(this, 0, 3);
        this.leftarm.addBox(0.0f, 0.0f, -1.0f, 1, 4, 1);
        this.leftarm.setRotationPoint(1.0f, 22.0f, -2.0f);
        this.leftarm.mirror = true;
        ModelGeckoMount.setRotation(this.leftarm, 0.0f, 0.2602503f, -1.226894f);
        this.rightleg = new ModelRenderer(this, 4, 3);
        this.rightleg.addBox(-1.0f, 0.0f, 0.0f, 1, 4, 1);
        this.rightleg.setRotationPoint(-1.0f, 22.0f, 2.0f);
        this.rightleg.mirror = true;
        ModelGeckoMount.setRotation(this.rightleg, 0.0f, 0.2602503f, 1.226894f);
        this.leftleg = new ModelRenderer(this, 4, 3);
        this.leftleg.addBox(0.0f, 0.0f, 0.0f, 1, 4, 1);
        this.leftleg.setRotationPoint(1.0f, 22.0f, 2.0f);
        ModelGeckoMount.setRotation(this.leftleg, 0.0f, -0.2602503f, -1.226894f);
        this.nose = new ModelRenderer(this, 0, 8);
        this.nose.addBox(-1.5f, -1.0f, -1.5f, 3, 2, 2);
        this.nose.setRotationPoint(0.0f, 21.0f, -4.0f);
        ModelGeckoMount.setRotation(this.nose, 0.0f, 0.0f, 0.0f);
        this.tail = new ModelRenderer(this, 0, 12);
        this.tail.addBox(-0.5f, 0.0f, -0.5f, 1, 8, 1);
        this.tail.setRotationPoint(0.0f, 21.5f, 3.0f);
        ModelGeckoMount.setRotation(this.tail, 1.487144f, 0.0f, 0.0f);
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float rotFloat, float rotYaw, float rotPitch, float partTicks) {
        this.setRotationAngles(limbSwing, limbSwingAmount, rotFloat, rotYaw, rotPitch, partTicks, entity);
        this.head.render(partTicks);
        this.body.render(partTicks);
        this.rightarm.render(partTicks);
        this.leftarm.render(partTicks);
        this.rightleg.render(partTicks);
        this.leftleg.render(partTicks);
        this.nose.render(partTicks);
        this.tail.render(partTicks);
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float rotFloat, float rotYaw, float rotPitch, float partTicks, Entity entity) {
        this.rightarm.rotateAngleX = MathHelper.cos(limbSwing * 0.6662f + 3.141593f) * 0.5f * limbSwingAmount;
        this.leftarm.rotateAngleX = MathHelper.cos(limbSwing * 0.6662f) * 0.5f * limbSwingAmount;
        this.rightleg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662f) * 0.5f * limbSwingAmount;
        this.leftleg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662f + 3.141593f) * 0.5f * limbSwingAmount;
        this.tail.rotateAngleY = MathHelper.cos(limbSwing * 0.6662f + 3.141593f) * 0.5f * limbSwingAmount;
    }

    private static void setRotation(ModelRenderer model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }
}

