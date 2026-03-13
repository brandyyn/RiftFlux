package com.voidsrift.riftflux.mixin.late.divinerpg;

import net.divinerpg.client.render.gui.GUIOverlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = GUIOverlay.class, remap = false)
public class MixinGUIOverlay_DisableHaliteArmorPiece {
    @Inject(method = "drawArmor", at = @At("HEAD"), cancellable = true)
    private void riftflux$disableHaliteExtraArmorPiece(CallbackInfo ci) {
        ci.cancel();
    }
}
