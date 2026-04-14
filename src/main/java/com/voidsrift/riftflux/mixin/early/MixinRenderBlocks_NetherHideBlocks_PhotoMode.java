package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.client.photomode.IsometricPhotoModeController;
import com.voidsrift.riftflux.mixin.accessor.ChunkCacheAccessor;
import java.lang.reflect.Field;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.EmptyChunk;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RenderBlocks.class)
public abstract class MixinRenderBlocks_NetherHideBlocks_PhotoMode {

    private static final String RIFTFLUX_NETHERLICIOUS_BRITTLE_BEDROCK_CLASS =
            "DelirusCrux.Netherlicious.Common.Blocks.BrittleBedrock";
    private static final String ANGELICA_WORLD_SLICE_CLASS =
            "com.gtnewhorizons.angelica.rendering.celeritas.world.WorldSlice";

    @Shadow
    public IBlockAccess blockAccess;

    @Inject(method = "renderFaceZNeg", at = @At("HEAD"), cancellable = true)
    private void riftflux$hideNetherPhotoModeZNegFace(Block block, double x, double y, double z, IIcon icon, CallbackInfo ci) {
        this.riftflux$applyBoundaryLavaFaceLighting(block, x, y, z, 2);

        if (this.riftflux$shouldHideFullBlock(block, x, y, z)) {
            ci.cancel();
            return;
        }

        if (this.riftflux$shouldHideFaceAgainstHiddenNeighbor(x, y, z, 2)) {
            ci.cancel();
            return;
        }

        if (this.riftflux$shouldHideBoundaryFace(block, x, y, z, 2)) {
            ci.cancel();
        }
    }

    @Inject(method = "renderFaceZPos", at = @At("HEAD"), cancellable = true)
    private void riftflux$hideNetherPhotoModeZPosFace(Block block, double x, double y, double z, IIcon icon, CallbackInfo ci) {
        this.riftflux$applyBoundaryLavaFaceLighting(block, x, y, z, 3);

        if (this.riftflux$shouldHideFullBlock(block, x, y, z)) {
            ci.cancel();
            return;
        }

        if (this.riftflux$shouldHideFaceAgainstHiddenNeighbor(x, y, z, 3)) {
            ci.cancel();
            return;
        }

        if (this.riftflux$shouldHideBoundaryFace(block, x, y, z, 3)) {
            ci.cancel();
        }
    }

    @Inject(method = "renderFaceXNeg", at = @At("HEAD"), cancellable = true)
    private void riftflux$hideNetherPhotoModeXNegFace(Block block, double x, double y, double z, IIcon icon, CallbackInfo ci) {
        this.riftflux$applyBoundaryLavaFaceLighting(block, x, y, z, 4);

        if (this.riftflux$shouldHideFullBlock(block, x, y, z)) {
            ci.cancel();
            return;
        }

        if (this.riftflux$shouldHideFaceAgainstHiddenNeighbor(x, y, z, 4)) {
            ci.cancel();
            return;
        }

        if (this.riftflux$shouldHideBoundaryFace(block, x, y, z, 4)) {
            ci.cancel();
        }
    }

    @Inject(method = "renderFaceXPos", at = @At("HEAD"), cancellable = true)
    private void riftflux$hideNetherPhotoModeXPosFace(Block block, double x, double y, double z, IIcon icon, CallbackInfo ci) {
        this.riftflux$applyBoundaryLavaFaceLighting(block, x, y, z, 5);

        if (this.riftflux$shouldHideFullBlock(block, x, y, z)) {
            ci.cancel();
            return;
        }

        if (this.riftflux$shouldHideFaceAgainstHiddenNeighbor(x, y, z, 5)) {
            ci.cancel();
            return;
        }

        if (this.riftflux$shouldHideBoundaryFace(block, x, y, z, 5)) {
            ci.cancel();
        }
    }

    @Inject(method = "renderFaceYNeg", at = @At("HEAD"), cancellable = true)
    private void riftflux$hideNetherPhotoModeYNegFace(Block block, double x, double y, double z, IIcon icon, CallbackInfo ci) {
        if (this.riftflux$shouldHideFullBlock(block, x, y, z) || this.riftflux$shouldHideFaceAgainstHiddenNeighbor(x, y, z, 0)) {
            ci.cancel();
        }
    }

