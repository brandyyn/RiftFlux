package gravestone.models.entity;

import gravestone.models.block.ModelSkull;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;

public class ModelSkullCrawler extends ModelBase {
   private static final float baseZ = 0.3490659F;
   private static final float advBaseZ = 0.25830877F;
   private static final float baseY = 0.1047198F;
   private static final float advBaseY = 0.2094396F;
   private static final float rBaseY = 3.2463126F;
   private static final float rAdvBaseY = 3.3510323F;
   private ModelSkull skull;
   ModelRenderer rightLeg1;
   ModelRenderer rightLeg2;
   ModelRenderer rightLeg3;
   ModelRenderer rightLeg4;
   ModelRenderer leftLeg1;
   ModelRenderer leftLeg2;
   ModelRenderer leftLeg3;
   ModelRenderer leftLeg4;

   public ModelSkullCrawler() {
      this.textureWidth = 32;
      this.textureHeight = 32;
      this.skull = new ModelSkull();
      this.rightLeg1 = new ModelRenderer(this, 0, 16);
      this.rightLeg1.addBox(0.0F, 0.0F, 0.0F, 3, 1, 1);
      this.rightLeg1.setRotationPoint(-3.9F, 22.0F, -2.1F);
      this.rightLeg2 = new ModelRenderer(this, 0, 16);
      this.rightLeg2.addBox(0.0F, 0.0F, 0.0F, 3, 1, 1);
      this.rightLeg2.setRotationPoint(-3.9F, 22.0F, -0.9F);
      this.rightLeg3 = new ModelRenderer(this, 0, 16);
      this.rightLeg3.addBox(0.0F, 0.0F, 0.0F, 3, 1, 1);
      this.rightLeg3.setRotationPoint(-3.9F, 22.0F, 0.4F);
      this.rightLeg4 = new ModelRenderer(this, 0, 16);
      this.rightLeg4.addBox(0.0F, 0.0F, 0.0F, 3, 1, 1);
      this.rightLeg4.setRotationPoint(-3.9F, 22.0F, 1.6F);
      this.leftLeg1 = new ModelRenderer(this, 0, 16);
      this.leftLeg1.addBox(0.0F, 0.0F, 0.0F, 3, 1, 1);
      this.leftLeg1.setRotationPoint(3.7F, 22.0F, -3.1F);
      this.leftLeg2 = new ModelRenderer(this, 0, 16);
      this.leftLeg2.addBox(0.0F, 0.0F, 0.0F, 3, 1, 1);
      this.leftLeg2.setRotationPoint(3.7F, 22.0F, -1.9F);
      this.leftLeg3 = new ModelRenderer(this, 0, 16);
      this.leftLeg3.addBox(0.0F, 0.0F, 0.0F, 3, 1, 1);
      this.leftLeg3.setRotationPoint(3.7F, 22.0F, -0.6F);
      this.leftLeg4 = new ModelRenderer(this, 0, 16);
      this.leftLeg4.addBox(0.0F, 0.0F, 0.0F, 3, 1, 1);
      this.leftLeg4.setRotationPoint(3.7F, 22.0F, 0.6F);
   }

   public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
      super.render(entity, f, f1, f2, f3, f4, f5);
      this.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
      GL11.glPushMatrix();
      GL11.glTranslated(0.0D, -0.05D, 0.0D);
      this.skull.render(entity, f, f1, f2, f3, f4, f5);
      GL11.glPopMatrix();
      this.rightLeg1.render(f5);
      this.rightLeg2.render(f5);
      this.rightLeg3.render(f5);
      this.rightLeg4.render(f5);
      this.leftLeg1.render(f5);
      this.leftLeg2.render(f5);
      this.leftLeg3.render(f5);
      this.leftLeg4.render(f5);
   }

   private void setRotation(ModelRenderer model, float x, float y, float z) {
      model.rotateAngleX = x;
      model.rotateAngleY = y;
      model.rotateAngleZ = z;
   }

   public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity entity) {
      super.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
      this.rightLeg1.rotateAngleZ = -0.3490659F;
      this.rightLeg2.rotateAngleZ = -0.25830877F;
      this.rightLeg3.rotateAngleZ = -0.25830877F;
      this.rightLeg4.rotateAngleZ = -0.3490659F;
      this.rightLeg1.rotateAngleY = -3.3510323F;
      this.rightLeg2.rotateAngleY = -3.2463126F;
      this.rightLeg3.rotateAngleY = 3.2463126F;
      this.rightLeg4.rotateAngleY = 3.3510323F;
      this.leftLeg1.rotateAngleZ = 0.3490659F;
      this.leftLeg2.rotateAngleZ = 0.25830877F;
      this.leftLeg3.rotateAngleZ = 0.25830877F;
      this.leftLeg4.rotateAngleZ = 0.3490659F;
      this.leftLeg1.rotateAngleY = 0.2094396F;
      this.leftLeg2.rotateAngleY = 0.1047198F;
      this.leftLeg3.rotateAngleY = -0.1047198F;
      this.leftLeg4.rotateAngleY = -0.2094396F;
      float firstY = -(MathHelper.cos(f * 0.6662F * 2.0F + 0.0F) * 0.4F) * f1;
      float secondY = -(MathHelper.cos(f * 0.6662F * 2.0F + (float)Math.PI) * 0.4F) * f1;
      float thirdY = -(MathHelper.cos(f * 0.6662F * 2.0F + ((float)Math.PI / 2F)) * 0.4F) * f1;
      float fourthY = -(MathHelper.cos(f * 0.6662F * 2.0F + ((float)Math.PI * 1.5F)) * 0.4F) * f1;
      float firstZ = Math.abs(MathHelper.sin(f * 0.6662F + 0.0F) * 0.4F) * f1;
      float secondZ = Math.abs(MathHelper.sin(f * 0.6662F + (float)Math.PI) * 0.4F) * f1;
      float thirdZ = Math.abs(MathHelper.sin(f * 0.6662F + ((float)Math.PI / 2F)) * 0.4F) * f1;
      float fourthZ = Math.abs(MathHelper.sin(f * 0.6662F + ((float)Math.PI * 1.5F)) * 0.4F) * f1;
      this.rightLeg1.rotateAngleY += firstY;
      this.rightLeg2.rotateAngleY += secondY;
      this.rightLeg3.rotateAngleY += thirdY;
      this.rightLeg4.rotateAngleY += fourthY;
      this.rightLeg1.rotateAngleZ += firstZ;
      this.rightLeg2.rotateAngleZ += secondZ;
      this.rightLeg3.rotateAngleZ += thirdZ;
      this.rightLeg4.rotateAngleZ += fourthZ;
      this.leftLeg1.rotateAngleY -= firstY;
      this.leftLeg2.rotateAngleY -= secondY;
      this.leftLeg3.rotateAngleY -= thirdY;
      this.leftLeg4.rotateAngleY -= fourthY;
      this.leftLeg1.rotateAngleZ -= firstZ;
      this.leftLeg2.rotateAngleZ -= secondZ;
      this.leftLeg3.rotateAngleZ -= thirdZ;
      this.leftLeg4.rotateAngleZ -= fourthZ;
   }
}
