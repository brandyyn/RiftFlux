/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.inventory.IInventory
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTBase
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.nbt.NBTTagList
 */
package zairus.worldexplorer.core.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class InventoryBase
implements IInventory {
    protected final int inventorySize = 1;
    protected ItemStack[] contents = new ItemStack[1];
    protected String inventoryName;
    protected boolean inventoryChanged = false;

    public int getSizeInventory() {
        return this.contents.length;
    }

    public ItemStack getStackInSlot(int slot) {
        return slot < this.contents.length ? this.contents[slot] : null;
    }

    public ItemStack decrStackSize(int slot, int count) {
        if (this.contents[slot] != null) {
            if (this.contents[slot].stackSize <= count) {
                ItemStack itemstack = this.contents[slot];
                this.contents[slot] = null;
                this.markDirty();
                return itemstack;
            }
            ItemStack itemstack = this.contents[slot].splitStack(count);
            if (this.contents[slot].stackSize == 0) {
                this.contents[slot] = null;
            }
            this.markDirty();
            return itemstack;
        }
        return null;
    }

    public ItemStack getStackInSlotOnClosing(int slot) {
        if (this.contents[slot] != null) {
            ItemStack itemstack = this.contents[slot];
            this.contents[slot] = null;
            return itemstack;
        }
        return null;
    }

    public void setInventorySlotContents(int slot, ItemStack stack) {
        this.contents[slot] = stack;
        if (stack != null && stack.stackSize > this.getInventoryStackLimit()) {
            stack.stackSize = this.getInventoryStackLimit();
        }
        this.markDirty();
    }

    public String getInventoryName() {
        return this.hasCustomInventoryName() ? this.inventoryName : "inventory.name";
    }

    public boolean hasCustomInventoryName() {
        return this.inventoryName != null && this.inventoryName.length() > 0;
    }

    public int getInventoryStackLimit() {
        return 64;
    }

    public void markDirty() {
        this.inventoryChanged = true;
    }

    public boolean isUseableByPlayer(EntityPlayer player) {
        return true;
    }

    public void openInventory() {
    }

    public void closeInventory() {
    }

    public boolean isItemValidForSlot(int slot, ItemStack syack) {
        return true;
    }

    public NBTTagList writeToNBT(NBTTagList tagList) {
        for (int i = 0; i < this.contents.length; ++i) {
            if (this.contents[i] == null) continue;
            NBTTagCompound nbttagcompound = new NBTTagCompound();
            nbttagcompound.setByte("Slot", (byte)i);
            this.contents[i].writeToNBT(nbttagcompound);
            tagList.appendTag((NBTBase)nbttagcompound);
        }
        return tagList;
    }

    public void readFromNBT(NBTTagList tagList) {
        this.contents = new ItemStack[1];
        for (int i = 0; i < tagList.tagCount(); ++i) {
            NBTTagCompound nbttagcompound = tagList.getCompoundTagAt(i);
            int j = nbttagcompound.getByte("Slot") & 0xFF;
            ItemStack itemstack = ItemStack.loadItemStackFromNBT((NBTTagCompound)nbttagcompound);
            if (itemstack == null || j < 0 || j >= this.contents.length) continue;
            this.contents[j] = itemstack;
        }
    }
}

