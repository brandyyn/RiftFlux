package com.voidsrift.riftflux.axolotl;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;

public class ModelAxolotl extends ModelBase {
    private final ModelRenderer body;
    private final ModelRenderer head;
    private final ModelRenderer tail;
    private final ModelRenderer topGills;
    private final ModelRenderer leftGills;
    private final ModelRenderer rightGills;
    private final ModelRenderer rightHindLeg;
    private final ModelRenderer leftHindLeg;
    private final ModelRenderer rightFrontLeg;
    private final ModelRenderer leftFrontLeg;

    public ModelAxolotl() {
        this.textureWidth = 64;
        this.textureHeight = 64;

        this.body = new ModelRenderer(this, 0, 11);
        this.body.addBox(-4.0F, -2.0F, -9.0F, 8, 4, 10);
        this.body.addBox(0.0F, -3.0F, -8.0F, 0, 5, 9);
        this.body.setRotationPoint(0.0F, 20.0F, 5.0F);

        this.head = new ModelRenderer(this, 0, 1);
        this.head.addBox(-4.0F, -3.0F, -5.0F, 8, 5, 5, 0.001F);
        this.head.setRotationPoint(0.0F, 0.0F, -9.0F);
        this.body.addChild(this.head);

        this.topGills = new ModelRenderer(this, 3, 37);
        this.topGills.addBox(-4.0F, -3.0F, 0.0F, 8, 3, 0, 0.001F);
        this.topGills.setRotationPoint(0.0F, -3.0F, -1.0F);
        this.head.addChild(this.topGills);

        this.leftGills = new ModelRenderer(this, 0, 40);
        this.leftGills.addBox(-3.0F, -5.0F, 0.0F, 3, 7, 0, 0.001F);
        this.leftGills.setRotationPoint(-4.0F, 0.0F, -1.0F);
        this.head.addChild(this.leftGills);

        this.rightGills = new ModelRenderer(this, 11, 40);
        this.rightGills.addBox(0.0F, -5.0F, 0.0F, 3, 7, 0, 0.001F);
        this.rightGills.setRotationPoint(4.0F, 0.0F, -1.0F);
        this.head.addChild(this.rightGills);

        this.rightHindLeg = new ModelRenderer(this, 2, 13);
        this.rightHindLeg.addBox(-2.0F, 0.0F, 0.0F, 3, 5, 0, 0.001F);
        this.rightHindLeg.setRotationPoint(-3.5F, 1.0F, -1.0F);
        this.body.addChild(this.rightHindLeg);

        this.leftHindLeg = new ModelRenderer(this, 2, 13);
        this.leftHindLeg.addBox(-1.0F, 0.0F, 0.0F, 3, 5, 0, 0.001F);
        this.leftHindLeg.setRotationPoint(3.5F, 1.0F, -1.0F);
        this.body.addChild(this.leftHindLeg);

        this.rightFrontLeg = new ModelRenderer(this, 2, 13);
        this.rightFrontLeg.addBox(-2.0F, 0.0F, 0.0F, 3, 5, 0, 0.001F);
        this.rightFrontLeg.setRotationPoint(-3.5F, 1.0F, -8.0F);
        this.body.addChild(this.rightFrontLeg);

        this.leftFrontLeg = new ModelRenderer(this, 2, 13);
        this.leftFrontLeg.addBox(-1.0F, 0.0F, 0.0F, 3, 5, 0, 0.001F);
        this.leftFrontLeg.setRotationPoint(3.5F, 1.0F, -8.0F);
        this.body.addChild(this.leftFrontLeg);

        this.tail = new ModelRenderer(this, 2, 19);
        this.tail.addBox(0.0F, -3.0F, 0.0F, 0, 5, 12);
        this.tail.setRotationPoint(0.0F, 0.0F, 1.0F);
        this.body.addChild(this.tail);
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks,
                       float netHeadYaw, float headPitch, float scale) {
        this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entity);
        this.body.render(scale);
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks,
                                  float netHeadYaw, float headPitch, float scaleFactor, Entity entity) {
        super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entity);
        this.resetPose();

        EntityAxolotl axolotl = entity instanceof EntityAxolotl ? (EntityAxolotl) entity : null;
        float yaw = netHeadYaw * 0.017453292F;
        float pitch = headPitch * 0.017453292F;
        float swimSpeed = 0.0F;
        if (axolotl != null) {
            double horizontalSpeed = Math.sqrt(axolotl.motionX * axolotl.motionX + axolotl.motionZ * axolotl.motionZ);
            swimSpeed = Math.min(1.0F, (float)(horizontalSpeed * 10.0D + Math.abs(axolotl.motionY) * 4.0D));
        }
        float swimWave = ageInTicks * 0.34F;
        float tailWave = MathHelper.sin(swimWave);
        float bodyWave = MathHelper.cos(swimWave * 0.8F);
        float walkAmount = Math.min(1.0F, limbSwingAmount);
        float walkWave = limbSwing * 0.95F;
        boolean inWater = axolotl != null && axolotl.isInWater();
        boolean onGround = axolotl != null && axolotl.onGround && !inWater;
        boolean playingDead = axolotl != null && axolotl.isPlayingDead();
        boolean sitting = axolotl != null && axolotl.isSitting() && !inWater;

        this.head.rotateAngleY = yaw * 0.5F;
        this.head.rotateAngleX = pitch * 0.35F;

        if (playingDead) {
            this.body.rotateAngleZ = 1.5707964F;
            this.body.rotateAngleX = 0.2F;
            this.tail.rotateAngleY = 0.15F;
            this.leftFrontLeg.rotateAngleX = 1.0F;
            this.rightFrontLeg.rotateAngleX = 1.0F;
            this.leftHindLeg.rotateAngleX = 1.3F;
            this.rightHindLeg.rotateAngleX = 1.3F;
            return;
        }

        if (inWater) {
            float swimAmount = 0.4F + swimSpeed * 0.6F;
            float finWave = MathHelper.sin(swimWave * 1.35F);
            float legWave = MathHelper.sin(swimWave * 1.1F + 0.7F) * 0.18F * swimAmount;
            this.body.rotationPointY = 20.35F;
            this.body.rotateAngleX = 0.05F + pitch * 0.16F + bodyWave * 0.05F * swimAmount;
            this.body.rotateAngleY = tailWave * 0.11F * swimAmount;
            this.head.rotateAngleY = yaw * 0.3F - tailWave * 0.04F * swimAmount;
            this.head.rotateAngleX += -0.16F - bodyWave * 0.03F * swimAmount;
            this.tail.rotateAngleX = -0.03F + bodyWave * 0.04F * swimAmount;
            this.tail.rotateAngleY = tailWave * (0.42F + swimAmount * 0.24F);
            this.topGills.rotateAngleX = -0.72F - finWave * 0.14F;
            this.leftGills.rotateAngleY = 0.88F + finWave * 0.24F;
            this.rightGills.rotateAngleY = -0.88F - finWave * 0.24F;

            this.leftFrontLeg.rotateAngleX = 1.0F + legWave;
            this.leftFrontLeg.rotateAngleY = 1.0F + tailWave * 0.14F;
            this.leftFrontLeg.rotateAngleZ = 0.12F * swimAmount;
            this.rightFrontLeg.rotateAngleX = 1.0F - legWave;
            this.rightFrontLeg.rotateAngleY = -1.0F - tailWave * 0.14F;
            this.rightFrontLeg.rotateAngleZ = -0.12F * swimAmount;

            this.leftHindLeg.rotateAngleX = 1.16F - legWave * 0.85F;
            this.leftHindLeg.rotateAngleY = 0.42F + tailWave * 0.12F;
            this.leftHindLeg.rotateAngleZ = 0.28F + finWave * 0.05F;
            this.rightHindLeg.rotateAngleX = 1.16F + legWave * 0.85F;
            this.rightHindLeg.rotateAngleY = -0.42F - tailWave * 0.12F;
            this.rightHindLeg.rotateAngleZ = -0.28F - finWave * 0.05F;
            return;
        }

        if (sitting) {
            this.body.rotationPointY = 21.8F;
            this.body.rotateAngleX = -0.02F;
            this.body.rotateAngleY = 0.0F;
            this.body.rotateAngleZ = 0.0F;
            this.head.rotateAngleX += 0.02F;
            this.tail.rotateAngleX = 0.04F;
            this.tail.rotateAngleY = MathHelper.sin(ageInTicks * 0.05F) * 0.02F;
            this.topGills.rotateAngleX = -0.08F;
            this.leftGills.rotateAngleY = 0.42F;
            this.rightGills.rotateAngleY = -0.42F;

            this.leftFrontLeg.rotateAngleX = 1.5707964F;
            this.leftFrontLeg.rotateAngleY = 1.5707964F;
            this.leftFrontLeg.rotateAngleZ = 0.0F;
            this.rightFrontLeg.rotateAngleX = 1.5707964F;
            this.rightFrontLeg.rotateAngleY = -1.5707964F;
            this.rightFrontLeg.rotateAngleZ = 0.0F;

            this.leftHindLeg.rotateAngleX = 1.5707964F;
            this.leftHindLeg.rotateAngleY = 1.5707964F;
            this.leftHindLeg.rotateAngleZ = 0.0F;
            this.rightHindLeg.rotateAngleX = 1.5707964F;
            this.rightHindLeg.rotateAngleY = -1.5707964F;
            this.rightHindLeg.rotateAngleZ = 0.0F;
            return;
        }

        if (onGround) {
            float idle = MathHelper.sin(ageInTicks * 0.09F) * (1.0F - walkAmount) * 0.05F;
            float frontSwing = MathHelper.sin(walkWave) * 0.7F * walkAmount;
            float backSwing = MathHelper.cos(walkWave) * 0.6F * walkAmount;
            this.body.rotateAngleX = 0.0F;
            this.body.rotateAngleY = idle + MathHelper.sin(walkWave * 0.5F) * 0.08F * walkAmount;
            this.body.rotateAngleZ = 0.0F;
            this.head.rotateAngleX += 0.03F;
            this.tail.rotateAngleY = MathHelper.sin(walkWave * 0.5F) * 0.2F * (0.3F + walkAmount);
            this.leftFrontLeg.rotateAngleX = 0.8F + frontSwing;
            this.leftFrontLeg.rotateAngleY = 1.2F;
            this.rightFrontLeg.rotateAngleX = 0.8F - frontSwing;
            this.rightFrontLeg.rotateAngleY = -1.2F;
            this.leftHindLeg.rotateAngleX = 1.05F - backSwing;
            this.leftHindLeg.rotateAngleY = 0.55F;
            this.leftHindLeg.rotateAngleZ = 0.8F;
            this.rightHindLeg.rotateAngleX = 1.05F + backSwing;
            this.rightHindLeg.rotateAngleY = -0.55F;
            this.rightHindLeg.rotateAngleZ = -0.8F;
            return;
        }

        float air = MathHelper.sin(ageInTicks * 0.25F);
        float riseAmount = axolotl == null ? 0.0F : Math.min(1.0F, (float)Math.max(0.0D, axolotl.motionY) * 4.5F + 0.1F);
        float fallAmount = axolotl == null ? 0.0F : Math.min(1.0F, (float)Math.max(0.0D, -axolotl.motionY) * 5.0F);
        this.body.rotateAngleX = -0.02F - riseAmount * 0.08F + fallAmount * 0.07F;
        this.tail.rotateAngleY = air * 0.14F;
        this.leftFrontLeg.rotateAngleX = 0.95F + riseAmount * 0.22F - fallAmount * 0.28F;
        this.leftFrontLeg.rotateAngleY = 1.2F;
        this.rightFrontLeg.rotateAngleX = 0.95F + riseAmount * 0.22F - fallAmount * 0.28F;
        this.rightFrontLeg.rotateAngleY = -1.2F;
        this.leftHindLeg.rotateAngleX = 1.05F + riseAmount * 0.18F - fallAmount * 0.24F;
        this.leftHindLeg.rotateAngleY = 0.55F;
        this.leftHindLeg.rotateAngleZ = 0.8F;
        this.rightHindLeg.rotateAngleX = 1.05F + riseAmount * 0.18F - fallAmount * 0.24F;
        this.rightHindLeg.rotateAngleY = -0.55F;
        this.rightHindLeg.rotateAngleZ = -0.8F;
    }

    private void resetPose() {
        this.body.rotationPointX = 0.0F;
        this.body.rotationPointY = 20.0F;
        this.body.rotationPointZ = 5.0F;
        this.body.rotateAngleX = 0.0F;
        this.body.rotateAngleY = 0.0F;
        this.body.rotateAngleZ = 0.0F;
        this.head.rotateAngleX = 0.0F;
        this.head.rotateAngleY = 0.0F;
        this.head.rotateAngleZ = 0.0F;
        this.tail.rotateAngleX = 0.0F;
        this.tail.rotateAngleY = 0.0F;
        this.tail.rotateAngleZ = 0.0F;
        this.topGills.rotateAngleX = 0.0F;
        this.topGills.rotateAngleY = 0.0F;
        this.topGills.rotateAngleZ = 0.0F;
        this.leftGills.rotateAngleX = 0.0F;
        this.leftGills.rotateAngleY = 0.0F;
        this.leftGills.rotateAngleZ = 0.0F;
        this.rightGills.rotateAngleX = 0.0F;
        this.rightGills.rotateAngleY = 0.0F;
        this.rightGills.rotateAngleZ = 0.0F;
        this.rightHindLeg.rotateAngleX = 0.0F;
        this.rightHindLeg.rotateAngleY = 0.0F;
        this.rightHindLeg.rotateAngleZ = 0.0F;
        this.leftHindLeg.rotateAngleX = 0.0F;
        this.leftHindLeg.rotateAngleY = 0.0F;
        this.leftHindLeg.rotateAngleZ = 0.0F;
        this.rightFrontLeg.rotateAngleX = 0.0F;
        this.rightFrontLeg.rotateAngleY = 0.0F;
        this.rightFrontLeg.rotateAngleZ = 0.0F;
        this.leftFrontLeg.rotateAngleX = 0.0F;
        this.leftFrontLeg.rotateAngleY = 0.0F;
        this.leftFrontLeg.rotateAngleZ = 0.0F;
    }
}
