package com.voidsrift.riftflux.client;

import com.voidsrift.riftflux.ModConfig;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.io.File;
import java.nio.ByteBuffer;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GLContext;

@SideOnly(Side.CLIENT)
public final class PostProcessRenderer {
    private static final PostProcessRenderer INSTANCE = new PostProcessRenderer();
    private static final File CONFIG_FILE = new File("config/riftflux.cfg");
    private static final int GL_FRAMEBUFFER_BINDING = 36006;
    private static final int GL_COLOR_ATTACHMENT0 = 36064;

    private int sceneTexture = -1;
    private int depthTexture = -1;
    private int textureWidth = -1;
    private int textureHeight = -1;
    private int shaderProgram = -1;
    private int uniformScene = -1;
    private int uniformDepth = -1;
    private int uniformTexelSize = -1;
    private int uniformDesaturation = -1;
    private int uniformGamma = -1;
    private int uniformBloomStrength = -1;
    private int uniformBloomThreshold = -1;
    private int uniformBloomRadius = -1;
    private int uniformCelestialBloomStrength = -1;
    private int uniformCelestialBloomThreshold = -1;
    private int uniformCelestialBloomRadius = -1;
    private int uniformSkyStarBrightness = -1;
    private int uniformSkyStarOnlyGate = -1;
    private int uniformApplyColorGrade = -1;
    private int uniformApplyBloom = -1;
    private int uniformCelestialOnly = -1;
    private long lastConfigCheckMillis;
    private long lastConfigModified = Long.MIN_VALUE;
    private boolean shaderFailed;
    private boolean warnedNoShaderSupport;
    private boolean warnedShaderFailure;
    private boolean loggedHookReached;
    private boolean loggedFirstRender;

    private PostProcessRenderer() {
    }

    public static void renderSkyBloomAfterSky(float partialTicks) {
        INSTANCE.renderFrame(partialTicks, false, true, true, "after sky");
    }

    public static void renderWorldBloomBeforeHand(float partialTicks) {
        if (!ModConfig.postProcessBloomAffectsHeldItem) {
            INSTANCE.renderFrame(partialTicks, false, true, false, "before held item");
        }
    }

    public static void renderBeforeHud(float partialTicks) {
        INSTANCE.renderFrame(partialTicks, true, ModConfig.postProcessBloomAffectsHeldItem, false, "before GUI overlay");
    }

