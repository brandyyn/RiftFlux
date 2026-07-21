package gravestone.core.compatibility;

import gravestone.config.GraveStoneConfig;
import gravestone.core.logger.GSLogger;
import java.lang.reflect.Method;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class GSCompatibilityRpgInventory {
   protected static boolean isInstalled = false;

   private GSCompatibilityRpgInventory() {
   }

   public static void addItems(List<ItemStack> items, EntityPlayer player) {
      if (isLoaded() && GraveStoneConfig.storeRpgInventoryItems) {
         try {
            Class<?> clazz = Class.forName("rpgInventory.gui.rpginv.PlayerRpgInventory");
            Method m = clazz.getDeclaredMethod("get", EntityPlayer.class);
            Object result = m.invoke((Object)null, player);
            IInventory inventory = (IInventory)result;
            if (inventory != null) {
               for(int slot = 0; slot < inventory.getSizeInventory(); ++slot) {
                  ItemStack stack = inventory.getStackInSlot(slot);
                  if (stack != null) {
                     items.add(stack.copy());
                     inventory.setInventorySlotContents(slot, (ItemStack)null);
                  }
               }
            }
         } catch (Exception var8) {
            GSLogger.logError("Can't save RpgInventory items!!!");
         }
      }

   }

   public static boolean isLoaded() {
      return isInstalled;
   }
}
