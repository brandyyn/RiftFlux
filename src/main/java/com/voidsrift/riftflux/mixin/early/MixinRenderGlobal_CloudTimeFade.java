package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.client.sky.CloudTimeFadeHelper;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(RenderGlobal.class)
public abstract class MixinRenderGlobal_CloudTimeFade {

    @Shadow
    private WorldClient theWorld;

    @Redirect(
            method = "renderClouds(F)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/Tessellator;setColorRGBA_F(FFFF)V"
            ),
            require = 0
    )
    private void riftflux$fadeFastCloudAlpha(
            Tessellator tessellator,
            float red,
            float green,
            float blue,
            float alpha,
            float partialTicks
    ) {
        tessellator.setColorRGBA_F(
                red,
                green,
                blue,
                alpha * CloudTimeFadeHelper.getOpacity(this.theWorld, partialTicks)
        );
    }

    @Redirect(
            method = "renderCloudsFancy(F)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/Tessellator;setColorRGBA_F(FFFF)V"
            ),
            require = 0
    )
    private void riftflux$fadeFancyCloudAlpha(
            Tessellator tessellator,
            float red,
            float green,
            float blue,
            float alpha,
            float partialTicks
    ) {
        tessellator.setColorRGBA_F(
                red,
                green,
                blue,
                alpha * CloudTimeFadeHelper.getOpacity(this.theWorld, partialTicks)
        );
    }
}
