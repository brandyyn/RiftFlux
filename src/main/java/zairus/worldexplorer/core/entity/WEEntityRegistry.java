package zairus.worldexplorer.core.entity;

import com.voidsrift.riftflux.riftflux;
import cpw.mods.fml.common.registry.EntityRegistry;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.Entity;

public class WEEntityRegistry {
    private static final int LOCAL_ENTITY_ID_BASE = 620;
    private static final List<Class<? extends Entity>> registeredEntities = new ArrayList<Class<? extends Entity>>();

    public static int getNextEntityId() {
        return LOCAL_ENTITY_ID_BASE + registeredEntities.size();
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
        EntityRegistry.registerModEntity(
                entityClass,
                entityId,
                id,
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
        int id = registerEntity(entityClass, entityId, mod, trackingTime, updateFrecuency, sendVelocityUpdates);
        int globalId = EntityRegistry.findGlobalUniqueEntityId();
        String globalEntityName = entityId;
        if (globalEntityName == null || globalEntityName.trim().isEmpty()) {
            globalEntityName = "riftflux_entity_" + globalId;
        }
        EntityRegistry.registerGlobalEntityID(entityClass, globalEntityName, globalId, eggBackground, eggForeground);
        return id;
    }
}
