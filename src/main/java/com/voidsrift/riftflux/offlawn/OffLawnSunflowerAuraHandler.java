package com.voidsrift.riftflux.offlawn;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.util.ConfiguredPotionEffectHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import java.util.Collections;
import java.util.List;

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
            for (PotionEffect effect : ConfiguredPotionEffectHelper.parseEffects(effects)) {
                shortestDuration = Math.min(shortestDuration, effect.getDuration());
            }
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
        List<PotionEffect> effects = auraEnabled
                ? ConfiguredPotionEffectHelper.parseEffects(bright
                        ? ModConfig.offLawnBrightSunflowerAuraEffects
                        : ModConfig.offLawnSunflowerAuraEffects)
                : Collections.<PotionEffect>emptyList();

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
                applyPotionEffects(player, effects);
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

    private static void applyPotionEffects(EntityPlayer player, List<PotionEffect> effects) {
        for (PotionEffect effect : effects) {
            int safeDuration = Math.max(1, effect.getDuration());
            Potion potion = Potion.potionTypes[effect.getPotionID()];
            PotionEffect active = player.getActivePotionEffect(potion);
            if (active != null && active.getAmplifier() == effect.getAmplifier()
                    && active.getDuration() > Math.min(safeDuration - 1, MAX_REFRESH_TICKS)) {
                continue;
            }
            player.addPotionEffect(new PotionEffect(
                    effect.getPotionID(),
                    safeDuration,
                    effect.getAmplifier(),
                    true
            ));
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
}
