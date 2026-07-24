package com.voidsrift.riftflux.client.sky;

import com.voidsrift.riftflux.ModConfig;
import net.minecraft.client.multiplayer.WorldClient;

public final class CloudTimeFadeHelper {
    private static final double DAY_TICKS = 24000.0D;

    private CloudTimeFadeHelper() {
    }

    public static float getOpacity(WorldClient world, float partialTicks) {
        if (!ModConfig.celestialCloudTimeFade || world == null) {
            return 1.0F;
        }

        double timeOfDay = positiveModulo((double) world.getWorldTime() + (double) partialTicks, DAY_TICKS);
        double sinceFadeOut = positiveModulo(timeOfDay - ModConfig.celestialCloudFadeOutTime, DAY_TICKS);
        double sinceFadeIn = positiveModulo(timeOfDay - ModConfig.celestialCloudFadeInTime, DAY_TICKS);

        if (sinceFadeOut < sinceFadeIn) {
            return 1.0F - fadeProgress(sinceFadeOut, ModConfig.celestialCloudFadeOutDuration);
        }
        return fadeProgress(sinceFadeIn, ModConfig.celestialCloudFadeInDuration);
    }

    private static float fadeProgress(double elapsed, int duration) {
        if (duration <= 0) {
            return 1.0F;
        }
        double progress = elapsed / (double) duration;
        if (progress <= 0.0D) {
            return 0.0F;
        }
        if (progress >= 1.0D) {
            return 1.0F;
        }
        float value = (float) progress;
        return value * value * (3.0F - 2.0F * value);
    }

    private static double positiveModulo(double value, double modulus) {
        double result = value % modulus;
        return result < 0.0D ? result + modulus : result;
    }
}
