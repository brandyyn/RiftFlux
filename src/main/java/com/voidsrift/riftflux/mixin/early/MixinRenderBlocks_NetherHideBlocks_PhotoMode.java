package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.client.photomode.IsometricPhotoModeController;
import com.voidsrift.riftflux.mixin.accessor.ChunkCacheAccessor;
import java.lang.reflect.Field;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
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
        if (this.riftflux$shouldHideBoundaryFace(block, x, y, z, 2)) {
            ci.cancel();
        }
    }

    @Inject(method = "renderFaceZPos", at = @At("HEAD"), cancellable = true)
    private void riftflux$hideNetherPhotoModeZPosFace(Block block, double x, double y, double z, IIcon icon, CallbackInfo ci) {
        if (this.riftflux$shouldHideBoundaryFace(block, x, y, z, 3)) {
            ci.cancel();
        }
    }

    @Inject(method = "renderFaceXNeg", at = @At("HEAD"), cancellable = true)
    private void riftflux$hideNetherPhotoModeXNegFace(Block block, double x, double y, double z, IIcon icon, CallbackInfo ci) {
        if (this.riftflux$shouldHideBoundaryFace(block, x, y, z, 4)) {
            ci.cancel();
        }
    }

    @Inject(method = "renderFaceXPos", at = @At("HEAD"), cancellable = true)
    private void riftflux$hideNetherPhotoModeXPosFace(Block block, double x, double y, double z, IIcon icon, CallbackInfo ci) {
        if (this.riftflux$shouldHideBoundaryFace(block, x, y, z, 5)) {
            ci.cancel();
        }
    }

    @Inject(method = "renderBlockByRenderType", at = @At("HEAD"), cancellable = true)
    private void riftflux$hideNetherPhotoModeBlocks(Block block, int x, int y, int z, CallbackInfoReturnable<Boolean> cir) {
        if (!ModConfig.isometricPhotoModeHideNetherNetherrackAndBedrock) {
            return;
        }

        IsometricPhotoModeController controller = IsometricPhotoModeController.instance();
        if (!controller.isActive()) {
            return;
        }

        if (!this.riftflux$isTargetBlock(block)) {
            return;
        }

        if (!this.riftflux$isNetherBlockAccess(this.blockAccess)) {
            return;
        }

        if (this.riftflux$isHiddenBedrockBlock(block)) {
            cir.setReturnValue(Boolean.FALSE);
            return;
        }

        boolean touchesBoundary = block == Blocks.netherrack
                && controller.touchesHorizontalPhotoModeRenderBoundary(x, z);
        if (!touchesBoundary && !this.riftflux$hasAnyEffectiveAirNeighbor(this.blockAccess, x, y, z)) {
            cir.setReturnValue(Boolean.FALSE);
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

    private boolean riftflux$hasAnyEffectiveAirNeighbor(IBlockAccess access, int x, int y, int z) {
        return this.riftflux$isEffectiveExposedAirNeighbor(access, x, y, z, x + 1, y, z)
                || this.riftflux$isEffectiveExposedAirNeighbor(access, x, y, z, x - 1, y, z)
                || this.riftflux$isEffectiveExposedAirNeighbor(access, x, y, z, x, y + 1, z)
                || this.riftflux$isEffectiveExposedAirNeighbor(access, x, y, z, x, y - 1, z)
                || this.riftflux$isEffectiveExposedAirNeighbor(access, x, y, z, x, y, z + 1)
                || this.riftflux$isEffectiveExposedAirNeighbor(access, x, y, z, x, y, z - 1);
    }

    private boolean riftflux$isEffectiveExposedAirNeighbor(
            IBlockAccess access,
            int sourceX,
            int sourceY,
            int sourceZ,
            int airX,
            int airY,
            int airZ
    ) {
        if (this.riftflux$isWorldCutoff(access, airX, airY, airZ) || !access.isAirBlock(airX, airY, airZ)) {
            return false;
        }

        return this.riftflux$hasSecondaryExposureNeighbor(access, sourceX, sourceY, sourceZ, airX, airY, airZ);
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
