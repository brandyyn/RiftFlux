package com.voidsrift.riftflux.compat.thaumcraft;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraft.entity.player.EntityPlayerMP;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.playerdata.PacketSyncWarp;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ThaumcraftWarpSyncCompat {
    private static final Logger LOGGER = LogManager.getLogger("RiftFluxThaumcraft");
    private static final long STALE_ENTRY_MS = 30L * 60L * 1000L;
    private static final int MAX_EARLY_WARP_LOG_ENTRIES = 2048;
    private static final ConcurrentHashMap<UUID, Long> PENDING_WARP_SYNC =
            new ConcurrentHashMap<UUID, Long>();
    private static final ConcurrentHashMap<String, Long> LOGGED_EARLY_WARP_RESEARCH =
            new ConcurrentHashMap<String, Long>();

    public static void markPending(EntityPlayerMP player) {
        if (player == null || player.getGameProfile() == null || player.getGameProfile().getId() == null) {
            return;
        }
        long now = System.currentTimeMillis();
        pruneStaleEntries(now);
        PENDING_WARP_SYNC.put(player.getGameProfile().getId(), Long.valueOf(now));
    }

    public static void logEarlyWarpResearch(EntityPlayerMP player, String key, int warp, boolean autoUnlock) {
        if (player == null || key == null || key.isEmpty() || warp <= 0) {
            return;
        }
        long now = System.currentTimeMillis();
        pruneStaleEntries(now);

        String playerKey = player.getGameProfile() != null && player.getGameProfile().getId() != null
                ? player.getGameProfile().getId().toString()
                : player.getCommandSenderName();
        String dedupe = playerKey + "|" + key;
        if (LOGGED_EARLY_WARP_RESEARCH.putIfAbsent(dedupe, Long.valueOf(now)) != null) {
            return;
        }
        trimEarlyWarpLog();

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
        pruneStaleEntries(System.currentTimeMillis());

        EntityPlayerMP player = (EntityPlayerMP) event.player;
        if (player.playerNetServerHandler == null || player.getGameProfile() == null || player.getGameProfile().getId() == null) {
            return;
        }

        UUID id = player.getGameProfile().getId();
        clearLoggedEarlyWarpResearch(player);
        if (PENDING_WARP_SYNC.remove(id) == null) {
            return;
        }

        PacketHandler.INSTANCE.sendTo(new PacketSyncWarp(player, (byte) 0), player);
        PacketHandler.INSTANCE.sendTo(new PacketSyncWarp(player, (byte) 1), player);
        PacketHandler.INSTANCE.sendTo(new PacketSyncWarp(player, (byte) 2), player);
    }

    @SubscribeEvent
    public void onPlayerLoggedOut(PlayerLoggedOutEvent event) {
        if (!(event.player instanceof EntityPlayerMP)) {
            return;
        }
        EntityPlayerMP player = (EntityPlayerMP) event.player;
        if (player.getGameProfile() != null && player.getGameProfile().getId() != null) {
            PENDING_WARP_SYNC.remove(player.getGameProfile().getId());
        }
        clearLoggedEarlyWarpResearch(player);
    }

    private static void clearLoggedEarlyWarpResearch(EntityPlayerMP player) {
        String playerKey = player.getGameProfile() != null && player.getGameProfile().getId() != null
                ? player.getGameProfile().getId().toString()
                : player.getCommandSenderName();
        if (playerKey == null || playerKey.isEmpty()) {
            return;
        }
        String prefix = playerKey + "|";
        String[] logged = LOGGED_EARLY_WARP_RESEARCH.keySet().toArray(new String[LOGGED_EARLY_WARP_RESEARCH.size()]);
        for (String key : logged) {
            if (key != null && key.startsWith(prefix)) {
                LOGGED_EARLY_WARP_RESEARCH.remove(key);
            }
        }
    }

    private static void pruneStaleEntries(long now) {
        long cutoff = now - STALE_ENTRY_MS;
        for (Map.Entry<UUID, Long> entry : PENDING_WARP_SYNC.entrySet()) {
            Long createdAt = entry.getValue();
            if (createdAt == null || createdAt.longValue() < cutoff) {
                PENDING_WARP_SYNC.remove(entry.getKey(), createdAt);
            }
        }
        for (Map.Entry<String, Long> entry : LOGGED_EARLY_WARP_RESEARCH.entrySet()) {
            Long createdAt = entry.getValue();
            if (createdAt == null || createdAt.longValue() < cutoff) {
                LOGGED_EARLY_WARP_RESEARCH.remove(entry.getKey(), createdAt);
            }
        }
        trimEarlyWarpLog();
    }

    private static void trimEarlyWarpLog() {
        int excess = LOGGED_EARLY_WARP_RESEARCH.size() - MAX_EARLY_WARP_LOG_ENTRIES;
        if (excess <= 0) {
            return;
        }
        for (String key : LOGGED_EARLY_WARP_RESEARCH.keySet()) {
            LOGGED_EARLY_WARP_RESEARCH.remove(key);
            excess--;
            if (excess <= 0) {
                return;
            }
        }
    }
}
