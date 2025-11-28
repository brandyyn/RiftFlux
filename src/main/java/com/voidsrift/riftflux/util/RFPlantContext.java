package com.voidsrift.riftflux.util;

import net.minecraft.world.World;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

/**
 * Shared flags / helpers for plant- and foliage-related tweaks.
 */
public class RFPlantContext {

    // ------------------------------------------------------------------------
    // Optional: worldgen / bonemeal flags (if you already use these elsewhere)
    // ------------------------------------------------------------------------

    public static boolean bonemealPlacementActive = false;

    private static int worldGenDepth = 0;

    public static void pushWorldGen() {
        worldGenDepth++;
    }

    public static void popWorldGen() {
        if (worldGenDepth > 0) {
            worldGenDepth--;
        }
    }

    public static boolean isWorldGenActive() {
        return worldGenDepth > 0;
    }

    // ------------------------------------------------------------------------
    // Player-placed bush tracking (for survival checks)
    // ------------------------------------------------------------------------

    private static final Map<World, Set<Long>> PLAYER_PLACED =
            new WeakHashMap<World, Set<Long>>();

    private static long pack(int x, int y, int z) {
        long lx = (long) x & 0x3FFFFFFL; // 26 bits
        long ly = (long) y & 0xFFFL;     // 12 bits
        long lz = (long) z & 0x3FFFFFFL; // 26 bits
        return (lx << 38) | (ly << 26) | lz;
    }

    /** Mark a bush at the given position as being placed by a player. */
    public static void markPlayerPlaced(World world, int x, int y, int z) {
        if (world == null) return;
        synchronized (PLAYER_PLACED) {
            Set<Long> set = PLAYER_PLACED.get(world);
            if (set == null) {
                set = Collections.synchronizedSet(new HashSet<Long>());
                PLAYER_PLACED.put(world, set);
            }
            set.add(pack(x, y, z));
        }
    }

    /** True if this position was previously marked as player-placed. */
    public static boolean isPlayerPlaced(World world, int x, int y, int z) {
        if (world == null) return false;
        synchronized (PLAYER_PLACED) {
            Set<Long> set = PLAYER_PLACED.get(world);
            return set != null && set.contains(pack(x, y, z));
        }
    }

    /** Optional cleanup when a bush is removed; purely to limit map size. */
    public static void clearPlayerPlaced(World world, int x, int y, int z) {
        if (world == null) return;
        synchronized (PLAYER_PLACED) {
            Set<Long> set = PLAYER_PLACED.get(world);
            if (set != null) {
                set.remove(pack(x, y, z));
            }
        }
    }
}
