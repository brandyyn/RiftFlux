package com.voidsrift.riftflux.client;

import net.minecraft.client.Minecraft;

import java.io.File;

public final class ClientRespawnDelayState {
    private static final String[] LEGACY_TIMER_PATHS = new String[] {
            "riftflux_respawn_timers.dat",
            "config/riftflux_respawn_timers.properties",
            "config/riftflux_respawn_timers.cfg"
    };

    private static long clientUnlockAtMs;
    private static boolean legacyFilesCleaned;

    private ClientRespawnDelayState() {
    }

    public static void applyRemainingMs(long remainingMs) {
        long clampedRemainingMs = Math.max(0L, remainingMs);
        clientUnlockAtMs = clampedRemainingMs <= 0L ? 0L : System.currentTimeMillis() + clampedRemainingMs;
    }

    public static long getRemainingMs() {
        if (clientUnlockAtMs <= 0L) {
            return 0L;
        }
        long remainingMs = clientUnlockAtMs - System.currentTimeMillis();
        if (remainingMs <= 0L) {
            clientUnlockAtMs = 0L;
            return 0L;
        }
        return remainingMs;
    }

    public static void clear() {
        clientUnlockAtMs = 0L;
    }

    public static void cleanupLegacyFiles() {
        if (legacyFilesCleaned) {
            return;
        }
        legacyFilesCleaned = true;

        Minecraft mc = Minecraft.getMinecraft();
        File root = mc != null && mc.mcDataDir != null ? mc.mcDataDir : new File(".");
        for (String relativePath : LEGACY_TIMER_PATHS) {
            File file = new File(root, relativePath);
            if (file.isFile()) {
                file.delete();
            }
        }
    }
}
