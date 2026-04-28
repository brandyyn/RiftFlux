package com.voidsrift.riftflux.mixin.early.chromaticraft;

import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles.FabricatorRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.RecipesCastingTable;
import com.voidsrift.riftflux.ModConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = RecipesCastingTable.class, remap = false)
public abstract class MixinRecipesCastingTable_DisableFabricatorRecipe {

    @Inject(method = "addRecipe", at = @At("HEAD"), cancellable = true, remap = false)
    private void riftflux$skipFabricatorRecipe(CastingRecipe recipe, CallbackInfoReturnable<CastingRecipe> cir) {
        if (ModConfig.disableChromatiCraftItemFabricator && recipe instanceof FabricatorRecipe) {
            cir.setReturnValue(null);
        }
    }
}
