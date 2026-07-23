package gravestone.core;

import gravestone.block.enums.EnumGraves;
import gravestone.block.enums.EnumSpawner;
import gravestone.config.GraveStoneConfig;
import gravestone.core.logger.GSLogger;
import gravestone.entity.monster.EntitySkullCrawler;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class GSMobSpawn {
   public static final String WITHER_ID = "WitherBoss";
   private static final int HELL_HEIGHT = 51;
   public static Map<String, Constructor<EntityLiving>> mobNameToClassMapping = new HashMap<>();
   public static List<String> MOB_ID = new ArrayList<>(Arrays.asList("Zombie", "Skeleton"));
   public static List<String> DOG_ID = new ArrayList<>(Arrays.asList("riftflux.GSZombieDog", "riftflux.GSSkeletonDog"));
   public static List<String> CAT_ID = new ArrayList<>(Arrays.asList("riftflux.GSZombieCat", "riftflux.GSSkeletonCat"));
   public static List<String> HELL_MOB_ID = new ArrayList<>(Arrays.asList("PigZombie", "Skeleton"));
   public static List<String> skeletonSpawnerMobs = new ArrayList<>(Arrays.asList("Skeleton", "Skeleton", "Skeleton", "Skeleton", "riftflux.GSSkeletonDog", "riftflux.GSSkeletonCat"));
   public static List<String> zombieSpawnerMobs = new ArrayList<>(Arrays.asList("Zombie", "Zombie", "Zombie", "Zombie", "riftflux.GSZombieDog", "riftflux.GSZombieCat"));
   public static List<String> catacombsStatuesMobs = new ArrayList<>(Arrays.asList("Skeleton", "Zombie"));

   private GSMobSpawn() {
   }

   private static boolean canSpawnHellCreatures(World world, int x, int y, int z) {
      if (world == null) {
         return false;
      } else {
         return y < 51 && world.getBlock(x, y - 1, z).equals(Blocks.nether_brick);
      }
   }

   public static Entity getMobEntity(World world, EnumGraves graveType, int x, int y, int z) {
      String id;
      switch(graveType) {
      case WOODEN_DOG_STATUE:
      case SANDSTONE_DOG_STATUE:
      case STONE_DOG_STATUE:
      case MOSSY_DOG_STATUE:
      case IRON_DOG_STATUE:
      case GOLDEN_DOG_STATUE:
      case DIAMOND_DOG_STATUE:
      case EMERALD_DOG_STATUE:
      case LAPIS_DOG_STATUE:
      case REDSTONE_DOG_STATUE:
      case OBSIDIAN_DOG_STATUE:
      case QUARTZ_DOG_STATUE:
      case ICE_DOG_STATUE:
         id = getMobID(world.rand, GSMobSpawn.EnumMobType.UNDEAD_DOGS);
         break;
      case WOODEN_CAT_STATUE:
      case SANDSTONE_CAT_STATUE:
      case STONE_CAT_STATUE:
      case MOSSY_CAT_STATUE:
      case IRON_CAT_STATUE:
      case GOLDEN_CAT_STATUE:
      case DIAMOND_CAT_STATUE:
      case EMERALD_CAT_STATUE:
      case LAPIS_CAT_STATUE:
      case REDSTONE_CAT_STATUE:
      case OBSIDIAN_CAT_STATUE:
      case QUARTZ_CAT_STATUE:
      case ICE_CAT_STATUE:
         id = getMobID(world.rand, GSMobSpawn.EnumMobType.UNDEAD_CATS);
         break;
      case WOODEN_HORSE_STATUE:
      case SANDSTONE_HORSE_STATUE:
      case STONE_HORSE_STATUE:
      case MOSSY_HORSE_STATUE:
      case IRON_HORSE_STATUE:
      case GOLDEN_HORSE_STATUE:
      case DIAMOND_HORSE_STATUE:
      case EMERALD_HORSE_STATUE:
      case LAPIS_HORSE_STATUE:
      case REDSTONE_HORSE_STATUE:
      case OBSIDIAN_HORSE_STATUE:
      case QUARTZ_HORSE_STATUE:
      case ICE_HORSE_STATUE:
         return null;
      default:
         if (canSpawnHellCreatures(world, x, y, z) && world.rand.nextInt(10) == 0) {
            id = getMobID(world.rand, GSMobSpawn.EnumMobType.HELL_MOBS);
            if (id.equals("Skeleton")) {
               EntitySkeleton skeleton = getSkeleton(world);
               skeleton.setSkeletonType(1);
               return skeleton;
            }
         } else {
            id = getMobID(world.rand, GSMobSpawn.EnumMobType.DEFAULT_MOBS);
            if (id.equals("Skeleton")) {
               return getSkeleton(world);
            }
         }
      }

      if (id == null) {
         return null;
      }

      EntityLiving entity = (EntityLiving)EntityList.createEntityByName(id, world);
      if (entity == null) {
         entity = getForeinMob(world, id);
      }

      try {
         entity.onSpawnWithEgg((IEntityLivingData)null);
      } catch (Exception var8) {
         GSLogger.logError("getMobEntity exception with onSpawnWithEgg");
         var8.printStackTrace();
      }

      return entity;
   }

   public static Entity getMobEntityForSpawner(World world, EnumSpawner spawnerType, int x, int y, int z) {
      String mobId;
      switch(spawnerType) {
      case WITHER_SPAWNER:
         mobId = "WitherBoss";
         break;
      case SKELETON_SPAWNER:
         mobId = getMobForSkeletonSpawner(world.rand);
         if (mobId.equals("Skeleton") && world.rand.nextInt(10) == 0) {
            EntitySkeleton skeleton = getSkeleton(world);
            skeleton.setSkeletonType(1);
            return skeleton;
         }
         break;
      case ZOMBIE_SPAWNER:
      default:
         mobId = getMobForZombieSpawner(world.rand);
      }

      EntityLiving entity = (EntityLiving)EntityList.createEntityByName(mobId, world);
      if (entity == null) {
         entity = getForeinMob(world, mobId);
      }

      try {
         entity.onSpawnWithEgg((IEntityLivingData)null);
      } catch (Exception var8) {
         GSLogger.logError("getMobEntity exception with onSpawnWithEgg");
         var8.printStackTrace();
      }

      return entity;
   }

   private static EntitySkeleton getSkeleton(World world) {
      EntitySkeleton skeleton = (EntitySkeleton)EntityList.createEntityByName("Skeleton", world);
      if (world.rand.nextInt(2) == 0) {
         skeleton.setCurrentItemOrArmor(0, new ItemStack(Items.stone_sword, 1, 0));
      } else {
         skeleton.setCurrentItemOrArmor(0, new ItemStack(Items.bow, 1, 0));
      }

      return skeleton;
   }

   public static EntitySkeleton getSkeleton(World world, byte skeletonType) {
      EntitySkeleton skeleton = (EntitySkeleton)EntityList.createEntityByName("Skeleton", world);
      if (skeletonType == 0) {
         skeleton.setCurrentItemOrArmor(0, new ItemStack(Items.bow, 1, 0));
      } else {
         skeleton.setCurrentItemOrArmor(0, new ItemStack(Items.stone_sword, 1, 0));
      }

      return skeleton;
   }

   public static boolean isWitherSkeleton(EntitySkeleton skeleton) {
      return skeleton.getSkeletonType() == 1;
   }

   private static EntityLiving getForeinMob(World world, String mobName) {
      EntityLiving mob = null;

      try {
         mob = mobNameToClassMapping.get(mobName).newInstance(world);
      } catch (InstantiationException var4) {
         GSLogger.logError("getForeinMob InstantiationException. mob name " + mobName);
         var4.printStackTrace();
      } catch (IllegalAccessException var5) {
         GSLogger.logError("getForeinMob IllegalAccessException. mob name " + mobName);
         var5.printStackTrace();
      } catch (InvocationTargetException var6) {
         GSLogger.logError("getForeinMob InvocationTargetException. mob name " + mobName);
         var6.getCause().printStackTrace();
      } catch (NullPointerException var7) {
         GSLogger.logError("getForeinMob NullPointerException. mob name " + mobName);
         var7.getCause().printStackTrace();
      }

      return mob;
   }

   public static String getMobID(Random random, GSMobSpawn.EnumMobType mobType) {
      switch(mobType) {
      case HELL_MOBS:
         return HELL_MOB_ID.get(random.nextInt(HELL_MOB_ID.size()));
      case UNDEAD_DOGS:
         return getEnabledPetMob(random, GraveStoneConfig.spawnZombieDogs, DOG_ID.get(0), GraveStoneConfig.spawnSkeletonDogs, DOG_ID.get(1));
      case UNDEAD_CATS:
         return getEnabledPetMob(random, GraveStoneConfig.spawnZombieCats, CAT_ID.get(0), GraveStoneConfig.spawnSkeletonCats, CAT_ID.get(1));
      case DEFAULT_MOBS:
      default:
         return MOB_ID.get(random.nextInt(MOB_ID.size()));
      }
   }

   public static boolean spawnMob(World world, Entity mob, double x, double y, double z, boolean checkSpawn) {
      float rotation = world.rand.nextFloat() * 360.0F;
      return spawnMob(world, mob, x, y, z, rotation, checkSpawn);
   }

   public static boolean spawnMob(World world, Entity mob, double x, double y, double z, float rotation, boolean checkSpawn) {
      EntityLiving livingEntity = (EntityLiving)mob;
      boolean canSpawn = false;
      double xPosition = x + 0.5D;
      double zPosition = z + 0.5D;
      mob.setLocationAndAngles(xPosition, y, zPosition, rotation, 0.0F);
      if (checkSpawn && !livingEntity.getCanSpawnHere()) {
         if (!(mob instanceof EntityZombie)) {
            ++xPosition;
            mob.setLocationAndAngles(xPosition, y, zPosition, rotation, 0.0F);
            if (livingEntity.getCanSpawnHere()) {
               canSpawn = true;
            } else {
               --xPosition;
               ++zPosition;
               mob.setLocationAndAngles(xPosition, y, zPosition, rotation, 0.0F);
               if (livingEntity.getCanSpawnHere()) {
                  canSpawn = true;
               } else {
                  zPosition = zPosition - 2.0D;
                  mob.setLocationAndAngles(xPosition, y, zPosition, rotation, 0.0F);
                  if (livingEntity.getCanSpawnHere()) {
                     canSpawn = true;
                  } else {
                     --xPosition;
                     ++zPosition;
                     mob.setLocationAndAngles(xPosition, y, zPosition, rotation, 0.0F);
                     if (livingEntity.getCanSpawnHere()) {
                        canSpawn = true;
                     }
                  }
               }
            }
         }
      } else {
         canSpawn = true;
      }

      if (canSpawn) {
         xPosition = x + (double)world.rand.nextFloat();
         double yPosition = y + (double)world.rand.nextFloat();
         zPosition = z + (double)world.rand.nextFloat();
         world.spawnParticle("largesmoke", xPosition, yPosition + 2.0D, zPosition, 0.0D, 0.0D, 0.0D);
         world.spawnParticle("flame", xPosition, yPosition + 1.0D, zPosition, 0.0D, 0.0D, 0.0D);
         world.spawnEntityInWorld(mob);
         world.playAuxSFX(2004, (int)x, (int)y, (int)z, 0);
         return true;
      } else {
         return false;
      }
   }

   public static boolean checkChance(Random random) {
      return random.nextInt(100) < GraveStoneConfig.spawnChance;
   }

   public static String getMobForSkeletonSpawner(Random random) {
      int variants = 4 + (GraveStoneConfig.spawnSkeletonDogs ? 1 : 0) + (GraveStoneConfig.spawnSkeletonCats ? 1 : 0);
      int selected = random.nextInt(variants);
      if (selected < 4) {
         return "Skeleton";
      }
      if (GraveStoneConfig.spawnSkeletonDogs && selected-- == 4) {
         return "riftflux.GSSkeletonDog";
      }
      return "riftflux.GSSkeletonCat";
   }

   public static String getMobForZombieSpawner(Random random) {
      int variants = 4 + (GraveStoneConfig.spawnZombieDogs ? 1 : 0) + (GraveStoneConfig.spawnZombieCats ? 1 : 0);
      int selected = random.nextInt(variants);
      if (selected < 4) {
         return "Zombie";
      }
      if (GraveStoneConfig.spawnZombieDogs && selected-- == 4) {
         return "riftflux.GSZombieDog";
      }
      return "riftflux.GSZombieCat";
   }

   public static String getMobForStatueSpawner(Random random) {
      return catacombsStatuesMobs.get(random.nextInt(catacombsStatuesMobs.size()));
   }

   private static String getEnabledPetMob(Random random, boolean firstEnabled, String firstId, boolean secondEnabled, String secondId) {
      if (firstEnabled && secondEnabled) {
         return random.nextBoolean() ? firstId : secondId;
      }
      if (firstEnabled) {
         return firstId;
      }
      return secondEnabled ? secondId : null;
   }

   public static void spawnCrawler(Entity entity, EntitySkullCrawler crawler) {
      if (entity.worldObj.rand.nextInt(10) == 0) {
         spawnMob(entity.worldObj, crawler, (double)((int)Math.floor(entity.posX)), entity.posY + 1.5D, (double)((int)Math.floor(entity.posZ)), entity.rotationYaw, false);
      }

   }

   public static enum EnumMobType {
      DEFAULT_MOBS,
      HELL_MOBS,
      UNDEAD_DOGS,
      UNDEAD_CATS;
   }
}
