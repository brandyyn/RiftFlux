package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.pets.PetKnockdown;
import net.minecraft.entity.passive.EntityTameable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Downed tameables use their neutral standing model before the renderer rolls
 * the whole body into the death pose. This avoids sit/stand pose oscillation.
 */
@Mixin(EntityTameable.class)
public abstract class MixinEntityTameable_PetKnockdown {

    @Inject(method = "isSitting()Z", at = @At("HEAD"), cancellable = true)
    private void riftflux$renderKnockedDownPetsFromNeutralPose(CallbackInfoReturnable<Boolean> cir) {
        EntityTameable self = (EntityTameable) (Object) this;
        if (self.worldObj.isRemote && PetKnockdown.isKnockedDown(self)) {
            cir.setReturnValue(false);
        }
    }
}
