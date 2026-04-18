package com.voidsrift.riftflux.client.sky;

import com.voidsrift.riftflux.ModConfig;
import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.Vec3;

public final class SunriseSkyTintHelper {
    private static final float DEFAULT_BETA_FOG_TINT_STRENGTH = 0.38F;
    private static final float DEFAULT_SKY_MATCH_TOLERANCE = 0.08F;
    private static final float NIGHT_FOG_MIN_BRIGHTNESS = 0.009F;
    private static final float NIGHT_FOG_DESATURATION_BRIGHTNESS_RANGE = 0.011F;
    private static final int EVENT_FADE_TICKS = 1200;
    private static final int DAY_EVENT_START_TICK = 23000;
    private static final int DAY_EVENT_END_TICK = 13000;
    private static final int NIGHT_EVENT_START_TICK = 12000;
    private static final int NIGHT_EVENT_END_TICK = 24000;
    private static final float[] DEFAULT_OVERWORLD_SKY = toRgbFloats(Color.getHSBColor(0.62222224F, 0.5F, 1.0F).getRGB());

    private SunriseSkyTintHelper() {
    }

    public static float[] blendSkyTint(WorldClient world, Minecraft mc, float partialTicks, float red, float green, float blue) {
        if (!ModConfig.celestialFullSunriseSunsetTint || world == null || mc == null || world.provider == null) {
            return new float[] { red, green, blue };
        }

        float[] sunrise = getSunriseSunsetColors(world, partialTicks);
        if (sunrise == null || sunrise.length < 4) {
            return new float[] { red, green, blue };
        }

        float blend = clamp01(sunrise[3]);
        if (blend <= 0.0F) {
            return new float[] { red, green, blue };
        }

        float tintRed = sunrise[0];
        float tintGreen = sunrise[1];
        float tintBlue = sunrise[2];
        if (mc.gameSettings != null && mc.gameSettings.anaglyph) {
            float tintGray = (tintRed * 30.0F + tintGreen * 59.0F + tintBlue * 11.0F) / 100.0F;
            float tintYellow = (tintRed * 30.0F + tintGreen * 70.0F) / 100.0F;
            float tintBlueMix = (tintRed * 30.0F + tintBlue * 70.0F) / 100.0F;
            tintRed = tintGray;
            tintGreen = tintYellow;
            tintBlue = tintBlueMix;
        }

        return new float[] {
                red * (1.0F - blend) + tintRed * blend,
                green * (1.0F - blend) + tintGreen * blend,
                blue * (1.0F - blend) + tintBlue * blend
        };
    }

    public static boolean shouldForceFullSunriseTint(WorldClient world, float partialTicks) {
        return ModConfig.celestialFullSunriseSunsetTint && isSunriseOrSunsetActive(world, partialTicks);
    }

    public static boolean shouldMatchFogToSky(WorldClient world) {
        return !shouldUseBetaStyleBiomeFog(world)
                && getSkyMatchingFogStrength(world, 1.0F) > 0.001F
                && world != null
                && world.provider != null
                && !world.provider.hasNoSky;
    }

    public static boolean shouldUseBetaStyleBiomeFog(WorldClient world) {
        return getBetaStyleBiomeFogStrength(world, 1.0F) > 0.001F
                && world != null
                && world.provider != null
                && !world.provider.hasNoSky;
    }

    public static boolean shouldUseBetaStyleBiomeFogWeatherEvent(WorldClient world) {
        return getBetaStyleBiomeFogWeatherEventStrength(world, 1.0F) > 0.001F;
    }

    public static float getBetaStyleBiomeFogStrength(WorldClient world, float partialTicks) {
        if (world == null || world.provider == null || world.provider.hasNoSky) {
            return 0.0F;
        }
        if (ModConfig.celestialBetaStyleFogBiomeTint) {
            return 1.0F;
        }
        if (ModConfig.celestialFogChanceEventsUseSkyMatchingFog) {
            return 0.0F;
        }

        return getFogChanceEventStrength(world, partialTicks);
    }

    public static float getSkyMatchingFogStrength(WorldClient world, float partialTicks) {
        if (world == null || world.provider == null || world.provider.hasNoSky) {
            return 0.0F;
        }
        if (ModConfig.celestialBetaStyleFogBiomeTint) {
            return 0.0F;
        }
        if (ModConfig.celestialFogMatchesSky) {
            return 1.0F;
        }
        if (!ModConfig.celestialFogChanceEventsUseSkyMatchingFog) {
            return 0.0F;
        }
        return getFogChanceEventStrength(world, partialTicks);
    }

