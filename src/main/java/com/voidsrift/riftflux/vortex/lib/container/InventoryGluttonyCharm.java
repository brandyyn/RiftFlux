package com.voidsrift.riftflux.vortex.lib.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class InventoryGluttonyCharm implements IInventory {
   public static final String invName = "Gluttony Charm";
   public static final int invSize = 1;
   private final ItemStack gluttonyCharm;
   private ItemStack[] inventory = new ItemStack[1];

   public InventoryGluttonyCharm(ItemStack itemStack) {
      this.gluttonyCharm = itemStack;
      if (!itemStack.hasTagCompound()) {
         itemStack.setTagCompound(new NBTTagCompound());
      }

      this.readFromNBT(itemStack.getTagCompound());
   }

   public int getSizeInventory() {
      return 1;
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

   public void setInventorySlotContents(int slot, ItemStack itemStack) {
      this.inventory[slot] = itemStack;
      if (itemStack != null && itemStack.stackSize > this.getInventoryStackLimit()) {
         itemStack.stackSize = this.getInventoryStackLimit();
      }

      this.markDirty();
   }

   public String getInventoryName() {
      return "Gluttony Charm";
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

      this.writeToNBT(this.gluttonyCharm.getTagCompound());
   }

   public boolean isUseableByPlayer(EntityPlayer p_70300_1_) {
      return true;
   }

   public void openInventory() {
   }

   public void closeInventory() {
   }

   public boolean isItemValidForSlot(int p_94041_1_, ItemStack itemStack) {
      return itemStack.getItem() instanceof ItemFood;
   }

   public void readFromNBT(NBTTagCompound compound) {
      NBTTagList items = compound.getTagList("Gluttony Charm", 10);

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

      tagcompound.setTag("Gluttony Charm", items);
   }
}
