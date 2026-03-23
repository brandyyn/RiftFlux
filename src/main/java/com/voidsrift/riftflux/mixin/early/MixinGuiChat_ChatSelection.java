package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.client.chatcopy.ChatSelectionManager;
import net.minecraft.client.gui.GuiChat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiChat.class)
public abstract class MixinGuiChat_ChatSelection {
    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void riftflux$interceptChatSelectionClick(int mouseX, int mouseY, int button, CallbackInfo ci) {
        if (ChatSelectionManager.interceptChatClick((GuiChat) (Object) this, mouseX, mouseY, button)) {
            ci.cancel();
        }
    }

    @Inject(method = "mouseClickMove", at = @At("HEAD"))
    private void riftflux$trackChatSelectionDrag(int mouseX, int mouseY, int button, long dragTime, CallbackInfo ci) {
        ChatSelectionManager.handleChatDrag((GuiChat) (Object) this, mouseX, mouseY, button);
    }

    @Inject(method = "mouseMovedOrUp", at = @At("HEAD"))
    private void riftflux$finishChatSelection(int mouseX, int mouseY, int button, CallbackInfo ci) {
        ChatSelectionManager.handleChatMouseUp((GuiChat) (Object) this, mouseX, mouseY, button);
    }

    @Inject(method = "keyTyped", at = @At("HEAD"), cancellable = true)
    private void riftflux$copyChatSelection(char typedChar, int keyCode, CallbackInfo ci) {
        if (ChatSelectionManager.handleChatKeyTyped(typedChar, keyCode)) {
            ci.cancel();
        }
    }

    @Inject(method = "onGuiClosed", at = @At("HEAD"))
    private void riftflux$resetChatSelectionState(CallbackInfo ci) {
        ChatSelectionManager.resetChatState();
    }
}
