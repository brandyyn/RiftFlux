package com.voidsrift.riftflux.pets;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.util.ConfigResolver;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;

public final class PetOwnerMeleeProtection {
    private PetOwnerMeleeProtection() {
    }

    public static boolean shouldBlock(EntityLivingBase target, DamageSource source, float amount) {
        if (!ModConfig.enablePetOwnerMeleeProtection || target == null || source == null || amount <= 0.0F) {
            return false;
        }

        Entity attackerEntity = source.getEntity();
        Entity directEntity = source.getSourceOfDamage();
        if (!(attackerEntity instanceof EntityPlayer) || directEntity != attackerEntity) {
            return false;
        }
        if (!(target instanceof IEntityOwnable)) {
            return false;
        }
        if (ConfigResolver.matchesConfiguredEntity(target, ModConfig.petOwnerMeleeProtectionMobBlacklist)) {
            return false;
        }
        if (hasEntries(ModConfig.petOwnerMeleeProtectionMobWhitelist)
                && !ConfigResolver.matchesConfiguredEntity(target, ModConfig.petOwnerMeleeProtectionMobWhitelist)) {
            return false;
        }

        if (!ModConfig.petOwnerMeleeProtectionAllOwnedMobs && !isTamedPet(target)) {
            return false;
        }

        return isOwnedBy((IEntityOwnable) target, (EntityPlayer) attackerEntity);
    }

    private static boolean isTamedPet(EntityLivingBase target) {
        if (target instanceof EntityTameable && ((EntityTameable) target).isTamed()) {
            return true;
        }
        return target instanceof EntityHorse && ((EntityHorse) target).isTame();
    }

    private static boolean isOwnedBy(IEntityOwnable owned, EntityPlayer player) {
        if (owned.getOwner() == player) {
            return true;
        }

        String ownerId = owned.func_152113_b();
        if (ownerId == null || ownerId.isEmpty()) {
            return false;
        }
        return ownerId.equalsIgnoreCase(player.getUniqueID().toString())
                || ownerId.equalsIgnoreCase(player.getCommandSenderName());
    }

    private static boolean hasEntries(String[] entries) {
        if (entries == null) {
            return false;
        }
        for (String entry : entries) {
            if (entry != null && !entry.trim().isEmpty()) {
                return true;
            }
        }
        return false;
    }
}
