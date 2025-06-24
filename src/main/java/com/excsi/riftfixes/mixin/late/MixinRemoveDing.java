package com.excsi.riftfixes.mixin.late;

import Reika.DragonAPI.Auxiliary.DragonAPIEventWatcher;
import Reika.DragonAPI.DragonAPIInit;
import Reika.DragonAPI.Instantiable.Event.Client.GameFinishedLoadingEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DragonAPIEventWatcher.class)
public class MixinRemoveDing {

    @Inject(method = "onGameLoaded",at = @At(value= "INVOKE_ASSIGN",target="LReika/DragonAPI/APIProxy;registerSidedHandlersGameLoaded()V"),cancellable = true,remap = false)
    public void inject(GameFinishedLoadingEvent event, CallbackInfo ding){
        ding.cancel();
    }
}


