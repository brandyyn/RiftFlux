package com.voidsrift.riftflux.mixin.early.angelica;

import com.voidsrift.riftflux.client.photomode.IsometricPhotoModeController;
import org.embeddedt.embeddium.impl.render.chunk.shader.ChunkFogMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "com.gtnewhorizons.angelica.glsm.AngelicaFogService", remap = false)
public abstract class MixinAngelicaFogService_PhotoMode {

    private static final float[] RIFTFLUX_NO_FOG_COLOR = new float[]{0.0F, 0.0F, 0.0F, 0.0F};

    @Inject(
            method = "getFogMode()Lorg/embeddedt/embeddium/impl/render/chunk/shader/ChunkFogMode;",
            at = @At("HEAD"),
            cancellable = true,
            require = 0
    )
    private void riftflux$disableFogMode(CallbackInfoReturnable<ChunkFogMode> cir) {
        if (IsometricPhotoModeController.instance().isActive()) {
            cir.setReturnValue(ChunkFogMode.NONE);
        }
    }

    @Inject(
            method = "getFogMode()Lorg/embeddedt/embeddium/impl/render/chunk/shader/ChunkShaderComponent$Factory;",
            at = @At("HEAD"),
            cancellable = true,
            require = 0
    )
    private void riftflux$disableFogModeBridge(CallbackInfoReturnable cir) {
        if (IsometricPhotoModeController.instance().isActive()) {
            cir.setReturnValue(ChunkFogMode.NONE);
        }
    }

    @Inject(method = "getFogStart", at = @At("HEAD"), cancellable = true, require = 0)
    private void riftflux$disableFogStart(CallbackInfoReturnable<Float> cir) {
        if (IsometricPhotoModeController.instance().isActive()) {
            cir.setReturnValue(Float.valueOf(Float.MAX_VALUE));
        }
    }

    @Inject(method = "getFogEnd", at = @At("HEAD"), cancellable = true, require = 0)
    private void riftflux$disableFogEnd(CallbackInfoReturnable<Float> cir) {
        if (IsometricPhotoModeController.instance().isActive()) {
            cir.setReturnValue(Float.valueOf(Float.MAX_VALUE));
        }
    }

    @Inject(method = "getFogCutoff", at = @At("HEAD"), cancellable = true, require = 0)
    private void riftflux$disableFogCutoff(CallbackInfoReturnable<Float> cir) {
        if (IsometricPhotoModeController.instance().isActive()) {
            cir.setReturnValue(Float.valueOf(Float.MAX_VALUE));
        }
    }

    @Inject(method = "getFogDensity", at = @At("HEAD"), cancellable = true, require = 0)
    private void riftflux$disableFogDensity(CallbackInfoReturnable<Float> cir) {
        if (IsometricPhotoModeController.instance().isActive()) {
            cir.setReturnValue(Float.valueOf(0.0F));
        }
    }

    @Inject(method = "getFogColor", at = @At("HEAD"), cancellable = true, require = 0)
    private void riftflux$disableFogColor(CallbackInfoReturnable<float[]> cir) {
        if (IsometricPhotoModeController.instance().isActive()) {
            cir.setReturnValue(RIFTFLUX_NO_FOG_COLOR);
        }
    }
}
