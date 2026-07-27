package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.pets.PetKnockdown;
import net.minecraft.entity.passive.EntityWolf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityWolf.class)
public abstract class MixinEntityWolf_PetKnockdownAnimation {

    @Inject(method = "getInterestedAngle(F)F", at = @At("HEAD"), cancellable = true)
    private void riftflux$stopKnockedDownHeadTilt(float partialTicks, CallbackInfoReturnable<Float> cir) {
        EntityWolf self = (EntityWolf) (Object) this;
        if (PetKnockdown.isKnockedDown(self)) {
            cir.setReturnValue(Float.valueOf(0.0F));
        }
    }

    @Inject(method = "getShakeAngle(FF)F", at = @At("HEAD"), cancellable = true)
    private void riftflux$stopKnockedDownWetShake(
            float partialTicks,
            float offset,
            CallbackInfoReturnable<Float> cir
    ) {
        EntityWolf self = (EntityWolf) (Object) this;
        if (PetKnockdown.isKnockedDown(self)) {
            cir.setReturnValue(Float.valueOf(0.0F));
        }
    }
}
