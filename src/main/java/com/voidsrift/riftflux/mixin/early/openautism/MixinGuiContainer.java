package com.voidsrift.riftflux.mixin.early.vortex;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.vortex.item.ModItems;
import com.voidsrift.riftflux.vortex.lib.helper.ContainerHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({GuiContainer.class})
public abstract class MixinGuiContainer {

   @Inject(
      method = {"handleMouseClick"},
      at = {@At(
   value = "FIELD",
   target = "net/minecraft/inventory.Slot.slotNumber:I"
)},
      cancellable = true
   )
   public void onHandleMouseClick(Slot slot, int slotNumber, int p_146984_3_, int p_146984_4_, CallbackInfo ci) {
      // Avoid @Shadow'ing GuiScreen#mc as it is remapped/obfuscated in 1.7.10.
      // Using the singleton is stable in both dev and production.
      EntityPlayer player = Minecraft.getMinecraft().thePlayer;
      if (!player.capabilities.isCreativeMode && !ModConfig.backpackStorage) {
         ItemStack backpack = com.voidsrift.riftflux.vortex.item.ItemBackpack.getEquippedBackpack(player);
         if (backpack != null && !ContainerHelper.getBackpackInventory(backpack).isEmpty() && slot.getHasStack() && slot.getStack() == backpack) {
            ci.cancel();
         }
      }

   }
}
