package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.ModConfig;
import net.minecraft.client.renderer.EntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(EntityRenderer.class)
public abstract class MixinEntityRenderer_VoidFogHeight {

    @ModifyConstant(
            method = "setupFog(IF)V",
            constant = @Constant(doubleValue = 32.0D),
            require = 0
    )
    private double riftflux$adjustVoidFogHeightDivisor(double original) {
        return Math.max(1.0D, (double) ModConfig.celestialVoidFogStartHeight + 4.0D);
    }
}
