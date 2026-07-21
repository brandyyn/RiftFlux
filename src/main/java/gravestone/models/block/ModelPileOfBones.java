package gravestone.models.block;

import gravestone.core.Resources;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import org.lwjgl.opengl.GL11;

public class ModelPileOfBones extends ModelBase {
   private ModelRenderer bone1;
   private ModelRenderer bone2;
   private ModelRenderer bone3;
   private ModelRenderer bone4;
   private ModelRenderer bone5;
   private ModelRenderer bone6;
   private ModelRenderer bone7;
   private ModelRenderer bone8;
   private ModelSkull skull;

   public ModelPileOfBones() {
      this.textureWidth = 32;
      this.textureHeight = 32;
      this.bone1 = new ModelRenderer(this, 0, 0);
      this.bone1.addBox(0.0F, 0.0F, 0.0F, 4, 1, 1);
      this.bone1.setRotationPoint(7.0F, 23.0F, 4.0F);
      this.bone1.setTextureSize(32, 32);
      this.bone1.mirror = true;
      this.setRotation(this.bone1, 0.0F, -2.303835F, 0.0F);
      this.bone2 = new ModelRenderer(this, 0, 3);
      this.bone2.addBox(0.0F, 0.0F, 0.0F, 4, 1, 1);
      this.bone2.setRotationPoint(0.0F, 23.0F, 1.0F);
      this.bone2.setTextureSize(32, 32);
      this.bone2.mirror = true;
      this.setRotation(this.bone2, 0.0F, 0.2792527F, 0.0F);
      this.bone3 = new ModelRenderer(this, 0, 6);
      this.bone3.addBox(0.0F, 0.0F, 0.0F, 4, 1, 1);
      this.bone3.setRotationPoint(-6.0F, 23.0F, -3.0F);
      this.bone3.setTextureSize(32, 32);
      this.bone3.mirror = true;
      this.setRotation(this.bone3, 0.0F, ((float)Math.PI / 4F), 0.0F);
      this.bone4 = new ModelRenderer(this, 0, 9);
      this.bone4.addBox(0.0F, 0.0F, 0.0F, 4, 1, 1);
      this.bone4.setRotationPoint(-6.0F, 23.0F, 3.0F);
      this.bone4.setTextureSize(32, 32);
      this.bone4.mirror = true;
      this.setRotation(this.bone4, 0.0F, -0.418879F, 0.0F);
      this.bone5 = new ModelRenderer(this, 11, 0);
      this.bone5.addBox(0.0F, 0.0F, 0.0F, 4, 1, 1);
      this.bone5.setRotationPoint(2.0F, 23.0F, -4.0F);
      this.bone5.setTextureSize(32, 32);
      this.bone5.mirror = true;
      this.setRotation(this.bone5, 0.0F, 0.1919862F, 0.0F);
      this.bone6 = new ModelRenderer(this, 11, 3);
      this.bone6.addBox(0.0F, 0.0F, 0.0F, 4, 1, 1);
      this.bone6.setRotationPoint(1.0F, 23.0F, -2.0F);
      this.bone6.setTextureSize(32, 32);
      this.bone6.mirror = true;
      this.setRotation(this.bone6, 0.0F, -0.9250245F, -0.2617994F);
      this.bone7 = new ModelRenderer(this, 11, 6);
      this.bone7.addBox(0.0F, 0.0F, 0.0F, 4, 1, 1);
      this.bone7.setRotationPoint(-6.0F, 23.0F, 7.0F);
      this.bone7.setTextureSize(32, 32);
      this.bone7.mirror = true;
      this.setRotation(this.bone7, 0.0F, 1.117011F, -0.3141593F);
      this.bone8 = new ModelRenderer(this, 11, 9);
      this.bone8.addBox(0.0F, 0.0F, 0.0F, 4, 1, 1);
      this.bone8.setRotationPoint(-5.0F, 22.0F, -5.0F);
      this.bone8.setTextureSize(32, 32);
      this.bone8.mirror = true;
      this.setRotation(this.bone8, 0.0F, -0.5759587F, 0.3490659F);
      this.skull = new ModelSkull();
   }

   private void setRotation(ModelRenderer model, float x, float y, float z) {
      model.rotateAngleX = x;
      model.rotateAngleY = y;
      model.rotateAngleZ = z;
   }

   public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
      super.render(entity, f, f1, f2, f3, f4, f5);
      this.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
      this.bone1.render(f5);
      this.bone2.render(f5);
      this.bone3.render(f5);
      this.bone4.render(f5);
      this.bone5.render(f5);
      this.bone6.render(f5);
      this.bone7.render(f5);
      this.bone8.render(f5);
   }

   public void renderAll(boolean haveSkull) {
      float f5 = 0.0625F;
      this.bone1.render(f5);
      this.bone2.render(f5);
      this.bone3.render(f5);
      this.bone4.render(f5);
      this.bone5.render(f5);
      this.bone6.render(f5);
      this.bone7.render(f5);
      this.bone8.render(f5);
      if (haveSkull) {
         Minecraft.getMinecraft().renderEngine.bindTexture(Resources.SKELETON_SKULL_CANDLE);
         GL11.glPushMatrix();
         GL11.glTranslated(0.4D, 0.0D, 0.5D);
         GL11.glRotated(45.0D, 0.0D, 1.0D, 0.0D);
         this.skull.renderAll();
         GL11.glPopMatrix();
      }

   }
}
