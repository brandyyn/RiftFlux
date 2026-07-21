package gravestone.core.compatibility;

import baubles.api.BaublesApi;
import gravestone.config.GraveStoneConfig;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class GSCompatibilityBaubles {
   protected static boolean isInstalled = false;

   private GSCompatibilityBaubles() {
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
