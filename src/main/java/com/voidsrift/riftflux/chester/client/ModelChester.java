package com.voidsrift.riftflux.chester.client;

import com.voidsrift.riftflux.chester.EntityChester;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;

public class ModelChester extends ModelBase {
    private final ModelRenderer body;
    private final ModelRenderer topLid;
    private final ModelRenderer tongue;
    private final ModelRenderer leftFrontLeg;
    private final ModelRenderer leftBackLeg;
    private final ModelRenderer rightFrontLeg;
    private final ModelRenderer rightBackLeg;

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

        rightFrontLeg = root();
        child(rightFrontLeg, part(41, 29, -2, 0, -2, 4, 4, 4, -4, 18, -3.5F, -0.3316126F, 0.2792527F, 0));
        child(rightFrontLeg, part(17, 51, -2, 3, -3, 4, 3, 4, -4, 18, -3.5F, 0, 0.2792527F, 0));
        child(body, rightFrontLeg);

        leftFrontLeg = root();
        child(leftFrontLeg, part(0, 32, -2, 0, -2, 4, 4, 4, 4, 18, -3.5F, -0.3316126F, -0.2792527F, 0));
        child(leftFrontLeg, part(41, 21, -2, 3, -3, 4, 3, 4, 4, 18, -3.5F, 0, -0.2792527F, 0));
        child(body, leftFrontLeg);

        leftBackLeg = root();
        child(leftBackLeg, part(0, 50, -2, 0, -2, 4, 4, 4, 4, 18, 3, 0.3316126F, 0.2792527F, 0));
        child(leftBackLeg, part(74, 1, -2, 3, -1, 4, 3, 4, 4, 18, 3, 0, 0.2792527F, 0));
        child(body, leftBackLeg);

        rightBackLeg = root();
        child(rightBackLeg, part(0, 41, -2, 0, -2, 4, 4, 4, -4, 18, 3, 0.3316126F, -0.2792527F, 0));
        child(rightBackLeg, part(64, 14, -2, 3, -1, 4, 3, 4, -4, 18, 3, 0, -0.2792527F, 0));
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
        ModelRenderer renderer = new ModelRenderer(this);
        renderer.setRotationPoint(0.0F, 0.0F, 0.0F);
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
        EntityChester chester = (EntityChester) entity;
        topLid.setRotationPoint(0.0F, 0.0F, chester.isOpen() ? 10.0F : 0.0F);
        topLid.rotateAngleX = chester.isOpen() ? -0.72F : 0.0F;
        topLid.render(scale);
        tongue.render(scale);
        body.render(scale);
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
        leftFrontLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 0.07F * limbSwingAmount;
        rightFrontLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI)
                * 0.07F * limbSwingAmount;
        rightBackLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 0.07F * limbSwingAmount;
        leftBackLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI)
                * 0.07F * limbSwingAmount;
    }
}
