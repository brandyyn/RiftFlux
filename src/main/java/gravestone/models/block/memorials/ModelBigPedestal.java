package gravestone.models.block.memorials;

import gravestone.models.block.ModelGraveStone;
import gravestone.renderer.tileentity.TileEntityGSMemorialRenderer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class ModelBigPedestal extends ModelGraveStone {
   protected ModelRenderer Pedestal1;
   protected ModelRenderer Pedestal2;
   protected ModelRenderer Pedestal3;
   protected ModelRenderer Pedestal4;
   protected ModelRenderer Pedestal5;
   protected ModelRenderer Sign;
   protected ResourceLocation texture = null;

   public ModelBigPedestal() {
      this.textureWidth = 64;
      this.textureHeight = 64;
      this.Pedestal1 = new ModelRenderer(this, 0, 24);
      this.Pedestal1.addBox(0.0F, 0.0F, 0.0F, 16, 1, 16);
      this.Pedestal1.setRotationPoint(-8.0F, 23.0F, -8.0F);
      this.Pedestal1.setTextureSize(64, 64);
      this.Pedestal1.mirror = true;
      this.setRotation(this.Pedestal1, 0.0F, 0.0F, 0.0F);
      this.Pedestal2 = new ModelRenderer(this, 0, 24);
      this.Pedestal2.addBox(0.0F, 0.0F, 0.0F, 16, 1, 16);
      this.Pedestal2.setRotationPoint(-8.0F, 8.0F, -8.0F);
      this.Pedestal2.setTextureSize(64, 64);
      this.Pedestal2.mirror = true;
      this.setRotation(this.Pedestal2, 0.0F, 0.0F, 0.0F);
      this.Pedestal3 = new ModelRenderer(this, 0, 41);
      this.Pedestal3.addBox(0.0F, 0.0F, 0.0F, 14, 1, 14);
      this.Pedestal3.setRotationPoint(-7.0F, 22.0F, -7.0F);
      this.Pedestal3.setTextureSize(64, 64);
      this.Pedestal3.mirror = true;
      this.setRotation(this.Pedestal3, 0.0F, 0.0F, 0.0F);
      this.Pedestal4 = new ModelRenderer(this, 0, 41);
      this.Pedestal4.addBox(0.0F, 0.0F, 0.0F, 14, 1, 14);
      this.Pedestal4.setRotationPoint(-7.0F, 9.0F, -7.0F);
      this.Pedestal4.setTextureSize(64, 64);
      this.Pedestal4.mirror = true;
      this.setRotation(this.Pedestal4, 0.0F, 0.0F, 0.0F);
      this.Pedestal5 = new ModelRenderer(this, 0, 0);
      this.Pedestal5.addBox(0.0F, 0.0F, 0.0F, 12, 12, 12);
      this.Pedestal5.setRotationPoint(-6.0F, 10.0F, -6.0F);
      this.Pedestal5.setTextureSize(64, 64);
      this.Pedestal5.mirror = true;
      this.setRotation(this.Pedestal5, 0.0F, 0.0F, 0.0F);
      this.Sign = new ModelRenderer(this, 42, 41);
      this.Sign.addBox(0.0F, 0.0F, 0.0F, 10, 5, 1);
      this.Sign.setRotationPoint(-5.0F, 13.0F, -6.5F);
      this.Sign.setTextureSize(64, 64);
      this.Sign.mirror = true;
      this.setRotation(this.Sign, 0.0F, 0.0F, 0.0F);
   }

   public void renderAll() {
      unshiftModel();
      float f5 = 0.0625F;
      TileEntityGSMemorialRenderer.instance.bindTextureByName(this.texture);
      this.Pedestal1.render(f5);
      this.Pedestal2.render(f5);
      this.Pedestal3.render(f5);
      this.Pedestal4.render(f5);
      this.Pedestal5.render(f5);
      this.Sign.render(f5);
   }

   public void setTexture(ResourceLocation texture) {
      this.texture = texture;
   }

   public static void shiftModel() {
      GL11.glTranslated(0.0D, -1.0D, 0.0D);
   }

   public static void unshiftModel() {
      GL11.glTranslated(0.0D, 1.0D, 0.0D);
   }
}
