package com.voidsrift.riftflux.mixin.early;

import net.minecraft.enchantment.EnchantmentThorns;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnchantmentThorns.class)
public abstract class MixinEnchantmentThorns_NoExtraDurability {
    @Inject(
            method = "canApply(Lnet/minecraft/item/ItemStack;)Z",
            at = @At("HEAD"),
            cancellable = true
    )
    private void riftflux$allowThornsOnAnyArmor(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (riftflux$isArmor(stack)) {
            cir.setReturnValue(true);
        }
    }

    @Redirect(
            method = "func_151367_b(Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/entity/Entity;I)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/item/ItemStack;damageItem(ILnet/minecraft/entity/EntityLivingBase;)V"
            )
    )
    private void riftflux$skipThornsArmorDurability(ItemStack stack, int amount, EntityLivingBase wearer) {
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
