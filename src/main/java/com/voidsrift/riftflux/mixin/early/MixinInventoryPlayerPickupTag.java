package com.voidsrift.riftflux.mixin.early;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Server-truth tagging of picked up items:
 * - Before addItemStackToInventory: snapshot counts
 * - After success: mark slots whose counts increased with riftflux_new=true and clear riftflux_seen
 */
@Mixin(InventoryPlayer.class)
public abstract class MixinInventoryPlayerPickupTag {

    @Shadow public ItemStack[] mainInventory;

    @Unique private int[] rf$beforeCounts;

    @Inject(method = "addItemStackToInventory(Lnet/minecraft/item/ItemStack;)Z", at = @At("HEAD"))
    private void rf$captureBefore(ItemStack incoming, CallbackInfoReturnable<Boolean> cir) {
        if (mainInventory == null) return;
        rf$beforeCounts = new int[mainInventory.length];
        for (int i = 0; i < mainInventory.length; i++) {
            ItemStack s = mainInventory[i];
            rf$beforeCounts[i] = (s != null ? s.stackSize : 0);
        }
    }

    @Inject(method = "addItemStackToInventory(Lnet/minecraft/item/ItemStack;)Z", at = @At("RETURN"))
    private void rf$tagIncreased(ItemStack incoming, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue()) { rf$beforeCounts = null; return; }
        if (mainInventory == null || rf$beforeCounts == null) return;

        for (int i = 0; i < mainInventory.length; i++) {
            ItemStack s = mainInventory[i];
            int after  = (s != null ? s.stackSize : 0);
            int before = rf$beforeCounts[i];

            if (s != null && after > before) {
                NBTTagCompound tag = s.getTagCompound();
                if (tag == null) tag = new NBTTagCompound();
                tag.removeTag("riftflux_seen");
                tag.setBoolean("riftflux_new", true);
                s.setTagCompound(tag);
            }
        }
        rf$beforeCounts = null;
    }
}
