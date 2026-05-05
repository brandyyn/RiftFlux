package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.spawning.SpawnTypeMobCapHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class MixinEntity_SpawnTypeMobCap {
    @Inject(method = "isCreatureType", at = @At("HEAD"), cancellable = true, remap = false)
    private void riftflux$useSpawnTypeForMobCap(
            EnumCreatureType type,
            boolean forSpawnCount,
            CallbackInfoReturnable<Boolean> cir
    ) {
        if (!forSpawnCount) {
            return;
        }
        cir.setReturnValue(SpawnTypeMobCapHandler.isCreatureType((Entity) (Object) this, type));
    }
}
