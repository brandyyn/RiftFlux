package com.voidsrift.riftflux.mixin.late.divinerpg;

import cpw.mods.fml.common.gameevent.PlayerEvent;
import net.divinerpg.utils.events.ArcanaHelper;
import net.divinerpg.utils.events.ArcanaTickHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ArcanaTickHandler.class, remap = false)
public class MixinArcanaTickHandler_FullOnLogin {
    private static final float RIFTFLUX_ARCANA_FULL = 200.0f;

    @Inject(method = "onPlayerLoggedIn", at = @At("HEAD"), cancellable = true)
    private void riftflux$startArcanaFull(PlayerEvent.PlayerLoggedInEvent event, CallbackInfo ci) {
        ArcanaHelper.getProperties(event.player).setBarValue(RIFTFLUX_ARCANA_FULL);
        ci.cancel();
    }
}
