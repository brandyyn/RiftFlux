package com.voidsrift.riftflux.mixin.early.angelica;

import com.voidsrift.riftflux.client.photomode.IsometricPhotoModeController;
import org.embeddedt.embeddium.impl.render.chunk.occlusion.OcclusionCuller;
import org.embeddedt.embeddium.impl.render.chunk.occlusion.OcclusionNode;
import org.embeddedt.embeddium.impl.render.viewport.Viewport;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Field;
import java.util.Collection;

@Pseudo
@Mixin(targets = "org.embeddedt.embeddium.impl.render.chunk.occlusion.OcclusionCuller", remap = false)
public abstract class MixinEmbeddiumOcclusionCuller_PhotoMode {

    @Unique
    private static Field riftflux$sectionsField;

    @Inject(method = "findVisible", at = @At("HEAD"), cancellable = true, require = 0)
    private void riftflux$keepAllLoadedSectionsVisible(OcclusionCuller.Visitor visitor, Viewport viewport, float searchDistance, boolean useOcclusionCulling, int frame, CallbackInfo ci) {
        if (!IsometricPhotoModeController.instance().isActive()) {
            return;
        }

        Collection<?> sections = this.riftflux$getSections();
        if (sections == null) {
            return;
        }

        for (Object value : sections) {
            if (!(value instanceof OcclusionNode)) {
                continue;
            }

            OcclusionNode section = (OcclusionNode) value;
            section.setLastVisibleFrame(frame);
            section.setIncomingDirections(0);
            visitor.visit(section, true);
        }

        ci.cancel();
    }

    @Unique
    private Collection<?> riftflux$getSections() {
        try {
            if (riftflux$sectionsField == null) {
                riftflux$sectionsField = this.getClass().getDeclaredField("sections");
                riftflux$sectionsField.setAccessible(true);
            }

            Object map = riftflux$sectionsField.get(this);
            if (map == null) {
                return null;
            }

            Object values = map.getClass().getMethod("values").invoke(map);
            return values instanceof Collection<?> ? (Collection<?>) values : null;
        } catch (Throwable ignored) {
            return null;
        }
    }
}
