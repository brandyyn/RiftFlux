package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.ModConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.lwjgl.opengl.GL11;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Mixin(RenderGlobal.class)
public abstract class MixinRenderGlobal_CelestialEventTextures {

    @Unique
    private static final long rf$dayTicks = 24000L;
    @Unique
    private static final long rf$sunRollSalt = 0x53D15EEDBEEFL;
    @Unique
    private static final long rf$moonRollSalt = 0x4D00B5EEDL;
    @Unique
    private static final long rf$sunPickSalt = 0x53554E5049434BL;
    @Unique
    private static final long rf$moonPickSalt = 0x4D4F4F4E5049434BL;

    @Unique
    private static final ResourceLocation rf$defaultSun = new ResourceLocation("textures/environment/sun.png");
    @Unique
    private static final ResourceLocation rf$defaultMoon = new ResourceLocation("textures/environment/moon_phases.png");

    @Unique
    private static final Map<String, ResourceLocation> rf$validTextureCache = new HashMap<String, ResourceLocation>();
    @Unique
    private static final Map<String, Long> rf$missingTextureCheckedDay = new HashMap<String, Long>();

    @Unique
    private float rf$celestialSkyAlpha = 1.0F;
    @Unique
    private ResourceLocation rf$cachedSunTexture;

    @Shadow
    private WorldClient theWorld;

