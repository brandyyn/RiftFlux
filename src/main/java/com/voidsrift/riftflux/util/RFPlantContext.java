package com.voidsrift.riftflux.util;

/**
 * Simple shared flags / helpers to distinguish between:
 * - bonemeal-driven growth
 * - vanilla world generation (chunk decoration)
 * - normal placement / survival
 */
public class RFPlantContext {
    /** True while our custom bonemeal growth logic is running. */
    public static boolean bonemealPlacementActive = false;

    /**
     * Simple depth counter used to track when vanilla chunk decoration /
     * world-generation foliage placement is running.
     *
     * Using a counter instead of a single boolean makes this safe even if
     * decorate(...) is ever called re-entrantly.
     */
    private static int worldGenDepth = 0;

    public static void pushWorldGen() {
        worldGenDepth++;
    }

    public static void popWorldGen() {
        if (worldGenDepth > 0) {
            worldGenDepth--;
        }
    }

    /** @return true if we are currently inside a world-gen decorate(...) call. */
    public static boolean isWorldGenActive() {
        return worldGenDepth > 0;
    }
}
