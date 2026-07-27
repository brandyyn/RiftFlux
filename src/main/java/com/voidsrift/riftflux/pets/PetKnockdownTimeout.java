package com.voidsrift.riftflux.pets;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.net.MsgSyncPetKnockdownTimeout;
import com.voidsrift.riftflux.net.RFNetwork;
import com.voidsrift.riftflux.util.ConfigResolver;
import cpw.mods.fml.common.network.NetworkRegistry;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;

public final class PetKnockdownTimeout {
    private static final String NBT_ACTIVE = "RiftFluxPetTimeoutActive";
    private static final String NBT_DEADLINE = "RiftFluxPetTimeoutDeadline";
    private static final String NBT_LAST_SYNC_TICK = "RiftFluxPetTimeoutLastSync";
    private static final String CLIENT_ACTIVE = "RiftFluxPetTimeoutClientActive";
    private static final String CLIENT_DEADLINE = "RiftFluxPetTimeoutClientDeadline";
    private static final double SYNC_RANGE = 160.0D;
    private static final int MAX_SECONDS = 31536000;

    private PetKnockdownTimeout() {
    }

    public static void tick(EntityLivingBase entity) {
        if (entity == null || entity.worldObj == null || entity.worldObj.isRemote || entity.isDead) {
            return;
        }

        if (!PetKnockdown.isKnockedDown(entity) || !appliesTo(entity)) {
            clearServerTimer(entity);
            return;
        }

        NBTTagCompound data = entity.getEntityData();
        long now = entity.worldObj.getTotalWorldTime();
        if (!data.getBoolean(NBT_ACTIVE)) {
            int durationSeconds = resolveDurationSeconds(entity);
            if (durationSeconds <= 0) {
                return;
            }

            data.setBoolean(NBT_ACTIVE, true);
            data.setLong(NBT_DEADLINE, now + (long) durationSeconds * 20L);
            data.setLong(NBT_LAST_SYNC_TICK, Long.MIN_VALUE);
        }

        long deadline = data.getLong(NBT_DEADLINE);
        long remaining = deadline - now;
        if (remaining <= 0L) {
            clearServerTimer(entity);
            PetKnockdown.expire(entity);
            return;
        }

        long lastSyncTick = data.getLong(NBT_LAST_SYNC_TICK);
        if (lastSyncTick == Long.MIN_VALUE || now - lastSyncTick >= 100L) {
            data.setLong(NBT_LAST_SYNC_TICK, now);
            syncAround(entity, clampRemainingTicks(remaining));
        }
    }

    public static void syncTo(EntityLivingBase entity, EntityPlayerMP player) {
        if (entity == null || player == null || RFNetwork.CH == null || entity.worldObj == null) {
            return;
        }
        NBTTagCompound data = entity.getEntityData();
        if (!data.getBoolean(NBT_ACTIVE)) {
            return;
        }

        long remaining = data.getLong(NBT_DEADLINE) - entity.worldObj.getTotalWorldTime();
        if (remaining > 0L) {
            RFNetwork.CH.sendTo(
                    new MsgSyncPetKnockdownTimeout(entity.getEntityId(), clampRemainingTicks(remaining)),
                    player
            );
        }
    }

    public static void applyClientSync(EntityLivingBase entity, int remainingTicks) {
        if (entity == null || entity.worldObj == null) {
            return;
        }
        NBTTagCompound data = entity.getEntityData();
        if (remainingTicks < 0) {
            data.removeTag(CLIENT_ACTIVE);
            data.removeTag(CLIENT_DEADLINE);
            return;
        }

        data.setBoolean(CLIENT_ACTIVE, true);
        data.setLong(CLIENT_DEADLINE, entity.worldObj.getTotalWorldTime() + (long) remainingTicks);
    }

    public static int getClientRemainingTicks(EntityLivingBase entity) {
        if (entity == null || entity.worldObj == null) {
            return -1;
        }
        NBTTagCompound data = entity.getEntityData();
        if (!data.getBoolean(CLIENT_ACTIVE)) {
            return -1;
        }

        long remaining = data.getLong(CLIENT_DEADLINE) - entity.worldObj.getTotalWorldTime();
        return remaining <= 0L ? 0 : clampRemainingTicks(remaining);
    }

    private static boolean appliesTo(EntityLivingBase entity) {
        if (!ModConfig.enablePetKnockdownTimeout) {
            return false;
        }
        if (ConfigResolver.matchesConfiguredEntity(entity, ModConfig.petKnockdownTimeoutMobBlacklist)) {
            return false;
        }
        return !hasEntries(ModConfig.petKnockdownTimeoutMobWhitelist)
                || ConfigResolver.matchesConfiguredEntity(entity, ModConfig.petKnockdownTimeoutMobWhitelist);
    }

    private static int resolveDurationSeconds(EntityLivingBase entity) {
        String[] overrides = ModConfig.petKnockdownTimeoutOverrides;
        if (overrides != null) {
            for (String override : overrides) {
                if (override == null) {
                    continue;
                }
                String[] parts = override.split("\\|", 2);
                if (parts.length != 2 || !ConfigResolver.matchesConfiguredEntity(entity, parts[0])) {
                    continue;
                }
                try {
                    return clampSeconds(Integer.parseInt(parts[1].trim()));
                } catch (NumberFormatException ignored) {
                    // Invalid matching entry does not hide later valid entries.
                }
            }
        }
        return clampSeconds(ModConfig.petKnockdownTimeoutSeconds);
    }

    private static int clampSeconds(int seconds) {
        return Math.max(0, Math.min(MAX_SECONDS, seconds));
    }

    private static int clampRemainingTicks(long ticks) {
        return (int) Math.max(0L, Math.min((long) Integer.MAX_VALUE, ticks));
    }

    private static void clearServerTimer(EntityLivingBase entity) {
        NBTTagCompound data = entity.getEntityData();
        boolean active = data.getBoolean(NBT_ACTIVE);
        data.removeTag(NBT_ACTIVE);
        data.removeTag(NBT_DEADLINE);
        data.removeTag(NBT_LAST_SYNC_TICK);
        if (active) {
            syncAround(entity, -1);
        }
    }

    private static void syncAround(EntityLivingBase entity, int remainingTicks) {
        if (RFNetwork.CH == null || entity.worldObj == null || entity.worldObj.isRemote) {
            return;
        }
        RFNetwork.CH.sendToAllAround(
                new MsgSyncPetKnockdownTimeout(entity.getEntityId(), remainingTicks),
                new NetworkRegistry.TargetPoint(
                        entity.dimension,
                        entity.posX,
                        entity.posY,
                        entity.posZ,
                        SYNC_RANGE
                )
        );
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
