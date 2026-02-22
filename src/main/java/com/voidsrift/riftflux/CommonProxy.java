package com.voidsrift.riftflux;

public class CommonProxy {
    public void initClientFeatures() { /* no-op on server */ }
    
    public boolean isJumpKeyDown() {
        return false;
    }

    public void openPaintingSelectorScreen() {
        // no-op on server
    }
}
