package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.ModConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Invincibility logic for:
 *  - Rideable entities being ridden by a player (config: invincibleRideableEntities)
 *  - Owned mobs / pets (config: invincibleOwnedMobs, invincibleOwnedAllMobs)
 */
@Mixin(EntityLivingBase.class)
public abstract class MixinEntityLivingBase_PetInvincibility {

    @Inject(
            method = "attackEntityFrom(Lnet/minecraft/util/DamageSource;F)Z",
            at = @At("HEAD"),
            cancellable = true
    )
    private void rf$invincibilityHandlers(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        EntityLivingBase self = (EntityLivingBase) (Object) this;

        // Server side only
        if (self.worldObj == null || self.worldObj.isRemote) {
            return;
        }

        // === 1) Rideable entities: anything currently ridden by a player ===
        if (ModConfig.invincibleRideableEntities && self.riddenByEntity instanceof EntityPlayer) {
            // Any mob / mount being ridden by a player is invincible
            cir.setReturnValue(false);
            cir.cancel();
            return;
        }

        // === 2) Owned mobs / pets ===
        if (!ModConfig.invincibleOwnedMobs) {
            return; // feature disabled
        }

        // Only care about entities that are "ownable"
        if (!(self instanceof IEntityOwnable)) {
            return;
        }

        IEntityOwnable owned = (IEntityOwnable) self;
        Entity owner = owned.getOwner();

        // Only if the owner is a player
        if (!(owner instanceof EntityPlayer)) {
            return;
        }

        // Mode 1: old behavior – ALL IEntityOwnable with player owner
        if (ModConfig.invincibleOwnedAllMobs) {
            cir.setReturnValue(false);
            cir.cancel();
            return;
        }

        // Mode 2: only true tamed pets (EntityTameable / EntityHorse)
        boolean isTamedPet = false;

        if (self instanceof EntityTameable) {
            isTamedPet = ((EntityTameable) self).isTamed();
        }

        if (!isTamedPet && self instanceof EntityHorse) {
            isTamedPet = ((EntityHorse) self).isTame();
        }

        if (!isTamedPet) {
            // Likely a modded summon or non-tamed ownable entity – let it take damage
            return;
        }

        // Block ALL damage to tamed pets owned by players
        cir.setReturnValue(false);
        cir.cancel();
    }
}
