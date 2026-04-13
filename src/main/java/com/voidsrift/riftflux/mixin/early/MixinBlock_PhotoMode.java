package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.client.photomode.IsometricPhotoModeController;
import com.voidsrift.riftflux.mixin.accessor.ChunkCacheAccessor;
import java.lang.reflect.Field;
import net.minecraft.block.Block;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.EmptyChunk;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(net.minecraft.block.Block.class)
public abstract class MixinBlock_PhotoMode {
    private static final String ANGELICA_WORLD_SLICE_CLASS =
            "com.gtnewhorizons.angelica.rendering.celeritas.world.WorldSlice";

    @Inject(method = "shouldSideBeRendered", at = @At("HEAD"), cancellable = true)
    private void riftflux$disableFaceCullingInPhotoMode(IBlockAccess world, int x, int y, int z, int side, CallbackInfoReturnable<Boolean> cir) {
        Block block = (Block) (Object) this;
        if (!IsometricPhotoModeController.instance().isActive()
                || !block.isOpaqueCube()
                || block.getMaterial().isLiquid()) {
            return;
        }

        Block adjacent = world.getBlock(x, y, z);
        if (adjacent.isOpaqueCube()) {
            cir.setReturnValue(Boolean.FALSE);
            return;
        }

        if (!world.isAirBlock(x, y, z)) {
            cir.setReturnValue(Boolean.TRUE);
            return;
        }

        int sourceX = x;
        int sourceY = y;
        int sourceZ = z;
        switch (side) {
            case 0:
                sourceY++;
                break;
            case 1:
                sourceY--;
                break;
            case 2:
                sourceZ++;
                break;
            case 3:
                sourceZ--;
                break;
            case 4:
                sourceX++;
                break;
            case 5:
                sourceX--;
                break;
            default:
                break;
        }

        cir.setReturnValue(Boolean.valueOf(
                this.riftflux$shouldRenderAgainstAir(world, sourceX, sourceY, sourceZ, x, y, z)
        ));
    }

    private boolean riftflux$shouldRenderAgainstAir(
            IBlockAccess access,
            int sourceX,
            int sourceY,
            int sourceZ,
            int airX,
            int airY,
            int airZ
    ) {
        if (this.riftflux$isWorldCutoff(access, airX, airY, airZ) || !access.isAirBlock(airX, airY, airZ)) {
            return true;
        }

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

            int state = this.riftflux$getExposureState(access, x, y, z);
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

    private int riftflux$getExposureState(IBlockAccess access, int x, int y, int z) {
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

            Object worldObject = this.riftflux$readField(access, "world");
            if (!(worldObject instanceof World)) {
                return true;
            }

            return !((World) worldObject).blockExists(x, y, z);
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
