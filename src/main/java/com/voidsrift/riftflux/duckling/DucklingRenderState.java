package com.voidsrift.riftflux.duckling;

import java.nio.ByteBuffer;
import net.geckominecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.Entity;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;

final class DucklingRenderState {
    private static final int CLIENT_ALL_ATTRIB_BITS = -1;
    private static final int GUI_ENTITY_BRIGHTNESS = 0x00F000F0;
    private static final float LIGHTMAP_TEXTURE_SCALE = 0.00390625F;
    private static final String WDMLA_ROOT = "com.gtnewhorizons.wdmla.";
    private static final String WAILA_ROOT = "mcp.mobius.waila.";
    private static final ByteBuffer BYTE_BUFFER = BufferUtils.createByteBuffer(4);
    private static final DucklingLightmapCompat LIGHTMAP_COMPAT = createLightmapCompat();

    static final class Snapshot {
        private final int matrixMode;
        private final int activeTextureUnit;
        private final int clientActiveTextureUnit;
        private final float brightnessX;
        private final float brightnessY;
        private final boolean alphaTest;
        private final int alphaFunc;
        private final float alphaRef;
        private final boolean depthTest;
        private final int depthFunc;
        private final boolean depthMask;
        private final boolean blend;
        private final int blendSrcRgb;
        private final int blendDstRgb;
        private final int blendSrcAlpha;
        private final int blendDstAlpha;
        private final int blendEquation;
        private final boolean cullFace;
        private final boolean lighting;
        private final boolean colorMaterial;
        private final boolean rescaleNormal;
        private final boolean normalize;
        private final boolean polygonOffsetFill;
        private final float polygonOffsetFactor;
        private final float polygonOffsetUnits;
        private final boolean colorLogic;
        private final int shadeModel;
        private final boolean colorMaskRed;
        private final boolean colorMaskGreen;
        private final boolean colorMaskBlue;
        private final boolean colorMaskAlpha;
        private final boolean defaultTexture2D;
        private final int defaultTextureBinding;
        private final int defaultTextureEnvMode;
        private final boolean lightmapTexture2D;
        private final int lightmapTextureBinding;
        private final int lightmapTextureEnvMode;
        private final long rpleLightMapRGB64;

        private Snapshot(
                int matrixMode,
                int activeTextureUnit,
                int clientActiveTextureUnit,
                float brightnessX,
                float brightnessY,
                boolean alphaTest,
                int alphaFunc,
                float alphaRef,
                boolean depthTest,
                int depthFunc,
                boolean depthMask,
                boolean blend,
                int blendSrcRgb,
                int blendDstRgb,
                int blendSrcAlpha,
                int blendDstAlpha,
                int blendEquation,
                boolean cullFace,
                boolean lighting,
                boolean colorMaterial,
                boolean rescaleNormal,
                boolean normalize,
                boolean polygonOffsetFill,
                float polygonOffsetFactor,
                float polygonOffsetUnits,
                boolean colorLogic,
                int shadeModel,
                boolean colorMaskRed,
                boolean colorMaskGreen,
                boolean colorMaskBlue,
                boolean colorMaskAlpha,
                boolean defaultTexture2D,
                int defaultTextureBinding,
                int defaultTextureEnvMode,
                boolean lightmapTexture2D,
                int lightmapTextureBinding,
                int lightmapTextureEnvMode,
                long rpleLightMapRGB64
        ) {
            this.matrixMode = matrixMode;
            this.activeTextureUnit = activeTextureUnit;
            this.clientActiveTextureUnit = clientActiveTextureUnit;
            this.brightnessX = brightnessX;
            this.brightnessY = brightnessY;
            this.alphaTest = alphaTest;
            this.alphaFunc = alphaFunc;
            this.alphaRef = alphaRef;
            this.depthTest = depthTest;
            this.depthFunc = depthFunc;
            this.depthMask = depthMask;
            this.blend = blend;
            this.blendSrcRgb = blendSrcRgb;
            this.blendDstRgb = blendDstRgb;
            this.blendSrcAlpha = blendSrcAlpha;
            this.blendDstAlpha = blendDstAlpha;
            this.blendEquation = blendEquation;
            this.cullFace = cullFace;
            this.lighting = lighting;
            this.colorMaterial = colorMaterial;
            this.rescaleNormal = rescaleNormal;
            this.normalize = normalize;
            this.polygonOffsetFill = polygonOffsetFill;
            this.polygonOffsetFactor = polygonOffsetFactor;
            this.polygonOffsetUnits = polygonOffsetUnits;
            this.colorLogic = colorLogic;
            this.shadeModel = shadeModel;
            this.colorMaskRed = colorMaskRed;
            this.colorMaskGreen = colorMaskGreen;
            this.colorMaskBlue = colorMaskBlue;
            this.colorMaskAlpha = colorMaskAlpha;
            this.defaultTexture2D = defaultTexture2D;
            this.defaultTextureBinding = defaultTextureBinding;
            this.defaultTextureEnvMode = defaultTextureEnvMode;
            this.lightmapTexture2D = lightmapTexture2D;
            this.lightmapTextureBinding = lightmapTextureBinding;
            this.lightmapTextureEnvMode = lightmapTextureEnvMode;
            this.rpleLightMapRGB64 = rpleLightMapRGB64;
        }
    }

