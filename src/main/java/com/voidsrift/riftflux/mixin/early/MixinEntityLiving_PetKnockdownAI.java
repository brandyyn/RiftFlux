package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.pets.PetKnockdown;
import net.minecraft.entity.EntityLiving;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityLiving.class)
public abstract class MixinEntityLiving_PetKnockdownAI {

    @Inject(method = "updateEntityActionState()V", at = @At("HEAD"), cancellable = true)
    private void riftflux$freezeKnockedDownAI(CallbackInfo ci) {
        EntityLiving self = (EntityLiving) (Object) this;
        if (PetKnockdown.isKnockedDown(self)) {
            ci.cancel();
        }
    }
}
