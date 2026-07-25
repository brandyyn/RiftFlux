package com.voidsrift.riftflux.client.photomode;

import com.gtnewhorizons.angelica.glsm.GLStateManager;
import com.gtnewhorizons.angelica.proxy.ClientProxy;
import me.jellysquid.mods.sodium.client.gui.SodiumGameOptions;
import org.lwjgl.opengl.GL11;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public final class AngelicaPhotoModeCompat {

    private static final String BEDDIUM_FOG_GL = "com.ventooth.beddium.modules.TerrainRendering.fog.FogGL";
    private static final String BEDDIUM_FOG_STATE = "com.ventooth.beddium.modules.TerrainRendering.fog.FogState";

    private static boolean initialized;
    private static boolean beddiumFogAvailable;
    private static boolean optionOverridesCaptured;
    private static Method beddiumGlDisableMethod;
    private static Field beddiumFogEnabledField;
    private static boolean previousParticleCulling;
    private static boolean previousEntityCulling;
    private static boolean previousCompactVertexFormat;

    private AngelicaPhotoModeCompat() {
    }

    public static boolean enablePhotoModeOverrides() {
        try {
            SodiumGameOptions options = ClientProxy.options();
            if (options == null || options.advanced == null || options.performance == null) {
                return false;
            }

            if (!optionOverridesCaptured) {
                previousParticleCulling = options.advanced.useParticleCulling;
                previousEntityCulling = options.performance.useEntityCulling;
                previousCompactVertexFormat = options.performance.useCompactVertexFormat;
                optionOverridesCaptured = true;
            }

            boolean compactVertexFormatChanged = options.performance.useCompactVertexFormat;
            options.advanced.useParticleCulling = false;
            options.performance.useEntityCulling = false;
            options.performance.useCompactVertexFormat = false;
            return compactVertexFormatChanged;
        } catch (Throwable ignored) {
            return false;
        }
    }

    public static boolean disablePhotoModeOverrides() {
        if (!optionOverridesCaptured) {
            return false;
        }

        boolean compactVertexFormatChanged = false;
        try {
            SodiumGameOptions options = ClientProxy.options();
            if (options != null && options.advanced != null && options.performance != null) {
                options.advanced.useParticleCulling = previousParticleCulling;
                options.performance.useEntityCulling = previousEntityCulling;
                compactVertexFormatChanged =
                        options.performance.useCompactVertexFormat != previousCompactVertexFormat;
                options.performance.useCompactVertexFormat = previousCompactVertexFormat;
            }
        } catch (Throwable ignored) {
        } finally {
            optionOverridesCaptured = false;
        }
        return compactVertexFormatChanged;
    }

    public static void enforceNoFog() {
        ensureInitialized();

        try {
            GLStateManager.disableFog();
        } catch (Throwable ignored) {
        }

        try {
            if (beddiumFogAvailable) {
                beddiumGlDisableMethod.invoke(null, Integer.valueOf(GL11.GL_FOG));
                beddiumFogEnabledField.setBoolean(null, false);
            }
        } catch (Throwable ignored) {
        }

        try {
            GL11.glDisable(GL11.GL_FOG);
        } catch (Throwable ignored) {
        }
    }

    private static void ensureInitialized() {
        if (initialized) {
            return;
        }

        initialized = true;
        try {
            ClassLoader loader = AngelicaPhotoModeCompat.class.getClassLoader();
            Class<?> fogGlClass = Class.forName(BEDDIUM_FOG_GL, false, loader);
            Class<?> fogStateClass = Class.forName(BEDDIUM_FOG_STATE, false, loader);

            beddiumGlDisableMethod = fogGlClass.getMethod("glDisable", Integer.TYPE);
            beddiumFogEnabledField = fogStateClass.getField("enabled");
            beddiumFogAvailable = true;
        } catch (Throwable ignored) {
            beddiumFogAvailable = false;
        }

    }
}
