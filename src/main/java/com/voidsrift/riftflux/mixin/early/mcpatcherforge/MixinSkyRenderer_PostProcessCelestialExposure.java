package com.voidsrift.riftflux.mixin.early.mcpatcherforge;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.client.photomode.IsometricPhotoModeController;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "com.prupe.mcpatcher.sky.SkyRenderer", remap = false)
public abstract class MixinSkyRenderer_PostProcessCelestialExposure {

    private static final ResourceLocation riftflux$defaultSun =
            new ResourceLocation("textures/environment/sun.png");
    private static final ResourceLocation riftflux$defaultMoon =
            new ResourceLocation("textures/environment/moon_phases.png");

    @Shadow
    public static boolean active;

    @Shadow
    private static float rainStrength;

    @Inject(
            method = "setupCelestialObject(Lnet/minecraft/util/ResourceLocation;)Lnet/minecraft/util/ResourceLocation;",
            at = @At("RETURN"),
            require = 0
    )
    private static void riftflux$applyBetterSkiesExposureCompensation(
            ResourceLocation requested,
            CallbackInfoReturnable<ResourceLocation> cir
    ) {
        if (!active) {
            return;
        }

        ResourceLocation rendered = cir.getReturnValue();
        boolean sun = riftflux$same(requested, riftflux$defaultSun)
                || riftflux$matchesConfiguredTexture(requested, ModConfig.celestialSunEventTextures)
                || riftflux$sameConfiguredTexture(requested, ModConfig.celestialSunEventTexture);
        boolean moon = riftflux$same(requested, riftflux$defaultMoon)
                || riftflux$matchesConfiguredTexture(requested, ModConfig.celestialMoonEventTextures)
                || riftflux$sameConfiguredTexture(requested, ModConfig.celestialMoonEventTexture);

        if (!sun && !moon) {
            return;
        }

        if (IsometricPhotoModeController.instance().isActive()) {
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.0F);
            return;
        }
        if (!ModConfig.enablePostProcessing) {
            return;
        }

        boolean eventTexture = sun
                ? !riftflux$same(requested, riftflux$defaultSun) || !riftflux$same(rendered, riftflux$defaultSun)
                : !riftflux$same(requested, riftflux$defaultMoon) || !riftflux$same(rendered, riftflux$defaultMoon);
        float compensation = sun
                ? (eventTexture
                ? ModConfig.postProcessSunEventExposureCompensationPercent
                : ModConfig.postProcessSunExposureCompensationPercent)
                : (eventTexture
                ? ModConfig.postProcessMoonEventExposureCompensationPercent
                : ModConfig.postProcessMoonExposureCompensationPercent);
        float multiplier = riftflux$exposureCompensationMultiplier(compensation);
        GL11.glColor4f(multiplier, multiplier, multiplier, rainStrength);
    }

    private static boolean riftflux$matchesConfiguredTexture(ResourceLocation texture, String[] configuredTextures) {
        if (texture == null || configuredTextures == null) {
            return false;
        }
        for (String configuredTexture : configuredTextures) {
            if (riftflux$sameConfiguredTexture(texture, configuredTexture)) {
                return true;
            }
        }
        return false;
    }

    private static boolean riftflux$sameConfiguredTexture(ResourceLocation texture, String configuredTexture) {
        if (texture == null || configuredTexture == null || configuredTexture.trim().isEmpty()) {
            return false;
        }
        try {
            return riftflux$same(texture, new ResourceLocation(configuredTexture.trim()));
        } catch (Throwable ignored) {
            return false;
        }
    }

    private static boolean riftflux$same(ResourceLocation left, ResourceLocation right) {
        return left != null
                && right != null
                && left.getResourceDomain().equals(right.getResourceDomain())
                && left.getResourcePath().equals(right.getResourcePath());
    }

    private static float riftflux$exposureCompensationMultiplier(float compensationPercent) {
        float compensation = riftflux$clamp(compensationPercent / 100.0F, 0.0F, 10.0F);
        if (compensation <= 0.0F) {
            return 1.0F;
        }
        float exposureScale = Math.max(0.001F, 1.0F + riftflux$clamp(ModConfig.postProcessExposure, -1.0F, 1.0F));
        float inverseExposure = 1.0F / exposureScale;
        return 1.0F + (inverseExposure - 1.0F) * compensation;
    }

    private static float riftflux$clamp(float value, float min, float max) {
        if (value < min) {
            return min;
        }
        if (value > max) {
            return max;
        }
        return value;
    }
}
