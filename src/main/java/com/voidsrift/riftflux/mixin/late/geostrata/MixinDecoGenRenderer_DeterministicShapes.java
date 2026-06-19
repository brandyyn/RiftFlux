package com.voidsrift.riftflux.mixin.late.geostrata;

import Reika.GeoStrata.Rendering.DecoGenRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = DecoGenRenderer.class, remap = false)
public abstract class MixinDecoGenRenderer_DeterministicShapes {
    @Unique
    private static long riftflux$crystalShapeSeed;

    @Redirect(
            method = "<clinit>",
            at = @At(
                    value = "INVOKE",
                    target = "LReika/DragonAPI/Libraries/Java/ReikaRandomHelper;getRandomBetween(DD)D"
            ),
            require = 0
    )
    private static double riftflux$getDeterministicRandomBetween(double min, double max) {
        return min + (max - min) * riftflux$nextCrystalShapeRandom();
    }

    @Redirect(
            method = "<clinit>",
            at = @At(
                    value = "INVOKE",
                    target = "LReika/DragonAPI/Libraries/Java/ReikaRandomHelper;getRandomPlusMinus(DD)D"
            ),
            require = 0
    )
    private static double riftflux$getDeterministicRandomPlusMinus(double base, double range) {
        return base + (riftflux$nextCrystalShapeRandom() * 2.0D - 1.0D) * range;
    }

    @Unique
    private static double riftflux$nextCrystalShapeRandom() {
        if (riftflux$crystalShapeSeed == 0L) {
            riftflux$crystalShapeSeed = 0x6A09E667F3BCC909L;
        }
        riftflux$crystalShapeSeed = riftflux$crystalShapeSeed * 6364136223846793005L + 1442695040888963407L;
        return (double) (riftflux$crystalShapeSeed >>> 11) * 0x1.0p-53D;
    }
}
