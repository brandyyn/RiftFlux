package makamys.satchels.compat;

import baubles.api.BaublesApi;
import baubles.api.expanded.BaubleExpandedSlots;
import makamys.satchels.Satchels;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public final class BaublesCompat {

    public static final String TYPE_SATCHEL = "satchel";
    public static final String TYPE_POUCH = "pouch";
    public static final String TYPE_BACKPACK = "backpack";

    private static boolean slotsRegistered = false;

    private BaublesCompat() {
    }

    public static void registerSlots() {
        if (slotsRegistered) {
            return;
        }
        slotsRegistered = true;
        tryRegister(TYPE_SATCHEL, 1);
        tryRegister(TYPE_POUCH, 2);
        tryRegister(TYPE_BACKPACK, 1);
    }

    private static void tryRegister(String type, int minSlots) {
        try {
            BaubleExpandedSlots.tryRegisterType(type);
            boolean ok = BaubleExpandedSlots.tryAssignSlotsUpToMinimum(type, minSlots);
            if (!ok) {
                Satchels.LOGGER.warn("Failed to assign {} bauble slot(s) for type {}", minSlots, type);
            }
        } catch (Throwable t) {
            Satchels.LOGGER.warn("Failed to register bauble type {}", type, t);
        }
    }

    public static ItemStack getBaubleStack(EntityPlayer player, String type, int ordinal) {
        if (player == null) return null;
        IInventory baubles = BaublesApi.getBaubles(player);
        if (baubles == null) return null;
        int[] slots = BaubleExpandedSlots.getIndexesOfAssignedSlotsOfType(type);
        if (slots == null || slots.length <= ordinal) return null;
        return baubles.getStackInSlot(slots[ordinal]);
    }

    public static boolean equipToFirstEmpty(EntityPlayer player, ItemStack stack, String type) {
        if (player == null || stack == null) return false;
        IInventory baubles = BaublesApi.getBaubles(player);
        if (baubles == null) return false;
        int[] slots = BaubleExpandedSlots.getIndexesOfAssignedSlotsOfType(type);
        if (slots == null || slots.length == 0) return false;
        for (int slot : slots) {
            if (baubles.getStackInSlot(slot) != null) continue;
            if (!baubles.isItemValidForSlot(slot, stack)) continue;
            ItemStack equip = stack.copy();
            equip.stackSize = 1;
            baubles.setInventorySlotContents(slot, equip);
            baubles.markDirty();
            stack.stackSize -= 1;
            return true;
        }
        return false;
    }
}
