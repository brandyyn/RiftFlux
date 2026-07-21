package gravestone.gui.container;

import gravestone.inventory.GraveInventory;
import gravestone.tileentity.TileEntityGSGraveStone;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class GraveContainer extends Container {
   public static final int PLAYER_INVENTORY_ROWS_COUNT = 3;
   public static final int ROWS_COUNT = 6;
   public static final int COLUMNS_COUNT = 9;
   public static final int SLOT_WIDTH = 18;
   private GraveInventory graveInventory;

   public GraveContainer(InventoryPlayer inventoryPlayer, TileEntityGSGraveStone te) {
      int i = 36;
      this.graveInventory = te.getInventory();

      for(int row = 0; row < 6; ++row) {
         for(int column = 0; column < 9; ++column) {
            this.addSlotToContainer(new GraveSlot(this.graveInventory, column + row * 9, 8 + column * 18, 18 + row * 18));
         }
      }

      for(int row = 0; row < 3; ++row) {
         for(int column = 0; column < 9; ++column) {
            this.addSlotToContainer(new Slot(inventoryPlayer, column + row * 9 + 9, 8 + column * 18, 103 + row * 18 + i));
         }
      }

      for(int column = 0; column < 9; ++column) {
         this.addSlotToContainer(new Slot(inventoryPlayer, column, 8 + column * 18, 161 + i));
      }

   }

   public boolean canInteractWith(EntityPlayer player) {
      return true;
   }

   public ItemStack transferStackInSlot(EntityPlayer player, int slot) {
      ItemStack stack = null;
      Slot slotObject = (Slot)this.inventorySlots.get(slot);
      if (slotObject != null && slotObject.getHasStack()) {
         ItemStack stackInSlot = slotObject.getStack();
         stack = stackInSlot.copy();
         if (slot >= this.graveInventory.getSizeInventoryForGui()) {
            return null;
         }

         if (!this.mergeItemStack(stackInSlot, this.graveInventory.getSizeInventoryForGui(), 36 + this.graveInventory.getSizeInventoryForGui(), true)) {
            return null;
         }

         if (stackInSlot.stackSize == 0) {
            slotObject.putStack((ItemStack)null);
         } else {
            slotObject.onSlotChanged();
         }

         if (stackInSlot.stackSize == stack.stackSize) {
            return null;
         }

         slotObject.onPickupFromSlot(player, stackInSlot);
      }

      return stack;
   }
}
