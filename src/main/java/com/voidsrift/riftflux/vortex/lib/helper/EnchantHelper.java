package com.voidsrift.riftflux.vortex.lib.helper;

import com.voidsrift.riftflux.ModConfig;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentDurability;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import com.voidsrift.riftflux.vortex.item.ModItems;

public class EnchantHelper {
   public static final String CUSTOM_GLINT_TAG = "customGlint";
   private static final int RANDOM_GLINT_COLOR_COUNT = 16;

   public static boolean canEnchantAtTable(ItemStack itemStack, Enchantment enchantment) {
      if (itemStack.getItem() != ModItems.backpack) {
         return true;
      } else {
         return ModConfig.backpackDurability || !(enchantment instanceof EnchantmentDurability);
      }
   }

   public static boolean hasCustomGlint(ItemStack itemStack) {
      return itemStack != null && itemStack.hasTagCompound() && itemStack.getTagCompound().hasKey(CUSTOM_GLINT_TAG, 3);
   }

   public static boolean hasStoredEnchantments(ItemStack itemStack) {
      return itemStack != null
              && itemStack.getItem() == Items.enchanted_book
              && Items.enchanted_book.func_92110_g(itemStack).tagCount() > 0;
   }

   public static boolean isRandomGlintEligible(ItemStack itemStack) {
      return itemStack != null && (itemStack.isItemEnchanted() || hasStoredEnchantments(itemStack));
   }

   public static void maybeApplyRandomGlint(ItemStack itemStack) {
      maybeApplyRandomGlint(itemStack, null);
   }

   public static void maybeApplyRandomGlint(ItemStack itemStack, Random random) {
      if (!ModConfig.randomizeEnchantedGlintColors || !isRandomGlintEligible(itemStack) || hasCustomGlint(itemStack)) {
         return;
      }

      NBTTagCompound tag = itemStack.getTagCompound();
      if (tag == null) {
         tag = new NBTTagCompound();
         itemStack.setTagCompound(tag);
      }

      tag.setInteger(CUSTOM_GLINT_TAG, nextRandomGlintColor(random));
   }

   private static int nextRandomGlintColor(Random random) {
      return random != null ? random.nextInt(RANDOM_GLINT_COLOR_COUNT)
              : ThreadLocalRandom.current().nextInt(RANDOM_GLINT_COLOR_COUNT);
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