    private void renderFrame(float partialTicks, boolean applyColorGrade, boolean applyBloom, boolean celestialOnly, String stageName) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc == null || mc.theWorld == null || mc.renderViewEntity == null || mc.displayWidth <= 0 || mc.displayHeight <= 0) {
            return;
        }
        if (!loggedHookReached) {
            loggedHookReached = true;
            FMLLog.info("[RiftFlux] Post processing hook reached world frame %s.", stageName);
        }

        refreshConfigIfChanged();
        if (!shouldRender(applyColorGrade, applyBloom, celestialOnly)) {
            return;
        }
        if (!GLContext.getCapabilities().OpenGL20) {
            shaderFailed = true;
            if (!warnedNoShaderSupport) {
                warnedNoShaderSupport = true;
                FMLLog.warning("[RiftFlux] Post processing disabled: OpenGL 2.0 shader support is unavailable.");
            }
            return;
        }

        int program = getShaderProgram();
        if (program <= 0) {
            return;
        }

        ensureSceneTexture(mc.displayWidth, mc.displayHeight);
        copySceneTexture(mc.displayWidth, mc.displayHeight);
        copyDepthTexture(mc.displayWidth, mc.displayHeight);
        renderFullscreen(program, mc.displayWidth, mc.displayHeight, partialTicks, applyColorGrade, applyBloom, celestialOnly);
        if (!loggedFirstRender) {
            loggedFirstRender = true;
            FMLLog.info("[RiftFlux] Post processing rendered first frame %s.", stageName);
        }
    }

    private void refreshConfigIfChanged() {
        if (!ModConfig.postProcessConfigHotSwap) {
            return;
        }
        long now = System.currentTimeMillis();
        if (now - lastConfigCheckMillis < 1000L) {
            return;
        }
        lastConfigCheckMillis = now;
        if (!CONFIG_FILE.isFile()) {
            return;
        }
        long modified = CONFIG_FILE.lastModified();
        if (modified != lastConfigModified) {
            lastConfigModified = modified;
            ModConfig.reload();
        }
    }

    private boolean shouldRender(boolean applyColorGrade, boolean applyBloom, boolean celestialOnly) {
        if (!ModConfig.enablePostProcessing || shaderFailed) {
            return false;
        }
        boolean colorGradeActive = applyColorGrade
                && (ModConfig.postProcessDesaturationPercent > 0.0F
                        || Math.abs(ModConfig.postProcessGamma - 1.0F) > 0.001F);
        boolean bloomActive = applyBloom && ModConfig.postProcessBloomStrengthPercent > 0.0F
                && (!celestialOnly || ModConfig.postProcessCelestialBloomStrengthPercent > 0.0F);
        return colorGradeActive || bloomActive;
    }

    private void ensureSceneTexture(int width, int height) {
        if (sceneTexture > 0 && textureWidth == width && textureHeight == height) {
            return;
        }
        if (sceneTexture > 0) {
            GL11.glDeleteTextures(sceneTexture);
        }
        if (depthTexture > 0) {
            GL11.glDeleteTextures(depthTexture);
        }

        textureWidth = width;
        textureHeight = height;
        sceneTexture = GL11.glGenTextures();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, sceneTexture);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, (ByteBuffer) null);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);

        depthTexture = GL11.glGenTextures();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, depthTexture);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_DEPTH_COMPONENT, width, height, 0, GL11.GL_DEPTH_COMPONENT, GL11.GL_FLOAT, (ByteBuffer) null);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
    }

    private void copySceneTexture(int width, int height) {
        int previousReadBuffer = GL11.glGetInteger(GL11.GL_READ_BUFFER);
        int currentFramebuffer = GL11.glGetInteger(GL_FRAMEBUFFER_BINDING);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, sceneTexture);
        GL11.glReadBuffer(currentFramebuffer == 0 ? GL11.GL_BACK : GL_COLOR_ATTACHMENT0);
        GL11.glCopyTexSubImage2D(GL11.GL_TEXTURE_2D, 0, 0, 0, 0, 0, width, height);
        GL11.glReadBuffer(previousReadBuffer);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
    }

    private void copyDepthTexture(int width, int height) {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, depthTexture);
        GL11.glCopyTexSubImage2D(GL11.GL_TEXTURE_2D, 0, 0, 0, 0, 0, width, height);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
    }

    private void renderFullscreen(int program, int width, int height, float partialTicks, boolean applyColorGrade, boolean applyBloom, boolean celestialOnly) {
        int previousProgram = GL11.glGetInteger(GL20.GL_CURRENT_PROGRAM);
        int previousActiveTexture = GL11.glGetInteger(GL13.GL_ACTIVE_TEXTURE);

        GL11.glPushAttrib(GL11.GL_ENABLE_BIT | GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_TEXTURE_BIT | GL11.GL_CURRENT_BIT | GL11.GL_VIEWPORT_BIT);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(false);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_FOG);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glViewport(0, 0, width, height);

        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        GL11.glOrtho(0.0D, 1.0D, 0.0D, 1.0D, -1.0D, 1.0D);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();

        try {
            GL13.glActiveTexture(GL13.GL_TEXTURE0);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, sceneTexture);
            GL13.glActiveTexture(GL13.GL_TEXTURE1);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, depthTexture);
            GL13.glActiveTexture(GL13.GL_TEXTURE0);

            GL20.glUseProgram(program);
            GL20.glUniform1i(uniformScene, 0);
            GL20.glUniform1i(uniformDepth, 1);
            GL20.glUniform2f(uniformTexelSize, 1.0F / (float) width, 1.0F / (float) height);
            GL20.glUniform1f(uniformDesaturation, clamp01(ModConfig.postProcessDesaturationPercent / 100.0F));
            GL20.glUniform1f(uniformGamma, Math.max(0.01F, ModConfig.postProcessGamma));
            GL20.glUniform1f(uniformBloomStrength, clamp01(ModConfig.postProcessBloomStrengthPercent / 100.0F));
            GL20.glUniform1f(uniformBloomThreshold, clamp01(ModConfig.postProcessBloomThreshold));
            GL20.glUniform1f(uniformBloomRadius, Math.max(0.25F, ModConfig.postProcessBloomRadiusPixels));
            GL20.glUniform1f(uniformCelestialBloomStrength, Math.max(0.0F, ModConfig.postProcessCelestialBloomStrengthPercent / 100.0F));
            GL20.glUniform1f(uniformCelestialBloomThreshold, clamp01(ModConfig.postProcessCelestialBloomThreshold / 100.0F));
            GL20.glUniform1f(uniformCelestialBloomRadius, Math.max(0.25F, ModConfig.postProcessCelestialBloomRadiusPixels));
            Minecraft mc = Minecraft.getMinecraft();
            float starBrightness = mc != null && mc.theWorld != null ? mc.theWorld.getStarBrightness(partialTicks) : 0.0F;
            GL20.glUniform1f(uniformSkyStarBrightness, clamp01(starBrightness));
            GL20.glUniform1f(uniformSkyStarOnlyGate, mc != null && mc.theWorld != null ? computeStarOnlyGate(mc, partialTicks) : 0.0F);
            GL20.glUniform1f(uniformApplyColorGrade, applyColorGrade ? 1.0F : 0.0F);
            GL20.glUniform1f(uniformApplyBloom, applyBloom ? 1.0F : 0.0F);
            GL20.glUniform1f(uniformCelestialOnly, celestialOnly ? 1.0F : 0.0F);

            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
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
        } finally {
            GL20.glUseProgram(previousProgram);
            GL13.glActiveTexture(GL13.GL_TEXTURE1);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
            GL13.glActiveTexture(GL13.GL_TEXTURE0);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
            GL13.glActiveTexture(previousActiveTexture);
            GL11.glMatrixMode(GL11.GL_MODELVIEW);
            GL11.glPopMatrix();
            GL11.glMatrixMode(GL11.GL_PROJECTION);
            GL11.glPopMatrix();
            GL11.glMatrixMode(GL11.GL_MODELVIEW);
            GL11.glDepthMask(true);
            GL11.glPopAttrib();
        }
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
                        "uniform sampler2D uScene;\n" +
                        "uniform sampler2D uDepth;\n" +
                        "uniform vec2 uTexelSize;\n" +
                        "uniform float uDesaturation;\n" +
                        "uniform float uGamma;\n" +
                        "uniform float uBloomStrength;\n" +
                        "uniform float uBloomThreshold;\n" +
                        "uniform float uBloomRadius;\n" +
                        "uniform float uCelestialBloomStrength;\n" +
                        "uniform float uCelestialBloomThreshold;\n" +
                        "uniform float uCelestialBloomRadius;\n" +
                        "uniform float uSkyStarBrightness;\n" +
                        "uniform float uSkyStarOnlyGate;\n" +
                        "uniform float uApplyColorGrade;\n" +
                        "uniform float uApplyBloom;\n" +
                        "uniform float uCelestialOnly;\n" +
                        "varying vec2 vTexCoord;\n" +
                        "float luma(vec3 color) {\n" +
                        "    return dot(color, vec3(0.2126, 0.7152, 0.0722));\n" +
                        "}\n" +
                        "vec3 graded(vec2 uv) {\n" +
                        "    vec3 color = texture2D(uScene, clamp(uv, vec2(0.0), vec2(1.0))).rgb;\n" +
                        "    float grey = luma(color);\n" +
                        "    color = mix(color, vec3(grey), uDesaturation * uApplyColorGrade);\n" +
                        "    color = mix(color, pow(max(color, vec3(0.0)), vec3(1.0 / max(uGamma, 0.01))), uApplyColorGrade);\n" +
                        "    return color;\n" +
                        "}\n" +
                        "vec3 bloomSample(vec2 uv) {\n" +
                        "    vec3 raw = texture2D(uScene, clamp(uv, vec2(0.0), vec2(1.0))).rgb;\n" +
                        "    float bright = max(max(raw.r, raw.g), raw.b);\n" +
                        "    float depth = texture2D(uDepth, clamp(uv, vec2(0.0), vec2(1.0))).r;\n" +
                        "    float closeAmount = 1.0 - smoothstep(0.985, 0.9998, depth);\n" +
                        "    closeAmount = closeAmount * closeAmount * (3.0 - 2.0 * closeAmount);\n" +
                        "    float depthBoost = mix(1.0, 7.0, closeAmount);\n" +
                        "    float gate = smoothstep(uBloomThreshold, 1.0, bright);\n" +
                        "    gate *= gate;\n" +
                        "    vec3 excess = max(raw - vec3(uBloomThreshold), vec3(0.0)) / max(1.0 - uBloomThreshold, 0.001);\n" +
                        "    excess *= excess;\n" +
                        "    float blueLead = raw.b - max(raw.r, raw.g);\n" +
                        "    float notBlueSky = 1.0 - smoothstep(0.03, 0.18, blueLead);\n" +
                        "    float warmOrWhite = smoothstep(-0.12, 0.18, raw.r - raw.b) * smoothstep(-0.12, 0.18, raw.g - raw.b);\n" +
                        "    float starVisibility = smoothstep(0.04, 0.22, uSkyStarBrightness) * uSkyStarOnlyGate;\n" +
                        "    float starMask = smoothstep(0.006, 0.09, bright) * warmOrWhite * mix(starVisibility, 1.0, uCelestialBloomThreshold);\n" +
                        "    float broadThreshold = mix(1.05, 0.48, uCelestialBloomThreshold);\n" +
                        "    float broadMask = smoothstep(broadThreshold, 1.0, bright) * mix(notBlueSky, 1.0, uCelestialBloomThreshold) * warmOrWhite;\n" +
                        "    float celestialMask = max(starMask, broadMask * uCelestialBloomThreshold);\n" +
                        "    vec3 celestialBloom = raw * celestialMask * uCelestialBloomStrength * uCelestialOnly;\n" +
                        "    vec3 normalBloom = excess * gate * depthBoost * (1.0 - uCelestialOnly);\n" +
                        "    vec3 normalLinear = pow(clamp(normalBloom, vec3(0.0), vec3(1.0)), vec3(2.2));\n" +
                        "    return clamp(normalLinear + celestialBloom, vec3(0.0), vec3(1.0));\n" +
                        "}\n" +
                        "void main() {\n" +
                        "    vec2 uv = vTexCoord;\n" +
                        "    float activeBloomRadius = mix(uBloomRadius, uCelestialBloomRadius, uCelestialOnly);\n" +
                        "    vec2 step1 = uTexelSize * max(activeBloomRadius, 0.25);\n" +
                        "    vec2 step2 = step1 * 2.0;\n" +
                        "    vec3 color = graded(uv);\n" +
                        "    vec3 bloom = bloomSample(uv) * 0.12;\n" +
                        "    bloom += bloomSample(uv + vec2(step1.x, 0.0)) * 0.11;\n" +
                        "    bloom += bloomSample(uv - vec2(step1.x, 0.0)) * 0.11;\n" +
                        "    bloom += bloomSample(uv + vec2(0.0, step1.y)) * 0.11;\n" +
                        "    bloom += bloomSample(uv - vec2(0.0, step1.y)) * 0.11;\n" +
                        "    bloom += bloomSample(uv + step1) * 0.075;\n" +
                        "    bloom += bloomSample(uv - step1) * 0.075;\n" +
                        "    bloom += bloomSample(uv + vec2(step1.x, -step1.y)) * 0.075;\n" +
                        "    bloom += bloomSample(uv + vec2(-step1.x, step1.y)) * 0.075;\n" +
                        "    bloom += bloomSample(uv + vec2(step2.x, 0.0)) * 0.035;\n" +
                        "    bloom += bloomSample(uv - vec2(step2.x, 0.0)) * 0.035;\n" +
                        "    bloom += bloomSample(uv + vec2(0.0, step2.y)) * 0.035;\n" +
                        "    bloom += bloomSample(uv - vec2(0.0, step2.y)) * 0.035;\n" +
                        "    bloom = pow(max(bloom, vec3(0.0)), vec3(1.0 / 2.2));\n" +
                        "    float outputBloomStrength = mix(uBloomStrength * 0.22, 1.15, uCelestialOnly);\n" +
                        "    color += bloom * uApplyBloom * outputBloomStrength;\n" +
                        "    gl_FragColor = vec4(clamp(color, 0.0, 1.0), 1.0);\n" +
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
        uniformScene = GL20.glGetUniformLocation(program, "uScene");
        uniformDepth = GL20.glGetUniformLocation(program, "uDepth");
        uniformTexelSize = GL20.glGetUniformLocation(program, "uTexelSize");
        uniformDesaturation = GL20.glGetUniformLocation(program, "uDesaturation");
        uniformGamma = GL20.glGetUniformLocation(program, "uGamma");
        uniformBloomStrength = GL20.glGetUniformLocation(program, "uBloomStrength");
        uniformBloomThreshold = GL20.glGetUniformLocation(program, "uBloomThreshold");
        uniformBloomRadius = GL20.glGetUniformLocation(program, "uBloomRadius");
        uniformCelestialBloomStrength = GL20.glGetUniformLocation(program, "uCelestialBloomStrength");
        uniformCelestialBloomThreshold = GL20.glGetUniformLocation(program, "uCelestialBloomThreshold");
        uniformCelestialBloomRadius = GL20.glGetUniformLocation(program, "uCelestialBloomRadius");
        uniformSkyStarBrightness = GL20.glGetUniformLocation(program, "uSkyStarBrightness");
        uniformSkyStarOnlyGate = GL20.glGetUniformLocation(program, "uSkyStarOnlyGate");
        uniformApplyColorGrade = GL20.glGetUniformLocation(program, "uApplyColorGrade");
        uniformApplyBloom = GL20.glGetUniformLocation(program, "uApplyBloom");
        uniformCelestialOnly = GL20.glGetUniformLocation(program, "uCelestialOnly");
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
            FMLLog.warning("[RiftFlux] Post processing shader failed (%s).", reason);
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

    private static float computeStarOnlyGate(Minecraft mc, float partialTicks) {
        long worldTime = mc.theWorld.getWorldTime() % 24000L;
        float time = (float) worldTime + partialTicks;
        if (time < 0.0F) {
            time += 24000.0F;
        }
        float start = clampTime(ModConfig.postProcessCelestialBloomStartTime);
        float end = clampTime(ModConfig.postProcessCelestialBloomEndTime);
        float windowLength = wrapTime(end - start);
        if (windowLength <= 0.0F) {
            return 1.0F;
        }

        float elapsed = wrapTime(time - start);
        if (elapsed > windowLength) {
            return 0.0F;
        }

        float fadeTicks = Math.min(1500.0F, windowLength * 0.5F);
        if (fadeTicks <= 0.0F) {
            return 1.0F;
        }
        float nightIn = smoothstep(elapsed / fadeTicks);
        float nightOut = smoothstep((windowLength - elapsed) / fadeTicks);
        return clamp01(nightIn * nightOut);
    }

    private static float clampTime(int time) {
        if (time < 0) {
            return 0.0F;
        }
        if (time > 23999) {
            return 23999.0F;
        }
        return (float)time;
    }

    private static float wrapTime(float time) {
        float wrapped = time % 24000.0F;
        return wrapped < 0.0F ? wrapped + 24000.0F : wrapped;
    }

    private static float smoothstep(float value) {
        float t = clamp01(value);
        return t * t * (3.0F - 2.0F * t);
    }
}
