package com.voidsrift.riftflux.mixin.early.beddium;

import com.voidsrift.riftflux.client.photomode.IsometricPhotoModeController;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "com.ventooth.beddium.modules.TerrainRendering.render.SwanSongChunkRenderer", remap = false)
public abstract class MixinBeddiumSwanSongChunkRenderer_PhotoMode {

    @Inject(method = "useBlockFaceCulling", at = @At("HEAD"), cancellable = true, require = 0)
    private void riftflux$disableBeddiumSwanSongChunkFaceCulling(CallbackInfoReturnable<Boolean> cir) {
        if (IsometricPhotoModeController.instance().isActive()) {
            cir.setReturnValue(Boolean.FALSE);
        }
    }
}
