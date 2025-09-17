package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.ModConfig;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashSet;

/**
 * Nearest-first dropped item limiter with optional render distance expansion.
 * - HEAD: compute the K nearest items (K = droppedItemLimit; 2048 = unlimited).
 * - STORE 'flag': render only if entity id is in allowed set; if vanilla 'flag' is false
 *   and expansion is enabled (capSq finite), enable render when distSq <= capSq.
 */
@Mixin(RenderGlobal.class)
public abstract class MixinRenderGlobal_ItemRenderDist {

    @Unique private static final HashSet<Integer> rf$allowed = new HashSet<Integer>(1024);

    // Reusable buffers (no inner classes)
    @Unique private static int[]    rf$idBuf   = new int[0];
    @Unique private static double[] rf$distBuf = new double[0];

    @Unique private static double rf$capSq = Double.POSITIVE_INFINITY; // current frame cap (blocks^2); INF disables

    @Inject(method = "renderEntities", at = @At("HEAD"))
    private void rf$buildAllowed(EntityLivingBase view, ICamera camera, float partialTicks, CallbackInfo ci) {
        rf$allowed.clear();
        if (!ModConfig.enableItemRenderLimiter) { rf$capSq = Double.POSITIVE_INFINITY; return; }

        final int limit = (ModConfig.droppedItemLimit == 2048)
                ? Integer.MAX_VALUE
                : Math.max(0, ModConfig.droppedItemLimit);

        rf$capSq = (ModConfig.droppedItemMaxRenderDistance > 0)
                ? (double)ModConfig.droppedItemMaxRenderDistance * (double)ModConfig.droppedItemMaxRenderDistance
                : Double.POSITIVE_INFINITY;

        // Camera (interpolated)
        final double cx = view.lastTickPosX + (view.posX - view.lastTickPosX) * partialTicks;
        final double cy = view.lastTickPosY + (view.posY - view.lastTickPosY) * partialTicks;
        final double cz = view.lastTickPosZ + (view.posZ - view.lastTickPosZ) * partialTicks;

        // Count items & ensure capacity
        final java.util.List<?> list = view.worldObj.loadedEntityList;
        int total = 0;
        for (int i = 0, n = list.size(); i < n; i++) if (list.get(i) instanceof EntityItem) total++;
        if (total == 0) return;
        rf$ensureCapacity(total);

        // Fill buffers (id, distSq) within cap
        int idx = 0;
        for (int i = 0, n = list.size(); i < n; i++) {
            final Object o = list.get(i);
            if (!(o instanceof EntityItem)) continue;
            final Entity e = (Entity)o;

            final double ex = e.lastTickPosX + (e.posX - e.lastTickPosX) * partialTicks;
            final double ey = e.lastTickPosY + (e.posY - e.lastTickPosY) * partialTicks;
            final double ez = e.lastTickPosZ + (e.posZ - e.lastTickPosZ) * partialTicks;

            final double dx = ex - cx, dy = ey - cy, dz = ez - cz;
            final double d2 = dx*dx + dy*dy + dz*dz;
            if (d2 > rf$capSq) continue;

            rf$idBuf[idx]   = e.getEntityId();
            rf$distBuf[idx] = d2;
            idx++;
        }
        if (idx == 0) return;

        if (limit == Integer.MAX_VALUE || idx <= limit) {
            for (int i = 0; i < idx; i++) rf$allowed.add(rf$idBuf[i]);
            return;
        }

        // Quickselect: first 'limit' entries become the smallest by distSq (tie-break by id)
        rf$nthElement(rf$idBuf, rf$distBuf, 0, idx - 1, limit - 1);
        for (int i = 0; i < limit; i++) rf$allowed.add(rf$idBuf[i]);
    }

    @ModifyVariable(
            method = "renderEntities",
            at = @At(value = "STORE", ordinal = 0),
            ordinal = 0 // boolean 'flag'
    )
    private boolean rf$gateItems(boolean flag,
                                 @Local(ordinal = 0) Entity entity,
                                 @Local(ordinal = 0) double d0,
                                 @Local(ordinal = 1) double d1,
                                 @Local(ordinal = 2) double d2) {
        if (!(entity instanceof EntityItem) || !ModConfig.enableItemRenderLimiter) return flag;

        final boolean allowed = rf$allowed.contains(entity.getEntityId());
        if (!allowed) {
            return false; // outside nearest-K → cull
        }

        if (flag) {
            return true;  // vanilla already wants to render it
        }

        // Expand render distance when vanilla said false but within our cap
        if (rf$capSq != Double.POSITIVE_INFINITY) {
            final double distSq = entity.getDistanceSq(d0, d1, d2);
            if (distSq <= rf$capSq) return true;
        }
        return flag;
    }

    // ---- helpers ----
    @Unique
    private static void rf$ensureCapacity(int needed) {
        if (rf$idBuf.length >= needed) return;
        int cap = rf$idBuf.length == 0 ? 1024 : rf$idBuf.length;
        while (cap < needed) cap <<= 1;
        rf$idBuf   = java.util.Arrays.copyOf(rf$idBuf, cap);
        rf$distBuf = java.util.Arrays.copyOf(rf$distBuf, cap);
    }

    @Unique
    private static void rf$nthElement(int[] ids, double[] dists, int left, int right, int n) {
        while (left < right) {
            int pivotIndex = (left + right) >>> 1;
            double pv = dists[pivotIndex];
            int    pi = ids[pivotIndex];

            int i = left, j = right;
            while (i <= j) {
                while (dists[i] < pv || (dists[i] == pv && ids[i] < pi)) i++;
                while (dists[j] > pv || (dists[j] == pv && ids[j] > pi)) j--;
                if (i <= j) {
                    double td = dists[i]; dists[i] = dists[j]; dists[j] = td;
                    int ti = ids[i];      ids[i]   = ids[j];   ids[j]   = ti;
                    i++; j--;
                }
            }
            if (n <= j) right = j;
            else if (n >= i) left = i;
            else return;
        }
    }
}