    public static float getFogChanceEventStrength(WorldClient world, float partialTicks) {
        if (world == null || world.provider == null || world.provider.hasNoSky) {
            return 0.0F;
        }
        if (!CelestialFogEventClientState.hasSync(world)) {
            return 0.0F;
        }

        float weather = getBetaStyleBiomeFogWeatherEventStrength(world, partialTicks);
        float daytime = getSyncedTimeFogEventStrength(
                world,
                partialTicks,
                DAY_EVENT_START_TICK,
                DAY_EVENT_END_TICK,
                CelestialFogEventClientState.isDayFogActive(world)
        );
        float night = getSyncedTimeFogEventStrength(
                world,
                partialTicks,
                NIGHT_EVENT_START_TICK,
                NIGHT_EVENT_END_TICK,
                CelestialFogEventClientState.isNightFogActive(world)
        );
        return clamp01(Math.max(weather, Math.max(daytime, night)));
    }

    public static float getBetaStyleBiomeFogWeatherEventStrength(WorldClient world, float partialTicks) {
        if (!CelestialFogEventClientState.isWeatherFogActive(world)
                || world == null
                || world.provider == null
                || world.provider.hasNoSky) {
            return 0.0F;
        }

        float weatherStrength = clamp01(Math.max(world.getRainStrength(partialTicks), world.getWeightedThunderStrength(partialTicks)));
        if (weatherStrength <= 0.001F) {
            return 0.0F;
        }

        return smoothStep(weatherStrength);
    }

    public static float[] resolveRawSkyColor(WorldClient world, Minecraft mc, float partialTicks) {
        if (world == null || mc == null || mc.renderViewEntity == null) {
            return null;
        }

        Vec3 sky = world.getSkyColor(mc.renderViewEntity, partialTicks);
        if (sky == null) {
            return null;
        }

        return new float[] { (float) sky.xCoord, (float) sky.yCoord, (float) sky.zCoord };
    }

    public static float[] resolveSkyTintedColor(WorldClient world, Minecraft mc, float partialTicks) {
        float[] rawSky = resolveRawSkyColor(world, mc, partialTicks);
        if (rawSky == null) {
            return null;
        }

        return blendSkyTint(world, mc, partialTicks, rawSky[0], rawSky[1], rawSky[2]);
    }

    public static float[] resolveFogSkyColor(WorldClient world, Minecraft mc, float partialTicks) {
        float[] sunriseOverride = resolveSunriseSunsetFogOverride(world, mc, partialTicks);
        if (sunriseOverride != null) {
            return sunriseOverride;
        }
        // Outside sunrise/sunset, fog stays close to vanilla sky fog instead of inheriting the full sky overlay.
        return resolveRawSkyColor(world, mc, partialTicks);
    }

    public static float[] resolveBetaStyleBiomeFogColor(WorldClient world, Minecraft mc, float partialTicks) {
        float[] sunriseOverride = resolveSunriseSunsetFogOverride(world, mc, partialTicks);
        if (sunriseOverride != null) {
            return sunriseOverride;
        }

        float[] rawSky = resolveRawSkyColor(world, mc, partialTicks);
        if (rawSky == null) {
            return null;
        }
        float[] tintSource = rawSky;

        float luminance = clamp01(rawSky[0] * 0.299F + rawSky[1] * 0.587F + rawSky[2] * 0.114F);
        float betaBrightness = clamp01(0.76F + luminance * 0.12F);
        float[] betaBase = new float[] {
                clamp01(betaBrightness * 0.96F),
                clamp01(betaBrightness * 0.985F),
                clamp01(betaBrightness * 1.035F)
        };
        float[] pastelSky = new float[] {
                clamp01(tintSource[0] * 0.25F + betaBrightness * 0.75F),
                clamp01(tintSource[1] * 0.25F + betaBrightness * 0.75F),
                clamp01(tintSource[2] * 0.25F + betaBrightness * 0.75F)
        };
        if (isCloseToDefaultSky(rawSky)) {
            return mixColors(betaBase, pastelSky, 0.16F);
        }

        return mixColors(betaBase, pastelSky, DEFAULT_BETA_FOG_TINT_STRENGTH);
    }

