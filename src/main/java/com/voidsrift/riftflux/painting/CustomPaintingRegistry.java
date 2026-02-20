package com.voidsrift.riftflux.painting;

import com.voidsrift.riftflux.ModConfig;
import cpw.mods.fml.common.FMLLog;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.EnumHelper;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public final class CustomPaintingRegistry {

    private static final Map<String, ResourceLocation> CUSTOM_TEXTURES_BY_TITLE = new HashMap<String, ResourceLocation>();
    private static final String FALLBACK_TEXTURE = "riftflux:textures/painting/custom_paintings.png";

    private static boolean registered;

    private CustomPaintingRegistry() {}

    public static synchronized void registerFromConfig() {
        if (registered) {
            return;
        }
        registered = true;

        if (!ModConfig.enableCustomPaintings) {
            return;
        }

        String defaultTexture = normalizeTexture(ModConfig.customPaintingDefaultTexture);
        if (defaultTexture == null) {
            defaultTexture = FALLBACK_TEXTURE;
        }

        String[] entries = ModConfig.customPaintingEntries;
        if (entries == null || entries.length == 0) {
            return;
        }

        Set<String> knownTitles = new HashSet<String>();
        Set<String> knownEnumNames = new HashSet<String>();
        for (EntityPainting.EnumArt art : EntityPainting.EnumArt.values()) {
            knownTitles.add(art.title);
            knownEnumNames.add(art.name());
        }

        int addedCount = 0;
        for (String raw : entries) {
            PaintingSpec spec = parseSpec(raw, defaultTexture);
            if (spec == null) {
                continue;
            }
            if (knownTitles.contains(spec.title)) {
                FMLLog.warning("[RiftFlux] Skipping custom painting '%s': motive title already exists.", spec.title);
                continue;
            }
            if (!isValidMultipleOf16(spec.sizeX) || !isValidMultipleOf16(spec.sizeY)) {
                FMLLog.warning("[RiftFlux] Skipping custom painting '%s': size must be > 0 and divisible by 16.", spec.title);
                continue;
            }
            if (spec.offsetX < 0 || spec.offsetY < 0) {
                FMLLog.warning("[RiftFlux] Skipping custom painting '%s': offsets must be >= 0.", spec.title);
                continue;
            }

            String enumName = uniqueEnumName(spec.title, knownEnumNames);
            EntityPainting.EnumArt added = EnumHelper.addArt(
                    enumName,
                    spec.title,
                    spec.sizeX,
                    spec.sizeY,
                    spec.offsetX,
                    spec.offsetY
            );

            if (added == null) {
                FMLLog.warning("[RiftFlux] Failed to add custom painting '%s'.", spec.title);
                continue;
            }

            knownEnumNames.add(enumName);
            knownTitles.add(spec.title);
            CUSTOM_TEXTURES_BY_TITLE.put(spec.title, new ResourceLocation(spec.texture));
            addedCount++;
        }

        if (addedCount > 0) {
            FMLLog.info("[RiftFlux] Registered %d custom paintings.", addedCount);
        }
    }

    public static synchronized ResourceLocation getCustomTexture(EntityPainting.EnumArt art) {
        if (!registered) {
            registerFromConfig();
        }
        if (art == null) {
            return null;
        }
        return CUSTOM_TEXTURES_BY_TITLE.get(art.title);
    }

    private static boolean isValidMultipleOf16(int value) {
        return value > 0 && (value & 15) == 0;
    }

    private static String uniqueEnumName(String title, Set<String> knownEnumNames) {
        String base = sanitizeEnumName(title);
        String candidate = base;
        int i = 1;
        while (knownEnumNames.contains(candidate)) {
            candidate = base + "_" + i;
            i++;
        }
        return candidate;
    }

    private static String sanitizeEnumName(String title) {
        StringBuilder out = new StringBuilder();
        boolean previousUnderscore = false;

        for (int i = 0; i < title.length(); i++) {
            char c = title.charAt(i);
            if (Character.isLetterOrDigit(c)) {
                out.append(Character.toUpperCase(c));
                previousUnderscore = false;
            } else if (!previousUnderscore) {
                out.append('_');
                previousUnderscore = true;
            }
        }

        int start = 0;
        int end = out.length();
        while (start < end && out.charAt(start) == '_') {
            start++;
        }
        while (end > start && out.charAt(end - 1) == '_') {
            end--;
        }

        String trimmed = start >= end ? "" : out.substring(start, end);
        if (trimmed.isEmpty()) {
            trimmed = "RIFTFLUX_ART";
        }
        if (Character.isDigit(trimmed.charAt(0))) {
            trimmed = "ART_" + trimmed;
        }
        return trimmed.toUpperCase(Locale.ROOT);
    }

    private static PaintingSpec parseSpec(String raw, String defaultTexture) {
        if (raw == null) {
            return null;
        }

        String line = raw.trim();
        if (line.isEmpty()) {
            return null;
        }

        String[] parts = line.split(";", 6);
        if (parts.length < 5) {
            FMLLog.warning("[RiftFlux] Invalid custom painting entry '%s'. Expected at least 5 ';'-separated fields.", line);
            return null;
        }

        String title = parts[0].trim();
        if (title.isEmpty()) {
            FMLLog.warning("[RiftFlux] Invalid custom painting entry '%s'. Title cannot be empty.", line);
            return null;
        }

        Integer sizeX = parseInt(parts[1], "sizeX", line);
        Integer sizeY = parseInt(parts[2], "sizeY", line);
        Integer offsetX = parseInt(parts[3], "offsetX", line);
        Integer offsetY = parseInt(parts[4], "offsetY", line);
        if (sizeX == null || sizeY == null || offsetX == null || offsetY == null) {
            return null;
        }

        String texture = parts.length >= 6 ? normalizeTexture(parts[5]) : null;
        if (texture == null) {
            texture = defaultTexture;
        }
        if (texture == null) {
            texture = FALLBACK_TEXTURE;
        }

        return new PaintingSpec(title, sizeX, sizeY, offsetX, offsetY, texture);
    }

    private static Integer parseInt(String raw, String fieldName, String sourceLine) {
        if (raw == null) {
            FMLLog.warning("[RiftFlux] Invalid custom painting entry '%s'. Field '%s' is missing.", sourceLine, fieldName);
            return null;
        }
        try {
            return Integer.valueOf(raw.trim());
        } catch (NumberFormatException e) {
            FMLLog.warning("[RiftFlux] Invalid custom painting entry '%s'. Field '%s' must be an integer.", sourceLine, fieldName);
            return null;
        }
    }

    private static String normalizeTexture(String raw) {
        if (raw == null) {
            return null;
        }
        String texture = raw.trim();
        return texture.isEmpty() ? null : texture;
    }

    private static final class PaintingSpec {
        private final String title;
        private final int sizeX;
        private final int sizeY;
        private final int offsetX;
        private final int offsetY;
        private final String texture;

        private PaintingSpec(String title, int sizeX, int sizeY, int offsetX, int offsetY, String texture) {
            this.title = title;
            this.sizeX = sizeX;
            this.sizeY = sizeY;
            this.offsetX = offsetX;
            this.offsetY = offsetY;
            this.texture = texture;
        }
    }
}
