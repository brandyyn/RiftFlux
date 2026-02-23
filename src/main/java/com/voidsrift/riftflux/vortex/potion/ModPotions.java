package com.voidsrift.riftflux.vortex.potion;

import com.voidsrift.riftflux.ModConfig;
import cpw.mods.fml.common.FMLLog;
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

      int configuredId = ModConfig.highlanderPotionEffectId;
      int selectedId = ids[0];
      if (configuredId >= 0 && configuredId < Potion.potionTypes.length && Potion.potionTypes[configuredId] == null) {
         selectedId = configuredId;
      } else if (configuredId >= 0 && configuredId < Potion.potionTypes.length && Potion.potionTypes[configuredId] != null) {
         FMLLog.warning("[RiftFlux] HighlanderPotionEffectId %d is already occupied; using %d instead.", configuredId, selectedId);
      }

      headBuff = (new PotionHeadBuff(selectedId, false, 0)).setPotionName("potion.headbuff");
   }
}
