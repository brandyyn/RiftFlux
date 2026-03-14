package com.voidsrift.riftflux.furniture.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerCabinet extends Container {
    private static final int STORAGE_SIZE = 15;
    private final IInventory storage;

    public ContainerCabinet(IInventory playerInventory, IInventory storage) {
        this.storage = storage;
        storage.openInventory();

        int index = 0;
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 5; ++col) {
                this.addSlotToContainer(new Slot(storage, index++, 44 + col * 18, 18 + row * 18));
            }
        }

        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlotToContainer(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 85 + row * 18));
            }
        }

        for (int col = 0; col < 9; ++col) {
            this.addSlotToContainer(new Slot(playerInventory, col, 8 + col * 18, 143));
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return this.storage.isUseableByPlayer(player);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slotIndex) {
        if (slotIndex < 0 || slotIndex >= this.inventorySlots.size()) {
            return null;
        }

        ItemStack result = null;
        Slot slot = (Slot) this.inventorySlots.get(slotIndex);
        if (slot != null && slot.getHasStack()) {
            ItemStack stack = slot.getStack();
            result = stack.copy();

            if (slotIndex < STORAGE_SIZE) {
                if (!this.mergeItemStack(stack, STORAGE_SIZE, this.inventorySlots.size(), true)) {
                    return null;
                }
            } else if (!this.mergeItemStack(stack, 0, STORAGE_SIZE, false)) {
                return null;
            }

            if (stack.stackSize == result.stackSize) {
                return null;
            }

            if (stack.stackSize == 0) {
                slot.putStack(null);
            } else {
                slot.onSlotChanged();
            }

            slot.onPickupFromSlot(player, stack);
        }
        return result;
    }

    @Override
    public void onContainerClosed(EntityPlayer player) {
        super.onContainerClosed(player);
        this.storage.closeInventory();
    }
}
