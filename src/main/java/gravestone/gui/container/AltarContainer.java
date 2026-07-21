package gravestone.gui.container;

import gravestone.item.corpse.CorpseHelper;
import gravestone.tileentity.TileEntityGSAltar;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class AltarContainer extends Container {
   protected TileEntityGSAltar tileEntity;

   public AltarContainer(InventoryPlayer inventoryPlayer, TileEntityGSAltar te) {
      this.tileEntity = te;
      this.addSlotToContainer(new AltarSlot(this.tileEntity, 0, 27, 27));

      for(int row = 0; row < 3; ++row) {
         for(int column = 0; column < 9; ++column) {
            this.addSlotToContainer(new Slot(inventoryPlayer, column + row * 9 + 9, 8 + column * 18, 84 + row * 18));
         }
      }

      for(int column = 0; column < 9; ++column) {
         this.addSlotToContainer(new Slot(inventoryPlayer, column, 8 + column * 18, 142));
      }

   }

   public boolean canInteractWith(EntityPlayer player) {
      return this.tileEntity.isUseableByPlayer(player);
   }

   public ItemStack transferStackInSlot(EntityPlayer player, int slot) {
      ItemStack stack = null;
      Slot slotObject = (Slot)this.inventorySlots.get(slot);
      if (slotObject != null && slotObject.getHasStack()) {
         ItemStack stackInSlot = slotObject.getStack();
         stack = stackInSlot.copy();
         if (slot == 0) {
            if (!this.mergeItemStack(stackInSlot, 0, this.inventorySlots.size(), true)) {
               return null;
            }
         } else {
            if (((Slot)this.inventorySlots.get(0)).getHasStack() || !((AltarSlot)this.inventorySlots.get(0)).isItemValid(stackInSlot)) {
               return null;
            }

            if (stackInSlot.hasTagCompound() && stackInSlot.stackSize == 1) {
               ((Slot)this.inventorySlots.get(0)).putStack(stackInSlot.copy());
               stackInSlot.stackSize = 0;
            } else if (stackInSlot.stackSize >= 1) {
               ItemStack newStack = new ItemStack(stackInSlot.getItem(), 1, stackInSlot.getItemDamage());
               if (stackInSlot.hasTagCompound()) {
                  newStack.setTagCompound((NBTTagCompound)stackInSlot.getTagCompound().copy());
               }

               ((Slot)this.inventorySlots.get(0)).putStack(newStack);
               --stackInSlot.stackSize;
            }
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

   public int getResurrectionLevel() {
      return CorpseHelper.getRequiredLevel(this.tileEntity.getCorpse());
   }
}
