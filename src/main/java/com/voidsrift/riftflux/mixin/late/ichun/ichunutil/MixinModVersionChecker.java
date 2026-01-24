package com.voidsrift.riftflux.mixin.late.ichun.ichunutil;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import ichun.common.core.updateChecker.ModVersionChecker;

@Mixin(value = ModVersionChecker.class, remap = false)
public class MixinModVersionChecker {
    /**
     * @author Charsy89
     * @reason Minimizing internet connection by preventing iChunUtil from
     *         attempting to connect to GitHub to download mod version info.
     */
    @Overwrite
    public static void init() {
    }
}
