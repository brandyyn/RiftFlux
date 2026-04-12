package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.client.photomode.IsometricPhotoModeController;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.util.AxisAlignedBB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RenderGlobal.class)
public abstract class MixinRenderGlobal_IsometricPhotoMode {

    @Inject(method = "hasCloudFog", at = @At("HEAD"), cancellable = true)
    private void riftflux$disableCloudFogInPhotoMode(double x, double y, double z, float partialTicks, CallbackInfoReturnable<Boolean> cir) {
        if (IsometricPhotoModeController.instance().isActive()) {
            cir.setReturnValue(Boolean.FALSE);
        }
    }

    @Redirect(
            method = "renderEntities",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/Entity;isInRangeToRender3d(DDD)Z"
            ),
            require = 0
    )
    private boolean riftflux$disableEntityDistanceCulling(Entity entity, double x, double y, double z) {
        if (IsometricPhotoModeController.instance().isActive()) {
            return true;
        }

        return entity.isInRangeToRender3d(x, y, z);
    }

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
}
