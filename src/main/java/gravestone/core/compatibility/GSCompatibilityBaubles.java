package gravestone.core.compatibility;

import baubles.api.BaublesApi;
import baubles.api.expanded.BaubleExpandedSlots;
import gravestone.config.GraveStoneConfig;
import gravestone.core.GraveStoneDeathInventory;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;

public class GSCompatibilityBaubles {
   protected static boolean isInstalled = false;

   private GSCompatibilityBaubles() {
   }

   public static void captureKeptItems(EntityPlayer player, NBTTagList keptItems) {
      if (!isInstalled()) {
         return;
      }

      IInventory inventory = BaublesApi.getBaubles(player);
      if (inventory == null) {
         return;
      }

      for(int slot = 0; slot < inventory.getSizeInventory(); ++slot) {
         ItemStack stack = inventory.getStackInSlot(slot);
         if (stack != null && (shouldKeepBaubleSlot(slot) || GraveStoneDeathInventory.isWhitelistedItem(stack))) {
            GraveStoneDeathInventory.addKeptBauble(keptItems, slot, stack);
            inventory.setInventorySlotContents(slot, (ItemStack)null);
         }
      }
      inventory.markDirty();
   }

   public static boolean restoreKeptItem(EntityPlayer player, int slot, ItemStack stack) {
      if (!isInstalled()) {
         return false;
      }

      IInventory inventory = BaublesApi.getBaubles(player);
      if (inventory == null || slot < 0 || slot >= inventory.getSizeInventory() || inventory.getStackInSlot(slot) != null) {
         return false;
      }
      inventory.setInventorySlotContents(slot, stack);
      inventory.markDirty();
      return true;
   }

   private static boolean shouldKeepBaubleSlot(int slot) {
      if (!GraveStoneConfig.keepBaublesOnDeath) {
         return false;
      }

      String slotType = BaubleExpandedSlots.getSlotType(slot);
      if (matchesSlotList(GraveStoneConfig.keepBaubleSlotBlacklist, slot, slotType)) {
         return false;
      }
      return GraveStoneConfig.keepBaubleSlotWhitelist == null
            || GraveStoneConfig.keepBaubleSlotWhitelist.length == 0
            || matchesSlotList(GraveStoneConfig.keepBaubleSlotWhitelist, slot, slotType);
   }

   private static boolean matchesSlotList(String[] entries, int slot, String slotType) {
      if (entries == null) {
         return false;
      }

      for(String configured : entries) {
         if (configured == null) {
            continue;
         }
         String entry = configured.trim();
         if (entry.equalsIgnoreCase(slotType) || entry.equals(Integer.toString(slot))
               || entry.equalsIgnoreCase("slot:" + slot)) {
            return true;
         }
      }
      return false;
   }

   public static void addItems(List<ItemStack> items, EntityPlayer player) {
      if (isInstalled() && GraveStoneConfig.storeBaublesItems) {
         IInventory inventory = BaublesApi.getBaubles(player);
         if (inventory != null) {
            for(int slot = 0; slot < inventory.getSizeInventory(); ++slot) {
               ItemStack stack = inventory.getStackInSlot(slot);
               if (stack != null) {
                  items.add(stack.copy());
                  inventory.setInventorySlotContents(slot, (ItemStack)null);
               }
            }
         }
      }

   }

   public static boolean isInstalled() {
      return isInstalled;
   }
}
