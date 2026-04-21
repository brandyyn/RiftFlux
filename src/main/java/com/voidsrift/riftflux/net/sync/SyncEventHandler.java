package com.voidsrift.riftflux.net.sync;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import inurosen.healaltar.common.entity.ExtendedPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.entity.player.PlayerEvent.Clone;

public class SyncEventHandler {
    @SubscribeEvent
    public void onStartTracking(net.minecraftforge.event.entity.player.PlayerEvent.StartTracking event) {
        if (event == null || !(event.entityPlayer instanceof EntityPlayerMP) || event.target == null) {
            return;
        }

        EntityPlayerMP trackingPlayer = (EntityPlayerMP) event.entityPlayer;
        EntitySyncHelper.syncTo(event.target, trackingPlayer);

        if (event.target instanceof EntityPlayer) {
            syncPlayerStateTo((EntityPlayer) event.target, trackingPlayer);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onPlayerClone(Clone event) {
        if (event == null || !(event.entityPlayer instanceof EntityPlayerMP)) {
            return;
        }
        syncOwnPlayerState((EntityPlayerMP) event.entityPlayer);
    }

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event == null || !(event.player instanceof EntityPlayerMP)) {
            return;
        }
        syncOwnPlayerState((EntityPlayerMP) event.player);
    }

    @SubscribeEvent
    public void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event == null || !(event.player instanceof EntityPlayerMP)) {
            return;
        }
        syncOwnPlayerState((EntityPlayerMP) event.player);
    }

    @SubscribeEvent
    public void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event == null || !(event.player instanceof EntityPlayerMP)) {
            return;
        }
        syncOwnPlayerState((EntityPlayerMP) event.player);
    }

    private static void syncOwnPlayerState(EntityPlayerMP player) {
        syncPlayerStateTo(player, player);
    }

    private static void syncPlayerStateTo(EntityPlayer player, EntityPlayerMP target) {
        if (player == null || target == null) {
            return;
        }

        Object legendGearProps = player.getExtendedProperties(net.nmccoy.legendgear.PlayerStarstatsExtension.EXT_PROP_NAME);
        if (legendGearProps instanceof IPlayerSyncData) {
            PlayerSyncHelper.syncTo(player, (IPlayerSyncData) legendGearProps, target);
        }

        Object soulHeartProps = player.getExtendedProperties(ExtendedPlayer.EXT_PROP_NAME);
        if (soulHeartProps instanceof IPlayerSyncData) {
            PlayerSyncHelper.syncTo(player, (IPlayerSyncData) soulHeartProps, target);
        }
    }
}
