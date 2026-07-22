package com.voidsrift.riftflux.mixin.early.baubles;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import baubles.common.container.SlotBauble;

@Mixin(value = SlotBauble.class, remap = false)
public abstract class MixinSlotBauble_UniversalSlotOnly {

    @ModifyConstant(
            method = "isItemValid",
            constant = @Constant(stringValue = "universal")
    )
    private String rf$makeUniversalTypeExact(String constant) {
        return "riftflux:no_universal_wildcard";
    }
}
