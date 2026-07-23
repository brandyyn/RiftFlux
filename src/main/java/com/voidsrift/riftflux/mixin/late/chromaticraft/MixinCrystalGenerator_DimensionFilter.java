package com.voidsrift.riftflux.mixin.late.chromaticraft;

import com.voidsrift.riftflux.ModConfig;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

import Reika.ChromatiCraft.World.IWG.CrystalGenerator;

@Mixin(CrystalGenerator.class)
public abstract class MixinCrystalGenerator_DimensionFilter {

    @Inject(method = "generate", at = @At("HEAD"), cancellable = true, remap = false)
    private void riftflux$filterCrystalGeneration(
            Random random,
            int chunkX,
            int chunkZ,
            World world,
            IChunkProvider chunkGenerator,
            IChunkProvider chunkProvider,
            CallbackInfo ci
    ) {
        if (world == null
                || world.provider == null
                || !ModConfig.isChromatiCraftCrystalDimensionAllowed(world.provider.dimensionId)) {
            ci.cancel();
        }
    }
}
