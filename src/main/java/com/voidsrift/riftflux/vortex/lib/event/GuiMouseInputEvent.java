package com.voidsrift.riftflux.vortex.lib.event;

import cpw.mods.fml.common.eventhandler.Cancelable;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.client.event.GuiScreenEvent;

public class GuiMouseInputEvent extends GuiScreenEvent {
   public GuiMouseInputEvent(GuiScreen gui) {
      super(gui);
   }

   @Cancelable
   public static class Post extends GuiMouseInputEvent {
      public Post(GuiScreen gui) {
         super(gui);
      }
   }

   @Cancelable
   public static class Pre extends GuiMouseInputEvent {
      public Pre(GuiScreen gui) {
         super(gui);
      }
   }
}
