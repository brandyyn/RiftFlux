package com.voidsrift.riftflux.mixin.early.witchinggadgets;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import cpw.mods.fml.common.registry.VillagerRegistry;
import witchinggadgets.WitchingGadgets;

@Mixin(value = WitchingGadgets.class, remap = false)
public class MixinWitchingGadgets_DisableVillageHouse {

    @Redirect(
            method = "preInit",
            at = @At(
                    value = "INVOKE",
                    target = "Lcpw/mods/fml/common/registry/VillagerRegistry;registerVillageCreationHandler(Lcpw/mods/fml/common/registry/VillagerRegistry$IVillageCreationHandler;)V",
                    remap = false
            ),
            remap = false
    )
    private void riftflux$skipVillageHouseRegistration(
            VillagerRegistry registry,
            VillagerRegistry.IVillageCreationHandler handler
    ) {
    }
}
