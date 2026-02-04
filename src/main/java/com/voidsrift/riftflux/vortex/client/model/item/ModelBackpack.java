package com.voidsrift.riftflux.vortex.client.model.item;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;

public class ModelBackpack extends ModelBiped {
   public ModelRenderer backpackBase;
   public ModelRenderer backpackFlap;
   public ModelRenderer backpackPouch;
   public ModelRenderer backpackLock;
   public ModelRenderer backpackFlapFront1;
   public ModelRenderer backpackFlapFront2;
   public ModelRenderer backpackFlapLeft1;
   public ModelRenderer backpackFlapLeft2;
   public ModelRenderer backpackFlapRight1;
   public ModelRenderer backpackFlapRight2;

   public ModelBackpack() {
      this.textureWidth = 64;
      this.textureHeight = 64;
      this.backpackLock = new ModelRenderer(this, 0, 26);
      this.backpackLock.setRotationPoint(0.0F, -0.3F, 5.0F);
      this.backpackLock.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, 0.0F);
      this.backpackFlapLeft2 = new ModelRenderer(this, 26, 12);
      this.backpackFlapLeft2.setRotationPoint(3.7F, -2.0F, 1.5F);
      this.backpackFlapLeft2.addBox(-0.5F, -1.0F, -1.5F, 1, 2, 3, 0.0F);
      this.backpackFlap = new ModelRenderer(this, 0, 12);
      this.backpackFlap.setRotationPoint(0.0F, 4.5F, 2.0F);
      this.backpackFlap.addBox(-4.0F, -2.99F, 0.0F, 8, 3, 5, 0.0F);
      this.backpackPouch = new ModelRenderer(this, 0, 20);
      this.backpackPouch.setRotationPoint(0.0F, 8.5F, 6.6F);
      this.backpackPouch.addBox(-3.0F, -2.0F, -0.5F, 6, 4, 2, 0.0F);
      this.backpackFlapRight1 = new ModelRenderer(this, 26, 17);
      this.backpackFlapRight1.setRotationPoint(-3.7F, -1.5F, 3.7F);
      this.backpackFlapRight1.addBox(-0.5F, -1.5F, -1.5F, 1, 3, 3, 0.0F);
      this.backpackFlapRight2 = new ModelRenderer(this, 26, 23);
      this.backpackFlapRight2.setRotationPoint(-3.7F, -2.0F, 1.5F);
      this.backpackFlapRight2.addBox(-0.5F, -1.0F, -1.5F, 1, 2, 3, 0.0F);
      this.backpackFlapFront2 = new ModelRenderer(this, 26, 4);
      this.backpackFlapFront2.setRotationPoint(0.0F, 1.0F, 4.7005F);
      this.backpackFlapFront2.addBox(-3.0F, -1.5F, -0.5F, 6, 1, 1, 0.0F);
      this.backpackFlapFront1 = new ModelRenderer(this, 26, 0);
      this.backpackFlapFront1.setRotationPoint(0.0F, -1.5F, 4.7F);
      this.backpackFlapFront1.addBox(-3.5F, -1.5F, -0.5F, 7, 3, 1, 0.0F);
      this.backpackFlapLeft1 = new ModelRenderer(this, 26, 6);
      this.backpackFlapLeft1.setRotationPoint(3.71F, -1.5F, 3.7F);
      this.backpackFlapLeft1.addBox(-0.5F, -1.5F, -1.5F, 1, 3, 3, 0.0F);
      this.backpackBase = new ModelRenderer(this, 0, 0);
      this.backpackBase.setRotationPoint(0.0F, 0.0F, 0.0F);
      this.backpackBase.addBox(-4.0F, 4.5F, 2.0F, 8, 7, 5, 0.0F);
      this.backpackFlap.addChild(this.backpackLock);
      this.backpackFlap.addChild(this.backpackFlapLeft2);
      this.backpackBase.addChild(this.backpackFlap);
      this.backpackBase.addChild(this.backpackPouch);
      this.backpackFlap.addChild(this.backpackFlapRight1);
      this.backpackFlap.addChild(this.backpackFlapRight2);
      this.backpackFlap.addChild(this.backpackFlapFront2);
      this.backpackFlap.addChild(this.backpackFlapFront1);
      this.backpackFlap.addChild(this.backpackFlapLeft1);
      this.bipedHeadwear.cubeList.clear();
      this.bipedHead.cubeList.clear();
      this.bipedBody.cubeList.clear();
      this.bipedRightArm.cubeList.clear();
      this.bipedLeftArm.cubeList.clear();
      this.bipedRightLeg.cubeList.clear();
      this.bipedLeftLeg.cubeList.clear();
      this.bipedBody.addChild(this.backpackBase);
   }

   public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
      modelRenderer.rotateAngleX = x;
      modelRenderer.rotateAngleY = y;
      modelRenderer.rotateAngleZ = z;
   }
}
