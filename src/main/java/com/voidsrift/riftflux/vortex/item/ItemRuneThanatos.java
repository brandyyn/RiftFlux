package com.voidsrift.riftflux.vortex.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class ItemRuneThanatos extends Item {
   public IIcon icon;

   public ItemRuneThanatos() {
      this.setMaxStackSize(1);
      this.setCreativeTab(CreativeTabs.tabTools);
   }

   @SideOnly(Side.CLIENT)
   public void registerIcons(IIconRegister p_94581_1_) {
      this.itemIcon = p_94581_1_.registerIcon("riftflux:runethanatos");
      this.icon = this.itemIcon;
   }

   @SideOnly(Side.CLIENT)
   public IIcon getIconFromDamage(int par1) {
      return this.icon != null ? this.icon : this.itemIcon;
   }

   public EnumRarity getRarity(ItemStack p_77613_1_) {
      return EnumRarity.rare;
   }

    @Override
    @cpw.mods.fml.relauncher.SideOnly(cpw.mods.fml.relauncher.Side.CLIENT)
    public void addInformation(ItemStack stack, net.minecraft.entity.player.EntityPlayer player, java.util.List list, boolean advanced) {
        list.add(net.minecraft.util.EnumChatFormatting.LIGHT_PURPLE + "Helps to keep you anchored in the mortal realm...");
    }

}
