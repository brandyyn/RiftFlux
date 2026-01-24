package com.voidsrift.riftflux.mixin.late.ichun.ichunutil;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import ichun.client.thread.ThreadGetPatrons;

@Mixin(value = ThreadGetPatrons.class, remap = false)
public class MixinThreadGetPatrons {
    /**
     * @author Charsy89
     * @reason Minimizing internet connection by preventing iChunUtil from
     *         connecting to GitHub to download a list of patrons.
     */
    @Overwrite
    public void run() {
    }
}
