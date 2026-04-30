package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.client.photomode.IsometricPhotoModeController;
import com.voidsrift.riftflux.client.photomode.PhotoModeBlockRenderContext;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(RenderBlocks.class)
public abstract class MixinRenderBlocks_PhotoModeAllFacesBrightness {

    @Redirect(
            method = "renderStandardBlock",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/Minecraft;isAmbientOcclusionEnabled()Z"
            ),
            require = 0
    )
    private boolean riftflux$disableAmbientOcclusionForPhotoModeEdgeBlock() {
        return !PhotoModeBlockRenderContext.isActive() && Minecraft.isAmbientOcclusionEnabled();
    }

    @Redirect(
            method = "renderStandardBlockWithAmbientOcclusion",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/Block;shouldSideBeRendered(Lnet/minecraft/world/IBlockAccess;IIII)Z"
            ),
            require = 0
    )
    private boolean riftflux$forcePhotoModeAmbientOcclusionEdgeSideRender(
            Block block,
            IBlockAccess access,
            int x,
            int y,
            int z,
            int side
    ) {
        return this.riftflux$shouldRenderPhotoModeEdgeSide(block, access, x, y, z, side);
    }

    @Redirect(
            method = "renderStandardBlockWithColorMultiplier",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/Block;shouldSideBeRendered(Lnet/minecraft/world/IBlockAccess;IIII)Z"
            ),
            require = 0
    )
    private boolean riftflux$forcePhotoModeColorMultiplierEdgeSideRender(
            Block block,
            IBlockAccess access,
            int x,
            int y,
            int z,
            int side
    ) {
        return this.riftflux$shouldRenderPhotoModeEdgeSide(block, access, x, y, z, side);
    }

    @Redirect(
            method = "renderStandardBlockWithAmbientOcclusion",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/Block;getMixedBrightnessForBlock(Lnet/minecraft/world/IBlockAccess;III)I"
            ),
            require = 0
    )
    private int riftflux$stabilizePhotoModeAmbientOcclusionEdgeBrightness(
            Block block,
            IBlockAccess access,
            int x,
            int y,
            int z
    ) {
        return this.riftflux$getPhotoModeEdgeBrightness(block, access, x, y, z);
    }

    @Redirect(
            method = "renderStandardBlockWithColorMultiplier",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/Block;getMixedBrightnessForBlock(Lnet/minecraft/world/IBlockAccess;III)I"
            ),
            require = 0
    )
    private int riftflux$stabilizePhotoModeAllFacesBrightness(
            Block block,
            IBlockAccess access,
            int x,
            int y,
            int z
    ) {
        return this.riftflux$getPhotoModeEdgeBrightness(block, access, x, y, z);
    }

    @Unique
    private boolean riftflux$shouldRenderPhotoModeEdgeSide(
            Block block,
            IBlockAccess access,
            int x,
            int y,
            int z,
            int side
    ) {
        boolean shouldRender = block.shouldSideBeRendered(access, x, y, z, side);
        return shouldRender || side >= 2 && side <= 5
                && IsometricPhotoModeController.instance().isOutsideHorizontalPhotoModeRenderBoundary(x, z);
    }

    @Unique
    private int riftflux$getPhotoModeEdgeBrightness(
            Block block,
            IBlockAccess access,
            int x,
            int y,
            int z
    ) {
        if (PhotoModeBlockRenderContext.shouldUseSourceBrightness(x, y, z)) {
            int sourceBrightness = block.getMixedBrightnessForBlock(
                    access,
                    PhotoModeBlockRenderContext.x(),
                    PhotoModeBlockRenderContext.y(),
                    PhotoModeBlockRenderContext.z()
            );
            return this.riftflux$hasSkyLight() ? 0x00F00000 | sourceBrightness & 0x000000F0 : sourceBrightness;
        }

        return block.getMixedBrightnessForBlock(access, x, y, z);
    }

    @Unique
    private boolean riftflux$hasSkyLight() {
        Minecraft minecraft = Minecraft.getMinecraft();
        World world = minecraft == null ? null : minecraft.theWorld;
        return world == null || world.provider == null || !world.provider.hasNoSky;
    }
}
