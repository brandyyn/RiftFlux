package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.client.photomode.IsometricPhotoModeController;
import com.voidsrift.riftflux.client.photomode.AngelicaPhotoModeCompat;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.util.Vec3;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = RenderGlobal.class, priority = 900)
public abstract class MixinRenderGlobal_IsometricPhotoMode {

    @Shadow
    private Minecraft mc;

    @Inject(method = "renderSky(F)V", at = @At("HEAD"), require = 0)
    private void riftflux$disableFogBeforeSkyPass(float partialTicks, CallbackInfo ci) {
        if (IsometricPhotoModeController.instance().isActive()) {
            this.riftflux$drawPhotoModeSkyBackdrop(partialTicks);
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

    @Unique
    private void riftflux$drawPhotoModeSkyBackdrop(float partialTicks) {
        if (this.mc == null || this.mc.theWorld == null || this.mc.theWorld.provider == null
                || this.mc.theWorld.provider.hasNoSky || !this.mc.theWorld.provider.isSurfaceWorld()) {
            return;
        }

        float red;
        float green;
        float blue;
        if (this.mc.theWorld.getStarBrightness(partialTicks) > 0.05F) {
            red = 0.0F;
            green = 0.0F;
            blue = 0.0F;
        } else {
            Vec3 skyColor = this.mc.theWorld.getSkyColor(this.mc.renderViewEntity, partialTicks);
            red = skyColor == null ? 0.0F : (float) skyColor.xCoord;
            green = skyColor == null ? 0.0F : (float) skyColor.yCoord;
            blue = skyColor == null ? 0.0F : (float) skyColor.zCoord;
        }

        GL11.glPushAttrib(GL11.GL_ENABLE_BIT | GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_FOG);
        GL11.glDepthMask(false);

        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        GL11.glOrtho(0.0D, 1.0D, 0.0D, 1.0D, -1.0D, 1.0D);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();

        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.setColorOpaque_F(red, green, blue);
        tessellator.addVertex(0.0D, 0.0D, 0.0D);
        tessellator.addVertex(1.0D, 0.0D, 0.0D);
        tessellator.addVertex(1.0D, 1.0D, 0.0D);
        tessellator.addVertex(0.0D, 1.0D, 0.0D);
        tessellator.draw();

        GL11.glPopMatrix();
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glPopMatrix();
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glDepthMask(true);
        GL11.glPopAttrib();
    }

}
