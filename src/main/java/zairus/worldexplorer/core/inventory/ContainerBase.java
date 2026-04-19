/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.InventoryPlayer
 *  net.minecraft.inventory.Container
 *  net.minecraft.inventory.ICrafting
 *  net.minecraft.inventory.IInventory
 *  net.minecraft.inventory.Slot
 *  net.minecraft.item.ItemStack
 */
package zairus.worldexplorer.core.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerBase
extends Container {
    public int bindPlayerInventory(InventoryPlayer playerInv) {
        int iIndex = 0;
        int gridX = 7;
        int gridY = 149;
        int gridCols = 9;
        int gridRows = 1;
        iIndex = this.placeSlotGrid((IInventory)playerInv, iIndex, gridX, gridY, gridCols, gridRows);
        gridX = 7;
        gridY = 91;
        gridCols = 9;
        gridRows = 3;
        iIndex = this.placeSlotGrid((IInventory)playerInv, iIndex, gridX, gridY, gridCols, gridRows);
        return iIndex;
    }

    public int placeSlotGrid(IInventory inv, int iIndex, int gridX, int gridY, int gridCols, int gridRows) {
        block0: for (int i = 0; i < gridRows; ++i) {
            for (int j = 0; j < gridCols; ++j) {
                if (iIndex > inv.getSizeInventory()) break block0;
                this.addSlotToContainer(new Slot(inv, iIndex, gridX + j * 18, gridY + i * 18));
                ++iIndex;
            }
        }
        return iIndex;
    }

    public boolean canInteractWith(EntityPlayer player) {
        return true;
    }

    public ItemStack slotClick(int slot, int slotX, int slotY, EntityPlayer player) {
        ItemStack stack = super.slotClick(slot, slotX, slotY, player);
        return stack;
    }

    protected void retrySlotClick(int slotNumber, int p_75133_2_, boolean p_75133_3_, EntityPlayer player) {
        if (slotNumber < 36) {
            this.slotClick(slotNumber, p_75133_2_, 1, player);
        }
    }

    public void detectAndSendChanges() {
        for (int i = 0; i < this.inventorySlots.size(); ++i) {
            ItemStack itemstack = ((Slot)this.inventorySlots.get(i)).getStack();
            ItemStack itemstack1 = (ItemStack)this.inventoryItemStacks.get(i);
            if (ItemStack.areItemStacksEqual((ItemStack)itemstack1, (ItemStack)itemstack)) continue;
            itemstack1 = itemstack == null ? null : itemstack.copy();
            this.inventoryItemStacks.set(i, itemstack1);
            for (int j = 0; j < this.crafters.size(); ++j) {
                ((ICrafting)this.crafters.get(j)).sendSlotContents((Container)this, i, itemstack1);
            }
        }
    }

    public ItemStack transferStackInSlot(EntityPlayer player, int slotNumber) {
        ItemStack itemstack = null;
        Slot slot = (Slot)this.inventorySlots.get(slotNumber);
        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            if (itemstack1.stackSize == 0) {
                slot.putStack((ItemStack)null);
            } else {
                slot.onSlotChanged();
            }
        }
        return itemstack;
    }

    public void onContainerClosed(EntityPlayer player) {
    }

    public void putStackInSlot(int slot, ItemStack stack) {
        this.getSlot(slot).putStack(stack);
    }
}

