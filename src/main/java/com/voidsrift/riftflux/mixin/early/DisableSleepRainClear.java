package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.ModConfig;
import net.minecraft.world.WorldServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * MC 1.7.10: Cancel the "clear weather after sleep" hook.
 * We cancel WorldServer#resetRainAndThunder() so sleeping does not stop rain/thunder.
 */
@Mixin(WorldServer.class)
public abstract class DisableSleepRainClear {

    // Use MCP name with descriptor so it resolves in dev:
    // resetRainAndThunder()V  (returns void)
    @Inject(
            method = "resetRainAndThunder()V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void riftflux$cancelSleepWeatherReset(CallbackInfo ci) {
        if (ModConfig.disableSleepRainClear) {
            ci.cancel(); // do nothing -> keep current rain/thunder state
        }
    }
}