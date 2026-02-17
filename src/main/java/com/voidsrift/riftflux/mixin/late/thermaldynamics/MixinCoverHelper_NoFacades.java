package com.voidsrift.riftflux.mixin.late.thermaldynamics;

import cofh.thermaldynamics.duct.attachments.cover.CoverHelper;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = CoverHelper.class, remap = false)
public class MixinCoverHelper_NoFacades {

    @Inject(method = "isValid(Lnet/minecraft/item/ItemStack;)Z", at = @At("HEAD"), cancellable = true)
    private static void riftflux$disableFacadeValidity(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(false);
    }

    @Inject(method = "isValid(Lnet/minecraft/block/Block;I)Z", at = @At("HEAD"), cancellable = true)
    private static void riftflux$disableFacadeBlockValidity(Block block, int meta, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(false);
    }
}
