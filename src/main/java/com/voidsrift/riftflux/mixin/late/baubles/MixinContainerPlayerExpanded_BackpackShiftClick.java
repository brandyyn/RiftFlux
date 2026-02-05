package com.voidsrift.riftflux.mixin.late.baubles;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import baubles.common.container.ContainerPlayerExpanded;
import baubles.common.container.InventoryBaubles;
import com.voidsrift.riftflux.vortex.item.ModItems;
import makamys.satchels.compat.BaublesCompat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

@Mixin(value = ContainerPlayerExpanded.class, remap = false)
public abstract class MixinContainerPlayerExpanded_BackpackShiftClick {

    @Inject(method = "transferStackInSlot", at = @At("HEAD"), cancellable = true)
    private void rf$shiftClickBackpack(EntityPlayer player, int index, CallbackInfoReturnable<ItemStack> cir) {
        List inventorySlots = ((Container) (Object) this).inventorySlots;
        if (index < 0 || index >= inventorySlots.size()) {
            return;
        }
        Object slotObj = inventorySlots.get(index);
        if (!(slotObj instanceof Slot)) {
            return;
        }
        Slot slot = (Slot) slotObj;
        if (!slot.getHasStack()) {
            return;
        }
        ItemStack stack = slot.getStack();
        if (stack == null || stack.getItem() != ModItems.backpack) {
            return;
        }
        if (slot.inventory instanceof InventoryBaubles) {
            return;
        }

        ItemStack original = stack.copy();
        boolean moved = BaublesCompat.equipToFirstEmpty(player, stack, BaublesCompat.TYPE_BACKPACK);
        if (!moved) {
            cir.setReturnValue(null);
            return;
        }
        if (stack.stackSize <= 0) {
            slot.putStack(null);
        } else {
            slot.onSlotChanged();
        }
        slot.onPickupFromSlot(player, stack);
        cir.setReturnValue(original);
    }
}
