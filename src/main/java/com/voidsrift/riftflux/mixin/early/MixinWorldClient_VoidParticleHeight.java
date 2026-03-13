package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.ModConfig;
import net.minecraft.client.multiplayer.WorldClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(WorldClient.class)
public abstract class MixinWorldClient_VoidParticleHeight {

    @ModifyConstant(
            method = "doVoidFogParticles(III)V",
            constant = @Constant(intValue = 8),
            require = 0
    )
    private int riftflux$adjustVoidParticleStartHeight(int original) {
        return ModConfig.celestialVoidParticleStartHeight;
    }
}