    private static final class TextureUnitState {
        private final boolean texture2D;
        private final int textureBinding;
        private final int textureEnvMode;

        private TextureUnitState(boolean texture2D, int textureBinding, int textureEnvMode) {
            this.texture2D = texture2D;
            this.textureBinding = textureBinding;
            this.textureEnvMode = textureEnvMode;
        }
    }

    private DucklingRenderState() {
    }

    static Snapshot capture() {
        int activeTextureUnit = GL11.glGetInteger(GL13.GL_ACTIVE_TEXTURE);
        TextureUnitState defaultTexture = captureTextureUnit(OpenGlHelper.defaultTexUnit);
        TextureUnitState lightmapTexture = captureTextureUnit(OpenGlHelper.lightmapTexUnit);
        restoreRawActiveTexture(activeTextureUnit);
        boolean[] colorMask = captureColorMask();
        return new Snapshot(
                GL11.glGetInteger(GL11.GL_MATRIX_MODE),
                activeTextureUnit,
                GL11.glGetInteger(GL13.GL_CLIENT_ACTIVE_TEXTURE),
                OpenGlHelper.lastBrightnessX,
                OpenGlHelper.lastBrightnessY,
                GL11.glIsEnabled(GL11.GL_ALPHA_TEST),
                GL11.glGetInteger(GL11.GL_ALPHA_TEST_FUNC),
                GL11.glGetFloat(GL11.GL_ALPHA_TEST_REF),
                GL11.glIsEnabled(GL11.GL_DEPTH_TEST),
                GL11.glGetInteger(GL11.GL_DEPTH_FUNC),
                GL11.glGetBoolean(GL11.GL_DEPTH_WRITEMASK),
                GL11.glIsEnabled(GL11.GL_BLEND),
                GL11.glGetInteger(GL14.GL_BLEND_SRC_RGB),
                GL11.glGetInteger(GL14.GL_BLEND_DST_RGB),
                GL11.glGetInteger(GL14.GL_BLEND_SRC_ALPHA),
                GL11.glGetInteger(GL14.GL_BLEND_DST_ALPHA),
                GL11.glGetInteger(GL14.GL_BLEND_EQUATION),
                GL11.glIsEnabled(GL11.GL_CULL_FACE),
                GL11.glIsEnabled(GL11.GL_LIGHTING),
                GL11.glIsEnabled(GL11.GL_COLOR_MATERIAL),
                GL11.glIsEnabled(GL12.GL_RESCALE_NORMAL),
                GL11.glIsEnabled(GL11.GL_NORMALIZE),
                GL11.glIsEnabled(GL11.GL_POLYGON_OFFSET_FILL),
                GL11.glGetFloat(GL11.GL_POLYGON_OFFSET_FACTOR),
                GL11.glGetFloat(GL11.GL_POLYGON_OFFSET_UNITS),
                GL11.glIsEnabled(GL11.GL_COLOR_LOGIC_OP),
                GL11.glGetInteger(GL11.GL_SHADE_MODEL),
                colorMask[0],
                colorMask[1],
                colorMask[2],
                colorMask[3],
                defaultTexture.texture2D,
                defaultTexture.textureBinding,
                defaultTexture.textureEnvMode,
                lightmapTexture.texture2D,
                lightmapTexture.textureBinding,
                lightmapTexture.textureEnvMode,
                LIGHTMAP_COMPAT.lastLightMapRGB64()
        );
    }

