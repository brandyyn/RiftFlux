package com.voidsrift.riftflux.net.sync;

import com.voidsrift.riftflux.net.MsgSyncPlayerData;
import com.voidsrift.riftflux.net.RFNetwork;
import cpw.mods.fml.common.network.NetworkRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;

public final class PlayerSyncHelper {
    private static final double DEFAULT_SYNC_RANGE = 160.0D;

    private PlayerSyncHelper() {
    }

    public static void sync(EntityPlayer player, IPlayerSyncData data) {
        if (!(player instanceof EntityPlayerMP) || data == null || RFNetwork.CH == null) {
            return;
        }

        EntityPlayerMP serverPlayer = (EntityPlayerMP) player;
        if (!isReadyForSync(serverPlayer)) {
            return;
        }

        NBTTagCompound tag = new NBTTagCompound();
        data.rf$writeSyncData(tag);
        MsgSyncPlayerData message = new MsgSyncPlayerData(serverPlayer.getEntityId(), data.rf$getSyncKey(), tag);
        RFNetwork.CH.sendTo(message, serverPlayer);
        RFNetwork.CH.sendToAllAround(
                message,
                new NetworkRegistry.TargetPoint(serverPlayer.dimension, serverPlayer.posX, serverPlayer.posY, serverPlayer.posZ, DEFAULT_SYNC_RANGE)
        );
    }

    public static void syncTo(EntityPlayer player, IPlayerSyncData data, EntityPlayerMP target) {
        if (player == null || data == null || target == null || RFNetwork.CH == null) {
            return;
        }
        NBTTagCompound tag = new NBTTagCompound();
        data.rf$writeSyncData(tag);
        RFNetwork.CH.sendTo(new MsgSyncPlayerData(player.getEntityId(), data.rf$getSyncKey(), tag), target);
    }

    private static boolean isReadyForSync(EntityPlayerMP player) {
        return player.worldObj != null
                && !player.worldObj.isRemote
                && player.playerNetServerHandler != null;
    }
}
