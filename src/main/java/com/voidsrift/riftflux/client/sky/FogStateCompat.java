package com.voidsrift.riftflux.client.sky;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;

import java.lang.reflect.Method;
import java.nio.FloatBuffer;

public final class FogStateCompat {
    private static final Class<?> ANGELICA_GL_STATE_MANAGER;
    private static final Method ANGELICA_GL_FOG;
    private static final Method ANGELICA_GL_FOGF;
    private static final Method ANGELICA_GL_FOGI;
    private static final Method ANGELICA_GET_FOG_STATE;
    private static final Method ANGELICA_SET_FOG_DISTANCE_MODE;
    private static final int GL_FOG_DISTANCE_MODE_NV = 34138;
    private static final int GL_EYE_RADIAL_NV = 34139;
    private static final int GL_EYE_PLANE_ABSOLUTE_NV = 34140;

    static {
        Class<?> glStateManager = null;
        Method glFog = null;
        Method glFogf = null;
        Method glFogi = null;
        Method getFogState = null;
        Method setFogDistanceMode = null;
        try {
            glStateManager = Class.forName("com.gtnewhorizons.angelica.glsm.GLStateManager");
            glFog = glStateManager.getMethod("glFog", int.class, FloatBuffer.class);
            glFogf = glStateManager.getMethod("glFogf", int.class, float.class);
            glFogi = glStateManager.getMethod("glFogi", int.class, int.class);
            getFogState = glStateManager.getMethod("getFogState");
            Class<?> fogState = Class.forName("com.gtnewhorizons.angelica.glsm.states.FogState");
            setFogDistanceMode = fogState.getMethod("setFogDistanceMode", int.class);
        } catch (Throwable ignored) {
            glStateManager = null;
            glFog = null;
            glFogf = null;
            glFogi = null;
            getFogState = null;
            setFogDistanceMode = null;
        }
        ANGELICA_GL_STATE_MANAGER = glStateManager;
        ANGELICA_GL_FOG = glFog;
        ANGELICA_GL_FOGF = glFogf;
        ANGELICA_GL_FOGI = glFogi;
        ANGELICA_GET_FOG_STATE = getFogState;
        ANGELICA_SET_FOG_DISTANCE_MODE = setFogDistanceMode;
    }

    private FogStateCompat() {
    }

    public static void fog(int pname, FloatBuffer params) {
        if (ANGELICA_GL_STATE_MANAGER != null && ANGELICA_GL_FOG != null) {
            try {
                params.rewind();
                ANGELICA_GL_FOG.invoke(null, pname, params);
                return;
            } catch (Throwable ignored) {
            }
        }
        params.rewind();
        GL11.glFog(pname, params);
    }

    public static void fogf(int pname, float param) {
        if (ANGELICA_GL_STATE_MANAGER != null && ANGELICA_GL_FOGF != null) {
            try {
                ANGELICA_GL_FOGF.invoke(null, pname, param);
                return;
            } catch (Throwable ignored) {
            }
        }
        GL11.glFogf(pname, param);
    }

    public static void fogi(int pname, int param) {
        if (pname == GL_FOG_DISTANCE_MODE_NV) {
            setAngelicaFogDistanceMode(param);
            try {
                if (GLContext.getCapabilities().GL_NV_fog_distance) {
                    GL11.glFogi(pname, param);
                }
            } catch (Throwable ignored) {
            }
            return;
        }
        if (ANGELICA_GL_STATE_MANAGER != null && ANGELICA_GL_FOGI != null) {
            try {
                ANGELICA_GL_FOGI.invoke(null, pname, param);
                return;
            } catch (Throwable ignored) {
            }
        }
        GL11.glFogi(pname, param);
    }

    private static void setAngelicaFogDistanceMode(int glMode) {
        if (ANGELICA_GET_FOG_STATE == null || ANGELICA_SET_FOG_DISTANCE_MODE == null) {
            return;
        }
        try {
            Object fogState = ANGELICA_GET_FOG_STATE.invoke(null);
            ANGELICA_SET_FOG_DISTANCE_MODE.invoke(fogState, mapFogDistanceMode(glMode));
        } catch (Throwable ignored) {
        }
    }

    private static int mapFogDistanceMode(int glMode) {
        if (glMode == GL_EYE_RADIAL_NV) {
            return 0;
        }
        if (glMode == GL_EYE_PLANE_ABSOLUTE_NV) {
            return 2;
        }
        return 1;
    }
}
