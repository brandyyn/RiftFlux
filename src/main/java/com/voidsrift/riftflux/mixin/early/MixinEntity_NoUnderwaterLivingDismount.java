package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.palaria.entity.EntityNimatinSeat;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class MixinEntity_NoUnderwaterLivingDismount {

    @Inject(method = "shouldDismountInWater", at = @At("HEAD"), cancellable = true, remap = false)
    private void riftflux$keepProtectedMountsMountedInWater(Entity rider, CallbackInfoReturnable<Boolean> cir) {
        Entity mount = (Entity) (Object) this;
        if (rider != null && riftflux$isWaterDismount(rider, mount) && riftflux$isProtectedWaterMount(mount)) {
            rider.fallDistance = 0.0F;
            cir.setReturnValue(Boolean.FALSE);
        }
    }

    @Inject(method = "mountEntity", at = @At("HEAD"), cancellable = true)
    private void riftflux$preventUnderwaterLivingDismount(Entity targetMount, CallbackInfo ci) {
        Entity self = (Entity) (Object) this;
        if (targetMount != null) {
            return;
        }
        Entity currentMount = self.ridingEntity;
        if (currentMount == null || self.isDead || currentMount.isDead) {
            return;
        }
        if (!riftflux$isProtectedWaterMount(currentMount)) {
            return;
        }
        if (self.worldObj == null || self.worldObj != currentMount.worldObj) {
            return;
        }
        if (!riftflux$isWaterDismount(self, currentMount)) {
            return;
        }
        if (currentMount.riddenByEntity != null && currentMount.riddenByEntity != self) {
            return;
        }
        self.fallDistance = 0.0F;
        ci.cancel();
    }

    @Unique
    private static boolean riftflux$isProtectedWaterMount(Entity mount) {
        return mount instanceof EntityLivingBase || mount instanceof EntityNimatinSeat;
    }

    @Unique
    private static boolean riftflux$isWaterDismount(Entity rider, Entity mount) {
        return (rider != null && rider.isInWater()) || (mount != null && mount.isInWater());
    }
}
