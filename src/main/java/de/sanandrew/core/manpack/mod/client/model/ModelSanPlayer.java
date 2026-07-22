/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.opengl.GL11
 */
package de.sanandrew.core.manpack.mod.client.model;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.sanandrew.core.manpack.util.client.helpers.ModelBoxBuilder;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import org.lwjgl.opengl.GL11;

@SideOnly(value=Side.CLIENT)
public class ModelSanPlayer
extends ModelBiped {
    public ModelRenderer legLeft;
    public ModelRenderer legRight;
    public ModelRenderer armLeft2;
    public ModelRenderer armRight2;
    public ModelRenderer body;
    public ModelRenderer breast;
    public ModelRenderer skirt1;
    public ModelRenderer skirt2;
    public ModelRenderer head;
    public ModelRenderer hair;
    public ModelRenderer quadTail1;
    public ModelRenderer quadTail2;
    public ModelRenderer quadTail3;
    public ModelRenderer quadTail4;
    public ModelRenderer hatBase;
    public boolean hideTails;
    private boolean isArmor;

    public ModelSanPlayer(float scaling, boolean isArmor) {
        super(scaling);
        this.isArmor = isArmor;
        this.textureWidth = 64;
        this.textureHeight = 64;
        this.legLeft = ModelBoxBuilder.newBuilder(this).setTexture(0, 16, true).setLocation(2.5f, 12.0f, 0.0f).setRotation(0.0f, 0.0f, 0.0f).getBox(-1.5f, 0.0f, -1.5f, 3, 12, 3, scaling);
        this.legRight = ModelBoxBuilder.newBuilder(this).setTexture(0, 16, false).setLocation(-2.5f, 12.0f, 0.0f).setRotation(0.0f, 0.0f, 0.0f).getBox(-1.5f, 0.0f, -1.5f, 3, 12, 3, scaling);
        this.bipedLeftArm = ModelBoxBuilder.newBuilder(this).setTexture(40, 16, true).setLocation(5.0f, 2.0f, 0.5f).setRotation(-0.00331613f, 0.0f, -0.09983283f).getBox(-1.0f, -2.0f, -2.0f, 3, 12, 3, scaling);
        this.bipedRightArm = ModelBoxBuilder.newBuilder(this).setTexture(40, 16, false).setLocation(-4.0f, 2.0f, 0.5f).setRotation(0.0f, 0.0f, 0.09983283f).getBox(-3.0f, -2.0f, -2.0f, 3, 12, 3, scaling);
        this.armLeft2 = ModelBoxBuilder.newBuilder(this).setTexture(40, 32, true).setLocation(5.0f, 2.0f, 0.5f).setRotation(-0.00331613f, 0.0f, -0.09983283f).getBox(-1.0f, 5.0f, -2.0f, 3, 5, 3, scaling);
        this.armRight2 = ModelBoxBuilder.newBuilder(this).setTexture(40, 32, false).setLocation(-4.0f, 2.0f, 0.5f).setRotation(0.0f, 0.0f, 0.09983283f).getBox(-3.0f, 5.0f, -2.0f, 3, 5, 3, scaling);
        this.body = ModelBoxBuilder.newBuilder(this).setTexture(16, 16, false).setLocation(0.0f, 0.0f, 0.0f).setRotation(0.0f, 0.0f, 0.0f).getBox(-4.0f, 0.0f, -2.0f, 8, 12, 4, scaling);
        this.breast = ModelBoxBuilder.newBuilder(this).setTexture(0, 54, false).setLocation(0.0f, 3.0f, -3.5f).setRotation(0.87266463f, 0.0f, 0.0f).getBox(-3.5f, 0.0f, 0.0f, 7, 3, 3, scaling);
        this.skirt1 = ModelBoxBuilder.newBuilder(this).setTexture(16, 36, false).setLocation(0.0f, 0.0f, 0.0f).setRotation(0.0f, 0.0f, 0.0f).getBox(-4.5f, 8.0f, -2.5f, 9, 3, 5, 0.0f);
        this.skirt2 = ModelBoxBuilder.newBuilder(this).setTexture(16, 44, false).setLocation(0.0f, 0.0f, 0.0f).setRotation(0.0f, 0.0f, 0.0f).getBox(-4.5f, 11.0f, -3.0f, 9, 4, 6, 0.0f);
        this.head = ModelBoxBuilder.newBuilder(this).setTexture(28, 0, false).setLocation(0.0f, 0.0f, 0.0f).setRotation(0.0f, 0.0f, 0.0f).getBox(-3.5f, -7.0f, -3.5f, 7, 7, 7, scaling);
        this.hair = ModelBoxBuilder.newBuilder(this).setTexture(0, 0, false).setLocation(0.0f, 0.0f, 0.0f).setRotation(0.0f, 0.0f, 0.0f).getBox(-3.5f, -7.4f, -3.5f, 7, 7, 7, scaling + 0.4f);
        this.quadTail1 = ModelBoxBuilder.newBuilder(this).setTexture(0, 40, true).setLocation(0.0f, -3.0f, 0.0f).setRotation(0.5235988f, 0.0f, -2.268928f).getBox(-1.5f, 3.0f, 1.0f, 3, 10, 3, scaling);
        this.quadTail2 = ModelBoxBuilder.newBuilder(this).setTexture(0, 40, false).setLocation(0.0f, -3.0f, 0.0f).setRotation(0.5235988f, 0.0f, 2.268928f).getBox(-1.5f, 3.0f, 1.0f, 3, 10, 3, scaling);
        this.quadTail3 = ModelBoxBuilder.newBuilder(this).setTexture(0, 32, true).setLocation(0.0f, -3.0f, 0.0f).setRotation(0.63739425f, 0.0f, -0.61086524f).getBox(-0.5f, 3.0f, 0.9f, 2, 6, 2, scaling);
        this.quadTail4 = ModelBoxBuilder.newBuilder(this).setTexture(0, 32, false).setLocation(0.0f, -3.0f, 0.0f).setRotation(0.63739425f, 0.0f, 0.61086524f).getBox(-1.5f, 3.0f, 0.9f, 2, 6, 2, scaling);
        this.body.addChild(this.breast);
        this.head.addChild(this.quadTail1);
        this.head.addChild(this.quadTail2);
        this.head.addChild(this.quadTail3);
        this.head.addChild(this.quadTail4);
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float rotFloat, float rotYaw, float rotPitch, float partTicks) {
        this.setRotationAngles(limbSwing, limbSwingAmount, rotFloat, rotYaw, rotPitch, partTicks, entity);
        this.quadTail1.isHidden = this.hideTails;
        this.quadTail2.isHidden = this.hideTails;
        this.quadTail3.isHidden = this.hideTails;
        this.quadTail4.isHidden = this.hideTails;
        if (!this.isArmor) {
            GL11.glPushMatrix();
            GL11.glTranslatef((float)this.head.offsetX, (float)this.head.offsetY, (float)this.head.offsetZ);
            GL11.glTranslatef((float)(this.head.rotationPointX * partTicks), (float)(this.head.rotationPointY * partTicks), (float)(this.head.rotationPointZ * partTicks));
            GL11.glScaled((double)1.1, (double)1.1, (double)1.1);
            GL11.glTranslatef((float)(-this.head.offsetX), (float)(-this.head.offsetY), (float)(-this.head.offsetZ));
            GL11.glTranslatef((float)(-this.head.rotationPointX * partTicks), (float)(-this.head.rotationPointY * partTicks), (float)(-this.head.rotationPointZ * partTicks));
            this.head.render(partTicks);
            GL11.glPopMatrix();
            this.hair.render(partTicks);
        } else if (this.hatBase != null) {
            this.hatBase.render(partTicks);
        }
        this.bipedLeftArm.render(partTicks);
        this.bipedRightArm.render(partTicks);
        this.legLeft.render(partTicks);
        this.legRight.render(partTicks);
        this.body.render(partTicks);
        this.skirt1.render(partTicks);
        this.skirt2.render(partTicks);
        GL11.glPushMatrix();
        GL11.glTranslatef((float)this.armLeft2.offsetX, (float)this.armLeft2.offsetY, (float)this.armLeft2.offsetZ);
        GL11.glTranslatef((float)(this.armLeft2.rotationPointX * partTicks + 0.025f), (float)(this.armLeft2.rotationPointY * partTicks), (float)(this.armLeft2.rotationPointZ * partTicks));
        GL11.glScaled((double)1.05, (double)1.05, (double)1.05);
        GL11.glTranslatef((float)(-this.armLeft2.offsetX), (float)(-this.armLeft2.offsetY), (float)(-this.armLeft2.offsetZ));
        GL11.glTranslatef((float)(-this.armLeft2.rotationPointX * partTicks - 0.025f), (float)(-this.armLeft2.rotationPointY * partTicks), (float)(-this.armLeft2.rotationPointZ * partTicks));
        this.armLeft2.render(partTicks);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glTranslatef((float)this.armRight2.offsetX, (float)this.armRight2.offsetY, (float)this.armRight2.offsetZ);
        GL11.glTranslatef((float)(this.armRight2.rotationPointX * partTicks - 0.025f), (float)(this.armRight2.rotationPointY * partTicks), (float)(this.armRight2.rotationPointZ * partTicks));
        GL11.glScaled((double)1.05, (double)1.05, (double)1.05);
        GL11.glTranslatef((float)(-this.armRight2.offsetX), (float)(-this.armRight2.offsetY), (float)(-this.armRight2.offsetZ));
        GL11.glTranslatef((float)(-this.armRight2.rotationPointX * partTicks + 0.025f), (float)(-this.armRight2.rotationPointY * partTicks), (float)(-this.armRight2.rotationPointZ * partTicks));
        this.armRight2.render(partTicks);
        GL11.glPopMatrix();
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float rotFloat, float rotYaw, float rotPitch, float partTicks, Entity entity) {
        float prevHeadPosY = this.bipedHead.rotationPointY;
        super.setRotationAngles(limbSwing, limbSwingAmount, rotFloat, rotYaw, rotPitch, partTicks, entity);
        float deltaHeadPosY = this.bipedHead.rotationPointY - prevHeadPosY;
        this.hair.rotationPointY += deltaHeadPosY;
        this.head.rotationPointY += deltaHeadPosY;
        if (this.hatBase != null) {
            this.hatBase.rotationPointY += deltaHeadPosY;
            this.setRotateAngle(this.hatBase, this.bipedHead.rotateAngleX, this.bipedHead.rotateAngleY, this.bipedHead.rotateAngleZ);
        }
        this.setRotateAngle(this.head, this.bipedHead.rotateAngleX, this.bipedHead.rotateAngleY, this.bipedHead.rotateAngleZ);
        this.setRotateAngle(this.hair, this.bipedHead.rotateAngleX, this.bipedHead.rotateAngleY, this.bipedHead.rotateAngleZ);
        this.setRotateAngle(this.body, this.bipedBody.rotateAngleX * 0.5f, this.bipedBody.rotateAngleY, this.bipedBody.rotateAngleZ);
        this.setRotateAngle(this.skirt1, this.bipedBody.rotateAngleX * 0.5f, this.bipedBody.rotateAngleY, this.bipedBody.rotateAngleZ);
        this.setRotateAngle(this.skirt2, this.bipedBody.rotateAngleX * 0.5f, this.bipedBody.rotateAngleY, this.bipedBody.rotateAngleZ);
        this.setRotateAngle(this.armLeft2, this.bipedLeftArm.rotateAngleX, this.bipedLeftArm.rotateAngleY, this.bipedLeftArm.rotateAngleZ);
        this.setRotateAngle(this.armRight2, this.bipedRightArm.rotateAngleX, this.bipedRightArm.rotateAngleY, this.bipedRightArm.rotateAngleZ);
        if (this.isRiding) {
            this.setRotateAngle(this.legLeft, this.bipedLeftLeg.rotateAngleX, this.bipedLeftLeg.rotateAngleY, this.bipedLeftLeg.rotateAngleZ);
            this.setRotateAngle(this.legRight, this.bipedRightLeg.rotateAngleX, this.bipedRightLeg.rotateAngleY, this.bipedRightLeg.rotateAngleZ);
        } else {
            this.setRotateAngle(this.legLeft, this.bipedLeftLeg.rotateAngleX * 0.5f, this.bipedLeftLeg.rotateAngleY, this.bipedLeftLeg.rotateAngleZ);
            this.setRotateAngle(this.legRight, this.bipedRightLeg.rotateAngleX * 0.5f, this.bipedRightLeg.rotateAngleY, this.bipedRightLeg.rotateAngleZ);
        }
        this.bipedLeftArm.rotateAngleZ -= 0.1f;
        this.bipedRightArm.rotateAngleZ += 0.1f;
        this.armLeft2.rotateAngleZ -= 0.1f;
        this.armRight2.rotateAngleZ += 0.1f;
        if (this.isSneak) {
            this.legLeft.rotationPointZ = 3.0f;
            this.legRight.rotationPointZ = 3.0f;
            this.legLeft.rotateAngleX -= 0.15f;
            this.legRight.rotateAngleX -= 0.15f;
            this.bipedLeftArm.rotateAngleX += 0.2f;
            this.armLeft2.rotateAngleX += 0.2f;
            this.bipedRightArm.rotateAngleX += 0.2f;
            this.armRight2.rotateAngleX += 0.2f;
        } else {
            this.legLeft.rotationPointZ = 0.0f;
            this.legRight.rotationPointZ = 0.0f;
        }
        this.setRotationPoint(this.bipedLeftArm, 5.0f, 2.0f, 0.5f);
        this.setRotationPoint(this.bipedRightArm, -4.0f, 2.0f, 0.5f);
    }

    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }

    public void setRotationPoint(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotationPointX = x;
        modelRenderer.rotationPointY = y;
        modelRenderer.rotationPointZ = z;
    }
}

