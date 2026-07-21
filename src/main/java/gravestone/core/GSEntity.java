package gravestone.core;

import com.voidsrift.riftflux.entity.RiftFluxEntityRegistry;
import com.voidsrift.riftflux.riftflux;
import cpw.mods.fml.common.registry.EntityRegistry;
import gravestone.config.GraveStoneConfig;
import gravestone.entity.monster.EntitySkeletonCat;
import gravestone.entity.monster.EntitySkeletonDog;
import gravestone.entity.monster.EntitySkullCrawler;
import gravestone.entity.monster.EntityWitherSkullCrawler;
import gravestone.entity.monster.EntityZombieCat;
import gravestone.entity.monster.EntityZombieDog;
import gravestone.entity.monster.EntityZombieSkullCrawler;
import net.minecraft.entity.EnumCreatureType;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;

public class GSEntity {
   private static GSEntity instance;
   public static final String ZOMBIE_DOG_NAME = "GSZombieDog";
   public static final String ZOMBIE_CAT_NAME = "GSZombieCat";
   public static final String SKEKETON_DOG_NAME = "GSSkeletonDog";
   public static final String SKEKETON_CAT_NAME = "GSSkeletonCat";
   public static final String SKULL_CRAWLER_NAME = "GSSkullCrawler";
   public static final String WITHER_SKULL_CRAWLER_NAME = "GSWitherSkullCrawler";
   public static final String ZOMBIE_SKULL_CRAWLER_NAME = "GSZombieSkullCrawler";

   private GSEntity() {
      instance = this;
      this.getEntity();
   }

   public static GSEntity getInstance() {
      return instance == null ? new GSEntity() : instance;
   }

   public void getEntity() {
      RiftFluxEntityRegistry.registerModEntity(EntityZombieDog.class, "GSZombieDog", riftflux.instance, 100, 1, true);
      if (GraveStoneConfig.spawnZombieDogs) {
         EntityRegistry.addSpawn(EntityZombieDog.class, 2, 1, 1, EnumCreatureType.monster, BiomeDictionary.getBiomesForType(Type.FOREST));
      }

      RiftFluxEntityRegistry.registerModEntity(EntityZombieCat.class, "GSZombieCat", riftflux.instance, 100, 1, true);
      if (GraveStoneConfig.spawnZombieCats) {
         EntityRegistry.addSpawn(EntityZombieCat.class, 2, 1, 1, EnumCreatureType.monster, BiomeDictionary.getBiomesForType(Type.JUNGLE));
      }

      RiftFluxEntityRegistry.registerModEntity(EntitySkeletonDog.class, "GSSkeletonDog", riftflux.instance, 100, 1, true);
      if (GraveStoneConfig.spawnSkeletonDogs) {
         EntityRegistry.addSpawn(EntityZombieDog.class, 2, 1, 1, EnumCreatureType.monster, BiomeDictionary.getBiomesForType(Type.FOREST));
      }

      RiftFluxEntityRegistry.registerModEntity(EntitySkeletonCat.class, "GSSkeletonCat", riftflux.instance, 100, 1, true);
      if (GraveStoneConfig.spawnSkeletonCats) {
         EntityRegistry.addSpawn(EntityZombieCat.class, 2, 1, 1, EnumCreatureType.monster, BiomeDictionary.getBiomesForType(Type.JUNGLE));
      }

      RiftFluxEntityRegistry.registerModEntity(EntitySkullCrawler.class, "GSSkullCrawler", riftflux.instance, 100, 1, true);
      RiftFluxEntityRegistry.registerModEntity(EntityWitherSkullCrawler.class, "GSWitherSkullCrawler", riftflux.instance, 100, 1, true);
      EntityRegistry.addSpawn(EntityWitherSkullCrawler.class, 3, 1, 4, EnumCreatureType.monster, BiomeDictionary.getBiomesForType(Type.NETHER));
      RiftFluxEntityRegistry.registerModEntity(EntityZombieSkullCrawler.class, "GSZombieSkullCrawler", riftflux.instance, 100, 1, true);
   }
}
