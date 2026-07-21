package com.voidsrift.riftflux.mixin.early.nei;

import codechicken.nei.NEIServerUtils;
import com.voidsrift.riftflux.gravestone.GravestoneTypeGrouping;
import gravestone.item.ItemBlockGSGraveStone;
import gravestone.item.ItemBlockGSMemorial;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = NEIServerUtils.class, remap = false)
public abstract class MixinNEIServerUtils_GravestoneNBTRecipes {
    @Inject(method = "areStacksSameTypeCrafting", at = @At("HEAD"), cancellable = true, remap = false)
    private static void riftflux$matchGravestoneRecipeSubtype(
            ItemStack first,
            ItemStack second,
            CallbackInfoReturnable<Boolean> cir
    ) {
        if (first == null || second == null || first.getItem() != second.getItem()) {
            return;
        }
        if (!(first.getItem() instanceof ItemBlockGSGraveStone)
                && !(first.getItem() instanceof ItemBlockGSMemorial)) {
            return;
        }

        cir.setReturnValue(GravestoneTypeGrouping.areSameFamily(first, second));
    }
}
