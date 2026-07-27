package com.voidsrift.riftflux.entity;

import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;

public final class PrimedTntCarry {
    public static final String PLAYER_CARRY_TICKS_TAG = "RiftFluxTntCarryTicks";
    private static final String CARRIED_TAG = "RiftFluxTntCarried";
    private static final String THROWN_TAG = "RiftFluxTntThrown";
    private static final double CARRY_FORWARD_OFFSET = 0.15D;
    private static final double CARRY_VERTICAL_OFFSET = 0.55D;

    private PrimedTntCarry() {
    }

    public static boolean isCarried(EntityTNTPrimed tnt) {
        return tnt != null
                && tnt.ridingEntity instanceof EntityPlayer
                && (tnt.worldObj.isRemote || tnt.getEntityData().getBoolean(CARRIED_TAG));
    }

    public static void markCarried(EntityTNTPrimed tnt) {
        tnt.getEntityData().setBoolean(CARRIED_TAG, true);
        tnt.getEntityData().setBoolean(THROWN_TAG, false);
    }

    public static void markReleased(EntityTNTPrimed tnt) {
        tnt.getEntityData().setBoolean(CARRIED_TAG, false);
    }

    public static void markThrown(EntityTNTPrimed tnt) {
        tnt.getEntityData().setBoolean(CARRIED_TAG, false);
        tnt.getEntityData().setBoolean(THROWN_TAG, true);
    }

    public static boolean isThrown(EntityTNTPrimed tnt) {
        if (tnt == null) {
            return false;
        }
        if (tnt.getEntityData().getBoolean(THROWN_TAG)) {
            return true;
        }
        if (tnt.worldObj.isRemote
                && tnt.ridingEntity == null
                && tnt.getEntityData().getBoolean(CARRIED_TAG)
                && (tnt.motionX != 0.0D || tnt.motionY != 0.0D || tnt.motionZ != 0.0D)) {
            markThrown(tnt);
            return true;
        }
        return false;
    }

    public static void settleThrown(EntityTNTPrimed tnt) {
        if (tnt != null) {
            tnt.getEntityData().setBoolean(THROWN_TAG, false);
        }
    }

    public static void clearState(EntityTNTPrimed tnt) {
        if (tnt == null) {
            return;
        }
        markReleased(tnt);
        settleThrown(tnt);
        if (tnt.ridingEntity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) tnt.ridingEntity;
            player.getEntityData().setInteger(PLAYER_CARRY_TICKS_TAG, 0);
            tnt.mountEntity(null);
        }
    }

    public static void updateCarriedPosition(EntityTNTPrimed tnt, EntityPlayer player) {
        if (tnt == null || player == null) {
            return;
        }

        markCarried(tnt);
        float yawRadians = player.rotationYaw / 180.0F * (float) Math.PI;
        double x = player.posX - MathHelper.sin(yawRadians) * CARRY_FORWARD_OFFSET;
        double y = player.posY + (double) player.getEyeHeight() + CARRY_VERTICAL_OFFSET;
        double z = player.posZ + MathHelper.cos(yawRadians) * CARRY_FORWARD_OFFSET;
        tnt.setPosition(x, y, z);
        tnt.motionX = 0.0D;
        tnt.motionY = 0.0D;
        tnt.motionZ = 0.0D;
        tnt.fallDistance = 0.0F;
    }
}
