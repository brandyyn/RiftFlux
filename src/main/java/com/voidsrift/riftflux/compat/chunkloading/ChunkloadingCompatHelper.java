package com.voidsrift.riftflux.compat.chunkloading;

import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public final class ChunkloadingCompatHelper {
    private ChunkloadingCompatHelper() {
    }

    public static void ensureBlockExists(World world, int x, int y, int z) {
        if (world instanceof WorldServer) {
            WorldServer worldServer = (WorldServer) world;
            ensureBlockExists(worldServer, x, y, z);
        }
    }

    public static void ensureBlockExists(WorldServer world, int x, int y, int z) {
        if (world != null && !world.blockExists(x, y, z)) {
            world.getChunkProvider().loadChunk(x >> 4, z >> 4);
        }
    }

    public static void ensureBlockExists(World world, Object pointLike) {
        if (pointLike == null) {
            return;
        }

        Integer x = invokeIntNoArgs(pointLike, "getX");
        Integer y = invokeIntNoArgs(pointLike, "getY");
        Integer z = invokeIntNoArgs(pointLike, "getZ");
        if (x != null && y != null && z != null) {
            ensureBlockExists(world, x.intValue(), y.intValue(), z.intValue());
        }
    }

    private static Integer invokeIntNoArgs(Object target, String methodName) {
        try {
            Object result = target.getClass().getMethod(methodName).invoke(target);
            if (result instanceof Number) {
                return Integer.valueOf(((Number) result).intValue());
            }
        } catch (Throwable ignored) {
        }
        return null;
    }
}