    public static float[] resolveBetaStyleLowerSkyColor(WorldClient world, Minecraft mc, float partialTicks) {
        float[] fogTint = resolveBetaStyleBiomeFogColor(world, mc, partialTicks);
        float[] skyTint = resolveFogSkyColor(world, mc, partialTicks);
        if (fogTint == null) {
            return skyTint;
        }
        if (skyTint == null) {
            return fogTint;
        }
        return mixColors(skyTint, fogTint, 0.32F);
    }

    public static float[] resolveEffectiveBetaStyleFogColor(WorldClient world, Minecraft mc, float partialTicks, float[] fallback) {
        float strength = getBetaStyleBiomeFogStrength(world, partialTicks);
        if (strength <= 0.001F) {
            return fallback == null ? null : copyClamped(fallback);
        }

        float[] betaFog = resolveBetaStyleLowerSkyColor(world, mc, partialTicks);
        if (betaFog == null || betaFog.length < 3) {
            return fallback == null ? null : copyClamped(fallback);
        }
        betaFog = applyConfiguredFogDesaturation(world, mc, partialTicks, betaFog);
        betaFog = applyNightFogFloor(world, partialTicks, betaFog);

        if (strength >= 0.999F) {
            return betaFog;
        }

        float[] baseFog = resolveNonBetaFogFallback(world, mc, partialTicks, fallback);
        if (baseFog == null || baseFog.length < 3) {
            baseFog = resolveFogSkyColor(world, mc, partialTicks);
        }
        if (baseFog == null || baseFog.length < 3) {
            return betaFog;
        }

        return mixColors(copyClamped(baseFog), betaFog, strength);
    }

    public static float[] resolveEffectiveSkyMatchingFogColor(WorldClient world, Minecraft mc, float partialTicks, float[] fallback) {
        float strength = getSkyMatchingFogStrength(world, partialTicks);
        if (strength <= 0.001F) {
            return fallback == null ? null : copyClamped(fallback);
        }

        float[] skyFog = resolveFogSkyColor(world, mc, partialTicks);
        if (skyFog == null || skyFog.length < 3) {
            return fallback == null ? null : copyClamped(fallback);
        }
        skyFog = applyConfiguredFogDesaturation(world, mc, partialTicks, skyFog);
        skyFog = applyNightFogFloor(world, partialTicks, skyFog);

        if (strength >= 0.999F) {
            return skyFog;
        }

        float[] baseFog = resolveNonBetaFogFallback(world, mc, partialTicks, fallback);
        if (baseFog == null || baseFog.length < 3) {
            return skyFog;
        }

        return mixColors(copyClamped(baseFog), skyFog, strength);
    }

    public static float[] resolveNonBetaFogFallback(WorldClient world, Minecraft mc, float partialTicks, float[] fallback) {
        if (world == null || world.provider == null || world.provider.hasNoSky) {
            return fallback == null ? null : copyClamped(fallback);
        }

        if (ModConfig.celestialFogMatchesSky) {
            float[] skyTint = resolveFogSkyColor(world, mc, partialTicks);
            if (skyTint != null && skyTint.length >= 3) {
                skyTint = applyConfiguredFogDesaturation(world, mc, partialTicks, skyTint);
                return applyNightFogFloor(world, partialTicks, skyTint);
            }
        }

        if (shouldUseBlackNightFogIgnoringBeta(world, partialTicks)) {
            return resolveBlackNightFogColorRaw(world, mc, partialTicks);
        }

        return fallback == null ? null : copyClamped(fallback);
    }

    public static float[] resolveBetaStyleClearColor(WorldClient world, Minecraft mc, float partialTicks) {
        return resolveSkyTintedColor(world, mc, partialTicks);
    }

    public static float[] resolveBetaStyleCloudColor(WorldClient world, Minecraft mc, float partialTicks, float red, float green, float blue) {
        float[] lowerSky = resolveBetaStyleLowerSkyColor(world, mc, partialTicks);
        if (lowerSky == null) {
            return new float[] { red, green, blue };
        }
        return new float[] {
                red * 0.4F + lowerSky[0] * 0.6F,
                green * 0.4F + lowerSky[1] * 0.6F,
                blue * 0.4F + lowerSky[2] * 0.6F
        };
    }

