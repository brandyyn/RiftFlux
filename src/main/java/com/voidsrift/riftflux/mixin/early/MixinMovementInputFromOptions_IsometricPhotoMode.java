package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.client.photomode.IsometricPhotoModeController;
import net.minecraft.util.MovementInputFromOptions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MovementInputFromOptions.class)
public abstract class MixinMovementInputFromOptions_IsometricPhotoMode {

    @Inject(method = "updatePlayerMoveState", at = @At("RETURN"))
    private void riftflux$cancelMovementWhileControllingCamera(CallbackInfo ci) {
        IsometricPhotoModeController controller = IsometricPhotoModeController.instance();
        if (!controller.isActive() || controller.isPlayerControlled()) {
            return;
        }

        MovementInputFromOptions input = (MovementInputFromOptions) (Object) this;
        input.moveStrafe = 0.0F;
        input.moveForward = 0.0F;
        input.jump = false;
        input.sneak = false;
    }
}
