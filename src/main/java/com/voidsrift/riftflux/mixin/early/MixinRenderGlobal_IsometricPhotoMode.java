package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.client.photomode.IsometricPhotoModeController;
import com.voidsrift.riftflux.client.photomode.AngelicaPhotoModeCompat;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.util.AxisAlignedBB;
import org.objectweb.asm.Opcodes;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderGlobal.class)
public abstract class MixinRenderGlobal_IsometricPhotoMode {

    @Unique
    private static final boolean RIFTFLUX_HAS_ANGELICA = riftflux$hasClass("com.gtnewhorizons.angelica.AngelicaMod");

    @Shadow
    private Minecraft mc;

    @Shadow
    private WorldRenderer[] worldRenderers;

    @Shadow
    private boolean occlusionEnabled;

    @Inject(method = "renderSky(F)V", at = @At("HEAD"), require = 0)
    private void riftflux$disableFogBeforeSkyPass(float partialTicks, CallbackInfo ci) {
        if (IsometricPhotoModeController.instance().isActive()) {
            AngelicaPhotoModeCompat.enforceNoFog();
            GL11.glDisable(GL11.GL_FOG);
        }
    }

    @Inject(method = "renderSky(F)V", at = @At("RETURN"), require = 0)
    private void riftflux$disableFogAfterSkyPass(float partialTicks, CallbackInfo ci) {
        if (IsometricPhotoModeController.instance().isActive()) {
            AngelicaPhotoModeCompat.enforceNoFog();
            GL11.glDisable(GL11.GL_FOG);
        }
    }

    @Inject(method = "hasCloudFog", at = @At("HEAD"), cancellable = true)
    private void riftflux$disableCloudFogInPhotoMode(double x, double y, double z, float partialTicks, CallbackInfoReturnable<Boolean> cir) {
        if (IsometricPhotoModeController.instance().isActive()) {
            cir.setReturnValue(Boolean.FALSE);
        }
    }

    @Redirect(
            method = "renderSky(F)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/lwjgl/opengl/GL11;glColor4f(FFFF)V",
                    ordinal = 0,
                    remap = false
            ),
            require = 0
    )
    private void riftflux$hideSunAndMoonInPhotoMode(float red, float green, float blue, float alpha) {
        GL11.glColor4f(red, green, blue, IsometricPhotoModeController.instance().isActive() ? 0.0F : alpha);
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

    @Inject(method = "renderEntities", at = @At("HEAD"), require = 0)
    private void riftflux$disableFogBeforeEntityPass(EntityLivingBase viewEntity, ICamera camera, float partialTicks, CallbackInfo ci) {
        if (IsometricPhotoModeController.instance().isActive()) {
            AngelicaPhotoModeCompat.enforceNoFog();
            GL11.glDisable(GL11.GL_FOG);
        }
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
        if (!this.riftflux$useVanillaPhotoChunkFallback() || this.worldRenderers == null) {
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
        if (this.riftflux$useVanillaPhotoChunkFallback()) {
            return false;
        }

        return this.occlusionEnabled;
    }

    @Unique
    private EntityLivingBase riftflux$getPhotoModeChunkAnchor(EntityLivingBase fallback) {
        if (!this.riftflux$useVanillaPhotoChunkFallback()) {
            return fallback;
        }

        return this.mc != null && this.mc.thePlayer != null ? this.mc.thePlayer : fallback;
    }

    @Unique
    private boolean riftflux$useVanillaPhotoChunkFallback() {
        return IsometricPhotoModeController.instance().isActive() && !RIFTFLUX_HAS_ANGELICA;
    }

    @Unique
    private static boolean riftflux$hasClass(String className) {
        try {
            ClassLoader loader = MixinRenderGlobal_IsometricPhotoMode.class.getClassLoader();
            return loader.getResource(className.replace('.', '/') + ".class") != null;
        } catch (Throwable ignored) {
            return false;
        }
    }

}
