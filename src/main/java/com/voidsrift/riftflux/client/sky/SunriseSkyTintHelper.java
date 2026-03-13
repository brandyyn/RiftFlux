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
                && ModConfig.celestialFogMatchesSky
                && world != null
                && world.provider != null
                && !world.provider.hasNoSky;
    }

    public static boolean shouldUseBetaStyleBiomeFog(WorldClient world) {
        return ModConfig.celestialBetaStyleFogBiomeTint
                && world != null
                && world.provider != null
                && !world.provider.hasNoSky;
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

    public static float[] resolveBetaStyleBiomeFogColor(WorldClient world, Minecraft mc, float partialTicks) {
        float[] rawSky = resolveRawSkyColor(world, mc, partialTicks);
        if (rawSky == null) {
            return null;
        }
        float[] skyTint = resolveSkyTintedColor(world, mc, partialTicks);
        float[] tintSource = skyTint == null ? rawSky : skyTint;

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
        float[] skyTint = resolveSkyTintedColor(world, mc, partialTicks);
        if (fogTint == null) {
            return skyTint;
        }
        if (skyTint == null) {
            return fogTint;
        }
        return mixColors(skyTint, fogTint, 0.32F);
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
        if (isSunriseOrSunsetActive(world, partialTicks)) {
            return false;
        }

        long timeOfDay = world.getWorldTime() % 24000L;
        if (timeOfDay < 0L) {
            timeOfDay += 24000L;
        }
        return timeOfDay >= 13000L && timeOfDay < 23000L;
    }

    private static float[] getSunriseSunsetColors(WorldClient world, float partialTicks) {
        if (world == null || world.provider == null) {
            return null;
        }
        return world.provider.calcSunriseSunsetColors(world.getCelestialAngle(partialTicks), partialTicks);
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

    private static float[] toRgbFloats(int rgb) {
        return new float[] {
                ((rgb >> 16) & 255) / 255.0F,
                ((rgb >> 8) & 255) / 255.0F,
                (rgb & 255) / 255.0F
        };
    }
}
