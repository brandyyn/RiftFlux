package gravestone.models.block;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import org.lwjgl.opengl.GL11;

public class ModelCandle extends ModelBase {
   private ModelRenderer Candle;
   private ModelRenderer Thread;

   public ModelCandle() {
      this.textureWidth = 16;
      this.textureHeight = 16;
      this.Candle = new ModelRenderer(this, 0, 0);
      this.Candle.addBox(0.0F, 0.0F, 0.0F, 2, 5, 2);
      this.Candle.setRotationPoint(-1.5F, 19.0F, 0.0F);
      this.setRotation(this.Candle, 0.0F, ((float)Math.PI / 4F), 0.0F);
      this.Thread = new ModelRenderer(this, 12, 0);
      this.Thread.addBox(0.0F, 0.0F, 0.0F, 1, 2, 1);
      this.Thread.setRotationPoint(-0.5F, 17.5F, -0.5F);
   }

   public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
      super.render(entity, f, f1, f2, f3, f4, f5);
      this.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
      this.Candle.render(f5);
      this.Thread.render(f5);
   }

   private void setRotation(ModelRenderer model, float x, float y, float z) {
      model.rotateAngleX = x;
      model.rotateAngleY = y;
      model.rotateAngleZ = z;
   }

   public void renderAll() {
      this.Candle.render(0.0625F);
      GL11.glPushMatrix();
      GL11.glScalef(0.5F, 1.0F, 0.5F);
      this.Thread.render(0.0625F);
      GL11.glPopMatrix();
   }
}
