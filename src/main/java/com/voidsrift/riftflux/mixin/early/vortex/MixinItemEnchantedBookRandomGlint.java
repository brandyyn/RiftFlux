package com.voidsrift.riftflux.mixin.early.vortex;

import com.voidsrift.riftflux.vortex.lib.helper.EnchantHelper;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEnchantedBook.class)
public abstract class MixinItemEnchantedBookRandomGlint {

    @Inject(method = "addEnchantment", at = @At("TAIL"))
    private void riftflux$applyRandomGlintOnStoredEnchant(ItemStack stack, EnchantmentData enchantmentData,
                                                          CallbackInfo ci) {
        EnchantHelper.maybeApplyRandomGlint(stack);
    }
}
