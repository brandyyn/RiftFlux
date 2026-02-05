package com.voidsrift.riftflux.mixin.early.vortex;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import com.voidsrift.riftflux.vortex.item.ModItems;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({Container.class})
public abstract class MixinContainer {
   @Inject(
      method = {"onContainerClosed"},
      at = {@At(
   value = "INVOKE",
   target = "net/minecraft/entity/player/EntityPlayer.dropPlayerItemWithRandomChoice(Lnet/minecraft/item/ItemStack;Z)Lnet/minecraft/entity/item/EntityItem;"
)},
      cancellable = true
   )
   private void onContainerClosed(EntityPlayer player, CallbackInfo ci) {
      if ((Object)this == player.inventoryContainer) {
         InventoryPlayer inventoryPlayer = player.inventory;
         ItemStack itemStack = inventoryPlayer.getItemStack();
         if (itemStack != null && com.voidsrift.riftflux.vortex.item.ItemBackpack.getEquippedBackpack(player) != null
                 && itemStack.getItem().isValidArmor(itemStack, 1, player)) {
            ci.cancel();
         }
      }

   }
}
