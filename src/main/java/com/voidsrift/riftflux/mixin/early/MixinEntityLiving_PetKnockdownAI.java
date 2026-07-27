package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.pets.PetKnockdown;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityLiving.class)
public abstract class MixinEntityLiving_PetKnockdownAI {

    @Inject(method = "updateEntityActionState()V", at = @At("HEAD"), cancellable = true)
    private void riftflux$freezeKnockedDownAI(CallbackInfo ci) {
        EntityLiving self = (EntityLiving) (Object) this;
        riftflux$clearKnockedDownTarget(self);
        if (PetKnockdown.isKnockedDown(self)) {
            ci.cancel();
        }
    }

    @Inject(method = "updateEntityActionState()V", at = @At("RETURN"))
    private void riftflux$clearTargetAfterAI(CallbackInfo ci) {
        riftflux$clearKnockedDownTarget((EntityLiving) (Object) this);
    }

    @ModifyVariable(
            method = "setAttackTarget(Lnet/minecraft/entity/EntityLivingBase;)V",
            at = @At("HEAD"),
            argsOnly = true,
            ordinal = 0
    )
    private EntityLivingBase riftflux$rejectKnockedDownAttackTarget(EntityLivingBase target) {
        return target != null && PetKnockdown.isKnockedDown(target) ? null : target;
    }

    private static void riftflux$clearKnockedDownTarget(EntityLiving self) {
        EntityLivingBase target = self.getAttackTarget();
        if (target != null && PetKnockdown.isKnockedDown(target)) {
            self.setAttackTarget(null);
        }
        EntityLivingBase revengeTarget = self.getAITarget();
        if (revengeTarget != null && PetKnockdown.isKnockedDown(revengeTarget)) {
            self.setRevengeTarget(null);
        }
    }
}
