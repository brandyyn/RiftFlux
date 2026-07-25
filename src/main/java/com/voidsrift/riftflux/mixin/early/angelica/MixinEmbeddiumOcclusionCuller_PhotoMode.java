package com.voidsrift.riftflux.mixin.early.angelica;

import com.gtnewhorizons.angelica.rendering.celeritas.AngelicaRenderSectionManager;
import com.gtnewhorizons.angelica.rendering.celeritas.CeleritasWorldRenderer;
import com.voidsrift.riftflux.client.photomode.IsometricPhotoModeController;
import java.util.Collection;
import org.embeddedt.embeddium.impl.render.chunk.RenderSection;
import org.embeddedt.embeddium.impl.render.chunk.occlusion.OcclusionCuller;
import org.embeddedt.embeddium.impl.render.chunk.occlusion.OcclusionNode;
import org.embeddedt.embeddium.impl.render.viewport.Viewport;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "org.embeddedt.embeddium.impl.render.chunk.occlusion.OcclusionCuller", remap = false)
public abstract class MixinEmbeddiumOcclusionCuller_PhotoMode {

    @Invoker("getRenderSection")
    protected abstract OcclusionNode riftflux$getRenderSection(int chunkX, int chunkY, int chunkZ);

    @Inject(method = "findVisible", at = @At("HEAD"), cancellable = true, require = 1)
    private void riftflux$keepEveryLoadedSectionVisible(
            OcclusionCuller.Visitor visitor,
            Viewport viewport,
            float searchDistance,
            boolean useOcclusionCulling,
            int frame,
            CallbackInfo ci
    ) {
        IsometricPhotoModeController controller = IsometricPhotoModeController.instance();
        if (!controller.isActive()) {
            return;
        }

        CeleritasWorldRenderer worldRenderer = CeleritasWorldRenderer.getInstanceOrNull();
        AngelicaRenderSectionManager manager =
                worldRenderer == null ? null : worldRenderer.getRenderSectionManager();
        Collection<RenderSection> loadedSections =
                manager == null ? null : manager.getAllRenderSections();
        if (loadedSections == null) {
            return;
        }

        for (RenderSection renderSection : loadedSections) {
            OcclusionNode section = this.riftflux$getRenderSection(
                    renderSection.getChunkX(),
                    renderSection.getChunkY(),
                    renderSection.getChunkZ()
            );
            if (section == null) {
                continue;
            }

            section.setLastVisibleFrame(frame);
            section.setIncomingDirections(0);
            visitor.visit(section, true);
        }

        ci.cancel();
    }
}
