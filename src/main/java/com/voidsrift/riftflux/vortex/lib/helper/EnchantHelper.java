package com.voidsrift.riftflux.vortex.lib.helper;

import com.voidsrift.riftflux.ModConfig;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentDurability;
import net.minecraft.init.Items;
import net.minecraft.item.ItemDye;
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

   @SideOnly(Side.CLIENT)
   public static GlintColor resolveGlintColor(int value) {
      switch (value) {
      case 7:
         return new GlintColor(true, true, 0.36F, 0.36F, 0.36F);
      case 8:
         return new GlintColor(true, false, 0.36F, 0.36F, 0.36F);
      case 11:
         return new GlintColor(true, true, 0.72F, 0.39F, 0.02F);
      case 12:
         return new GlintColor(true, true, 0.35F, 0.48F, 0.57F);
      case 13:
         return new GlintColor(true, true, 0.54F, 0.22F, 0.57F);
      case 15:
         return new GlintColor(true, true, 0.52F, 0.52F, 0.52F);
      case 16:
         return new GlintColor(false, false, 0.0F, 0.0F, 0.0F);
      default:
         int color = value >= 0 && value <= 15 ? ItemDye.field_150922_c[15 - value] : value;
         float[] colors = generateColorsForGlint(color);
         return new GlintColor(true, false, colors[0], colors[1], colors[2]);
      }
   }

   @SideOnly(Side.CLIENT)
   public static GlintColor defaultGlintColor() {
      return new GlintColor(true, false, 0.38F, 0.19F, 0.608F);
   }

   @SideOnly(Side.CLIENT)
   public static final class GlintColor {
      public final boolean render;
      public final boolean subtractive;
      public final float red;
      public final float green;
      public final float blue;

      private GlintColor(boolean render, boolean subtractive, float red, float green, float blue) {
         this.render = render;
         this.subtractive = subtractive;
         this.red = red;
         this.green = green;
         this.blue = blue;
      }
   }
}
