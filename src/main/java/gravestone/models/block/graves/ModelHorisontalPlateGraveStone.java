package gravestone.models.block.graves;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gravestone.models.block.ModelGraveStone;
import net.minecraft.client.model.ModelRenderer;

@SideOnly(Side.CLIENT)
public class ModelHorisontalPlateGraveStone extends ModelGraveStone {
   ModelRenderer Plate;

   public ModelHorisontalPlateGraveStone() {
      this.textureWidth = 64;
      this.textureHeight = 32;
      this.Plate = new ModelRenderer(this, 0, 0);
      this.Plate.addBox(0.0F, 0.0F, 0.0F, 12, 1, 14);
      this.Plate.setRotationPoint(-6.0F, 23.0F, -7.0F);
      this.Plate.setTextureSize(64, 32);
      this.Plate.mirror = true;
      this.setRotation(this.Plate, 0.0F, 0.0F, 0.0F);
   }

   public void renderAll() {
      this.Plate.render(0.0625F);
   }
}
