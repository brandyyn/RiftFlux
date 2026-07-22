package gravestone.core.compatibility;

import baubles.api.BaublesApi;
import baubles.api.expanded.BaubleExpandedSlots;
import baubles.common.event.EventHandlerNetwork;
import cpw.mods.fml.common.registry.GameRegistry;
import gravestone.config.GraveStoneConfig;
import gravestone.core.GraveStoneDeathInventory;
import java.util.List;
import makamys.satchels.EntityPropertiesSatchels;
import com.voidsrift.riftflux.vortex.lib.container.InventoryBackpack;
import com.voidsrift.riftflux.vortex.lib.helper.ContainerHelper;
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
         if (stack != null && shouldKeepBauble(slot, stack)) {
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

   public static void syncAfterRespawn(EntityPlayer player) {
      if (isInstalled() && player != null) {
         EventHandlerNetwork.syncBaubles(player);
      }
   }

   private static boolean shouldKeepBauble(int slot, ItemStack stack) {
      if (GraveStoneDeathInventory.matchesConfiguredItem(stack, GraveStoneConfig.keepBaubleItemBlacklist)) {
         return false;
      }

      String slotType = BaubleExpandedSlots.getSlotType(slot);
      if (matchesSlotList(GraveStoneConfig.keepBaubleSlotBlacklist, slot, slotType)) {
         return false;
      }
      if (GraveStoneDeathInventory.isWhitelistedItem(stack)) {
         return true;
      }
      if (!GraveStoneConfig.keepBaublesOnDeath) {
         return false;
      }
      if (GraveStoneConfig.keepBaubleSlotWhitelist != null
            && GraveStoneConfig.keepBaubleSlotWhitelist.length > 0
            && !matchesSlotList(GraveStoneConfig.keepBaubleSlotWhitelist, slot, slotType)) {
         return false;
      }
      return GraveStoneConfig.keepBaubleItemWhitelist == null
            || GraveStoneConfig.keepBaubleItemWhitelist.length == 0
            || GraveStoneDeathInventory.matchesConfiguredItem(stack, GraveStoneConfig.keepBaubleItemWhitelist);
   }

   public static void extractCarrierContentsForDeath(EntityPlayer player) {
      if (!isInstalled()) {
         return;
      }
      IInventory inventory = BaublesApi.getBaubles(player);
      if (inventory == null) {
         return;
      }
      EntityPropertiesSatchels satchels = EntityPropertiesSatchels.fromPlayer(player);
      for (int slot = 0; slot < inventory.getSizeInventory(); ++slot) {
         ItemStack carrier = inventory.getStackInSlot(slot);
         if (carrier == null) {
            continue;
         }
         String registryName = getRegistryName(carrier);
         List<ItemStack> extracted = null;
         if ("riftflux:satchel".equalsIgnoreCase(registryName)
               && !GraveStoneConfig.keepSatchelContentsOnDeath && satchels != null) {
            extracted = satchels.extractStoredContents(carrier);
         } else if ("riftflux:pouch".equalsIgnoreCase(registryName)
               && !GraveStoneConfig.keepPouchContentsOnDeath && satchels != null) {
            extracted = satchels.extractStoredContents(carrier);
         } else if ("riftflux:backpack".equalsIgnoreCase(registryName)
               && !GraveStoneConfig.keepBackpackContentsOnDeath) {
            extracted = extractBackpackContents(carrier);
         }
         if (extracted != null) {
            for (ItemStack stack : extracted) {
               GraveStoneDeathInventory.addExtractedDeathItem(player, stack);
            }
         }
      }
   }

   private static List<ItemStack> extractBackpackContents(ItemStack backpack) {
      List<ItemStack> extracted = new java.util.ArrayList<ItemStack>();
      InventoryBackpack inventory = ContainerHelper.getBackpackInventory(backpack);
      for (int slot = 0; slot < inventory.getSizeInventory(); ++slot) {
         ItemStack stack = inventory.getStackInSlot(slot);
         if (stack != null) {
            extracted.add(stack);
            inventory.setInventorySlotContents(slot, null);
         }
      }
      return extracted;
   }

   private static String getRegistryName(ItemStack stack) {
      GameRegistry.UniqueIdentifier identifier = GameRegistry.findUniqueIdentifierFor(stack.getItem());
      return identifier == null ? "" : identifier.toString();
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
