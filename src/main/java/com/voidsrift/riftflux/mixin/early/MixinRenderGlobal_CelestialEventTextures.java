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

    @Shadow
    private WorldClient theWorld;

    @Redirect(
            method = "renderSky(F)V",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/renderer/RenderGlobal;locationSunPng:Lnet/minecraft/util/ResourceLocation;"
            )
    )
    private ResourceLocation rf$redirectSunTexture() {
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

    @Redirect(
            method = "renderSky(F)V",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/renderer/RenderGlobal;locationMoonPhasesPng:Lnet/minecraft/util/ResourceLocation;"
            )
    )
    private ResourceLocation rf$redirectMoonTexture() {
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
            return true;
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
