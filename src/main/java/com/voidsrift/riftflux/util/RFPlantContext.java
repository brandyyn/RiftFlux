package com.voidsrift.riftflux.util;

import com.voidsrift.riftflux.net.MsgSyncCrossedPlantFacing;
import com.voidsrift.riftflux.net.RFNetwork;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import java.util.Collections;
import java.util.HashMap;
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
    private static final Map<World, Map<Long, Byte>> CROSSED_PLANT_FACING =
            new WeakHashMap<World, Map<Long, Byte>>();

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

    /**
     * Marks placed crossed-plant facing from player yaw (0-3).
     */
    public static void markCrossedPlantFacingFromPlacer(World world, int x, int y, int z, EntityLivingBase placer) {
        if (placer == null) return;
        // For crossed plants we store a direct "face the player" cardinal:
        // 0=north, 1=east, 2=south, 3=west.
        int facing = MathHelper.floor_double((double) (placer.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
        markCrossedPlantFacing(world, x, y, z, facing);
    }

    /**
     * Stores crossed-plant facing at this position (0-3).
     */
    public static void markCrossedPlantFacing(World world, int x, int y, int z, int facing) {
        if (world == null) return;
        long key = pack(x, y, z);
        byte value = (byte) (facing & 3);
        synchronized (CROSSED_PLANT_FACING) {
            Map<Long, Byte> map = CROSSED_PLANT_FACING.get(world);
            if (map == null) {
                map = Collections.synchronizedMap(new HashMap<Long, Byte>());
                CROSSED_PLANT_FACING.put(world, map);
            }
            map.put(key, value);
        }
        if (!world.isRemote && world.provider != null && RFNetwork.CH != null) {
            RFNetwork.CH.sendToDimension(
                    new MsgSyncCrossedPlantFacing(world.provider.dimensionId, x, y, z, value & 3),
                    world.provider.dimensionId
            );
        }
    }

    /**
     * Returns placed crossed-plant facing (0-3), or fallback when unknown.
     */
    public static int getCrossedPlantFacing(World world, int x, int y, int z, int fallback) {
        if (world == null) return fallback & 3;
        long key = pack(x, y, z);
        synchronized (CROSSED_PLANT_FACING) {
            Map<Long, Byte> map = CROSSED_PLANT_FACING.get(world);
            if (map == null) {
                return fallback & 3;
            }
            Byte value = map.get(key);
            return value == null ? (fallback & 3) : (value.intValue() & 3);
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
        long key = pack(x, y, z);
        synchronized (PLAYER_PLACED) {
            Set<Long> set = PLAYER_PLACED.get(world);
            if (set != null) {
                set.remove(key);
                if (set.isEmpty()) {
                    PLAYER_PLACED.remove(world);
                }
            }
        }
        synchronized (CROSSED_PLANT_FACING) {
            Map<Long, Byte> map = CROSSED_PLANT_FACING.get(world);
            if (map != null) {
                map.remove(key);
                if (map.isEmpty()) {
                    CROSSED_PLANT_FACING.remove(world);
                }
            }
        }
    }

    public static void clearWorld(World world) {
        if (world == null) return;
        synchronized (PLAYER_PLACED) {
            PLAYER_PLACED.remove(world);
        }
        synchronized (CROSSED_PLANT_FACING) {
            CROSSED_PLANT_FACING.remove(world);
        }
    }
}
