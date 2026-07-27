package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.pets.PetOwnerMeleeProtection;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityLivingBase.class)
public abstract class MixinEntityLivingBase_PetOwnerMeleeProtection {

    @Inject(
            method = "attackEntityFrom(Lnet/minecraft/util/DamageSource;F)Z",
            at = @At("HEAD"),
            cancellable = true
    )
    private void riftflux$blockOwnerMeleeDamage(
            DamageSource source,
            float amount,
            CallbackInfoReturnable<Boolean> cir
    ) {
        EntityLivingBase self = (EntityLivingBase) (Object) this;
        if (PetOwnerMeleeProtection.shouldBlock(self, source, amount)) {
            cir.setReturnValue(false);
        }
    }
}
