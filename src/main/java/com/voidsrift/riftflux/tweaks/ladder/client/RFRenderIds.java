package com.voidsrift.riftflux.tweaks.ladder.client;

/**
 * Holds render IDs that are assigned on the client during init.
 *
 * IMPORTANT: This class must stay free of client-only imports so it can be safely referenced by common code.
 */
public final class RFRenderIds {

    private RFRenderIds() {}

    /** Set on client; -1 means "not assigned". */
    public static int doubleSidedLadderRenderId = -1;
}
