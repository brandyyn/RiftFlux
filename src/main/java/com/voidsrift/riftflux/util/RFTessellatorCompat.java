package com.voidsrift.riftflux.util;

import java.lang.reflect.Method;
import net.minecraft.client.renderer.Tessellator;

public final class RFTessellatorCompat {
    private static final Method FALSE_TWEAKS_THREAD_TESSELLATOR = findFalseTweaksThreadTessellator();

    private RFTessellatorCompat() {
    }

    public static Tessellator current() {
        if (FALSE_TWEAKS_THREAD_TESSELLATOR != null) {
            try {
                Object tessellator = FALSE_TWEAKS_THREAD_TESSELLATOR.invoke(null);
                if (tessellator instanceof Tessellator) {
                    return (Tessellator) tessellator;
                }
            } catch (Throwable ignored) {
            }
        }

        return Tessellator.instance;
    }

    private static Method findFalseTweaksThreadTessellator() {
        try {
            ClassLoader loader = RFTessellatorCompat.class.getClassLoader();
            Class<?> type = Class.forName(
                    "com.falsepattern.falsetweaks.modules.threadedupdates.ThreadTessellator",
                    false,
                    loader
            );
            return type.getMethod("getThreadTessellator");
        } catch (Throwable ignored) {
            return null;
        }
    }
}
