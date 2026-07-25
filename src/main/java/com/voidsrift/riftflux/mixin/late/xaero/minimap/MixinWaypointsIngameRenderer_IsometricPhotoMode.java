package com.voidsrift.riftflux.mixin.late.xaero.minimap;

import com.voidsrift.riftflux.client.photomode.IsometricPhotoModeController;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xaero.common.XaeroMinimapSession;

@Mixin(targets = "xaero.common.minimap.waypoints.render.WaypointsIngameRenderer", remap = false)
public abstract class MixinWaypointsIngameRenderer_IsometricPhotoMode {

    @Inject(
            method = "render(Lxaero/common/XaeroMinimapSession;F)V",
            at = @At("HEAD"),
            cancellable = true,
            require = 1
    )
    private void riftflux$hideWaypointsInIsometricPhotoMode(
            XaeroMinimapSession session,
            float partialTicks,
            CallbackInfo ci
    ) {
        if (IsometricPhotoModeController.instance().isActive()) {
            ci.cancel();
        }
    }
}
