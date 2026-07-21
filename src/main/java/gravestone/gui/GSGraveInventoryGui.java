package gravestone.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gravestone.core.Resources;
import gravestone.gui.container.GraveContainer;
import gravestone.tileentity.TileEntityGSGraveStone;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class GSGraveInventoryGui extends GuiContainer {
   public GSGraveInventoryGui(InventoryPlayer inventoryPlayer, TileEntityGSGraveStone tileEntity) {
      super(new GraveContainer(inventoryPlayer, tileEntity));
      this.ySize = 222;
   }

   protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.mc.getTextureManager().bindTexture(Resources.CHEST_GUI);
      int x = (this.width - this.xSize) / 2;
      int y = (this.height - this.ySize) / 2;
      this.drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize);
   }
}
