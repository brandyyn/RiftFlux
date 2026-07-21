package gravestone.models.block.memorials;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gravestone.core.Resources;
import gravestone.models.block.ModelGraveStone;
import gravestone.renderer.tileentity.TileEntityGSMemorialRenderer;
import net.minecraft.client.model.ModelRenderer;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class ModelSmallPedestal extends ModelGraveStone {
   protected ModelRenderer Pedestal;
   protected ModelRenderer Pedestal2;

   public ModelSmallPedestal() {
      this.textureWidth = 64;
      this.textureHeight = 32;
      this.Pedestal = new ModelRenderer(this, 0, 0);
      this.Pedestal.addBox(0.0F, 0.0F, 0.0F, 16, 4, 16);
      this.Pedestal.setRotationPoint(-8.0F, 20.0F, -8.0F);
      this.Pedestal.setTextureSize(64, 32);
      this.Pedestal.mirror = true;
      this.setRotation(this.Pedestal, 0.0F, 0.0F, 0.0F);
      this.Pedestal2 = new ModelRenderer(this, 2, 2);
      this.Pedestal2.addBox(0.0F, 0.0F, 0.0F, 14, 4, 14);
      this.Pedestal2.setRotationPoint(-7.0F, 16.0F, -7.0F);
      this.Pedestal2.setTextureSize(64, 32);
      this.Pedestal2.mirror = true;
      this.setRotation(this.Pedestal2, 0.0F, 0.0F, 0.0F);
   }

   public void renderAll() {
      unshiftModel();
      float par7 = 0.0625F;
      TileEntityGSMemorialRenderer.instance.bindTextureByName(Resources.SMALL_PEDESTAL);
      this.Pedestal.render(par7);
      this.Pedestal2.render(par7);
   }

   public static void shiftModel() {
      GL11.glTranslated(0.0D, -0.5D, 0.0D);
   }

   public static void unshiftModel() {
      GL11.glTranslated(0.0D, 0.5D, 0.0D);
   }
}
