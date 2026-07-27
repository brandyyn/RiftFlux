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

/**
 * Shared rules for pets and explicitly whitelisted mobs protected by the
 * knockdown system.
 *
 * Knockdown state is deliberately derived from synchronized health instead of
 * separate NBT. This keeps server, clients, and old worlds in agreement: a
 * protected pet at one health is down, and any healing above one revives it.
 */
public final class PetKnockdown {
    public static final float KNOCKDOWN_HEALTH = 1.0F;
    private static final ThreadLocal<EntityLivingBase> FORCED_DEATH = new ThreadLocal<EntityLivingBase>();

    private PetKnockdown() {
    }

    public static boolean isProtectedPet(EntityLivingBase entity) {
        if (!ModConfig.enablePetKnockdownSystem || entity == null) {
            return false;
        }
        if (ConfigResolver.matchesConfiguredEntity(entity, ModConfig.petKnockdownMobBlacklist)) {
            return false;
        }

        if (hasEntries(ModConfig.petKnockdownMobWhitelist)) {
            return ConfigResolver.matchesConfiguredEntity(entity, ModConfig.petKnockdownMobWhitelist);
        }

        if (!(entity instanceof IEntityOwnable)) {
            return false;
        }

        IEntityOwnable owned = (IEntityOwnable) entity;
        if (ModConfig.petKnockdownAllOwnedMobs) {
            return owned.getOwner() instanceof EntityPlayer;
        }

        boolean tame = entity instanceof EntityTameable && ((EntityTameable) entity).isTamed();
        if (!tame && entity instanceof EntityHorse) {
            tame = ((EntityHorse) entity).isTame();
        }
        return tame && hasOwner(owned);
    }

    public static boolean isKnockedDown(EntityLivingBase entity) {
        return isProtectedPet(entity)
                && entity.getHealth() > 0.0F
                && entity.getHealth() <= KNOCKDOWN_HEALTH;
    }

    public static boolean isForcingDeath(EntityLivingBase entity) {
        return entity != null && FORCED_DEATH.get() == entity;
    }

    public static void expire(EntityLivingBase entity) {
        if (entity == null || entity.worldObj == null || entity.worldObj.isRemote || entity.isDead) {
            return;
        }

        FORCED_DEATH.set(entity);
        try {
            entity.attackEntityFrom(DamageSource.outOfWorld, Float.MAX_VALUE);
            if (!entity.isDead && entity.getHealth() > 0.0F) {
                entity.setDead();
            }
        } finally {
            FORCED_DEATH.remove();
        }
    }

    public static boolean isDamageFromKnockedDownPet(DamageSource source) {
        if (source == null) {
            return false;
        }
        return isKnockedDownLiving(source.getEntity()) || isKnockedDownLiving(source.getSourceOfDamage());
    }

    private static boolean isKnockedDownLiving(Entity entity) {
        return entity instanceof EntityLivingBase && isKnockedDown((EntityLivingBase) entity);
    }

    private static boolean hasOwner(IEntityOwnable owned) {
        if (owned.getOwner() instanceof EntityPlayer) {
            return true;
        }
        String ownerId = owned.func_152113_b();
        return ownerId != null && !ownerId.isEmpty();
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
