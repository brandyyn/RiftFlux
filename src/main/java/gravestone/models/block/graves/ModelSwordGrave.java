package gravestone.models.block.graves;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gravestone.models.block.ModelGraveStone;
import net.minecraft.client.model.ModelRenderer;

@SideOnly(Side.CLIENT)
public class ModelSwordGrave extends ModelGraveStone {
   ModelRenderer Blade1;
   ModelRenderer Blade2;
   ModelRenderer Blade3;
   ModelRenderer Blade4;
   ModelRenderer Shape3;
   ModelRenderer Hilt;

   public ModelSwordGrave() {
      this.textureWidth = 32;
      this.textureHeight = 32;
      this.Blade1 = new ModelRenderer(this, 0, 0);
      this.Blade1.addBox(0.0F, 0.0F, 0.0F, 1, 10, 0);
      this.Blade1.setRotationPoint(-1.0F, 14.0F, 0.0F);
      this.Blade1.setTextureSize(64, 32);
      this.Blade1.mirror = true;
      this.setRotation(this.Blade1, 0.0F, 0.2617994F, 0.0F);
      this.Blade2 = new ModelRenderer(this, 2, 0);
      this.Blade2.addBox(0.0F, 0.0F, 0.0F, 1, 10, 0);
      this.Blade2.setRotationPoint(-1.0F, 14.0F, 0.0F);
      this.Blade2.setTextureSize(64, 32);
      this.Blade2.mirror = true;
      this.setRotation(this.Blade2, 0.0F, -0.2617994F, 0.0F);
      this.Blade3 = new ModelRenderer(this, 2, 0);
      this.Blade3.addBox(0.0F, 0.0F, 0.0F, 1, 10, 0);
      this.Blade3.setRotationPoint(0.9F, 14.0F, 0.0F);
      this.Blade3.setTextureSize(64, 32);
      this.Blade3.mirror = true;
      this.setRotation(this.Blade3, 0.0F, 2.879793F, 0.0F);
      this.Blade4 = new ModelRenderer(this, 0, 0);
      this.Blade4.addBox(0.0F, 0.0F, 0.0F, 1, 10, 0);
      this.Blade4.setRotationPoint(0.9F, 14.0F, 0.0F);
      this.Blade4.setTextureSize(64, 32);
      this.Blade4.mirror = true;
      this.setRotation(this.Blade4, 0.0F, -2.879793F, 0.0F);
      this.Shape3 = new ModelRenderer(this, 4, 0);
      this.Shape3.addBox(0.0F, 0.0F, 0.0F, 4, 1, 1);
      this.Shape3.setRotationPoint(-2.0F, 13.0F, -0.5F);
      this.Shape3.setTextureSize(64, 32);
      this.Shape3.mirror = true;
      this.setRotation(this.Shape3, 0.0F, 0.0F, 0.0F);
      this.Hilt = new ModelRenderer(this, 4, 2);
      this.Hilt.addBox(0.0F, 0.0F, 0.0F, 1, 3, 1);
      this.Hilt.setRotationPoint(-0.5F, 10.0F, -0.5F);
      this.Hilt.setTextureSize(64, 32);
      this.Hilt.mirror = true;
      this.setRotation(this.Hilt, 0.0F, 0.0F, 0.0F);
   }

   public void renderAll() {
      this.Blade1.render(0.0625F);
      this.Blade2.render(0.0625F);
      this.Blade3.render(0.0625F);
      this.Blade4.render(0.0625F);
      this.Shape3.render(0.0625F);
      this.Hilt.render(0.0625F);
   }
}
