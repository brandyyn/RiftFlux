package com.voidsrift.riftflux.dualhotbar;

import com.voidsrift.riftflux.ModConfig;

public final class DualHotbarConfig {
    public static boolean enable;
    public static boolean twoLayerRendering;
    public static boolean doubleTap;
    public static boolean keyCombo;
    public static int doubleTapTime;
    public static int numHotbars = 4;
    public static boolean heldItemTooltipAboveBars;
    public static int heldItemTooltipPadding;

    private DualHotbarConfig() {
    }

    public static void syncFromModConfig() {
        enable = ModConfig.dualHotbarEnable;
        twoLayerRendering = !ModConfig.dualHotbarLongHotbar;
        doubleTap = ModConfig.dualHotbarDoubleTap;
        keyCombo = ModConfig.dualHotbarKeyCombo;
        doubleTapTime = ModConfig.dualHotbarDoubleTapTime;
        numHotbars = ModConfig.dualHotbarNumHotbars;
        heldItemTooltipAboveBars = ModConfig.dualHotbarHeldItemTooltipAboveBars;
        heldItemTooltipPadding = Math.max(0, ModConfig.dualHotbarHeldItemTooltipPadding);

        if (numHotbars == 3) {
            twoLayerRendering = true;
        }

        if (numHotbars < 1 || numHotbars > 4) {
            numHotbars = 1;
        }

        DualHotbarState.applyConfigDefaults();
    }
}
