package gravestone.core.compatibility.forestry;

import gravestone.config.GraveStoneConfig;
import net.minecraft.item.Item;

public class GSCompatibilityForestry {
   public static boolean isInstalled = false;
   public static Item backpackItemT1;
   public static Item backpackItemT2;
   public static final int DEFAULT_BEEKEEPER_ID = 80;
   public static final int DEFAULT_LUMBERJACK_ID = 81;

   private GSCompatibilityForestry() {
   }

   public static int getApicultureVillagerID() {
      return isInstalled
            ? GSCompatibilityForestryBackpacks.getApicultureVillagerID()
            : DEFAULT_BEEKEEPER_ID;
   }

   public static int getArboricultureVillagerID() {
      return isInstalled
            ? GSCompatibilityForestryBackpacks.getArboricultureVillagerID()
            : DEFAULT_LUMBERJACK_ID;
   }

   public static void addBackpack() {
      if (!isInstalled || !GraveStoneConfig.enableForestryBackpacks) {
         return;
      }
      GSCompatibilityForestryBackpacks.addBackpack();
   }
}
