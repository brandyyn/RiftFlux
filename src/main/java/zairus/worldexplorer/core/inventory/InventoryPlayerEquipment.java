/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTBase
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.nbt.NBTTagList
 */
package zairus.worldexplorer.core.inventory;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import zairus.worldexplorer.core.inventory.InventoryBase;
import zairus.worldexplorer.core.items.WEItem;
import zairus.worldexplorer.core.player.CorePlayerManager;

public class InventoryPlayerEquipment
extends InventoryBase {
    private final int inventorySize = 10;
    public ItemStack[] contents = new ItemStack[10];
    public final EntityPlayer player;

    public InventoryPlayerEquipment(EntityPlayer player) {
        this.player = player;
    }

    public void saveInventory() {
        CorePlayerManager.savePlayerEquipmentInventory(this, this.player);
    }

    public boolean hasQuiver() {
        boolean hasit = false;
        if (this.getStackInSlot(3) != null && this.getStackInSlot(3).getItem() instanceof WEItem && ((WEItem)this.getStackInSlot(3).getItem()).holdsAmmo()) {
            hasit = true;
        }
        return hasit;
    }

    @Override
    public int getSizeInventory() {
        return 10;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return slot < this.contents.length ? this.contents[slot] : null;
    }

    @Override
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

    @Override
    public ItemStack getStackInSlotOnClosing(int slot) {
        if (this.contents[slot] != null) {
            ItemStack itemstack = this.contents[slot];
            this.contents[slot] = null;
            return itemstack;
        }
        return null;
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack) {
        this.contents[slot] = stack;
        if (stack != null && stack.stackSize > this.getInventoryStackLimit()) {
            stack.stackSize = this.getInventoryStackLimit();
        }
        this.markDirty();
    }

    @Override
    public String getInventoryName() {
        return this.hasCustomInventoryName() ? this.inventoryName : "inventory.name";
    }

    @Override
    public boolean hasCustomInventoryName() {
        return this.inventoryName != null && this.inventoryName.length() > 0;
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public void markDirty() {
        this.inventoryChanged = true;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return this.player.isDead ? false : player.getDistanceSqToEntity((Entity)this.player) < 64.0;
    }

    @Override
    public void closeInventory() {
        CorePlayerManager.savePlayerEquipmentInventory(this, this.player);
    }

    @Override
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

    @Override
    public void readFromNBT(NBTTagList tagList) {
        this.contents = new ItemStack[10];
        for (int i = 0; i < tagList.tagCount(); ++i) {
            NBTTagCompound nbttagcompound = tagList.getCompoundTagAt(i);
            int j = nbttagcompound.getByte("Slot") & 0xFF;
            ItemStack itemstack = ItemStack.loadItemStackFromNBT((NBTTagCompound)nbttagcompound);
            if (itemstack == null || j < 0 || j >= this.contents.length) continue;
            this.contents[j] = itemstack;
        }
    }
}