    public static float[] resolveBetaStyleFogDistance(WorldClient world, EntityLivingBase view, float farPlaneDistance, boolean negativeFogMode) {
        if (world == null || view == null) {
            return null;
        }

        float end = farPlaneDistance;
        if (world.provider != null && world.provider.doesXZShowFog((int) view.posX, (int) view.posZ)) {
            end = Math.min(end, 192.0F) * 0.5F;
        }

        float startFactor = negativeFogMode ? 0.0F : 0.0F;
        float endFactor = negativeFogMode ? 0.62F : 0.52F;
        float start = Math.max(0.0F, end * startFactor);
        float finish = Math.max(start + 1.0F, end * endFactor);
        return new float[] { start, finish };
    }

    public static boolean shouldUseDenseFogDistance(WorldClient world, float partialTicks) {
        return getDenseFogDistanceStrength(world, partialTicks) > 0.001F;
    }

    public static float[] resolveDenseFogDistance(WorldClient world, EntityLivingBase view, float farPlaneDistance, boolean negativeFogMode, float partialTicks) {
        float strength = getDenseFogDistanceStrength(world, partialTicks);
        if (strength <= 0.001F) {
            return null;
        }

        float[] dense = resolveBetaStyleFogDistance(world, view, farPlaneDistance, negativeFogMode);
        if (dense == null || dense.length < 2) {
            return dense;
        }
        if (strength >= 0.999F) {
            return dense;
        }

        float[] vanilla = resolveVanillaFogDistance(world, view, farPlaneDistance, negativeFogMode);
        return new float[] {
                vanilla[0] * (1.0F - strength) + dense[0] * strength,
                vanilla[1] * (1.0F - strength) + dense[1] * strength
        };
    }

    public static boolean shouldUseFogDistanceGradient(WorldClient world) {
        return ModConfig.celestialFogDistanceGradient
                && ModConfig.celestialFogDistanceGradientStrengthPercent > 0.0F
                && world != null
                && world.provider != null
                && !world.provider.hasNoSky;
    }

    public static float[] resolveDistanceGradientFarFogColor(WorldClient world, Minecraft mc, float partialTicks, float[] fallback) {
        float[] skyTarget = resolveFogSkyColor(world, mc, partialTicks);
        if (skyTarget == null || skyTarget.length < 3) {
            return fallback == null ? null : copyClamped(fallback);
        }

        return applyConfiguredFogDesaturation(world, mc, partialTicks, skyTarget);
    }

    public static boolean isSunriseOrSunsetActive(WorldClient world, float partialTicks) {
        float[] sunrise = getSunriseSunsetColors(world, partialTicks);
        return sunrise != null && sunrise.length >= 4 && sunrise[3] > 0.0F;
    }

    public static boolean shouldUseBlackNightFog(WorldClient world, float partialTicks) {
        if (shouldUseBetaStyleBiomeFog(world)
                || shouldMatchFogToSky(world)
                || !ModConfig.celestialBlackNightFog
                || world == null
                || world.provider == null
                || world.provider.hasNoSky) {
            return false;
        }
        return shouldUseBlackNightFogIgnoringBeta(world, partialTicks);
    }

    public static float[] resolveBlackNightFogColor(WorldClient world, Minecraft mc, float partialTicks) {
        if (!shouldUseBlackNightFog(world, partialTicks)) {
            return null;
        }
        return resolveBlackNightFogColorRaw(world, mc, partialTicks);
    }

    public static float[] resolveBlackNightFogColorRaw(WorldClient world, Minecraft mc, float partialTicks) {
        float[] skyTint = resolveFogSkyColor(world, mc, partialTicks);
        if (skyTint == null || skyTint.length < 3) {
            float floor = getNightFogMinBrightness();
            return new float[] { floor, floor, floor };
        }

        float floor = getNightFogMinBrightness();
        float[] darkFog = new float[] {
                clamp01(skyTint[0] * 0.085F + floor * 0.915F),
                clamp01(skyTint[1] * 0.085F + floor * 0.915F),
                clamp01(skyTint[2] * 0.085F + floor * 0.915F)
        };
        return applyConfiguredFogDesaturation(world, mc, partialTicks, darkFog);
    }

