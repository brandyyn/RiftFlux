package gravestone.gui;

import gravestone.ModGraveStone;
import gravestone.core.GSMessageHandler;
import gravestone.core.Resources;
import gravestone.gui.container.AltarContainer;
import gravestone.packets.AltarMessageToServer;
import gravestone.tileentity.TileEntityGSAltar;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import org.lwjgl.opengl.GL11;

public class GSAltarGui extends GuiContainer {
   private final String requirementsStr = ModGraveStone.proxy.getLocalizedString("gui.altar.requirements");
   private final String resurrectionButtonStr = ModGraveStone.proxy.getLocalizedString("gui.altar.resurrect");
   private AltarContainer container;
   private GuiButton resurrectionButton;
   private TileEntityGSAltar tileEntity = null;
   private EntityPlayer player = null;

   public GSAltarGui(InventoryPlayer inventoryPlayer, TileEntityGSAltar tileEntity) {
      super(new AltarContainer(inventoryPlayer, tileEntity));
      this.tileEntity = tileEntity;
      this.player = inventoryPlayer.player;
      this.container = (AltarContainer)this.inventorySlots;
   }

   public void initGui() {
      super.initGui();
      this.buttonList.add(this.resurrectionButton = new GuiButton(0, (this.width - this.xSize) / 2 + 100, (this.height - this.ySize) / 2 + 25, 70, 20, this.resurrectionButtonStr));
      this.resurrectionButton.enabled = false;
   }

   public void actionPerformed(GuiButton button) {
      switch(button.id) {
      case 0:
         GSMessageHandler.networkWrapper.sendToServer(new AltarMessageToServer(this.player, this.tileEntity.xCoord, this.tileEntity.yCoord, this.tileEntity.zCoord, AltarMessageToServer.MOB_TYPE.LIVED));
      default:
      }
   }

   protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.mc.renderEngine.bindTexture(Resources.ALTAR);
      int x = (this.width - this.xSize) / 2;
      int y = (this.height - this.ySize) / 2;
      this.drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize);
      this.drawString(this.fontRendererObj, String.format(this.requirementsStr, this.container.getResurrectionLevel()), this.width / 2 - 40, (this.height - this.ySize) / 2 + 55, 16777215);
      if (this.player != null) {
         this.resurrectionButton.enabled = this.player.capabilities.isCreativeMode || this.player.experienceLevel >= this.container.getResurrectionLevel();
      }

   }
}
