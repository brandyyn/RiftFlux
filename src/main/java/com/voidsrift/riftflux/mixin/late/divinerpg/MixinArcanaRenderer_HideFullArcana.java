package com.voidsrift.riftflux.mixin.late.divinerpg;

import net.divinerpg.client.ArcanaRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ArcanaRenderer.class, remap = false)
public class MixinArcanaRenderer_HideFullArcana {
    private static final float RIFTFLUX_ARCANA_FULL = 200.0f;

    @Inject(method = "onTickRender", at = @At("HEAD"), cancellable = true)
    private void riftflux$hideArcanaWhenFull(CallbackInfo ci) {
        if (ArcanaRenderer.value >= RIFTFLUX_ARCANA_FULL) {
            ci.cancel();
        }
    }
}
