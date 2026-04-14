package com.voidsrift.riftflux.vortex.respawn;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.net.MsgSyncRespawnDelay;
import com.voidsrift.riftflux.net.RFNetwork;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.WorldServer;

public final class RespawnDelayHelper {
    public static final String NBT_RESPAWN_UNLOCK_AT = "RiftFluxRespawnUnlockAt";
    private static final String NBT_PENDING_SYNC = "RiftFluxRespawnDelayPendingSync";
    private static final String NBT_PENDING_SYNC_DELAY = "RiftFluxRespawnDelayPendingSyncDelay";
    private static final String NBT_PENDING_RESPAWN_STABILIZE = "RiftFluxRespawnPendingStabilize";
    private static final String NBT_PENDING_RESPAWN_STABILIZE_DELAY = "RiftFluxRespawnPendingStabilizeDelay";
    private static final String NBT_PENDING_RESPAWN_STABILIZE_TICKS = "RiftFluxRespawnPendingStabilizeTicks";
    private static final int RESPAWN_STABILIZE_TICKS = 40;

    private RespawnDelayHelper() {
    }

    public static boolean isEnabled() {
        return ModConfig.deathRespawnDelaySeconds > 0;
    }

    public static long getConfiguredDelayMs() {
        return Math.max(0, ModConfig.deathRespawnDelaySeconds) * 1000L;
    }

    public static void startDeathTimer(EntityPlayer player) {
        if (player == null) {
            return;
        }
        if (!isEnabled()) {
            clearDeathTimer(player);
            return;
        }

        long nowMs = System.currentTimeMillis();
        long unlockAtMs = getUnlockAtMs(player);
        if (unlockAtMs > nowMs) {
            return;
        }
        setUnlockAtMs(player, nowMs + getConfiguredDelayMs());
    }

    public static void clearDeathTimer(EntityPlayer player) {
        setUnlockAtMs(player, 0L);
        clearPendingSync(player);
        if (player != null) {
            clearPendingRespawnStabilize(player.getEntityData());
        }
    }

    public static boolean shouldBlockRespawn(EntityPlayer player) {
        return getRemainingMs(player) > 0L;
    }

    public static long getRemainingMs(EntityPlayer player) {
        if (player == null || !isEnabled()) {
            return 0L;
        }

        long unlockAtMs = getUnlockAtMs(player);
        if (unlockAtMs <= 0L) {
            return 0L;
        }

        long remainingMs = unlockAtMs - System.currentTimeMillis();
        if (remainingMs <= 0L) {
            clearDeathTimer(player);
            return 0L;
        }
        return remainingMs;
    }

    public static void sync(EntityPlayer player) {
        if (!(player instanceof EntityPlayerMP) || RFNetwork.CH == null) {
            return;
        }
        RFNetwork.CH.sendTo(new MsgSyncRespawnDelay(getRemainingMs(player)), (EntityPlayerMP) player);
    }

    public static void markPendingSync(EntityPlayer player, int delayTicks) {
        if (!(player instanceof EntityPlayerMP)) {
            return;
        }
        NBTTagCompound data = player.getEntityData();
        data.setBoolean(NBT_PENDING_SYNC, true);
        data.setInteger(NBT_PENDING_SYNC_DELAY, Math.max(0, delayTicks));
    }

    public static void tickPendingSync(TickEvent.PlayerTickEvent event) {
        if (event == null || event.phase != TickEvent.Phase.END) {
            return;
        }
        EntityPlayer player = event.player;
        if (!(player instanceof EntityPlayerMP) || player.worldObj == null || player.worldObj.isRemote || RFNetwork.CH == null) {
            return;
        }

        NBTTagCompound data = player.getEntityData();
        if (!data.getBoolean(NBT_PENDING_SYNC)) {
            return;
        }

        int delay = data.getInteger(NBT_PENDING_SYNC_DELAY);
        if (delay > 0) {
            data.setInteger(NBT_PENDING_SYNC_DELAY, delay - 1);
            return;
        }

        sync(player);
        clearPendingSync(player);
    }

    public static void markPendingRespawnStabilize(EntityPlayer player, int delayTicks) {
        markPendingRespawnStabilize(player, delayTicks, RESPAWN_STABILIZE_TICKS);
    }

    public static void markPendingRespawnStabilize(EntityPlayer player, int delayTicks, int stabilizeTicks) {
        if (!(player instanceof EntityPlayerMP)) {
            return;
        }
        NBTTagCompound data = player.getEntityData();
        data.setBoolean(NBT_PENDING_RESPAWN_STABILIZE, true);
        data.setInteger(NBT_PENDING_RESPAWN_STABILIZE_DELAY, Math.max(0, delayTicks));
        data.setInteger(NBT_PENDING_RESPAWN_STABILIZE_TICKS, Math.max(1, stabilizeTicks));
    }

