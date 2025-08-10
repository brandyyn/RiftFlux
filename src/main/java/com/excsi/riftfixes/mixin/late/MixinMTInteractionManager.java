package com.excsi.riftfixes.mixin.late;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(targets = "Reika.DragonAPI.Instantiable.Event.MTInteractionManager", remap = false)
public abstract class MixinMTInteractionManager {

    @Overwrite
    public static boolean isMTLoaded() {
        return false;
    }
}