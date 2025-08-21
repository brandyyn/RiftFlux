package com.voidsrift.riftflux.mixin.early;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Extends storePartialItemStack to allow merging when the ONLY NBT differences
 * are our marker keys ("riftflux_new"/"riftflux_seen"). We only handle the
 * "merge into existing partially-filled stacks" part and return the leftover,
 * matching vanilla semantics. Empty-slot insertion remains handled by vanilla.
 */
@Mixin(InventoryPlayer.class)
public abstract class MixinInventoryPlayerMergeIgnoreStarTags {

    private static final String TAG_NEW  = "riftflux_new";
    private static final String TAG_SEEN = "riftflux_seen";

    @Shadow public ItemStack[] mainInventory;

    @Inject(
            method = "storePartialItemStack(Lnet/minecraft/item/ItemStack;)I",
            at = @At("HEAD"),
            cancellable = true
    )
    private void riftflux$mergeIgnoringMarkerTags(ItemStack stack, CallbackInfoReturnable<Integer> cir) {
        if (stack == null || !stack.isStackable() || mainInventory == null) return;

        int left = stack.stackSize;

        // First pass: merge only into stacks of the same item/damage and (vanilla-tags-equal OR equal ignoring marker keys)
        for (int i = 0; i < mainInventory.length && left > 0; ++i) {
            ItemStack slot = mainInventory[i];
            if (slot == null) continue;

            if (slot.getItem() == stack.getItem()
                    && slot.getItemDamage() == stack.getItemDamage()
                    && (ItemStack.areItemStackTagsEqual(slot, stack) || rf$equalIgnoringMarkers(slot, stack))) {

                int max = Math.min(stack.getMaxStackSize(), slot.getMaxStackSize());
                int can = Math.min(max - slot.stackSize, left);
                if (can > 0) {
                    slot.stackSize += can;
                    left -= can;
                    slot.setItemDamage(slot.getItemDamage()); // touch to ensure dirty
                }
            }
        }

        // If we did any merge at all, return the remainder now and cancel vanilla
        if (left != stack.stackSize) {
            cir.setReturnValue(left);
            return;
        }
        // Otherwise, let vanilla continue (so behavior stays identical in other paths)
    }

    /** Compare NBT after stripping our marker keys; treat empty result as null. */
    private static boolean rf$equalIgnoringMarkers(ItemStack a, ItemStack b) {
        NBTTagCompound ta = (a != null) ? a.getTagCompound() : null;
        NBTTagCompound tb = (b != null) ? b.getTagCompound() : null;
        if (ta == tb) return true; // covers both null

        NBTTagCompound na = rf$normalize(ta);
        NBTTagCompound nb = rf$normalize(tb);
        if (na == null && nb == null) return true;
        if (na != null && nb != null) return na.equals(nb);
        return false;
    }

    private static NBTTagCompound rf$normalize(NBTTagCompound tag) {
        if (tag == null) return null;
        NBTTagCompound c = (NBTTagCompound) tag.copy();
        c.removeTag(TAG_NEW);
        c.removeTag(TAG_SEEN);
        return c.hasNoTags() ? null : c;
    }
}