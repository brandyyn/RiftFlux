package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.client.sky.CloudTimeFadeHelper;
import com.voidsrift.riftflux.client.photomode.IsometricPhotoModeController;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = RenderGlobal.class, priority = 900)
public abstract class MixinRenderGlobal_CloudTimeFade {

    @Shadow
    private WorldClient theWorld;

    @Unique
    private int riftflux$cloudFadePassDepth;

    @Unique
    private int riftflux$cloudFadePreviousAlphaFunction;

    @Unique
    private float riftflux$cloudFadePreviousAlphaReference;

    @Unique
    private boolean riftflux$cloudFadeChangedAlphaThreshold;

    @Inject(method = "renderClouds(F)V", at = @At("HEAD"), cancellable = true)
    private void riftflux$beginCloudFadePass(float partialTicks, CallbackInfo ci) {
        if (IsometricPhotoModeController.instance().isActive()) {
            ci.cancel();
            return;
        }
        this.riftflux$beginCloudFadeAlphaThreshold(partialTicks);
    }

    @Inject(method = "renderClouds(F)V", at = @At("RETURN"))
    private void riftflux$endCloudFadePass(float partialTicks, CallbackInfo ci) {
        this.riftflux$endCloudFadeAlphaThreshold();
    }

    @Inject(method = "renderCloudsFancy(F)V", at = @At("HEAD"), cancellable = true)
    private void riftflux$beginFancyCloudFadePass(float partialTicks, CallbackInfo ci) {
        if (IsometricPhotoModeController.instance().isActive()) {
            ci.cancel();
            return;
        }
        this.riftflux$beginCloudFadeAlphaThreshold(partialTicks);
    }

    @Inject(method = "renderCloudsFancy(F)V", at = @At("RETURN"))
    private void riftflux$endFancyCloudFadePass(float partialTicks, CallbackInfo ci) {
        this.riftflux$endCloudFadeAlphaThreshold();
    }

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

    @Redirect(
            method = "renderCloudsFast(F)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/Tessellator;setColorRGBA_F(FFFF)V",
                    remap = true
            ),
            require = 0,
            remap = false
    )
    private void riftflux$fadeAngelicaFastCloudAlpha(
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

    @Inject(
            method = "renderCloudsFast(F)V",
            at = @At("HEAD"),
            cancellable = true,
            require = 0,
            remap = false
    )
    private void riftflux$beginAngelicaFastCloudFadePass(float partialTicks, CallbackInfo ci) {
        if (IsometricPhotoModeController.instance().isActive()) {
            ci.cancel();
            return;
        }
        this.riftflux$beginCloudFadeAlphaThreshold(partialTicks);
    }

    @Inject(
            method = "renderCloudsFast(F)V",
            at = @At("RETURN"),
            require = 0,
            remap = false
    )
    private void riftflux$endAngelicaFastCloudFadePass(float partialTicks, CallbackInfo ci) {
        this.riftflux$endCloudFadeAlphaThreshold();
    }

    @Unique
    private void riftflux$beginCloudFadeAlphaThreshold(float partialTicks) {
        ++this.riftflux$cloudFadePassDepth;
        if (this.riftflux$cloudFadePassDepth != 1) {
            return;
        }

        float opacity = CloudTimeFadeHelper.getOpacity(this.theWorld, partialTicks);
        if (opacity >= 0.9999F || !GL11.glIsEnabled(GL11.GL_ALPHA_TEST)) {
            return;
        }

        this.riftflux$cloudFadePreviousAlphaFunction = GL11.glGetInteger(GL11.GL_ALPHA_TEST_FUNC);
        this.riftflux$cloudFadePreviousAlphaReference = GL11.glGetFloat(GL11.GL_ALPHA_TEST_REF);
        GL11.glAlphaFunc(
                this.riftflux$cloudFadePreviousAlphaFunction,
                this.riftflux$cloudFadePreviousAlphaReference * opacity
        );
        this.riftflux$cloudFadeChangedAlphaThreshold = true;
    }

    @Unique
    private void riftflux$endCloudFadeAlphaThreshold() {
        if (this.riftflux$cloudFadePassDepth <= 0) {
            this.riftflux$cloudFadePassDepth = 0;
            return;
        }

        --this.riftflux$cloudFadePassDepth;
        if (this.riftflux$cloudFadePassDepth == 0 && this.riftflux$cloudFadeChangedAlphaThreshold) {
            GL11.glAlphaFunc(
                    this.riftflux$cloudFadePreviousAlphaFunction,
                    this.riftflux$cloudFadePreviousAlphaReference
            );
            this.riftflux$cloudFadeChangedAlphaThreshold = false;
        }
    }
}
