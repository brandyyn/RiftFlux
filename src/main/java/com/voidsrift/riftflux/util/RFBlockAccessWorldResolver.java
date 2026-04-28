package com.voidsrift.riftflux.util;

import com.voidsrift.riftflux.mixin.accessor.ChunkCacheAccessor;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public final class RFBlockAccessWorldResolver {
    private static final String ANGELICA_WORLD_SLICE_CLASS =
            "com.gtnewhorizons.angelica.rendering.celeritas.world.WorldSlice";

    private RFBlockAccessWorldResolver() {
    }

    public static World resolve(IBlockAccess access) {
        if (access instanceof World) {
            return (World) access;
        }
        if (access != null && ANGELICA_WORLD_SLICE_CLASS.equals(access.getClass().getName())) {
            try {
                return AngelicaWorldSliceBridge.getWorld(access);
            } catch (Throwable ignored) {
                return null;
            }
        }
        if (access instanceof ChunkCache) {
            try {
                return ((ChunkCacheAccessor) access).riftflux$getWorldObj();
            } catch (Throwable ignored) {
                return null;
            }
        }
        return null;
    }
}
