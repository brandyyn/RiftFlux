package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.client.photomode.IsometricPhotoModeController;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.settings.GameSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MixinMinecraft_IsometricPhotoMode {

    @Unique
    private boolean riftflux$photoModeActive() {
        return IsometricPhotoModeController.instance().isActive();
    }

    @Inject(method = "loadWorld(Lnet/minecraft/client/multiplayer/WorldClient;Ljava/lang/String;)V", at = @At("HEAD"))
    private void riftflux$disablePhotoModeBeforeWorldChange(WorldClient world, String loadingMessage, CallbackInfo ci) {
        IsometricPhotoModeController.instance().onWorldChange();
    }

    @Redirect(
            method = "runTick",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/settings/GameSettings;thirdPersonView:I",
                    opcode = 181
            )
    )
    private void riftflux$exitPhotoModeOnPerspectiveToggle(GameSettings instance, int value) {
        if (this.riftflux$photoModeActive()) {
            IsometricPhotoModeController.instance().exitWithPerspectiveKey();
            instance.thirdPersonView = 0;
            return;
        }

        instance.thirdPersonView = value;
    }

    @Inject(method = "func_147115_a", at = @At("HEAD"), cancellable = true)
    private void riftflux$cancelLeftClick(boolean leftClick, CallbackInfo ci) {
        if (this.riftflux$photoModeActive()) {
            ci.cancel();
        }
    }

    @Inject(method = "func_147116_af", at = @At("HEAD"), cancellable = true)
    private void riftflux$cancelMiddleClick(CallbackInfo ci) {
        if (this.riftflux$photoModeActive()) {
            ci.cancel();
        }
    }

    @Inject(method = "func_147121_ag", at = @At("HEAD"), cancellable = true)
    private void riftflux$cancelRightClick(CallbackInfo ci) {
        if (this.riftflux$photoModeActive()) {
            ci.cancel();
        }
    }

    @Inject(method = "func_147112_ai", at = @At("HEAD"), cancellable = true)
    private void riftflux$cancelPickBlock(CallbackInfo ci) {
        if (this.riftflux$photoModeActive()) {
            ci.cancel();
        }
    }
}
