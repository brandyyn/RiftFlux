package com.voidsrift.riftflux.mixin.late.xaero.worldmap;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import xaero.map.WorldMap;
import xaero.map.misc.Internet;

@Mixin(value = Internet.class, remap = false)
public class MixinInternet {
    /**
     * @author Charsy89
     * @reason Minimizing internet connection by preventing Xaero's World Map
     *         from checking for updates.
     */
    @Overwrite
    public static void checkModVersion() {
        WorldMap.isOutdated = false;
    }
}
