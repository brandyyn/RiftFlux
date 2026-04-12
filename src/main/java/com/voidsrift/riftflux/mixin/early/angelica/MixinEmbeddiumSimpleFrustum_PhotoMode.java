package com.voidsrift.riftflux.mixin.early.angelica;

import com.voidsrift.riftflux.client.photomode.IsometricPhotoModeController;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "org.embeddedt.embeddium.impl.render.viewport.frustum.SimpleFrustum", remap = false)
public abstract class MixinEmbeddiumSimpleFrustum_PhotoMode {

    @Inject(method = "testAab", at = @At("HEAD"), cancellable = true, require = 0)
    private void riftflux$disableFrustumCulling(float minX, float minY, float minZ, float maxX, float maxY, float maxZ, CallbackInfoReturnable<Boolean> cir) {
        if (IsometricPhotoModeController.instance().isActive()) {
            cir.setReturnValue(Boolean.TRUE);
        }
    }
}
