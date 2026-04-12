package com.voidsrift.riftflux.mixin.early.angelica;

import com.voidsrift.riftflux.client.photomode.IsometricPhotoModeController;
import org.embeddedt.embeddium.impl.render.chunk.RenderSectionManager;
import org.embeddedt.embeddium.impl.render.terrain.SimpleWorldRenderer;
import org.embeddedt.embeddium.impl.render.viewport.Viewport;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "org.embeddedt.embeddium.impl.render.terrain.SimpleWorldRenderer", remap = false)
public abstract class MixinEmbeddiumSimpleWorldRenderer_PhotoMode {

    @Shadow
    protected RenderSectionManager renderSectionManager;

    @Unique
    private int riftflux$lastTerrainRefreshToken = Integer.MIN_VALUE;

    @Inject(method = "setupTerrain", at = @At("HEAD"), require = 0)
    private void riftflux$refreshTerrainForPhotoZoom(Viewport viewport, SimpleWorldRenderer.CameraState cameraState, int frame, boolean spectator, boolean updateImmediately, CallbackInfo ci) {
        IsometricPhotoModeController controller = IsometricPhotoModeController.instance();
        if (!controller.isActive() || this.renderSectionManager == null) {
            this.riftflux$lastTerrainRefreshToken = Integer.MIN_VALUE;
            return;
        }

        int token = controller.getTerrainRefreshToken();
        if (this.riftflux$lastTerrainRefreshToken == Integer.MIN_VALUE) {
            this.riftflux$lastTerrainRefreshToken = token;
            return;
        }

        if (token != this.riftflux$lastTerrainRefreshToken) {
            this.renderSectionManager.markGraphDirty();
            this.riftflux$lastTerrainRefreshToken = token;
        }
    }
}
