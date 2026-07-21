package gravestone.core.compatibility;

import gravestone.config.GraveStoneConfig;
import gravestone.core.GraveStoneDeathInventory;
import gravestone.core.logger.GSLogger;
import java.lang.reflect.Method;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;

public final class GSCompatibilityTravellersGear {
   protected static boolean isInstalled;

   private GSCompatibilityTravellersGear() {
   }

   public static void captureKeptItems(EntityPlayer player, NBTTagList keptItems) {
      if (!isInstalled) {
         return;
      }

      try {
         ItemStack[] inventory = getInventory(player);
         if (inventory == null) {
            return;
         }

         boolean changed = false;
         for(int slot = 0; slot < inventory.length; ++slot) {
            ItemStack stack = inventory[slot];
            if (stack != null && shouldKeepItem(stack)) {
               GraveStoneDeathInventory.addKeptTravellersGear(keptItems, slot, stack);
               inventory[slot] = null;
               changed = true;
            }
         }
         if (changed) {
            setInventory(player, inventory);
         }
      } catch (Exception exception) {
         GSLogger.logError("Can't keep Traveller's Gear items after death: " + exception);
      }
   }

   public static boolean restoreKeptItem(EntityPlayer player, int slot, ItemStack stack) {
      if (!isInstalled) {
         return false;
      }

      try {
         ItemStack[] inventory = getInventory(player);
         if (inventory == null || slot < 0 || slot >= inventory.length || inventory[slot] != null) {
            return false;
         }
         inventory[slot] = stack;
         setInventory(player, inventory);
         return true;
      } catch (Exception exception) {
         GSLogger.logError("Can't restore Traveller's Gear item after death: " + exception);
         return false;
      }
   }

   private static boolean shouldKeepItem(ItemStack stack) {
      if (GraveStoneDeathInventory.matchesConfiguredItem(stack,
            GraveStoneConfig.keepTravellersGearItemBlacklist)) {
         return false;
      }
      if (GraveStoneDeathInventory.isWhitelistedItem(stack)) {
         return true;
      }
      return GraveStoneConfig.keepTravellersGearOnDeath
            && (GraveStoneConfig.keepTravellersGearItemWhitelist == null
                  || GraveStoneConfig.keepTravellersGearItemWhitelist.length == 0
                  || GraveStoneDeathInventory.matchesConfiguredItem(stack,
                        GraveStoneConfig.keepTravellersGearItemWhitelist));
   }

   public static void addItems(List<ItemStack> items, EntityPlayer player) {
      if (!isInstalled || !GraveStoneConfig.storeTravellersGearItems) {
         return;
      }

      try {
         ItemStack[] inventory = getInventory(player);
         if (inventory == null) {
            return;
         }

         for (int slot = 0; slot < inventory.length; ++slot) {
            if (inventory[slot] != null) {
               items.add(inventory[slot].copy());
               inventory[slot] = null;
            }
         }
         setInventory(player, inventory);
      } catch (Exception exception) {
         GSLogger.logError("Can't save Traveller's Gear items: " + exception);
      }
   }

   private static ItemStack[] getInventory(EntityPlayer player) throws Exception {
      Class<?> api = Class.forName("travellersgear.api.TravellersGearAPI");
      Method method = api.getMethod("getExtendedInventory", EntityPlayer.class);
      return (ItemStack[]) method.invoke(null, player);
   }

   private static void setInventory(EntityPlayer player, ItemStack[] inventory) throws Exception {
      Class<?> api = Class.forName("travellersgear.api.TravellersGearAPI");
      Method method = api.getMethod("setExtendedInventory", EntityPlayer.class, ItemStack[].class);
      method.invoke(null, player, (Object) inventory);
   }
}
