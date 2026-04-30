package com.voidsrift.riftflux.mixin.early.beddium;

import com.voidsrift.riftflux.client.photomode.IsometricPhotoModeController;
import org.embeddedt.embeddium.impl.render.viewport.Viewport;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "com.ventooth.beddium.modules.TerrainRendering.ArchaicRenderSectionManager", remap = false)
public abstract class MixinBeddiumArchaicRenderSectionManager_PhotoMode {

    @Inject(method = "useFogOcclusion", at = @At("HEAD"), cancellable = true, require = 0)
    private void riftflux$disableFogOcclusion(CallbackInfoReturnable<Boolean> cir) {
        if (IsometricPhotoModeController.instance().isActive()) {
            cir.setReturnValue(Boolean.FALSE);
        }
    }

    @Inject(method = "shouldUseOcclusionCulling", at = @At("HEAD"), cancellable = true, require = 0)
    private void riftflux$disableSectionOcclusion(Viewport viewport, boolean spectator, CallbackInfoReturnable<Boolean> cir) {
        if (IsometricPhotoModeController.instance().isActive()) {
            cir.setReturnValue(Boolean.FALSE);
        }
    }
}