    public static float[] applyNightFogFloor(WorldClient world, float partialTicks, float[] rgb) {
        if (rgb == null || rgb.length < 3) {
            return rgb;
        }
        if (world == null) {
            return new float[] { clamp01(rgb[0]), clamp01(rgb[1]), clamp01(rgb[2]) };
        }
        if (isSunriseOrSunsetActive(world, partialTicks)) {
            return new float[] { clamp01(rgb[0]), clamp01(rgb[1]), clamp01(rgb[2]) };
        }
        long timeOfDay = world.getWorldTime() % 24000L;
        if (timeOfDay < 0L) {
            timeOfDay += 24000L;
        }
        if (timeOfDay < 13000L || timeOfDay >= 23000L) {
            return new float[] { clamp01(rgb[0]), clamp01(rgb[1]), clamp01(rgb[2]) };
        }

        float floor = getNightFogMinBrightness();
        return new float[] {
                Math.max(clamp01(rgb[0]), floor),
                Math.max(clamp01(rgb[1]), floor),
                Math.max(clamp01(rgb[2]), floor)
        };
    }

    public static float[] applyConfiguredFogDesaturation(float[] rgb) {
        if (rgb == null || rgb.length < 3) {
            return rgb;
        }
        return applyFogDesaturationToGray(rgb, clamp01(ModConfig.celestialFogDesaturationPercent / 100.0F));
    }

    public static float[] applyConfiguredFogDesaturation(WorldClient world, Minecraft mc, float partialTicks, float[] rgb) {
        if (rgb == null || rgb.length < 3) {
            return rgb;
        }
        if (shouldForceFullSunriseTint(world, partialTicks)) {
            return copyClamped(rgb);
        }
        float amount = getConfiguredFogDesaturationAmount(world, partialTicks);
        if (amount <= 0.0F) {
            return new float[] { clamp01(rgb[0]), clamp01(rgb[1]), clamp01(rgb[2]) };
        }

        float[] betaTarget = resolveBetaStyleLowerSkyColor(world, mc, partialTicks);
        if (betaTarget == null || betaTarget.length < 3) {
            return applyFogDesaturationToGray(rgb, amount);
        }

        return mixColors(copyClamped(rgb), copyClamped(betaTarget), amount);
    }

    private static float[] resolveSunriseSunsetFogOverride(WorldClient world, Minecraft mc, float partialTicks) {
        if (!shouldForceFullSunriseTint(world, partialTicks)) {
            return null;
        }

        float[] skyTint = resolveSkyTintedColor(world, mc, partialTicks);
        return skyTint == null || skyTint.length < 3 ? null : copyClamped(skyTint);
    }

    private static float[] getSunriseSunsetColors(WorldClient world, float partialTicks) {
        if (world == null || world.provider == null) {
            return null;
        }
        return world.provider.calcSunriseSunsetColors(world.getCelestialAngle(partialTicks), partialTicks);
    }

    private static boolean shouldUseBlackNightFogIgnoringBeta(WorldClient world, float partialTicks) {
        if (!ModConfig.celestialBlackNightFog
                || ModConfig.celestialFogMatchesSky
                || world == null
                || world.provider == null
                || world.provider.hasNoSky
                || isSunriseOrSunsetActive(world, partialTicks)) {
            return false;
        }

        return isNightFogPeriod(world, partialTicks);
    }

    private static boolean isNightFogPeriod(WorldClient world, float partialTicks) {
        if (world == null) {
            return false;
        }
        long timeOfDay = world.getWorldTime() % 24000L;
        if (timeOfDay < 0L) {
            timeOfDay += 24000L;
        }
        return timeOfDay >= 13000L && timeOfDay < 23000L;
    }

    private static float getDenseFogDistanceStrength(WorldClient world, float partialTicks) {
        if (world == null || world.provider == null || world.provider.hasNoSky) {
            return 0.0F;
        }
        if (ModConfig.celestialBetaStyleFogBiomeTint) {
            return 1.0F;
        }

        float eventStrength = getFogChanceEventStrength(world, partialTicks);
        if (eventStrength > 0.001F) {
            return eventStrength;
        }

        return shouldUseBlackNightFog(world, partialTicks) ? 1.0F : 0.0F;
    }

    public static float[] resolveVanillaFogDistance(WorldClient world, EntityLivingBase view, float farPlaneDistance, boolean negativeFogMode) {
        float[] vanilla = resolveVanillaFogDistance(farPlaneDistance, negativeFogMode);
        if (world != null && world.provider != null && view != null
                && world.provider.doesXZShowFog((int) view.posX, (int) view.posZ)) {
            return new float[] {
                    Math.max(0.0F, farPlaneDistance * 0.05F),
                    Math.max(1.0F, Math.min(farPlaneDistance, 192.0F) * 0.5F)
            };
        }
        return vanilla;
    }

