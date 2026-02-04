package com.voidsrift.riftflux.vortex.item;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import baubles.common.container.InventoryBaubles;
import baubles.common.lib.PlayerHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class ItemFocusBand extends Item implements IBauble {
   public IIcon icon;

   public ItemFocusBand() {
      this.setMaxStackSize(1);
      this.setCreativeTab(CreativeTabs.tabTools);
   }

   @SideOnly(Side.CLIENT)
   public void registerIcons(IIconRegister p_94581_1_) {
      this.icon = p_94581_1_.registerIcon("riftflux:focusband");
   }

   @SideOnly(Side.CLIENT)
   public IIcon getIconFromDamage(int par1) {
      return this.icon;
   }

   public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer) {
      if (!par2World.isRemote) {
         InventoryBaubles baubles = PlayerHandler.getPlayerBaubles(par3EntityPlayer);

         for(int i = 0; i < baubles.getSizeInventory(); ++i) {
            if (baubles.getStackInSlot(i) == null && baubles.isItemValidForSlot(i, par1ItemStack)) {
               baubles.setInventorySlotContents(i, par1ItemStack.copy());
               if (!par3EntityPlayer.capabilities.isCreativeMode) {
                  par3EntityPlayer.inventory.setInventorySlotContents(par3EntityPlayer.inventory.currentItem, (ItemStack)null);
               }

               this.onEquipped(par1ItemStack, par3EntityPlayer);
               break;
            }
         }
      }

      return par1ItemStack;
   }

   public boolean canEquip(ItemStack arg0, EntityLivingBase arg1) {
      return true;
   }

   public boolean canUnequip(ItemStack arg0, EntityLivingBase arg1) {
      return true;
   }

   public BaubleType getBaubleType(ItemStack arg0) {
      return BaubleType.BELT;
   }

   public void onEquipped(ItemStack arg0, EntityLivingBase arg1) {
   }

   public void onUnequipped(ItemStack arg0, EntityLivingBase arg1) {
   }

   public void onWornTick(ItemStack arg0, EntityLivingBase arg1) {
   }

   public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4) {
      list.add("Prevents KO from 4 or more hearts");
   }
}
