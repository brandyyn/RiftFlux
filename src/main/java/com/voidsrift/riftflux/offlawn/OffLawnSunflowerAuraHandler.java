package com.voidsrift.riftflux.offlawn;

import com.voidsrift.riftflux.ModConfig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public final class OffLawnSunflowerAuraHandler {
    private static final int MIN_REFRESH_TICKS = 10;
    private static final int MAX_REFRESH_TICKS = 40;
    public static int getRefreshIntervalTicks(boolean bright) {
        int shortestDuration = Integer.MAX_VALUE;
        String[] effects = bright
                ? ModConfig.offLawnBrightSunflowerAuraEffects
                : ModConfig.offLawnSunflowerAuraEffects;
        boolean auraEnabled = bright
                ? ModConfig.offLawnBrightSunflowerAuraEnabled
                : ModConfig.offLawnSunflowerAuraEnabled;
        if (auraEnabled && hasEntries(effects)) {
            shortestDuration = (bright
                    ? ModConfig.offLawnBrightSunflowerAuraDurationSeconds
                    : ModConfig.offLawnSunflowerAuraDurationSeconds) * 20;
        }
        if (bright && ModConfig.offLawnBrightSunflowerSpeedBoostEnabled
                && ModConfig.offLawnBrightSunflowerSpeedBoostPercent > 0.0F) {
            shortestDuration = Math.min(shortestDuration,
                    ModConfig.offLawnBrightSunflowerSpeedBoostDurationSeconds * 20);
        }
        if (shortestDuration == Integer.MAX_VALUE) {
            return 0;
        }
        return Math.max(MIN_REFRESH_TICKS, Math.min(MAX_REFRESH_TICKS, shortestDuration / 2));
    }

    public static void refreshAura(World world, int x, int y, int z, boolean bright) {
        if (world == null || world.isRemote) {
            return;
        }

        boolean auraEnabled = bright
                ? ModConfig.offLawnBrightSunflowerAuraEnabled
                : ModConfig.offLawnSunflowerAuraEnabled;
        float auraRadius = bright
                ? ModConfig.offLawnBrightSunflowerAuraRadius
                : ModConfig.offLawnSunflowerAuraRadius;
        int auraDuration = (bright
                ? ModConfig.offLawnBrightSunflowerAuraDurationSeconds
                : ModConfig.offLawnSunflowerAuraDurationSeconds) * 20;
        List<AuraEffect> effects = auraEnabled
                ? parseEffects(bright
                        ? ModConfig.offLawnBrightSunflowerAuraEffects
                        : ModConfig.offLawnSunflowerAuraEffects)
                : Collections.<AuraEffect>emptyList();

        boolean speedEnabled = bright
                && ModConfig.offLawnBrightSunflowerSpeedBoostEnabled
                && ModConfig.offLawnBrightSunflowerSpeedBoostPercent > 0.0F;
        float speedRadius = speedEnabled ? ModConfig.offLawnBrightSunflowerSpeedBoostRadius : 0.0F;
        float maxRadius = Math.max(effects.isEmpty() ? 0.0F : auraRadius, speedRadius);
        if (maxRadius <= 0.0F) {
            return;
        }

        double centerX = x + 0.5D;
        double centerY = y + 1.0D;
        double centerZ = z + 0.5D;
        AxisAlignedBB bounds = AxisAlignedBB.getBoundingBox(
                centerX - maxRadius, centerY - maxRadius, centerZ - maxRadius,
                centerX + maxRadius, centerY + maxRadius, centerZ + maxRadius);
        List<EntityPlayer> players = world.getEntitiesWithinAABB(EntityPlayer.class, bounds);
        double auraRadiusSq = (double) auraRadius * auraRadius;
        double speedRadiusSq = (double) speedRadius * speedRadius;

        for (EntityPlayer player : players) {
            if (player == null || !player.isEntityAlive()) {
                continue;
            }
            double dx = player.posX - centerX;
            double dy = player.posY + player.height * 0.5D - centerY;
            double dz = player.posZ - centerZ;
            double distanceSq = dx * dx + dy * dy + dz * dz;
            if (!effects.isEmpty() && distanceSq <= auraRadiusSq) {
                applyPotionEffects(player, effects, auraDuration);
            }
            if (speedEnabled && distanceSq <= speedRadiusSq) {
                refreshHappyEffect(player, ModConfig.offLawnBrightSunflowerSpeedBoostDurationSeconds * 20);
            }
        }
    }

    private static void refreshHappyEffect(EntityPlayer player, int durationTicks) {
        Potion happy = OffLawnPotions.happy;
        if (happy == null) {
            return;
        }
        int safeDuration = Math.max(20, durationTicks);
        PotionEffect active = player.getActivePotionEffect(happy);
        if (active != null && active.getDuration() > Math.min(safeDuration - 1, MAX_REFRESH_TICKS)) {
            return;
        }
        player.addPotionEffect(new PotionEffect(happy.id, safeDuration, 0, true));
    }

    private static void applyPotionEffects(EntityPlayer player, List<AuraEffect> effects, int durationTicks) {
        int safeDuration = Math.max(20, durationTicks);
        for (AuraEffect effect : effects) {
            Potion potion = Potion.potionTypes[effect.potionId];
            PotionEffect active = player.getActivePotionEffect(potion);
            if (active != null && active.getAmplifier() == effect.amplifier
                    && active.getDuration() > Math.min(safeDuration - 1, MAX_REFRESH_TICKS)) {
                continue;
            }
            player.addPotionEffect(new PotionEffect(effect.potionId, safeDuration, effect.amplifier, true));
        }
    }

    private static List<AuraEffect> parseEffects(String[] entries) {
        if (!hasEntries(entries)) {
            return Collections.emptyList();
        }
        List<AuraEffect> effects = new ArrayList<AuraEffect>();
        for (String entry : entries) {
            if (entry == null) {
                continue;
            }
            String[] parts = entry.trim().split("\\s*,\\s*");
            if (parts.length < 2) {
                continue;
            }
            int potionId = parsePotionId(parts[0]);
            int amplifier = parseInt(parts[1], 0);
            if (potionId >= 0 && potionId < Potion.potionTypes.length
                    && Potion.potionTypes[potionId] != null) {
                effects.add(new AuraEffect(potionId, Math.max(0, amplifier)));
            }
        }
        return effects;
    }

    private static int parsePotionId(String token) {
        if (token == null) {
            return -1;
        }
        String value = token.trim();
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ignored) {
        }
        String normalized = value.toLowerCase(Locale.ROOT);
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

    private static int parseInt(String value, int fallback) {
        try {
            return Integer.parseInt(value.trim());
        } catch (RuntimeException ignored) {
            return fallback;
        }
    }

    private static boolean hasEntries(String[] entries) {
        if (entries == null) {
            return false;
        }
        for (String entry : entries) {
            if (entry != null && !entry.trim().isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private static final class AuraEffect {
        private final int potionId;
        private final int amplifier;

        private AuraEffect(int potionId, int amplifier) {
            this.potionId = potionId;
            this.amplifier = amplifier;
        }
    }
}
