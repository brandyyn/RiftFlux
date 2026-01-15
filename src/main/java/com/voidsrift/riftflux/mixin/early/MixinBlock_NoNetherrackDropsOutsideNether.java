package com.voidsrift.riftflux.mixin.early;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Block.class)
public class MixinBlock_NoNetherrackDropsOutsideNether {

    @Inject(method = "dropBlockAsItemWithChance", at = @At("HEAD"), cancellable = true)
    private void riftflux$blockNetherrackDropsOnlyInNether(
            World world,
            int x, int y, int z,
            int meta,
            float chance,
            int fortune,
            CallbackInfo ci
    ) {
        // Server only
        if (world == null || world.isRemote) return;

        // Only apply to Netherrack
        if ((Object) this != Blocks.netherrack) return;

        // Allow drops ONLY in the Nether dimension (-1)
        if (world.provider == null || world.provider.dimensionId != -1) {
            ci.cancel(); // prevents ANY item entities being spawned (includes Silk Touch)
        }
    }
}
