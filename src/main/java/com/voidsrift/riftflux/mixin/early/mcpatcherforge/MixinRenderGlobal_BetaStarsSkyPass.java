package com.voidsrift.riftflux.mixin.early.mcpatcherforge;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.client.sky.BetaStarsRenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderGlobal;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderGlobal.class)
public abstract class MixinRenderGlobal_BetaStarsSkyPass {
    @Shadow
    private int starGLCallList;

    private float riftflux$currentSkyPartialTicks;

    @Inject(method = "renderSky(F)V", at = @At("HEAD"))
    private void riftflux$beginBetaStarsSkyPass(float partialTicks, CallbackInfo ci) {
        riftflux$currentSkyPartialTicks = partialTicks;
        if (!ModConfig.betaStarsEnabled) {
            return;
        }
        BetaStarsRenderHelper.beginSkyRenderPass();
    }

    @Redirect(
            method = "renderSky(F)V",
            at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glCallList(I)V", remap = false)
    )
    private void riftflux$renderDynamicBetaStars(int listId) {
        if (!ModConfig.betaStarsEnabled || listId != starGLCallList) {
            GL11.glCallList(listId);
            return;
        }
        if (BetaStarsRenderHelper.hasRenderedThisSkyPass()) {
            return;
        }
        riftflux$renderBetaStarsInCurrentSkyMatrix(riftflux$currentSkyPartialTicks);
    }

    private void riftflux$renderBetaStarsInCurrentSkyMatrix(float partialTicks) {
        if (BetaStarsRenderHelper.hasRenderedThisSkyPass()) {
            return;
        }
        Minecraft mc = Minecraft.getMinecraft();
        if (mc == null || mc.theWorld == null) {
            return;
        }

        int starCount = BetaStarsRenderHelper.resolvePreferredStarCount();
        float starBrightness = BetaStarsRenderHelper.computeStarBrightness(mc, partialTicks);
        if (starCount <= 0 || starBrightness <= 0.0F) {
            return;
        }

        GL11.glPushAttrib(GL11.GL_ENABLE_BIT | GL11.GL_CURRENT_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDepthMask(false);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glPushMatrix();
        if (!ModConfig.betaStarsSpinWithSunMoon) {
            BetaStarsRenderHelper.undoSunMoonRotation(partialTicks);
        }
        BetaStarsRenderHelper.renderBetaStars(starCount, starBrightness, partialTicks);
        GL11.glPopMatrix();
        BetaStarsRenderHelper.markRenderedThisSkyPass();
        GL11.glDepthMask(true);
        GL11.glPopAttrib();
    }

    @Inject(method = "renderSky(F)V", at = @At("RETURN"))
    private void riftflux$renderBetaStarsAtSkyEnd(float partialTicks, CallbackInfo ci) {
        if (!ModConfig.betaStarsEnabled) {
            return;
        }
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

        GL11.glPushAttrib(GL11.GL_ENABLE_BIT | GL11.GL_CURRENT_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDepthMask(false);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glPushMatrix();
        if (ModConfig.betaStarsSpinWithSunMoon) {
            BetaStarsRenderHelper.applySunMoonRotation(partialTicks);
        }
        BetaStarsRenderHelper.renderBetaStars(starCount, starBrightness, partialTicks);
        GL11.glPopMatrix();
        BetaStarsRenderHelper.markRenderedThisSkyPass();
        GL11.glDepthMask(true);
        GL11.glPopAttrib();
    }
}
