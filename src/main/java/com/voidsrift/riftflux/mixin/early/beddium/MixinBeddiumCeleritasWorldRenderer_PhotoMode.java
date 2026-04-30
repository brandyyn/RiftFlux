package com.voidsrift.riftflux.mixin.early.beddium;

import com.voidsrift.riftflux.client.photomode.IsometricPhotoModeController;
import net.minecraft.entity.Entity;
import org.embeddedt.embeddium.impl.render.chunk.RenderSectionManager;
import org.embeddedt.embeddium.impl.render.viewport.Viewport;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.Field;

@Pseudo
@Mixin(targets = "com.ventooth.beddium.modules.TerrainRendering.CeleritasWorldRenderer", remap = false)
public abstract class MixinBeddiumCeleritasWorldRenderer_PhotoMode {

    @Unique
    private static Field riftflux$renderSectionManagerField;

    @Unique
    private int riftflux$lastTerrainRefreshToken = Integer.MIN_VALUE;

    @Unique
    private boolean riftflux$wasPhotoModeActive;

    @Unique
    private int riftflux$lastPhotoModeCenterChunkX = Integer.MIN_VALUE;

    @Unique
    private int riftflux$lastPhotoModeCenterChunkZ = Integer.MIN_VALUE;

    @Unique
    private int riftflux$lastPhotoModeRenderDistance = Integer.MIN_VALUE;

    @Inject(method = "isEntityVisible", at = @At("HEAD"), cancellable = true, require = 0)
    private void riftflux$disableEntityCullingInPhotoMode(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (IsometricPhotoModeController.instance().isActive()) {
            cir.setReturnValue(Boolean.TRUE);
        }
    }

    @Inject(method = "setupTerrain", at = @At("HEAD"), require = 0)
    private void riftflux$refreshTerrainForPhotoZoom(
            Viewport viewport,
            float partialTicks,
            int frame,
            boolean spectator,
            boolean updateImmediately,
            CallbackInfo ci
    ) {
        IsometricPhotoModeController controller = IsometricPhotoModeController.instance();
        RenderSectionManager renderSectionManager = this.riftflux$getRenderSectionManager();
        if (!controller.isActive()) {
            if (this.riftflux$wasPhotoModeActive && renderSectionManager != null) {
                this.riftflux$rebuildPhotoModeTerrain(renderSectionManager);
            }
            this.riftflux$resetPhotoModeTerrainState();
            return;
        }

        if (renderSectionManager == null) {
            this.riftflux$lastTerrainRefreshToken = Integer.MIN_VALUE;
            return;
        }

        int centerChunkX = controller.getHorizontalPhotoModeCenterChunkX();
        int centerChunkZ = controller.getHorizontalPhotoModeCenterChunkZ();
        int renderDistance = controller.getHorizontalPhotoModeRenderDistanceChunks();
        if (!this.riftflux$wasPhotoModeActive
                || centerChunkX != this.riftflux$lastPhotoModeCenterChunkX
                || centerChunkZ != this.riftflux$lastPhotoModeCenterChunkZ
                || renderDistance != this.riftflux$lastPhotoModeRenderDistance) {
            this.riftflux$rebuildPhotoModeTerrain(renderSectionManager);
            this.riftflux$lastPhotoModeCenterChunkX = centerChunkX;
            this.riftflux$lastPhotoModeCenterChunkZ = centerChunkZ;
            this.riftflux$lastPhotoModeRenderDistance = renderDistance;
        }
        this.riftflux$wasPhotoModeActive = true;

        int token = controller.getTerrainRefreshToken();
        if (this.riftflux$lastTerrainRefreshToken == Integer.MIN_VALUE) {
            this.riftflux$lastTerrainRefreshToken = token;
            return;
        }

        if (token != this.riftflux$lastTerrainRefreshToken) {
            renderSectionManager.markGraphDirty();
            this.riftflux$lastTerrainRefreshToken = token;
        }
    }

    @Unique
    private void riftflux$rebuildPhotoModeTerrain(RenderSectionManager renderSectionManager) {
        renderSectionManager.markGraphDirty();
        renderSectionManager.scheduleRebuildAll();
    }

    @Unique
    private void riftflux$resetPhotoModeTerrainState() {
        this.riftflux$lastTerrainRefreshToken = Integer.MIN_VALUE;
        this.riftflux$wasPhotoModeActive = false;
        this.riftflux$lastPhotoModeCenterChunkX = Integer.MIN_VALUE;
        this.riftflux$lastPhotoModeCenterChunkZ = Integer.MIN_VALUE;
        this.riftflux$lastPhotoModeRenderDistance = Integer.MIN_VALUE;
    }

    @Unique
    private RenderSectionManager riftflux$getRenderSectionManager() {
        try {
            if (riftflux$renderSectionManagerField == null) {
                riftflux$renderSectionManagerField = this.getClass().getDeclaredField("renderSectionManager");
                riftflux$renderSectionManagerField.setAccessible(true);
            }

            Object value = riftflux$renderSectionManagerField.get(this);
            return value instanceof RenderSectionManager ? (RenderSectionManager) value : null;
        } catch (Throwable ignored) {
            return null;
        }
    }
}