    public static float[] resolveVanillaFogDistance(float farPlaneDistance, boolean negativeFogMode) {
        if (negativeFogMode) {
            return new float[] { 0.0F, Math.max(1.0F, farPlaneDistance * 0.8F) };
        }
        return new float[] {
                Math.max(0.0F, farPlaneDistance * 0.75F),
                Math.max(1.0F, farPlaneDistance)
        };
    }

    private static float getSyncedTimeFogEventStrength(WorldClient world, float partialTicks, int startTick, int endTick, boolean enabled) {
        if (!enabled || world == null) {
            return 0.0F;
        }

        long worldTime = world.getWorldTime();
        double timeOfDay = (double) (worldTime % 24000L) + partialTicks;
        if (timeOfDay < 0.0D) {
            timeOfDay += 24000.0D;
        }

        boolean wraps = startTick > endTick;
        boolean active = wraps
                ? timeOfDay >= (double) startTick || timeOfDay < (double) endTick
                : timeOfDay >= (double) startTick && timeOfDay < (double) endTick;
        if (!active) {
            return 0.0F;
        }

        double duration = wraps ? 24000.0D - (double) startTick + (double) endTick : (double) endTick - (double) startTick;
        double elapsed = wraps && timeOfDay < (double) endTick
                ? timeOfDay + 24000.0D - (double) startTick
                : timeOfDay - (double) startTick;
        double fadeIn = Math.min(1.0D, elapsed / (double) EVENT_FADE_TICKS);
        double fadeOut = Math.min(1.0D, (duration - elapsed) / (double) EVENT_FADE_TICKS);
        return smoothStep((float) Math.min(fadeIn, fadeOut));
    }

    private static float smoothStep(float value) {
        float clamped = clamp01(value);
        return clamped * clamped * (3.0F - 2.0F * clamped);
    }

    private static float getNightFogMinBrightness() {
        float amount = clamp01(ModConfig.celestialNightFogDesaturationPercent / 100.0F);
        return NIGHT_FOG_MIN_BRIGHTNESS + amount * NIGHT_FOG_DESATURATION_BRIGHTNESS_RANGE;
    }

    private static float getConfiguredFogDesaturationAmount(WorldClient world, float partialTicks) {
        if (world != null && isNightFogPeriod(world, partialTicks)) {
            return clamp01(ModConfig.celestialNightFogDesaturationPercent / 100.0F);
        }
        return clamp01(ModConfig.celestialFogDesaturationPercent / 100.0F);
    }

    private static float[] applyFogDesaturationToGray(float[] rgb, float amount) {
        if (amount <= 0.0F) {
            return new float[] { clamp01(rgb[0]), clamp01(rgb[1]), clamp01(rgb[2]) };
        }

        float red = clamp01(rgb[0]);
        float green = clamp01(rgb[1]);
        float blue = clamp01(rgb[2]);
        float gray = clamp01(red * 0.299F + green * 0.587F + blue * 0.114F);
        return new float[] {
                red * (1.0F - amount) + gray * amount,
                green * (1.0F - amount) + gray * amount,
                blue * (1.0F - amount) + gray * amount
        };
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

    private static boolean isCloseToDefaultSky(float[] sky) {
        float dr = sky[0] - DEFAULT_OVERWORLD_SKY[0];
        float dg = sky[1] - DEFAULT_OVERWORLD_SKY[1];
        float db = sky[2] - DEFAULT_OVERWORLD_SKY[2];
        return dr * dr + dg * dg + db * db <= DEFAULT_SKY_MATCH_TOLERANCE * DEFAULT_SKY_MATCH_TOLERANCE;
    }

    private static float[] mixColors(float[] base, float[] tint, float blend) {
        float clampedBlend = clamp01(blend);
        return new float[] {
                base[0] * (1.0F - clampedBlend) + tint[0] * clampedBlend,
                base[1] * (1.0F - clampedBlend) + tint[1] * clampedBlend,
                base[2] * (1.0F - clampedBlend) + tint[2] * clampedBlend
        };
    }

    private static float[] copyClamped(float[] rgb) {
        if (rgb == null || rgb.length < 3) {
            return null;
        }
        return new float[] { clamp01(rgb[0]), clamp01(rgb[1]), clamp01(rgb[2]) };
    }

    private static float[] toRgbFloats(int rgb) {
        return new float[] {
                ((rgb >> 16) & 255) / 255.0F,
                ((rgb >> 8) & 255) / 255.0F,
                (rgb & 255) / 255.0F
        };
    }
}
