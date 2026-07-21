package gravestone.models.block;

import gravestone.core.Resources;
import gravestone.renderer.tileentity.TileEntityGSMemorialRenderer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import org.lwjgl.opengl.GL11;

public class ModelSkullCandle extends ModelBase {
   private ModelSkull skull;
   private ModelCandle candle;

   public ModelSkullCandle() {
      this.textureWidth = 32;
      this.textureHeight = 32;
      this.skull = new ModelSkull();
      this.candle = new ModelCandle();
   }

   public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
      super.render(entity, f, f1, f2, f3, f4, f5);
      this.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
      this.skull.render(entity, f, f1, f2, f3, f4, f5);
      this.candle.render(entity, f, f1, f2, f3, f4, f5);
   }

   private void setRotation(ModelRenderer model, float x, float y, float z) {
      model.rotateAngleX = x;
      model.rotateAngleY = y;
      model.rotateAngleZ = z;
   }

   public void renderAll() {
      this.skull.renderAll();
      TileEntityGSMemorialRenderer.instance.bindTextureByName(Resources.CANDLE);
      GL11.glPushMatrix();
      GL11.glTranslated(0.0D, -0.34D, -0.07D);
      this.candle.renderAll();
      GL11.glPopMatrix();
   }
}
