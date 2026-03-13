/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.MathHelper
 */
package net.nmccoy.legendgear.render;

import net.minecraft.util.MathHelper;

public class Rainbow {
    public static float r(float phase) {
        phase = (float)((double)phase * (Math.PI * 2));
        float r = (MathHelper.sin((float)(phase + 0.0f)) + 1.0f) * 0.5f;
        float g = (MathHelper.sin((float)(phase + 2.0943952f)) + 1.0f) * 0.5f;
        float b = (MathHelper.sin((float)(phase + 4.1887903f)) + 1.0f) * 0.5f;
        float resat = Math.min(r, Math.min(g, b));
        float scaler = 1.0f / Math.max(r -= resat, Math.max(g -= resat, b -= resat));
        r = Math.min(scaler * r, 1.0f);
        g = Math.min(scaler * g, 1.0f);
        b = Math.min(scaler * b, 1.0f);
        return r;
    }

    public static float g(float phase) {
        phase = (float)((double)phase * (Math.PI * 2));
        float r = (MathHelper.sin((float)(phase + 0.0f)) + 1.0f) * 0.5f;
        float g = (MathHelper.sin((float)(phase + 2.0943952f)) + 1.0f) * 0.5f;
        float b = (MathHelper.sin((float)(phase + 4.1887903f)) + 1.0f) * 0.5f;
        float resat = Math.min(r, Math.min(g, b));
        float scaler = 1.0f / Math.max(r -= resat, Math.max(g -= resat, b -= resat));
        r = Math.min(scaler * r * 0.5f + 0.5f, 1.0f);
        g = Math.min(scaler * g * 0.5f + 0.5f, 1.0f);
        b = Math.min(scaler * b * 0.5f + 0.5f, 1.0f);
        return g;
    }

    public static float b(float phase) {
        phase = (float)((double)phase * (Math.PI * 2));
        float r = (MathHelper.sin((float)(phase + 0.0f)) + 1.0f) * 0.5f;
        float g = (MathHelper.sin((float)(phase + 2.0943952f)) + 1.0f) * 0.5f;
        float b = (MathHelper.sin((float)(phase + 4.1887903f)) + 1.0f) * 0.5f;
        float resat = Math.min(r, Math.min(g, b));
        float scaler = 1.0f / Math.max(r -= resat, Math.max(g -= resat, b -= resat));
        r = Math.min(scaler * r * 0.5f + 0.5f, 1.0f);
        g = Math.min(scaler * g * 0.5f + 0.5f, 1.0f);
        b = Math.min(scaler * b * 0.5f + 0.5f, 1.0f);
        return b;
    }
}

