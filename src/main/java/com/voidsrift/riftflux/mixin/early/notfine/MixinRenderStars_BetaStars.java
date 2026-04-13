package com.voidsrift.riftflux.mixin.early.notfine;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.client.sky.BetaStarsRenderHelper;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "jss.notfine.render.RenderStars", remap = false)
public abstract class MixinRenderStars_BetaStars {

    @Inject(method = "renderStars", at = @At("HEAD"), cancellable = true)
    private static void riftflux$renderBetaStarsInstead(CallbackInfo ci) {
        if (!ModConfig.betaStarsEnabled) {
            return;
        }
        if (!BetaStarsRenderHelper.shouldRenderBetaStars(Minecraft.getMinecraft())) {
            ci.cancel();
            return;
        }

        BetaStarsRenderHelper.markRenderedThisSkyPass();
        int starCount = BetaStarsRenderHelper.resolvePreferredStarCount();
        float starBrightness = BetaStarsRenderHelper.computeStarBrightness(Minecraft.getMinecraft(), 0.0F);
        if (starCount > 0 && starBrightness > 0.0F) {
            BetaStarsRenderHelper.renderBetaStars(starCount, starBrightness, 0.0F);
        }
        ci.cancel();
    }
}
