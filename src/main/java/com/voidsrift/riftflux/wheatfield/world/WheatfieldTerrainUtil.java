package com.voidsrift.riftflux.wheatfield.world;

import com.voidsrift.riftflux.wheatfield.WheatfieldContent;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

public final class WheatfieldTerrainUtil {
    private WheatfieldTerrainUtil() {
    }

    public static boolean isActive() {
        return WheatfieldContent.wheatfieldBiome != null;
    }

    public static int getWheatfieldBiomeId() {
        return isActive() ? WheatfieldContent.wheatfieldBiome.biomeID : -1;
    }

    public static boolean isWheatfieldBiome(int biomeId) {
        return isActive() && biomeId == WheatfieldContent.wheatfieldBiome.biomeID;
    }

    public static boolean isRiverBiome(int biomeId) {
        return biomeId == BiomeGenBase.river.biomeID || biomeId == BiomeGenBase.frozenRiver.biomeID;
    }

    public static boolean isWheatfieldBiome(BiomeGenBase biome) {
        return isActive() && biome == WheatfieldContent.wheatfieldBiome;
    }

    public static boolean chunkHasWheatfield(BiomeGenBase[] biomeArray) {
        if (!isActive() || biomeArray == null) {
            return false;
        }

        for (BiomeGenBase biome : biomeArray) {
            if (isWheatfieldBiome(biome)) {
                return true;
            }
        }
        return false;
    }

    public static boolean chunkHasWheatfield(byte[] biomeIdArray) {
        if (!isActive() || biomeIdArray == null) {
            return false;
        }

        int wheatfieldBiomeId = getWheatfieldBiomeId();
        for (byte biomeId : biomeIdArray) {
            if ((biomeId & 255) == wheatfieldBiomeId) {
                return true;
            }
        }
        return false;
    }

    public static boolean chunkHasWheatfield(World world, int chunkX, int chunkZ) {
        return chunkHasWheatfield(loadChunkBiomes(world, chunkX, chunkZ));
    }

    public static BiomeGenBase[] loadChunkBiomes(World world, int chunkX, int chunkZ) {
        if (!isOverworld(world)) {
            return null;
        }

        int startX = chunkX << 4;
        int startZ = chunkZ << 4;
        return world.getWorldChunkManager().loadBlockGeneratorData(null, startX, startZ, 16, 16);
    }

    public static boolean isOverworld(World world) {
        if (!isActive() || world == null || world.provider == null || world.provider.dimensionId != 0) {
            return false;
        }
        return true;
    }
}