    static void pushRenderAttribs() {
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
    }

    static void popRenderAttribs() {
        GL11.glPopAttrib();
    }

    static void pushRenderClientAttribs() {
        GL11.glPushClientAttrib(CLIENT_ALL_ATTRIB_BITS);
    }

    static void popRenderClientAttribs() {
        GL11.glPopClientAttrib();
    }

    static void prepareForRender() {
        OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
        setActiveTextureUnit(OpenGlHelper.defaultTexUnit);
        resetClientArrays();
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        restoreOpaqueEntityState();
    }

    static void prepareForEntityRender(Entity entity, float partialTicks) {
        if (entity != null) {
            int brightness = resolveEntityBrightness(entity, partialTicks);
            prepareTexturedLightmap((float) (brightness & 65535), (float) (brightness >> 16));
        }
        prepareForRender();
    }

    static int resolveEntityBrightness(Entity entity, float partialTicks) {
        if (entity == null || isTooltipPreviewRender()) {
            return GUI_ENTITY_BRIGHTNESS;
        }
        return entity.getBrightnessForRender(partialTicks);
    }

    static boolean isTooltipPreviewRender() {
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        for (int i = 0; i < stack.length; i++) {
            String className = stack[i].getClassName();
            if (className == null) {
                continue;
            }
            if (className.startsWith(WDMLA_ROOT) || className.startsWith(WAILA_ROOT)) {
                return true;
            }
        }
        return false;
    }

    static void pushRenderMatrices() {
        setActiveTextureUnit(OpenGlHelper.lightmapTexUnit);
        GL11.glMatrixMode(GL11.GL_TEXTURE);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        setActiveTextureUnit(OpenGlHelper.defaultTexUnit);
        GL11.glMatrixMode(GL11.GL_TEXTURE);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glPushMatrix();
    }

    static void popRenderMatrices() {
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glPopMatrix();
        setActiveTextureUnit(OpenGlHelper.defaultTexUnit);
        GL11.glMatrixMode(GL11.GL_TEXTURE);
        GL11.glPopMatrix();
        setActiveTextureUnit(OpenGlHelper.lightmapTexUnit);
        GL11.glMatrixMode(GL11.GL_TEXTURE);
        GL11.glPopMatrix();
    }

    static void restoreAfterRender(Snapshot snapshot) {
        restoreSnapshot(snapshot);
        normalizeAfterEntityRender();
    }

    private static void restoreSnapshot(Snapshot snapshot) {
        restoreGlState(snapshot);
        restoreLightmap(snapshot);
        OpenGlHelper.setClientActiveTexture(snapshot.clientActiveTextureUnit);
        restoreTextureUnit(OpenGlHelper.defaultTexUnit, snapshot.defaultTexture2D, snapshot.defaultTextureBinding, snapshot.defaultTextureEnvMode);
        restoreTextureUnit(OpenGlHelper.lightmapTexUnit, snapshot.lightmapTexture2D, snapshot.lightmapTextureBinding, snapshot.lightmapTextureEnvMode);
        setActiveTextureUnit(snapshot.activeTextureUnit);
        GL11.glMatrixMode(snapshot.matrixMode);
    }

    private static void restoreLightmap(Snapshot snapshot) {
        if (LIGHTMAP_COMPAT.restoreLightMapTextureCoords(snapshot.rpleLightMapRGB64)) {
            return;
        }
        restoreLightmap(snapshot.brightnessX, snapshot.brightnessY);
    }

