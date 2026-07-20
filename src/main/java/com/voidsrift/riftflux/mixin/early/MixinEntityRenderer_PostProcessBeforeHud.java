package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.client.PostProcessRenderer;
import net.minecraft.client.renderer.EntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public abstract class MixinEntityRenderer_PostProcessBeforeHud {
    @Inject(method = "renderWorld(FJ)V", at = @At("HEAD"), require = 0)
    private void riftflux$beginPersistentPostProcessTarget(float partialTicks, long finishTimeNano, CallbackInfo ci) {
        PostProcessRenderer.beginWorldRender(partialTicks);
    }

    @Inject(
            method = "renderWorld(FJ)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/EntityRenderer;renderHand(FI)V",
                    shift = At.Shift.BEFORE
            ),
            require = 0
    )
    private void riftflux$renderPostProcessBloomBeforeHand(float partialTicks, long finishTimeNano, CallbackInfo ci) {
        PostProcessRenderer.renderWorldBloomBeforeHand(partialTicks);
    }

    @Inject(
            method = "renderWorld(FJ)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/EntityRenderer;renderHand(FI)V",
                    shift = At.Shift.AFTER
            ),
            require = 0
    )
    private void riftflux$renderPostProcessAfterHand(float partialTicks, long finishTimeNano, CallbackInfo ci) {
        PostProcessRenderer.renderBeforeHud(partialTicks);
    }

    @Inject(method = "renderWorld(FJ)V", at = @At("RETURN"), require = 0)
    private void riftflux$finishPersistentPostProcessTarget(float partialTicks, long finishTimeNano, CallbackInfo ci) {
        PostProcessRenderer.finishWorldRender(partialTicks);
    }
}
