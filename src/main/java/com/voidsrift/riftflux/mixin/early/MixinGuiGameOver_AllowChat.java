package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.client.DeathScreenChatHelper;
import net.minecraft.client.gui.GuiGameOver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiGameOver.class)
public abstract class MixinGuiGameOver_AllowChat {

    @Inject(method = "keyTyped", at = @At("HEAD"), cancellable = true)
    private void riftflux$allowChat(char typedChar, int keyCode, CallbackInfo ci) {
        if (DeathScreenChatHelper.tryOpenDeathScreenChat(keyCode)) {
            ci.cancel();
        }
    }
}
