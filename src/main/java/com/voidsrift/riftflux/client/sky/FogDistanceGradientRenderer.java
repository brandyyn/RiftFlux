package com.voidsrift.riftflux.client.sky;

import com.voidsrift.riftflux.ModConfig;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import java.nio.ByteBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GLContext;

public class FogDistanceGradientRenderer {
    private static final float NEAR_PLANE = 0.05F;
    private int depthTexture = -1;
    private int textureWidth = -1;
    private int textureHeight = -1;
    private int shaderProgram = -1;
    private int uniformDepth = -1;
    private int uniformNearPlane = -1;
    private int uniformFarPlane = -1;
    private int uniformProjectionScaleX = -1;
    private int uniformProjectionScaleY = -1;
    private int uniformFogStart = -1;
    private int uniformFogEnd = -1;
    private int uniformGradientStart = -1;
    private int uniformGradientEnd = -1;
    private int uniformStrength = -1;
    private int uniformBaseColor = -1;
    private int uniformFarColor = -1;
    private boolean shaderFailed;
    private boolean warnedNoShaderSupport;
    private boolean warnedShaderFailure;

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        if (!ModConfig.celestialFogDistanceGradient
                || ModConfig.celestialFogDistanceGradientStrengthPercent <= 0.0F
                || shaderFailed) {
            return;
        }
        if (!GLContext.getCapabilities().OpenGL20) {
            shaderFailed = true;
            if (!warnedNoShaderSupport) {
                warnedNoShaderSupport = true;
                FMLLog.warning("[RiftFlux] Celestial fog distance gradient disabled: OpenGL 2.0 shader support is unavailable.");
            }
            return;
        }

        Minecraft mc = Minecraft.getMinecraft();
        WorldClient world = mc == null ? null : mc.theWorld;
        if (!SunriseSkyTintHelper.shouldUseFogDistanceGradient(world)
                || !FogDistanceGradientState.isReady(world)
                || mc == null
                || !(mc.renderViewEntity instanceof EntityLivingBase)
                || mc.displayWidth <= 0
                || mc.displayHeight <= 0) {
            return;
        }

        float[] farColor = SunriseSkyTintHelper.resolveDistanceGradientFarFogColor(world, mc, event.partialTicks, FogDistanceGradientState.getFogColor());
        if (farColor == null || farColor.length < 3) {
            return;
        }

        int program = getShaderProgram();
        if (program <= 0) {
            return;
        }

