package com.voidsrift.riftflux.compat.thaumcraft;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraft.entity.player.EntityPlayerMP;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.playerdata.PacketSyncWarp;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ThaumcraftWarpSyncCompat {
    private static final Set<UUID> PENDING_WARP_SYNC =
            Collections.newSetFromMap(new ConcurrentHashMap<UUID, Boolean>());

    public static void markPending(EntityPlayerMP player) {
        if (player == null || player.getGameProfile() == null || player.getGameProfile().getId() == null) {
            return;
        }
        PENDING_WARP_SYNC.add(player.getGameProfile().getId());
    }

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerLoggedInEvent event) {
        if (!(event.player instanceof EntityPlayerMP)) {
            return;
        }

        EntityPlayerMP player = (EntityPlayerMP) event.player;
        if (player.playerNetServerHandler == null || player.getGameProfile() == null || player.getGameProfile().getId() == null) {
            return;
        }

        UUID id = player.getGameProfile().getId();
        if (!PENDING_WARP_SYNC.remove(id)) {
            return;
        }

        PacketHandler.INSTANCE.sendTo(new PacketSyncWarp(player, (byte) 0), player);
        PacketHandler.INSTANCE.sendTo(new PacketSyncWarp(player, (byte) 1), player);
        PacketHandler.INSTANCE.sendTo(new PacketSyncWarp(player, (byte) 2), player);
    }
}
