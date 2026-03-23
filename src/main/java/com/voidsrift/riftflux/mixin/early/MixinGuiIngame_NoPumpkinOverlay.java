package com.voidsrift.riftflux.mixin.early;

import net.minecraft.client.gui.GuiIngame;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiIngame.class)
public abstract class MixinGuiIngame_NoPumpkinOverlay {
    @Inject(method = "renderPumpkinBlur", at = @At("HEAD"), cancellable = true)
    private void riftflux$disablePumpkinOverlay(int width, int height, CallbackInfo ci) {
        ci.cancel();
    }
}
