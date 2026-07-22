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
public class ModelHorseMount
extends ModelBase {
    public ModelRenderer head = new ModelRenderer(this, 0, 0);
    public ModelRenderer ear1;
    public ModelRenderer ear2;
    public ModelRenderer body;
    public ModelRenderer neck;
    public ModelRenderer mane;
    public ModelRenderer leg1;
    public ModelRenderer leg2;
    public ModelRenderer leg3;
    public ModelRenderer leg4;
    public ModelRenderer tail;

    public ModelHorseMount() {
        this.head.addBox(-1.0f, 0.0f, -4.0f, 2, 2, 4, 0.2f);
        this.head.setRotationPoint(0.0f, -3.75f, -7.75f);
        this.ear1 = new ModelRenderer(this, 0, 0);
        this.ear1.addBox(-1.25f, -0.8f, -1.0f, 1, 1, 1, 0.1f);
        this.ear1.setRotationPoint(0.0f, -3.75f, -7.75f);
        this.ear2 = new ModelRenderer(this, 0, 0);
        this.ear2.addBox(0.25f, -0.8f, -1.0f, 1, 1, 1, 0.1f);
        this.ear2.setRotationPoint(0.0f, -3.75f, -7.75f);
        this.body = new ModelRenderer(this, 0, 8);
        this.body.addBox(-2.0f, 0.0f, -4.0f, 4, 4, 8, 0.0f);
        this.body.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.neck = new ModelRenderer(this, 12, 0);
        this.neck.addBox(-1.0f, 0.0f, -6.0f, 2, 2, 6, 0.4f);
        this.neck.setRotationPoint(0.0f, 0.0f, -2.0f);
        this.mane = new ModelRenderer(this, 28, 0);
        this.mane.addBox(-1.0f, -1.1f, -6.0f, 2, 1, 6, 0.0f);
        this.mane.setRotationPoint(0.0f, 0.0f, -2.0f);
        this.leg1 = new ModelRenderer(this, 24, 10);
        this.leg1.addBox(-1.0f, 0.0f, -1.0f, 2, 8, 2, 0.25f);
        this.leg1.setRotationPoint(-1.0f, 3.75f, -2.75f);
        this.leg2 = new ModelRenderer(this, 24, 10);
        this.leg2.mirror = true;
        this.leg2.addBox(-1.0f, 0.0f, -1.0f, 2, 8, 2, 0.25f);
        this.leg2.setRotationPoint(1.0f, 3.75f, -2.75f);
        this.leg3 = new ModelRenderer(this, 24, 10);
        this.leg3.addBox(-1.0f, 0.0f, -1.0f, 2, 8, 2, 0.25f);
        this.leg3.setRotationPoint(-1.0f, 3.75f, 2.75f);
        this.leg4 = new ModelRenderer(this, 24, 10);
        this.leg4.mirror = true;
        this.leg4.addBox(-1.0f, 0.0f, -1.0f, 2, 8, 2, 0.25f);
        this.leg4.setRotationPoint(1.0f, 3.75f, 2.75f);
        this.tail = new ModelRenderer(this, 36, 11);
        this.tail.addBox(-0.5f, 0.0f, -0.5f, 1, 5, 1, 0.15f);
        this.tail.setRotationPoint(0.0f, 0.0f, 3.75f);
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float rotFloat, float rotYaw, float rotPitch, float partTicks) {
        this.setRotationAngles(limbSwing, limbSwingAmount, rotFloat, rotYaw, rotPitch, partTicks, entity);
        this.head.render(partTicks);
        this.body.render(partTicks);
        this.leg1.render(partTicks);
        this.leg2.render(partTicks);
        this.leg3.render(partTicks);
        this.leg4.render(partTicks);
        this.neck.render(partTicks);
        this.mane.render(partTicks);
        this.tail.render(partTicks);
        this.ear1.render(partTicks);
        this.ear2.render(partTicks);
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float rotFloat, float rotYaw, float rotPitch, float partTicks, Entity entity) {
        this.head.rotateAngleY = rotYaw / 57.29578f;
        this.head.rotateAngleX = rotPitch / 57.29578f + 0.79f;
        this.leg1.rotateAngleX = MathHelper.cos(limbSwing * 0.6662f + (float)Math.PI) * 2.0f * limbSwingAmount * 0.25f;
        this.leg2.rotateAngleX = MathHelper.cos(limbSwing * 0.6662f) * 2.0f * limbSwingAmount * 0.25f;
        this.leg3.rotateAngleX = this.leg1.rotateAngleX;
        this.leg4.rotateAngleX = this.leg2.rotateAngleX;
        this.leg1.rotateAngleZ = 0.0f;
        this.leg2.rotateAngleZ = 0.0f;
        this.leg1.rotateAngleY = 0.0f;
        this.leg2.rotateAngleY = 0.0f;
        this.leg3.rotateAngleY = 0.0f;
        this.leg4.rotateAngleY = 0.0f;
        this.tail.rotateAngleX = 0.3f + this.leg1.rotateAngleX * this.leg1.rotateAngleX;
        this.neck.rotateAngleX = -0.6f;
        this.mane.rotateAngleX = -0.6f;
        this.ear1.rotateAngleX = this.ear2.rotateAngleX = this.head.rotateAngleX;
        this.ear1.rotateAngleY = this.ear2.rotateAngleY = this.head.rotateAngleY;
    }
}

