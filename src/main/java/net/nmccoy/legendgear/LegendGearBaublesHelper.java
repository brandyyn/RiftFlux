package net.nmccoy.legendgear;

import baubles.api.BaublesApi;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public final class LegendGearBaublesHelper {
    private LegendGearBaublesHelper() {
    }

    public static int findFirstMatchingSlot(EntityPlayer player, Item item, int... metas) {
        if (player == null || item == null) {
            return -1;
        }
        try {
            IInventory baubles = BaublesApi.getBaubles(player);
            if (baubles == null) {
                return -1;
            }
            for (int i = 0; i < baubles.getSizeInventory(); i++) {
                if (matches(baubles.getStackInSlot(i), item, metas)) {
                    return i;
                }
            }
        } catch (Throwable ignored) {
        }
        return -1;
    }

    public static ItemStack findFirstMatchingStack(EntityPlayer player, Item item, int... metas) {
        int slot = findFirstMatchingSlot(player, item, metas);
        return slot < 0 ? null : getStackInSlot(player, slot);
    }

    public static ItemStack getStackInSlot(EntityPlayer player, int slot) {
        if (player == null || slot < 0) {
            return null;
        }
        try {
            IInventory baubles = BaublesApi.getBaubles(player);
            if (baubles == null || slot >= baubles.getSizeInventory()) {
                return null;
            }
            return baubles.getStackInSlot(slot);
        } catch (Throwable ignored) {
        }
        return null;
    }

    public static boolean consumeFirstMatching(EntityPlayer player, Item item, int... metas) {
        return consumeSlot(player, findFirstMatchingSlot(player, item, metas));
    }

    public static boolean consumeSlot(EntityPlayer player, int slot) {
        if (player == null || slot < 0) {
            return false;
        }
        try {
            IInventory baubles = BaublesApi.getBaubles(player);
            if (baubles == null || slot >= baubles.getSizeInventory()) {
                return false;
            }
            ItemStack stack = baubles.getStackInSlot(slot);
            if (stack == null) {
                return false;
            }
            stack.stackSize--;
            if (stack.stackSize <= 0) {
                baubles.setInventorySlotContents(slot, null);
            }
            baubles.markDirty();
            return true;
        } catch (Throwable ignored) {
        }
        return false;
    }

    private static boolean matches(ItemStack stack, Item item, int... metas) {
        if (stack == null || stack.getItem() != item) {
            return false;
        }
        if (metas == null || metas.length == 0) {
            return true;
        }
        int meta = stack.getItemDamage();
        for (int i = 0; i < metas.length; i++) {
            if (meta == metas[i]) {
                return true;
            }
        }
        return false;
    }
}
