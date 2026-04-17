package com.voidsrift.riftflux.client.photomode;

public final class PhotoModeBlockRenderContext {

    private static final ThreadLocal<State> STATE = new ThreadLocal<State>();

    private PhotoModeBlockRenderContext() {
    }

    public static void begin(int x, int y, int z, int minX, int maxXExclusive, int minZ, int maxZExclusive) {
        STATE.set(new State(x, y, z, minX, maxXExclusive, minZ, maxZExclusive));
    }

    public static void end() {
        STATE.remove();
    }

    public static boolean isActive() {
        return STATE.get() != null;
    }

    public static boolean isOwnBlock(int x, int y, int z) {
        State state = STATE.get();
        return state != null && state.x == x && state.y == y && state.z == z;
    }

    public static boolean isHorizontalNeighbor(int x, int y, int z) {
        State state = STATE.get();
        if (state == null || state.y != y) {
            return false;
        }

        return state.x == x && Math.abs(state.z - z) == 1
                || state.z == z && Math.abs(state.x - x) == 1;
    }

    public static boolean isOutsideHorizontalRenderGrid(int x, int z) {
        State state = STATE.get();
        return state != null
                && (x < state.minX || x >= state.maxXExclusive || z < state.minZ || z >= state.maxZExclusive);
    }

    public static int x() {
        State state = STATE.get();
        return state == null ? 0 : state.x;
    }

    public static int y() {
        State state = STATE.get();
        return state == null ? 0 : state.y;
    }

    public static int z() {
        State state = STATE.get();
        return state == null ? 0 : state.z;
    }

    private static final class State {
        private final int x;
        private final int y;
        private final int z;
        private final int minX;
        private final int maxXExclusive;
        private final int minZ;
        private final int maxZExclusive;

        private State(int x, int y, int z, int minX, int maxXExclusive, int minZ, int maxZExclusive) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.minX = minX;
            this.maxXExclusive = maxXExclusive;
            this.minZ = minZ;
            this.maxZExclusive = maxZExclusive;
        }
    }
}
