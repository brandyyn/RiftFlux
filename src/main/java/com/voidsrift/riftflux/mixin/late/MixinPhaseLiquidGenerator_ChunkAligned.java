package com.voidsrift.riftflux.mixin.late;

import cn.academy.crafting.ModuleCrafting;
import cn.academy.crafting.PhaseLiquidGenerator;
import cn.academy.crafting.WorldGenPhaseLiq;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(value = PhaseLiquidGenerator.class, remap = false)
public abstract class MixinPhaseLiquidGenerator_ChunkAligned {

    // Use the existing generator instance from PhaseLiquidGenerator
    @Shadow
    private WorldGenPhaseLiq genLakes;

    /**
     * Keep the original spawn rate and big blob generator, but make it
     * chunk-aligned so it doesn't spill into neighbouring chunks and
     * cause cascading worldgen.
     */
    @Inject(
            method = "generate",
            at = @At("HEAD"),
            cancellable = true
    )
    private void riftflux$chunkAlignedPhaseLakes(Random random,
                                                 int chunkX, int chunkZ,
                                                 World world,
                                                 IChunkProvider chunkGenerator,
                                                 IChunkProvider chunkProvider,
                                                 CallbackInfo ci) {

        // Only override Overworld Imag Phase gen; let other dims fall back
        // to the original method (which also does nothing there).
        if (!ModuleCrafting.GENERATE_PHASE_LIQUID ||
                !world.provider.getDimensionName().equals("Overworld")) {
            return;
        }

        // We fully handle Overworld generation now.
        ci.cancel();

        // Same spawn rate as vanilla AC: ~30% of chunks
        if (random.nextDouble() >= 0.24D) {
            return;
        }

        // CHUNK-ALIGNED:
        // WorldGenPhaseLiq assumes x..x+15, z..z+15 are safe — so give it
        // the exact chunk corner to keep all writes inside this chunk.
        int x = chunkX * 16;
        int z = chunkZ * 16;

        //  vertical randomness
        int y = 5 + random.nextInt(33);

        // This still generates the full "big lake blob", just aligned so it
        // doesn't touch neighbour chunks.
        genLakes.generate(world, random, x, y, z);
    }
}
