package com.voidsrift.riftflux.client.sky;

import net.minecraft.client.multiplayer.WorldClient;

public final class FogDistanceGradientState {
    private static WorldClient colorWorld;
    private static WorldClient distanceWorld;
    private static boolean colorValid;
    private static boolean distanceValid;
    private static float fogRed;
    private static float fogGreen;
    private static float fogBlue;
    private static float fogStart;
    private static float fogEnd;
    private static float projectionFar;
    private static float projectionScaleX = 1.0F;
    private static float projectionScaleY = 1.0F;

    private FogDistanceGradientState() {
    }

    public static void setFogColor(WorldClient world, float red, float green, float blue) {
        colorWorld = world;
        colorValid = world != null;
        fogRed = clamp01(red);
        fogGreen = clamp01(green);
        fogBlue = clamp01(blue);
    }

    public static void setFogDistance(WorldClient world, float start, float end, float far, float scaleX, float scaleY) {
        distanceWorld = world;
        distanceValid = world != null && end > start + 0.001F && far > 1.0F;
        fogStart = start;
        fogEnd = end;
        projectionFar = far;
        projectionScaleX = scaleX < 0.001F ? 1.0F : scaleX;
        projectionScaleY = scaleY < 0.001F ? 1.0F : scaleY;
    }

    public static void invalidate(WorldClient world) {
        if (world == null || colorWorld == world) {
            colorValid = false;
        }
        if (world == null || distanceWorld == world) {
            distanceValid = false;
        }
    }

    public static boolean isReady(WorldClient world) {
        return world != null
                && colorValid
                && distanceValid
                && colorWorld == world
                && distanceWorld == world;
    }

    public static float[] getFogColor() {
        return new float[] { fogRed, fogGreen, fogBlue };
    }

    public static float getFogStart() {
        return fogStart;
    }

    public static float getFogEnd() {
        return fogEnd;
    }

    public static float getProjectionFar() {
        return projectionFar;
    }

    public static float getProjectionScaleX() {
        return projectionScaleX;
    }

    public static float getProjectionScaleY() {
        return projectionScaleY;
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
