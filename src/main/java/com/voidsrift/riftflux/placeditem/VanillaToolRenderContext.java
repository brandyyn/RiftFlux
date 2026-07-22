package com.voidsrift.riftflux.placeditem;

public final class VanillaToolRenderContext {
    private static final ThreadLocal<Integer> PLACED_ITEM_DEPTH = newDepth();
    private static final ThreadLocal<Integer> GRAVESTONE_DEPTH = newDepth();

    private static ThreadLocal<Integer> newDepth() {
        return new ThreadLocal<Integer>() {
            @Override
            protected Integer initialValue() {
                return 0;
            }
        };
    }

    private VanillaToolRenderContext() {
    }

    public static void enterPlacedItem() {
        enter(PLACED_ITEM_DEPTH);
    }

    public static void exitPlacedItem() {
        exit(PLACED_ITEM_DEPTH);
    }

    public static void enterGravestone() {
        enter(GRAVESTONE_DEPTH);
    }

    public static void exitGravestone() {
        exit(GRAVESTONE_DEPTH);
    }

    private static void enter(ThreadLocal<Integer> depth) {
        depth.set(depth.get() + 1);
    }

    private static void exit(ThreadLocal<Integer> depth) {
        int value = depth.get() - 1;
        if (value <= 0) {
            depth.remove();
        } else {
            depth.set(value);
        }
    }

    public static boolean isPlacedItem() {
        return PLACED_ITEM_DEPTH.get() > 0;
    }

    public static boolean isActive() {
        return isPlacedItem() || GRAVESTONE_DEPTH.get() > 0;
    }
}
