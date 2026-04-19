package com.voidsrift.riftflux.compat.thaumcraft;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraft.entity.player.EntityPlayerMP;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.playerdata.PacketSyncWarp;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ThaumcraftWarpSyncCompat {
    private static final Logger LOGGER = LogManager.getLogger("RiftFluxThaumcraft");
    private static final Set<UUID> PENDING_WARP_SYNC =
            Collections.newSetFromMap(new ConcurrentHashMap<UUID, Boolean>());
    private static final Set<String> LOGGED_EARLY_WARP_RESEARCH =
            Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());

    public static void markPending(EntityPlayerMP player) {
        if (player == null || player.getGameProfile() == null || player.getGameProfile().getId() == null) {
            return;
        }
        PENDING_WARP_SYNC.add(player.getGameProfile().getId());
    }

    public static void logEarlyWarpResearch(EntityPlayerMP player, String key, int warp, boolean autoUnlock) {
        if (player == null || key == null || key.isEmpty() || warp <= 0) {
            return;
        }

        String playerKey = player.getGameProfile() != null && player.getGameProfile().getId() != null
                ? player.getGameProfile().getId().toString()
                : player.getCommandSenderName();
        String dedupe = playerKey + "|" + key;
        if (!LOGGED_EARLY_WARP_RESEARCH.add(dedupe)) {
            return;
        }

        LOGGER.warn(
                "Thaumcraft completed warp research '{}' for player '{}' before playerNetServerHandler existed (warp={}, autoUnlock={}). RiftFlux will defer the sync packet, but another research registration is unlocking too early.",
                key,
                player.getCommandSenderName(),
                warp,
                autoUnlock
        );
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
        clearLoggedEarlyWarpResearch(player);
        if (!PENDING_WARP_SYNC.remove(id)) {
            return;
        }

        PacketHandler.INSTANCE.sendTo(new PacketSyncWarp(player, (byte) 0), player);
        PacketHandler.INSTANCE.sendTo(new PacketSyncWarp(player, (byte) 1), player);
        PacketHandler.INSTANCE.sendTo(new PacketSyncWarp(player, (byte) 2), player);
    }

    private static void clearLoggedEarlyWarpResearch(EntityPlayerMP player) {
        String playerKey = player.getGameProfile() != null && player.getGameProfile().getId() != null
                ? player.getGameProfile().getId().toString()
                : player.getCommandSenderName();
        if (playerKey == null || playerKey.isEmpty()) {
            return;
        }
        String prefix = playerKey + "|";
        String[] logged = LOGGED_EARLY_WARP_RESEARCH.toArray(new String[LOGGED_EARLY_WARP_RESEARCH.size()]);
        for (String key : logged) {
            if (key != null && key.startsWith(prefix)) {
                LOGGED_EARLY_WARP_RESEARCH.remove(key);
            }
        }
    }
}
