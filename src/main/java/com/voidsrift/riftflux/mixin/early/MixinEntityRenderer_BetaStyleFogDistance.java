package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.client.sky.FogStateCompat;
import com.voidsrift.riftflux.client.sky.FogDistanceGradientState;
import com.voidsrift.riftflux.client.sky.SunriseSkyTintHelper;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public abstract class MixinEntityRenderer_BetaStyleFogDistance {
    private static final int GL_FOG_DISTANCE_MODE_NV = 34138;
    private static final int GL_EYE_RADIAL_NV = 34139;

    @Shadow
    private Minecraft mc;

    @Shadow
    private boolean cloudFog;

    @Shadow
    private float farPlaneDistance;

    @Shadow
    protected abstract float getFOVModifier(float partialTicks, boolean useFovSetting);

    @Inject(method = "setupFog(IF)V", at = @At("RETURN"))
    private void riftflux$adjustBetaStyleFogDistance(int fogMode, float partialTicks, CallbackInfo ci) {
        WorldClient world = this.mc == null ? null : this.mc.theWorld;
        if (this.mc == null
                || !(this.mc.renderViewEntity instanceof EntityLivingBase)
                || this.cloudFog
                || fogMode == 999) {
            FogDistanceGradientState.invalidate(world);
            return;
        }

        EntityLivingBase view = (EntityLivingBase) this.mc.renderViewEntity;
        if (view.isPotionActive(Potion.blindness)) {
            FogDistanceGradientState.invalidate(world);
            return;
        }

        Block block = ActiveRenderInfo.getBlockAtEntityViewpoint(world, view, partialTicks);
        Material material = block == null ? Material.air : block.getMaterial();
        if (material == Material.water || material == Material.lava) {
            FogDistanceGradientState.invalidate(world);
            return;
        }

        FogStateCompat.fogi(GL_FOG_DISTANCE_MODE_NV, GL_EYE_RADIAL_NV);

        float gradientStart = 0.0F;
        float gradientEnd = 0.0F;
        boolean gradientDistanceReady = false;
        if (SunriseSkyTintHelper.shouldUseDenseFogDistance(world, partialTicks)) {
            float[] fogDistance = SunriseSkyTintHelper.resolveDenseFogDistance(world, view, this.farPlaneDistance, fogMode < 0, partialTicks);
            if (fogDistance == null || fogDistance.length < 2) {
                FogDistanceGradientState.invalidate(world);
                return;
            }
            float start = fogDistance[0];
            float finish = fogDistance[1];

            FogStateCompat.fogi(GL11.GL_FOG_MODE, GL11.GL_LINEAR);
            FogStateCompat.fogf(GL11.GL_FOG_START, start);
            FogStateCompat.fogf(GL11.GL_FOG_END, finish);
            gradientStart = start;
            gradientEnd = finish;
            gradientDistanceReady = true;
        }

        if (SunriseSkyTintHelper.shouldUseFogDistanceGradient(world)) {
            if (!gradientDistanceReady) {
                float[] fogDistance = SunriseSkyTintHelper.resolveVanillaFogDistance(world, view, this.farPlaneDistance, fogMode < 0);
                if (fogDistance == null || fogDistance.length < 2) {
                    FogDistanceGradientState.invalidate(world);
                    return;
                }
                gradientStart = fogDistance[0];
                gradientEnd = fogDistance[1];
            }

            float[] projectionScale = this.riftflux$estimateProjectionScale(partialTicks);
            FogDistanceGradientState.setFogDistance(
                    world,
                    gradientStart,
                    gradientEnd,
                    this.farPlaneDistance * 2.0F,
                    projectionScale[0],
                    projectionScale[1]
            );
        }
    }

    private float[] riftflux$estimateProjectionScale(float partialTicks) {
        float fov = this.getFOVModifier(partialTicks, true);
        if (fov <= 1.0F) {
            fov = 70.0F;
        }
        float aspect = this.mc != null && this.mc.displayHeight > 0
                ? (float) this.mc.displayWidth / (float) this.mc.displayHeight
                : 1.0F;
        if (aspect < 0.001F) {
            aspect = 1.0F;
        }
        float scaleY = (float) (1.0D / Math.tan(Math.toRadians(fov) * 0.5D));
        float scaleX = scaleY / aspect;
        return new float[] { scaleX, scaleY };
    }
}
