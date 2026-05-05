package com.voidsrift.riftflux.spawning;

import cpw.mods.fml.common.FMLLog;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.world.biome.BiomeGenBase;

public final class SpawnTypeMobCapHandler {
    private static final ConcurrentMap<Class<? extends Entity>, Optional<EnumCreatureType>> CREATURE_TYPE_LOOKUP =
            new ConcurrentHashMap<Class<? extends Entity>, Optional<EnumCreatureType>>();
    private static boolean prewarmed;

    private SpawnTypeMobCapHandler() {
    }

    public static boolean isCreatureType(Entity entity, EnumCreatureType queryType) {
        if (entity == null || queryType == null) {
            return false;
        }
        Optional<EnumCreatureType> creatureType = getCreatureType(entity.getClass());
        return creatureType.orElse(null) == queryType;
    }

    public static synchronized void prewarm() {
        if (prewarmed) {
            return;
        }
        prewarmed = true;
        CREATURE_TYPE_LOOKUP.clear();

        Set<Class<? extends Entity>> entityClasses = collectEntityClasses();
        for (Class<? extends Entity> entityClass : entityClasses) {
            getCreatureType(entityClass);
        }

        FMLLog.info("[RiftFlux] Prewarmed spawn-type mob-cap cache for %d entity classes.", entityClasses.size());
    }

    private static Optional<EnumCreatureType> getCreatureType(Class<? extends Entity> entityClass) {
        Optional<EnumCreatureType> cached = CREATURE_TYPE_LOOKUP.get(entityClass);
        if (cached != null) {
            return cached;
        }

        Optional<EnumCreatureType> computed = Optional.ofNullable(computeCreatureType(entityClass));
        Optional<EnumCreatureType> existing = CREATURE_TYPE_LOOKUP.putIfAbsent(entityClass, computed);
        return existing == null ? computed : existing;
    }

    private static EnumCreatureType computeCreatureType(Class<? extends Entity> entityClass) {
        List<EnumCreatureType> vanillaTypes = computeVanillaCreatureTypes(entityClass);
        if (entityClass.getName().startsWith("net.minecraft.")) {
            return vanillaTypes.isEmpty() ? null : vanillaTypes.get(0);
        }

        EnumCreatureType spawnType = computeSpawnType(entityClass);
        if (spawnType != null) {
            return spawnType;
        }
        return vanillaTypes.isEmpty() ? null : vanillaTypes.get(0);
    }

    private static List<EnumCreatureType> computeVanillaCreatureTypes(Class<? extends Entity> entityClass) {
        List<EnumCreatureType> out = new ArrayList<EnumCreatureType>();
        for (EnumCreatureType creatureType : EnumCreatureType.values()) {
            if (creatureType.getCreatureClass().isAssignableFrom(entityClass)) {
                out.add(creatureType);
            }
        }
        return out;
    }

    private static EnumCreatureType computeSpawnType(Class<? extends Entity> entityClass) {
        EnumCreatureType[] creatureTypes = EnumCreatureType.values();
        int[] counts = new int[creatureTypes.length];
        BiomeGenBase[] biomes = BiomeGenBase.getBiomeGenArray();
        if (biomes == null) {
            return null;
        }

        for (BiomeGenBase biome : biomes) {
            if (biome == null) {
                continue;
            }
            for (EnumCreatureType creatureType : creatureTypes) {
                List spawnList = biome.getSpawnableList(creatureType);
                if (spawnList == null) {
                    continue;
                }
                for (Object entryObj : spawnList) {
                    if (entryObj instanceof BiomeGenBase.SpawnListEntry
                            && ((BiomeGenBase.SpawnListEntry) entryObj).entityClass == entityClass) {
                        counts[creatureType.ordinal()]++;
                        break;
                    }
                }
            }
        }

        int bestIndex = -1;
        int bestCount = 0;
        for (int i = 0; i < counts.length; i++) {
            if (counts[i] > bestCount) {
                bestCount = counts[i];
                bestIndex = i;
            }
        }
        return bestIndex < 0 ? null : creatureTypes[bestIndex];
    }

    @SuppressWarnings("unchecked")
    private static Set<Class<? extends Entity>> collectEntityClasses() {
        Set<Class<? extends Entity>> out = new LinkedHashSet<Class<? extends Entity>>();

        for (Object value : EntityList.stringToClassMapping.values()) {
            if (value instanceof Class && Entity.class.isAssignableFrom((Class<?>) value)) {
                out.add((Class<? extends Entity>) value);
            }
        }

        BiomeGenBase[] biomes = BiomeGenBase.getBiomeGenArray();
        if (biomes == null) {
            return out;
        }

        for (BiomeGenBase biome : biomes) {
            if (biome == null) {
                continue;
            }
            for (EnumCreatureType creatureType : EnumCreatureType.values()) {
                List spawnList = biome.getSpawnableList(creatureType);
                if (spawnList == null) {
                    continue;
                }
                for (Object entryObj : spawnList) {
                    if (!(entryObj instanceof BiomeGenBase.SpawnListEntry)) {
                        continue;
                    }
                    Class<?> entityClass = ((BiomeGenBase.SpawnListEntry) entryObj).entityClass;
                    if (entityClass != null && Entity.class.isAssignableFrom(entityClass)) {
                        out.add((Class<? extends Entity>) entityClass);
                    }
                }
            }
        }

        return out;
    }
}
