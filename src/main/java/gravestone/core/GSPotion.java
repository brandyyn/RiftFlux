package gravestone.core;

import gravestone.config.GraveStoneConfig;
import gravestone.potion.CursePotion;

public class GSPotion {
   public static CursePotion curse;
   public static final int CURSE_DEFAULT_ID = 31;

   public static void init() {
      curse = new CursePotion(GraveStoneConfig.cursePotionEffectId, true, 0);
   }
}
