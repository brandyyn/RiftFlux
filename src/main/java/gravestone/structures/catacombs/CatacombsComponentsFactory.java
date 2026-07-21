package gravestone.structures.catacombs;

import gravestone.structures.catacombs.components.Bridge;
import gravestone.structures.catacombs.components.CatacombsBaseComponent;
import gravestone.structures.catacombs.components.Corridor;
import gravestone.structures.catacombs.components.CreeperRoom;
import gravestone.structures.catacombs.components.Crossing;
import gravestone.structures.catacombs.components.EnderHall;
import gravestone.structures.catacombs.components.GraveCorridor;
import gravestone.structures.catacombs.components.GraveHall;
import gravestone.structures.catacombs.components.SpidersCorridor;
import gravestone.structures.catacombs.components.StatuesHall;
import gravestone.structures.catacombs.components.TrapCorridor;
import gravestone.structures.catacombs.components.Treasury;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Random;

public class CatacombsComponentsFactory {
   private CatacombsComponentsFactory() {
   }

   public static Class getNextComponentForLevel(Class componentClass, Random random, int level) {
      int chance = random.nextInt(100);
      switch(level) {
      case 1:
         if (chance >= 25) {
            return getCorridorType(random);
         } else if (chance >= 10) {
            if (componentClass == Crossing.class) {
               return getCorridorType(random);
            }

            return getCrossingType(random);
         } else if (chance >= 5) {
            if (componentClass == SpidersCorridor.class) {
               return getCorridorType(random);
            }

            return SpidersCorridor.class;
         } else {
            if (componentClass == EnderHall.class) {
               return getCorridorType(random);
            }

            return EnderHall.class;
         }
      default:
         if (chance >= 55) {
            return getCorridorType(random);
         } else if (chance >= 40) {
            return componentClass == Crossing.class ? getCorridorType(random) : getCrossingType(random);
         } else if (chance >= 30) {
            return componentClass == SpidersCorridor.class ? getCorridorType(random) : SpidersCorridor.class;
         } else if (chance >= 20) {
            return componentClass == EnderHall.class ? getCorridorType(random) : EnderHall.class;
         } else if (chance >= 10) {
            return getHallType(random);
         } else if (chance >= 5) {
            return componentClass == Bridge.class ? getCorridorType(random) : Bridge.class;
         } else {
            return Treasury.class;
         }
      }
   }

   public static Class getNextComponent(Class componentClass, CatacombsLevel.COMPONENT_SIDE componentSide, Random random, int level) {
      if (componentSide == CatacombsLevel.COMPONENT_SIDE.TOP) {
         return getNextComponentForLevel(componentClass, random, level);
      } else {
         return level != 1 && random.nextInt(100) < 5 ? Treasury.class : Corridor.class;
      }
   }

   public static Class getCorridorType(Random random) {
      int corridorChance = random.nextInt(100);
      if (corridorChance >= 65) {
         return Corridor.class;
      } else {
         return corridorChance >= 10 ? GraveCorridor.class : TrapCorridor.class;
      }
   }

   private static Class getCrossingType(Random random) {
      return random.nextInt(100) >= 10 ? Crossing.class : CreeperRoom.class;
   }

   private static Class getHallType(Random random) {
      int hallChance = random.nextInt(10);
      return hallChance >= 2 ? GraveHall.class : StatuesHall.class;
   }

   public static CatacombsBaseComponent createComponent(CatacombsBaseComponent component, Random random, int direction, int level, Class buildComponent, CatacombsLevel.COMPONENT_SIDE componentSide) {
      if (component != null) {
         int y = component.getYEnd();
         int x;
         int z;
         if (componentSide == CatacombsLevel.COMPONENT_SIDE.TOP) {
            x = component.getTopXEnd();
            z = component.getTopZEnd();
         } else if (componentSide == CatacombsLevel.COMPONENT_SIDE.LEFT) {
            x = component.getLeftXEnd();
            z = component.getLeftZEnd();
         } else {
            x = component.getRightXEnd();
            z = component.getRightZEnd();
         }

         try {
            Constructor<CatacombsBaseComponent> constructor = buildComponent.getConstructor(Integer.TYPE, Integer.TYPE, Random.class, Integer.TYPE, Integer.TYPE, Integer.TYPE);
            return constructor.newInstance(direction, level, random, x, y, z);
         } catch (NoSuchMethodException var10) {
            var10.printStackTrace();
         } catch (InstantiationException var11) {
            var11.printStackTrace();
         } catch (IllegalAccessException var12) {
            var12.printStackTrace();
         } catch (InvocationTargetException var13) {
            var13.printStackTrace();
         }
      }

      return null;
   }
}
