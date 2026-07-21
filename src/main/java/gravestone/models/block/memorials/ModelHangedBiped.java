package gravestone.models.block.memorials;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;

public class ModelHangedBiped extends ModelBiped {
   protected ModelRenderer rightArm;
   protected ModelRenderer leftArm;
   protected ModelRenderer rightArm2;
   protected ModelRenderer leftArm2;
   private boolean isInStocks = false;

   public ModelHangedBiped(boolean isInStocks) {
      this(isInStocks, false);
   }

   public ModelHangedBiped(boolean isInStocks, boolean isZombie) {
      super(0.0F, 0.0F, 64, isZombie ? 64 : 32);
      this.isInStocks = isInStocks;
      this.setRotation(this.bipedHead, 0.2F, 0.0F, 0.0F);
      this.rightArm = new ModelRenderer(this, 40, 16);
      this.rightArm.addBox(-4.0F, -2.0F, -2.0F, 4, 4, 4);
      this.rightArm.setRotationPoint(-5.0F, 3.0F, 0.0F);
      this.setRotation(this.rightArm, 0.0F, 0.0F, 1.57F);
      this.rightArm.setTextureSize(this.textureWidth, this.textureHeight);
      this.leftArm = new ModelRenderer(this, 40, 16);
      this.leftArm.addBox(0.0F, -2.0F, -2.0F, 4, 4, 4);
      this.leftArm.setRotationPoint(5.0F, 3.0F, 0.0F);
      this.setRotation(this.leftArm, 0.0F, 0.0F, -1.57F);
      this.leftArm.setTextureSize(this.textureWidth, this.textureHeight);
      this.rightArm2 = new ModelRenderer(this, 40, 16);
      this.rightArm2.addBox(0.0F, 0.0F, 0.0F, 4, 8, 4);
      this.rightArm2.setRotationPoint(-7.0F, 3.0F, -2.0F);
      this.setRotation(this.rightArm2, 0.0F, 0.0F, 3.14F);
      this.rightArm2.setTextureSize(this.textureWidth, this.textureHeight);
      this.leftArm2 = new ModelRenderer(this, 40, 16);
      this.leftArm2.addBox(0.0F, 0.0F, 0.0F, 4, 8, 4);
      this.leftArm2.setRotationPoint(11.0F, 3.0F, -2.0F);
      this.setRotation(this.leftArm2, 0.0F, 0.0F, 3.14F);
      this.leftArm2.setTextureSize(this.textureWidth, this.textureHeight);
   }

   private void setRotation(ModelRenderer model, float x, float y, float z) {
      model.rotateAngleX = x;
      model.rotateAngleY = y;
      model.rotateAngleZ = z;
   }

   public void renderAll() {
      float f5 = 0.0625F;
      this.bipedHead.render(f5);
      this.bipedBody.render(f5);
      if (this.isInStocks) {
         this.rightArm.render(f5);
         this.leftArm.render(f5);
         this.rightArm2.render(f5);
         this.leftArm2.render(f5);
      } else {
         this.bipedRightArm.render(f5);
         this.bipedLeftArm.render(f5);
      }

      this.bipedRightLeg.render(f5);
      this.bipedLeftLeg.render(f5);
   }
}
