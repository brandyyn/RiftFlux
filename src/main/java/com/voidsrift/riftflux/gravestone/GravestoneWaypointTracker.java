package com.voidsrift.riftflux.gravestone;

import java.lang.ref.WeakReference;
import com.voidsrift.riftflux.mixin.late.xaero.minimap.XaeroWaypointsManagerInvoker;
import gravestone.config.GraveStoneConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

public final class GravestoneWaypointTracker {
    private static boolean valid;
    private static int x;
    private static int y;
    private static int z;
    private static int dimensionId;
    private static WeakReference<Object> xaeroManager = new WeakReference<Object>(null);
    private static boolean pendingXaeroDeathpoint;
    private static boolean creatingGraveDeathpoint;
    private static boolean suppressXaeroDeathpointDuplicates;

    private GravestoneWaypointTracker() {
    }

    public static void receive(int graveX, int graveY, int graveZ, int graveDimensionId) {
        if (!isEnabled()) {
            valid = false;
            pendingXaeroDeathpoint = false;
            suppressXaeroDeathpointDuplicates = false;
            return;
        }
        x = graveX;
        y = graveY;
        z = graveZ;
        dimensionId = graveDimensionId;
        valid = true;
        suppressXaeroDeathpointDuplicates = false;
    }

    public static boolean createPendingXaeroDeathpoint(EntityPlayer player) {
        if (!valid || !pendingXaeroDeathpoint || player == null) {
            return false;
        }
        Object manager = xaeroManager.get();
        if (!(manager instanceof XaeroWaypointsManagerInvoker)) {
            return false;
        }

        creatingGraveDeathpoint = true;
        try {
            ((XaeroWaypointsManagerInvoker)manager).riftflux$createDeathpoint(player);
            return true;
        } finally {
            creatingGraveDeathpoint = false;
        }
    }

    public static boolean hasLocation(Entity entity) {
        return isEnabled() && valid && entity != null;
    }

    public static int getX() {
        return x;
    }

    public static int getY() {
        return y;
    }

    public static int getZ() {
        return z;
    }

    public static int getDimensionId() {
        return dimensionId;
    }

    public static void registerXaeroManager(Object manager) {
        if (manager != null) {
            xaeroManager = new WeakReference<Object>(manager);
        }
    }

    public static boolean shouldCancelXaeroDeathpoint() {
        if (creatingGraveDeathpoint) {
            return false;
        }
        if (!isEnabled()) {
            return false;
        }
        if (!valid || suppressXaeroDeathpointDuplicates) {
            pendingXaeroDeathpoint = true;
            return true;
        }
        return false;
    }

    private static boolean isEnabled() {
        return GraveStoneConfig.enableXaeroMinimapGraveWaypoints
                && GraveStoneConfig.enablePlayerDeathGraves
                && GraveStoneConfig.generatePlayerGraves;
    }

    public static void markXaeroDeathpointFinalized() {
        pendingXaeroDeathpoint = false;
        suppressXaeroDeathpointDuplicates = true;
    }
}
