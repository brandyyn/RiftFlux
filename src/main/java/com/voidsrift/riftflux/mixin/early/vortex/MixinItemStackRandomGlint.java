package com.voidsrift.riftflux.mixin.early.vortex;

import com.voidsrift.riftflux.vortex.lib.helper.EnchantHelper;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemStack.class)
public abstract class MixinItemStackRandomGlint {

    @Inject(method = "addEnchantment", at = @At("TAIL"))
    private void riftflux$applyRandomGlintOnEnchant(Enchantment enchantment, int level, CallbackInfo ci) {
        EnchantHelper.maybeApplyRandomGlint((ItemStack) (Object) this);
    }
}
