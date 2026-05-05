package com.voidsrift.riftflux.spawning;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.util.ConfigResolver;
import com.voidsrift.riftflux.wheatfield.WheatfieldContent;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.EntityRegistry;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityAmbientCreature;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;

public final class ConfiguredMobSpawns {
    private static final Map<Class<? extends EntityLiving>, RuleSet> ACTIVE_RULES =
            new HashMap<Class<? extends EntityLiving>, RuleSet>();
    private static final BitSet WHITELIST_ONLY_BIOME_IDS = new BitSet();
    private static boolean initialized;

    private ConfiguredMobSpawns() {
    }

    public static void init() {
        if (initialized) {
            return;
        }
        initialized = true;

        List<SpawnRule> whitelist = parseRules(ModConfig.mobSpawnWhitelist, true);
        List<SpawnRule> blacklist = parseRules(ModConfig.mobSpawnBlacklist, false);
        setWhitelistOnlyBiomes(parseWhitelistOnlyBiomes(ModConfig.mobSpawnWhitelistOnlyBiomes));

        for (SpawnRule rule : whitelist) {
            Class<? extends EntityLiving> entityClass = resolveLivingClass(rule.mobName);
            if (entityClass == null) {
                FMLLog.warning("[RiftFlux] Ignoring mob spawn whitelist entry with unknown living entity '%s'.", rule.mobName);
                continue;
            }
            if (rule.weight <= 0) {
                continue;
            }

            RuleSet ruleSet = ACTIVE_RULES.get(entityClass);
            if (ruleSet == null) {
                ruleSet = new RuleSet(entityClass, inferCreatureType(entityClass, rule.mobName));
                ACTIVE_RULES.put(entityClass, ruleSet);
            }
            ruleSet.whitelist.add(rule);
        }

        for (SpawnRule rule : blacklist) {
            Class<? extends EntityLiving> entityClass = resolveLivingClass(rule.mobName);
            if (entityClass == null) {
                FMLLog.warning("[RiftFlux] Ignoring mob spawn blacklist entry with unknown living entity '%s'.", rule.mobName);
                continue;
            }

            RuleSet ruleSet = ACTIVE_RULES.get(entityClass);
            if (ruleSet != null) {
                ruleSet.blacklist.add(rule);
            }
        }

        for (RuleSet ruleSet : ACTIVE_RULES.values()) {
            removeExistingSpawns(ruleSet.entityClass);
            registerWhitelistSpawns(ruleSet);
        }
        removeUnwhitelistedSpawnsFromWhitelistOnlyBiomes();

        MinecraftForge.EVENT_BUS.register(new ConfiguredMobSpawns());
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onCheckSpawn(LivingSpawnEvent.CheckSpawn event) {
        if (event == null
                || event.entityLiving == null
                || event.entityLiving.worldObj == null
                || event.entityLiving.worldObj.isRemote) {
            return;
        }

        World world = event.entityLiving.worldObj;
        int x = MathHelper.floor_double(event.x);
        int y = MathHelper.floor_double(event.entityLiving.boundingBox.minY);
        int z = MathHelper.floor_double(event.z);
        if (isHostileMob(event.entityLiving) && isBrightDaylightSurface(world, x, y, z)) {
            event.setResult(Event.Result.DENY);
            return;
        }

        RuleSet ruleSet = ACTIVE_RULES.get(event.entityLiving.getClass());
        if (ruleSet == null && WHITELIST_ONLY_BIOME_IDS.isEmpty()) {
            return;
        }

        BiomeGenBase biome = world.getBiomeGenForCoords(x, z);
        int dimension = world.provider == null ? 0 : world.provider.dimensionId;
        boolean whitelistOnlyBiome = biome != null && isWhitelistOnlyBiome(biome.biomeID);
        if (ruleSet == null) {
            if (whitelistOnlyBiome) {
                event.setResult(Event.Result.DENY);
            }
            return;
        }
        if (!ruleSet.allows(dimension, biome)) {
            event.setResult(Event.Result.DENY);
        }
    }

    private static void registerWhitelistSpawns(RuleSet ruleSet) {
        for (SpawnRule rule : ruleSet.whitelist) {
            Map<Integer, List<BiomeGenBase>> biomesByWeight = collectRegistrationBiomesByWeight(rule);
            for (Map.Entry<Integer, List<BiomeGenBase>> entry : biomesByWeight.entrySet()) {
                if (entry.getKey().intValue() <= 0 || entry.getValue().isEmpty()) {
                    continue;
                }
                EntityRegistry.addSpawn(
                        ruleSet.entityClass,
                        entry.getKey().intValue(),
                        rule.minGroup,
                        rule.maxGroup,
                        ruleSet.creatureType,
                        entry.getValue().toArray(new BiomeGenBase[entry.getValue().size()])
                );
            }
        }
    }

    private static Map<Integer, List<BiomeGenBase>> collectRegistrationBiomesByWeight(SpawnRule rule) {
        Map<Integer, List<BiomeGenBase>> out = new LinkedHashMap<Integer, List<BiomeGenBase>>();
        BiomeGenBase[] biomes = BiomeGenBase.getBiomeGenArray();
        if (biomes == null) {
            return out;
        }

        for (BiomeGenBase biome : biomes) {
            if (biome != null && rule.matchesBiome(biome)) {
                Integer weight = Integer.valueOf(rule.getRegistrationWeight(biome));
                List<BiomeGenBase> weightedBiomes = out.get(weight);
                if (weightedBiomes == null) {
                    weightedBiomes = new ArrayList<BiomeGenBase>();
                    out.put(weight, weightedBiomes);
                }
                weightedBiomes.add(biome);
            }
        }
        return out;
    }

    private static void removeExistingSpawns(Class<? extends EntityLiving> entityClass) {
        BiomeGenBase[] biomes = BiomeGenBase.getBiomeGenArray();
        if (biomes == null || entityClass == null) {
            return;
        }

        for (BiomeGenBase biome : biomes) {
            if (biome == null) {
                continue;
            }
            EnumCreatureType[] creatureTypes = EnumCreatureType.values();
            for (EnumCreatureType creatureType : creatureTypes) {
                List spawnList = biome.getSpawnableList(creatureType);
                if (spawnList == null) {
                    continue;
                }
                Iterator iterator = spawnList.iterator();
                while (iterator.hasNext()) {
                    Object entryObj = iterator.next();
                    if (entryObj instanceof BiomeGenBase.SpawnListEntry
                            && ((BiomeGenBase.SpawnListEntry) entryObj).entityClass == entityClass) {
                        iterator.remove();
                    }
                }
            }
        }
    }

    private static void removeUnwhitelistedSpawnsFromWhitelistOnlyBiomes() {
        if (WHITELIST_ONLY_BIOME_IDS.isEmpty()) {
            return;
        }

        BiomeGenBase[] biomes = BiomeGenBase.getBiomeGenArray();
        if (biomes == null) {
            return;
        }

        for (BiomeGenBase biome : biomes) {
            if (biome == null || !isWhitelistOnlyBiome(biome.biomeID)) {
                continue;
            }

            EnumCreatureType[] creatureTypes = EnumCreatureType.values();
            for (EnumCreatureType creatureType : creatureTypes) {
                List spawnList = biome.getSpawnableList(creatureType);
                if (spawnList == null) {
                    continue;
                }

                Iterator iterator = spawnList.iterator();
                while (iterator.hasNext()) {
                    Object entryObj = iterator.next();
                    if (!(entryObj instanceof BiomeGenBase.SpawnListEntry)) {
                        continue;
                    }

                    Class entityClass = ((BiomeGenBase.SpawnListEntry) entryObj).entityClass;
                    RuleSet ruleSet = ACTIVE_RULES.get(entityClass);
                    if (ruleSet == null || !ruleSet.allowsBiomeIgnoringDimension(biome)) {
                        iterator.remove();
                    }
                }
            }
        }
    }

    private static List<SpawnRule> parseRules(String[] entries, boolean whitelist) {
        List<SpawnRule> rules = new ArrayList<SpawnRule>();
        if (entries == null) {
            return rules;
        }

        for (String raw : entries) {
            SpawnRule rule = SpawnRule.parse(raw, whitelist);
            if (rule != null) {
                rules.add(rule);
            }
        }
        return rules;
    }

    private static Set<Integer> parseWhitelistOnlyBiomes(String[] entries) {
        Set<Integer> out = new LinkedHashSet<Integer>();
        if (entries == null) {
            return out;
        }

        for (String raw : entries) {
            out.addAll(SpawnRule.parseBiomeIdSet(raw, "WhitelistOnlyBiomes"));
        }
        return out;
    }

    private static void setWhitelistOnlyBiomes(Set<Integer> biomeIds) {
        WHITELIST_ONLY_BIOME_IDS.clear();
        if (biomeIds == null) {
            return;
        }

        for (Integer biomeId : biomeIds) {
            if (biomeId != null && biomeId.intValue() >= 0) {
                WHITELIST_ONLY_BIOME_IDS.set(biomeId.intValue());
            }
        }
    }

    private static boolean isWhitelistOnlyBiome(int biomeId) {
        return biomeId >= 0 && WHITELIST_ONLY_BIOME_IDS.get(biomeId);
    }

    private static boolean isHostileMob(EntityLivingBase entity) {
        return entity instanceof IMob || entity instanceof EntityMob;
    }

    private static boolean isBrightDaylightSurface(World world, int x, int y, int z) {
        return world != null
                && world.provider != null
                && !world.provider.hasNoSky
                && world.isDaytime()
                && !world.isRaining()
                && !world.isThundering()
                && (world.canBlockSeeTheSky(x, y, z)
                || world.getSavedLightValue(EnumSkyBlock.Sky, x, y, z) >= 8);
    }

    @SuppressWarnings("unchecked")
    private static Class<? extends EntityLiving> resolveLivingClass(String mobName) {
        if (mobName == null || mobName.trim().isEmpty()) {
            return null;
        }

        for (Object entryObj : EntityList.stringToClassMapping.entrySet()) {
            if (!(entryObj instanceof Map.Entry)) {
                continue;
            }

            Map.Entry<?, ?> entry = (Map.Entry<?, ?>) entryObj;
            Object rawName = entry.getKey();
            Object rawClass = entry.getValue();
            if (!(rawName instanceof String) || !(rawClass instanceof Class)) {
                continue;
            }

            Class<?> entityClass = (Class<?>) rawClass;
            if (!EntityLiving.class.isAssignableFrom(entityClass)) {
                continue;
            }

            if (matchesEntityName(mobName, (String) rawName, entityClass)) {
                return (Class<? extends EntityLiving>) entityClass;
            }
        }

        return null;
    }

    private static boolean matchesEntityName(String configuredName, String registeredName, Class<?> entityClass) {
        if (hasRiftFluxEntityPrefix(configuredName) && !isRiftFluxEntity(registeredName, entityClass)) {
            return false;
        }

        String configured = ConfigResolver.stripKnownEntityPrefixes(ConfigResolver.normalizeToken(configuredName));
        String configuredPath = ConfigResolver.stripKnownEntityPrefixes(ConfigResolver.normalizeToken(stripNamespace(configuredName)));
        if (configured.isEmpty() && configuredPath.isEmpty()) {
            return false;
        }

        String registered = ConfigResolver.stripKnownEntityPrefixes(ConfigResolver.normalizeToken(registeredName));
        String registeredPath = ConfigResolver.stripKnownEntityPrefixes(ConfigResolver.normalizeToken(stripNamespace(registeredName)));
        String simple = ConfigResolver.stripKnownEntityPrefixes(ConfigResolver.normalizeToken(entityClass.getSimpleName()));
        String full = ConfigResolver.normalizeToken(entityClass.getName());

        return configured.equals(registered)
                || configured.equals(registeredPath)
                || configured.equals(simple)
                || configured.equals(full)
                || configuredPath.equals(registered)
                || configuredPath.equals(registeredPath)
                || configuredPath.equals(simple)
                || configuredPath.equals(full);
    }

    private static boolean hasRiftFluxEntityPrefix(String configuredName) {
        if (configuredName == null) {
            return false;
        }

        String trimmed = configuredName.trim().toLowerCase(Locale.ROOT);
        if (trimmed.startsWith("riftflux:") || trimmed.startsWith("riftflux.")) {
            return true;
        }
        return ConfigResolver.normalizeToken(configuredName).startsWith("riftflux");
    }

    private static boolean isRiftFluxEntity(String registeredName, Class<?> entityClass) {
        if (entityClass != null && entityClass.getName().startsWith("com.voidsrift.riftflux.")) {
            return true;
        }
        return registeredName != null && ConfigResolver.normalizeToken(registeredName).startsWith("riftflux");
    }

    private static String stripNamespace(String value) {
        if (value == null) {
            return "";
        }
        int colon = value.indexOf(':');
        int dot = value.indexOf('.');
        int split = colon >= 0 ? colon : dot;
        return split >= 0 && split + 1 < value.length() ? value.substring(split + 1) : value;
    }

    private static EnumCreatureType inferCreatureType(Class<? extends EntityLiving> entityClass, String mobName) {
        String key = ConfigResolver.stripKnownEntityPrefixes(ConfigResolver.normalizeToken(mobName));
        String simple = ConfigResolver.stripKnownEntityPrefixes(ConfigResolver.normalizeToken(entityClass.getSimpleName()));

        if ("axolotl".equals(key) || "axolotl".equals(simple) || EntityWaterMob.class.isAssignableFrom(entityClass)) {
            return EnumCreatureType.waterCreature;
        }
        if ("sootsprite".equals(key)
                || "duck".equals(key)
                || "quackling".equals(key)
                || "flowerman".equals(key)
                || "nimatin".equals(key)
                || EntityAnimal.class.isAssignableFrom(entityClass)) {
            return EnumCreatureType.creature;
        }
        if (EntityAmbientCreature.class.isAssignableFrom(entityClass)) {
            return EnumCreatureType.ambient;
        }
        if (IMob.class.isAssignableFrom(entityClass)) {
            return EnumCreatureType.monster;
        }
        return EnumCreatureType.creature;
    }

    private static final class RuleSet {
        private final Class<? extends EntityLiving> entityClass;
        private final EnumCreatureType creatureType;
        private final List<SpawnRule> whitelist = new ArrayList<SpawnRule>();
        private final List<SpawnRule> blacklist = new ArrayList<SpawnRule>();

        private RuleSet(Class<? extends EntityLiving> entityClass, EnumCreatureType creatureType) {
            this.entityClass = entityClass;
            this.creatureType = creatureType;
        }

        private boolean allows(int dimension, BiomeGenBase biome) {
            for (SpawnRule rule : blacklist) {
                if (rule.matches(dimension, biome)) {
                    return false;
                }
            }
            for (SpawnRule rule : whitelist) {
                if (rule.matches(dimension, biome)) {
                    return true;
                }
            }
            return false;
        }

        private boolean allowsBiomeIgnoringDimension(BiomeGenBase biome) {
            for (SpawnRule rule : blacklist) {
                if (rule.matchesBiome(biome)) {
                    return false;
                }
            }
            for (SpawnRule rule : whitelist) {
                if (rule.matchesBiome(biome)) {
                    return true;
                }
            }
            return false;
        }
    }

    private static final class SpawnRule {
        private final String mobName;
        private final int weight;
        private final int minGroup;
        private final int maxGroup;
        private final Set<Integer> dimensions;
        private final Set<BiomeDictionary.Type> biomeTypes;
        private final Set<Integer> biomeIds;
        private final boolean anyBiome;
        private final BitSet matchingBiomeIds;
        private final Map<Integer, Integer> biomeWeights;
        private SpawnRule(
                String mobName,
                int weight,
                int minGroup,
                int maxGroup,
                Set<Integer> dimensions,
                Set<BiomeDictionary.Type> biomeTypes,
                Set<Integer> biomeIds,
                Map<Integer, Integer> biomeWeights
        ) {
            this.mobName = mobName;
            this.weight = weight;
            this.minGroup = minGroup;
            this.maxGroup = maxGroup;
            this.dimensions = dimensions;
            this.biomeTypes = biomeTypes;
            this.biomeIds = biomeIds;
            this.anyBiome = biomeTypes.isEmpty() && biomeIds.isEmpty();
            this.matchingBiomeIds = compileMatchingBiomeIds(biomeTypes, biomeIds);
            this.biomeWeights = biomeWeights;
        }

        private static SpawnRule parse(String raw, boolean whitelist) {
            if (raw == null) {
                return null;
            }

            String trimmed = raw.trim();
            if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                return null;
            }

            String[] parts = trimmed.split("\\|", -1);
            String mobName = getPart(parts, 0);
            if (mobName.isEmpty()) {
                return null;
            }

            int weight = whitelist ? parsePositiveInt(getPart(parts, 1), 0) : 0;
            if (whitelist && weight <= 0) {
                return null;
            }

            int[] group = parseGroupSize(getPart(parts, 2));
            BiomeSelectorParse biomeSelectors = parseBiomeSelectors(getPart(parts, 5), mobName);
            return new SpawnRule(
                    mobName,
                    weight,
                    group[0],
                    group[1],
                    parseIntegerSet(getPart(parts, 3)),
                    parseBiomeTypeSet(getPart(parts, 4)),
                    biomeSelectors.biomeIds,
                    biomeSelectors.biomeWeights
            );
        }

        private boolean matches(int dimension, BiomeGenBase biome) {
            return matchesDimension(dimension) && matchesBiome(biome);
        }

        private boolean matchesDimension(int dimension) {
            return dimensions.isEmpty() || dimensions.contains(dimension);
        }

        private boolean matchesBiome(BiomeGenBase biome) {
            if (biome == null) {
                return false;
            }
            if (anyBiome) {
                return true;
            }
            return biome.biomeID >= 0 && matchingBiomeIds.get(biome.biomeID);
        }

        private int getRegistrationWeight(BiomeGenBase biome) {
            if (biome == null) {
                return weight;
            }
            Integer biomeWeight = biomeWeights.get(Integer.valueOf(biome.biomeID));
            return biomeWeight == null ? weight : biomeWeight.intValue();
        }

        private static String getPart(String[] parts, int index) {
            return parts != null && index >= 0 && index < parts.length && parts[index] != null
                    ? parts[index].trim()
                    : "";
        }

        private static int parsePositiveInt(String value, int fallback) {
            if (value == null || value.trim().isEmpty()) {
                return fallback;
            }
            try {
                return Math.max(0, Integer.parseInt(value.trim()));
            } catch (NumberFormatException ignored) {
                return fallback;
            }
        }

        private static int[] parseGroupSize(String value) {
            if (value == null || value.trim().isEmpty()) {
                return new int[]{1, 1};
            }

            String trimmed = value.trim();
            String[] parts = trimmed.split("-", 2);
            int min = parsePositiveInt(parts[0], 1);
            int max = parts.length > 1 ? parsePositiveInt(parts[1], min) : min;
            min = Math.max(1, min);
            max = Math.max(min, max);
            return new int[]{min, max};
        }

        private static Set<Integer> parseIntegerSet(String value) {
            Set<Integer> out = new LinkedHashSet<Integer>();
            if (value == null || value.trim().isEmpty()) {
                return out;
            }

            String[] parts = value.trim().split("[,; ]+");
            for (String part : parts) {
                if (part == null) {
                    continue;
                }
                String token = part.trim();
                if (token.isEmpty() || !ConfigResolver.isInteger(token)) {
                    continue;
                }
                out.add(Integer.valueOf(token));
            }
            return out;
        }

        private static Set<BiomeDictionary.Type> parseBiomeTypeSet(String value) {
            Set<BiomeDictionary.Type> out = new LinkedHashSet<BiomeDictionary.Type>();
            if (value == null || value.trim().isEmpty()) {
                return out;
            }

            String[] parts = value.trim().split("[,;]+");
            for (String part : parts) {
                if (part == null) {
                    continue;
                }
                String token = part.trim();
                if (token.isEmpty()) {
                    continue;
                }

                String normalized = token.toUpperCase(Locale.ROOT);
                if (normalized.startsWith("TYPE:")) {
                    normalized = normalized.substring(5).trim();
                }
                if ("MAGIC".equals(normalized)) {
                    normalized = "MAGICAL";
                }
                if ("ICE".equals(normalized) || "ICY".equals(normalized)) {
                    normalized = "SNOWY";
                }

                try {
                    out.add(BiomeDictionary.Type.valueOf(normalized));
                } catch (IllegalArgumentException ignored) {
                }
            }
            return out;
        }

        private static BitSet compileMatchingBiomeIds(Set<BiomeDictionary.Type> biomeTypes, Set<Integer> biomeIds) {
            BitSet out = new BitSet();
            if (biomeIds != null) {
                for (Integer biomeId : biomeIds) {
                    if (biomeId != null && biomeId.intValue() >= 0) {
                        out.set(biomeId.intValue());
                    }
                }
            }

            if (biomeTypes == null || biomeTypes.isEmpty()) {
                return out;
            }

            BiomeGenBase[] biomes = BiomeGenBase.getBiomeGenArray();
            if (biomes == null) {
                return out;
            }

            for (BiomeGenBase biome : biomes) {
                if (biome == null || biome.biomeID < 0) {
                    continue;
                }

                for (BiomeDictionary.Type type : biomeTypes) {
                    if (BiomeDictionary.isBiomeOfType(biome, type)) {
                        out.set(biome.biomeID);
                        break;
                    }
                }
            }
            return out;
        }

        private static Set<Integer> parseBiomeIdSet(String value, String mobName) {
            return parseBiomeSelectors(value, mobName).biomeIds;
        }

        private static BiomeSelectorParse parseBiomeSelectors(String value, String mobName) {
            Set<Integer> out = new LinkedHashSet<Integer>();
            Map<Integer, Integer> weights = new LinkedHashMap<Integer, Integer>();
            if (value == null || value.trim().isEmpty()) {
                return new BiomeSelectorParse(out, weights);
            }

            String[] parts = value.trim().split("[,;]+");
            for (String part : parts) {
                if (part == null) {
                    continue;
                }
                String token = part.trim();
                if (token.isEmpty()) {
                    continue;
                }

                String rawToken = token;
                Integer weightOverride = null;
                int openParen = token.lastIndexOf('(');
                if (openParen > 0 && token.endsWith(")")) {
                    String weightText = token.substring(openParen + 1, token.length() - 1).trim();
                    if (ConfigResolver.isInteger(weightText)) {
                        weightOverride = Integer.valueOf(Math.max(0, ConfigResolver.parseIntSafe(weightText, 0)));
                        token = token.substring(0, openParen).trim();
                    }
                }

                String lowered = token.toLowerCase(Locale.ROOT);
                if (lowered.startsWith("id:")) {
                    token = token.substring(3).trim();
                    lowered = token.toLowerCase(Locale.ROOT);
                } else if (lowered.startsWith("name:")) {
                    token = token.substring(5).trim();
                    lowered = token.toLowerCase(Locale.ROOT);
                }

                if (ConfigResolver.isInteger(token)) {
                    Integer biomeId = Integer.valueOf(ConfigResolver.parseIntSafe(token, Integer.MIN_VALUE));
                    out.add(biomeId);
                    if (weightOverride != null) {
                        weights.put(biomeId, weightOverride);
                    }
                    continue;
                }

                if ("wheatfield".equals(lowered) || "name:wheatfield".equals(lowered)) {
                    Integer wheatfieldId = getWheatfieldBiomeId();
                    if (wheatfieldId != null) {
                        out.add(wheatfieldId);
                        if (weightOverride != null) {
                            weights.put(wheatfieldId, weightOverride);
                        }
                    }
                    continue;
                }

                Integer biomeId = resolveBiomeName(token);
                if (biomeId == null) {
                    biomeId = resolveBiomeName(stripNamespace(token));
                }
                if (biomeId != null) {
                    out.add(biomeId);
                    if (weightOverride != null) {
                        weights.put(biomeId, weightOverride);
                    }
                } else {
                    FMLLog.warning("[RiftFlux] Ignoring unknown natural mob spawn biome name/id '%s' in rule for '%s'.", rawToken, mobName);
                }
            }
            return new BiomeSelectorParse(out, weights);
        }

        private static Integer resolveBiomeName(String value) {
            String normalized = ConfigResolver.normalizeToken(value);
            if (normalized.isEmpty()) {
                return null;
            }

            BiomeGenBase[] biomes = BiomeGenBase.getBiomeGenArray();
            if (biomes == null) {
                return null;
            }

            for (BiomeGenBase biome : biomes) {
                if (biome != null
                        && biome.biomeName != null
                        && normalized.equals(ConfigResolver.normalizeToken(biome.biomeName))) {
                    return Integer.valueOf(biome.biomeID);
                }
            }
            return null;
        }

        private static Integer getWheatfieldBiomeId() {
            return WheatfieldContent.wheatfieldBiome == null
                    ? null
                    : Integer.valueOf(WheatfieldContent.wheatfieldBiome.biomeID);
        }

        private static final class BiomeSelectorParse {
            private final Set<Integer> biomeIds;
            private final Map<Integer, Integer> biomeWeights;

            private BiomeSelectorParse(Set<Integer> biomeIds, Map<Integer, Integer> biomeWeights) {
                this.biomeIds = biomeIds;
                this.biomeWeights = biomeWeights;
            }
        }
    }
}
