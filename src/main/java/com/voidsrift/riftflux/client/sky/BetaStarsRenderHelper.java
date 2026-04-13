package com.voidsrift.riftflux.client.sky;

import com.voidsrift.riftflux.ModConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public final class BetaStarsRenderHelper {
    private static final int DEFAULT_STAR_COUNT = 1500;
    private static final float SUNSET_FADE_IN_MAX_BRIGHTNESS = 0.45F;
    private static final float SUNSET_RED_TINT_THRESHOLD = 0.45F;
    private static boolean renderedThisSkyPass;
    private static boolean betterSkiesReflectionInitialized;
    private static Field betterSkiesLayerTextureField;
    private static Field betterSkiesLayerPropertiesField;
    private static Method propertiesGetResourceMethod;
    private static Method propertiesEntrySetMethod;

    private BetaStarsRenderHelper() {
    }

    public static int resolvePreferredStarCount() {
        int configuredCount = ModConfig.betaStarsCount;
        if (configuredCount > 0) {
            return configuredCount;
        }
        return configuredCount == 0 ? 0 : DEFAULT_STAR_COUNT;
    }

    public static boolean shouldRenderBetaStars(Minecraft mc) {
        return shouldRenderBetaStars(mc, 0.0F);
    }

    public static boolean shouldRenderBetaStars(Minecraft mc, float partialTicks) {
        if (!ModConfig.betaStarsEnabled || mc == null || mc.theWorld == null || mc.theWorld.provider == null) {
            return false;
        }
        return !mc.theWorld.provider.hasNoSky;
    }

    public static float computeStarBrightness(Minecraft mc, float partialTicks) {
        if (!shouldRenderBetaStars(mc, partialTicks)) {
            return 0.0F;
        }

        float rainFade = 1.0F - mc.theWorld.getRainStrength(partialTicks);
        float baseBrightness = clamp01(mc.theWorld.getStarBrightness(partialTicks) * rainFade);
        float sunsetFadeIn = computeSunsetFadeInBrightness(mc, partialTicks) * rainFade;
        float brightness = Math.max(baseBrightness, sunsetFadeIn);
        return clamp01(brightness * computeSunsetVisibilityGate(mc, partialTicks));
    }

    public static void renderBetaStars(int starCount, float baseBrightness, float partialTicks) {
        if (starCount <= 0 || baseBrightness <= 0.0F) {
            return;
        }

        Random random = new Random(10842L);
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        double sizeMultiplier = Math.max(0.1D, (double) ModConfig.betaStarsSizeMultiplier);
        float time = resolveRenderTime(partialTicks);

        for (int i = 0; i < starCount; ++i) {
            double d0 = (double) (random.nextFloat() * 2.0F - 1.0F);
            double d1 = (double) (random.nextFloat() * 2.0F - 1.0F);
            double d2 = (double) (random.nextFloat() * 2.0F - 1.0F);
            double d3 = (0.35D + (double) (random.nextFloat() * 0.25F)) * sizeMultiplier;
            double d4 = d0 * d0 + d1 * d1 + d2 * d2;

            if (d4 < 1.0D && d4 > 0.01D) {
                d4 = 1.0D / Math.sqrt(d4);
                d0 *= d4;
                d1 *= d4;
                d2 *= d4;
                double d5 = d0 * 100.0D;
                double d6 = d1 * 100.0D;
                double d7 = d2 * 100.0D;
                double d8 = Math.atan2(d0, d2);
                double d9 = Math.sin(d8);
                double d10 = Math.cos(d8);
                double d11 = Math.atan2(Math.sqrt(d0 * d0 + d2 * d2), d1);
                double d12 = Math.sin(d11);
                double d13 = Math.cos(d11);
                double d14 = random.nextDouble() * Math.PI * 2.0D;
                double d15 = Math.sin(d14);
                double d16 = Math.cos(d14);
                float starAlpha = clamp01(baseBrightness * resolveBlinkFactor(random, time));
                if (starAlpha <= 0.003F) {
                    continue;
                }
                tessellator.setColorRGBA_F(1.0F, 1.0F, 1.0F, starAlpha);

                for (int j = 0; j < 4; ++j) {
                    double d17 = 0.0D;
                    double d18 = (double) ((j & 2) - 1) * d3;
                    double d19 = (double) ((j + 1 & 2) - 1) * d3;
                    double d20 = d18 * d16 - d19 * d15;
                    double d21 = d19 * d16 + d18 * d15;
                    double d22 = d20 * d12 + d17 * d13;
                    double d23 = d17 * d12 - d20 * d13;
                    double d24 = d23 * d9 - d21 * d10;
                    double d25 = d21 * d9 + d23 * d10;
                    tessellator.addVertex(d5 + d24, d6 + d22, d7 + d25);
                }
            }
        }

        tessellator.draw();
    }

    public static void beginSkyRenderPass() {
        renderedThisSkyPass = false;
    }

    public static boolean hasRenderedThisSkyPass() {
        return renderedThisSkyPass;
    }

    public static void markRenderedThisSkyPass() {
        renderedThisSkyPass = true;
    }

    public static boolean shouldSuppressBetterSkiesStarLayer(Object layer) {
        if (!ModConfig.betaStarsEnabled || !ModConfig.betaStarsDisableBetterSkiesStars || layer == null) {
            return false;
        }

        initializeBetterSkiesReflection(layer.getClass());
        if (betterSkiesLayerTextureField == null && betterSkiesLayerPropertiesField == null) {
            return false;
        }

        try {
            if (looksLikeStarResource(stringifyResource(betterSkiesLayerTextureField == null ? null : betterSkiesLayerTextureField.get(layer)))) {
                return true;
            }
            Object properties = betterSkiesLayerPropertiesField == null ? null : betterSkiesLayerPropertiesField.get(layer);
            return properties != null && looksLikeStarProperties(properties);
        } catch (IllegalAccessException ignored) {
            return false;
        }
    }

    private static void initializeBetterSkiesReflection(Class<?> layerClass) {
        if (betterSkiesReflectionInitialized) {
            return;
        }
        betterSkiesReflectionInitialized = true;

        try {
            betterSkiesLayerTextureField = layerClass.getDeclaredField("texture");
            betterSkiesLayerTextureField.setAccessible(true);
        } catch (Throwable ignored) {
            betterSkiesLayerTextureField = null;
        }

        try {
            betterSkiesLayerPropertiesField = layerClass.getDeclaredField("properties");
            betterSkiesLayerPropertiesField.setAccessible(true);
        } catch (Throwable ignored) {
            betterSkiesLayerPropertiesField = null;
        }

        if (betterSkiesLayerPropertiesField == null) {
            return;
        }

        try {
            Class<?> propertiesClass = betterSkiesLayerPropertiesField.getType();
            propertiesGetResourceMethod = propertiesClass.getMethod("getResource");
            propertiesEntrySetMethod = propertiesClass.getMethod("entrySet");
        } catch (Throwable ignored) {
            propertiesGetResourceMethod = null;
            propertiesEntrySetMethod = null;
        }
    }

    private static boolean looksLikeStarProperties(Object properties) {
        if (properties == null) {
            return false;
        }

        try {
            if (looksLikeStarResource(stringifyResource(propertiesGetResourceMethod == null ? null : propertiesGetResourceMethod.invoke(properties)))) {
                return true;
            }
            Object entries = propertiesEntrySetMethod == null ? null : propertiesEntrySetMethod.invoke(properties);
            if (!(entries instanceof Set)) {
                return false;
            }
            for (Object entryObj : (Set<?>) entries) {
                if (!(entryObj instanceof Map.Entry)) {
                    continue;
                }
                Map.Entry<?, ?> entry = (Map.Entry<?, ?>) entryObj;
                String key = entry.getKey() == null ? "" : entry.getKey().toString().toLowerCase(Locale.ROOT);
                String value = entry.getValue() == null ? "" : entry.getValue().toString();
                if ("source".equals(key) && looksLikeStarResource(value)) {
                    return true;
                }
                if (("texture".equals(key) || "resource".equals(key)) && looksLikeStarResource(value)) {
                    return true;
                }
            }
        } catch (Throwable ignored) {
            return false;
        }

        return false;
    }

    private static boolean looksLikeStarResource(Object resourceObj) {
        String value = stringifyResource(resourceObj);
        if (value == null || value.isEmpty()) {
            return false;
        }
        String normalized = value.toLowerCase(Locale.ROOT);
        return normalized.contains("star")
                || normalized.contains("constellation")
                || normalized.contains("starfield");
    }

    private static String stringifyResource(Object resourceObj) {
        if (resourceObj == null) {
            return null;
        }
        if (resourceObj instanceof ResourceLocation) {
            return ((ResourceLocation) resourceObj).toString();
        }
        return resourceObj.toString();
    }

    private static float computeSunsetFadeInBrightness(Minecraft mc, float partialTicks) {
        if (mc == null || mc.theWorld == null || mc.theWorld.provider == null) {
            return 0.0F;
        }

        float timeOfDay = getTimeOfDay(mc, partialTicks);
        int fadeStart = ModConfig.betaStarsSunsetFadeStartTick;
        int fadeEnd = Math.max(fadeStart + 1, ModConfig.betaStarsSunsetFadeEndTick);
        if (timeOfDay < (float) fadeStart || timeOfDay >= (float) fadeEnd) {
            return 0.0F;
        }

        float[] sunrise = mc.theWorld.provider.calcSunriseSunsetColors(
                mc.theWorld.getCelestialAngle(partialTicks),
                partialTicks
        );
        if (sunrise == null || sunrise.length < 4) {
            return 0.0F;
        }

        float redTintFade = clamp01((clamp01(sunrise[3]) - SUNSET_RED_TINT_THRESHOLD) / (1.0F - SUNSET_RED_TINT_THRESHOLD));
        float horizonFade = smoothstep(clamp01((timeOfDay - (float) fadeStart) / (float) (fadeEnd - fadeStart)));
        float fade = redTintFade * horizonFade;
        return fade * fade * SUNSET_FADE_IN_MAX_BRIGHTNESS;
    }

    private static float computeSunsetVisibilityGate(Minecraft mc, float partialTicks) {
        float timeOfDay = getTimeOfDay(mc, partialTicks);
        int fadeStart = ModConfig.betaStarsSunsetFadeStartTick;
        int fadeEnd = Math.max(fadeStart + 1, ModConfig.betaStarsSunsetFadeEndTick);
        if (timeOfDay >= 12000.0F && timeOfDay < (float) fadeStart) {
            return 0.0F;
        }
        if (timeOfDay >= (float) fadeStart && timeOfDay < (float) fadeEnd) {
            return smoothstep(clamp01((timeOfDay - (float) fadeStart) / (float) (fadeEnd - fadeStart)));
        }
        return 1.0F;
    }

    private static float getTimeOfDay(Minecraft mc, float partialTicks) {
        if (mc == null || mc.theWorld == null) {
            return 0.0F;
        }
        float timeOfDay = (float) (mc.theWorld.getWorldTime() % 24000L) + partialTicks;
        if (timeOfDay < 0.0F) {
            timeOfDay += 24000.0F;
        }
        if (timeOfDay >= 24000.0F) {
            timeOfDay -= 24000.0F;
        }
        return timeOfDay;
    }

    private static float resolveRenderTime(float partialTicks) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc == null || mc.theWorld == null) {
            return partialTicks;
        }
        return (float) mc.theWorld.getTotalWorldTime() + partialTicks;
    }

    private static float resolveBlinkFactor(Random random, float time) {
        if (!ModConfig.betaStarsRandomBlink) {
            return 1.0F;
        }

        float phase = random.nextFloat() * ((float) Math.PI * 2.0F);
        float speedMultiplier = Math.max(0.05F, ModConfig.betaStarsTwinkleSpeedMultiplier);
        float speed = (0.055F + random.nextFloat() * 0.115F) * speedMultiplier;
        float floor = 0.38F + random.nextFloat() * 0.28F;
        float slowWave = 0.5F + 0.5F * (float) Math.sin(time * speed + phase);
        float shimmerWave = 0.5F + 0.5F * (float) Math.sin(time * speed * (2.1F + random.nextFloat() * 1.7F) + phase * 1.73F);
        float shimmer = slowWave * shimmerWave;
        if (random.nextFloat() < 0.08F) {
            float accent = 0.5F + 0.5F * (float) Math.sin(time * speed * 2.4F + phase * 2.9F);
            shimmer = Math.max(shimmer, accent * accent);
        }
        return clamp01(floor + (1.0F - floor) * shimmer);
    }

    private static float smoothstep(float value) {
        float t = clamp01(value);
        return t * t * (3.0F - 2.0F * t);
    }

    private static float clamp01(float value) {
        if (value < 0.0F) {
            return 0.0F;
        }
        if (value > 1.0F) {
            return 1.0F;
        }
        return value;
    }
}
