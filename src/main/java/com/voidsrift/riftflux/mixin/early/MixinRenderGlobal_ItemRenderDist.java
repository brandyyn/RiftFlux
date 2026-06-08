package com.voidsrift.riftflux.mixin.early;

import com.llamalad7.mixinextras.sugar.Local;
import com.voidsrift.riftflux.ModConfig;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Lightweight dropped-item render limiter.
 * Mirrors Angelica's distance-bucket approach when Angelica is not present.
 * If Angelica is present, RiftFlux stays out of this path to avoid double limiting.
 */
@Mixin(RenderGlobal.class)
public abstract class MixinRenderGlobal_ItemRenderDist {

    @Unique private static final boolean rf$angelicaPresent = rf$hasClass("com.gtnewhorizons.angelica.config.AngelicaConfig");
    @Unique private static final int[] rf$itemCounts = new int[256];
    @Unique private static int rf$itemRenderBucket = 255;
    @Unique private static boolean rf$limitActive;
    @Unique private static double rf$capSq = Double.POSITIVE_INFINITY;

    @Inject(method = "renderEntities", at = @At("HEAD"))
    private void rf$prepareItemLimit(EntityLivingBase view, net.minecraft.client.renderer.culling.ICamera camera, float partialTicks, CallbackInfo ci) {
        rf$limitActive = false;
        rf$capSq = Double.POSITIVE_INFINITY;

        int limit = (ModConfig.droppedItemLimit == 2048)
                ? Integer.MAX_VALUE
                : Math.max(0, ModConfig.droppedItemLimit);

        boolean enabled = ModConfig.enableDroppedItemRenderTweaks && !rf$angelicaPresent;
        if (enabled && ModConfig.droppedItemMaxRenderDistance > 0) {
            rf$capSq = (double) ModConfig.droppedItemMaxRenderDistance * (double) ModConfig.droppedItemMaxRenderDistance;
        }

        int entityCount = 0;
        boolean reachedLimit = false;
        for (int i = 0; i < rf$itemCounts.length; i++) {
            entityCount += rf$itemCounts[i];
            rf$itemCounts[i] = 0;
            if (enabled && !reachedLimit && entityCount > limit) {
                reachedLimit = true;
                rf$itemRenderBucket = i == 0 ? 1 : i;
            }
        }

        if (!enabled || !reachedLimit) {
            rf$itemRenderBucket = 255;
        }
        rf$limitActive = enabled && limit != Integer.MAX_VALUE;
    }

    @ModifyVariable(
            method = "renderEntities",
            at = @At(value = "STORE", ordinal = 0),
            ordinal = 0
    )
    private boolean rf$gateItems(boolean flag,
                                 @Local(ordinal = 0) Entity entity,
                                 @Local(ordinal = 0) double d0,
                                 @Local(ordinal = 1) double d1,
                                 @Local(ordinal = 2) double d2) {
        if (!(entity instanceof EntityItem) || !ModConfig.enableDroppedItemRenderTweaks || rf$angelicaPresent) {
            return flag;
        }

        if (!flag && rf$capSq == Double.POSITIVE_INFINITY) {
            return false;
        }

        double distSq = entity.getDistanceSq(d0, d1, d2);
        if (distSq > rf$capSq) {
            return false;
        }

        if (!rf$limitActive) {
            return true;
        }

        int bucket = Math.min((int) (distSq / 4.0D), 255);
        rf$itemCounts[bucket]++;
        return bucket <= rf$itemRenderBucket;
    }

    @Unique
    private static boolean rf$hasClass(String className) {
        try {
            Class.forName(className, false, MixinRenderGlobal_ItemRenderDist.class.getClassLoader());
            return true;
        } catch (Throwable ignored) {
            return false;
        }
    }
}
