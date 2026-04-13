package com.voidsrift.riftflux.mixin.early.dragonapi;

import net.minecraft.client.particle.EntityFX;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "Reika.DragonAPI.Extras.ThrottleableEffectRenderer", remap = false)
public abstract class MixinThrottleableEffectRenderer_VanillaParticleLoop {

    @Inject(method = "isParticleVisible", at = @At("HEAD"), cancellable = true, require = 0)
    private static void riftflux$alwaysRenderDragonAPIParticles(EntityFX fx, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(Boolean.TRUE);
    }
}
