package gravestone.renderer.tileentity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class TileEntityGSRenderer extends TileEntitySpecialRenderer {
   public void bindTextureByName(ResourceLocation texture) {
      this.bindTexture(texture);
   }

   public void renderTileEntityAt(TileEntity te, double x, double y, double z, float f) {
   }
}
