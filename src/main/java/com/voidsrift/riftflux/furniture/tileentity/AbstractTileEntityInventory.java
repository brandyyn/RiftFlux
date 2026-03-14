package com.voidsrift.riftflux.furniture.tileentity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;

public abstract class AbstractTileEntityInventory extends TileEntity implements IInventory {
    private final ItemStack[] contents;
    private final String inventoryName;

    protected AbstractTileEntityInventory(int size, String inventoryName) {
        this.contents = new ItemStack[size];
        this.inventoryName = inventoryName;
    }

    @Override
    public int getSizeInventory() {
        return this.contents.length;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        if (slot < 0 || slot >= this.contents.length) {
            return null;
        }
        return this.contents[slot];
    }

    @Override
    public ItemStack decrStackSize(int slot, int amount) {
        if (slot < 0 || slot >= this.contents.length || amount <= 0) {
            return null;
        }
        if (this.contents[slot] == null) {
            return null;
        }
        ItemStack result;
        if (this.contents[slot].stackSize <= amount) {
            result = this.contents[slot];
            this.contents[slot] = null;
        } else {
            result = this.contents[slot].splitStack(amount);
            if (this.contents[slot].stackSize <= 0) {
                this.contents[slot] = null;
            }
        }
        this.markDirty();
        return result;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slot) {
        if (slot < 0 || slot >= this.contents.length || this.contents[slot] == null) {
            return null;
        }
        ItemStack stack = this.contents[slot];
        this.contents[slot] = null;
        this.markDirty();
        return stack;
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack) {
        if (slot < 0 || slot >= this.contents.length) {
            return;
        }
        this.contents[slot] = stack;
        if (stack != null && stack.stackSize > this.getInventoryStackLimit()) {
            stack.stackSize = this.getInventoryStackLimit();
        }
        this.markDirty();
    }

    @Override
    public String getInventoryName() {
        return this.inventoryName;
    }

    @Override
    public boolean hasCustomInventoryName() {
        return false;
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return this.worldObj != null
                && this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord) == this
                && player.getDistanceSq(this.xCoord + 0.5D, this.yCoord + 0.5D, this.zCoord + 0.5D) <= 64.0D;
    }

    @Override
    public void openInventory() {
    }

    @Override
    public void closeInventory() {
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        return true;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        for (int slot = 0; slot < this.contents.length; ++slot) {
            this.contents[slot] = null;
        }
        NBTTagList items = tag.getTagList("Items", 10);
        for (int i = 0; i < items.tagCount(); ++i) {
            NBTTagCompound itemTag = items.getCompoundTagAt(i);
            int slot = itemTag.getByte("Slot") & 255;
            if (slot >= 0 && slot < this.contents.length) {
                this.contents[slot] = ItemStack.loadItemStackFromNBT(itemTag);
            }
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        NBTTagList items = new NBTTagList();
        for (int slot = 0; slot < this.contents.length; ++slot) {
            if (this.contents[slot] == null) {
                continue;
            }
            NBTTagCompound itemTag = new NBTTagCompound();
            itemTag.setByte("Slot", (byte) slot);
            this.contents[slot].writeToNBT(itemTag);
            items.appendTag(itemTag);
        }
        tag.setTag("Items", items);
    }
}
