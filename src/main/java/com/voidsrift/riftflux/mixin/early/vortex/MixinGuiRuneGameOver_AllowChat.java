package com.voidsrift.riftflux.mixin.early.vortex;

import com.voidsrift.riftflux.client.DeathScreenChatHelper;
import com.voidsrift.riftflux.vortex.client.gui.GuiRuneGameOver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = GuiRuneGameOver.class, remap = false)
public abstract class MixinGuiRuneGameOver_AllowChat {

    @Inject(method = "keyTyped", at = @At("HEAD"), cancellable = true, remap = false)
    private void riftflux$allowChat(char typedChar, int keyCode, CallbackInfo ci) {
        if (DeathScreenChatHelper.tryOpenDeathScreenChat(keyCode)) {
            ci.cancel();
        }
    }
}
