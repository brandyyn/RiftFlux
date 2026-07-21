package gravestone.core;

import cpw.mods.fml.common.registry.GameRegistry;
import gravestone.config.GraveStoneConfig;
import gravestone.core.compatibility.GSCompatibilityBaubles;
import gravestone.core.compatibility.GSCompatibilityTravellersGear;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public final class GraveStoneDeathInventory {
   private static final String DATA_KEY = "RiftFluxGravestoneKeptInventory";
   private static final String ITEMS_KEY = "Items";
   private static final byte INVENTORY_MAIN = 0;
   private static final byte INVENTORY_ARMOR = 1;
   private static final byte INVENTORY_BAUBLES = 2;
   private static final byte INVENTORY_TRAVELLERS_GEAR = 3;

   private GraveStoneDeathInventory() {
   }

   public static void captureConfiguredItems(EntityPlayer player) {
      if (player == null || player.worldObj == null || player.worldObj.isRemote
            || player.worldObj.getGameRules().getGameRuleBooleanValue("keepInventory")) {
         return;
      }

      player.getEntityData().removeTag(DATA_KEY);
      NBTTagList keptItems = new NBTTagList();

      for(int slot = 0; slot < player.inventory.mainInventory.length; ++slot) {
         ItemStack stack = player.inventory.mainInventory[slot];
         if (stack != null && ((GraveStoneConfig.keepHotbarOnDeath && slot < 9) || isWhitelistedItem(stack))) {
            addKeptItem(keptItems, INVENTORY_MAIN, slot, stack);
            player.inventory.mainInventory[slot] = null;
         }
      }

      for(int slot = 0; slot < player.inventory.armorInventory.length; ++slot) {
         ItemStack stack = player.inventory.armorInventory[slot];
         if (stack != null && (GraveStoneConfig.keepArmorOnDeath || isWhitelistedItem(stack))) {
            addKeptItem(keptItems, INVENTORY_ARMOR, slot, stack);
            player.inventory.armorInventory[slot] = null;
         }
      }

      GSCompatibilityBaubles.captureKeptItems(player, keptItems);
      GSCompatibilityTravellersGear.captureKeptItems(player, keptItems);
      if (keptItems.tagCount() > 0) {
         NBTTagCompound keptData = new NBTTagCompound();
         keptData.setTag(ITEMS_KEY, keptItems);
         player.getEntityData().setTag(DATA_KEY, keptData);
         player.inventory.markDirty();
      }
   }

   public static void restoreConfiguredItems(EntityPlayer original, EntityPlayer player) {
      if (original == null || player == null || player.worldObj == null || player.worldObj.isRemote
            || !original.getEntityData().hasKey(DATA_KEY, 10)) {
         return;
      }

      NBTTagList keptItems = original.getEntityData().getCompoundTag(DATA_KEY).getTagList(ITEMS_KEY, 10);
      for(int i = 0; i < keptItems.tagCount(); ++i) {
         NBTTagCompound entry = keptItems.getCompoundTagAt(i);
         ItemStack stack = ItemStack.loadItemStackFromNBT(entry.getCompoundTag("Stack"));
         if (stack == null) {
            continue;
         }

         byte inventory = entry.getByte("Inventory");
         int slot = entry.getInteger("Slot");
         boolean restored = inventory == INVENTORY_MAIN && restoreMainSlot(player, slot, stack)
               || inventory == INVENTORY_ARMOR && restoreArmorSlot(player, slot, stack)
               || inventory == INVENTORY_BAUBLES && GSCompatibilityBaubles.restoreKeptItem(player, slot, stack)
               || inventory == INVENTORY_TRAVELLERS_GEAR
                     && GSCompatibilityTravellersGear.restoreKeptItem(player, slot, stack);
         if (!restored) {
            restoreToAvailableInventorySlot(player, stack);
         }
      }

      original.getEntityData().removeTag(DATA_KEY);
      player.getEntityData().removeTag(DATA_KEY);
      player.inventory.markDirty();
   }

   public static void addKeptBauble(NBTTagList keptItems, int slot, ItemStack stack) {
      addKeptItem(keptItems, INVENTORY_BAUBLES, slot, stack);
   }

   public static void addKeptTravellersGear(NBTTagList keptItems, int slot, ItemStack stack) {
      addKeptItem(keptItems, INVENTORY_TRAVELLERS_GEAR, slot, stack);
   }

   public static boolean isWhitelistedItem(ItemStack stack) {
      return matchesConfiguredItem(stack, GraveStoneConfig.keepItemsOnDeathWhitelist);
   }

   public static boolean matchesConfiguredItem(ItemStack stack, String[] configuredItems) {
      if (stack == null || configuredItems == null || configuredItems.length == 0) {
         return false;
      }

      GameRegistry.UniqueIdentifier identifier = GameRegistry.findUniqueIdentifierFor(stack.getItem());
      if (identifier == null) {
         return false;
      }

      String registryName = identifier.toString();
      for(String configured : configuredItems) {
         if (matchesItem(configured, registryName, stack.getItemDamage())) {
            return true;
         }
      }
      return false;
   }

   private static boolean matchesItem(String configured, String registryName, int metadata) {
      if (configured == null) {
         return false;
      }

      String entry = configured.trim();
      if (entry.equalsIgnoreCase(registryName)) {
         return true;
      }

      int metadataSeparator = entry.lastIndexOf(':');
      if (metadataSeparator <= entry.indexOf(':')) {
         return false;
      }

      try {
         return entry.substring(0, metadataSeparator).equalsIgnoreCase(registryName)
               && Integer.parseInt(entry.substring(metadataSeparator + 1)) == metadata;
      } catch (NumberFormatException ignored) {
         return false;
      }
   }

   private static void addKeptItem(NBTTagList keptItems, byte inventory, int slot, ItemStack stack) {
      NBTTagCompound entry = new NBTTagCompound();
      NBTTagCompound stackData = new NBTTagCompound();
      stack.writeToNBT(stackData);
      entry.setByte("Inventory", inventory);
      entry.setInteger("Slot", slot);
      entry.setTag("Stack", stackData);
      keptItems.appendTag(entry);
   }

   private static boolean restoreMainSlot(EntityPlayer player, int slot, ItemStack stack) {
      if (slot < 0 || slot >= player.inventory.mainInventory.length || player.inventory.mainInventory[slot] != null) {
         return false;
      }
      player.inventory.mainInventory[slot] = stack;
      return true;
   }

   private static boolean restoreArmorSlot(EntityPlayer player, int slot, ItemStack stack) {
      if (slot < 0 || slot >= player.inventory.armorInventory.length || player.inventory.armorInventory[slot] != null) {
         return false;
      }
      player.inventory.armorInventory[slot] = stack;
      return true;
   }

   private static void restoreToAvailableInventorySlot(EntityPlayer player, ItemStack stack) {
      if (!player.inventory.addItemStackToInventory(stack) && stack.stackSize > 0) {
         player.dropPlayerItemWithRandomChoice(stack, false);
      }
   }
}
