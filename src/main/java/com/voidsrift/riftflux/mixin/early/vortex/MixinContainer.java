package com.voidsrift.riftflux.mixin.early.vortex;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import com.voidsrift.riftflux.vortex.item.ModItems;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.List;

@Mixin({Container.class})
public abstract class MixinContainer {
   @Shadow
   public List inventorySlots;

   @Inject(
      method = {"putStackInSlot"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void riftflux$guardPutStackInSlot(int slot, ItemStack stack, CallbackInfo ci) {
      if (this.inventorySlots != null && (slot < 0 || slot >= this.inventorySlots.size())) {
         ci.cancel();
      }
   }

   @Inject(
      method = {"getSlot", "func_75139_a"},
      at = @At("HEAD"),
      cancellable = true
   )
   private void riftflux$guardGetSlot(int slot, CallbackInfoReturnable<Slot> cir) {
      if (this.inventorySlots != null && (slot < 0 || slot >= this.inventorySlots.size())) {
         cir.setReturnValue(null);
      }
   }

   @ModifyVariable(
      method = {"putStacksInSlots", "func_75131_a"},
      at = @At("HEAD"),
      index = 1,
      argsOnly = true
   )
   private List riftflux$clampStacksList(List stacks) {
      if (stacks == null || this.inventorySlots == null) {
         return stacks;
      }
      int max = this.inventorySlots.size();
      if (stacks.size() > max) {
         return stacks.subList(0, max);
      }
      return stacks;
   }
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
