package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.wheatfield.world.WheatfieldTerrainUtil;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenLakes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Mixin(WorldGenLakes.class)
public abstract class MixinWorldGenLakes_SkipWheatfield {
    @Inject(method = "generate", at = @At("HEAD"), cancellable = true)
    private void riftflux$skipWheatfieldLakes(World world, Random random, int x, int y, int z, CallbackInfoReturnable<Boolean> cir) {
        if (!WheatfieldTerrainUtil.isOverworld(world)) {
            return;
        }

        if (WheatfieldTerrainUtil.isWheatfieldBiome(world.getBiomeGenForCoords(x, z))) {
            cir.setReturnValue(false);
        }
    }
}
