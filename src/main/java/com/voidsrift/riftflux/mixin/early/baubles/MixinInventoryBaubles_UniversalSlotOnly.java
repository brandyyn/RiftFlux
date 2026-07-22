package com.voidsrift.riftflux.mixin.early.baubles;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import baubles.common.container.InventoryBaubles;

@Mixin(value = InventoryBaubles.class, remap = false)
public abstract class MixinInventoryBaubles_UniversalSlotOnly {

    @ModifyConstant(
            method = "isItemValidForSlot",
            constant = @Constant(stringValue = "universal")
    )
    private String rf$makeUniversalTypeExact(String constant) {
        return "riftflux:no_universal_wildcard";
    }
}
