package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.client.photomode.IsometricPhotoModeController;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderGlobal.class)
public abstract class MixinRenderGlobal_VanillaPhotoModeFallback {

    @Shadow
    private Minecraft mc;

    @Shadow
    private WorldRenderer[] worldRenderers;

    @Shadow
    private boolean occlusionEnabled;

    @Redirect(
            method = "renderEntities",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/culling/ICamera;isBoundingBoxInFrustum(Lnet/minecraft/util/AxisAlignedBB;)Z"
            ),
            require = 0
    )
    private boolean riftflux$disableEntityFrustumCulling(ICamera camera, AxisAlignedBB box) {
        if (IsometricPhotoModeController.instance().isActive()) {
            return true;
        }

        return camera.isBoundingBoxInFrustum(box);
    }

    @ModifyVariable(method = "sortAndRender", at = @At("HEAD"), ordinal = 0, argsOnly = true, require = 0)
    private EntityLivingBase riftflux$anchorChunkGridToPlayer(EntityLivingBase renderViewEntity) {
        return this.riftflux$getPhotoModeChunkAnchor(renderViewEntity);
    }

    @ModifyVariable(method = "updateRenderers", at = @At("HEAD"), ordinal = 0, argsOnly = true, require = 0)
    private EntityLivingBase riftflux$anchorChunkUpdatesToPlayer(EntityLivingBase renderViewEntity) {
        return this.riftflux$getPhotoModeChunkAnchor(renderViewEntity);
    }

    @Redirect(
            method = "loadRenderers",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/Minecraft;renderViewEntity:Lnet/minecraft/entity/EntityLivingBase;",
                    opcode = Opcodes.GETFIELD
            ),
            require = 0
    )
    private EntityLivingBase riftflux$anchorLoadRenderersToPlayer(Minecraft minecraft) {
        return this.riftflux$getPhotoModeChunkAnchor(minecraft.renderViewEntity);
    }

    @Inject(method = "clipRenderersByFrustum", at = @At("HEAD"), cancellable = true, require = 0)
    private void riftflux$keepLoadedChunksInFrustum(ICamera camera, float partialTicks, CallbackInfo ci) {
        if (!IsometricPhotoModeController.instance().isActive() || this.worldRenderers == null) {
            return;
        }

        for (WorldRenderer worldRenderer : this.worldRenderers) {
            if (worldRenderer == null || worldRenderer.skipAllRenderPasses()) {
                continue;
            }

            worldRenderer.isInFrustum = true;
            worldRenderer.isVisible = true;
        }

        ci.cancel();
    }

    @Redirect(
            method = {"sortAndRender", "renderSortedRenderers"},
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/renderer/RenderGlobal;occlusionEnabled:Z",
                    opcode = Opcodes.GETFIELD
            ),
            require = 0
    )
    private boolean riftflux$disableVanillaChunkOcclusionInPhotoMode(RenderGlobal renderGlobal) {
        if (IsometricPhotoModeController.instance().isActive()) {
            return false;
        }

        return this.occlusionEnabled;
    }

    @Unique
    private EntityLivingBase riftflux$getPhotoModeChunkAnchor(EntityLivingBase fallback) {
        return IsometricPhotoModeController.instance().isActive() && this.mc != null && this.mc.thePlayer != null
                ? this.mc.thePlayer
                : fallback;
    }
}
