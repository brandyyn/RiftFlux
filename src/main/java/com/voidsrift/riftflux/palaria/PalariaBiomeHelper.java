package com.voidsrift.riftflux.palaria;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.minecraft.world.biome.BiomeGenBase;

public final class PalariaBiomeHelper {
    private PalariaBiomeHelper() {
    }

    public static BiomeGenBase[] overworldMonsterBiomes(boolean includeEnd) {
        List<BiomeGenBase> out = new ArrayList<BiomeGenBase>();
        add(out, BiomeGenBase.ocean);
        add(out, BiomeGenBase.plains);
        add(out, BiomeGenBase.extremeHills);
        add(out, BiomeGenBase.extremeHillsEdge);
        add(out, BiomeGenBase.forest);
        add(out, BiomeGenBase.forestHills);
        add(out, BiomeGenBase.jungle);
        add(out, BiomeGenBase.jungleHills);
        add(out, BiomeGenBase.mushroomIsland);
        add(out, BiomeGenBase.mushroomIslandShore);
        add(out, BiomeGenBase.beach);
        add(out, BiomeGenBase.river);
        add(out, BiomeGenBase.swampland);
        if (includeEnd) {
            add(out, BiomeGenBase.sky);
        }
        addNameMatches(out, "wheatfield", "unnamed");
        return out.toArray(new BiomeGenBase[out.size()]);
    }

    public static BiomeGenBase[] nimatinBiomes() {
        return new BiomeGenBase[]{
                BiomeGenBase.extremeHills,
                BiomeGenBase.extremeHillsEdge,
                BiomeGenBase.forest,
                BiomeGenBase.forestHills,
                BiomeGenBase.jungle,
                BiomeGenBase.jungleHills
        };
    }

    public static BiomeGenBase[] endBiomes() {
        return new BiomeGenBase[]{BiomeGenBase.sky};
    }

    public static BiomeGenBase[] netherBiomes() {
        return new BiomeGenBase[]{BiomeGenBase.hell};
    }

    private static void add(List<BiomeGenBase> list, BiomeGenBase biome) {
        if (biome != null && !list.contains(biome)) {
            list.add(biome);
        }
    }

    private static void addNameMatches(List<BiomeGenBase> list, String... needles) {
        BiomeGenBase[] biomes = BiomeGenBase.getBiomeGenArray();
        if (biomes == null) {
            return;
        }
        for (BiomeGenBase biome : biomes) {
            if (biome == null || biome.biomeName == null) {
                continue;
            }
            String name = biome.biomeName.toLowerCase(Locale.ROOT);
            for (String needle : needles) {
                if (name.contains(needle)) {
                    add(list, biome);
                    break;
                }
            }
        }
    }
}
