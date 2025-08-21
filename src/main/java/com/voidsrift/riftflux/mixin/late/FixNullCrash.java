package com.voidsrift.riftflux.mixin.late;


import net.minecraft.tileentity.MobSpawnerBaseLogic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "Reika.DragonAPI.Instantiable.Event.SpawnerCheckPlayerEvent")
public class FixNullCrash {

    @Inject(method = "runCheck", at = @At("HEAD"), cancellable = true, remap = false)
    private static void preventNullWorld(MobSpawnerBaseLogic lgc, CallbackInfoReturnable<Boolean> cir) {
        if (lgc == null || lgc.getSpawnerWorld() == null) {
            System.out.println("[Mixin] Prevented crash: MobSpawnerBaseLogic or its world was null in runCheck.");
            cir.setReturnValue(false);
        }
    }
}