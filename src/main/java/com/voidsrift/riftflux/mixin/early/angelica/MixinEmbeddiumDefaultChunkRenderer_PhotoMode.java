package com.voidsrift.riftflux.mixin.early.angelica;

import com.voidsrift.riftflux.client.photomode.IsometricPhotoModeController;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "org.embeddedt.embeddium.impl.render.chunk.DefaultChunkRenderer", remap = false)
public abstract class MixinEmbeddiumDefaultChunkRenderer_PhotoMode {

    @Inject(method = "useBlockFaceCulling", at = @At("HEAD"), cancellable = true, require = 0)
    private void riftflux$disableEmbeddiumChunkFaceCulling(CallbackInfoReturnable<Boolean> cir) {
        if (IsometricPhotoModeController.instance().isActive()) {
            cir.setReturnValue(Boolean.FALSE);
        }
    }
}
