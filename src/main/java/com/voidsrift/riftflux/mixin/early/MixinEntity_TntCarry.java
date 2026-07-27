package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.entity.PrimedTntCarry;
import com.voidsrift.riftflux.pets.PetKnockdownCarry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class MixinEntity_TntCarry {

    @Inject(method = "updateRiderPosition()V", at = @At("HEAD"), cancellable = true)
    private void riftflux$updateCarriedPassengerPosition(CallbackInfo ci) {
        Entity carrier = (Entity) (Object) this;
        if (!(carrier instanceof EntityPlayer)) {
            return;
        }

        Entity passenger = carrier.riddenByEntity;
        EntityPlayer player = (EntityPlayer) carrier;
        if (passenger instanceof EntityTNTPrimed
                && PrimedTntCarry.isCarried((EntityTNTPrimed) passenger)) {
            PrimedTntCarry.updateCarriedPosition((EntityTNTPrimed) passenger, player);
            ci.cancel();
        } else if (passenger instanceof EntityLivingBase
                && PetKnockdownCarry.isCarried((EntityLivingBase) passenger)) {
            PetKnockdownCarry.updateCarriedPosition((EntityLivingBase) passenger, player);
            ci.cancel();
        }
    }
}
