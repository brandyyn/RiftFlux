package com.voidsrift.riftflux.vortex.client.gui;

import net.minecraft.client.gui.GuiScreen;

public interface IDefaultGui {
   default GuiScreen getGui() {
      return (GuiScreen)this;
   }

   boolean boundDefaultTexture();

   void setBoundDefaultTexture();
}
