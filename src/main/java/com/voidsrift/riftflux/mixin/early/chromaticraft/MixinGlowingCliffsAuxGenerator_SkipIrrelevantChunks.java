package com.voidsrift.riftflux.mixin.early.chromaticraft;

import Reika.ChromatiCraft.World.BiomeGlowingCliffs;
import Reika.ChromatiCraft.World.IWG.GlowingCliffsAuxGenerator;
import Reika.DragonAPI.Libraries.World.ReikaChunkHelper;
import com.voidsrift.riftflux.ModConfig;
import java.util.Random;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = GlowingCliffsAuxGenerator.class, remap = false)
public abstract class MixinGlowingCliffsAuxGenerator_SkipIrrelevantChunks {

    @Inject(method = "generate", at = @At("HEAD"), cancellable = true, remap = false)
    private void riftflux$skipChunksWithoutGlowingCliffs(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider, CallbackInfo ci) {
        if (!ModConfig.optimizeChromatiCraftCliffsChunkGeneration) {
            return;
        }
        if (!ReikaChunkHelper.chunkContainsBiomeTypeBlockCoords(world, chunkX * 16, chunkZ * 16, BiomeGlowingCliffs.class)) {
            ci.cancel();
        }
    }
}
