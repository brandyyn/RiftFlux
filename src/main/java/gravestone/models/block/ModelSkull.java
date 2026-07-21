package gravestone.models.block;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelSkull extends ModelBase {
   private ModelRenderer skull;
   private ModelRenderer teeth;

   public ModelSkull() {
      this.textureWidth = 32;
      this.textureHeight = 32;
      this.skull = new ModelRenderer(this, 0, 0);
      this.skull.addBox(0.0F, 0.0F, 0.0F, 8, 6, 8);
      this.skull.setRotationPoint(-4.0F, 16.6F, -4.0F);
      this.setRotation(this.skull, -0.1745329F, 0.0F, 0.0F);
      this.teeth = new ModelRenderer(this, 0, 14);
      this.teeth.addBox(0.0F, 0.0F, 0.0F, 4, 2, 2);
      this.teeth.setRotationPoint(-2.0F, 22.0F, -4.8F);
      this.setRotation(this.teeth, -0.1745329F, 0.0F, 0.0F);
   }

   public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
      super.render(entity, f, f1, f2, f3, f4, f5);
      this.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
      this.skull.render(f5);
      this.teeth.render(f5);
   }

   private void setRotation(ModelRenderer model, float x, float y, float z) {
      model.rotateAngleX = x;
      model.rotateAngleY = y;
      model.rotateAngleZ = z;
   }

   public void renderAll() {
      this.skull.render(0.0625F);
      this.teeth.render(0.0625F);
   }
}
