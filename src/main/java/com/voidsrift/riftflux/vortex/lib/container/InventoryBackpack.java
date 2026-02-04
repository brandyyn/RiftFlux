package com.voidsrift.riftflux.vortex.lib.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import com.voidsrift.riftflux.vortex.item.ModItems;

public class InventoryBackpack implements IInventory {
   public static final String invName = "Backpack";
   public static final int invSize = 27;
   private final ItemStack backpack;
   private ItemStack[] inventory = new ItemStack[27];

   public InventoryBackpack(ItemStack stack) {
      this.backpack = stack;
      if (!stack.hasTagCompound()) {
         stack.setTagCompound(new NBTTagCompound());
      }

      this.readFromNBT(stack.getTagCompound());
   }

   public int getSizeInventory() {
      return 27;
   }

   public ItemStack getStackInSlot(int slot) {
      return this.inventory[slot];
   }

   public ItemStack decrStackSize(int slot, int amount) {
      ItemStack stack = this.getStackInSlot(slot);
      if (stack != null) {
         if (stack.stackSize > amount) {
            stack = stack.splitStack(amount);
            this.markDirty();
         } else {
            this.setInventorySlotContents(slot, (ItemStack)null);
         }
      }

      return stack;
   }

   public ItemStack getStackInSlotOnClosing(int slot) {
      return this.getStackInSlot(slot);
   }

   public void setInventorySlotContents(int slot, ItemStack stack) {
      this.inventory[slot] = stack;
      if (stack != null && stack.stackSize > this.getInventoryStackLimit()) {
         stack.stackSize = this.getInventoryStackLimit();
      }

      this.markDirty();
   }

   public String getInventoryName() {
      return "Backpack";
   }

   public boolean hasCustomInventoryName() {
      return true;
   }

   public int getInventoryStackLimit() {
      return 64;
   }

   public void markDirty() {
      for(int i = 0; i < this.getSizeInventory(); ++i) {
         if (this.getStackInSlot(i) != null && this.getStackInSlot(i).stackSize == 0) {
            this.inventory[i] = null;
         }
      }

      this.writeToNBT(this.backpack.getTagCompound());
   }

   public boolean isUseableByPlayer(EntityPlayer p_70300_1_) {
      return true;
   }

   public void openInventory() {
   }

   public void closeInventory() {
   }

   public boolean isItemValidForSlot(int p_94041_1_, ItemStack itemStack) {
      return itemStack.getItem() != ModItems.backpack;
   }

   public boolean isEmpty() {
      for(int i = 0; i < this.inventory.length; ++i) {
         if (this.inventory[i] != null) {
            return false;
         }
      }

      return true;
   }

   public void readFromNBT(NBTTagCompound compound) {
      NBTTagList items = compound.getTagList("Backpack", 10);

      for(int i = 0; i < items.tagCount(); ++i) {
         NBTTagCompound item = items.getCompoundTagAt(i);
         int slot = item.getInteger("Slot");
         if (slot >= 0 && slot < this.getSizeInventory()) {
            this.inventory[slot] = ItemStack.loadItemStackFromNBT(item);
         }
      }

   }

   public void writeToNBT(NBTTagCompound tagcompound) {
      NBTTagList items = new NBTTagList();

      for(int i = 0; i < this.getSizeInventory(); ++i) {
         if (this.getStackInSlot(i) != null) {
            NBTTagCompound item = new NBTTagCompound();
            item.setInteger("Slot", i);
            this.getStackInSlot(i).writeToNBT(item);
            items.appendTag(item);
         }
      }

      tagcompound.setTag("Backpack", items);
   }
}