        ensureTextures(mc.displayWidth, mc.displayHeight);
        copyDepthTexture(mc.displayWidth, mc.displayHeight);
        renderGradient(program, FogDistanceGradientState.getFogColor(), farColor);
    }

    private void ensureTextures(int width, int height) {
        if (depthTexture > 0 && textureWidth == width && textureHeight == height) {
            return;
        }
        if (depthTexture > 0) {
            GL11.glDeleteTextures(depthTexture);
        }

        textureWidth = width;
        textureHeight = height;
        depthTexture = createTexture(width, height);
    }

    private int createTexture(int width, int height) {
        int texture = GL11.glGenTextures();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_COMPARE_MODE, GL11.GL_NONE);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL14.GL_DEPTH_COMPONENT24, width, height, 0, GL11.GL_DEPTH_COMPONENT, GL11.GL_UNSIGNED_INT, (ByteBuffer) null);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
        return texture;
    }

    private void copyDepthTexture(int width, int height) {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, depthTexture);
        GL11.glCopyTexSubImage2D(GL11.GL_TEXTURE_2D, 0, 0, 0, 0, 0, width, height);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
    }

    private void renderGradient(int program, float[] baseColor, float[] farColor) {
        float fogStart = FogDistanceGradientState.getFogStart();
        float fogEnd = Math.max(fogStart + 1.0F, FogDistanceGradientState.getFogEnd());
        float gradientStart = fogStart + (fogEnd - fogStart) * clamp01(ModConfig.celestialFogDistanceGradientStartPercent / 100.0F);
        float gradientEnd = fogStart + (fogEnd - fogStart) * clamp01(ModConfig.celestialFogDistanceGradientEndPercent / 100.0F);
        if (gradientEnd <= gradientStart + 1.0F) {
            gradientEnd = gradientStart + 1.0F;
        }

        float projectionFar = Math.max(FogDistanceGradientState.getProjectionFar(), fogEnd + 1.0F);
        float strength = clamp01(ModConfig.celestialFogDistanceGradientStrengthPercent / 100.0F);

        GL11.glPushAttrib(GL11.GL_ENABLE_BIT | GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_TEXTURE_BIT | GL11.GL_CURRENT_BIT);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(false);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_FOG);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        GL11.glOrtho(0.0D, 1.0D, 0.0D, 1.0D, -1.0D, 1.0D);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();

        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, depthTexture);

        GL20.glUseProgram(program);
        GL20.glUniform1i(uniformDepth, 0);
        GL20.glUniform1f(uniformNearPlane, NEAR_PLANE);
        GL20.glUniform1f(uniformFarPlane, projectionFar);
        GL20.glUniform1f(uniformProjectionScaleX, FogDistanceGradientState.getProjectionScaleX());
        GL20.glUniform1f(uniformProjectionScaleY, FogDistanceGradientState.getProjectionScaleY());
        GL20.glUniform1f(uniformFogStart, fogStart);
        GL20.glUniform1f(uniformFogEnd, fogEnd);
        GL20.glUniform1f(uniformGradientStart, gradientStart);
        GL20.glUniform1f(uniformGradientEnd, gradientEnd);
        GL20.glUniform1f(uniformStrength, strength);
        GL20.glUniform3f(uniformBaseColor, baseColor[0], baseColor[1], baseColor[2]);
        GL20.glUniform3f(uniformFarColor, farColor[0], farColor[1], farColor[2]);

        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2f(0.0F, 0.0F);
        GL11.glVertex2f(0.0F, 0.0F);
        GL11.glTexCoord2f(1.0F, 0.0F);
        GL11.glVertex2f(1.0F, 0.0F);
        GL11.glTexCoord2f(1.0F, 1.0F);
        GL11.glVertex2f(1.0F, 1.0F);
        GL11.glTexCoord2f(0.0F, 1.0F);
        GL11.glVertex2f(0.0F, 1.0F);
        GL11.glEnd();

        GL20.glUseProgram(0);
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
        GL11.glPopMatrix();
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glPopMatrix();
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glDepthMask(true);
        GL11.glPopAttrib();
    }

    private int getShaderProgram() {
        if (shaderProgram > 0) {
            return shaderProgram;
        }

        int vertex = compileShader(GL20.GL_VERTEX_SHADER,
                "#version 120\n" +
                        "varying vec2 vTexCoord;\n" +
                        "void main() {\n" +
                        "    vTexCoord = gl_MultiTexCoord0.xy;\n" +
                        "    gl_Position = ftransform();\n" +
                        "}\n");
        int fragment = compileShader(GL20.GL_FRAGMENT_SHADER,
                "#version 120\n" +
                        "uniform sampler2D uDepth;\n" +
                        "uniform float uNearPlane;\n" +
                        "uniform float uFarPlane;\n" +
                        "uniform float uProjectionScaleX;\n" +
                        "uniform float uProjectionScaleY;\n" +
                        "uniform float uFogStart;\n" +
                        "uniform float uFogEnd;\n" +
                        "uniform float uGradientStart;\n" +
                        "uniform float uGradientEnd;\n" +
                        "uniform float uStrength;\n" +
                        "uniform vec3 uBaseColor;\n" +
                        "uniform vec3 uFarColor;\n" +
                        "varying vec2 vTexCoord;\n" +
                        "float linearViewZ(float depth) {\n" +
                        "    float z = depth * 2.0 - 1.0;\n" +
                        "    return (2.0 * uNearPlane * uFarPlane) / (uFarPlane + uNearPlane - z * (uFarPlane - uNearPlane));\n" +
                        "}\n" +
                        "float radialDistance(float viewZ, vec2 uv) {\n" +
                        "    vec2 ndc = uv * 2.0 - 1.0;\n" +
                        "    float x = ndc.x / max(uProjectionScaleX, 0.001);\n" +
                        "    float y = ndc.y / max(uProjectionScaleY, 0.001);\n" +
                        "    return viewZ * sqrt(1.0 + x * x + y * y);\n" +
                        "}\n" +
                        "float ramp(float edge0, float edge1, float value) {\n" +
                        "    float t = clamp((value - edge0) / max(edge1 - edge0, 0.001), 0.0, 1.0);\n" +
                        "    return t * t * (3.0 - 2.0 * t);\n" +
                        "}\n" +
                        "void main() {\n" +
                        "    float depth = texture2D(uDepth, vTexCoord).r;\n" +
                        "    if (depth >= 0.999999) {\n" +
                        "        discard;\n" +
                        "        return;\n" +
                        "    }\n" +
                        "    float distance = radialDistance(linearViewZ(depth), vTexCoord);\n" +
                        "    float fogAmount = ramp(uFogStart, uFogEnd, distance);\n" +
                        "    float gradientAmount = ramp(uGradientStart, uGradientEnd, distance) * uStrength;\n" +
                        "    float alpha = clamp(fogAmount * gradientAmount, 0.0, 1.0);\n" +
                        "    if (alpha <= 0.001) discard;\n" +
                        "    gl_FragColor = vec4(uFarColor, alpha);\n" +
                        "}\n");

        if (vertex <= 0 || fragment <= 0) {
            shaderFailed = true;
            warnShaderFailure("compile");
            return -1;
        }

        shaderProgram = GL20.glCreateProgram();
        GL20.glAttachShader(shaderProgram, vertex);
        GL20.glAttachShader(shaderProgram, fragment);
        GL20.glLinkProgram(shaderProgram);
        if (GL20.glGetProgrami(shaderProgram, GL20.GL_LINK_STATUS) == 0) {
            String log = GL20.glGetProgramInfoLog(shaderProgram, GL20.glGetProgrami(shaderProgram, GL20.GL_INFO_LOG_LENGTH));
            shaderFailed = true;
            GL20.glDeleteProgram(shaderProgram);
            shaderProgram = -1;
            warnShaderFailure("link: " + log);
            return -1;
        }
        cacheUniforms(shaderProgram);
        GL20.glDeleteShader(vertex);
        GL20.glDeleteShader(fragment);
        return shaderProgram;
    }

    private void cacheUniforms(int program) {
        uniformDepth = GL20.glGetUniformLocation(program, "uDepth");
        uniformNearPlane = GL20.glGetUniformLocation(program, "uNearPlane");
        uniformFarPlane = GL20.glGetUniformLocation(program, "uFarPlane");
        uniformProjectionScaleX = GL20.glGetUniformLocation(program, "uProjectionScaleX");
        uniformProjectionScaleY = GL20.glGetUniformLocation(program, "uProjectionScaleY");
        uniformFogStart = GL20.glGetUniformLocation(program, "uFogStart");
        uniformFogEnd = GL20.glGetUniformLocation(program, "uFogEnd");
        uniformGradientStart = GL20.glGetUniformLocation(program, "uGradientStart");
        uniformGradientEnd = GL20.glGetUniformLocation(program, "uGradientEnd");
        uniformStrength = GL20.glGetUniformLocation(program, "uStrength");
        uniformBaseColor = GL20.glGetUniformLocation(program, "uBaseColor");
        uniformFarColor = GL20.glGetUniformLocation(program, "uFarColor");
    }

    private int compileShader(int type, String source) {
        int shader = GL20.glCreateShader(type);
        GL20.glShaderSource(shader, source);
        GL20.glCompileShader(shader);
        if (GL20.glGetShaderi(shader, GL20.GL_COMPILE_STATUS) == 0) {
            String log = GL20.glGetShaderInfoLog(shader, GL20.glGetShaderi(shader, GL20.GL_INFO_LOG_LENGTH));
            GL20.glDeleteShader(shader);
            warnShaderFailure("shader compile: " + log);
            return -1;
        }
        return shader;
    }

    private void warnShaderFailure(String reason) {
        if (!warnedShaderFailure) {
            warnedShaderFailure = true;
            FMLLog.warning("[RiftFlux] Celestial fog distance gradient shader failed (%s).", reason);
        }
    }

    private static float clamp01(float value) {
        if (value < 0.0F) {
            return 0.0F;
        }
        if (value > 1.0F) {
            return 1.0F;
        }
        return value;
    }
}
