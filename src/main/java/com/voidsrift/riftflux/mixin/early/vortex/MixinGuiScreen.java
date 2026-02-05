package com.voidsrift.riftflux.mixin.early.vortex;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.MinecraftForge;
import com.voidsrift.riftflux.vortex.lib.event.GuiKeyboardInputEvent;
import com.voidsrift.riftflux.vortex.lib.event.GuiMouseInputEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin({GuiScreen.class})
public abstract class MixinGuiScreen {
   @Shadow
   private Minecraft mc;

   @Shadow
   private void handleMouseInput() {
   }

   @Shadow
   private void handleKeyboardInput() {
   }

   @Overwrite
   public void handleInput() {
      GuiScreen gui;
      if (Mouse.isCreated()) {
         while(Mouse.next()) {
            gui = (GuiScreen)(Object)this;
            if (!MinecraftForge.EVENT_BUS.post(new GuiMouseInputEvent.Pre(gui))) {
               this.handleMouseInput();
               if (gui.equals(this.mc.currentScreen)) {
                  MinecraftForge.EVENT_BUS.post(new GuiMouseInputEvent.Post(gui));
               }
            }
         }
      }

      if (Keyboard.isCreated()) {
         while(Keyboard.next()) {
            gui = (GuiScreen)(Object)this;
            if (!MinecraftForge.EVENT_BUS.post(new GuiKeyboardInputEvent.Pre(gui))) {
               this.handleKeyboardInput();
               if (gui.equals(this.mc.currentScreen)) {
                  MinecraftForge.EVENT_BUS.post(new GuiKeyboardInputEvent.Post(gui));
               }
            }
         }
      }

   }
}
