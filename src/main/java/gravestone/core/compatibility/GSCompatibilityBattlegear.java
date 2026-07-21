package gravestone.core.compatibility;

import gravestone.config.GraveStoneConfig;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class GSCompatibilityBattlegear {
   private static final short FIRST_SLOT = 150;
   private static final short LAST_SLOT = 155;
   protected static boolean isInstalled = false;

   private GSCompatibilityBattlegear() {
   }

   public static void addItems(List<ItemStack> items, EntityPlayer player) {
      if (isInstalled() && GraveStoneConfig.storeBattlegearItems) {
         for(int slot = 150; slot <= 155; ++slot) {
            items.add(player.inventory.getStackInSlot(slot));
            player.inventory.setInventorySlotContents(slot, (ItemStack)null);
         }
      }

   }

   public static boolean isInstalled() {
      return isInstalled;
   }
}
