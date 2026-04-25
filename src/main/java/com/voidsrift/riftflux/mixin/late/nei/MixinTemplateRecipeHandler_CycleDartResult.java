package com.voidsrift.riftflux.mixin.late.nei;

import codechicken.nei.NEIClientUtils;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.TemplateRecipeHandler;
import java.util.List;
import java.util.Random;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import zairus.worldexplorer.archery.items.WEArcheryItems;

@Mixin(value = TemplateRecipeHandler.class, remap = false)
public abstract class MixinTemplateRecipeHandler_CycleDartResult {
    @Shadow(remap = false)
    public int cycleticks;

    @Inject(method = "getResultStack", at = @At("RETURN"), remap = false)
    private void riftflux$cycleDartInfusionResult(int recipe, CallbackInfoReturnable<PositionedStack> cir) {
        PositionedStack result = cir.getReturnValue();
        if (!riftflux$isCyclingDartResult(result) || NEIClientUtils.shiftKey()) {
            return;
        }

        List<ItemStack> outputs = result.getFilteredPermutations();
        if (outputs.size() <= 1) {
            return;
        }
        int index = new Random((long)(this.cycleticks / 20) + 0x51A7BEAFL).nextInt(outputs.size());
        result.setPermutationToRender(outputs.get(index));
    }

    private static boolean riftflux$isCyclingDartResult(PositionedStack result) {
        if (result == null || result.items == null || result.items.length <= 1 || WEArcheryItems.dart == null) {
            return false;
        }
        for (int i = 0; i < result.items.length; i++) {
            ItemStack stack = result.items[i];
            if (stack == null || stack.getItem() != WEArcheryItems.dart || stack.stackSize != 8) {
                return false;
            }
        }
        return true;
    }
}
