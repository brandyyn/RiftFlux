package com.voidsrift.riftflux.riftexplorer;

import com.voidsrift.riftflux.net.MsgSyncRiftChestRandomMobState;
import com.voidsrift.riftflux.net.RFNetwork;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;

public class RiftChestRandomMobStateTracker {
    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinWorldEvent event) {
        if (event == null || event.world == null || event.world.isRemote || event.entity == null) {
            return;
        }
        RiftChestRandomMobStateHelper.ensureStoredState(event.entity);
    }

    @SubscribeEvent
    public void onStartTracking(PlayerEvent.StartTracking event) {
        if (event == null || !(event.entityPlayer instanceof EntityPlayerMP) || event.target == null) {
            return;
        }
        syncState((EntityPlayerMP) event.entityPlayer, event.target);
    }

    private static void syncState(EntityPlayerMP player, Entity entity) {
        if (player == null || entity == null || RFNetwork.CH == null || !RiftChestRandomMobStateHelper.hasStoredState(entity)) {
            return;
        }
        RFNetwork.CH.sendTo(new MsgSyncRiftChestRandomMobState(
                entity.getEntityId(),
                RiftChestRandomMobStateHelper.getInitialX(entity),
                RiftChestRandomMobStateHelper.getInitialY(entity),
                RiftChestRandomMobStateHelper.getInitialZ(entity),
                RiftChestRandomMobStateHelper.getBiomeId(entity),
                RiftChestRandomMobStateHelper.getRandomSeed(entity)
        ), player);
    }
}
