package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.client.photomode.IsometricPhotoModeController;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.MovementInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityPlayerSP.class)
public abstract class MixinEntityPlayerSP_IsometricPhotoMode {

    @Shadow
    public MovementInput movementInput;

    @Inject(method = "onLivingUpdate", at = @At("HEAD"))
    private void riftflux$clearPreviousPhotoModeInput(CallbackInfo ci) {
        this.riftflux$clearCameraControlInput();
    }

    @Inject(
            method = "onLivingUpdate",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/MovementInput;updatePlayerMoveState()V",
                    shift = At.Shift.AFTER
            )
    )
    private void riftflux$clearUpdatedPhotoModeInput(CallbackInfo ci) {
        this.riftflux$clearCameraControlInput();
    }

    private void riftflux$clearCameraControlInput() {
        if (!IsometricPhotoModeController.instance().isCapturingInput() || this.movementInput == null) {
            return;
        }

        this.movementInput.moveStrafe = 0.0F;
        this.movementInput.moveForward = 0.0F;
        this.movementInput.jump = false;
        this.movementInput.sneak = false;
    }
}
