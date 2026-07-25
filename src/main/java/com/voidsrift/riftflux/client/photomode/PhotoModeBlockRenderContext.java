package com.voidsrift.riftflux.client.photomode;

public final class PhotoModeBlockRenderContext {

    private static final ThreadLocal<State> STATE = new ThreadLocal<State>();

    private PhotoModeBlockRenderContext() {
    }

    public static void begin(int x, int y, int z, int minX, int maxXExclusive, int minZ, int maxZExclusive) {
        STATE.set(new State(x, y, z, minX, maxXExclusive, minZ, maxZExclusive, true, 0, 0));
    }

    public static void begin(
            int x,
            int y,
            int z,
            int minX,
            int maxXExclusive,
            int minZ,
            int maxZExclusive,
            int horizontalSideMask
    ) {
        begin(
                x,
                y,
                z,
                minX,
                maxXExclusive,
                minZ,
                maxZExclusive,
                horizontalSideMask,
                0
        );
    }

    public static void begin(
            int x,
            int y,
            int z,
            int minX,
            int maxXExclusive,
            int minZ,
            int maxZExclusive,
            int horizontalSideMask,
            int liquidSideMask
    ) {
        STATE.set(new State(
                x,
                y,
                z,
                minX,
                maxXExclusive,
                minZ,
                maxZExclusive,
                true,
                horizontalSideMask,
                liquidSideMask
        ));
    }

    public static void beginForcedSides(int x, int y, int z, int horizontalSideMask) {
        STATE.set(new State(x, y, z, 0, 0, 0, 0, false, horizontalSideMask, 0));
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
                && state.hasRenderGrid
                && (x < state.minX || x >= state.maxXExclusive || z < state.minZ || z >= state.maxZExclusive);
    }

    public static boolean shouldForceHorizontalSide(int side) {
        State state = STATE.get();
        return state != null && side >= 2 && side <= 5 && (state.horizontalSideMask & (1 << side)) != 0;
    }

    public static boolean shouldForceHorizontalLiquidSide(int side) {
        State state = STATE.get();
        return state != null && side >= 2 && side <= 5 && (state.liquidSideMask & (1 << side)) != 0;
    }

    public static boolean shouldUseSourceBrightness(int x, int y, int z) {
        State state = STATE.get();
        if (state == null) {
            return false;
        }

        if (state.hasRenderGrid && (x < state.minX || x >= state.maxXExclusive || z < state.minZ || z >= state.maxZExclusive)) {
            return true;
        }

        return shouldForceHorizontalNeighbor(x, y, z);
    }

    public static boolean shouldForceHorizontalNeighbor(int x, int y, int z) {
        State state = STATE.get();
        if (state == null) {
            return false;
        }

        if (state.y != y) {
            return false;
        }

        return (state.liquidSideMask & (1 << 2)) != 0 && state.x == x && state.z - 1 == z
                || (state.liquidSideMask & (1 << 3)) != 0 && state.x == x && state.z + 1 == z
                || (state.liquidSideMask & (1 << 4)) != 0 && state.x - 1 == x && state.z == z
                || (state.liquidSideMask & (1 << 5)) != 0 && state.x + 1 == x && state.z == z;
    }

    public static boolean shouldForceHorizontalLiquidEdge(int x, int y, int z) {
        State state = STATE.get();
        if (state == null || state.y != y) {
            return false;
        }

        return (state.liquidSideMask & (1 << 2)) != 0
                && state.z - 1 == z
                && (state.x == x || state.x + 1 == x)
                || (state.liquidSideMask & (1 << 3)) != 0
                && state.z + 1 == z
                && (state.x == x || state.x + 1 == x)
                || (state.liquidSideMask & (1 << 4)) != 0
                && state.x - 1 == x
                && (state.z == z || state.z + 1 == z)
                || (state.liquidSideMask & (1 << 5)) != 0
                && state.x + 1 == x
                && (state.z == z || state.z + 1 == z);
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
        private final boolean hasRenderGrid;
        private final int horizontalSideMask;
        private final int liquidSideMask;
        private State(
                int x,
                int y,
                int z,
                int minX,
                int maxXExclusive,
                int minZ,
                int maxZExclusive,
                boolean hasRenderGrid,
                int horizontalSideMask,
                int liquidSideMask
        ) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.minX = minX;
            this.maxXExclusive = maxXExclusive;
            this.minZ = minZ;
            this.maxZExclusive = maxZExclusive;
            this.hasRenderGrid = hasRenderGrid;
            this.horizontalSideMask = horizontalSideMask;
            this.liquidSideMask = liquidSideMask;
        }
    }
}
