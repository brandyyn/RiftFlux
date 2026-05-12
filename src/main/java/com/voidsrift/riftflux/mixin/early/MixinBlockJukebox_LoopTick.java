package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.jukebox.JukeboxLoopHelper;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(Block.class)
public abstract class MixinBlockJukebox_LoopTick {
    @Inject(method = "updateTick", at = @At("HEAD"))
    private void riftflux$runScheduledJukeboxLoop(World world, int x, int y, int z, Random random, CallbackInfo ci) {
        if ((Object) this == Blocks.jukebox) {
            JukeboxLoopHelper.runScheduledLoop(world, x, y, z);
        }
    }
}
