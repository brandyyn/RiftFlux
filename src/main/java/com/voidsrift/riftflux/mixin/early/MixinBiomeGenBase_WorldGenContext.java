package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.util.RFPlantContext;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

/**
 * Marks the period while a biome is decorating a chunk (vanilla world
 * generation) so that BlockBush canBlockStay overrides can skip affecting
 * worldgen placement.
 *
 * This keeps {@code AllowPlantsOnAnyBlock} strictly to player-placed plants
 * while leaving world decoration (flowers, tall grass, etc.) vanilla.
 */
@Mixin(BiomeGenBase.class)
public abstract class MixinBiomeGenBase_WorldGenContext {

    @Inject(method = "decorate", at = @At("HEAD"))
    private void riftflux$enterDecorate(World world, Random rand, int chunkX, int chunkZ, CallbackInfo ci) {
        RFPlantContext.pushWorldGen();
    }

    @Inject(method = "decorate", at = @At("RETURN"))
    private void riftflux$exitDecorate(World world, Random rand, int chunkX, int chunkZ, CallbackInfo ci) {
        RFPlantContext.popWorldGen();
    }
}
