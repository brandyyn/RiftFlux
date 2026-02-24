package com.voidsrift.riftflux.mixin.early;

import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.S2FPacketSetSlot;
import net.minecraft.network.play.server.S30PacketWindowItems;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NetHandlerPlayClient.class)
public abstract class MixinNetHandlerPlayClient_WindowItemsClamp {

    @Shadow
    private net.minecraft.client.Minecraft gameController;

    private static final java.lang.reflect.Field ITEM_STACKS_FIELD = findItemStacksField();
    private static final java.lang.reflect.Field SET_SLOT_STACK_FIELD = findSetSlotStackField();

    @Inject(method = "func_147241_a", at = @At("HEAD"))
    private void riftflux$clampWindowItems(S30PacketWindowItems packet, CallbackInfo ci) {
        if (packet == null || this.gameController == null) {
            return;
        }
        Container container = this.gameController.thePlayer != null
                ? this.gameController.thePlayer.openContainer
                : null;
        if (container == null || container.inventorySlots == null) {
            return;
        }
        ItemStack[] stacks = getItemStacks(packet);
        if (stacks == null) {
            return;
        }
        boolean changed = sanitizeStacks(stacks);
        int max = container.inventorySlots.size();
        if (stacks.length > max) {
            ItemStack[] trimmed = new ItemStack[max];
            System.arraycopy(stacks, 0, trimmed, 0, max);
            stacks = trimmed;
            changed = true;
        }
        if (changed) {
            setItemStacks(packet, stacks);
        }
    }

    @Inject(method = {"handleSetSlot", "func_147266_a"}, at = @At("HEAD"))
    private void riftflux$sanitizeSetSlot(S2FPacketSetSlot packet, CallbackInfo ci) {
        if (packet == null) {
            return;
        }
        ItemStack stack = getSetSlotStack(packet);
        if (isInvalidStack(stack)) {
            setSetSlotStack(packet, null);
        }
    }

    private static ItemStack[] getItemStacks(S30PacketWindowItems packet) {
        if (ITEM_STACKS_FIELD == null) {
            return null;
        }
        try {
            return (ItemStack[]) ITEM_STACKS_FIELD.get(packet);
        } catch (IllegalAccessException ignored) {
            return null;
        }
    }

    private static void setItemStacks(S30PacketWindowItems packet, ItemStack[] stacks) {
        if (ITEM_STACKS_FIELD == null) {
            return;
        }
        try {
            ITEM_STACKS_FIELD.set(packet, stacks);
        } catch (IllegalAccessException ignored) {
        }
    }

    private static boolean sanitizeStacks(ItemStack[] stacks) {
        boolean changed = false;
        for (int i = 0; i < stacks.length; i++) {
            if (isInvalidStack(stacks[i])) {
                stacks[i] = null;
                changed = true;
            }
        }
        return changed;
    }

    private static boolean isInvalidStack(ItemStack stack) {
        return stack != null && stack.getItem() == null;
    }

    private static ItemStack getSetSlotStack(S2FPacketSetSlot packet) {
        if (SET_SLOT_STACK_FIELD == null) {
            return null;
        }
        try {
            return (ItemStack) SET_SLOT_STACK_FIELD.get(packet);
        } catch (IllegalAccessException ignored) {
            return null;
        }
    }

    private static void setSetSlotStack(S2FPacketSetSlot packet, ItemStack stack) {
        if (SET_SLOT_STACK_FIELD == null) {
            return;
        }
        try {
            SET_SLOT_STACK_FIELD.set(packet, stack);
        } catch (IllegalAccessException ignored) {
        }
    }

    private static java.lang.reflect.Field findItemStacksField() {
        try {
            for (java.lang.reflect.Field field : S30PacketWindowItems.class.getDeclaredFields()) {
                if (field.getType().isArray()
                        && ItemStack.class.equals(field.getType().getComponentType())) {
                    field.setAccessible(true);
                    return field;
                }
            }
        } catch (Throwable ignored) {
        }
        return null;
    }

    private static java.lang.reflect.Field findSetSlotStackField() {
        try {
            for (java.lang.reflect.Field field : S2FPacketSetSlot.class.getDeclaredFields()) {
                if (ItemStack.class.equals(field.getType())) {
                    field.setAccessible(true);
                    return field;
                }
            }
        } catch (Throwable ignored) {
        }
        return null;
    }
}
