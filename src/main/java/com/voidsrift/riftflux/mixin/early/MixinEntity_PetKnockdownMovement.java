package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.pets.PetKnockdown;
import com.voidsrift.riftflux.pets.PetKnockdownCarry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class MixinEntity_PetKnockdownMovement {

    @Inject(method = "setFire(I)V", at = @At("HEAD"), cancellable = true)
    private void riftflux$preventKnockedDownPetFire(int seconds, CallbackInfo ci) {
        if (riftflux$isKnockedDownLiving()) {
            ci.cancel();
        }
    }

    @Inject(method = "playSound(Ljava/lang/String;FF)V", at = @At("HEAD"), cancellable = true)
    private void riftflux$silenceKnockedDownPet(
            String sound,
            float volume,
            float pitch,
            CallbackInfo ci
    ) {
        if (ModConfig.silenceKnockedDownPets && riftflux$isKnockedDownLiving()) {
            ci.cancel();
        }
    }

    @ModifyVariable(method = "moveEntity(DDD)V", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private double riftflux$stopKnockedDownHorizontalX(double movement) {
        return riftflux$shouldStopKnockedDownMovement() ? 0.0D : movement;
    }

    @ModifyVariable(method = "moveEntity(DDD)V", at = @At("HEAD"), argsOnly = true, ordinal = 1)
    private double riftflux$stopKnockedDownUpwardMovement(double movement) {
        return riftflux$shouldStopKnockedDownMovement() ? 0.0D : movement;
    }

    @ModifyVariable(method = "moveEntity(DDD)V", at = @At("HEAD"), argsOnly = true, ordinal = 2)
    private double riftflux$stopKnockedDownHorizontalZ(double movement) {
        return riftflux$shouldStopKnockedDownMovement() ? 0.0D : movement;
    }

    private boolean riftflux$shouldStopKnockedDownMovement() {
        Entity self = (Entity) (Object) this;
        return self instanceof EntityLivingBase
                && PetKnockdown.isKnockedDown((EntityLivingBase) self)
                && !PetKnockdownCarry.canMoveWhileKnockedDown((EntityLivingBase) self);
    }

    private boolean riftflux$isKnockedDownLiving() {
        Entity self = (Entity) (Object) this;
        return self instanceof EntityLivingBase && PetKnockdown.isKnockedDown((EntityLivingBase) self);
    }
}
