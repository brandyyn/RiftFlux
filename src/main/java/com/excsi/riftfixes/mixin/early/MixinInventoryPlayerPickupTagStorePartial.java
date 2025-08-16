package com.excsi.riftfixes.mixin.early;

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
 * Server-side: mirror the "tag on increased slot" behavior for the
 * storePartialItemStack(...) path as well, so re-pickups (and some
 * mod flows) still get starred.
 */
@Mixin(InventoryPlayer.class)
public abstract class MixinInventoryPlayerPickupTagStorePartial {

    @Shadow public ItemStack[] mainInventory;

    @Unique private int[] rf$beforeCountsSP;

    @Inject(method = "storePartialItemStack(Lnet/minecraft/item/ItemStack;)I", at = @At("HEAD"))
    private void rf$captureBeforeSP(ItemStack incoming, CallbackInfoReturnable<Integer> cir) {
        if (mainInventory == null) return;
        rf$beforeCountsSP = new int[mainInventory.length];
        for (int i = 0; i < mainInventory.length; i++) {
            ItemStack s = mainInventory[i];
            rf$beforeCountsSP[i] = (s != null ? s.stackSize : 0);
        }
    }

    @Inject(method = "storePartialItemStack(Lnet/minecraft/item/ItemStack;)I", at = @At("RETURN"))
    private void rf$tagAfterSP(ItemStack incoming, CallbackInfoReturnable<Integer> cir) {
        if (mainInventory == null || rf$beforeCountsSP == null) return;

        for (int i = 0; i < mainInventory.length; i++) {
            ItemStack s = mainInventory[i];
            int before = rf$beforeCountsSP[i];
            int after  = (s != null ? s.stackSize : 0);
            if (s != null && after > before) {
                NBTTagCompound tag = s.getTagCompound();
                if (tag == null) tag = new NBTTagCompound();
                tag.removeTag("riftfixes_seen");
                tag.setBoolean("riftfixes_new", true);
                s.setTagCompound(tag);
            }
        }
        rf$beforeCountsSP = null;
    }
}