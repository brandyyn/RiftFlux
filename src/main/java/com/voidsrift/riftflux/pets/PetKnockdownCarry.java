package com.voidsrift.riftflux.pets;

import com.voidsrift.riftflux.ModConfig;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;

public final class PetKnockdownCarry {
    public static final String PLAYER_CARRY_TICKS_TAG = "RiftFluxPetCarryTicks";
    private static final String CARRIED_TAG = "RiftFluxPetCarried";
    private static final String THROWN_TAG = "RiftFluxPetThrown";
    private static final double CARRY_FORWARD_OFFSET = 0.15D;
    private static final double CARRY_VERTICAL_OFFSET = 0.55D;

    private PetKnockdownCarry() {
    }

    public static boolean isCarried(EntityLivingBase entity) {
        return entity != null
                && entity.ridingEntity instanceof EntityPlayer
                && (entity.worldObj.isRemote || entity.getEntityData().getBoolean(CARRIED_TAG));
    }

    public static boolean isThrown(EntityLivingBase entity) {
        if (entity == null) {
            return false;
        }
        if (entity.getEntityData().getBoolean(THROWN_TAG)) {
            return true;
        }
        if (entity.worldObj.isRemote
                && entity.ridingEntity == null
                && entity.getEntityData().getBoolean(CARRIED_TAG)
                && (entity.motionX != 0.0D || entity.motionY != 0.0D || entity.motionZ != 0.0D)) {
            markThrown(entity);
            return true;
        }
        return false;
    }

    public static boolean hasState(EntityLivingBase entity) {
        return entity != null
                && (entity.getEntityData().getBoolean(CARRIED_TAG)
                        || entity.getEntityData().getBoolean(THROWN_TAG));
    }

    public static boolean canMoveWhileKnockedDown(EntityLivingBase entity) {
        return ModConfig.enablePetKnockdownPickupAndThrow && isThrown(entity);
    }

    public static void markCarried(EntityLivingBase entity) {
        entity.getEntityData().setBoolean(CARRIED_TAG, true);
        entity.getEntityData().setBoolean(THROWN_TAG, false);
    }

    public static void markThrown(EntityLivingBase entity) {
        entity.getEntityData().setBoolean(CARRIED_TAG, false);
        entity.getEntityData().setBoolean(THROWN_TAG, true);
    }

    public static void settleThrown(EntityLivingBase entity) {
        entity.getEntityData().setBoolean(THROWN_TAG, false);
    }

    public static void clearState(EntityLivingBase entity) {
        if (entity == null) {
            return;
        }
        entity.getEntityData().setBoolean(CARRIED_TAG, false);
        entity.getEntityData().setBoolean(THROWN_TAG, false);
        if (entity.ridingEntity instanceof EntityPlayer) {
            EntityPlayer carrier = (EntityPlayer) entity.ridingEntity;
            carrier.getEntityData().setInteger(PLAYER_CARRY_TICKS_TAG, 0);
            entity.mountEntity(null);
        }
    }

    public static void updateCarriedPosition(EntityLivingBase entity, EntityPlayer player) {
        if (entity == null || player == null) {
            return;
        }

        markCarried(entity);
        float yawRadians = player.rotationYaw / 180.0F * (float) Math.PI;
        double x = player.posX - MathHelper.sin(yawRadians) * CARRY_FORWARD_OFFSET;
        double y = player.posY + (double) player.getEyeHeight() + CARRY_VERTICAL_OFFSET;
        double z = player.posZ + MathHelper.cos(yawRadians) * CARRY_FORWARD_OFFSET;
        entity.setPosition(x, y, z);
        entity.motionX = 0.0D;
        entity.motionY = 0.0D;
        entity.motionZ = 0.0D;
        entity.fallDistance = 0.0F;
    }
}
