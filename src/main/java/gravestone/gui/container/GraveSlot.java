package gravestone.gui.container;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class GraveSlot extends Slot {
   public GraveSlot(IInventory inventory, int slotNum, int xPos, int yPos) {
      super(inventory, slotNum, xPos, yPos);
   }

   public int getSlotStackLimit() {
      return 0;
   }

   public boolean isItemValid(ItemStack stack) {
      return false;
   }
}
