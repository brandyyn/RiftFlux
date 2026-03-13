package com.voidsrift.riftflux.client.sky;

import org.lwjgl.opengl.GL11;

import java.lang.reflect.Method;
import java.nio.FloatBuffer;

public final class FogStateCompat {
    private static final Class<?> ANGELICA_GL_STATE_MANAGER;
    private static final Method ANGELICA_GL_FOG;
    private static final Method ANGELICA_GL_FOGF;
    private static final Method ANGELICA_GL_FOGI;

    static {
        Class<?> glStateManager = null;
        Method glFog = null;
        Method glFogf = null;
        Method glFogi = null;
        try {
            glStateManager = Class.forName("com.gtnewhorizons.angelica.glsm.GLStateManager");
            glFog = glStateManager.getMethod("glFog", int.class, FloatBuffer.class);
            glFogf = glStateManager.getMethod("glFogf", int.class, float.class);
            glFogi = glStateManager.getMethod("glFogi", int.class, int.class);
        } catch (Throwable ignored) {
            glStateManager = null;
            glFog = null;
            glFogf = null;
            glFogi = null;
        }
        ANGELICA_GL_STATE_MANAGER = glStateManager;
        ANGELICA_GL_FOG = glFog;
        ANGELICA_GL_FOGF = glFogf;
        ANGELICA_GL_FOGI = glFogi;
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
        if (ANGELICA_GL_STATE_MANAGER != null && ANGELICA_GL_FOGI != null) {
            try {
                ANGELICA_GL_FOGI.invoke(null, pname, param);
                return;
            } catch (Throwable ignored) {
            }
        }
        GL11.glFogi(pname, param);
    }
}
