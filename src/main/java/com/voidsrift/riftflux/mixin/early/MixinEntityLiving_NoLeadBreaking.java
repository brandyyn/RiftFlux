package com.voidsrift.riftflux.mixin.early;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityLiving.class)
public abstract class MixinEntityLiving_NoLeadBreaking {
    private static final double LEASH_BREAK_DISTANCE_SQ = 64.0D;

    @Redirect(
            method = "updateLeashedState",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/EntityLiving;clearLeashed(ZZ)V"
            )
    )
    private void riftflux$skipAutomaticLeadBreak(EntityLiving entity, boolean sendPacket, boolean dropLead) {
        // Player interaction removes leads through a different path; only skip automatic maintenance breaks.
    }

    @Inject(method = "clearLeashed(ZZ)V", at = @At("HEAD"), cancellable = true)
    private void riftflux$skipFarLeadBreak(boolean sendPacket, boolean dropLead, CallbackInfo ci) {
        EntityLiving self = (EntityLiving) (Object) this;
        if (!self.getLeashed()) {
            return;
        }

        Entity holder = self.getLeashedToEntity();
        if (holder == null || holder.isDead) {
            return;
        }

        if (self.getDistanceSqToEntity(holder) > LEASH_BREAK_DISTANCE_SQ) {
            ci.cancel();
        }
    }
}
