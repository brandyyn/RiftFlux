package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.client.sky.FogStateCompat;
import com.voidsrift.riftflux.client.sky.SunriseSkyTintHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.util.Vec3;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.FloatBuffer;

@Mixin(RenderGlobal.class)
public abstract class MixinRenderGlobal_SunriseTint {

    private static final FloatBuffer RIFTFLUX_FOG_BUFFER = BufferUtils.createFloatBuffer(4);

    @Shadow
    private WorldClient theWorld;

    @Redirect(
            method = "renderSky(F)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/lwjgl/opengl/GL11;glColor3f(FFF)V",
                    ordinal = 0,
                    remap = false
            ),
            require = 0
    )
    private void riftflux$tintSkyColorPrimary(float red, float green, float blue, float partialTicks) {
        float[] tint = SunriseSkyTintHelper.blendSkyTint(this.theWorld, Minecraft.getMinecraft(), partialTicks, red, green, blue);
        GL11.glColor3f(tint[0], tint[1], tint[2]);
    }

    @Redirect(
            method = "renderSky(F)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/lwjgl/opengl/GL11;glColor3f(FFF)V",
                    ordinal = 1,
                    remap = false
            ),
            require = 0
    )
    private void riftflux$tintSkyColorSecondary(float red, float green, float blue, float partialTicks) {
        float[] tint = SunriseSkyTintHelper.blendSkyTint(this.theWorld, Minecraft.getMinecraft(), partialTicks, red, green, blue);
        GL11.glColor3f(tint[0], tint[1], tint[2]);
    }

    @Redirect(
            method = "renderSky(F)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/lwjgl/opengl/GL11;glColor3f(FFF)V",
                    ordinal = 3,
                    remap = false
            ),
            require = 0
    )
    private void riftflux$tintSkyColorLowerColored(float red, float green, float blue, float partialTicks) {
        float[] tint;
        if (SunriseSkyTintHelper.shouldUseBetaStyleBiomeFog(this.theWorld)) {
            tint = SunriseSkyTintHelper.resolveBetaStyleLowerSkyColor(this.theWorld, Minecraft.getMinecraft(), partialTicks);
        } else {
            tint = SunriseSkyTintHelper.resolveSkyTintedColor(this.theWorld, Minecraft.getMinecraft(), partialTicks);
        }
        if (tint == null || tint.length < 3) {
            tint = SunriseSkyTintHelper.blendSkyTint(this.theWorld, Minecraft.getMinecraft(), partialTicks, red, green, blue);
        }
        GL11.glColor3f(tint[0], tint[1], tint[2]);
    }

    @Redirect(
            method = "renderSky(F)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/lwjgl/opengl/GL11;glColor3f(FFF)V",
                    ordinal = 4,
                    remap = false
            ),
            require = 0
    )
    private void riftflux$tintSkyColorLowerVanilla(float red, float green, float blue, float partialTicks) {
        float[] tint;
        if (SunriseSkyTintHelper.shouldUseBetaStyleBiomeFog(this.theWorld)) {
            tint = SunriseSkyTintHelper.resolveBetaStyleLowerSkyColor(this.theWorld, Minecraft.getMinecraft(), partialTicks);
        } else {
            tint = SunriseSkyTintHelper.resolveSkyTintedColor(this.theWorld, Minecraft.getMinecraft(), partialTicks);
        }
        if (tint == null || tint.length < 3) {
            tint = SunriseSkyTintHelper.blendSkyTint(this.theWorld, Minecraft.getMinecraft(), partialTicks, red, green, blue);
        }
        GL11.glColor3f(tint[0], tint[1], tint[2]);
    }

    @Redirect(
            method = "renderSky(F)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/lwjgl/opengl/GL11;glCallList(I)V",
                    ordinal = 0,
                    remap = false
            ),
            require = 0
    )
    private void riftflux$applyTopSkyFogColor(int list, float partialTicks) {
        if (SunriseSkyTintHelper.shouldUseBetaStyleBiomeFog(this.theWorld)) {
            float[] skyTint = SunriseSkyTintHelper.resolveSkyTintedColor(this.theWorld, Minecraft.getMinecraft(), partialTicks);
            float[] fogTint = SunriseSkyTintHelper.resolveBetaStyleBiomeFogColor(this.theWorld, Minecraft.getMinecraft(), partialTicks);
            riftflux$setFogColor(skyTint);
            GL11.glCallList(list);
            riftflux$setFogColor(fogTint);
            return;
        }
        GL11.glCallList(list);
    }

    @Redirect(
            method = "renderSky(F)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/lwjgl/opengl/GL11;glCallList(I)V",
                    ordinal = 3,
                    remap = false
            ),
            require = 0
    )
    private void riftflux$forceLowerSkyCallColor(int list, float partialTicks) {
        float[] tint;
        if (SunriseSkyTintHelper.shouldUseBetaStyleBiomeFog(this.theWorld)) {
            tint = SunriseSkyTintHelper.resolveBetaStyleLowerSkyColor(this.theWorld, Minecraft.getMinecraft(), partialTicks);
        } else {
            tint = SunriseSkyTintHelper.resolveSkyTintedColor(this.theWorld, Minecraft.getMinecraft(), partialTicks);
        }
        if (tint != null && tint.length >= 3) {
            GL11.glColor3f(tint[0], tint[1], tint[2]);
        }
        GL11.glCallList(list);
        if (SunriseSkyTintHelper.shouldUseBetaStyleBiomeFog(this.theWorld)) {
            riftflux$setFogColor(SunriseSkyTintHelper.resolveBetaStyleLowerSkyColor(this.theWorld, Minecraft.getMinecraft(), partialTicks));
        }
    }

    private static void riftflux$setFogColor(float[] rgb) {
        if (rgb == null || rgb.length < 3) {
            return;
        }
        RIFTFLUX_FOG_BUFFER.clear();
        RIFTFLUX_FOG_BUFFER.put(rgb[0]).put(rgb[1]).put(rgb[2]).put(1.0F);
        RIFTFLUX_FOG_BUFFER.flip();
        FogStateCompat.fog(GL11.GL_FOG_COLOR, RIFTFLUX_FOG_BUFFER);
    }

    @Inject(method = "renderSky(F)V", at = @At("RETURN"), require = 0)
    private void riftflux$drawDedicatedBetaHorizon(float partialTicks, CallbackInfo ci) {
        if (!SunriseSkyTintHelper.shouldUseBetaStyleBiomeFog(this.theWorld)) {
            return;
        }

        Minecraft mc = Minecraft.getMinecraft();
        if (mc == null || mc.renderViewEntity == null || this.theWorld == null || !this.theWorld.provider.isSurfaceWorld()) {
            return;
        }

        float[] lowerSkyTint = SunriseSkyTintHelper.resolveBetaStyleLowerSkyColor(this.theWorld, mc, partialTicks);
        float[] skyTint = SunriseSkyTintHelper.resolveSkyTintedColor(this.theWorld, mc, partialTicks);
        if (lowerSkyTint == null || skyTint == null) {
            return;
        }

        Vec3 cameraPos = mc.renderViewEntity.getPosition(partialTicks);
        if (cameraPos == null) {
            return;
        }

        double horizonDelta = cameraPos.yCoord - this.theWorld.getHorizon();
        float horizonY = (float) (-horizonDelta);

        GL11.glPushMatrix();
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glDisable(GL11.GL_FOG);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDepthMask(false);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        riftflux$drawHorizonGradientBand(horizonY, skyTint, lowerSkyTint);

        GL11.glDepthMask(true);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_FOG);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glPopMatrix();
    }

    private static void riftflux$drawHorizonGradientBand(float horizonY, float[] topTint, float[] bottomTint) {
        final Tessellator tessellator = Tessellator.instance;
        final int segments = 64;
        final double radius = 384.0D;
        final double topY = horizonY + 120.0D;
        final double bottomY = horizonY - 24.0D;

        GL11.glShadeModel(GL11.GL_SMOOTH);
        tessellator.startDrawing(8);
        for (int i = 0; i <= segments; i++) {
            double angle = (double) i * Math.PI * 2.0D / (double) segments;
            double x = Math.sin(angle) * radius;
            double z = Math.cos(angle) * radius;
            tessellator.setColorRGBA_F(topTint[0], topTint[1], topTint[2], 1.0F);
            tessellator.addVertex(x, topY, z);
            tessellator.setColorRGBA_F(bottomTint[0], bottomTint[1], bottomTint[2], 1.0F);
            tessellator.addVertex(x, bottomY, z);
        }
        tessellator.draw();
        GL11.glShadeModel(GL11.GL_FLAT);
    }
}
