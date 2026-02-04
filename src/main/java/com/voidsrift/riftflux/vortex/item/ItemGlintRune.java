package com.voidsrift.riftflux.vortex.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class ItemGlintRune extends Item {
   public IIcon[] icons = new IIcon[17];

   public ItemGlintRune() {
      this.setHasSubtypes(true);
      this.setMaxStackSize(16);
      this.setCreativeTab(CreativeTabs.tabMaterials);
   }

   @SideOnly(Side.CLIENT)
   public void registerIcons(IIconRegister p_94581_1_) {
      for(int i = 0; i < 17; ++i) {
         this.icons[i] = p_94581_1_.registerIcon("riftflux:glintrune_" + i);
      }

   }

   @SideOnly(Side.CLIENT)
   public IIcon getIconFromDamage(int par1) {
      return this.icons[par1];
   }

   @SideOnly(Side.CLIENT)
   public void getSubItems(Item item, CreativeTabs p_150895_2_, List list) {
      for(int i = 0; i < 17; ++i) {
         list.add(new ItemStack(item, 1, i));
      }

   }

   public String getUnlocalizedName(ItemStack itemStack) {
      return super.getUnlocalizedName() + "_" + itemStack.getItemDamage();
   }

   public EnumRarity getRarity(ItemStack p_77613_1_) {
      return EnumRarity.epic;
   }
}
