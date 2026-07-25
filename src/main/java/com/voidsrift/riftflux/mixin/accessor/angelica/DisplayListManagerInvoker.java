package com.voidsrift.riftflux.mixin.accessor.angelica;

import com.gtnewhorizons.angelica.glsm.DisplayListManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = DisplayListManager.class, remap = false)
public interface DisplayListManagerInvoker {

    @Invoker("flushAll")
    static void riftflux$flushAll() {
        throw new AssertionError();
    }
}
