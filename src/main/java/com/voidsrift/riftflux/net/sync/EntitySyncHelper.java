package com.voidsrift.riftflux.net.sync;

import com.voidsrift.riftflux.net.MsgSyncEntityData;
import com.voidsrift.riftflux.net.RFNetwork;
import cpw.mods.fml.common.network.NetworkRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;

public final class EntitySyncHelper {
    private static final double DEFAULT_SYNC_RANGE = 160.0D;

    private EntitySyncHelper() {
    }

    public static void sync(Entity entity) {
        if (!(entity instanceof IEntitySyncData) || !isReadyForSync(entity) || RFNetwork.CH == null) {
            return;
        }

        NBTTagCompound tag = new NBTTagCompound();
        ((IEntitySyncData) entity).rf$writeSyncData(tag);
        RFNetwork.CH.sendToAllAround(
                new MsgSyncEntityData(entity.getEntityId(), tag),
                new NetworkRegistry.TargetPoint(entity.dimension, entity.posX, entity.posY, entity.posZ, DEFAULT_SYNC_RANGE)
        );
    }

    public static void syncTo(Entity entity, EntityPlayerMP player) {
        if (!(entity instanceof IEntitySyncData) || !isReadyForSync(entity) || player == null || RFNetwork.CH == null) {
            return;
        }

        NBTTagCompound tag = new NBTTagCompound();
        ((IEntitySyncData) entity).rf$writeSyncData(tag);
        RFNetwork.CH.sendTo(new MsgSyncEntityData(entity.getEntityId(), tag), player);
    }

    private static boolean isReadyForSync(Entity entity) {
        return entity != null
                && entity.worldObj != null
                && !entity.worldObj.isRemote
                && entity.worldObj.getEntityByID(entity.getEntityId()) == entity;
    }
}
