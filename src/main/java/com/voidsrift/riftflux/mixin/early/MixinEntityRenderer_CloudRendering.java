package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.client.photomode.IsometricPhotoModeController;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.RenderGlobal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public abstract class MixinEntityRenderer_CloudRendering {

    @Shadow
    private Minecraft mc;

    @Shadow
    protected abstract void renderCloudsCheck(RenderGlobal renderGlobal, float partialTicks);

    @Unique
    private boolean riftflux$terrainRenderingStarted;

    @Unique
    private boolean riftflux$deferredCloudRender;

    @Inject(method = "renderWorld(FJ)V", at = @At("HEAD"))
    private void riftflux$beginWorldRender(float partialTicks, long finishTimeNano, CallbackInfo ci) {
        this.riftflux$terrainRenderingStarted = false;
        this.riftflux$deferredCloudRender = false;
    }

    @Redirect(
            method = "renderWorld(FJ)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/EntityRenderer;renderCloudsCheck(Lnet/minecraft/client/renderer/RenderGlobal;F)V"
            ),
            require = 0
    )
    private void riftflux$deferCloudsUntilAfterTerrain(
            EntityRenderer entityRenderer,
            RenderGlobal renderGlobal,
            float partialTicks
    ) {
        if (IsometricPhotoModeController.instance().isActive()) {
            return;
        }

        if (!this.riftflux$terrainRenderingStarted) {
            this.riftflux$deferredCloudRender = true;
            return;
        }

        this.renderCloudsCheck(renderGlobal, partialTicks);
    }

    @Inject(
            method = "renderWorld(FJ)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/RenderGlobal;sortAndRender(Lnet/minecraft/entity/EntityLivingBase;ID)I",
                    ordinal = 0
            ),
            require = 1
    )
    private void riftflux$markTerrainRenderingStarted(
            float partialTicks,
            long finishTimeNano,
            CallbackInfo ci
    ) {
        this.riftflux$terrainRenderingStarted = true;
    }

    @Inject(
            method = "renderWorld(FJ)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraftforge/client/ForgeHooksClient;dispatchRenderLast(Lnet/minecraft/client/renderer/RenderGlobal;F)V",
                    remap = false
            ),
            require = 1
    )
    private void riftflux$renderDeferredCloudsAfterTerrain(
            float partialTicks,
            long finishTimeNano,
            CallbackInfo ci
    ) {
        if (!this.riftflux$deferredCloudRender || IsometricPhotoModeController.instance().isActive()) {
            return;
        }

        this.riftflux$deferredCloudRender = false;
        this.renderCloudsCheck(this.mc.renderGlobal, partialTicks);
    }
}
