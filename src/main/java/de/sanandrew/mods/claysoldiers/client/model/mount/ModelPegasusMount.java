/*
 * Decompiled with CFR 0.152.
 */
package de.sanandrew.mods.claysoldiers.client.model.mount;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.sanandrew.mods.claysoldiers.client.model.mount.ModelHorseMount;
import de.sanandrew.mods.claysoldiers.entity.mount.EntityPegasusMount;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

@SideOnly(value=Side.CLIENT)
public class ModelPegasusMount
extends ModelHorseMount {
    public float wingSwingAmount;
    public boolean isOnGround;
    public ModelRenderer wingLeft = new ModelRenderer(this, 0, 22);
    public ModelRenderer wingRight;

    public ModelPegasusMount() {
        this.wingLeft.addBox(-12.5f, 0.25f, -2.25f, 13, 1, 5, 0.0f);
        this.wingLeft.setRotationPoint(-1.5f, -0.5f, 0.0f);
        this.wingRight = new ModelRenderer(this, 0, 22);
        this.wingRight.mirror = true;
        this.wingRight.addBox(-0.5f, 0.25f, -2.25f, 13, 1, 5, 0.0f);
        this.wingRight.setRotationPoint(1.5f, -0.5f, 0.0f);
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float rotFloat, float rotYaw, float rotPitch, float partTicks) {
        EntityPegasusMount pegasus = (EntityPegasusMount)entity;
        this.isOnGround = pegasus.onGround;
        pegasus.wingSwingStep = this.wingSwingAmount = pegasus.wingSwingStep + (pegasus.wingSwing - pegasus.prevWingSwing) * partTicks;
        super.render(entity, limbSwing, limbSwingAmount, rotFloat, rotYaw, rotPitch, partTicks);
        this.wingLeft.render(partTicks);
        this.wingRight.render(partTicks);
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float rotFloat, float rotYaw, float rotPitch, float partTicks, Entity entity) {
        super.setRotationAngles(limbSwing, limbSwingAmount, rotFloat, rotYaw, rotPitch, partTicks, entity);
        this.wingLeft.rotateAngleY = 0.2f;
        this.wingRight.rotateAngleY = -0.2f;
        this.wingLeft.rotateAngleZ = -0.125f;
        this.wingRight.rotateAngleZ = 0.125f;
        this.wingLeft.rotateAngleY = (float)((double)this.wingLeft.rotateAngleY + Math.sin(this.wingSwingAmount) / 6.0);
        this.wingRight.rotateAngleY = (float)((double)this.wingRight.rotateAngleY - Math.sin(this.wingSwingAmount) / 6.0);
        this.wingLeft.rotateAngleZ = (float)((double)this.wingLeft.rotateAngleZ - Math.cos(this.wingSwingAmount) / (double)(this.isOnGround ? 10.0f : 1.5f));
        this.wingRight.rotateAngleZ = (float)((double)this.wingRight.rotateAngleZ + Math.cos(this.wingSwingAmount) / (double)(this.isOnGround ? 10.0f : 1.5f));
    }
}

