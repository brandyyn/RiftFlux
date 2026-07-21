package gravestone.models.block.memorials;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

@SideOnly(Side.CLIENT)
public class ModelHangedVillager extends ModelBase {
   protected ModelRenderer head;
   protected ModelRenderer body;
   protected ModelRenderer rightArm;
   protected ModelRenderer leftArm;
   protected ModelRenderer rightLeg;
   protected ModelRenderer leftLeg;
   protected ModelRenderer nose;
   protected ModelRenderer rightArm2;
   protected ModelRenderer leftArm2;

   public ModelHangedVillager(boolean isInStocks) {
      this(isInStocks, 64, 64);
   }

   public ModelHangedVillager(boolean isInStocks, int textureWidth, int textureHeight) {
      this.textureWidth = textureWidth;
      this.textureHeight = textureHeight;
      this.head = new ModelRenderer(this, 0, 0);
      this.head.addBox(-4.0F, -8.0F, -4.0F, 8, 10, 8);
      this.head.setRotationPoint(0.0F, -2.0F, 0.0F);
      this.head.setTextureSize(textureWidth, textureHeight);
      this.setRotation(this.head, 0.2F, 0.0F, 0.0F);
      this.body = new ModelRenderer(this, 16, 20);
      this.body.setTextureOffset(16, 20).addBox(-4.0F, 0.0F, -2.0F, 8, 12, 6);
      this.body.setTextureOffset(0, 38).addBox(-4.0F, 0.0F, -2.0F, 8, 18, 6, 0.5F);
      this.body.setRotationPoint(0.0F, 0.0F, -1.0F);
      this.body.setTextureSize(textureWidth, textureHeight);
      this.rightArm = new ModelRenderer(this, 44, 22);
      this.rightArm.addBox(-3.0F, -2.0F, -2.0F, 4, 8, 4);
      this.rightArm.setRotationPoint(-5.0F, 2.0F, 0.0F);
      this.rightArm.setTextureSize(textureWidth, textureHeight);
      this.leftArm = new ModelRenderer(this, 44, 22);
      this.leftArm.addBox(-1.0F, -2.0F, -2.0F, 4, 8, 4);
      this.leftArm.setRotationPoint(5.0F, 2.0F, 0.0F);
      this.leftArm.setTextureSize(textureWidth, textureHeight);
      this.rightLeg = new ModelRenderer(this, 0, 22);
      this.rightLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4);
      this.rightLeg.setRotationPoint(-2.0F, 12.0F, 0.0F);
      this.rightLeg.setTextureSize(textureWidth, textureHeight);
      this.leftLeg = new ModelRenderer(this, 0, 22);
      this.leftLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4);
      this.leftLeg.setRotationPoint(2.0F, 12.0F, 0.0F);
      this.leftLeg.setTextureSize(textureWidth, textureHeight);
      this.nose = new ModelRenderer(this, 24, 0);
      this.nose.addBox(0.0F, 0.0F, 0.0F, 2, 4, 2);
      this.nose.setRotationPoint(-1.0F, -3.0F, -6.0F);
      this.nose.setTextureSize(textureWidth, textureHeight);
      this.rightArm2 = new ModelRenderer(this, 44, 22);
      this.rightArm2.addBox(0.0F, 0.0F, 0.0F, 4, 4, 4);
      this.rightArm2.setRotationPoint(-8.0F, 8.0F, -2.0F);
      this.rightArm2.setTextureSize(textureWidth, textureHeight);
      this.leftArm2 = new ModelRenderer(this, 44, 22);
      this.leftArm2.addBox(0.0F, 0.0F, 0.0F, 4, 4, 4);
      this.leftArm2.setRotationPoint(4.0F, 8.0F, -2.0F);
      this.leftArm2.setTextureSize(textureWidth, textureHeight);
      if (isInStocks) {
         this.setRotation(this.leftArm, 0.0F, 0.0F, -1.57F);
         this.setRotation(this.rightArm, 0.0F, 0.0F, 1.57F);
         this.leftArm2.setRotationPoint(11.0F, -1.0F, -2.0F);
         this.setRotation(this.leftArm2, 0.0F, 0.0F, 3.14F);
         this.rightArm2.setRotationPoint(-7.0F, -1.0F, -2.0F);
         this.setRotation(this.rightArm2, 0.0F, 0.0F, 3.14F);
      }

   }

   public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
      super.render(entity, f, f1, f2, f3, f4, f5);
      this.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
      this.head.render(f5);
      this.body.render(f5);
      this.rightArm.render(f5);
      this.leftArm.render(f5);
      this.rightLeg.render(f5);
      this.leftLeg.render(f5);
      this.nose.render(f5);
      this.rightArm2.render(f5);
      this.leftArm2.render(f5);
   }

   protected void setRotation(ModelRenderer model, float x, float y, float z) {
      model.rotateAngleX = x;
      model.rotateAngleY = y;
      model.rotateAngleZ = z;
   }

   public void renderAll() {
      float f5 = 0.0625F;
      this.head.render(f5);
      this.body.render(f5);
      this.rightArm.render(f5);
      this.leftArm.render(f5);
      this.rightLeg.render(f5);
      this.leftLeg.render(f5);
      this.nose.render(f5);
      this.rightArm2.render(f5);
      this.leftArm2.render(f5);
   }
}
