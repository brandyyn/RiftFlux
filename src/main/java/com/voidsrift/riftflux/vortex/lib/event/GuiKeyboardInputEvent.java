package com.voidsrift.riftflux.vortex.lib.event;

import cpw.mods.fml.common.eventhandler.Cancelable;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.client.event.GuiScreenEvent;

public class GuiKeyboardInputEvent extends GuiScreenEvent {
   public GuiKeyboardInputEvent(GuiScreen gui) {
      super(gui);
   }

   @Cancelable
   public static class Post extends GuiKeyboardInputEvent {
      public Post(GuiScreen gui) {
         super(gui);
      }
   }

   @Cancelable
   public static class Pre extends GuiKeyboardInputEvent {
      public Pre(GuiScreen gui) {
         super(gui);
      }
   }
}
