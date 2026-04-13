package com.voidsrift.riftflux.mixin.early.angelica;

import com.voidsrift.riftflux.client.photomode.IsometricPhotoModeController;
import com.voidsrift.riftflux.client.sky.SunriseSkyTintHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "com.gtnewhorizons.angelica.glsm.AngelicaFogService", remap = false)
public abstract class MixinAngelicaFogService_BetaStyleFog {

    @Inject(method = "getFogColor", at = @At("HEAD"), cancellable = true, require = 0)
    private void riftflux$overrideFogColor(CallbackInfoReturnable<float[]> cir) {
        if (IsometricPhotoModeController.instance().isActive()) {
            return;
        }

        Minecraft mc = Minecraft.getMinecraft();
        WorldClient world = mc == null ? null : mc.theWorld;
        if (!SunriseSkyTintHelper.shouldUseBetaStyleBiomeFog(world)) {
            return;
        }

        float partialTicks = 0.0F;
        float[] fogTint = SunriseSkyTintHelper.resolveBetaStyleLowerSkyColor(world, mc, partialTicks);
        if (fogTint != null && fogTint.length >= 3) {
            cir.setReturnValue(new float[] { fogTint[0], fogTint[1], fogTint[2], 1.0F });
        }
    }

    @Inject(method = "getFogStart", at = @At("HEAD"), cancellable = true, require = 0)
    private void riftflux$overrideFogStart(CallbackInfoReturnable<Float> cir) {
        if (IsometricPhotoModeController.instance().isActive()) {
            return;
        }

        float[] fogDistance = riftflux$resolveFogDistance();
        if (fogDistance != null) {
            cir.setReturnValue(Float.valueOf(fogDistance[0]));
        }
    }

    @Inject(method = "getFogEnd", at = @At("HEAD"), cancellable = true, require = 0)
    private void riftflux$overrideFogEnd(CallbackInfoReturnable<Float> cir) {
        if (IsometricPhotoModeController.instance().isActive()) {
            return;
        }

        float[] fogDistance = riftflux$resolveFogDistance();
        if (fogDistance != null) {
            cir.setReturnValue(Float.valueOf(fogDistance[1]));
        }
    }

    @Inject(method = "getFogCutoff", at = @At("HEAD"), cancellable = true, require = 0)
    private void riftflux$overrideFogCutoff(CallbackInfoReturnable<Float> cir) {
        if (IsometricPhotoModeController.instance().isActive()) {
            return;
        }

        float[] fogDistance = riftflux$resolveFogDistance();
        if (fogDistance != null) {
            cir.setReturnValue(Float.valueOf(fogDistance[1]));
        }
    }

    private static float[] riftflux$resolveFogDistance() {
        Minecraft mc = Minecraft.getMinecraft();
        WorldClient world = mc == null ? null : mc.theWorld;
        if (!SunriseSkyTintHelper.shouldUseBetaStyleBiomeFog(world)
                || mc == null
                || !(mc.renderViewEntity instanceof EntityLivingBase)) {
            return null;
        }

        EntityLivingBase view = (EntityLivingBase) mc.renderViewEntity;
        if (view.isPotionActive(Potion.blindness) || view.isInWater() || view.isInsideOfMaterial(net.minecraft.block.material.Material.lava)) {
            return null;
        }

        float farPlaneDistance = mc.gameSettings == null ? 128.0F : (float) (mc.gameSettings.renderDistanceChunks * 16);
        return SunriseSkyTintHelper.resolveBetaStyleFogDistance(world, view, farPlaneDistance, false);
    }
}
