package zairus.worldexplorer.core.entity;

import com.voidsrift.riftflux.entity.RiftFluxEntityRegistry;
import com.voidsrift.riftflux.riftflux;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.Entity;

public class WEEntityRegistry {
    private static final List<Class<? extends Entity>> registeredEntities = new ArrayList<Class<? extends Entity>>();

    public static int getNextEntityId() {
        return registeredEntities.size();
    }

    public static int registerEntity(
            Class<? extends Entity> entityClass,
            String entityId,
            Object mod,
            int trackingTime,
            int updateFrecuency,
        boolean sendVelocityUpdates
    ) {
        int id = getNextEntityId();
        registeredEntities.add(entityClass);
        RiftFluxEntityRegistry.registerModEntity(
                entityClass,
                entityId,
                riftflux.instance,
                trackingTime,
                updateFrecuency,
                sendVelocityUpdates
        );
        return id;
    }

    public static int registerEntity(
            Class<? extends Entity> entityClass,
            String entityId,
            Object mod,
            int trackingTime,
            int updateFrecuency,
            boolean sendVelocityUpdates,
            int eggBackground,
            int eggForeground
    ) {
        return registerEntity(entityClass, entityId, mod, trackingTime, updateFrecuency, sendVelocityUpdates);
    }
}
