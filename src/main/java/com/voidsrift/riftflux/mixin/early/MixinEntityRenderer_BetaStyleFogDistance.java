package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.client.sky.FogStateCompat;
import com.voidsrift.riftflux.client.sky.SunriseSkyTintHelper;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public abstract class MixinEntityRenderer_BetaStyleFogDistance {

    @Shadow
    private Minecraft mc;

    @Shadow
    private boolean cloudFog;

    @Shadow
    private float farPlaneDistance;

    @Inject(method = "setupFog(IF)V", at = @At("RETURN"))
    private void riftflux$adjustBetaStyleFogDistance(int fogMode, float partialTicks, CallbackInfo ci) {
        WorldClient world = this.mc == null ? null : this.mc.theWorld;
        if (!SunriseSkyTintHelper.shouldUseBetaStyleBiomeFog(world)
                || this.mc == null
                || !(this.mc.renderViewEntity instanceof EntityLivingBase)
                || this.cloudFog
                || fogMode == 999) {
            return;
        }

        EntityLivingBase view = (EntityLivingBase) this.mc.renderViewEntity;
        if (view.isPotionActive(Potion.blindness)) {
            return;
        }

        Block block = ActiveRenderInfo.getBlockAtEntityViewpoint(world, view, partialTicks);
        Material material = block == null ? Material.air : block.getMaterial();
        if (material == Material.water || material == Material.lava) {
            return;
        }

        float[] fogDistance = SunriseSkyTintHelper.resolveBetaStyleFogDistance(world, view, this.farPlaneDistance, fogMode < 0);
        if (fogDistance == null || fogDistance.length < 2) {
            return;
        }
        float start = fogDistance[0];
        float finish = fogDistance[1];

        FogStateCompat.fogi(org.lwjgl.opengl.GL11.GL_FOG_MODE, org.lwjgl.opengl.GL11.GL_LINEAR);
        FogStateCompat.fogf(org.lwjgl.opengl.GL11.GL_FOG_START, start);
        FogStateCompat.fogf(org.lwjgl.opengl.GL11.GL_FOG_END, finish);
    }
}
