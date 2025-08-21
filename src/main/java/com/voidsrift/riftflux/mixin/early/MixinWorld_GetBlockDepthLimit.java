package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.ModConfig;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Returns air if getBlock recursion goes too deep (prevents SOE). */
@Mixin(World.class)
public abstract class MixinWorld_GetBlockDepthLimit {

    @Unique private static final ThreadLocal<Integer> RF$GB_DEPTH =
            new ThreadLocal<Integer>() { @Override protected Integer initialValue() { return 0; } };

    // Hook both MCP and SRG names
    @Inject(method = {
            "getBlock(III)Lnet/minecraft/block/Block;",
    },
            at = @At("HEAD"), cancellable = true)
    private void rf$capDepthHead(int x, int y, int z, CallbackInfoReturnable<Block> cir) {
        if (!ModConfig.enableStackOverflowGuard) return;
        final int limit = Math.max(32, ModConfig.stackOverflowMaxDepth);
        int d = RF$GB_DEPTH.get();
        if (d >= limit) {
            cir.setReturnValue(Blocks.air);
            cir.cancel();
            return;
        }
        RF$GB_DEPTH.set(d + 1);
    }

    @Inject(method = {
            "getBlock(III)Lnet/minecraft/block/Block;",
    },
            at = @At("RETURN"))
    private void rf$capDepthReturn(int x, int y, int z, CallbackInfoReturnable<Block> cir) {
        if (!ModConfig.enableStackOverflowGuard) return;
        int d = RF$GB_DEPTH.get();
        if (d > 0) RF$GB_DEPTH.set(d - 1);
    }
}
