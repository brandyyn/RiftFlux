package com.voidsrift.riftflux.mixin.early.vortex;

import java.util.Map;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import com.voidsrift.riftflux.vortex.lib.helper.EnchantHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({EnchantmentHelper.class})
public abstract class MixinEnchantmentHelper {
   @Redirect(
      method = {"mapEnchantmentData"},
      at = @At(
   value = "INVOKE",
   target = "canApplyAtEnchantingTable",
   remap = false
)
   )
   private static boolean onCanApplyAtEnchantingTable(Enchantment enchantment, ItemStack itemStack) {
      return enchantment.canApplyAtEnchantingTable(itemStack) && EnchantHelper.canEnchantAtTable(itemStack, enchantment);
   }

   @Inject(method = {"setEnchantments"}, at = @At("TAIL"))
   private static void riftflux$applyRandomGlintOnSetEnchantments(Map enchantments, ItemStack itemStack,
                                                                  CallbackInfo ci) {
      EnchantHelper.maybeApplyRandomGlint(itemStack);
   }
}
