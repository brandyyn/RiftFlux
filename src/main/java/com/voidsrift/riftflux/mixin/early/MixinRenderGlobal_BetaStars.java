package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.client.sky.BetaStarsRenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderGlobal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderGlobal.class)
public abstract class MixinRenderGlobal_BetaStars {

    @Inject(method = "renderStars", at = @At("HEAD"), cancellable = true)
    private void riftflux$renderBetaSizedStars(CallbackInfo ci) {
        if (!ModConfig.betaStarsEnabled) {
            return;
        }
        if (!BetaStarsRenderHelper.shouldRenderBetaStars(Minecraft.getMinecraft())) {
            ci.cancel();
            return;
        }

        BetaStarsRenderHelper.markRenderedThisSkyPass();
        int starCount = BetaStarsRenderHelper.resolvePreferredStarCount();
        if (starCount > 0) {
            BetaStarsRenderHelper.renderBetaStars(starCount);
        }
        ci.cancel();
    }
}
