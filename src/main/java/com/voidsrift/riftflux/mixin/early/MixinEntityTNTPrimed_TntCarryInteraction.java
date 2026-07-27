package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.entity.PrimedTntCarry;
import net.minecraft.entity.item.EntityTNTPrimed;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityTNTPrimed.class)
public abstract class MixinEntityTNTPrimed_TntCarryInteraction {

    @Inject(method = "onUpdate()V", at = @At("RETURN"))
    private void riftflux$stopThrownTntSpinOnLanding(CallbackInfo ci) {
        EntityTNTPrimed self = (EntityTNTPrimed) (Object) this;
        if (PrimedTntCarry.isThrown(self)
                && (self.onGround || (self.isCollidedVertically && self.motionY <= 0.0D))) {
            PrimedTntCarry.settleThrown(self);
        }
    }

    @Inject(method = "canBeCollidedWith()Z", at = @At("HEAD"), cancellable = true)
    private void riftflux$makePrimedTntInteractable(CallbackInfoReturnable<Boolean> cir) {
        EntityTNTPrimed self = (EntityTNTPrimed) (Object) this;
        if (PrimedTntCarry.isCarried(self)) {
            cir.setReturnValue(Boolean.FALSE);
            return;
        }
        if (ModConfig.enablePrimedTntPickupAndThrow && !self.isDead) {
            cir.setReturnValue(Boolean.TRUE);
        }
    }
}
