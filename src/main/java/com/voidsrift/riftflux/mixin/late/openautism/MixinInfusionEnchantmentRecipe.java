package com.voidsrift.riftflux.mixin.late.vortex;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import com.voidsrift.riftflux.vortex.lib.helper.EnchantHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import thaumcraft.api.crafting.InfusionEnchantmentRecipe;

@Mixin({InfusionEnchantmentRecipe.class})
public abstract class MixinInfusionEnchantmentRecipe {
   @Redirect(
      method = {"matches"},
      at = @At(
   value = "INVOKE",
   target = "canApply"
),
      remap = false
   )
   private boolean onCanApply(Enchantment enchantment, ItemStack central) {
      return enchantment.canApply(central) && EnchantHelper.canEnchantAtTable(central, enchantment);
   }
}
