package com.voidsrift.riftflux.mixin.early;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.init.Blocks;
import net.minecraft.world.IBlockAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockFence.class)
public abstract class MixinBlockFence_NoPumpkinConnections {
    @Inject(method = "canConnectFenceTo", at = @At("HEAD"), cancellable = true)
    private void riftflux$preventPumpkinFenceConnections(IBlockAccess world, int x, int y, int z, CallbackInfoReturnable<Boolean> cir) {
        Block block = world.getBlock(x, y, z);
        if (block == Blocks.pumpkin || block == Blocks.lit_pumpkin) {
            cir.setReturnValue(false);
        }
    }
}
