package com.voidsrift.riftflux.mixin.early;

import com.gtnewhorizons.angelica.glsm.GLStateManager;
import com.voidsrift.riftflux.client.sky.CloudTimeFadeHelper;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.shader.TesselatorVertexState;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.nio.ByteOrder;
import java.nio.FloatBuffer;

@Mixin(value = RenderGlobal.class, priority = 900)
public abstract class MixinRenderGlobal_CloudTimeFade {

    @Shadow
    private WorldClient theWorld;

    @Unique
    private FloatBuffer riftflux$cloudFogColor;

    @Unique
    private FloatBuffer riftflux$scaledCloudFogColor;

    @Redirect(
            method = "renderClouds(F)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/Tessellator;setColorRGBA_F(FFFF)V"
            ),
            require = 0
    )
    private void riftflux$fadeFastCloudAlpha(
            Tessellator tessellator,
            float red,
            float green,
            float blue,
            float alpha,
            float partialTicks
    ) {
        tessellator.setColorRGBA_F(
                red,
                green,
                blue,
                alpha * CloudTimeFadeHelper.getOpacity(this.theWorld, partialTicks)
        );
    }

    @Redirect(
            method = "renderCloudsFancy(F)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/Tessellator;setColorRGBA_F(FFFF)V"
            ),
            require = 0
    )
    private void riftflux$fadeFancyCloudAlpha(
            Tessellator tessellator,
            float red,
            float green,
            float blue,
            float alpha,
            float partialTicks
    ) {
        tessellator.setColorRGBA_F(red, green, blue, alpha);
    }

    @Redirect(
            method = "renderCloudsFancy(F)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/Tessellator;draw()I"
            ),
            require = 0
    )
    private int riftflux$drawFancyCloudsWithNativeOpacity(
            Tessellator tessellator,
        float partialTicks
    ) {
        float opacity = CloudTimeFadeHelper.getOpacity(this.theWorld, partialTicks);
        if (opacity >= 0.9999F || !this.riftflux$isCloudColorPass()) {
            return tessellator.draw();
        }

        TesselatorVertexState state = tessellator.getVertexState(0.0F, 0.0F, 0.0F);
        if (state == null || !state.getHasColor() || state.getVertexCount() <= 0) {
            return tessellator.draw();
        }

        int[] rawBuffer = state.getRawBuffer();
        int rawBufferIndex = state.getRawBufferIndex();
        int originalDepthFunction = GL11.glGetInteger(GL11.GL_DEPTH_FUNC);
        boolean originalDepthMask = GL11.glGetBoolean(GL11.GL_DEPTH_WRITEMASK);
        boolean originalAlphaTest = GLStateManager.glIsEnabled(GL11.GL_ALPHA_TEST);
        int originalBlendSourceRgb = GL11.glGetInteger(GL14.GL_BLEND_SRC_RGB);
        int originalBlendDestinationRgb = GL11.glGetInteger(GL14.GL_BLEND_DST_RGB);
        int originalBlendSourceAlpha = GL11.glGetInteger(GL14.GL_BLEND_SRC_ALPHA);
        int originalBlendDestinationAlpha = GL11.glGetInteger(GL14.GL_BLEND_DST_ALPHA);
        boolean fogEnabled = GLStateManager.glIsEnabled(GL11.GL_FOG);

        if (fogEnabled) {
            this.riftflux$ensureCloudFogBuffers();
            this.riftflux$cloudFogColor.clear();
            GL11.glGetFloat(GL11.GL_FOG_COLOR, this.riftflux$cloudFogColor);
            this.riftflux$cloudFogColor.rewind();
        }

        int bytesDrawn = 0;
        try {
            this.riftflux$scaleVertexAlpha(rawBuffer, rawBufferIndex, opacity);
            tessellator.setVertexState(state);

            GLStateManager.glDisable(GL11.GL_ALPHA_TEST);
            GLStateManager.glDepthMask(false);
            GLStateManager.glDepthFunc(GL11.GL_EQUAL);
            GLStateManager.glBlendFuncSeparate(
                    GL11.GL_ZERO,
                    GL11.GL_ONE_MINUS_SRC_ALPHA,
                    GL11.GL_ZERO,
                    GL11.GL_ONE_MINUS_SRC_ALPHA
            );
            bytesDrawn += tessellator.draw();

            this.riftflux$prepareAdditiveCloudColors(rawBuffer, rawBufferIndex, opacity);
            tessellator.startDrawingQuads();
            tessellator.setVertexState(state);
            if (originalAlphaTest) {
                GLStateManager.glEnable(GL11.GL_ALPHA_TEST);
            }
            GLStateManager.glBlendFuncSeparate(
                    GL11.GL_SRC_ALPHA,
                    GL11.GL_ONE,
                    GL11.GL_ONE,
                    GL11.GL_ONE
            );
            if (fogEnabled) {
                this.riftflux$setScaledFogColor(opacity);
            }
            bytesDrawn += tessellator.draw();
            return bytesDrawn;
        } finally {
            if (fogEnabled) {
                this.riftflux$cloudFogColor.rewind();
                GLStateManager.glFog(GL11.GL_FOG_COLOR, this.riftflux$cloudFogColor);
            }
            GLStateManager.glBlendFuncSeparate(
                    originalBlendSourceRgb,
                    originalBlendDestinationRgb,
                    originalBlendSourceAlpha,
                    originalBlendDestinationAlpha
            );
            GLStateManager.glDepthFunc(originalDepthFunction);
            GLStateManager.glDepthMask(originalDepthMask);
            if (originalAlphaTest) {
                GLStateManager.glEnable(GL11.GL_ALPHA_TEST);
            } else {
                GLStateManager.glDisable(GL11.GL_ALPHA_TEST);
            }
        }
    }

    @Unique
    private boolean riftflux$isCloudColorPass() {
        return GLStateManager.glGetBoolean(GL11.GL_COLOR_WRITEMASK);
    }

    @Unique
    private void riftflux$ensureCloudFogBuffers() {
        if (this.riftflux$cloudFogColor == null) {
            this.riftflux$cloudFogColor = BufferUtils.createFloatBuffer(4);
        }
        if (this.riftflux$scaledCloudFogColor == null) {
            this.riftflux$scaledCloudFogColor = BufferUtils.createFloatBuffer(4);
        }
    }

    @Unique
    private void riftflux$scaleVertexAlpha(int[] rawBuffer, int rawBufferIndex, float opacity) {
        boolean littleEndian = ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN;
        for (int colorIndex = 5; colorIndex < rawBufferIndex; colorIndex += 8) {
            int color = rawBuffer[colorIndex];
            int alpha = littleEndian ? color >>> 24 & 255 : color & 255;
            int scaledAlpha = Math.round((float) alpha * opacity);
            rawBuffer[colorIndex] = littleEndian
                    ? color & 0x00FFFFFF | scaledAlpha << 24
                    : color & 0xFFFFFF00 | scaledAlpha;
        }
    }

    @Unique
    private void riftflux$prepareAdditiveCloudColors(int[] rawBuffer, int rawBufferIndex, float opacity) {
        boolean littleEndian = ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN;
        for (int colorIndex = 5; colorIndex < rawBufferIndex; colorIndex += 8) {
            int color = rawBuffer[colorIndex];
            if (littleEndian) {
                int red = Math.round((float) (color & 255) * opacity);
                int green = Math.round((float) (color >>> 8 & 255) * opacity);
                int blue = Math.round((float) (color >>> 16 & 255) * opacity);
                rawBuffer[colorIndex] = 204 << 24 | blue << 16 | green << 8 | red;
            } else {
                int red = Math.round((float) (color >>> 24 & 255) * opacity);
                int green = Math.round((float) (color >>> 16 & 255) * opacity);
                int blue = Math.round((float) (color >>> 8 & 255) * opacity);
                rawBuffer[colorIndex] = red << 24 | green << 16 | blue << 8 | 204;
            }
        }
    }

    @Unique
    private void riftflux$setScaledFogColor(float opacity) {
        this.riftflux$cloudFogColor.rewind();
        this.riftflux$scaledCloudFogColor.clear();
        this.riftflux$scaledCloudFogColor.put(this.riftflux$cloudFogColor.get() * opacity);
        this.riftflux$scaledCloudFogColor.put(this.riftflux$cloudFogColor.get() * opacity);
        this.riftflux$scaledCloudFogColor.put(this.riftflux$cloudFogColor.get() * opacity);
        this.riftflux$scaledCloudFogColor.put(this.riftflux$cloudFogColor.get());
        this.riftflux$scaledCloudFogColor.flip();
        GLStateManager.glFog(GL11.GL_FOG_COLOR, this.riftflux$scaledCloudFogColor);
    }
}
