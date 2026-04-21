package com.voidsrift.riftflux.mixin.early;

import net.minecraft.entity.EntityLiving;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import zairus.worldexplorer.archery.items.WEArcheryItems;

@Mixin(EntityLiving.class)
public abstract class MixinEntityLiving_NoRiftChestPickup {
    @Inject(method = "getArmorPosition", at = @At("HEAD"), cancellable = true)
    private static void riftflux$preventRiftChestMobPickup(ItemStack stack, CallbackInfoReturnable<Integer> cir) {
        if (stack != null && stack.getItem() == WEArcheryItems.captured_ender_chest) {
            cir.setReturnValue(-1);
        }
    }
}
