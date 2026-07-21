package gravestone.models.block;

import gravestone.renderer.tileentity.TileEntityGSSpawnerRenderer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class ModelSpawnerPentagram extends ModelBase {
   private ModelRenderer pentagram;
   private ModelSkullCandle candle1;
   private ModelSkullCandle candle2;
   private ModelSkullCandle candle3;
   private ModelSkullCandle candle4;
   private ModelSkullCandle candle5;
   private ResourceLocation candleTexture;

   public ModelSpawnerPentagram(ResourceLocation candleTexture) {
      this.candleTexture = candleTexture;
      this.textureWidth = 32;
      this.textureHeight = 32;
      this.pentagram = new ModelRenderer(this, -32, -32);
      this.pentagram.addBox(0.0F, 0.0F, 0.0F, 32, 0, 32);
      this.pentagram.setRotationPoint(-16.0F, 24.0F, -16.0F);
      this.candle1 = new ModelSkullCandle();
      this.candle2 = new ModelSkullCandle();
      this.candle3 = new ModelSkullCandle();
      this.candle4 = new ModelSkullCandle();
      this.candle5 = new ModelSkullCandle();
   }

   private void setRotation(ModelRenderer model, float x, float y, float z) {
      model.rotateAngleX = x;
      model.rotateAngleY = y;
      model.rotateAngleZ = z;
   }

   public void renderAll() {
      this.pentagram.render(0.0625F);
      TileEntityGSSpawnerRenderer.instance.bindTextureByName(this.candleTexture);
      GL11.glPushMatrix();
      GL11.glTranslated(0.0D, 0.0D, 1.0D);
      GL11.glRotated(180.0D, 0.0D, 1.0D, 0.0D);
      this.candle1.renderAll();
      GL11.glPopMatrix();
      TileEntityGSSpawnerRenderer.instance.bindTextureByName(this.candleTexture);
      GL11.glPushMatrix();
      GL11.glTranslated(0.95D, 0.0D, 0.3D);
      GL11.glRotated(252.0D, 0.0D, 1.0D, 0.0D);
      this.candle2.renderAll();
      GL11.glPopMatrix();
      TileEntityGSSpawnerRenderer.instance.bindTextureByName(this.candleTexture);
      GL11.glPushMatrix();
      GL11.glTranslated(-0.95D, 0.0D, 0.3D);
      GL11.glRotated(108.0D, 0.0D, 1.0D, 0.0D);
      this.candle3.renderAll();
      GL11.glPopMatrix();
      TileEntityGSSpawnerRenderer.instance.bindTextureByName(this.candleTexture);
      GL11.glPushMatrix();
      GL11.glTranslated(-0.59D, 0.0D, -0.8D);
      GL11.glRotated(36.0D, 0.0D, 1.0D, 0.0D);
      this.candle4.renderAll();
      GL11.glPopMatrix();
      TileEntityGSSpawnerRenderer.instance.bindTextureByName(this.candleTexture);
      GL11.glPushMatrix();
      GL11.glTranslated(0.61D, 0.0D, -0.8D);
      GL11.glRotated(-36.0D, 0.0D, 1.0D, 0.0D);
      this.candle5.renderAll();
      GL11.glPopMatrix();
   }
}
