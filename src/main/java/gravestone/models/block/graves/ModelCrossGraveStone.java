package gravestone.models.block.graves;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gravestone.models.block.ModelGraveStone;
import net.minecraft.client.model.ModelRenderer;

@SideOnly(Side.CLIENT)
public class ModelCrossGraveStone extends ModelGraveStone {
   ModelRenderer VerticalPart;
   ModelRenderer RightPart;
   ModelRenderer LeftPart;

   public ModelCrossGraveStone() {
      this.textureWidth = 32;
      this.textureHeight = 32;
      this.VerticalPart = new ModelRenderer(this, 0, 0);
      this.VerticalPart.addBox(0.0F, 0.0F, 0.0F, 2, 16, 2);
      this.VerticalPart.setRotationPoint(-1.0F, 8.0F, 5.0F);
      this.VerticalPart.setTextureSize(32, 32);
      this.VerticalPart.mirror = true;
      this.setRotation(this.VerticalPart, 0.0F, 0.0F, 0.0F);
      this.RightPart = new ModelRenderer(this, 0, 0);
      this.RightPart.addBox(0.0F, 0.0F, 0.0F, 5, 1, 1);
      this.RightPart.setRotationPoint(1.0F, 13.0F, 5.5F);
      this.RightPart.setTextureSize(32, 32);
      this.RightPart.mirror = true;
      this.setRotation(this.RightPart, 0.0F, 0.0F, 0.0F);
      this.LeftPart = new ModelRenderer(this, 0, 0);
      this.LeftPart.addBox(0.0F, 0.0F, 0.0F, 5, 1, 1);
      this.LeftPart.setRotationPoint(-6.0F, 13.0F, 5.5F);
      this.LeftPart.setTextureSize(32, 32);
      this.LeftPart.mirror = true;
      this.setRotation(this.LeftPart, 0.0F, 0.0F, 0.0F);
   }

   public void renderAll() {
      this.VerticalPart.render(0.0625F);
      this.RightPart.render(0.0625F);
      this.LeftPart.render(0.0625F);
   }
}
