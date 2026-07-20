package com.voidsrift.riftflux.chester;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerChester extends Container {
    private final IInventory chesterInventory;
    private final EntityChester chester;

    public ContainerChester(IInventory playerInventory, IInventory chesterInventory, EntityChester chester) {
        this.chesterInventory = chesterInventory;
        this.chester = chester;
        chesterInventory.openInventory();

        int slot = 0;
        for (int row = 0; row < chester.getInventoryRows(); row++) {
            for (int column = 0; column < 9; column++) {
                addSlotToContainer(new Slot(
                        chesterInventory,
                        slot++,
                        8 + column * 18,
                        18 + row * 18
                ));
            }
        }
        bindPlayerInventory(playerInventory);
    }

    private void bindPlayerInventory(IInventory playerInventory) {
        int rowOffset = (chester.getInventoryRows() - 4) * 18;
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                addSlotToContainer(new Slot(
                        playerInventory,
                        column + row * 9 + 9,
                        8 + column * 18,
                        103 + row * 18 + rowOffset
                ));
            }
        }
        for (int column = 0; column < 9; column++) {
            addSlotToContainer(new Slot(playerInventory, column, 8 + column * 18, 161 + rowOffset));
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return chester.canPlayerUseInventory(player);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slotIndex) {
        ItemStack result = null;
        Slot slot = (Slot) inventorySlots.get(slotIndex);
        if (slot == null || !slot.getHasStack()) {
            return null;
        }

        ItemStack stack = slot.getStack();
        result = stack.copy();
        int chesterSlots = chesterInventory.getSizeInventory();
        if (slotIndex < chesterSlots) {
            if (!mergeItemStack(stack, chesterSlots, inventorySlots.size(), true)) {
                return null;
            }
        } else if (!mergeItemStack(stack, 0, chesterSlots, false)) {
            return null;
        }

        if (stack.stackSize == 0) {
            slot.putStack(null);
        } else {
            slot.onSlotChanged();
        }
        return result;
    }

    @Override
    public void onContainerClosed(EntityPlayer player) {
        super.onContainerClosed(player);
        chesterInventory.closeInventory();
        if (!player.worldObj.isRemote) {
            chester.setOpen(false);
            player.worldObj.playSoundEffect(
                    chester.posX,
                    chester.posY,
                    chester.posZ,
                    "chester:chesterclose",
                    1.0F,
                    1.0F
            );
        }
    }
}
