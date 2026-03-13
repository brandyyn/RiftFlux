package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.client.sky.SunriseSkyTintHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.util.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntityRenderer.class)
public abstract class MixinEntityRenderer_SunriseTint {

    @Shadow
    private Minecraft mc;

    @Redirect(
            method = "updateFogColor(F)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/Vec3;dotProduct(Lnet/minecraft/util/Vec3;)D"
            )
    )
    private double riftflux$removeViewDirectionGate(Vec3 lookVector, Vec3 celestialVector, float partialTicks) {
        WorldClient world = this.mc == null ? null : this.mc.theWorld;
        if (!SunriseSkyTintHelper.shouldForceFullSunriseTint(world, partialTicks)) {
            return lookVector.dotProduct(celestialVector);
        }
        return 1.0D;
    }
}
