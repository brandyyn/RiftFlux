package com.voidsrift.riftflux.dualhotbar;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public final class DualHotbarPickBlockHelper {
    private DualHotbarPickBlockHelper() {
    }

    public static int getHotbarSize(EntityPlayer player) {
        if (player == null || player.inventory == null || player.inventory.mainInventory == null) {
            return 0;
        }
        return Math.min(DualHotbarState.hotbarSize, player.inventory.mainInventory.length);
    }

    public static int findMatchingHotbarSlot(EntityPlayer player, ItemStack result) {
        int hotbarSize = getHotbarSize(player);
        for (int slot = 0; slot < hotbarSize; slot++) {
            ItemStack stack = player.inventory.getStackInSlot(slot);
            if (stack == null || !stack.isItemEqual(result) || !ItemStack.areItemStackTagsEqual(stack, result)) {
                continue;
            }
            return slot;
        }
        return -1;
    }

    public static int findFirstEmptyHotbarSlot(EntityPlayer player) {
        int hotbarSize = getHotbarSize(player);
        for (int slot = 0; slot < hotbarSize; slot++) {
            if (player.inventory.getStackInSlot(slot) == null) {
                return slot;
            }
        }
        return -1;
    }

    public static int getCreativeContainerSlot(EntityPlayer player, int hotbarSlot) {
        if (player == null || player.inventoryContainer == null || hotbarSlot < 0) {
            return hotbarSlot;
        }
        if (hotbarSlot < 9) {
            return player.inventoryContainer.inventorySlots.size() - 9 + hotbarSlot;
        }
        return hotbarSlot;
    }
}