    public static void tickPendingRespawnStabilize(TickEvent.PlayerTickEvent event) {
        if (event == null || event.phase != TickEvent.Phase.END) {
            return;
        }
        EntityPlayer player = event.player;
        if (!(player instanceof EntityPlayerMP) || player.worldObj == null || player.worldObj.isRemote) {
            return;
        }

        NBTTagCompound data = player.getEntityData();
        if (!data.getBoolean(NBT_PENDING_RESPAWN_STABILIZE)) {
            return;
        }

        int delay = data.getInteger(NBT_PENDING_RESPAWN_STABILIZE_DELAY);
        if (delay > 0) {
            data.setInteger(NBT_PENDING_RESPAWN_STABILIZE_DELAY, delay - 1);
            return;
        }

        int remainingTicks = data.getInteger(NBT_PENDING_RESPAWN_STABILIZE_TICKS);
        if (remainingTicks <= 0) {
            clearPendingRespawnStabilize(data);
            return;
        }

        stabilizeRespawn((EntityPlayerMP) player);
        if (remainingTicks <= 1) {
            clearPendingRespawnStabilize(data);
        } else {
            data.setInteger(NBT_PENDING_RESPAWN_STABILIZE_TICKS, remainingTicks - 1);
        }
    }

    private static void clearPendingSync(EntityPlayer player) {
        if (player == null) {
            return;
        }
        NBTTagCompound data = player.getEntityData();
        data.removeTag(NBT_PENDING_SYNC);
        data.removeTag(NBT_PENDING_SYNC_DELAY);
    }

    private static void clearPendingRespawnStabilize(NBTTagCompound data) {
        if (data == null) {
            return;
        }
        data.removeTag(NBT_PENDING_RESPAWN_STABILIZE);
        data.removeTag(NBT_PENDING_RESPAWN_STABILIZE_DELAY);
        data.removeTag(NBT_PENDING_RESPAWN_STABILIZE_TICKS);
    }

    private static void stabilizeRespawn(EntityPlayerMP player) {
        if (player == null || player.playerNetServerHandler == null) {
            return;
        }
        WorldServer world = player.getServerForPlayer();
        if (world == null) {
            return;
        }

        int chunkX = MathHelper.floor_double(player.posX) >> 4;
        int chunkZ = MathHelper.floor_double(player.posZ) >> 4;
        for (int offsetX = -2; offsetX <= 2; ++offsetX) {
            for (int offsetZ = -2; offsetZ <= 2; ++offsetZ) {
                world.theChunkProviderServer.loadChunk(chunkX + offsetX, chunkZ + offsetZ);
            }
        }
        world.getPlayerManager().updatePlayerPertinentChunks(player);

        player.motionX = 0.0D;
        player.motionY = 0.0D;
        player.motionZ = 0.0D;
        player.fallDistance = 0.0F;
        player.velocityChanged = true;
        player.playerNetServerHandler.setPlayerLocation(
                player.posX,
                player.posY,
                player.posZ,
                player.rotationYaw,
                player.rotationPitch
        );
    }

    private static long getUnlockAtMs(EntityPlayer player) {
        if (player == null) {
            return 0L;
        }

        NBTTagCompound data = player.getEntityData();
        if (data.hasKey(NBT_RESPAWN_UNLOCK_AT)) {
            return Math.max(0L, data.getLong(NBT_RESPAWN_UNLOCK_AT));
        }

        NBTTagCompound persisted = getPersisted(player, false);
        if (persisted == null || !persisted.hasKey(NBT_RESPAWN_UNLOCK_AT)) {
            return 0L;
        }

        long unlockAtMs = Math.max(0L, persisted.getLong(NBT_RESPAWN_UNLOCK_AT));
        if (unlockAtMs > 0L) {
            data.setLong(NBT_RESPAWN_UNLOCK_AT, unlockAtMs);
        }
        return unlockAtMs;
    }

    private static void setUnlockAtMs(EntityPlayer player, long unlockAtMs) {
        if (player == null) {
            return;
        }

        NBTTagCompound data = player.getEntityData();
        NBTTagCompound persisted = getPersisted(player, unlockAtMs > 0L);
        if (unlockAtMs > 0L) {
            data.setLong(NBT_RESPAWN_UNLOCK_AT, unlockAtMs);
            if (persisted != null) {
                persisted.setLong(NBT_RESPAWN_UNLOCK_AT, unlockAtMs);
            }
            return;
        }

        data.removeTag(NBT_RESPAWN_UNLOCK_AT);
        if (persisted != null) {
            persisted.removeTag(NBT_RESPAWN_UNLOCK_AT);
        }
    }

    private static NBTTagCompound getPersisted(EntityPlayer player, boolean create) {
        if (player == null) {
            return null;
        }

        NBTTagCompound data = player.getEntityData();
        if (!data.hasKey(EntityPlayer.PERSISTED_NBT_TAG)) {
            if (!create) {
                return null;
            }
            data.setTag(EntityPlayer.PERSISTED_NBT_TAG, new NBTTagCompound());
        }
        return data.getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
    }
}
