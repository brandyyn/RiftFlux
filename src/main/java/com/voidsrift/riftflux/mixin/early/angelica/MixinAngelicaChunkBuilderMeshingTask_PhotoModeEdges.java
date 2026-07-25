package com.voidsrift.riftflux.mixin.early.angelica;

import com.voidsrift.riftflux.client.photomode.AngelicaPhotoModeRenderGrid;
import com.voidsrift.riftflux.client.photomode.IsometricPhotoModeController;
import com.voidsrift.riftflux.client.photomode.PhotoModeBlockRenderContext;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.World;
import org.embeddedt.embeddium.impl.render.chunk.RenderSection;
import org.embeddedt.embeddium.impl.render.chunk.compile.ChunkBuildOutput;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(
        targets = "com.gtnewhorizons.angelica.rendering.celeritas.AngelicaChunkBuilderMeshingTask",
        remap = false
)
public abstract class MixinAngelicaChunkBuilderMeshingTask_PhotoModeEdges {

    @Shadow
    @Final
    protected RenderSection render;

    @Unique
    private boolean riftflux$computedHorizontalGridBounds;

    @Unique
    private int riftflux$minChunkX;

    @Unique
    private int riftflux$maxChunkX;

    @Unique
    private int riftflux$minChunkZ;

    @Unique
    private int riftflux$maxChunkZ;

    @Unique
    private AngelicaPhotoModeRenderGrid.Bounds riftflux$horizontalGridBounds;

    @Inject(
            method = "execute(Lorg/embeddedt/embeddium/impl/render/chunk/compile/ChunkBuildContext;"
                    + "Lorg/embeddedt/embeddium/impl/util/task/CancellationToken;)"
                    + "Lorg/embeddedt/embeddium/impl/render/chunk/compile/ChunkBuildOutput;",
            at = @At("HEAD"),
            require = 1
    )
    private void riftflux$resetPhotoBoundaryState(
            CallbackInfoReturnable<ChunkBuildOutput> cir
    ) {
        this.riftflux$computedHorizontalGridBounds = false;
        this.riftflux$horizontalGridBounds = null;
        this.riftflux$minChunkX = 1;
        this.riftflux$maxChunkX = 0;
        this.riftflux$minChunkZ = 1;
        this.riftflux$maxChunkZ = 0;
    }

    @Redirect(
            method = "renderBlock",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/RenderBlocks;renderBlockByRenderType(Lnet/minecraft/block/Block;III)Z",
                    remap = true
            ),
            require = 1
    )
    private boolean riftflux$renderPhotoModeEdgeBlock(
            RenderBlocks renderer,
            Block block,
            int x,
            int y,
            int z
    ) {
        IsometricPhotoModeController controller = IsometricPhotoModeController.instance();
        if (!controller.isActive()
                || this.riftflux$isNetherWorld()
                || this.render == null
                || !this.riftflux$ensureHorizontalGridBounds()) {
            return renderer.renderBlockByRenderType(block, x, y, z);
        }

        int outerSideMask = this.riftflux$getOutermostHorizontalSideMask(x, z);
        if (outerSideMask == 0) {
            return renderer.renderBlockByRenderType(block, x, y, z);
        }
        PhotoModeBlockRenderContext.begin(
                x,
                y,
                z,
                this.riftflux$minChunkX << 4,
                (this.riftflux$maxChunkX + 1) << 4,
                this.riftflux$minChunkZ << 4,
                (this.riftflux$maxChunkZ + 1) << 4,
                outerSideMask,
                0
        );
        try {
            return renderer.renderBlockByRenderType(block, x, y, z);
        } finally {
            PhotoModeBlockRenderContext.end();
        }
    }

    @Unique
    private int riftflux$getOutermostHorizontalSideMask(int x, int z) {
        int originX = this.render.getOriginX();
        int originZ = this.render.getOriginZ();
        int chunkX = this.render.getChunkX();
        int chunkZ = this.render.getChunkZ();
        int sideMask = 0;
        if (x == originX && chunkX == this.riftflux$minChunkX) {
            sideMask |= 1 << 4;
        }
        if (x == originX + 15 && chunkX == this.riftflux$maxChunkX) {
            sideMask |= 1 << 5;
        }
        if (z == originZ && chunkZ == this.riftflux$minChunkZ) {
            sideMask |= 1 << 2;
        }
        if (z == originZ + 15 && chunkZ == this.riftflux$maxChunkZ) {
            sideMask |= 1 << 3;
        }
        return sideMask;
    }

    @Unique
    private boolean riftflux$ensureHorizontalGridBounds() {
        if (this.riftflux$computedHorizontalGridBounds) {
            return this.riftflux$horizontalGridBounds != null
                    && this.riftflux$minChunkX <= this.riftflux$maxChunkX
                    && this.riftflux$minChunkZ <= this.riftflux$maxChunkZ;
        }

        this.riftflux$computedHorizontalGridBounds = true;
        AngelicaPhotoModeRenderGrid.Bounds bounds = AngelicaPhotoModeRenderGrid.get();
        if (bounds == null) {
            return false;
        }

        this.riftflux$horizontalGridBounds = bounds;
        this.riftflux$minChunkX = bounds.minChunkX;
        this.riftflux$maxChunkX = bounds.maxChunkX;
        this.riftflux$minChunkZ = bounds.minChunkZ;
        this.riftflux$maxChunkZ = bounds.maxChunkZ;
        return true;
    }

    private boolean riftflux$isNetherWorld() {
        Minecraft minecraft = Minecraft.getMinecraft();
        World world = minecraft == null ? null : minecraft.theWorld;
        return world != null
                && world.provider != null
                && (world.provider.isHellWorld || world.provider.dimensionId == -1);
    }
}