    @Inject(method = "renderFaceYPos", at = @At("HEAD"), cancellable = true)
    private void riftflux$hideNetherPhotoModeYPosFace(Block block, double x, double y, double z, IIcon icon, CallbackInfo ci) {
        if (this.riftflux$shouldHideFullBlock(block, x, y, z) || this.riftflux$shouldHideFaceAgainstHiddenNeighbor(x, y, z, 1)) {
            ci.cancel();
        }
    }

    @Inject(method = "renderBlockByRenderType", at = @At("HEAD"), cancellable = true)
    private void riftflux$hideNetherPhotoModeBlocks(Block block, int x, int y, int z, CallbackInfoReturnable<Boolean> cir) {
        if (this.riftflux$shouldHideFullBlock(block, x, y, z)) {
            cir.setReturnValue(Boolean.FALSE);
        }
    }

    @Redirect(
            method = "renderBlockLiquid",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/Block;shouldSideBeRendered(Lnet/minecraft/world/IBlockAccess;IIII)Z"
            ),
            require = 0
    )
    private boolean riftflux$forceLavaBoundarySideRender(
            Block block,
            IBlockAccess access,
            int x,
            int y,
            int z,
            int side
    ) {
        boolean shouldRender = block.shouldSideBeRendered(access, x, y, z, side);
        if (shouldRender || access == null || !this.riftflux$isLavaBlock(block) || side < 2 || side > 5) {
            return shouldRender;
        }

        if (!this.riftflux$isNetherPhotoModeHideActive()) {
            return shouldRender;
        }

        return IsometricPhotoModeController.instance().isOutsideHorizontalPhotoModeRenderBoundary(x, z);
    }

    @Redirect(
            method = "renderBlockLiquid",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/Block;getMixedBrightnessForBlock(Lnet/minecraft/world/IBlockAccess;III)I"
            ),
            require = 0
    )
    private int riftflux$stabilizeBoundaryLavaBrightness(
            Block block,
            IBlockAccess access,
            int x,
            int y,
            int z
    ) {
        if (!this.riftflux$isLavaBlock(block)
                || !this.riftflux$isNetherPhotoModeHideActive()
                || access == null
                || !this.riftflux$isNetherBlockAccess(access)) {
            return block.getMixedBrightnessForBlock(access, x, y, z);
        }

        if (IsometricPhotoModeController.instance().isOutsideHorizontalPhotoModeRenderBoundary(x, z)) {
            return 0x00F000F0;
        }

        return block.getMixedBrightnessForBlock(access, x, y, z);
    }

    @Inject(method = "getLiquidHeight", at = @At("HEAD"), cancellable = true, require = 0)
    private void riftflux$stabilizeBoundaryLavaHeight(
            int x,
            int y,
            int z,
            Material material,
            CallbackInfoReturnable<Float> cir
    ) {
        if (material != Material.lava
                || !this.riftflux$isNetherPhotoModeHideActive()
                || this.blockAccess == null
                || !this.riftflux$isNetherBlockAccess(this.blockAccess)) {
            return;
        }

        if (IsometricPhotoModeController.instance().isOutsideHorizontalPhotoModeRenderBoundary(x, z)) {
            cir.setReturnValue(1.0F);
        }
    }

    private boolean riftflux$isTargetBlock(Block block) {
        return this.riftflux$isHiddenBedrockBlock(block)
                || block == Blocks.netherrack
                || block == Blocks.lava
                || block == Blocks.flowing_lava;
    }

    private boolean riftflux$isHiddenBedrockBlock(Block block) {
        return block == Blocks.bedrock || RIFTFLUX_NETHERLICIOUS_BRITTLE_BEDROCK_CLASS.equals(block.getClass().getName());
    }

    private boolean riftflux$isNetherBlockAccess(IBlockAccess access) {
        if (access == null) {
            return false;
        }

        if (access instanceof ChunkCache) {
            World world = ((ChunkCacheAccessor) access).riftflux$getWorldObj();
            return world != null
                    && world.provider != null
                    && (world.provider.isHellWorld || world.provider.dimensionId == -1);
        }

        if (this.riftflux$isWorldSliceInstance(access)) {
            World world = this.riftflux$getWorldSliceWorld(access);
            return world != null
                    && world.provider != null
                    && (world.provider.isHellWorld || world.provider.dimensionId == -1);
        }

        if (access instanceof World) {
            World world = (World) access;
            return world.provider != null
                    && (world.provider.isHellWorld || world.provider.dimensionId == -1);
        }

        Minecraft mc = Minecraft.getMinecraft();
        World world = mc == null ? null : mc.theWorld;
        return world != null
                && world.provider != null
                && (world.provider.isHellWorld || world.provider.dimensionId == -1);
    }

    private boolean riftflux$shouldHideBoundaryFace(Block block, double x, double y, double z, int side) {
        if (!ModConfig.isometricPhotoModeHideNetherNetherrackAndBedrock
                || !IsometricPhotoModeController.instance().isActive()
                || block != Blocks.netherrack
                || this.blockAccess == null
                || !this.riftflux$isNetherBlockAccess(this.blockAccess)) {
            return false;
        }

        int neighborX = MathHelper.floor_double(x);
        int neighborZ = MathHelper.floor_double(z);
        switch (side) {
            case 2:
                neighborZ--;
                break;
            case 3:
                neighborZ++;
                break;
            case 4:
                neighborX--;
                break;
            case 5:
                neighborX++;
                break;
            default:
                return false;
        }

        return IsometricPhotoModeController.instance().isOutsideHorizontalPhotoModeRenderBoundary(neighborX, neighborZ);
    }

    private boolean riftflux$shouldHideFaceAgainstHiddenNeighbor(double x, double y, double z, int side) {
        if (this.blockAccess == null) {
            return false;
        }

        int neighborX = MathHelper.floor_double(x);
        int neighborY = MathHelper.floor_double(y);
        int neighborZ = MathHelper.floor_double(z);
        switch (side) {
            case 0:
                neighborY--;
                break;
            case 1:
                neighborY++;
                break;
            case 2:
                neighborZ--;
                break;
            case 3:
                neighborZ++;
                break;
            case 4:
                neighborX--;
                break;
            case 5:
                neighborX++;
                break;
            default:
                return false;
        }

        if (this.riftflux$isWorldCutoff(this.blockAccess, neighborX, neighborY, neighborZ)) {
            return false;
        }

        Block neighbor = this.blockAccess.getBlock(neighborX, neighborY, neighborZ);
        return neighbor != null && this.riftflux$shouldHideFullBlock(neighbor, neighborX, neighborY, neighborZ);
    }

    private void riftflux$applyBoundaryLavaFaceLighting(Block block, double x, double y, double z, int side) {
        if (!this.riftflux$shouldForceBoundaryLavaFaceLighting(block, x, y, z, side)) {
            return;
        }

        int bx = MathHelper.floor_double(x);
        int by = MathHelper.floor_double(y);
        int bz = MathHelper.floor_double(z);
        Tessellator tessellator = Tessellator.instance;
        tessellator.setBrightness(block.getMixedBrightnessForBlock(this.blockAccess, bx, by, bz));

        int color = block.colorMultiplier(this.blockAccess, bx, by, bz);
        float red = (float) (color >> 16 & 0xFF) / 255.0F;
        float green = (float) (color >> 8 & 0xFF) / 255.0F;
        float blue = (float) (color & 0xFF) / 255.0F;
        if (EntityRenderer.anaglyphEnable) {
            float convertedRed = (red * 30.0F + green * 59.0F + blue * 11.0F) / 100.0F;
            float convertedGreen = (red * 30.0F + green * 70.0F) / 100.0F;
            float convertedBlue = (red * 30.0F + blue * 70.0F) / 100.0F;
            red = convertedRed;
            green = convertedGreen;
            blue = convertedBlue;
        }

        tessellator.setColorOpaque_F(red, green, blue);
    }

    private boolean riftflux$shouldForceBoundaryLavaFaceLighting(Block block, double x, double y, double z, int side) {
        if (!this.riftflux$isLavaBlock(block)
                || side < 2
                || side > 5
                || this.blockAccess == null
                || !this.riftflux$isNetherPhotoModeHideActive()
                || !this.riftflux$isNetherBlockAccess(this.blockAccess)) {
            return false;
        }

        int neighborX = MathHelper.floor_double(x);
        int neighborZ = MathHelper.floor_double(z);
        switch (side) {
            case 2:
                neighborZ--;
                break;
            case 3:
                neighborZ++;
                break;
            case 4:
                neighborX--;
                break;
            case 5:
                neighborX++;
                break;
            default:
                return false;
        }

        return IsometricPhotoModeController.instance().isOutsideHorizontalPhotoModeRenderBoundary(neighborX, neighborZ);
    }

    private boolean riftflux$shouldHideFullBlock(Block block, double x, double y, double z) {
        int bx = MathHelper.floor_double(x);
        int by = MathHelper.floor_double(y);
        int bz = MathHelper.floor_double(z);
        return this.riftflux$shouldHideFullBlock(block, bx, by, bz);
    }

    private boolean riftflux$shouldHideFullBlock(Block block, int x, int y, int z) {
        if (!this.riftflux$isNetherPhotoModeHideActive()) {
            return false;
        }

        if (this.blockAccess == null) {
            return false;
        }

        if (!this.riftflux$isTargetBlock(block) || !this.riftflux$isNetherBlockAccess(this.blockAccess)) {
            return false;
        }

        if (this.riftflux$isHiddenBedrockBlock(block)) {
            return true;
        }

        IsometricPhotoModeController controller = IsometricPhotoModeController.instance();
        boolean touchesBoundary = block == Blocks.netherrack && controller.touchesHorizontalPhotoModeRenderBoundary(x, z);
        return !touchesBoundary && !this.riftflux$hasAnyEffectiveAirNeighbor(this.blockAccess, x, y, z, block);
    }

    private boolean riftflux$hasAnyEffectiveAirNeighbor(IBlockAccess access, int x, int y, int z, Block sourceBlock) {
        return this.riftflux$isEffectiveExposedAirNeighbor(access, x, y, z, x + 1, y, z, sourceBlock)
                || this.riftflux$isEffectiveExposedAirNeighbor(access, x, y, z, x - 1, y, z, sourceBlock)
                || this.riftflux$isEffectiveExposedAirNeighbor(access, x, y, z, x, y + 1, z, sourceBlock)
                || this.riftflux$isEffectiveExposedAirNeighbor(access, x, y, z, x, y - 1, z, sourceBlock)
                || this.riftflux$isEffectiveExposedAirNeighbor(access, x, y, z, x, y, z + 1, sourceBlock)
                || this.riftflux$isEffectiveExposedAirNeighbor(access, x, y, z, x, y, z - 1, sourceBlock);
    }

    private boolean riftflux$isEffectiveExposedAirNeighbor(
            IBlockAccess access,
            int sourceX,
            int sourceY,
            int sourceZ,
            int airX,
            int airY,
            int airZ,
            Block sourceBlock
    ) {
        if (access == null) {
            return false;
        }

        if (this.riftflux$isWorldCutoff(access, airX, airY, airZ)) {
            return this.riftflux$isLavaBlock(sourceBlock)
                    && IsometricPhotoModeController.instance().isOutsideHorizontalPhotoModeRenderBoundary(airX, airZ);
        }

        if (!access.isAirBlock(airX, airY, airZ)) {
            return false;
        }

        return this.riftflux$hasSecondaryExposureNeighbor(access, sourceX, sourceY, sourceZ, airX, airY, airZ);
    }

    private boolean riftflux$isNetherPhotoModeHideActive() {
        if (!ModConfig.isometricPhotoModeHideNetherNetherrackAndBedrock) {
            return false;
        }

        IsometricPhotoModeController controller = IsometricPhotoModeController.instance();
        return controller.isActive();
    }

    private boolean riftflux$isLavaBlock(Block block) {
        return block == Blocks.lava || block == Blocks.flowing_lava;
    }

    private boolean riftflux$hasSecondaryExposureNeighbor(
            IBlockAccess access,
            int sourceX,
            int sourceY,
            int sourceZ,
            int airX,
            int airY,
            int airZ
    ) {
        boolean unknownNeighbor = false;
        int[][] neighbors = new int[][]{
                new int[]{airX + 1, airY, airZ},
                new int[]{airX - 1, airY, airZ},
                new int[]{airX, airY + 1, airZ},
                new int[]{airX, airY - 1, airZ},
                new int[]{airX, airY, airZ + 1},
                new int[]{airX, airY, airZ - 1}
        };

        for (int[] neighbor : neighbors) {
            int x = neighbor[0];
            int y = neighbor[1];
            int z = neighbor[2];
            if (x == sourceX && y == sourceY && z == sourceZ) {
                continue;
            }

            int state = this.riftflux$getNeighborExposureState(access, x, y, z);
            if (state == 0) {
                return true;
            }
            if (state < 0) {
                unknownNeighbor = true;
                continue;
            }

            Block neighborBlock = access.getBlock(x, y, z);
            if (neighborBlock == null
                    || !neighborBlock.isOpaqueCube()
                    || neighborBlock.getMaterial().isLiquid()) {
                return true;
            }
        }

        return unknownNeighbor;
    }

    private int riftflux$getNeighborExposureState(IBlockAccess access, int x, int y, int z) {
        if (this.riftflux$isWorldCutoff(access, x, y, z)) {
            return -1;
        }

        return access.isAirBlock(x, y, z) ? 0 : 1;
    }

    private boolean riftflux$isWorldCutoff(IBlockAccess access, int x, int y, int z) {
        if (access == null) {
            return true;
        }

        if (y < 0 || y >= 256) {
            return true;
        }

        if (access instanceof ChunkCache) {
            return this.riftflux$isChunkCacheCutoff((ChunkCache) access, x, y, z);
        }

        if (this.riftflux$isWorldSliceInstance(access)) {
            return this.riftflux$isWorldSliceCutoff(access, x, y, z);
        }

        if (access instanceof World) {
            return !((World) access).blockExists(x, y, z);
        }

        return false;
    }

    private boolean riftflux$isChunkCacheCutoff(ChunkCache cache, int x, int y, int z) {
        ChunkCacheAccessor accessor = (ChunkCacheAccessor) cache;
        Chunk[][] chunks = accessor.riftflux$getChunkArray();
        int chunkX = (x >> 4) - accessor.riftflux$getChunkX();
        int chunkZ = (z >> 4) - accessor.riftflux$getChunkZ();
        World world = accessor.riftflux$getWorldObj();

        if (chunks == null || chunkX < 0 || chunkX >= chunks.length) {
            return world == null || !world.blockExists(x, y, z);
        }

        Chunk[] row = chunks[chunkX];
        if (row == null || chunkZ < 0 || chunkZ >= row.length) {
            return world == null || !world.blockExists(x, y, z);
        }

        Chunk chunk = row[chunkZ];
        if (chunk == null || chunk instanceof EmptyChunk || !chunk.isChunkLoaded) {
            return world == null || !world.blockExists(x, y, z);
        }

        return false;
    }

    private boolean riftflux$isWorldSliceInstance(IBlockAccess access) {
        if (access == null) {
            return false;
        }

        Class<?> type = access.getClass();
        while (type != null) {
            if (ANGELICA_WORLD_SLICE_CLASS.equals(type.getName())) {
                return true;
            }
            type = type.getSuperclass();
        }
        return false;
    }

    private World riftflux$getWorldSliceWorld(IBlockAccess access) {
        try {
            Object worldObject = this.riftflux$readField(access, "world");
            return worldObject instanceof World ? (World) worldObject : null;
        } catch (Throwable ignored) {
            return null;
        }
    }

    private boolean riftflux$isWorldSliceCutoff(IBlockAccess access, int x, int y, int z) {
        try {
            Object volumeObject = this.riftflux$readField(access, "volume");
            if (!(volumeObject instanceof StructureBoundingBox)) {
                return true;
            }
            StructureBoundingBox volume = (StructureBoundingBox) volumeObject;
            if (!volume.isVecInside(x, y, z)) {
                return true;
            }

            World world = this.riftflux$getWorldSliceWorld(access);
            return world == null || !world.blockExists(x, y, z);
        } catch (Throwable ignored) {
            return true;
        }
    }

    private Object riftflux$readField(Object instance, String fieldName) throws IllegalAccessException, NoSuchFieldException {
        Class<?> type = instance.getClass();
        while (type != null) {
            try {
                Field field = type.getDeclaredField(fieldName);
                field.setAccessible(true);
                return field.get(instance);
            } catch (NoSuchFieldException ignored) {
                type = type.getSuperclass();
            }
        }

        throw new NoSuchFieldException(fieldName);
    }

}
