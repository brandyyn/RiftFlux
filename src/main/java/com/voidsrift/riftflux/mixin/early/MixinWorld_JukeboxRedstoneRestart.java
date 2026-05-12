package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.jukebox.JukeboxLoopHelper;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(World.class)
public abstract class MixinWorld_JukeboxRedstoneRestart {
    @Inject(
            method = "notifyBlockOfNeighborChange(IIILnet/minecraft/block/Block;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/Block;onNeighborBlockChange(Lnet/minecraft/world/World;IIILnet/minecraft/block/Block;)V"
            ),
            locals = LocalCapture.CAPTURE_FAILSOFT
    )
    private void riftflux$restartJukeboxOnRedstone(int x, int y, int z, Block neighbor, CallbackInfo ci, Block block) {
        if (!ModConfig.jukeboxRedstoneRestartEnabled) {
            return;
        }

        if (block == Blocks.jukebox) {
            JukeboxLoopHelper.handleRedstoneChange((World) (Object) this, x, y, z);
        }
    }
}
