package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.world.MoonPhaseHelper;
import net.minecraft.world.WorldProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WorldProvider.class)
public abstract class MixinWorldProvider_InitialMoonPhase {
    @Inject(method = "getMoonPhase", at = @At("RETURN"), cancellable = true)
    private void riftflux$offsetMoonPhase(long worldTime, CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(MoonPhaseHelper.offsetPhase(
                cir.getReturnValue().intValue(),
                ModConfig.initialMoonPhaseIndex
        ));
    }
}
