package com.voidsrift.riftflux.mixin.early.vortex;

import net.minecraft.client.gui.inventory.GuiInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({GuiInventory.class})
public abstract class MixinGuiInventory {

   @Inject(
      method = {"updateScreen()V"},
      at = {@At("RETURN")}
   )
   private void onUpdateScreen(CallbackInfo ci) {
      // No-op: backpack inventory is integrated into the satchels GUI.
   }
}
