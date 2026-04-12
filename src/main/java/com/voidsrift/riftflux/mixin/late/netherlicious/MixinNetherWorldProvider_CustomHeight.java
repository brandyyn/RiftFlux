package com.voidsrift.riftflux.mixin.late.netherlicious;

import DelirusCrux.Netherlicious.Dimension.NetherWorldProvider;
import DelirusCrux.Netherlicious.Utility.Configuration.WorldgenConfiguration;
import com.voidsrift.riftflux.compat.netherlicious.NetherliciousHeightHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = NetherWorldProvider.class, remap = false)
public abstract class MixinNetherWorldProvider_CustomHeight {

    @Inject(method = "getHeight", at = @At("HEAD"), cancellable = true)
    private void riftflux$useConfiguredBigNetherWorldHeight(CallbackInfoReturnable<Integer> cir) {
        if (WorldgenConfiguration.BigNether && NetherliciousHeightHelper.hasCustomBigNetherTopY()) {
            cir.setReturnValue(NetherliciousHeightHelper.getConfiguredBigNetherActualHeight());
        }
    }

    @Inject(method = "getActualHeight", at = @At("HEAD"), cancellable = true)
    private void riftflux$useConfiguredBigNetherHeight(CallbackInfoReturnable<Integer> cir) {
        if (WorldgenConfiguration.BigNether && NetherliciousHeightHelper.hasCustomBigNetherTopY()) {
            cir.setReturnValue(NetherliciousHeightHelper.getConfiguredBigNetherActualHeight());
        }
    }
}
