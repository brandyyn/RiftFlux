package com.voidsrift.riftflux;

public class CommonProxy {
    public void initClientFeatures() { /* no-op on server */ }
    
    public boolean isJumpKeyDown() {
        return false;
    }

    public void openPaintingSelectorScreen() {
        // no-op on server
    }

    public void applyBlessingSync(String blessing, boolean hasSource, int x, int y, int z, int dim) {
        // no-op on server
    }

    public void applyToroHealthDamage(int entityId, int damage) {
        // no-op on server
    }

    public void applyRespawnDelaySync(long remainingMs) {
        // no-op on server
    }
}
