package com.voidsrift.riftflux.mixin.early.angelica;

import org.embeddedt.embeddium.impl.gl.device.CommandList;
import org.embeddedt.embeddium.impl.render.chunk.ChunkRenderMatrices;
import org.embeddedt.embeddium.impl.render.chunk.ChunkRenderer;
import org.embeddedt.embeddium.impl.render.chunk.lists.ChunkRenderListIterable;
import org.embeddedt.embeddium.impl.render.chunk.terrain.TerrainRenderPass;
import org.embeddedt.embeddium.impl.render.viewport.CameraTransform;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Pseudo
@Mixin(targets = "org.embeddedt.embeddium.impl.render.chunk.RenderSectionManager", remap = false)
public abstract class MixinEmbeddiumRenderSectionManager_PhotoMode {

    @Redirect(
            method = "renderLayer",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/embeddedt/embeddium/impl/render/chunk/ChunkRenderer;render(Lorg/embeddedt/embeddium/impl/render/chunk/ChunkRenderMatrices;Lorg/embeddedt/embeddium/impl/gl/device/CommandList;Lorg/embeddedt/embeddium/impl/render/chunk/lists/ChunkRenderListIterable;Lorg/embeddedt/embeddium/impl/render/chunk/terrain/TerrainRenderPass;Lorg/embeddedt/embeddium/impl/render/viewport/CameraTransform;Lorg/embeddedt/embeddium/impl/render/viewport/CameraTransform;)V"
            ),
            require = 0
    )
    private void riftflux$renderWithPhotoOcclusionCamera(
            ChunkRenderer renderer,
            ChunkRenderMatrices matrices,
            CommandList commandList,
            ChunkRenderListIterable renderLists,
            TerrainRenderPass pass,
            CameraTransform occlusionCamera,
            CameraTransform camera
    ) {
        renderer.render(matrices, commandList, renderLists, pass, occlusionCamera, camera);
    }
}
