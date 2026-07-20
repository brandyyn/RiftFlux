package com.voidsrift.riftflux.util;

import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public final class ConfiguredPotionEffectHelper {
    private ConfiguredPotionEffectHelper() {
    }

    /** Parses potionNameOrId,level,durationSeconds entries. Levels are one-based. */
    public static List<PotionEffect> parseEffects(String[] entries) {
        if (entries == null || entries.length == 0) {
            return Collections.emptyList();
        }

        List<PotionEffect> effects = new ArrayList<PotionEffect>();
        for (String entry : entries) {
            PotionEffect effect = parseEffect(entry);
            if (effect != null) {
                effects.add(effect);
            }
        }
        return effects.isEmpty() ? Collections.<PotionEffect>emptyList() : effects;
    }

    public static PotionEffect parseEffect(String entry) {
        if (entry == null) {
            return null;
        }
        String raw = entry.trim();
        if (raw.isEmpty()) {
            return null;
        }
        String[] parts = raw.split("\\s*,\\s*");
        if (parts.length < 3) {
            return null;
        }

        int potionId = parsePotionId(parts[0]);
        if (potionId < 0 || potionId >= Potion.potionTypes.length || Potion.potionTypes[potionId] == null) {
            return null;
        }

        int level = Math.max(1, parseInt(parts[1], 1));
        int durationTicks = Potion.potionTypes[potionId].isInstant() ? 1 : parseSecondsTicks(parts[2]);
        if (durationTicks <= 0) {
            return null;
        }
        return new PotionEffect(potionId, durationTicks, level - 1, true);
    }

    public static int parsePotionId(String token) {
        if (token == null) {
            return -1;
        }
        String trimmed = token.trim();
        if (trimmed.isEmpty()) {
            return -1;
        }
        try {
            return Integer.parseInt(trimmed);
        } catch (NumberFormatException ignored) {
        }

        String normalized = trimmed.toLowerCase(Locale.ROOT);
        if (normalized.startsWith("potion.")) {
            normalized = normalized.substring("potion.".length());
        }
        if ("speed".equals(normalized)) normalized = "movespeed";
        if ("slowness".equals(normalized)) normalized = "moveslowdown";
        if ("haste".equals(normalized)) normalized = "digspeed";
        if ("miningfatigue".equals(normalized)) normalized = "digslowdown";
        if ("strength".equals(normalized)) normalized = "damageboost";
        if ("regen".equals(normalized)) normalized = "regeneration";
        if ("manaregen".equals(normalized)) normalized = "legendgearmanaregen";

        for (Potion potion : Potion.potionTypes) {
            if (potion == null || potion.getName() == null) {
                continue;
            }
            String name = potion.getName().toLowerCase(Locale.ROOT);
            if (name.startsWith("potion.")) {
                name = name.substring("potion.".length());
            }
            if (name.equals(normalized)) {
                return potion.id;
            }
        }
        return -1;
    }

    private static int parseSecondsTicks(String token) {
        if (token == null) {
            return 0;
        }
        String value = token.trim().toLowerCase(Locale.ROOT);
        if (value.endsWith("s")) {
            value = value.substring(0, value.length() - 1).trim();
        }
        try {
            float seconds = Float.parseFloat(value);
            return seconds > 0.0F ? Math.max(1, Math.round(seconds * 20.0F)) : 0;
        } catch (NumberFormatException ignored) {
            return 0;
        }
    }

    private static int parseInt(String token, int fallback) {
        try {
            return Integer.parseInt(token.trim());
        } catch (RuntimeException ignored) {
            return fallback;
        }
    }
}
