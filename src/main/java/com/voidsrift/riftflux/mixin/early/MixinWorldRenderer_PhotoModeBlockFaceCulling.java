package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.client.photomode.IsometricPhotoModeController;
import com.voidsrift.riftflux.client.photomode.PhotoModeBlockRenderContext;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(WorldRenderer.class)
public abstract class MixinWorldRenderer_PhotoModeBlockFaceCulling {

    @Unique
    private static final boolean RIFTFLUX_HAS_CELERITAS_STACK =
            riftflux$hasClass("com.gtnewhorizons.angelica.AngelicaMod")
                    || riftflux$hasClass("com.ventooth.beddium.modules.TerrainRendering.CeleritasWorldRenderer");

    @Shadow
    public World worldObj;

    @Redirect(
            method = "updateRenderer",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/RenderBlocks;renderBlockByRenderType(Lnet/minecraft/block/Block;III)Z"
            ),
            require = 0
    )
    private boolean riftflux$renderPhotoModeBlocksWithAllFaces(
            RenderBlocks renderer,
            Block block,
            int x,
            int y,
            int z
    ) {
        if (!this.riftflux$beginPhotoModeEdgeBlockContext(x, y, z)) {
            return renderer.renderBlockByRenderType(block, x, y, z);
        }

        try {
            return renderer.renderBlockByRenderType(block, x, y, z);
        } finally {
            PhotoModeBlockRenderContext.end();
        }
    }

    @Unique
    private boolean riftflux$shouldApplyVanillaPhotoModeEdgeFix() {
        if (RIFTFLUX_HAS_CELERITAS_STACK || this.riftflux$isNetherWorld()) {
            return false;
        }

        return IsometricPhotoModeController.instance().isActive();
    }

    @Unique
    private boolean riftflux$beginPhotoModeEdgeBlockContext(int x, int y, int z) {
        if (!this.riftflux$shouldApplyVanillaPhotoModeEdgeFix()) {
            return false;
        }

        Minecraft minecraft = Minecraft.getMinecraft();
        if (minecraft == null || minecraft.gameSettings == null || minecraft.thePlayer == null) {
            return false;
        }

        int renderDistance = Math.max(1, minecraft.gameSettings.renderDistanceChunks);
        int centerChunkX = MathHelper.floor_double(minecraft.thePlayer.posX) >> 4;
        int centerChunkZ = MathHelper.floor_double(minecraft.thePlayer.posZ) >> 4;
        int minX = (centerChunkX - renderDistance) << 4;
        int maxXExclusive = (centerChunkX + renderDistance + 1) << 4;
        int minZ = (centerChunkZ - renderDistance) << 4;
        int maxZExclusive = (centerChunkZ + renderDistance + 1) << 4;

        if (x != minX && x != maxXExclusive - 1 && z != minZ && z != maxZExclusive - 1) {
            return false;
        }

        PhotoModeBlockRenderContext.begin(x, y, z, minX, maxXExclusive, minZ, maxZExclusive);
        return true;
    }

    @Unique
    private boolean riftflux$isNetherWorld() {
        return this.worldObj != null
                && this.worldObj.provider != null
                && (this.worldObj.provider.isHellWorld || this.worldObj.provider.dimensionId == -1);
    }

    @Unique
    private static boolean riftflux$hasClass(String className) {
        try {
            ClassLoader loader = MixinWorldRenderer_PhotoModeBlockFaceCulling.class.getClassLoader();
            return loader.getResource(className.replace('.', '/') + ".class") != null;
        } catch (Throwable ignored) {
            return false;
        }
    }
}
