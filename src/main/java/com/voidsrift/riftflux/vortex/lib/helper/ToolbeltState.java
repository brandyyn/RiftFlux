package com.voidsrift.riftflux.vortex.lib.helper;

/**
 * Tiny shared state holder used to cheaply invalidate the client-side toolbelt radial cache.
 *
 * The server is authoritative. Whenever the server mutates the toolbelt inventory it sends
 * PacketToolbeltSync which bumps this revision on the client.
 */
public final class ToolbeltState {

    private static volatile int clientRevision = 0;
    private static volatile boolean radialActive = false;

    private ToolbeltState() {}

    public static int getClientRevision() {
        return clientRevision;
    }

    public static void bumpClientRevision() {
        clientRevision++;
    }

    public static boolean isRadialActive() {
        return radialActive;
    }

    public static void setRadialActive(boolean active) {
        radialActive = active;
    }
}
