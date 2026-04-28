package com.voidsrift.riftflux.mixin.early;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Enchantment.class)
public abstract class MixinEnchantment_ThornsAnyArmorTable {
    @Inject(
            method = "canApplyAtEnchantingTable",
            at = @At("HEAD"),
            cancellable = true,
            remap = false
    )
    private void riftflux$allowThornsOnAnyArmorAtTable(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if ((Object) this == Enchantment.thorns && riftflux$isArmor(stack)) {
            cir.setReturnValue(true);
        }
    }

    private static boolean riftflux$isArmor(ItemStack stack) {
        if (stack == null || stack.getItem() == null) {
            return false;
        }
        if (stack.getItem() instanceof ItemArmor) {
            return true;
        }
        for (int armorType = 0; armorType < 4; armorType++) {
            try {
                if (stack.getItem().isValidArmor(stack, armorType, null)) {
                    return true;
                }
            } catch (Throwable ignored) {
            }
        }
        return false;
    }
}
