package com.voidsrift.riftflux.mixin.late.xaero.minimap;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import xaero.common.minimap.waypoints.Waypoint;

@Mixin(targets = "xaero.common.gui.GuiWaypoints", remap = false)
public abstract class MixinGuiWaypoints_ImmediateDelete {
    @Redirect(
            method = {
                    "actionPerformed(Lnet/minecraft/client/gui/GuiButton;)V",
                    "func_146284_a(Lnet/minecraft/client/gui/GuiButton;)V"
            },
            at = @At(
                    value = "INVOKE",
                    target = "Lxaero/common/minimap/waypoints/Waypoint;isTemporary()Z"
            ),
            require = 1
    )
    private boolean riftflux$deleteWaypointWithoutTemporaryConfirmation(Waypoint waypoint) {
        return true;
    }
}
