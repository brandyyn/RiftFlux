package com.voidsrift.riftflux.mixin.early;

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
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = EntityRenderer.class, priority = 2147483647)
public abstract class MixinEntityRenderer_BlackNightFog {

    @Shadow
    private Minecraft mc;

    @Shadow
    private float fogColorRed;

    @Shadow
    private float fogColorGreen;

    @Shadow
    private float fogColorBlue;

    @Shadow
    private boolean cloudFog;

    @Inject(
            method = "updateFogColor(F)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/lwjgl/opengl/GL11;glClearColor(FFFF)V",
                    shift = At.Shift.BEFORE,
                    remap = false
            )
    )
    private void riftflux$applyFinalFogOverride(float partialTicks, CallbackInfo ci) {
        WorldClient world = this.mc == null ? null : this.mc.theWorld;
        if (SunriseSkyTintHelper.shouldUseBetaStyleBiomeFog(world)
                && this.mc != null
                && this.mc.renderViewEntity instanceof EntityLivingBase
                && !this.cloudFog) {
            EntityLivingBase view = (EntityLivingBase) this.mc.renderViewEntity;
            Block block = ActiveRenderInfo.getBlockAtEntityViewpoint(world, view, partialTicks);
            Material material = block == null ? Material.air : block.getMaterial();

            if (material != Material.water && material != Material.lava) {
                float[] fallback = new float[] { this.fogColorRed, this.fogColorGreen, this.fogColorBlue };
                float[] fogTint = SunriseSkyTintHelper.resolveEffectiveBetaStyleFogColor(world, this.mc, partialTicks, fallback);
                if (fogTint != null && fogTint.length >= 3) {
                    this.fogColorRed = fogTint[0];
                    this.fogColorGreen = fogTint[1];
                    this.fogColorBlue = fogTint[2];
                }
            }
            riftflux$storeDistanceGradientFogColor(world, partialTicks);
            return;
        }

        if (SunriseSkyTintHelper.shouldMatchFogToSky(world)
                && this.mc != null
                && this.mc.renderViewEntity instanceof EntityLivingBase
                && !this.cloudFog) {
            EntityLivingBase view = (EntityLivingBase) this.mc.renderViewEntity;
            Block block = ActiveRenderInfo.getBlockAtEntityViewpoint(world, view, partialTicks);
            Material material = block == null ? Material.air : block.getMaterial();

            if (material != Material.water && material != Material.lava) {
                float[] fallback = new float[] { this.fogColorRed, this.fogColorGreen, this.fogColorBlue };
                float[] skyTint = SunriseSkyTintHelper.resolveEffectiveSkyMatchingFogColor(world, this.mc, partialTicks, fallback);
                if (skyTint != null && skyTint.length >= 3) {
                    this.fogColorRed = skyTint[0];
                    this.fogColorGreen = skyTint[1];
                    this.fogColorBlue = skyTint[2];
                }
            }
            riftflux$storeDistanceGradientFogColor(world, partialTicks);
            return;
        }

        if (SunriseSkyTintHelper.shouldUseBlackNightFog(world, partialTicks)) {
            float[] blackNightFog = SunriseSkyTintHelper.resolveBlackNightFogColor(world, this.mc, partialTicks);
            if (blackNightFog != null && blackNightFog.length >= 3) {
                blackNightFog = SunriseSkyTintHelper.applyNightFogFloor(world, partialTicks, blackNightFog);
                this.fogColorRed = blackNightFog[0];
                this.fogColorGreen = blackNightFog[1];
                this.fogColorBlue = blackNightFog[2];
            } else {
                this.fogColorRed = 0.0F;
                this.fogColorGreen = 0.0F;
                this.fogColorBlue = 0.0F;
            }
            riftflux$storeDistanceGradientFogColor(world, partialTicks);
            return;
        }

        riftflux$storeDistanceGradientFogColor(world, partialTicks);
    }

    @Redirect(
            method = "updateFogColor(F)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/lwjgl/opengl/GL11;glClearColor(FFFF)V",
                    remap = false
            )
    )
    private void riftflux$applyFogClearColor(float red, float green, float blue, float alpha, float partialTicks) {
        float finalRed = red;
        float finalGreen = green;
        float finalBlue = blue;
        WorldClient world = this.mc == null ? null : this.mc.theWorld;
        if (SunriseSkyTintHelper.shouldUseBetaStyleBiomeFog(world)
                && this.mc != null
                && this.mc.renderViewEntity instanceof EntityLivingBase
                && !this.cloudFog) {
            EntityLivingBase view = (EntityLivingBase) this.mc.renderViewEntity;
            Block block = ActiveRenderInfo.getBlockAtEntityViewpoint(world, view, partialTicks);
            Material material = block == null ? Material.air : block.getMaterial();
            if (material != Material.water && material != Material.lava) {
                float[] clearTint = SunriseSkyTintHelper.resolveSkyTintedColor(world, this.mc, partialTicks);
                if (clearTint != null && clearTint.length >= 3) {
                    finalRed = clearTint[0];
                    finalGreen = clearTint[1];
                    finalBlue = clearTint[2];
                }
            }
        } else if (SunriseSkyTintHelper.shouldMatchFogToSky(world)
                && this.mc != null
                && this.mc.renderViewEntity instanceof EntityLivingBase
                && !this.cloudFog) {
            EntityLivingBase view = (EntityLivingBase) this.mc.renderViewEntity;
            Block block = ActiveRenderInfo.getBlockAtEntityViewpoint(world, view, partialTicks);
            Material material = block == null ? Material.air : block.getMaterial();
            if (material != Material.water && material != Material.lava) {
                float[] skyTint = SunriseSkyTintHelper.resolveSkyTintedColor(world, this.mc, partialTicks);
                if (skyTint != null && skyTint.length >= 3) {
                    finalRed = skyTint[0];
                    finalGreen = skyTint[1];
                    finalBlue = skyTint[2];
                }
            }
        } else if (SunriseSkyTintHelper.shouldUseBlackNightFog(world, partialTicks)) {
            float[] skyTint = SunriseSkyTintHelper.resolveSkyTintedColor(world, this.mc, partialTicks);
            if (skyTint != null && skyTint.length >= 3) {
                finalRed = skyTint[0];
                finalGreen = skyTint[1];
                finalBlue = skyTint[2];
            }
        }

        GL11.glClearColor(finalRed, finalGreen, finalBlue, alpha);
    }

    private void riftflux$storeDistanceGradientFogColor(WorldClient world, float partialTicks) {
        if (!SunriseSkyTintHelper.shouldUseFogDistanceGradient(world)
                || this.mc == null
                || !(this.mc.renderViewEntity instanceof EntityLivingBase)
                || this.cloudFog) {
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

        FogDistanceGradientState.setFogColor(world, this.fogColorRed, this.fogColorGreen, this.fogColorBlue);
    }
}
