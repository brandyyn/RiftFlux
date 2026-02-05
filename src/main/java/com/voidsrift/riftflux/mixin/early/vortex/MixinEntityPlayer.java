package com.voidsrift.riftflux.mixin.early.vortex;

import net.minecraft.entity.player.EntityPlayer;
import com.voidsrift.riftflux.vortex.item.ModItems;
import com.voidsrift.riftflux.vortex.lib.helper.ItemHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({EntityPlayer.class})
public abstract class MixinEntityPlayer {
   @Inject(
      method = {"canEat"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onCanEat(boolean ignoreHunger, CallbackInfoReturnable<Boolean> ci) {
      EntityPlayer player = (EntityPlayer)(Object)this;
      if (ItemHelper.hasBauble(player, ModItems.gluttonyCharm)) {
         ci.setReturnValue(true);
      }

   }
}
