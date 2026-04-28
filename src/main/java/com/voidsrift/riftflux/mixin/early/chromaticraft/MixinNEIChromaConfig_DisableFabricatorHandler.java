package com.voidsrift.riftflux.mixin.early.chromaticraft;

import Reika.ChromatiCraft.ModInterface.NEI.NEIChromaConfig;
import codechicken.nei.api.API;
import codechicken.nei.recipe.ICraftingHandler;
import com.voidsrift.riftflux.ModConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = NEIChromaConfig.class, remap = false)
public abstract class MixinNEIChromaConfig_DisableFabricatorHandler {
    private static final String FABRICATOR_HANDLER_CLASS =
            "Reika.ChromatiCraft.ModInterface.NEI.FabricatorHandler";

    @Redirect(
            method = "loadConfig",
            at = @At(
                    value = "INVOKE",
                    target = "Lcodechicken/nei/api/API;registerRecipeHandler(Lcodechicken/nei/recipe/ICraftingHandler;)V",
                    remap = false
            ),
            remap = false,
            require = 0
    )
    private static void riftflux$skipFabricatorHandler(ICraftingHandler handler) {
        if (ModConfig.disableChromatiCraftItemFabricator
                && handler != null
                && FABRICATOR_HANDLER_CLASS.equals(handler.getClass().getName())) {
            return;
        }
        API.registerRecipeHandler(handler);
    }
}
