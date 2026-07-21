package gravestone.core.compatibility;

import gravestone.config.GraveStoneConfig;
import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import twilightforest.item.TFItems;

public class GSCompatibilityTwilightForest {
   protected static boolean isInstalled = false;

   private GSCompatibilityTwilightForest() {
   }

   public static boolean handleCharmsOfKeeping(List<ItemStack> items, EntityPlayer player) {
      if (isInstalled() && GraveStoneConfig.enableTwilightForestKeeping) {
         int[] keepingData = checkForCharmOfKeeping(player);
         if (keepingData[0] > 0) {
            Iterator<ItemStack> it = items.iterator();

            while(it.hasNext()) {
               ItemStack item = it.next();
               if (item != null && item.hasTagCompound()) {
                  byte slot = item.getTagCompound().getByte("slot");
                  if (keepingData[0] == 1) {
                     if (slot == player.inventory.currentItem) {
                        it.remove();
                        break;
                     }
                  } else if (keepingData[0] == 2) {
                     if (slot < 9) {
                        it.remove();
                     }
                  } else if (keepingData[0] == 3 && slot < 40) {
                     it.remove();
                  }
               }
            }

            for(ItemStack item : items) {
               if (item != null && item.hasTagCompound()) {
                  byte slot = item.getTagCompound().getByte("slot");
                  if (slot == keepingData[1]) {
                     player.inventory.setInventorySlotContents(slot, item.splitStack(1));
                     if (item.stackSize <= 0) {
                        item = null;
                     }
                  } else {
                     player.inventory.setInventorySlotContents(slot, (ItemStack)null);
                  }
               }
            }

            return true;
         }
      }

      return false;
   }

   public static void addSlotTags(List<ItemStack> items) {
      if (isInstalled() && GraveStoneConfig.enableTwilightForestKeeping) {
         for(int i = 0; i < items.size(); ++i) {
            ItemStack item = items.get(i);
            if (item != null) {
               NBTTagCompound nbt = item.getTagCompound();
               if (nbt == null) {
                  nbt = new NBTTagCompound();
                  if (nbt.hasKey("slot")) {
                     continue;
                  }
               }

               nbt.setByte("slot", (byte)i);
               item.setTagCompound(nbt);
            }
         }
      }

   }

   public static void removeSlotTags(List<ItemStack> items) {
      if (isInstalled() && GraveStoneConfig.enableTwilightForestKeeping && items.size() > 0) {
         for(int i = 0; i < items.size(); ++i) {
            ItemStack item = items.get(i);
            if (item != null && item.hasTagCompound()) {
               NBTTagCompound nbt = item.getTagCompound();
               nbt.removeTag("slot");
               if (nbt.hasNoTags()) {
                  item.setTagCompound((NBTTagCompound)null);
               }
            }
         }
      }

   }

   private static int[] checkForCharmOfKeeping(EntityPlayer player) {
      byte max = 0;
      int slot = -1;

      for(int i = 0; i < player.inventory.mainInventory.length; ++i) {
         ItemStack stack = player.inventory.mainInventory[i];
         if (stack != null) {
            if (stack.getItem() == TFItems.charmOfKeeping1) {
               if (max < 1) {
                  max = 1;
                  slot = i;
               }
            } else if (stack.getItem() == TFItems.charmOfKeeping2) {
               if (max < 2) {
                  max = 2;
                  slot = i;
               }
            } else if (stack.getItem() == TFItems.charmOfKeeping3 && max < 3) {
               max = 3;
               slot = i;
            }
         }
      }

      return new int[]{max, slot};
   }

   public static boolean isInstalled() {
      return isInstalled;
   }
}
