package com.voidsrift.riftflux.mixin.early;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityLivingBase.class)
public abstract class MixinEntityLivingBase_LeashedFallDamage {

    @Inject(
            method = "attackEntityFrom(Lnet/minecraft/util/DamageSource;F)Z",
            at = @At("HEAD"),
            cancellable = true
    )
    private void riftflux$skipLeashedFallDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (source != DamageSource.fall && (source == null || !"fall".equals(source.getDamageType()))) {
            return;
        }

        EntityLivingBase self = (EntityLivingBase) (Object) this;
        if (!(self instanceof EntityLiving)) {
            return;
        }

        EntityLiving living = (EntityLiving) self;
        if (!living.getLeashed()) {
            return;
        }

        Entity holder = living.getLeashedToEntity();
        if (holder == null || holder.isDead) {
            return;
        }

        self.fallDistance = 0.0F;
        cir.setReturnValue(false);
        cir.cancel();
    }
}
