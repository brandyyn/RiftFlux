package com.voidsrift.riftflux.vortex.item;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraftforge.common.util.EnumHelper;

public class ModItems {
   public static Item butterflyKnife;
   public static Item highlandSpirit;
   public static Item backpack;
   public static Item focusBand;
   public static Item toolbelt;
   public static Item runeThanatos;
   public static Item glintRune;
   public static Item gluttonyCharm;
   public static ToolMaterial BUTTERFLYKNIFE;
   public static ArmorMaterial EFFECTLESS = EnumHelper.addArmorMaterial("effectless", 0, new int[]{0, 0, 0, 0}, 0);
   public static ArmorMaterial BACKPACK;

   public static final void init() {
      int knifeDurability = com.voidsrift.riftflux.ModConfig.butterflyKnifeDurability;
      if (knifeDurability < 0) {
         knifeDurability = 0;
      }
      float knifeDamage = com.voidsrift.riftflux.ModConfig.butterflyKnifeDamage;
      float materialDamage = knifeDamage - 4.0F;
      BUTTERFLYKNIFE = EnumHelper.addToolMaterial("butterflyknife", 3, knifeDurability, 15.0F, materialDamage, 7);
      butterflyKnife = (new ItemButterflyKnife(BUTTERFLYKNIFE)).setUnlocalizedName("butterflyknife");
      GameRegistry.registerItem(butterflyKnife, "butterflyknife");
      highlandSpirit = (new ItemHighlandSpirit(EFFECTLESS, 2)).setUnlocalizedName("highlandspirit");
      GameRegistry.registerItem(highlandSpirit, "highlandspirit");
      int armorPoints = Math.max(0, Math.min(20, com.voidsrift.riftflux.ModConfig.backpackArmorPoints));
      int durabilityMult;
      if (com.voidsrift.riftflux.ModConfig.backpackDurability) {
         int target = Math.max(1, com.voidsrift.riftflux.ModConfig.backpackDurabilityAmount);
         durabilityMult = Math.max(1, (int)Math.ceil(target / 16.0));
      } else {
         durabilityMult = 0;
      }
      BACKPACK = EnumHelper.addArmorMaterial("backpack", durabilityMult, new int[]{0, armorPoints, 0, 0}, 15);
      backpack = (new ItemBackpack(BACKPACK, 1)).setUnlocalizedName("backpack");
      GameRegistry.registerItem(backpack, "backpack");
      focusBand = (new ItemFocusBand()).setUnlocalizedName("focusband");
      GameRegistry.registerItem(focusBand, "focusband");
      glintRune = (new ItemGlintRune()).setUnlocalizedName("glintrune");
      GameRegistry.registerItem(glintRune, "glintrune");
      gluttonyCharm = (new ItemGluttonyCharm()).setUnlocalizedName("gluttonycharm");
      GameRegistry.registerItem(gluttonyCharm, "gluttonycharm");
      if (Loader.isModLoaded("Thaumcraft")) {
         runeThanatos = (new ItemRuneThanatos()).setUnlocalizedName("runethanatos");
         GameRegistry.registerItem(runeThanatos, "runethanatos");
         toolbelt = (new ItemToolbelt()).setUnlocalizedName("riftflux_toolbelt");
         GameRegistry.registerItem(toolbelt, "toolbelt");
      }

   }

   public static void postInitCompat() {
      // No-op by default.
   }
}
