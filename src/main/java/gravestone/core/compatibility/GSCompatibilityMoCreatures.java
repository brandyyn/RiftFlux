package gravestone.core.compatibility;

import gravestone.config.GraveStoneConfig;
import gravestone.core.GSMobSpawn;
import gravestone.core.logger.GSLogger;
import java.lang.reflect.Constructor;
import java.util.List;
import net.minecraft.entity.EntityLiving;
import net.minecraft.world.World;

public class GSCompatibilityMoCreatures {
   protected static boolean isInstalled = false;
   public static final String MO_CREATURES_S_SKELETON = "drzhark.mocreatures.entity.monster.MoCEntitySilverSkeleton";
   public static final String MO_CREATURES_WRAITH = "drzhark.mocreatures.entity.monster.MoCEntityWraith";
   public static final String MO_CREATURES_F_WRAITH = "drzhark.mocreatures.entity.monster.MoCEntityFlameWraith";
   public static final String MO_CREATURES_SCORPIONS = "drzhark.mocreatures.entity.monster.MoCEntityScorpion";

   private GSCompatibilityMoCreatures() {
   }

   public static void addMobs() {
      if (GraveStoneConfig.spawnMoCreaturesMobs) {
         GSLogger.logInfo("start Mo'Creatures mobs loading");
         addMobToList(GSMobSpawn.MOB_ID, "SilverSkeleton", getForeinMobConstructor("drzhark.mocreatures.entity.monster.MoCEntitySilverSkeleton"));
         addMobToList(GSMobSpawn.MOB_ID, "Wraith", getForeinMobConstructor("drzhark.mocreatures.entity.monster.MoCEntityWraith"));
         addMobToList(GSMobSpawn.HELL_MOB_ID, "FlameWraith", getForeinMobConstructor("drzhark.mocreatures.entity.monster.MoCEntityFlameWraith"));
         addMobToList(GSMobSpawn.skeletonSpawnerMobs, "SilverSkeleton", getForeinMobConstructor("drzhark.mocreatures.entity.monster.MoCEntitySilverSkeleton"));
         GSLogger.logInfo("end Mo'Creatures mobs loading");
      }

   }

   private static Constructor getForeinMobConstructor(String path) {
      Constructor<EntityLiving> constructor = null;

      try {
         Class mobClass = Class.forName(path);
         constructor = mobClass.getConstructor(World.class);
      } catch (ClassNotFoundException var3) {
         GSLogger.logError("getForeinMobConstructor ClassNotFoundException. class path " + path);
         var3.printStackTrace();
      } catch (NoSuchMethodException var4) {
         GSLogger.logError("getForeinMobConstructor NoSuchMethodException. class path " + path);
         var4.printStackTrace();
      }

      return constructor;
   }

   private static void addMobToList(List<String> MOB_ID, String mobName, Constructor<EntityLiving> constructor) {
      if (constructor != null) {
         MOB_ID.add(mobName);
         GSMobSpawn.mobNameToClassMapping.put(mobName, constructor);
      }

   }

   public static boolean isLoaded() {
      return isInstalled;
   }
}
