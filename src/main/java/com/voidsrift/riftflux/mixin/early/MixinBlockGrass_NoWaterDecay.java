package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.ModConfig;
import net.minecraft.block.Block;
import net.minecraft.block.BlockGrass;
import net.minecraft.block.material.Material;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(BlockGrass.class)
public abstract class MixinBlockGrass_NoWaterDecay {
    @Inject(method = "updateTick", at = @At("HEAD"), cancellable = true)
    private void riftflux$skipWaterCoveredGrassDecay(World world, int x, int y, int z, Random random, CallbackInfo ci) {
        if (!ModConfig.preventWaterGrassDecay || world.isRemote) {
            return;
        }

        Block above = world.getBlock(x, y + 1, z);
        if (above != null && above.getMaterial() == Material.water) {
            ci.cancel();
        }
    }
}
