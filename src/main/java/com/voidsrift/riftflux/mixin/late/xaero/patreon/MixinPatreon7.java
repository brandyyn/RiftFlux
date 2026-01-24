package com.voidsrift.riftflux.mixin.late.xaero.patreon;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import xaero.patreon.Patreon7;

@Mixin(value = Patreon7.class, remap = false)
public class MixinPatreon7 {
    /**
     * @author Charsy89
     * @reason Minimizing internet connection by preventing Xaero's mods from
     *         retrieving the patron list.
     */
    @Overwrite
    public static void checkPatreon() {
        Patreon7.loaded = true;
    }
}
