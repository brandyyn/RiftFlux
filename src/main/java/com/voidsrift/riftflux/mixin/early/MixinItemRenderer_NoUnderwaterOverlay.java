package com.voidsrift.riftflux.mixin.early;

import net.minecraft.client.renderer.ItemRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderer.class)
public abstract class MixinItemRenderer_NoUnderwaterOverlay {
    @Inject(method = "renderWarpedTextureOverlay", at = @At("HEAD"), cancellable = true)
    private void riftflux$disableUnderwaterOverlay(float partialTicks, CallbackInfo ci) {
        ci.cancel();
    }
}
