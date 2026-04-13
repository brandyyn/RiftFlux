package com.voidsrift.riftflux.mixin.early.mcpatcherforge;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.client.sky.BetaStarsRenderHelper;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "com.prupe.mcpatcher.sky.SkyRenderer", remap = false)
public abstract class MixinSkyRenderer_BetaStars {

    @Inject(
            method = "renderAll",
            at = @At("RETURN"),
            require = 0
    )
    private static void riftflux$renderBetaStarsWithCustomSky(CallbackInfo ci) {
        if (!ModConfig.betaStarsEnabled || ModConfig.betaStarsRenderBehindSunMoon) {
            return;
        }
        riftflux$renderBetaStars(0.0F);
    }

    private static void riftflux$renderBetaStars(float partialTicks) {
        if (BetaStarsRenderHelper.hasRenderedThisSkyPass()) {
            return;
        }

        Minecraft mc = Minecraft.getMinecraft();
        if (mc == null || mc.theWorld == null) {
            return;
        }
        if (!BetaStarsRenderHelper.shouldRenderBetaStars(mc, partialTicks)) {
            return;
        }

        int starCount = BetaStarsRenderHelper.resolvePreferredStarCount();
        if (starCount <= 0) {
            return;
        }

        float starBrightness = BetaStarsRenderHelper.computeStarBrightness(mc, partialTicks);
        if (starBrightness <= 0.0F) {
            return;
        }

        GL11.glPushAttrib(GL11.GL_ENABLE_BIT | GL11.GL_CURRENT_BIT);
        if (GL11.glIsEnabled(GL11.GL_TEXTURE_2D)) {
            GL11.glDisable(GL11.GL_TEXTURE_2D);
        }
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        BetaStarsRenderHelper.renderBetaStars(starCount, starBrightness, partialTicks);
        BetaStarsRenderHelper.markRenderedThisSkyPass();
        GL11.glPopAttrib();
    }
}
