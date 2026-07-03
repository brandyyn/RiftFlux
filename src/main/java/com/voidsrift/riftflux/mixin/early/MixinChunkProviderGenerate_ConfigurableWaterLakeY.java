package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.ModConfig;
import net.minecraft.world.gen.ChunkProviderGenerate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ChunkProviderGenerate.class)
public abstract class MixinChunkProviderGenerate_ConfigurableWaterLakeY {
    @ModifyArg(
            method = "populate",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/gen/feature/WorldGenLakes;generate(Lnet/minecraft/world/World;Ljava/util/Random;III)Z",
                    ordinal = 0
            ),
            index = 3
    )
    private int riftflux$configurableWaterLakeY(int originalY) {
        int min = riftflux$clampLakeY(ModConfig.waterLakeMinY);
        int max = riftflux$clampLakeY(ModConfig.waterLakeMaxY);
        if (max < min) {
            int swap = min;
            min = max;
            max = swap;
        }
        if (max == min) {
            return min;
        }

        int normalizedOriginal = originalY < 0 ? 0 : Math.min(originalY, 255);
        return min + (int) ((long) normalizedOriginal * (max - min) / 255L);
    }

    private static int riftflux$clampLakeY(int y) {
        if (y < 5) {
            return 5;
        }
        return Math.min(y, 255);
    }
}