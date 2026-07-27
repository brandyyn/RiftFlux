package com.voidsrift.riftflux.client;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.EntityLivingBase;

public final class EntityNameTagBloomExclusion {
    private static final List<DeferredNameTag> DEFERRED = new ArrayList<DeferredNameTag>();
    private static boolean renderingDeferred;

    private EntityNameTagBloomExclusion() {
    }

    public static void beginDeferredRenderFrame() {
        DEFERRED.clear();
        renderingDeferred = false;
    }

    public static boolean shouldDefer() {
        return !renderingDeferred && PostProcessRenderer.shouldDeferBloomExcludedWorldOverlays();
    }

    public static void defer(
            RendererLivingEntity renderer,
            EntityLivingBase entity,
            double x,
            double y,
            double z
    ) {
        if (renderer == null || entity == null) {
            return;
        }
        DEFERRED.add(new DeferredNameTag(renderer, entity, x, y, z));
    }

    public static void renderDeferred() {
        if (DEFERRED.isEmpty()) {
            return;
        }

        renderingDeferred = true;
        try {
            for (DeferredNameTag deferred : DEFERRED) {
                if (deferred.renderer instanceof EntityNameTagRenderBridge && !deferred.entity.isDead) {
                    ((EntityNameTagRenderBridge) deferred.renderer).riftflux$renderDeferredNameTag(
                            deferred.entity,
                            deferred.x,
                            deferred.y,
                            deferred.z
                    );
                }
            }
        } finally {
            renderingDeferred = false;
            DEFERRED.clear();
        }
    }

    private static final class DeferredNameTag {
        private final RendererLivingEntity renderer;
        private final EntityLivingBase entity;
        private final double x;
        private final double y;
        private final double z;

        private DeferredNameTag(
                RendererLivingEntity renderer,
                EntityLivingBase entity,
                double x,
                double y,
                double z
        ) {
            this.renderer = renderer;
            this.entity = entity;
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }
}
