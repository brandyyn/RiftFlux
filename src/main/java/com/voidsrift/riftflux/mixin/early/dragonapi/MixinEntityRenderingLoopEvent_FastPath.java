package com.voidsrift.riftflux.mixin.early.dragonapi;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "Reika.DragonAPI.Instantiable.Event.Client.EntityRenderingLoopEvent", remap = false)
public abstract class MixinEntityRenderingLoopEvent_FastPath {

    @Inject(method = "fire", at = @At("HEAD"), cancellable = true, require = 0)
    private static void riftflux$skipUnusedEntityRenderLoopEvent(float ptick, CallbackInfo ci) {
        if (!Reika.DragonAPI.Extras.ChangePacketRenderer.isActive) {
            ci.cancel();
        }
    }
}