    private static void restoreLightmap(float brightnessX, float brightnessY) {
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, brightnessX, brightnessY);
    }

    private static void restoreGlState(Snapshot snapshot) {
        restoreAlpha(snapshot);
        restoreDepth(snapshot);
        restoreBlend(snapshot);
        restoreBooleanState(GL11.GL_CULL_FACE, snapshot.cullFace);
        if (snapshot.cullFace) {
            GlStateManager.enableCull();
        } else {
            GlStateManager.disableCull();
        }
        restoreBooleanState(GL11.GL_LIGHTING, snapshot.lighting);
        if (snapshot.lighting) {
            GlStateManager.enableLighting();
        } else {
            GlStateManager.disableLighting();
        }
        restoreBooleanState(GL11.GL_COLOR_MATERIAL, snapshot.colorMaterial);
        if (snapshot.colorMaterial) {
            GlStateManager.enableColorMaterial();
        } else {
            GlStateManager.disableColorMaterial();
        }
        restoreBooleanState(GL12.GL_RESCALE_NORMAL, snapshot.rescaleNormal);
        if (snapshot.rescaleNormal) {
            GlStateManager.enableRescaleNormal();
        } else {
            GlStateManager.disableRescaleNormal();
        }
        restoreBooleanState(GL11.GL_NORMALIZE, snapshot.normalize);
        if (snapshot.normalize) {
            GlStateManager.enableNormalize();
        } else {
            GlStateManager.disableNormalize();
        }
        restoreBooleanState(GL11.GL_POLYGON_OFFSET_FILL, snapshot.polygonOffsetFill);
        if (snapshot.polygonOffsetFill) {
            GlStateManager.enablePolygonOffset();
        } else {
            GlStateManager.disablePolygonOffset();
        }
        GL11.glPolygonOffset(snapshot.polygonOffsetFactor, snapshot.polygonOffsetUnits);
        GlStateManager.doPolygonOffset(snapshot.polygonOffsetFactor, snapshot.polygonOffsetUnits);
        restoreBooleanState(GL11.GL_COLOR_LOGIC_OP, snapshot.colorLogic);
        if (snapshot.colorLogic) {
            GlStateManager.enableColorLogic();
        } else {
            GlStateManager.disableColorLogic();
        }
        GL11.glShadeModel(snapshot.shadeModel);
        GlStateManager.shadeModel(snapshot.shadeModel);
        GL11.glColorMask(snapshot.colorMaskRed, snapshot.colorMaskGreen, snapshot.colorMaskBlue, snapshot.colorMaskAlpha);
        GlStateManager.colorMask(snapshot.colorMaskRed, snapshot.colorMaskGreen, snapshot.colorMaskBlue, snapshot.colorMaskAlpha);
        resetColorToOpaqueWhite();
    }

    private static void restoreAlpha(Snapshot snapshot) {
        restoreBooleanState(GL11.GL_ALPHA_TEST, snapshot.alphaTest);
        if (snapshot.alphaTest) {
            GlStateManager.enableAlpha();
        } else {
            GlStateManager.disableAlpha();
        }
        GL11.glAlphaFunc(snapshot.alphaFunc, snapshot.alphaRef);
        GlStateManager.alphaFunc(snapshot.alphaFunc, snapshot.alphaRef);
    }

    private static void restoreDepth(Snapshot snapshot) {
        restoreBooleanState(GL11.GL_DEPTH_TEST, snapshot.depthTest);
        if (snapshot.depthTest) {
            GlStateManager.enableDepth();
        } else {
            GlStateManager.disableDepth();
        }
        GL11.glDepthFunc(snapshot.depthFunc);
        GlStateManager.depthFunc(snapshot.depthFunc);
        GL11.glDepthMask(snapshot.depthMask);
        GlStateManager.depthMask(snapshot.depthMask);
    }

    private static void restoreBlend(Snapshot snapshot) {
        restoreBooleanState(GL11.GL_BLEND, snapshot.blend);
        if (snapshot.blend) {
            GlStateManager.enableBlend();
        } else {
            GlStateManager.disableBlend();
        }
        GL14.glBlendEquation(snapshot.blendEquation);
        GlStateManager.glBlendEquation(snapshot.blendEquation);
        OpenGlHelper.glBlendFunc(snapshot.blendSrcRgb, snapshot.blendDstRgb, snapshot.blendSrcAlpha, snapshot.blendDstAlpha);
        GlStateManager.tryBlendFuncSeparate(snapshot.blendSrcRgb, snapshot.blendDstRgb, snapshot.blendSrcAlpha, snapshot.blendDstAlpha);
    }

    private static void restoreTextureUnit(int textureUnit, boolean texture2D, int textureBinding, int textureEnvMode) {
        setActiveTextureUnit(textureUnit);
        restoreBooleanState(GL11.GL_TEXTURE_2D, texture2D);
        if (texture2D) {
            GlStateManager.enableTexture2D();
        } else {
            GlStateManager.disableTexture2D();
        }
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureBinding);
        GlStateManager.bindTexture(textureBinding);
        GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, textureEnvMode);
        GlStateManager.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, textureEnvMode);
    }

    private static void restoreBooleanState(int capability, boolean enabled) {
        if (enabled) {
            GL11.glEnable(capability);
        } else {
            GL11.glDisable(capability);
        }
    }

    private static TextureUnitState captureTextureUnit(int textureUnit) {
        restoreRawActiveTexture(textureUnit);
        return new TextureUnitState(
                GL11.glIsEnabled(GL11.GL_TEXTURE_2D),
                GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D),
                GL11.glGetTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE)
        );
    }

    private static boolean[] captureColorMask() {
        BYTE_BUFFER.clear();
        GL11.glGetBoolean(GL11.GL_COLOR_WRITEMASK, BYTE_BUFFER);
        return new boolean[] {
                BYTE_BUFFER.get(0) != 0,
                BYTE_BUFFER.get(1) != 0,
                BYTE_BUFFER.get(2) != 0,
                BYTE_BUFFER.get(3) != 0
        };
    }

    private static void restoreRawActiveTexture(int textureUnit) {
        OpenGlHelper.setActiveTexture(textureUnit);
    }

    private static void resetClientArrays() {
        OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
        disableClientState(GL11.GL_VERTEX_ARRAY);
        disableClientState(GL11.GL_NORMAL_ARRAY);
        disableClientState(GL11.GL_COLOR_ARRAY);
        disableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
        OpenGlHelper.setClientActiveTexture(OpenGlHelper.lightmapTexUnit);
        disableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
        OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
    }

    private static void disableClientState(int capability) {
        GL11.glDisableClientState(capability);
        GlStateManager.glDisableClientState(capability);
    }

    static void prepareTexturedLightmap(float brightnessX, float brightnessY) {
        if (LIGHTMAP_COMPAT.setLightMapTextureCoords(brightnessX, brightnessY)) {
            prepareDefaultTextureUnit();
            GL11.glMatrixMode(GL11.GL_MODELVIEW);
            resetColorToOpaqueWhite();
            return;
        }
        prepareLightmapTextureUnit();
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, brightnessX, brightnessY);
        prepareDefaultTextureUnit();
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        resetColorToOpaqueWhite();
    }

    private static void prepareLightmapTextureUnit() {
        setActiveTextureUnit(OpenGlHelper.lightmapTexUnit);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GlStateManager.enableTexture2D();
        restoreVanillaLightmapTextureCombiner();
        GL11.glMatrixMode(GL11.GL_TEXTURE);
        GL11.glLoadIdentity();
        GL11.glScalef(LIGHTMAP_TEXTURE_SCALE, LIGHTMAP_TEXTURE_SCALE, LIGHTMAP_TEXTURE_SCALE);
        GL11.glTranslatef(8.0F, 8.0F, 8.0F);
    }

    private static void prepareDefaultTextureUnit() {
        setActiveTextureUnit(OpenGlHelper.defaultTexUnit);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GlStateManager.enableTexture2D();
        GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_MODULATE);
        GlStateManager.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_MODULATE);
        GL11.glMatrixMode(GL11.GL_TEXTURE);
        GL11.glLoadIdentity();
    }

    private static void restoreVanillaLightmapTextureCombiner() {
        GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL13.GL_COMBINE);
        GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_COMBINE_RGB, GL11.GL_MODULATE);
        GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_SOURCE0_RGB, GL13.GL_PREVIOUS);
        GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_SOURCE1_RGB, GL11.GL_TEXTURE);
        GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_OPERAND0_RGB, GL11.GL_SRC_COLOR);
        GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_OPERAND1_RGB, GL11.GL_SRC_COLOR);
        GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_COMBINE_ALPHA, GL11.GL_REPLACE);
        GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_SOURCE0_ALPHA, GL13.GL_PREVIOUS);
        GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_OPERAND0_ALPHA, GL11.GL_SRC_ALPHA);
    }

    private static void restoreRenderableEntityState() {
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GlStateManager.enableAlpha();
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GlStateManager.enableDepth();
        GL11.glDepthFunc(GL11.GL_LEQUAL);
        GlStateManager.depthFunc(GL11.GL_LEQUAL);
        GL11.glDepthMask(true);
        GlStateManager.depthMask(true);
        GL11.glDisable(GL11.GL_POLYGON_OFFSET_FILL);
        GlStateManager.disablePolygonOffset();
        GL11.glPolygonOffset(0.0F, 0.0F);
        GlStateManager.doPolygonOffset(0.0F, 0.0F);
        GL11.glDisable(GL11.GL_COLOR_LOGIC_OP);
        GlStateManager.disableColorLogic();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GlStateManager.enableTexture2D();
        GL11.glEnable(GL11.GL_LIGHTING);
        GlStateManager.enableLighting();
        GL11.glDisable(GL11.GL_COLOR_MATERIAL);
        GlStateManager.disableColorMaterial();
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GlStateManager.enableRescaleNormal();
        GL11.glDisable(GL11.GL_NORMALIZE);
        GlStateManager.disableNormalize();
        GL11.glShadeModel(GL11.GL_SMOOTH);
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        GL11.glDisable(GL11.GL_BLEND);
        GlStateManager.disableBlend();
        GL11.glEnable(GL11.GL_CULL_FACE);
        GlStateManager.enableCull();
        GL14.glBlendEquation(GL14.GL_FUNC_ADD);
        GlStateManager.glBlendEquation(GL14.GL_FUNC_ADD);
        OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
        GL11.glColorMask(true, true, true, true);
        GlStateManager.colorMask(true, true, true, true);
        restoreStandardEntityLighting();
        resetColorToOpaqueWhite();
    }

    private static void restoreOpaqueEntityState() {
        restoreRenderableEntityState();
        GL11.glDisable(GL11.GL_BLEND);
        GlStateManager.disableBlend();
        GL11.glEnable(GL11.GL_CULL_FACE);
        GlStateManager.enableCull();
    }

    private static void normalizeAfterEntityRender() {
        resetClientArrays();
        OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);

        if (!LIGHTMAP_COMPAT.usesPackedLightmap()) {
            prepareLightmapTextureUnit();
        }
        prepareDefaultTextureUnit();
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        resetColorToOpaqueWhite();
    }

    private static void restoreStandardEntityLighting() {
        RenderHelper.enableStandardItemLighting();
        GlStateManager.enableLighting();
        GlStateManager.enableLight(0);
        GlStateManager.enableLight(1);
        GL11.glEnable(GL11.GL_COLOR_MATERIAL);
        GlStateManager.enableColorMaterial();
        GL11.glColorMaterial(GL11.GL_FRONT_AND_BACK, GL11.GL_AMBIENT_AND_DIFFUSE);
        GlStateManager.colorMaterial(GL11.GL_FRONT_AND_BACK, GL11.GL_AMBIENT_AND_DIFFUSE);
    }

    private static void setActiveTextureUnit(int textureUnit) {
        OpenGlHelper.setActiveTexture(textureUnit);
        GlStateManager.setActiveTexture(textureUnit);
    }

    private static void resetColorToOpaqueWhite() {
        GlStateManager.resetColor();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private static DucklingLightmapCompat createLightmapCompat() {
        if (!hasClass("com.falsepattern.rple.api.client.RPLETessBrightnessUtil")) {
            return new NoopLightmapCompat();
        }

        return new DucklingRPLELightmapCompat();
    }

    private static boolean hasClass(String className) {
        try {
            ClassLoader loader = DucklingRenderState.class.getClassLoader();
            return loader.getResource(className.replace('.', '/') + ".class") != null;
        } catch (Throwable ignored) {
            return false;
        }
    }

    private static final class NoopLightmapCompat implements DucklingLightmapCompat {
        @Override
        public long lastLightMapRGB64() {
            return 0L;
        }

        @Override
        public boolean restoreLightMapTextureCoords(long rgb64) {
            return false;
        }

        @Override
        public boolean setLightMapTextureCoords(float brightnessX, float brightnessY) {
            return false;
        }

        @Override
        public boolean usesPackedLightmap() {
            return false;
        }
    }
}
