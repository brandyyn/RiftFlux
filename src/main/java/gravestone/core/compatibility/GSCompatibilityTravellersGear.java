package gravestone.core.compatibility;

import gravestone.config.GraveStoneConfig;
import gravestone.core.logger.GSLogger;
import java.lang.reflect.Method;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public final class GSCompatibilityTravellersGear {
   protected static boolean isInstalled;

   private GSCompatibilityTravellersGear() {
   }

   public static void addItems(List<ItemStack> items, EntityPlayer player) {
      if (!isInstalled || !GraveStoneConfig.storeTravellersGearItems) {
         return;
      }

      try {
         Class<?> api = Class.forName("travellersgear.api.TravellersGearAPI");
         Method getInventory = api.getMethod("getExtendedInventory", EntityPlayer.class);
         Method setInventory = api.getMethod("setExtendedInventory", EntityPlayer.class, ItemStack[].class);
         ItemStack[] inventory = (ItemStack[]) getInventory.invoke(null, player);
         if (inventory == null) {
            return;
         }

         for (int slot = 0; slot < inventory.length; ++slot) {
            if (inventory[slot] != null) {
               items.add(inventory[slot].copy());
               inventory[slot] = null;
            }
         }
         setInventory.invoke(null, player, (Object) inventory);
      } catch (Exception exception) {
         GSLogger.logError("Can't save Traveller's Gear items: " + exception);
      }
   }
}
