package com.voidsrift.riftflux.mixin.early;

import net.minecraft.world.ColorizerFoliage;
import net.minecraft.world.ColorizerGrass;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.BiomeGenSwamp;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BiomeGenSwamp.class)
public abstract class MixinBiomeGenSwamp_Beta18Colors {

    @Inject(method = "getBiomeGrassColor", at = @At("HEAD"), cancellable = true)
    private void riftflux$useBeta18GrassColor(int x, int y, int z, CallbackInfoReturnable<Integer> cir) {
        BiomeGenBase biome = (BiomeGenBase) (Object) this;
        cir.setReturnValue(biome.getModdedBiomeGrassColor(
                ColorizerGrass.getGrassColor(biome.temperature, biome.rainfall)
        ));
    }

    @Inject(method = "getBiomeFoliageColor", at = @At("HEAD"), cancellable = true)
    private void riftflux$useBeta18FoliageColor(int x, int y, int z, CallbackInfoReturnable<Integer> cir) {
        BiomeGenBase biome = (BiomeGenBase) (Object) this;
        cir.setReturnValue(biome.getModdedBiomeFoliageColor(
                ColorizerFoliage.getFoliageColor(biome.temperature, biome.rainfall)
        ));
    }
}
