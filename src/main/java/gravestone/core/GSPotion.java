package gravestone.core;

import cpw.mods.fml.common.FMLLog;
import gravestone.config.GraveStoneConfig;
import gravestone.potion.CursePotion;
import net.minecraft.potion.Potion;

public class GSPotion {
   public static CursePotion curse;
   public static final int CURSE_DEFAULT_ID = 135;

   public static void init() {
      int configuredId = GraveStoneConfig.cursePotionEffectId;
      int selectedId = findFreePotionId(configuredId);
      if (selectedId < 0) {
         throw new IllegalStateException("No free potion ID available for the Gravestone curse effect.");
      }
      if (selectedId != configuredId) {
         FMLLog.warning("[RiftFlux] CursePotionEffectId %d is unavailable; using %d instead.", configuredId, selectedId);
      }
      curse = new CursePotion(selectedId, true, 0);
   }

   private static int findFreePotionId(int configuredId) {
      if (isPotionSlotFree(configuredId)) {
         return configuredId;
      }
      int firstScan = configuredId < 0 ? 0 : Math.min(Potion.potionTypes.length, configuredId + 1);
      for (int id = firstScan; id < Potion.potionTypes.length; ++id) {
         if (Potion.potionTypes[id] == null) {
            return id;
         }
      }
      int wrapLimit = configuredId < 0 ? 0 : Math.min(configuredId, Potion.potionTypes.length);
      for (int id = 0; id < wrapLimit; ++id) {
         if (Potion.potionTypes[id] == null) {
            return id;
         }
      }
      return -1;
   }

   private static boolean isPotionSlotFree(int id) {
      return id >= 0 && id < Potion.potionTypes.length && Potion.potionTypes[id] == null;
   }
}
