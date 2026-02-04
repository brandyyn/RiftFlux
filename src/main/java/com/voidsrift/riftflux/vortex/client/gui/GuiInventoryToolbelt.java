package com.voidsrift.riftflux.vortex.client.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import com.voidsrift.riftflux.vortex.lib.container.ContainerToolbelt;
import com.voidsrift.riftflux.vortex.lib.helper.RenderHelper;

@SideOnly(Side.CLIENT)
public class GuiInventoryToolbelt extends GuiContainer implements IDefaultGui {
   protected boolean boundDefaultTexture;
   private int[] edges;

   public GuiInventoryToolbelt(EntityPlayer player) {
      super(new ContainerToolbelt(player));
   }

   public void initGui() {
      this.edges = RenderHelper.generateDefaultGuiEdges(this, this.xSize, 132, 0, -20);
      super.initGui();
   }

   protected void drawGuiContainerForegroundLayer(int par1, int par2) {
      String text = I18n.format("Toolbelt", new Object[0]);
      this.fontRendererObj.drawString(text, 8, 6, 4210752);
      this.fontRendererObj.drawString(I18n.format("container.inventory", new Object[0]), 8, this.ySize - 128, 4210752);
   }

   protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
      RenderHelper.drawDefaultGui(this, this.edges);

      int i;
      for(i = 0; i < 5; ++i) {
         RenderHelper.drawDefaultSlot(this, -36 + i * 18, -57);
      }

      for(i = 0; i < 3; ++i) {
         RenderHelper.drawDefaultSlotRow(this, 0, -26 + i * 18);
      }

      RenderHelper.drawDefaultSlotRow(this, 0, 32);
      this.boundDefaultTexture = false;
   }

   public boolean boundDefaultTexture() {
      return this.boundDefaultTexture;
   }

   public void setBoundDefaultTexture() {
      this.boundDefaultTexture = true;
   }
}
