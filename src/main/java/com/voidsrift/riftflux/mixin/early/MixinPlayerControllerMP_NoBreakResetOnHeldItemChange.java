package com.voidsrift.riftflux.mixin.early;

import net.minecraft.client.multiplayer.PlayerControllerMP;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerControllerMP.class)
public abstract class MixinPlayerControllerMP_NoBreakResetOnHeldItemChange {
    @Shadow
    private int currentBlockX;

    @Shadow
    private int currentBlockY;

    @Shadow
    private int currentblockZ;

    @Inject(method = "sameToolAndBlock", at = @At("HEAD"), cancellable = true)
    private void riftflux$keepMiningProgressWhenHeldItemChanges(
            int x,
            int y,
            int z,
            CallbackInfoReturnable<Boolean> cir
    ) {
        cir.setReturnValue(x == this.currentBlockX && y == this.currentBlockY && z == this.currentblockZ);
    }
}
