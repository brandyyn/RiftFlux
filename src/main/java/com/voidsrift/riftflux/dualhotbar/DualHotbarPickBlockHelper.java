package com.voidsrift.riftflux.dualhotbar;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public final class DualHotbarPickBlockHelper {
    private static int pendingPickBlockSourceSlot = -1;
    private static ItemStack pendingPickBlockResult;

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

    public static int findMatchingInventorySlot(EntityPlayer player, ItemStack result, int ignoredSlot) {
        if (player == null || player.inventory == null || player.inventory.mainInventory == null || result == null) {
            return -1;
        }
        for (int slot = 0; slot < player.inventory.mainInventory.length; slot++) {
            if (slot == ignoredSlot) {
                continue;
            }
            ItemStack stack = player.inventory.getStackInSlot(slot);
            if (stack == null || !stack.isItemEqual(result) || !ItemStack.areItemStackTagsEqual(stack, result)) {
                continue;
            }
            return slot;
        }
        return -1;
    }

    public static int getInventoryContainerSlot(EntityPlayer player, int inventorySlot) {
        if (player == null || player.inventoryContainer == null || player.inventory == null || inventorySlot < 0) {
            return -1;
        }
        Slot slot = player.inventoryContainer.getSlotFromInventory(player.inventory, inventorySlot);
        return slot == null ? -1 : slot.slotNumber;
    }

    public static void clearPendingPickBlock() {
        pendingPickBlockSourceSlot = -1;
        pendingPickBlockResult = null;
    }

    public static void setPendingPickBlock(ItemStack result, int sourceSlot) {
        pendingPickBlockSourceSlot = sourceSlot;
        pendingPickBlockResult = result == null ? null : result.copy();
    }

    public static int getPendingPickBlockSourceSlot() {
        return pendingPickBlockSourceSlot;
    }

    public static ItemStack getPendingPickBlockResult() {
        return pendingPickBlockResult == null ? null : pendingPickBlockResult.copy();
    }
}
