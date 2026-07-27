package com.voidsrift.riftflux.mixin.early;

import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.BiomeGenSwamp;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BiomeGenBase.class)
public abstract class MixinBiomeGenBase_Beta18SwampWater {

    @Redirect(
            method = "getWaterColorMultiplier",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/world/biome/BiomeGenBase;waterColorMultiplier:I"
            )
    )
    private int riftflux$useBeta18SwampWaterColor(BiomeGenBase biome) {
        return biome instanceof BiomeGenSwamp ? 0xFFFFFF : biome.waterColorMultiplier;
    }
}
