package com.excsi.riftfixes.mixin.late;

import Reika.DragonAPI.Auxiliary.Trackers.RetroGenController;
import Reika.DragonAPI.Interfaces.RetroactiveGenerator;
import com.excsi.riftfixes.ModConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RetroGenController.class)
public class MixinRetroGenController {

    @Inject(method = "addHybridGenerator",at = @At("HEAD"),remap = false,cancellable = true)
    public void inject(RetroactiveGenerator gen, int weight, CallbackInfo ci){
        if(ModConfig.disableStrataVents && gen.getIDString().equals("GeoStrata Vents"))
            ci.cancel();
        if(ModConfig.disableStrataOreVeins && gen.getIDString().equals("GeoStrata Decorations"))
            ci.cancel();
    }
}
