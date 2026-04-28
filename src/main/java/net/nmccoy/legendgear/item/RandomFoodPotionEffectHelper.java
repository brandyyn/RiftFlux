package net.nmccoy.legendgear.item;

import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class RandomFoodPotionEffectHelper {
    private RandomFoodPotionEffectHelper() {
    }

    public static PotionEffect getRandomConfiguredEffect(World world, String[] entries) {
        if (world == null) {
            return null;
        }
        List<PotionEffect> effects = buildConfiguredEffects(entries);
        if (effects.isEmpty()) {
            return null;
        }
        return effects.get(world.rand.nextInt(effects.size()));
    }

    static List<PotionEffect> buildConfiguredEffects(String[] entries) {
        List<PotionEffect> effects = new ArrayList<PotionEffect>();
        if (entries == null) {
            return effects;
        }

        for (String entry : entries) {
            if (entry == null) {
                continue;
            }
            String raw = entry.trim();
            if (raw.isEmpty()) {
                continue;
            }
            String[] parts = raw.split("\\s*,\\s*");
            if (parts.length < 3) {
                continue;
            }

            int potionId = parsePotionId(parts[0]);
            if (potionId < 0 || potionId >= Potion.potionTypes.length || Potion.potionTypes[potionId] == null) {
                continue;
            }

            int level = Math.max(1, parseIntSafe(parts[1], 1));
            int durationTicks = Potion.potionTypes[potionId].isInstant() ? 1 : parseDurationTicks(parts[2]);
            if (durationTicks <= 0) {
                continue;
            }

            effects.add(new PotionEffect(potionId, durationTicks, level - 1, true));
        }

        return effects;
    }

    private static int parsePotionId(String token) {
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
            String simple = potion.getName().toLowerCase(Locale.ROOT);
            if (simple.startsWith("potion.")) {
                simple = simple.substring("potion.".length());
            }
            if (simple.equals(normalized)) {
                return potion.id;
            }
        }
        return -1;
    }

    private static int parseDurationTicks(String token) {
        if (token == null) {
            return 0;
        }
        String trimmed = token.trim().toLowerCase(Locale.ROOT);
        boolean seconds = trimmed.contains(".") || trimmed.endsWith("s");
        if (trimmed.endsWith("s")) {
            trimmed = trimmed.substring(0, trimmed.length() - 1);
        }
        float value;
        try {
            value = Float.parseFloat(trimmed);
        } catch (NumberFormatException ignored) {
            return 0;
        }
        if (seconds) {
            return Math.max(1, Math.round(value * 20.0F));
        }
        return Math.max(1, Math.round(value));
    }

    private static int parseIntSafe(String token, int fallback) {
        if (token == null) {
            return fallback;
        }
        try {
            return Integer.parseInt(token.trim());
        } catch (NumberFormatException ignored) {
            return fallback;
        }
    }
}
