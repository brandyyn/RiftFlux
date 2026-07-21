package gravestone.models.block.memorials;

import gravestone.core.Resources;
import gravestone.models.block.ModelSkull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelSkeleton;
import org.lwjgl.opengl.GL11;

public class ModelHangedSkeleton extends ModelSkeleton {
   private ModelSkull skull;
   protected ModelRenderer rightArm;
   protected ModelRenderer leftArm;
   protected ModelRenderer rightArm2;
   protected ModelRenderer leftArm2;
   private boolean isWitherSkeleton = false;
   private boolean isInStocks = false;

   public ModelHangedSkeleton(boolean isInStocks) {
      this(isInStocks, false);
   }

   public ModelHangedSkeleton(boolean isInStocks, boolean isWitherSkeleton) {
      super(0.0F);
      this.isInStocks = isInStocks;
      this.isWitherSkeleton = isWitherSkeleton;
      this.skull = new ModelSkull();
      this.rightArm = new ModelRenderer(this, 40, 16);
      this.rightArm.addBox(-1.0F, -2.0F, -1.0F, 2, 6, 2);
      this.rightArm.setRotationPoint(-6.0F, 1.0F, 0.0F);
      this.setRotation(this.rightArm, 0.0F, 0.0F, 1.57F);
      this.rightArm.setTextureSize(this.textureWidth, this.textureHeight);
      this.leftArm = new ModelRenderer(this, 40, 16);
      this.leftArm.addBox(-1.0F, -2.0F, -1.0F, 2, 6, 2);
      this.leftArm.setRotationPoint(6.0F, 1.0F, 0.0F);
      this.setRotation(this.leftArm, 0.0F, 0.0F, -1.57F);
      this.leftArm.setTextureSize(this.textureWidth, this.textureHeight);
      this.rightArm2 = new ModelRenderer(this, 40, 16);
      this.rightArm2.addBox(0.0F, 0.0F, 0.0F, 2, 8, 2);
      this.rightArm2.setRotationPoint(-9.0F, 2.0F, -1.0F);
      this.setRotation(this.rightArm2, 0.0F, 0.0F, 3.14F);
      this.rightArm2.setTextureSize(this.textureWidth, this.textureHeight);
      this.leftArm2 = new ModelRenderer(this, 40, 16);
      this.leftArm2.addBox(0.0F, 0.0F, 0.0F, 2, 8, 2);
      this.leftArm2.setRotationPoint(11.0F, 2.0F, -1.0F);
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
      this.bipedBody.render(f5);
      this.bipedRightLeg.render(f5);
      this.bipedLeftLeg.render(f5);
      if (this.isInStocks) {
         this.rightArm.render(f5);
         this.leftArm.render(f5);
         this.rightArm2.render(f5);
         this.leftArm2.render(f5);
      } else {
         this.bipedRightArm.render(f5);
         this.bipedLeftArm.render(f5);
      }

      Minecraft.getMinecraft().renderEngine.bindTexture(this.isWitherSkeleton ? Resources.WITHER_SKULL_CANDLE : Resources.SKELETON_SKULL_CANDLE);
      GL11.glPushMatrix();
      GL11.glRotated(20.0D, 1.0D, 0.0D, 0.0D);
      GL11.glTranslated(0.0D, -1.5D, 0.0D);
      this.skull.renderAll();
      GL11.glPopMatrix();
   }
}
