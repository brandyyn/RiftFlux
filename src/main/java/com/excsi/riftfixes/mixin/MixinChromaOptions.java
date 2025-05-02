package com.excsi.riftfixes.mixin;

import Reika.ChromatiCraft.Registry.ChromaOptions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChromaOptions.class)
public class MixinChromaOptions {

    @Inject(method = "isUserSpecific",at = @At("HEAD"),remap = false,cancellable = true)
    public void inject(CallbackInfoReturnable<Boolean> cir){
        cir.setReturnValue(false);
    }
}
