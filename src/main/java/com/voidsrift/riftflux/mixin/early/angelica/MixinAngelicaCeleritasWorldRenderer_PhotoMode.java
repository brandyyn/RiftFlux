package com.voidsrift.riftflux.mixin.early.angelica;

import com.gtnewhorizons.angelica.rendering.celeritas.AngelicaRenderSectionManager;
import com.gtnewhorizons.angelica.rendering.celeritas.CeleritasWorldRenderer;
import com.voidsrift.riftflux.client.photomode.AngelicaPhotoModeRenderGrid;
import com.voidsrift.riftflux.client.photomode.IsometricPhotoModeController;
import me.jellysquid.mods.sodium.client.gui.SodiumGameOptions;
import net.minecraft.entity.Entity;
import org.embeddedt.embeddium.impl.render.chunk.RenderSection;
import org.embeddedt.embeddium.impl.render.terrain.SimpleWorldRenderer;
import org.embeddedt.embeddium.impl.render.viewport.Viewport;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "com.gtnewhorizons.angelica.rendering.celeritas.CeleritasWorldRenderer", remap = false)
public abstract class MixinAngelicaCeleritasWorldRenderer_PhotoMode {

    @Inject(method = "setupTerrain", at = @At("RETURN"), require = 1)
    private void riftflux$captureStableRenderGrid(
            Viewport viewport,
            SimpleWorldRenderer.CameraState cameraState,
            int frame,
            boolean spectator,
            boolean updateChunks,
            CallbackInfo ci
    ) {
        AngelicaRenderSectionManager manager =
                ((CeleritasWorldRenderer) (Object) this).getRenderSectionManager();
        boolean photoModeActive = IsometricPhotoModeController.instance().isActive();
        boolean boundaryChanged = AngelicaPhotoModeRenderGrid.update(
                manager == null ? null : manager.getAllRenderSections(),
                photoModeActive
        );
        if (boundaryChanged
                && manager != null
                && photoModeActive) {
            this.riftflux$schedulePhotoBoundaryRebuilds(manager);
        }
    }

    private void riftflux$schedulePhotoBoundaryRebuilds(AngelicaRenderSectionManager manager) {
        AngelicaPhotoModeRenderGrid.Bounds current = AngelicaPhotoModeRenderGrid.get();
        AngelicaPhotoModeRenderGrid.Bounds previous = AngelicaPhotoModeRenderGrid.getPreviousBounds();
        int changedSides = AngelicaPhotoModeRenderGrid.getChangedSideMask();
        if (current == null || changedSides == 0) {
            return;
        }

        for (RenderSection section : manager.getAllRenderSections()) {
            if (!section.isBuilt() || !section.hasAnythingToRender()) {
                continue;
            }
            int chunkX = section.getChunkX();
            int chunkZ = section.getChunkZ();
            if (this.riftflux$isChangedBoundarySection(chunkX, chunkZ, current, changedSides)
                    || this.riftflux$isChangedBoundarySection(chunkX, chunkZ, previous, changedSides)) {
                manager.scheduleRebuild(chunkX, section.getChunkY(), chunkZ, true);
            }
        }
    }

    private boolean riftflux$isChangedBoundarySection(
            int chunkX,
            int chunkZ,
            AngelicaPhotoModeRenderGrid.Bounds bounds,
            int changedSides
    ) {
        return bounds != null
                && ((changedSides & 1 << 4) != 0 && chunkX == bounds.minChunkX
                || (changedSides & 1 << 5) != 0 && chunkX == bounds.maxChunkX
                || (changedSides & 1 << 2) != 0 && chunkZ == bounds.minChunkZ
                || (changedSides & 1 << 3) != 0 && chunkZ == bounds.maxChunkZ);
    }

    @Redirect(
            method = "chooseVertexType",
            at = @At(
                    value = "FIELD",
                    target = "Lme/jellysquid/mods/sodium/client/gui/SodiumGameOptions$PerformanceSettings;useCompactVertexFormat:Z"
            ),
            require = 0
    )
    private boolean riftflux$disableCompactVertexFormatInPhotoMode(
            SodiumGameOptions.PerformanceSettings settings
    ) {
        return !IsometricPhotoModeController.instance().isActive()
                && settings.useCompactVertexFormat;
    }

    @Inject(method = "isEntityVisible", at = @At("HEAD"), cancellable = true, require = 0)
    private void riftflux$disableEntityCullingInPhotoMode(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (IsometricPhotoModeController.instance().isActive()) {
            cir.setReturnValue(Boolean.TRUE);
        }
    }
}
