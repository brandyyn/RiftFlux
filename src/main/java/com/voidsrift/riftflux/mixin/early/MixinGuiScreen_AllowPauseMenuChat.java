package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.client.PauseScreenChatHelper;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.GuiScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiScreen.class)
public abstract class MixinGuiScreen_AllowPauseMenuChat {

    @Inject(method = "keyTyped", at = @At("HEAD"), cancellable = true)
    private void riftflux$allowPauseMenuChat(char typedChar, int keyCode, CallbackInfo ci) {
        if (!((Object) this instanceof GuiIngameMenu)) {
            return;
        }
        if (PauseScreenChatHelper.tryOpenPauseScreenChat(keyCode)) {
            ci.cancel();
        }
    }
}
