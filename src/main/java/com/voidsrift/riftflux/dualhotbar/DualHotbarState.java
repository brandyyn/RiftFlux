package com.voidsrift.riftflux.dualhotbar;

public final class DualHotbarState {
    public static boolean installedOnServer;
    public static int hotbarSize = 9;
    public static int value = 36;

    private DualHotbarState() {
    }

    public static void applyConfigDefaults() {
        if (DualHotbarConfig.enable) {
            hotbarSize = 18;
            value = 27;
        } else {
            hotbarSize = 9;
            value = 36;
        }
    }

    public static void applyServerConfig() {
        if (!installedOnServer) {
            hotbarSize = 9;
            value = 36;
            return;
        }
        if (DualHotbarConfig.enable) {
            hotbarSize = 9 * DualHotbarConfig.numHotbars;
            value = 45 - 9 * DualHotbarConfig.numHotbars;
        } else {
            hotbarSize = 9;
            value = 36;
        }
    }
}
