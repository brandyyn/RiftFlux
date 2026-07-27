package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.client.photomode.IsometricPhotoModeController;
import com.voidsrift.riftflux.client.photomode.AngelicaPhotoModeCompat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Project;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public abstract class MixinEntityRenderer_IsometricPhotoMode {

    private static final boolean RIFTFLUX_HAS_CELERITAS_STACK =
            riftflux$hasClass("com.gtnewhorizons.angelica.AngelicaMod")
                    || riftflux$hasClass("com.ventooth.beddium.modules.TerrainRendering.CeleritasWorldRenderer");

    private boolean riftflux$photoModeCullFaceWasEnabled;
    private boolean riftflux$restoreCullFaceAfterPhotoWorld;

    @Shadow
    private Minecraft mc;

    @Shadow
    private float farPlaneDistance;

    @Shadow
    protected abstract float getFOVModifier(float partialTicks, boolean useFovSetting);

    @Shadow
    public abstract void setupOverlayRendering();

    @Redirect(
            method = "setupCameraTransform(FI)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/lwjgl/util/glu/Project;gluPerspective(FFFF)V",
                    remap = false
            )
    )
    private void riftflux$useOrthographicProjection(float fov, float aspect, float zNear, float zFar, float partialTicks, int pass) {
        IsometricPhotoModeController controller = IsometricPhotoModeController.instance();
        if (!controller.isActive()) {
            Project.gluPerspective(fov, aspect, zNear, zFar);
            return;
        }

        double halfHeight = controller.getOrthographicViewHeight(partialTicks) * 0.5D;
        double halfWidth = halfHeight * (double) aspect;
        double depthRange = Math.max((double) zFar * 4.0D, halfHeight * 16.0D);
        depthRange = Math.max(depthRange, 4096.0D);
        GL11.glOrtho(-halfWidth, halfWidth, -halfHeight, halfHeight, -depthRange, depthRange);
    }

    @Redirect(
            method = "updateCameraAndRender",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/entity/EntityClientPlayerMP;setAngles(FF)V"
            )
    )
    private void riftflux$freezeMouseLookDuringCameraControl(EntityClientPlayerMP player, float yaw, float pitch) {
        IsometricPhotoModeController controller = IsometricPhotoModeController.instance();
        if (!controller.isCapturingInput()) {
            player.setAngles(yaw, pitch);
        }
    }

    @Inject(method = "updateCameraAndRender", at = @At("RETURN"))
    private void riftflux$renderPhotoModeControlStatus(float partialTicks, CallbackInfo ci) {
        IsometricPhotoModeController controller = IsometricPhotoModeController.instance();
        if (controller.isActive()) {
            this.setupOverlayRendering();
            controller.renderControlStatusOverlay(partialTicks);
        }
    }

    @Redirect(
            method = "renderWorld(FJ)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/ActiveRenderInfo;updateRenderInfo(Lnet/minecraft/entity/player/EntityPlayer;Z)V"
            ),
            require = 0
    )
    private void riftflux$updateRenderInfoFromPhotoCamera(EntityPlayer player, boolean thirdPerson) {
        EntityPlayer viewPlayer = this.riftflux$getPhotoViewPlayer(player);
        ActiveRenderInfo.updateRenderInfo(viewPlayer, thirdPerson);
    }

    @Redirect(
            method = "renderWorld(FJ)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/RenderGlobal;renderSky(F)V"
            ),
            require = 0
    )
    private void riftflux$renderSkyWithPerspective(RenderGlobal renderGlobal, float partialTicks) {
        if (!IsometricPhotoModeController.instance().isActive()) {
            renderGlobal.renderSky(partialTicks);
            return;
        }

        this.riftflux$pushPerspectiveProjection(partialTicks, false);
        try {
            renderGlobal.renderSky(partialTicks);
        } finally {
            this.riftflux$popPerspectiveProjection();
        }
    }

    @Redirect(
            method = "renderWorld(FJ)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/particle/EffectRenderer;renderParticles(Lnet/minecraft/entity/Entity;F)V"
            ),
            require = 0
    )
    private void riftflux$renderParticlesFromPhotoCamera(EffectRenderer effectRenderer, Entity entity, float partialTicks) {
        effectRenderer.renderParticles(this.riftflux$getPhotoViewEntity(entity), partialTicks);
    }

    @Redirect(
            method = "renderWorld(FJ)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/particle/EffectRenderer;renderLitParticles(Lnet/minecraft/entity/Entity;F)V"
            ),
            require = 0
    )
    private void riftflux$renderLitParticlesFromPhotoCamera(EffectRenderer effectRenderer, Entity entity, float partialTicks) {
        effectRenderer.renderLitParticles(this.riftflux$getPhotoViewEntity(entity), partialTicks);
    }

    @Inject(method = "renderWorld(FJ)V", at = @At("HEAD"))
    private void riftflux$disableCullFaceForPhotoWorld(float partialTicks, long finishTimeNano, CallbackInfo ci) {
        IsometricPhotoModeController.instance().applyRenderTweenState(partialTicks);
        if (IsometricPhotoModeController.instance().isActive()) {
            AngelicaPhotoModeCompat.enforceNoFog();
            GL11.glDisable(GL11.GL_FOG);
            if (this.riftflux$shouldDisableCullFaceForPhotoWorld()) {
                this.riftflux$photoModeCullFaceWasEnabled = GL11.glIsEnabled(GL11.GL_CULL_FACE);
                this.riftflux$restoreCullFaceAfterPhotoWorld = true;
                GL11.glDisable(GL11.GL_CULL_FACE);
            }
        }
    }

    @Inject(method = "renderWorld(FJ)V", at = @At("RETURN"))
    private void riftflux$restoreCullFaceAfterPhotoWorld(float partialTicks, long finishTimeNano, CallbackInfo ci) {
        IsometricPhotoModeController.instance().restoreRenderTweenState();
        if (this.riftflux$restoreCullFaceAfterPhotoWorld) {
            if (this.riftflux$photoModeCullFaceWasEnabled) {
                GL11.glEnable(GL11.GL_CULL_FACE);
            } else {
                GL11.glDisable(GL11.GL_CULL_FACE);
            }
            this.riftflux$restoreCullFaceAfterPhotoWorld = false;
            this.riftflux$photoModeCullFaceWasEnabled = false;
        }
    }

    @Inject(
            method = "renderWorld(FJ)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraftforge/client/ForgeHooksClient;renderFirstPersonHand(Lnet/minecraft/client/renderer/RenderGlobal;FI)Z",
                    remap = false
            ),
            require = 0
    )
    private void riftflux$capturePhotoModeCenterDepthBeforeHandClear(float partialTicks, long finishTimeNano, CallbackInfo ci) {
        IsometricPhotoModeController.instance().captureRenderedCenterDepth(partialTicks);
    }

    @Inject(method = "setupFog(IF)V", at = @At("HEAD"), cancellable = true)
    private void riftflux$disableFogInPhotoMode(int fogMode, float partialTicks, CallbackInfo ci) {
        if (IsometricPhotoModeController.instance().isActive()) {
            AngelicaPhotoModeCompat.enforceNoFog();
            GL11.glDisable(GL11.GL_FOG);
            ci.cancel();
        }
    }

    @Redirect(
            method = "setupFog(IF)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/lwjgl/opengl/GL11;glEnable(I)V",
                    remap = false
            ),
            require = 0
    )
    private void riftflux$preventFogEnableInSetupFog(int capability) {
        if (IsometricPhotoModeController.instance().isActive() && capability == GL11.GL_FOG) {
            return;
        }

        GL11.glEnable(capability);
    }

    @Redirect(
            method = "renderWorld(FJ)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/lwjgl/opengl/GL11;glEnable(I)V",
                    remap = false
            ),
            require = 0
    )
    private void riftflux$preventFogEnableInRenderWorld(int capability) {
        if (IsometricPhotoModeController.instance().isActive() && capability == GL11.GL_FOG) {
            return;
        }
        if (capability == GL11.GL_CULL_FACE && this.riftflux$shouldDisableCullFaceForPhotoWorld()) {
            return;
        }

        GL11.glEnable(capability);
    }

    @Inject(method = "renderHand(FI)V", at = @At("HEAD"), cancellable = true)
    private void riftflux$hideHandInPhotoMode(float partialTicks, int pass, CallbackInfo ci) {
        if (IsometricPhotoModeController.instance().isActive()) {
            ci.cancel();
        }
    }

    @Inject(method = "renderRainSnow(F)V", at = @At("HEAD"), cancellable = true)
    private void riftflux$hideWeatherInPhotoMode(float partialTicks, CallbackInfo ci) {
        if (IsometricPhotoModeController.instance().isActive()) {
            ci.cancel();
        }
    }

    @Inject(method = "addRainParticles", at = @At("HEAD"), cancellable = true, require = 0)
    private void riftflux$hideRainSplashParticlesInPhotoMode(CallbackInfo ci) {
        if (IsometricPhotoModeController.instance().isActive()) {
            ci.cancel();
        }
    }

    private Entity riftflux$getPhotoViewEntity(Entity fallback) {
        Entity viewEntity = net.minecraft.client.Minecraft.getMinecraft().renderViewEntity;
        return IsometricPhotoModeController.instance().isActive() && viewEntity != null ? viewEntity : fallback;
    }

    private EntityPlayer riftflux$getPhotoViewPlayer(EntityPlayer fallback) {
        Entity viewEntity = net.minecraft.client.Minecraft.getMinecraft().renderViewEntity;
        if (IsometricPhotoModeController.instance().isActive() && viewEntity instanceof EntityPlayer) {
            return (EntityPlayer) viewEntity;
        }

        return fallback;
    }

    private void riftflux$pushPerspectiveProjection(float partialTicks, boolean useFovSetting) {
        float aspect = this.mc.displayHeight > 0 ? (float) this.mc.displayWidth / (float) this.mc.displayHeight : 1.0F;
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        Project.gluPerspective(
                this.getFOVModifier(partialTicks, useFovSetting),
                aspect,
                0.05F,
                Math.max(this.farPlaneDistance * 2.0F, 1024.0F)
        );
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
    }

    private void riftflux$popPerspectiveProjection() {
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glPopMatrix();
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
    }

    private boolean riftflux$shouldDisableCullFaceForPhotoWorld() {
        return IsometricPhotoModeController.instance().isActive()
                && !RIFTFLUX_HAS_CELERITAS_STACK
                && (this.mc == null
                || this.mc.theWorld == null
                || this.mc.theWorld.provider == null
                || (!this.mc.theWorld.provider.isHellWorld && this.mc.theWorld.provider.dimensionId != -1));
    }

    private static boolean riftflux$hasClass(String className) {
        try {
            ClassLoader loader = MixinEntityRenderer_IsometricPhotoMode.class.getClassLoader();
            return loader.getResource(className.replace('.', '/') + ".class") != null;
        } catch (Throwable ignored) {
            return false;
        }
    }
}
