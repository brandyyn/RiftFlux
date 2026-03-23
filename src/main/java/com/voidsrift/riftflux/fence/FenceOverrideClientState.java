package com.voidsrift.riftflux.fence;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class FenceOverrideClientState {
    private static final Map<Integer, Set<String>> OVERRIDES = new HashMap<Integer, Set<String>>();

    private FenceOverrideClientState() {
    }

    public static synchronized void clearAll() {
        OVERRIDES.clear();
    }

    public static synchronized void applySync(boolean fullSync, int dim, boolean enabled, int[] coordinates) {
        if (fullSync) {
            Set<String> entries = new HashSet<String>();
            addCoordinates(entries, coordinates);
            if (entries.isEmpty()) {
                OVERRIDES.remove(Integer.valueOf(dim));
            } else {
                OVERRIDES.put(Integer.valueOf(dim), entries);
            }
            return;
        }

        if (coordinates == null || coordinates.length < 3) {
            return;
        }

        Integer key = Integer.valueOf(dim);
        Set<String> entries = OVERRIDES.get(key);
        if (enabled) {
            if (entries == null) {
                entries = new HashSet<String>();
                OVERRIDES.put(key, entries);
            }
            entries.add(key(coordinates[0], coordinates[1], coordinates[2]));
        } else if (entries != null) {
            entries.remove(key(coordinates[0], coordinates[1], coordinates[2]));
            if (entries.isEmpty()) {
                OVERRIDES.remove(key);
            }
        }
    }

    public static synchronized boolean isOriginalFence(int dim, int x, int y, int z) {
        Set<String> entries = OVERRIDES.get(Integer.valueOf(dim));
        return entries != null && entries.contains(key(x, y, z));
    }

    private static void addCoordinates(Set<String> entries, int[] coordinates) {
        if (entries == null || coordinates == null) {
            return;
        }
        for (int i = 0; i + 2 < coordinates.length; i += 3) {
            entries.add(key(coordinates[i], coordinates[i + 1], coordinates[i + 2]));
        }
    }

    private static String key(int x, int y, int z) {
        return x + ":" + y + ":" + z;
    }
}
