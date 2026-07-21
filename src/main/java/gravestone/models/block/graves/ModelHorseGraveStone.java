package gravestone.models.block.graves;

import gravestone.models.block.ModelGraveStone;
import net.minecraft.client.model.ModelRenderer;
import org.lwjgl.opengl.GL11;

public class ModelHorseGraveStone extends ModelGraveStone {
   private ModelRenderer head;
   private ModelRenderer mouthTop;
   private ModelRenderer mouthBottom;
   private ModelRenderer horseLeftEar;
   private ModelRenderer horseRightEar;
   private ModelRenderer neck;
   private ModelRenderer mane;
   private ModelRenderer pedestal;
   private ModelRenderer pedestal2;

   public ModelHorseGraveStone() {
      this.textureWidth = 128;
      this.textureHeight = 128;
      this.head = new ModelRenderer(this, 0, 0);
      this.head.addBox(-2.5F, -10.0F, -1.5F, 5, 5, 7);
      this.head.setRotationPoint(0.0F, 4.0F, -10.0F);
      this.setRotation(this.head, ((float)Math.PI / 6F), 0.0F, 0.0F);
      this.mouthTop = new ModelRenderer(this, 24, 18);
      this.mouthTop.addBox(-2.0F, -10.0F, -7.0F, 4, 3, 6);
      this.mouthTop.setRotationPoint(0.0F, 3.95F, -10.0F);
      this.setRotation(this.mouthTop, ((float)Math.PI / 6F), 0.0F, 0.0F);
      this.mouthBottom = new ModelRenderer(this, 24, 27);
      this.mouthBottom.addBox(-2.0F, -7.0F, -6.5F, 4, 2, 5);
      this.mouthBottom.setRotationPoint(0.0F, 4.0F, -10.0F);
      this.setRotation(this.mouthBottom, ((float)Math.PI / 6F), 0.0F, 0.0F);
      this.head.addChild(this.mouthTop);
      this.head.addChild(this.mouthBottom);
      this.horseLeftEar = new ModelRenderer(this, 0, 0);
      this.horseLeftEar.addBox(0.45F, -12.0F, 4.0F, 2, 3, 1);
      this.horseLeftEar.setRotationPoint(0.0F, 4.0F, -10.0F);
      this.setRotation(this.horseLeftEar, ((float)Math.PI / 6F), 0.0F, 0.0F);
      this.horseRightEar = new ModelRenderer(this, 0, 0);
      this.horseRightEar.addBox(-2.45F, -12.0F, 4.0F, 2, 3, 1);
      this.horseRightEar.setRotationPoint(0.0F, 4.0F, -10.0F);
      this.setRotation(this.horseRightEar, ((float)Math.PI / 6F), 0.0F, 0.0F);
      this.neck = new ModelRenderer(this, 0, 12);
      this.neck.addBox(-2.05F, -9.8F, -2.0F, 4, 14, 8);
      this.neck.setRotationPoint(0.0F, 4.0F, -10.0F);
      this.setRotation(this.neck, ((float)Math.PI / 6F), 0.0F, 0.0F);
      this.mane = new ModelRenderer(this, 58, 0);
      this.mane.addBox(-1.0F, -11.5F, 5.0F, 2, 16, 4);
      this.mane.setRotationPoint(0.0F, 4.0F, -10.0F);
      this.setRotation(this.mane, ((float)Math.PI / 6F), 0.0F, 0.0F);
      this.pedestal = new ModelRenderer(this, 0, 68);
      this.pedestal.addBox(0.0F, 0.0F, 0.0F, 12, 1, 9);
      this.pedestal.setRotationPoint(-6.0F, 23.0F, -2.0F);
      this.pedestal2 = new ModelRenderer(this, 33, 68);
      this.pedestal2.addBox(0.0F, 0.0F, 0.0F, 8, 2, 5);
      this.pedestal2.setRotationPoint(-4.0F, 21.0F, 1.0F);
   }

   public void renderAll() {
      this.pedestal.render(0.0625F);
      this.pedestal2.render(0.0625F);
      float horseSize = 0.7F;
      GL11.glPushMatrix();
      GL11.glTranslatef(0.0F, 0.75F, 0.5F);
      GL11.glScalef(horseSize, horseSize, horseSize);
      GL11.glTranslatef(0.0F, 1.35F * (1.0F - horseSize), 0.0F);
      this.neck.render(0.0625F);
      this.mane.render(0.0625F);
      GL11.glPopMatrix();
      GL11.glPushMatrix();
      GL11.glTranslatef(0.0F, 0.75F, 0.5F);
      float f8 = 0.5F + horseSize * horseSize * 0.5F;
      GL11.glScalef(f8, f8, f8);
      GL11.glTranslatef(0.0F, 1.35F * (1.0F - horseSize), 0.0F);
      this.mouthTop.rotationPointY = 0.02F;
      this.mouthBottom.rotationPointY = 0.0F;
      this.mouthTop.rotationPointZ = 0.02F;
      this.mouthBottom.rotationPointZ = 0.0F;
      this.mouthTop.rotateAngleX = 0.0F;
      this.mouthBottom.rotateAngleX = 0.0F;
      this.mouthTop.rotateAngleY = 0.0F;
      this.mouthBottom.rotateAngleY = 0.0F;
      this.horseLeftEar.render(0.0625F);
      this.horseRightEar.render(0.0625F);
      this.head.render(0.0625F);
      GL11.glPopMatrix();
   }
}
