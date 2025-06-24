package com.excsi.riftfixes.mixin.late;

import Reika.GeoStrata.Registry.GeoOptions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GeoOptions.class)
public class MixinGeoOptions {

    @Inject(method = "isUserSpecific",at = @At("HEAD"),cancellable = true,remap = false)
    public void inject(CallbackInfoReturnable<Boolean> cir){
        cir.setReturnValue(false);
    }
}
