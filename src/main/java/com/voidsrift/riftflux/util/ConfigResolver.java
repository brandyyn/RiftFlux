package com.voidsrift.riftflux.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.LinkedHashSet;

public final class ConfigResolver {
    private ConfigResolver() {
    }

    public static String normalizeToken(String value) {
        if (value == null) {
            return "";
        }

        String lowered = value.toLowerCase(Locale.ROOT);
        StringBuilder out = new StringBuilder(lowered.length());
        for (int i = 0; i < lowered.length(); i++) {
            char c = lowered.charAt(i);
            if ((c >= 'a' && c <= 'z') || (c >= '0' && c <= '9')) {
                out.append(c);
            }
        }
        return out.toString();
    }

    public static String stripKnownEntityPrefixes(String value) {
        if (value == null) {
            return "";
        }

        String stripped = value;
        if (stripped.startsWith("entity")) {
            stripped = stripped.substring("entity".length());
        }
        if (stripped.startsWith("riftflux")) {
            stripped = stripped.substring("riftflux".length());
        }
        return stripped;
    }

    public static boolean isInteger(String value) {
        if (value == null || value.isEmpty()) {
            return false;
        }
        int start = value.charAt(0) == '-' ? 1 : 0;
        if (start == value.length()) {
            return false;
        }
        for (int i = start; i < value.length(); i++) {
            if (!Character.isDigit(value.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static int parseIntSafe(String value, int fallback) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ignored) {
            return fallback;
        }
    }

    public static Set<Integer> parseIntegerSet(String raw) {
        LinkedHashSet<Integer> out = new LinkedHashSet<Integer>();
        if (raw == null) {
            return out;
        }

        String trimmed = raw.trim();
        if (trimmed.isEmpty()) {
            return out;
        }

        String[] parts = trimmed.split("[,; ]+");
        for (String part : parts) {
            if (part == null) {
                continue;
            }
            String token = part.trim();
            if (token.isEmpty() || !isInteger(token)) {
                continue;
            }
            out.add(Integer.parseInt(token));
        }
        return out;
    }

    public static int[] parseIntegerList(String[] entries) {
        if (entries == null || entries.length == 0) {
            return new int[0];
        }

        List<Integer> out = new ArrayList<Integer>();
        for (String entry : entries) {
            if (entry == null) {
                continue;
            }

            String trimmed = entry.trim();
            if (trimmed.isEmpty()) {
                continue;
            }

            String[] parts = trimmed.split("[,; ]+");
            for (String part : parts) {
                if (part == null || !isInteger(part.trim())) {
                    continue;
                }
                out.add(Integer.parseInt(part.trim()));
            }
        }

        int[] ids = new int[out.size()];
        for (int i = 0; i < out.size(); i++) {
            ids[i] = out.get(i);
        }
        return ids;
    }

    public static BiomeGenBase[] filterBiomes(BiomeGenBase[] biomes, String[] entries, boolean whitelist) {
        if (biomes == null || biomes.length == 0) {
            return new BiomeGenBase[0];
        }
        if (entries == null || entries.length == 0) {
            return whitelist ? new BiomeGenBase[0] : biomes;
        }

        List<BiomeGenBase> filtered = new ArrayList<BiomeGenBase>();
        for (BiomeGenBase biome : biomes) {
            if (biome == null) {
                continue;
            }

            boolean matched = false;
            for (String entry : entries) {
                if (matchesBiomeEntry(biome, entry)) {
                    matched = true;
                    break;
                }
            }

            if ((whitelist && matched) || (!whitelist && !matched)) {
                filtered.add(biome);
            }
        }

        return filtered.toArray(new BiomeGenBase[filtered.size()]);
    }

    public static boolean matchesBiomeEntry(BiomeGenBase biome, String entry) {
        if (biome == null || entry == null) {
            return false;
        }

        String trimmed = entry.trim();
        if (trimmed.isEmpty()) {
            return false;
        }

        String lowered = trimmed.toLowerCase(Locale.ROOT);
        if (lowered.startsWith("id:")) {
            return biome.biomeID == parseIntSafe(trimmed.substring(3).trim(), Integer.MIN_VALUE);
        }
        if (isInteger(trimmed)) {
            return biome.biomeID == parseIntSafe(trimmed, Integer.MIN_VALUE);
        }
        if (lowered.startsWith("type:") || lowered.startsWith("dict:") || lowered.startsWith("dictionary:")) {
            String typeName = trimmed.substring(trimmed.indexOf(':') + 1).trim();
            try {
                BiomeDictionary.Type type = BiomeDictionary.Type.valueOf(typeName.toUpperCase(Locale.ROOT));
                return BiomeDictionary.isBiomeOfType(biome, type);
            } catch (IllegalArgumentException ignored) {
                return false;
            }
        }
        if (lowered.startsWith("name:")) {
            trimmed = trimmed.substring(5).trim();
        }

        return normalizeToken(biome.biomeName).equals(normalizeToken(trimmed));
    }

    public static boolean matchesConfiguredEntity(Entity entity, String[] entries) {
        if (entity == null || entries == null || entries.length == 0) {
            return false;
        }

        for (String entry : entries) {
            if (matchesConfiguredEntity(entity, entry)) {
                return true;
            }
        }

        return false;
    }

    public static boolean matchesConfiguredEntity(Entity entity, String entry) {
        if (entity == null || entry == null) {
            return false;
        }

        String normalizedEntry = stripKnownEntityPrefixes(normalizeToken(entry));
        if (normalizedEntry.isEmpty()) {
            return false;
        }

        String entityString = getConfiguredEntityId(entity);
        String className = stripKnownEntityPrefixes(normalizeToken(entity.getClass().getSimpleName()));
        String fullClassName = normalizeToken(entity.getClass().getName());

        return normalizedEntry.equals(entityString)
                || normalizedEntry.equals(className)
                || normalizedEntry.equals(fullClassName);
    }

    public static String getConfiguredEntityId(Entity entity) {
        if (entity == null) {
            return "";
        }

        String entityString = stripKnownEntityPrefixes(normalizeToken(EntityList.getEntityString(entity)));
        if (!entityString.isEmpty()) {
            return entityString;
        }

        String className = stripKnownEntityPrefixes(normalizeToken(entity.getClass().getSimpleName()));
        if (!className.isEmpty()) {
            return className;
        }

        return normalizeToken(entity.getClass().getName());
    }

    public static boolean matchesConfiguredEntityClass(Class<?> entityClass, String[] entries) {
        if (entityClass == null || entries == null || entries.length == 0) {
            return false;
        }

        for (String entry : entries) {
            if (matchesConfiguredEntityClass(entityClass, entry)) {
                return true;
            }
        }

        return false;
    }

    public static boolean matchesConfiguredEntityClass(Class<?> entityClass, String entry) {
        if (entityClass == null || entry == null) {
            return false;
        }

        String normalizedEntry = stripKnownEntityPrefixes(normalizeToken(entry));
        if (normalizedEntry.isEmpty()) {
            return false;
        }

        Object rawRegisteredName = EntityList.classToStringMapping.get(entityClass);
        String registeredName = rawRegisteredName instanceof String ? (String) rawRegisteredName : "";
        String registeredNormalized = stripKnownEntityPrefixes(normalizeToken(registeredName));
        String simpleNormalized = stripKnownEntityPrefixes(normalizeToken(entityClass.getSimpleName()));
        String fullClassNormalized = normalizeToken(entityClass.getName());

        return normalizedEntry.equals(registeredNormalized)
                || normalizedEntry.equals(simpleNormalized)
                || normalizedEntry.equals(fullClassNormalized);
    }

    public static EntityLiving createRegisteredLivingByAlias(World world, String key) {
        if (world == null || key == null) {
            return null;
        }

        String trimmed = key.trim();
        if (trimmed.isEmpty()) {
            return null;
        }

        Entity entity = EntityList.createEntityByName(trimmed, world);
        if (entity instanceof EntityLiving) {
            return (EntityLiving) entity;
        }

        String normalizedKey = normalizeToken(trimmed);
        if (normalizedKey.isEmpty()) {
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

            String registeredName = (String) rawName;
            Class<?> entityClass = (Class<?>) rawClass;
            if (!EntityLiving.class.isAssignableFrom(entityClass)) {
                continue;
            }

            String registeredNormalized = stripKnownEntityPrefixes(normalizeToken(registeredName));
            String simpleNormalized = stripKnownEntityPrefixes(normalizeToken(entityClass.getSimpleName()));
            String fullClassNormalized = normalizeToken(entityClass.getName());

            if (!normalizedKey.equals(registeredNormalized)
                    && !normalizedKey.equals(simpleNormalized)
                    && !normalizedKey.equals(fullClassNormalized)) {
                continue;
            }

            entity = EntityList.createEntityByName(registeredName, world);
            if (entity instanceof EntityLiving) {
                return (EntityLiving) entity;
            }
        }

        return null;
    }
}
