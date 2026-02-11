package com.voidsrift.riftflux.mixin.early.vortex;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.MinecraftForge;
import com.voidsrift.riftflux.vortex.lib.event.GuiKeyboardInputEvent;
import com.voidsrift.riftflux.vortex.lib.event.GuiMouseInputEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = GuiScreen.class, priority = 1100)
public class MixinGuiScreen {
   @Shadow
   public Minecraft mc;

   @Inject(method = "handleMouseInput", at = @At("HEAD"), cancellable = true)
   private void onMouseInputPre(CallbackInfo ci) {
      GuiScreen gui = (GuiScreen)(Object)this;
      if (MinecraftForge.EVENT_BUS.post(new GuiMouseInputEvent.Pre(gui))) {
         ci.cancel();
      }
   }

   @Inject(method = "handleMouseInput", at = @At("RETURN"))
   private void onMouseInputPost(CallbackInfo ci) {
      GuiScreen gui = (GuiScreen)(Object)this;
      if (gui.equals(this.mc.currentScreen)) {
         MinecraftForge.EVENT_BUS.post(new GuiMouseInputEvent.Post(gui));
      }
   }

   @Inject(method = "handleKeyboardInput", at = @At("HEAD"), cancellable = true)
   private void onKeyboardInputPre(CallbackInfo ci) {
      GuiScreen gui = (GuiScreen)(Object)this;
      if (MinecraftForge.EVENT_BUS.post(new GuiKeyboardInputEvent.Pre(gui))) {
         ci.cancel();
      }
   }

   @Inject(method = "handleKeyboardInput", at = @At("RETURN"))
   private void onKeyboardInputPost(CallbackInfo ci) {
      GuiScreen gui = (GuiScreen)(Object)this;
      if (gui.equals(this.mc.currentScreen)) {
         MinecraftForge.EVENT_BUS.post(new GuiKeyboardInputEvent.Post(gui));
      }
   }
}
