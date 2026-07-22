/*
 * Decompiled with CFR 0.152.
 */
package de.sanandrew.core.manpack.util.helpers;

import de.sanandrew.core.manpack.util.helpers.ItemUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public final class InventoryUtils {
    public static ItemStack decrPlayerHeldStackSize(EntityPlayer player, int amount) {
        ItemStack stack = player.getHeldItem();
        if (stack == null) {
            return null;
        }
        if (amount < 1) {
            return stack;
        }
        stack.stackSize -= amount;
        if (stack.stackSize <= 0) {
            player.setCurrentItemOrArmor(0, null);
            stack = null;
        } else {
            player.setCurrentItemOrArmor(0, stack.copy());
        }
        player.inventoryContainer.detectAndSendChanges();
        return stack;
    }

    public static ItemStack addStackToInventory(ItemStack is, IInventory inv) {
        return InventoryUtils.addStackToInventory(is, inv, true);
    }

    public static ItemStack addStackToInventory(ItemStack is, IInventory inv, boolean checkNBT) {
        int invSize = inv.getSizeInventory() - (inv instanceof InventoryPlayer ? 4 : 0);
        for (int i = 0; i < invSize && is != null; ++i) {
            int rest;
            ItemStack invIS = inv.getStackInSlot(i);
            if (invIS != null && ItemUtils.areStacksEqual(is, invIS, checkNBT)) {
                rest = is.stackSize + invIS.stackSize;
                int maxStack = Math.min(invIS.getMaxStackSize(), inv.getInventoryStackLimit());
                if (rest <= maxStack) {
                    invIS.stackSize = rest;
                    inv.setInventorySlotContents(i, invIS.copy());
                    is = null;
                    break;
                }
                int rest1 = rest - maxStack;
                invIS.stackSize = maxStack;
                inv.setInventorySlotContents(i, invIS.copy());
                is.stackSize = rest1;
                continue;
            }
            if (invIS != null || !inv.isItemValidForSlot(i, is)) continue;
            if (is.stackSize <= inv.getInventoryStackLimit()) {
                inv.setInventorySlotContents(i, is.copy());
                is = null;
                break;
            }
            rest = is.stackSize - inv.getInventoryStackLimit();
            is.stackSize = inv.getInventoryStackLimit();
            inv.setInventorySlotContents(i, is.copy());
            is.stackSize = rest;
        }
        return is;
    }
}

