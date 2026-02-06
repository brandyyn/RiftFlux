package com.voidsrift.riftflux.vortex.lib.helper;

import com.google.common.base.CharMatcher;
import com.google.common.collect.MapMaker;
import cpw.mods.fml.common.registry.GameData;
import java.util.Map;
import baubles.api.BaublesApi;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemHelper {

   public static boolean hasArmor(EntityPlayer player, Item armor, int slot) {
      ItemStack equip = player.getCurrentArmor(slot);
      return equip != null && equip.getItem() == armor;
   }

   public static boolean hasBauble(EntityPlayer player, Item bauble) {
      if (player == null || bauble == null) {
         return false;
      }
      return findBaubleSlot(player, bauble) >= 0;
   }

   public static int findBaubleSlot(EntityPlayer player, Item bauble) {
      if (player == null || bauble == null) {
         return -1;
      }
      try {
         IInventory binventory = BaublesApi.getBaubles(player);
         if (binventory == null) return -1;
         for (int i = 0; i < binventory.getSizeInventory(); ++i) {
            ItemStack stack = binventory.getStackInSlot(i);
            if (stack != null && stack.getItem() == bauble) {
               return i;
            }
         }
      } catch (Throwable ignored) {
      }
      return -1;
   }

   /** Try to consume one instance of the given item from Baubles. Returns true if consumed. */
   public static boolean consumeBauble(EntityPlayer player, Item bauble) {
      if (player == null || bauble == null) {
         return false;
      }
      try {
         IInventory binventory = BaublesApi.getBaubles(player);
         if (binventory == null) return false;
         for (int i = 0; i < binventory.getSizeInventory(); ++i) {
            ItemStack stack = binventory.getStackInSlot(i);
            if (stack != null && stack.getItem() == bauble) {
               stack.stackSize--;
               if (stack.stackSize <= 0) {
                  binventory.setInventorySlotContents(i, (ItemStack) null);
               }
               binventory.markDirty();
               return true;
            }
         }
      } catch (Throwable ignored) {
      }
      return false;
   }

   public static ItemStack getItemStackfromName(String name, int stackSize) {
      int meta = 0;
      String[] segment;
      if (CharMatcher.is('/').countIn(name) == 1) {
         segment = name.split("/");
         name = segment[0];
      }

      if (CharMatcher.is(':').countIn(name) == 2) {
         segment = name.split(":");
         name = segment[0] + ":" + segment[1];
         meta = Integer.decode(segment[2]);
      }

      Item item = (Item)GameData.getItemRegistry().getObject(name);
      return item != null ? new ItemStack(item, stackSize, meta) : null;
   }

   public static ItemStack getItemStackfromName(String name) {
      return getItemStackfromName(name, 1);
   }

   public static boolean areItemStacksEqualStackSizeUnaware(ItemStack stack0, ItemStack stack1) {
      if (stack0 == null && stack1 != null) {
         return false;
      } else if (stack0 != null && stack1 == null) {
         return false;
      } else if (stack0 == null && stack1 == null) {
         return true;
      } else {
         return stack0.getItem() != stack1.getItem() ? false : (stack0.getItemDamage() != stack1.getItemDamage() ? false : (stack0.stackTagCompound == null && stack1.stackTagCompound != null ? false : stack0.stackTagCompound == null || stack0.getTagCompound().equals(stack1.getTagCompound())));
      }
   }
}
