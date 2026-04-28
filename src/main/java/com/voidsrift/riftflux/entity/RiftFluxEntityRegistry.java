package com.voidsrift.riftflux.entity;

import cpw.mods.fml.common.registry.EntityRegistry;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.entity.Entity;

public final class RiftFluxEntityRegistry {
    private static final Map<Integer, String> CLAIMED_IDS = new HashMap<Integer, String>();

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
        int id = claimId(entityClass);
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

    private static int claimId(Class<? extends Entity> entityClass) {
        String key = entityClass == null ? "unknown" : entityClass.getName();
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
        return id;
    }
}
