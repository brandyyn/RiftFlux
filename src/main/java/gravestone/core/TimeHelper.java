package gravestone.core;

import net.minecraft.world.World;

public class TimeHelper {
   public static final int PRE_NIGHT = 12000;
   public static final int NIGHT = 14000;
   public static final int PRE_MORNING = 22500;
   public static final int FOG_START_TIME = 12000;
   public static final int FOG_END_TIME = 24000;
   public static final int SUN_SET = 13000;
   public static final int SUN_RISING = 23000;
   public static final int GRAVE_SPAWN_START_TIME = 13500;
   public static final int GRAVE_SPAWN_END_TIME = 22500;
   private static boolean isGraveSpawnTime;

   public static long getDayTime(World world) {
      return world.getWorldTime() % 24000L;
   }

   public static long getDayTime(long time) {
      return time % 24000L;
   }

   public static boolean isGraveSpawnTime() {
      return isGraveSpawnTime;
   }

   public static void setIsGraveSpawnTime(boolean isValidTime) {
      isGraveSpawnTime = isValidTime;
   }

   public static void updateIsGraveSpawnTime(World world) {
      long time = getDayTime(world);
      setIsGraveSpawnTime(time > 13500L && time < 22500L || world.isThundering());
   }

   public static boolean isFogTime(World world) {
      if (world.isRaining()) {
         return false;
      } else {
         long dayTime = getDayTime(world);
         return dayTime > 12000L && dayTime < 24000L;
      }
   }
}
