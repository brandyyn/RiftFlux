package com.voidsrift.riftflux.chester.client;

import com.voidsrift.riftflux.chester.EntityChester;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;

public class ModelChester extends ModelBase {
    private final ModelRenderer body;
    private final ModelRenderer topLid;
    private final ModelRenderer tongue;
    private final ModelRenderer leftFrontLeg;
    private final ModelRenderer leftBackLeg;
    private final ModelRenderer rightFrontLeg;
    private final ModelRenderer rightBackLeg;
    private float sitProgress;
    private float mouthOpenProgress;

    public ModelChester() {
        textureWidth = 128;
        textureHeight = 64;

        body = root();
        topLid = root();
        tongue = root();

        child(body, part(0, 18, -5, -1, 0, 10, 3, 10, 0, 19, -5, 0, 0, 0));
        child(body, part(41, 13, -4, -7, -1, 8, 5, 1, 0, 20, -4, 0, 0, 0));
        child(body, part(17, 33, -1, -7, -5, 1, 5, 10, -4, 20, 0, 0, 0, 0));
        child(body, part(40, 47, 0, -7, -5, 1, 5, 10, 4, 20, 0, 0, 0, 0));
        child(body, part(40, 38, -4, -7, 0, 8, 5, 1, 0, 20, 4, 0, 0, 0));

        rightFrontLeg = rootAt(-4.0F, 18.0F, -3.5F);
        child(rightFrontLeg, part(41, 29, -2, 0, -2, 4, 4, 4, 0, 0, 0, -0.3316126F, 0.2792527F, 0));
        child(rightFrontLeg, part(17, 51, -2, 3, -3, 4, 3, 4, 0, 0, 0, 0, 0.2792527F, 0));
        child(body, rightFrontLeg);

        leftFrontLeg = rootAt(4.0F, 18.0F, -3.5F);
        child(leftFrontLeg, part(0, 32, -2, 0, -2, 4, 4, 4, 0, 0, 0, -0.3316126F, -0.2792527F, 0));
        child(leftFrontLeg, part(41, 21, -2, 3, -3, 4, 3, 4, 0, 0, 0, 0, -0.2792527F, 0));
        child(body, leftFrontLeg);

        leftBackLeg = rootAt(4.0F, 18.0F, 3.0F);
        child(leftBackLeg, part(0, 50, -2, 0, -2, 4, 4, 4, 0, 0, 0, 0.3316126F, 0.2792527F, 0));
        child(leftBackLeg, part(74, 1, -2, 3, -1, 4, 3, 4, 0, 0, 0, 0, 0.2792527F, 0));
        child(body, leftBackLeg);

        rightBackLeg = rootAt(-4.0F, 18.0F, 3.0F);
        child(rightBackLeg, part(0, 41, -2, 0, -2, 4, 4, 4, 0, 0, 0, 0.3316126F, -0.2792527F, 0));
        child(rightBackLeg, part(64, 14, -2, 3, -1, 4, 3, 4, 0, 0, 0, 0, -0.2792527F, 0));
        child(body, rightBackLeg);

        child(topLid, part(58, 22, -4, -2.5F, -9.5F, 8, 2, 9, 0, 13, 5, -0.2094395F, 0, 0));
        child(topLid, part(35, 0, -4.5F, -2, -10, 9, 2, 10, 0, 13, 5, -0.2094395F, 0, 0));
        child(topLid, part(0, 8, 1, -4, -5, 2, 2, 2, 0, 13, 5, -0.2094395F, 0, 0));
        child(topLid, part(9, 14, 2, -5, -4.5F, 1, 2, 1, 0, 13, 5, -0.2094395F, 0, -0.1919862F));
        child(topLid, part(0, 13, -3, -4, -5, 2, 2, 2, 0, 13, 5, -0.2094395F, 0, 0));
        child(topLid, part(9, 10, -3, -5, -4.5F, 1, 2, 1, 0, 13, 5, -0.2094395F, 0, 0.1919862F));

        child(tongue, part(64, 57, -2, -2.5F, -6, 4, 2, 5, 0, 14, -2, 0.3665191F, 0, 0));

        child(body, part(21, 0, -1, -1.3F, -0.7F, 2, 2, 1, 2.8F, 13, -4, 0, 0, 0));
        child(body, part(28, 0, -1, -1.3F, -0.7F, 2, 2, 1, -3, 13, -4, 0, 0, 0));
        child(topLid, part(21, 5, 0.3F, -1.2F, -9.7F, 2, 2, 1, 0, 13, 5, -0.2094395F, 0, 0));
        child(topLid, part(28, 5, -2.2F, -1.2F, -9.7F, 2, 2, 1, 0, 13, 5, -0.2094395F, 0, 0));
        child(body, part(14, 9, 0, -2, -0.4F, 2, 2, 1, 4, 14, -3, 0, -1.343904F, 0));
        child(body, part(21, 10, -2, -2, -0.4F, 2, 2, 1, -4, 14, -3, 0, 1.343904F, 0));
        child(topLid, part(14, 14, -0.9F, -1.3F, -9, 2, 2, 1, 0, 13, 5, -0.2094395F, 0.418879F, 0));
        child(topLid, part(21, 14, -1, -1.3F, -9, 2, 2, 1, 0, 13, 5, -0.2094395F, -0.418879F, 0));
        child(body, part(28, 9, -0.2F, -1.7F, -1, 1, 2, 2, 4, 14, 0.3F, 0, 0, 0));
        child(body, part(0, 0, -1.8F, -1.7F, -1, 1, 2, 2, -3, 14, 0, 0, 0, 0));
    }

