package com.voidsrift.riftflux.mixin.late.botania;

import com.voidsrift.riftflux.dualhotbar.DualHotbarConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import vazkii.botania.client.core.handler.HUDHandler;
import vazkii.botania.common.core.handler.ConfigHandler;

@Mixin(value = HUDHandler.class, remap = false)
public class MixinHUDHandler_ManaBarOffset {
    @Redirect(
            method = "renderManaInvBar",
            at = @At(
                    value = "FIELD",
                    target = "Lvazkii/botania/common/core/handler/ConfigHandler;manaBarHeight:I"
            )
    )
    private int riftflux$offsetBotaniaManaBarHeight() {
        return ConfigHandler.manaBarHeight + riftflux$getDualHotbarHudShift();
    }

    private static int riftflux$getDualHotbarHudShift() {
        if (!DualHotbarConfig.enable) {
            return 0;
        }
        if (!DualHotbarConfig.twoLayerRendering && DualHotbarConfig.numHotbars != 4) {
            return 0;
        }
        if (DualHotbarConfig.twoLayerRendering) {
            return Math.max(0, 20 * (DualHotbarConfig.numHotbars - 1));
        }
        return Math.max(0, 20 * (DualHotbarConfig.numHotbars / 2 - 1));
    }
}
