package com.voidsrift.riftflux.client.photomode;

public final class PhotoModeExternalKeySuppressions {

    private static final int CURRENT_PRESS_SUPPRESSION_TICKS = 4;
    private static int backhandSwapSuppressionTicks;

    private PhotoModeExternalKeySuppressions() {
    }

    public static void suppressBackhandSwapForCurrentPress() {
        backhandSwapSuppressionTicks = Math.max(backhandSwapSuppressionTicks, CURRENT_PRESS_SUPPRESSION_TICKS);
    }

    public static void tick() {
        if (backhandSwapSuppressionTicks > 0) {
            backhandSwapSuppressionTicks--;
        }
    }

    public static boolean shouldSuppressBackhandSwapKey() {
        return IsometricPhotoModeController.instance().isCapturingInput() || backhandSwapSuppressionTicks > 0;
    }
}
