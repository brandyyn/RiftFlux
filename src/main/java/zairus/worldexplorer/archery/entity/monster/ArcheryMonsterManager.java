package zairus.worldexplorer.archery.entity.monster;

import cpw.mods.fml.common.registry.EntityRegistry;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.world.biome.BiomeGenBase;
import zairus.worldexplorer.archery.Archery;
import zairus.worldexplorer.core.IWEAddonMonsterManager;
import zairus.worldexplorer.core.entity.WEEntityRegistry;

public class ArcheryMonsterManager implements IWEAddonMonsterManager {
    @Override
    public void registerMobs() {
        WEEntityRegistry.registerEntity(
                EntitySkeletonExplorer.class,
                "riftflux_slingshot_skeleton",
                Archery.instance,
                64,
                1,
                true,
                0xFFFFFF,
                0x00FF00
        );
        for (int i = 0; i < BiomeGenBase.getBiomeGenArray().length; ++i) {
            BiomeGenBase biome = BiomeGenBase.getBiome(i);
            if (biome == null || biome == BiomeGenBase.mushroomIsland || biome == BiomeGenBase.mushroomIslandShore || biome == BiomeGenBase.sky) {
                continue;
            }
            EntityRegistry.addSpawn(EntitySkeletonExplorer.class, 15, 1, 4, EnumCreatureType.monster, biome);
        }
    }
}
