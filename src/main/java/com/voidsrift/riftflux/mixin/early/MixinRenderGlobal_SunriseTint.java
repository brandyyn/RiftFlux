package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.client.sky.FogStateCompat;
import com.voidsrift.riftflux.client.sky.SunriseSkyTintHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.RenderGlobal;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

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
        float[] tint = SunriseSkyTintHelper.resolveSkyTintedColor(this.theWorld, Minecraft.getMinecraft(), partialTicks);
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
        float[] tint = SunriseSkyTintHelper.resolveSkyTintedColor(this.theWorld, Minecraft.getMinecraft(), partialTicks);
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
        float[] skyTint = this.riftflux$resolveSkyPassTint(partialTicks);
        float[] fogTint = this.riftflux$resolveActiveFogColor(partialTicks);
        if (skyTint != null && fogTint != null) {
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
        float[] tint = this.riftflux$resolveSkyPassTint(partialTicks);
        if (tint != null && tint.length >= 3) {
            GL11.glColor3f(tint[0], tint[1], tint[2]);
        }
        float[] fogTint = this.riftflux$resolveActiveFogColor(partialTicks);
        if (tint != null && fogTint != null) {
            riftflux$setFogColor(tint);
            GL11.glCallList(list);
            riftflux$setFogColor(fogTint);
            return;
        }
        GL11.glCallList(list);
    }

    private float[] riftflux$resolveActiveFogColor(float partialTicks) {
        Minecraft mc = Minecraft.getMinecraft();
        if (this.theWorld == null || mc == null) {
            return null;
        }
        if (SunriseSkyTintHelper.shouldUseBetaStyleBiomeFog(this.theWorld)) {
            return SunriseSkyTintHelper.resolveEffectiveBetaStyleFogColor(this.theWorld, mc, partialTicks, null);
        }
        if (SunriseSkyTintHelper.shouldMatchFogToSky(this.theWorld)) {
            return SunriseSkyTintHelper.resolveEffectiveSkyMatchingFogColor(this.theWorld, mc, partialTicks, null);
        }
        if (SunriseSkyTintHelper.shouldUseBlackNightFog(this.theWorld, partialTicks)) {
            float[] fogTint = SunriseSkyTintHelper.resolveBlackNightFogColor(this.theWorld, mc, partialTicks);
            if (fogTint == null || fogTint.length < 3) {
                return fogTint;
            }
            return SunriseSkyTintHelper.applyNightFogFloor(this.theWorld, partialTicks, fogTint);
        }
        return null;
    }

    private float[] riftflux$resolveSkyPassTint(float partialTicks) {
        Minecraft mc = Minecraft.getMinecraft();
        if (this.theWorld == null || mc == null) {
            return null;
        }
        if (SunriseSkyTintHelper.shouldUseBetaStyleBiomeFog(this.theWorld)) {
            return SunriseSkyTintHelper.resolveBetaStyleClearColor(this.theWorld, mc, partialTicks);
        }
        return SunriseSkyTintHelper.resolveSkyTintedColor(this.theWorld, mc, partialTicks);
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

}
