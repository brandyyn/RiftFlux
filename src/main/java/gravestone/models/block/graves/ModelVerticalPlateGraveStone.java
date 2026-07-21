package gravestone.models.block.graves;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gravestone.models.block.ModelGraveStone;
import net.minecraft.client.model.ModelRenderer;

@SideOnly(Side.CLIENT)
public class ModelVerticalPlateGraveStone extends ModelGraveStone {
   ModelRenderer Plate;

   public ModelVerticalPlateGraveStone() {
      this.textureWidth = 32;
      this.textureHeight = 32;
      this.Plate = new ModelRenderer(this, 0, 0);
      this.Plate.addBox(0.0F, 0.0F, 0.0F, 12, 15, 2);
      this.Plate.setRotationPoint(-6.0F, 9.0F, 5.0F);
      this.Plate.setTextureSize(32, 32);
      this.Plate.mirror = true;
      this.setRotation(this.Plate, 0.0F, 0.0F, 0.0F);
   }

   public void renderAll() {
      this.Plate.render(0.0625F);
   }
}
