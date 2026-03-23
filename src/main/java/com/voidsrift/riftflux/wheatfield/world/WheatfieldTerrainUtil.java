package com.voidsrift.riftflux.wheatfield.world;

import com.voidsrift.riftflux.wheatfield.WheatfieldContent;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.World;

final class WheatfieldTerrainUtil {
    private static final float PREDOMINANT_WHEATFIELD_THRESHOLD = 0.6F;

    private WheatfieldTerrainUtil() {
    }

    static boolean isActive() {
        return WheatfieldContent.wheatfieldBiome != null;
    }

    static int getWheatfieldBiomeId() {
        return isActive() ? WheatfieldContent.wheatfieldBiome.biomeID : -1;
    }

    static boolean isWheatfieldBiome(int biomeId) {
        return isActive() && biomeId == WheatfieldContent.wheatfieldBiome.biomeID;
    }

    static boolean isRiverBiome(int biomeId) {
        return biomeId == BiomeGenBase.river.biomeID || biomeId == BiomeGenBase.frozenRiver.biomeID;
    }

    static boolean isWheatfieldBiome(BiomeGenBase biome) {
        return isActive() && biome == WheatfieldContent.wheatfieldBiome;
    }

    static boolean chunkHasWheatfield(BiomeGenBase[] biomeArray) {
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

    static boolean chunkIsPredominantlyWheatfield(BiomeGenBase[] biomeArray) {
        if (!isActive() || biomeArray == null || biomeArray.length == 0) {
            return false;
        }

        int wheatfieldColumns = 0;
        for (BiomeGenBase biome : biomeArray) {
            if (isWheatfieldBiome(biome)) {
                wheatfieldColumns++;
            }
        }

        return wheatfieldColumns >= Math.max(1, Math.round(biomeArray.length * PREDOMINANT_WHEATFIELD_THRESHOLD));
    }

    static boolean chunkHasWheatfield(World world, int chunkX, int chunkZ) {
        if (!isActive() || world == null || world.provider == null || world.provider.dimensionId != 0) {
            return false;
        }

        int startX = chunkX << 4;
        int startZ = chunkZ << 4;
        for (int offsetX = 0; offsetX < 16; offsetX++) {
            for (int offsetZ = 0; offsetZ < 16; offsetZ++) {
                if (world.getBiomeGenForCoords(startX + offsetX, startZ + offsetZ) == WheatfieldContent.wheatfieldBiome) {
                    return true;
                }
            }
        }
        return false;
    }

    static boolean chunkIsPredominantlyWheatfield(World world, int chunkX, int chunkZ) {
        if (!isActive() || world == null || world.provider == null || world.provider.dimensionId != 0) {
            return false;
        }

        int startX = chunkX << 4;
        int startZ = chunkZ << 4;
        BiomeGenBase[] biomes = world.getWorldChunkManager().loadBlockGeneratorData(null, startX, startZ, 16, 16);
        return chunkIsPredominantlyWheatfield(biomes);
    }
}
