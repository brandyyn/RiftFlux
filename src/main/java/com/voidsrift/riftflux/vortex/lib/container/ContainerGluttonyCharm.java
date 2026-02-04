package com.voidsrift.riftflux.vortex.lib.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;

public class ContainerGluttonyCharm extends Container {
   private final InventoryGluttonyCharm inventoryGluttonyCharm;
   private static final int invStart = 1;
   private static final int invEnd = 27;
   private static final int hotbarStart = 28;
   private static final int hotbarEnd = 36;

   public ContainerGluttonyCharm(EntityPlayer player) {
      this.inventoryGluttonyCharm = new InventoryGluttonyCharm(player.getHeldItem());
      int xoff = 81;

      int i;
      for(i = 0; i < 1; ++i) {
         this.addSlotToContainer(new Slot(this.inventoryGluttonyCharm, i, xoff + i * 18 - 1, 18) {
            public boolean isItemValid(ItemStack stack) {
               return stack.getItem() instanceof ItemFood;
            }
         });
      }

      for(i = 0; i < 3; ++i) {
         for(int j = 0; j < 9; ++j) {
            this.addSlotToContainer(new Slot(player.inventory, j + i * 9 + 9, 8 + j * 18, 49 + i * 18));
         }
      }

      for(i = 0; i < 9; ++i) {
         this.addSlotToContainer(new Slot(player.inventory, i, 8 + i * 18, 107));
      }

   }

   public boolean canInteractWith(EntityPlayer p_75145_1_) {
      return true;
   }

   public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int index) {
      ItemStack itemstack = null;
      Slot slot = (Slot)this.inventorySlots.get(index);
      if (slot != null && slot.getHasStack()) {
         ItemStack itemstack1 = slot.getStack();
         itemstack = itemstack1.copy();
         if (index < 1) {
            if (!this.mergeItemStack(itemstack1, 1, 37, true)) {
               return null;
            }

            slot.onSlotChange(itemstack1, itemstack);
         } else if (index >= 1 && !this.mergeItemStack(itemstack1, 0, 1, false)) {
            return null;
         }

         if (itemstack1.stackSize == 0) {
            slot.putStack((ItemStack)null);
         } else {
            slot.onSlotChanged();
         }

         if (itemstack1.stackSize == itemstack.stackSize) {
            return null;
         }

         slot.onPickupFromSlot(par1EntityPlayer, itemstack1);
      }

      return itemstack;
   }

   public ItemStack slotClick(int slot, int button, int flag, EntityPlayer player) {
      return slot >= 0 && this.getSlot(slot) != null && this.getSlot(slot).getStack() == player.getHeldItem() ? null : super.slotClick(slot, button, flag, player);
   }

   protected boolean mergeItemStack(ItemStack stack, int start, int end, boolean backwards) {
      boolean flag1 = false;
      int k = backwards ? end - 1 : start;
      Slot slot;
      ItemStack itemstack1;
      int l;
      if (stack.isStackable()) {
         label116:
         while(true) {
            while(true) {
               if (stack.stackSize <= 0 || (backwards || k >= end) && (!backwards || k < start)) {
                  break label116;
               }

               slot = (Slot)this.inventorySlots.get(k);
               itemstack1 = slot.getStack();
               if (!slot.isItemValid(stack)) {
                  k += backwards ? -1 : 1;
               } else {
                  if (itemstack1 != null && itemstack1.getItem() == stack.getItem() && (!stack.getHasSubtypes() || stack.getItemDamage() == itemstack1.getItemDamage()) && ItemStack.areItemStackTagsEqual(stack, itemstack1)) {
                     l = itemstack1.stackSize + stack.stackSize;
                     if (l <= stack.getMaxStackSize() && l <= slot.getSlotStackLimit()) {
                        stack.stackSize = 0;
                        itemstack1.stackSize = l;
                        this.inventoryGluttonyCharm.markDirty();
                        flag1 = true;
                     } else if (itemstack1.stackSize < stack.getMaxStackSize() && l < slot.getSlotStackLimit()) {
                        stack.stackSize -= stack.getMaxStackSize() - itemstack1.stackSize;
                        itemstack1.stackSize = stack.getMaxStackSize();
                        this.inventoryGluttonyCharm.markDirty();
                        flag1 = true;
                     }
                  }

                  k += backwards ? -1 : 1;
               }
            }
         }
      }

      if (stack.stackSize > 0) {
         k = backwards ? end - 1 : start;

         while(!backwards && k < end || backwards && k >= start) {
            slot = (Slot)this.inventorySlots.get(k);
            itemstack1 = slot.getStack();
            if (!slot.isItemValid(stack)) {
               k += backwards ? -1 : 1;
            } else {
               if (itemstack1 == null) {
                  l = stack.stackSize;
                  if (l <= slot.getSlotStackLimit()) {
                     slot.putStack(stack.copy());
                     stack.stackSize = 0;
                     this.inventoryGluttonyCharm.markDirty();
                     flag1 = true;
                     break;
                  }

                  this.putStackInSlot(k, new ItemStack(stack.getItem(), slot.getSlotStackLimit(), stack.getItemDamage()));
                  stack.stackSize -= slot.getSlotStackLimit();
                  this.inventoryGluttonyCharm.markDirty();
                  flag1 = true;
               }

               k += backwards ? -1 : 1;
            }
         }
      }

      return flag1;
   }

   public InventoryGluttonyCharm getGluttonyCharm() {
      return this.inventoryGluttonyCharm;
   }
}
