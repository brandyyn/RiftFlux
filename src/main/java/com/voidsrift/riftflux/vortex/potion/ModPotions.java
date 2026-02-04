package com.voidsrift.riftflux.vortex.potion;

import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;

public class ModPotions {
   public static final ResourceLocation icon = new ResourceLocation("riftflux", "textures/gui/potion.png");
   public static Potion headBuff;

   public static final void init() {
      int[] ids = new int[2];

      int i;
      for (i = 0; i < ids.length; ++i) {
         for(int j = 0; j < Potion.potionTypes.length; ++j) {
            if (Potion.potionTypes[j] == null) {
               ids[i] = j;
               break;
            }
         }
      }

      int idx = 0;
      headBuff = (new PotionHeadBuff(ids[idx], false, 0)).setPotionName("potion.headbuff");
   }
}
