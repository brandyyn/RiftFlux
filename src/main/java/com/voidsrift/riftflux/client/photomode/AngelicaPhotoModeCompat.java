package com.voidsrift.riftflux.client.photomode;

import org.lwjgl.opengl.GL11;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public final class AngelicaPhotoModeCompat {

    private static final String ANGELICA_MOD = "com.gtnewhorizons.angelica.AngelicaMod";
    private static final String GL_STATE_MANAGER = "com.gtnewhorizons.angelica.glsm.GLStateManager";
    private static final String BOOLEAN_STATE_STACK = "com.gtnewhorizons.angelica.glsm.stacks.BooleanStateStack";

    private static boolean initialized;
    private static boolean glStateAvailable;
    private static boolean optionsAvailable;
    private static boolean optionOverridesCaptured;
    private static Method disableFogMethod;
    private static Method getFogModeMethod;
    private static Method optionsMethod;
    private static Method setEnabledMethod;
    private static Field advancedField;
    private static Field performanceField;
    private static Field useParticleCullingField;
    private static Field useEntityCullingField;
    private static boolean previousParticleCulling;
    private static boolean previousEntityCulling;

    private AngelicaPhotoModeCompat() {
    }

    public static void enablePhotoModeOverrides() {
        ensureInitialized();
        if (!optionsAvailable) {
            return;
        }

        try {
            Object options = optionsMethod.invoke(null);
            Object advanced = advancedField.get(options);
            Object performance = performanceField.get(options);

            if (!optionOverridesCaptured) {
                previousParticleCulling = useParticleCullingField.getBoolean(advanced);
                if (useEntityCullingField != null) {
                    previousEntityCulling = useEntityCullingField.getBoolean(performance);
                }
                optionOverridesCaptured = true;
            }

            useParticleCullingField.setBoolean(advanced, false);
            if (useEntityCullingField != null) {
                useEntityCullingField.setBoolean(performance, false);
            }
        } catch (Throwable ignored) {
        }
    }

    public static void disablePhotoModeOverrides() {
        ensureInitialized();
        if (!optionsAvailable || !optionOverridesCaptured) {
            return;
        }

        try {
            Object options = optionsMethod.invoke(null);
            Object advanced = advancedField.get(options);
            Object performance = performanceField.get(options);

            useParticleCullingField.setBoolean(advanced, previousParticleCulling);
            if (useEntityCullingField != null) {
                useEntityCullingField.setBoolean(performance, previousEntityCulling);
            }
        } catch (Throwable ignored) {
        } finally {
            optionOverridesCaptured = false;
        }
    }

    public static void enforceNoFog() {
        ensureInitialized();
        try {
            if (glStateAvailable) {
                disableFogMethod.invoke(null);

                Object fogMode = getFogModeMethod.invoke(null);
                if (fogMode != null) {
                    setEnabledMethod.invoke(fogMode, Boolean.FALSE);
                }
            }

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
            Class<?> glStateManagerClass = Class.forName(GL_STATE_MANAGER, false, loader);
            Class<?> booleanStateStackClass = Class.forName(BOOLEAN_STATE_STACK, false, loader);

            disableFogMethod = glStateManagerClass.getMethod("disableFog");
            getFogModeMethod = glStateManagerClass.getMethod("getFogMode");
            setEnabledMethod = booleanStateStackClass.getMethod("setEnabled", Boolean.TYPE);
            glStateAvailable = true;
        } catch (Throwable ignored) {
            glStateAvailable = false;
        }

        try {
            ClassLoader loader = AngelicaPhotoModeCompat.class.getClassLoader();
            Class<?> angelicaModClass = Class.forName(ANGELICA_MOD, false, loader);
            optionsMethod = angelicaModClass.getMethod("options");

            Object options = optionsMethod.invoke(null);
            Class<?> optionsClass = options.getClass();
            advancedField = optionsClass.getField("advanced");
            performanceField = optionsClass.getField("performance");

            Object advanced = advancedField.get(options);
            Object performance = performanceField.get(options);
            Class<?> advancedClass = advanced.getClass();
            Class<?> performanceClass = performance.getClass();

            useParticleCullingField = advancedClass.getField("useParticleCulling");
            try {
                useEntityCullingField = performanceClass.getField("useEntityCulling");
            } catch (Throwable ignored) {
                useEntityCullingField = null;
            }
            optionsAvailable = true;
        } catch (Throwable ignored) {
            optionsAvailable = false;
        }
    }
}
