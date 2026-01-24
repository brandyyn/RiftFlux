package com.voidsrift.riftflux.mixin.late.xaero.minimap;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import xaero.common.IXaeroMinimap;
import xaero.common.misc.Internet;

@Mixin(value = Internet.class, remap = false)
public class MixinInternet {
    /**
     * @author Charsy89
     * @reason Minimizing internet connection by preventing Xaero's Minimap from
     *         checking for updates.
     */
    @Overwrite
    public static void checkModVersion(IXaeroMinimap modMain) {
        modMain.setOutdated(false);
    }
}
