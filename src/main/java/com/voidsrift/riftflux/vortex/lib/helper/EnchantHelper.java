package com.voidsrift.riftflux.vortex.lib.helper;

import com.voidsrift.riftflux.ModConfig;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentDurability;
import net.minecraft.item.ItemStack;
import com.voidsrift.riftflux.vortex.item.ModItems;

public class EnchantHelper {
   public static boolean canEnchantAtTable(ItemStack itemStack, Enchantment enchantment) {
      if (itemStack.getItem() != ModItems.backpack) {
         return true;
      } else {
         return ModConfig.backpackDurability || !(enchantment instanceof EnchantmentDurability);
      }
   }

   @SideOnly(Side.CLIENT)
   public static float[] generateColorsForGlint(int color) {
      int red = color >> 16 & 255;
      int green = color >> 8 & 255;
      int blue = color & 255;
      int total = red + green + blue;
      int truncColor;
      if (total > 396) {
         float multiplier = 396.0F / (float)total;
         red = (int)((float)red * multiplier);
         green = (int)((float)green * multiplier);
         blue = (int)((float)blue * multiplier);
         truncColor = -16777216 + (red << 16) + (green << 8) + blue;
      } else {
         truncColor = color;
      }

      float[] colors = new float[]{(float)(truncColor >> 16 & 255) / 255.0F, (float)(truncColor >> 8 & 255) / 255.0F, (float)(truncColor & 255) / 255.0F};
      return colors;
   }
}
