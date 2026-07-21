package gravestone.models.block.memorials;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gravestone.models.block.ModelMemorial;
import net.minecraft.client.model.ModelRenderer;

@SideOnly(Side.CLIENT)
public class ModelMemorialCross extends ModelMemorial {
   ModelRenderer BottomPlate;
   ModelRenderer BottomPlate2;
   ModelRenderer VerticalPart;
   ModelRenderer RightHorisontalPart;
   ModelRenderer LeftHorisontalPart;

   public ModelMemorialCross() {
      this.textureWidth = 256;
      this.textureHeight = 128;
      this.BottomPlate = new ModelRenderer(this, 0, 0);
      this.BottomPlate.addBox(0.0F, 0.0F, 0.0F, 48, 6, 48);
      this.BottomPlate.setRotationPoint(-24.0F, 18.0F, -24.0F);
      this.BottomPlate.setTextureSize(64, 32);
      this.BottomPlate.mirror = true;
      this.setRotation(this.BottomPlate, 0.0F, 0.0F, 0.0F);
      this.BottomPlate2 = new ModelRenderer(this, 0, 54);
      this.BottomPlate2.addBox(0.0F, 0.0F, 0.0F, 32, 6, 32);
      this.BottomPlate2.setRotationPoint(-16.0F, 12.0F, -16.0F);
      this.BottomPlate2.setTextureSize(64, 32);
      this.BottomPlate2.mirror = true;
      this.setRotation(this.BottomPlate2, 0.0F, 0.0F, 0.0F);
      this.VerticalPart = new ModelRenderer(this, 192, 0);
      this.VerticalPart.addBox(0.0F, 0.0F, 0.0F, 10, 68, 10);
      this.VerticalPart.setRotationPoint(-5.0F, -55.0F, -5.0F);
      this.VerticalPart.setTextureSize(64, 32);
      this.VerticalPart.mirror = true;
      this.setRotation(this.VerticalPart, 0.0F, 0.0F, 0.0F);
      this.RightHorisontalPart = new ModelRenderer(this, 128, 54);
      this.RightHorisontalPart.addBox(0.0F, 0.0F, 0.0F, 20, 10, 10);
      this.RightHorisontalPart.setRotationPoint(5.0F, -36.0F, -5.0F);
      this.RightHorisontalPart.setTextureSize(64, 32);
      this.RightHorisontalPart.mirror = true;
      this.setRotation(this.RightHorisontalPart, 0.0F, 0.0F, 0.0F);
      this.LeftHorisontalPart = new ModelRenderer(this, 128, 54);
      this.LeftHorisontalPart.addBox(0.0F, 0.0F, 0.0F, 20, 10, 10);
      this.LeftHorisontalPart.setRotationPoint(-25.0F, -36.0F, -5.0F);
      this.LeftHorisontalPart.setTextureSize(64, 32);
      this.LeftHorisontalPart.mirror = true;
      this.setRotation(this.LeftHorisontalPart, 0.0F, 0.0F, 0.0F);
   }

   public void renderAll() {
      this.BottomPlate.render(0.0625F);
      this.BottomPlate2.render(0.0625F);
      this.VerticalPart.render(0.0625F);
      this.RightHorisontalPart.render(0.0625F);
      this.LeftHorisontalPart.render(0.0625F);
   }
}
