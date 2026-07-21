package gravestone.gui.container;

import gravestone.item.ItemGSCorpse;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;

public class AltarSlot extends Slot {
   public AltarSlot(IInventory inventory, int slotNum, int xPos, int yPos) {
      super(inventory, slotNum, xPos, yPos);
   }

   public boolean isItemValid(ItemStack stack) {
      return stack.getItem() instanceof ItemGSCorpse || stack.getItem() instanceof ItemTool || stack.getItem() instanceof ItemArmor || stack.getItem() instanceof ItemSword;
   }

   public int getSlotStackLimit() {
      return 1;
   }

   public void onSlotChange(ItemStack p_75220_1_, ItemStack p_75220_2_) {
      super.onSlotChange(p_75220_1_, p_75220_2_);
   }
}