    private ModelRenderer root() {
        return rootAt(0.0F, 0.0F, 0.0F);
    }

    private ModelRenderer rootAt(float x, float y, float z) {
        ModelRenderer renderer = new ModelRenderer(this);
        renderer.setRotationPoint(x, y, z);
        return renderer;
    }

    private ModelRenderer part(
            int textureX,
            int textureY,
            float boxX,
            float boxY,
            float boxZ,
            int width,
            int height,
            int depth,
            float pivotX,
            float pivotY,
            float pivotZ,
            float rotateX,
            float rotateY,
            float rotateZ
    ) {
        ModelRenderer renderer = new ModelRenderer(this, textureX, textureY);
        renderer.addBox(boxX, boxY, boxZ, width, height, depth);
        renderer.setRotationPoint(pivotX, pivotY, pivotZ);
        renderer.setTextureSize(textureWidth, textureHeight);
        renderer.mirror = true;
        renderer.rotateAngleX = rotateX;
        renderer.rotateAngleY = rotateY;
        renderer.rotateAngleZ = rotateZ;
        return renderer;
    }

    private static void child(ModelRenderer parent, ModelRenderer child) {
        parent.addChild(child);
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float age, float yaw, float pitch, float scale) {
        setRotationAngles(limbSwing, limbSwingAmount, age, yaw, pitch, scale, entity);
        float sitY = 3.0F * sitProgress;
        float sitZ = 1.5F * sitProgress;
        float bodyTilt = -0.12F * sitProgress;
        body.setRotationPoint(0.0F, sitY, sitZ);
        body.rotateAngleX = bodyTilt;
        float lidOpenOffset = 10.0F * mouthOpenProgress;
        float lidOffsetY = -MathHelper.sin(bodyTilt) * lidOpenOffset;
        float lidOffsetZ = MathHelper.cos(bodyTilt) * lidOpenOffset;
        topLid.setRotationPoint(0.0F, sitY + lidOffsetY, sitZ + lidOffsetZ);
        topLid.rotateAngleX = bodyTilt - 0.72F * mouthOpenProgress;
        tongue.setRotationPoint(0.0F, sitY, sitZ);
        tongue.rotateAngleX = bodyTilt;
        topLid.render(scale);
        tongue.render(scale);
        body.render(scale);
    }

    @Override
    public void setLivingAnimations(
            EntityLivingBase entity,
            float limbSwing,
            float limbSwingAmount,
            float partialTicks
    ) {
        EntityChester chester = (EntityChester) entity;
        sitProgress = chester.getSitAnimationProgress(partialTicks);
        mouthOpenProgress = chester.getMouthOpenAnimationProgress(partialTicks);
    }

    @Override
    public void setRotationAngles(
            float limbSwing,
            float limbSwingAmount,
            float age,
            float yaw,
            float pitch,
            float scale,
            Entity entity
    ) {
        super.setRotationAngles(limbSwing, limbSwingAmount, age, yaw, pitch, scale, entity);
        float leftFrontWalk = MathHelper.cos(limbSwing * 0.6662F) * 0.65F * limbSwingAmount;
        float rightFrontWalk = MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI)
                * 0.65F * limbSwingAmount;
        float rightBackWalk = MathHelper.cos(limbSwing * 0.6662F) * 0.65F * limbSwingAmount;
        float leftBackWalk = MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI)
                * 0.65F * limbSwingAmount;
        leftFrontLeg.rotateAngleX = blend(leftFrontWalk, -0.9F, sitProgress);
        rightFrontLeg.rotateAngleX = blend(rightFrontWalk, -0.9F, sitProgress);
        rightBackLeg.rotateAngleX = blend(rightBackWalk, 1.25F, sitProgress);
        leftBackLeg.rotateAngleX = blend(leftBackWalk, 1.25F, sitProgress);
    }

    private static float blend(float from, float to, float amount) {
        return from + (to - from) * amount;
    }
}
