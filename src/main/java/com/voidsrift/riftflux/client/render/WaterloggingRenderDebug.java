package com.voidsrift.riftflux.client.render;

import cpw.mods.fml.common.FMLLog;

public final class WaterloggingRenderDebug {
    private static boolean loggedSugarcanePass;
    private static boolean loggedSugarcaneRender;
    private static boolean loggedCrystalRender;

    private WaterloggingRenderDebug() {
    }

    public static void sugarcanePassHook(int pass) {
        if (!loggedSugarcanePass) {
            loggedSugarcanePass = true;
            FMLLog.info("[RiftFlux] Sugarcane waterlogging pass hook active; first reed canRenderInPass pass=%d.", pass);
        }
    }

    public static void sugarcaneRenderHook(int x, int y, int z, int pass, boolean waterlogged) {
        if (!loggedSugarcaneRender) {
            loggedSugarcaneRender = true;
            FMLLog.info("[RiftFlux] Sugarcane waterlogging render hook active at %d,%d,%d pass=%d waterlogged=%s.",
                    x, y, z, pass, waterlogged);
        }
    }

    public static void crystalRenderHook(int x, int y, int z) {
        if (!loggedCrystalRender) {
            loggedCrystalRender = true;
            FMLLog.info("[RiftFlux] GeoStrata crystal spike waterlogging render hook active at %d,%d,%d.", x, y, z);
        }
    }
}
