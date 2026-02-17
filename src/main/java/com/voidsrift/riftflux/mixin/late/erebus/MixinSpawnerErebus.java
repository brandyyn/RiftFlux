package com.voidsrift.riftflux.mixin.late.erebus;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.WorldServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "erebus.world.SpawnerErebus", remap = false)
public abstract class MixinSpawnerErebus {
    @Inject(
            method = "onServerTick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraftforge/common/DimensionManager;getWorld(I)Lnet/minecraft/world/WorldServer;",
                    shift = At.Shift.BY,
                    by = 2
            ),
            remap = false,
            cancellable = true,
            require = 0
    )
    private void riftflux$onServerTick$addNullCheck(CallbackInfo ci, @Local WorldServer erebusWorld) {
        if (erebusWorld == null) {
            ci.cancel();
        }
    }
}
