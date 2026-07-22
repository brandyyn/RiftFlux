package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.client.PostProcessRenderer;
import com.voidsrift.riftflux.client.chatbubbles.ChatBubblesClient;
import com.voidsrift.riftflux.client.worldtooltips.WorldTooltipClient;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraftforge.client.ForgeHooksClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public abstract class MixinEntityRenderer_PostProcessBeforeHud {
    @Inject(method = "renderWorld(FJ)V", at = @At("HEAD"), require = 0)
    private void riftflux$beginPersistentPostProcessTarget(float partialTicks, long finishTimeNano, CallbackInfo ci) {
        ChatBubblesClient.beginDeferredRenderFrame();
        WorldTooltipClient.beginDeferredRenderFrame();
        PostProcessRenderer.beginWorldRender(partialTicks);
    }

    @Redirect(
            method = "renderWorld(FJ)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraftforge/client/ForgeHooksClient;renderFirstPersonHand(Lnet/minecraft/client/renderer/RenderGlobal;FI)Z",
                    remap = false
            ),
            require = 0
    )
    private boolean riftflux$renderPostProcessAroundForgeHand(RenderGlobal renderGlobal, float partialTicks, int renderPass) {
        PostProcessRenderer.renderWorldBloomBeforeHand(partialTicks);
        if (!ModConfig.postProcessBloomAffectsHeldItem) {
            ChatBubblesClient.renderDeferred();
            WorldTooltipClient.renderDeferred();
        }

        boolean customHandRendered = ForgeHooksClient.renderFirstPersonHand(renderGlobal, partialTicks, renderPass);
        if (customHandRendered) {
            riftflux$renderPostProcessAfterHand(partialTicks);
        }
        return customHandRendered;
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
        riftflux$renderPostProcessAfterHand(partialTicks);
    }

    private void riftflux$renderPostProcessAfterHand(float partialTicks) {
        PostProcessRenderer.prepareBloomExcludedWorldOverlays(partialTicks);
        if (ModConfig.postProcessBloomAffectsHeldItem) {
            ChatBubblesClient.renderDeferred();
            WorldTooltipClient.renderDeferred();
        }
        PostProcessRenderer.renderBeforeHud(partialTicks);
    }

    @Inject(method = "renderWorld(FJ)V", at = @At("RETURN"), require = 0)
    private void riftflux$finishPersistentPostProcessTarget(float partialTicks, long finishTimeNano, CallbackInfo ci) {
        PostProcessRenderer.finishWorldRender(partialTicks);
    }
}
