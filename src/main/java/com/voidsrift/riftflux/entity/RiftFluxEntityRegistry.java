package com.voidsrift.riftflux.entity;

import cpw.mods.fml.common.registry.EntityRegistry;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import net.minecraft.entity.Entity;

public final class RiftFluxEntityRegistry {
    private static final Map<Integer, String> CLAIMED_IDS = new HashMap<Integer, String>();
    private static final Map<String, String> CLAIMED_NAMES = new HashMap<String, String>();
    private static final Map<String, Integer> CLAIMED_CLASSES = new HashMap<String, Integer>();

    private RiftFluxEntityRegistry() {
    }

    public static synchronized int registerModEntity(
            Class<? extends Entity> entityClass,
            String entityName,
            Object owner,
            int trackingRange,
            int updateFrequency,
            boolean sendVelocityUpdates
    ) {
        int id = claimId(entityClass, entityName);
        EntityRegistry.registerModEntity(
                entityClass,
                entityName,
                id,
                owner,
                trackingRange,
                updateFrequency,
                sendVelocityUpdates
        );
        return id;
    }

    private static int claimId(Class<? extends Entity> entityClass, String entityName) {
        String key = entityClass == null ? "unknown" : entityClass.getName();
        if (CLAIMED_CLASSES.containsKey(key)) {
            throw new IllegalStateException(
                    "RiftFlux entity class '" + key + "' was registered more than once (existing id "
                            + CLAIMED_CLASSES.get(key) + ")"
            );
        }

        String normalizedName = entityName == null ? "" : entityName.trim().toLowerCase(Locale.ROOT);
        if (normalizedName.isEmpty()) {
            throw new IllegalArgumentException("RiftFlux entity name must not be empty for class '" + key + "'");
        }
        String namedClass = CLAIMED_NAMES.get(normalizedName);
        if (namedClass != null) {
            throw new IllegalStateException(
                    "RiftFlux entity name collision for '" + entityName + "' between '" + namedClass + "' and '" + key + "'"
            );
        }

        int id = key.hashCode() & Integer.MAX_VALUE;
        if (id == 0) {
            id = 1;
        }
        String existing = CLAIMED_IDS.get(id);
        if (existing != null && !existing.equals(key)) {
            throw new IllegalStateException(
                    "Stable RiftFlux mod entity ID collision between '" + existing + "' and '" + key + "' (" + id + ")"
            );
        }
        CLAIMED_IDS.put(id, key);
        CLAIMED_NAMES.put(normalizedName, key);
        CLAIMED_CLASSES.put(key, id);
        return id;
    }
}
