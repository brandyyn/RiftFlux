package com.voidsrift.riftflux.mixin.early.mcpatcherforge;

import com.voidsrift.riftflux.client.sky.BetaStarsRenderHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "com.prupe.mcpatcher.sky.SkyRenderer$Layer", remap = false)
public abstract class MixinSkyRendererLayer_BetaStars {

    @Inject(
            method = "render()Z",
            at = @At("HEAD"),
            cancellable = true,
            require = 0
    )
    private void riftflux$suppressBetterSkiesStarLayer(CallbackInfoReturnable<Boolean> cir) {
        if (!BetaStarsRenderHelper.shouldSuppressBetterSkiesStarLayer(this)) {
            return;
        }
        cir.setReturnValue(false);
    }
}