    @Redirect(
            method = "renderSky(F)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/lwjgl/opengl/GL11;glColor4f(FFFF)V",
                    ordinal = 0,
                    remap = false
            ),
            require = 0
    )
    private void rf$applySunExposurePreColor(float red, float green, float blue, float alpha) {
        this.rf$celestialSkyAlpha = alpha;
        ResourceLocation sunTexture = this.rf$resolveSunTexture();
        this.rf$cachedSunTexture = sunTexture;
        float compensation = rf$isEventTexture(sunTexture, rf$defaultSun)
                ? ModConfig.postProcessSunEventExposureCompensationPercent
                : ModConfig.postProcessSunExposureCompensationPercent;
        float multiplier = rf$exposureCompensationMultiplier(compensation);
        GL11.glColor4f(red * multiplier, green * multiplier, blue * multiplier, alpha);
    }

    @Redirect(
            method = "renderSky(F)V",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/renderer/RenderGlobal;locationSunPng:Lnet/minecraft/util/ResourceLocation;"
            )
    )
    private ResourceLocation rf$redirectSunTexture() {
        ResourceLocation texture = this.rf$cachedSunTexture != null ? this.rf$cachedSunTexture : this.rf$resolveSunTexture();
        this.rf$cachedSunTexture = null;
        return texture;
    }

    @Redirect(
            method = "renderSky(F)V",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/renderer/RenderGlobal;locationMoonPhasesPng:Lnet/minecraft/util/ResourceLocation;"
            )
    )
    private ResourceLocation rf$redirectMoonTexture() {
        ResourceLocation moonTexture = this.rf$resolveMoonTexture();
        float compensation = rf$isEventTexture(moonTexture, rf$defaultMoon)
                ? ModConfig.postProcessMoonEventExposureCompensationPercent
                : ModConfig.postProcessMoonExposureCompensationPercent;
        float multiplier = rf$exposureCompensationMultiplier(compensation);
        GL11.glColor4f(multiplier, multiplier, multiplier, this.rf$celestialSkyAlpha);
        return moonTexture;
    }

    @Unique
    private ResourceLocation rf$resolveSunTexture() {
        if (!ModConfig.enableCelestialEventTextures) {
            return rf$defaultSun;
        }
        if (!rf$rollForToday(ModConfig.celestialSunEventChance, rf$sunRollSalt)) {
            return rf$defaultSun;
        }
        return rf$pickEventTexture(
                ModConfig.celestialSunEventTextures,
                ModConfig.celestialSunEventTexture,
                rf$defaultSun,
                rf$sunPickSalt
        );
    }

    @Unique
    private ResourceLocation rf$resolveMoonTexture() {
        if (!ModConfig.enableCelestialEventTextures) {
            return rf$defaultMoon;
        }
        if (!rf$rollForToday(ModConfig.celestialMoonEventChance, rf$moonRollSalt)) {
            return rf$defaultMoon;
        }
        return rf$pickEventTexture(
                ModConfig.celestialMoonEventTextures,
                ModConfig.celestialMoonEventTexture,
                rf$defaultMoon,
                rf$moonPickSalt
        );
    }

    @Unique
    private static boolean rf$isEventTexture(ResourceLocation texture, ResourceLocation fallback) {
        return texture != null
                && (!texture.getResourceDomain().equals(fallback.getResourceDomain())
                || !texture.getResourcePath().equals(fallback.getResourcePath()));
    }

    @Unique
    private static float rf$exposureCompensationMultiplier(float compensationPercent) {
        if (!ModConfig.enablePostProcessing) {
            return 1.0F;
        }
        float compensation = rf$clamp(compensationPercent / 100.0F, 0.0F, 10.0F);
        if (compensation <= 0.0F) {
            return 1.0F;
        }
        float exposureScale = Math.max(0.001F, 1.0F + rf$clamp(ModConfig.postProcessExposure, -1.0F, 1.0F));
        float inverseExposure = 1.0F / exposureScale;
        return 1.0F + (inverseExposure - 1.0F) * compensation;
    }

    @Unique
    private static float rf$clamp(float value, float min, float max) {
        if (value < min) {
            return min;
        }
        if (value > max) {
            return max;
        }
        return value;
    }

    @Unique
    private boolean rf$rollForToday(float chance, long salt) {
        if (chance <= 0.0F) {
            return false;
        }
        if (chance >= 1.0F) {
            return true;
        }
        if (this.theWorld == null) {
            return false;
        }

        long day = rf$getDayIndex();
        long seed = this.theWorld.getSeed();
        long mixed = rf$mix64(seed ^ (day * 341873128712L) ^ salt);
        double random01 = (double) (mixed >>> 11) * 0x1.0p-53;
        return random01 < (double) chance;
    }

    @Unique
    private ResourceLocation rf$pickEventTexture(String[] configuredList, String legacyPath, ResourceLocation fallback, long pickSalt) {
        boolean hasList = configuredList != null && configuredList.length > 0;
        int total = hasList ? configuredList.length : (rf$nonEmpty(legacyPath) ? 1 : 0);
        if (total <= 0) {
            return fallback;
        }

        int startIndex = rf$pickIndexForToday(total, pickSalt);
        for (int offset = 0; offset < total; offset++) {
            int idx = (startIndex + offset) % total;
            String candidate = hasList ? configuredList[idx] : legacyPath;
            ResourceLocation location = rf$resolveVerifiedTexture(candidate);
            if (location != null) {
                return location;
            }
        }
        return fallback;
    }

    @Unique
    private int rf$pickIndexForToday(int bound, long salt) {
        if (bound <= 1 || this.theWorld == null) {
            return 0;
        }
        long day = rf$getDayIndex();
        long seed = this.theWorld.getSeed();
        long mixed = rf$mix64(seed ^ (day * 132897987541L) ^ salt);
        long nonNegative = mixed & Long.MAX_VALUE;
        return (int) (nonNegative % (long) bound);
    }

    @Unique
    private ResourceLocation rf$resolveVerifiedTexture(String rawPath) {
        String path = rawPath == null ? "" : rawPath.trim();
        if (path.isEmpty()) {
            return null;
        }

        ResourceLocation cached = rf$validTextureCache.get(path);
        if (cached != null) {
            return cached;
        }

        long day = rf$getCurrentDay();
        Long checkedMissingDay = rf$missingTextureCheckedDay.get(path);
        if (checkedMissingDay != null && checkedMissingDay.longValue() == day) {
            return null;
        }

        ResourceLocation location;
        try {
            location = new ResourceLocation(path);
        } catch (Throwable ignored) {
            rf$missingTextureCheckedDay.put(path, day);
            return null;
        }

        if (!rf$resourceExists(location)) {
            rf$missingTextureCheckedDay.put(path, day);
            return null;
        }

        rf$validTextureCache.put(path, location);
        rf$missingTextureCheckedDay.remove(path);
        return location;
    }

    @Unique
    private long rf$getCurrentDay() {
        if (this.theWorld == null) {
            return -1L;
        }
        return rf$getDayIndex();
    }

    @Unique
    private long rf$getDayIndex() {
        if (this.theWorld == null) {
            return 0L;
        }
        return Math.floorDiv(this.theWorld.getWorldTime(), rf$dayTicks);
    }

    @Unique
    private static boolean rf$resourceExists(ResourceLocation location) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc == null || mc.getResourceManager() == null) {
            return false;
        }

        InputStream in = null;
        try {
            IResource resource = mc.getResourceManager().getResource(location);
            if (resource == null) {
                return false;
            }
            in = resource.getInputStream();
            BufferedImage image = ImageIO.read(in);
            return image != null && image.getWidth() > 0 && image.getHeight() > 0;
        } catch (IOException ignored) {
            return false;
        } catch (Throwable ignored) {
            return false;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    @Unique
    private static boolean rf$nonEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }

    @Unique
    private static long rf$mix64(long value) {
        value = (value ^ (value >>> 33)) * -49064778989728563L;
        value = (value ^ (value >>> 33)) * -4265267296055464877L;
        return value ^ (value >>> 33);
    }
}
