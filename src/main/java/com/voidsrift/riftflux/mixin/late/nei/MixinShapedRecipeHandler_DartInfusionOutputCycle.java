package com.voidsrift.riftflux.mixin.late.nei;

import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.ShapedRecipeHandler;
import com.voidsrift.riftflux.riftexplorer.DartInfusionDisplayRecipe;
import java.util.ArrayList;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ShapedRecipeHandler.class, remap = false)
public abstract class MixinShapedRecipeHandler_DartInfusionOutputCycle {
    @Inject(method = "forgeShapedRecipe", at = @At("RETURN"), remap = false)
    private void riftflux$useCyclingDartInfusionResult(
            ShapedOreRecipe recipe,
            CallbackInfoReturnable<ShapedRecipeHandler.CachedShapedRecipe> cir
    ) {
        if (!(recipe instanceof DartInfusionDisplayRecipe)) {
            return;
        }
        ShapedRecipeHandler.CachedShapedRecipe cachedRecipe = cir.getReturnValue();
        if (cachedRecipe == null) {
            return;
        }
        ArrayList<ItemStack> outputs = ((DartInfusionDisplayRecipe) recipe).getDisplayOutputs();
        if (outputs.size() > 1) {
            cachedRecipe.result = new PositionedStack(outputs, 119, 24);
        }
    }
}
