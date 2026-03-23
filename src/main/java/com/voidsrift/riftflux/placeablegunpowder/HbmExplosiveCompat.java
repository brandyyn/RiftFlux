package com.voidsrift.riftflux.placeablegunpowder;

import com.voidsrift.riftflux.ModConfig;
import net.minecraft.block.Block;
import net.minecraft.world.World;

final class HbmExplosiveCompat {

    private static final String DETONATABLE_CLASS = "com.hbm.blocks.bomb.BlockDetonatable";
    private static Class<?> detonatableClass;
    private static boolean detonatableLookupAttempted;

    private HbmExplosiveCompat() {
    }

    static void tryIgniteAdjacentExplosiveBarrels(World world, int x, int y, int z) {
        if (!ModConfig.placeableGunpowderIgnitesHbmBarrels) {
            return;
        }

        tryIgniteBarrel(world, x + 1, y, z);
        tryIgniteBarrel(world, x - 1, y, z);
        tryIgniteBarrel(world, x, y + 1, z);
        tryIgniteBarrel(world, x, y - 1, z);
        tryIgniteBarrel(world, x, y, z + 1);
        tryIgniteBarrel(world, x, y, z - 1);
    }

    private static void tryIgniteBarrel(World world, int x, int y, int z) {
        Block block = world.getBlock(x, y, z);
        if (!isExplosiveBarrel(block)) {
            return;
        }

        world.setBlockToAir(x, y, z);
        block.onBlockDestroyedByExplosion(world, x, y, z, null);
    }

    private static boolean isExplosiveBarrel(Block block) {
        if (block == null) {
            return false;
        }

        Class<?> blockClass = block.getClass();
        String className = blockClass.getName();
        if (!className.startsWith("com.hbm.blocks.")) {
            return false;
        }
        if (!blockClass.getSimpleName().contains("Barrel")) {
            return false;
        }

        Class<?> detonatable = getDetonatableClass();
        return detonatable != null && detonatable.isInstance(block);
    }

    private static Class<?> getDetonatableClass() {
        if (!detonatableLookupAttempted) {
            detonatableLookupAttempted = true;
            try {
                detonatableClass = Class.forName(DETONATABLE_CLASS);
            } catch (Throwable ignored) {
                detonatableClass = null;
            }
        }
        return detonatableClass;
    }
}
