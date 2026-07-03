package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.hopper.PulseLockedHopper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHopper;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockHopper.class)
public abstract class MixinBlockHopper_PulseLocked {
    @Inject(method = "onNeighborBlockChange(Lnet/minecraft/world/World;IIILnet/minecraft/block/Block;)V", at = @At("HEAD"))
    private void riftflux$runPulseLockedHopperOnRedstoneEdge(World world, int x, int y, int z, Block neighbor, CallbackInfo ci) {
        if (!ModConfig.pulseLockedHoppers || world == null || world.isRemote) {
            return;
        }

        boolean powered = world.isBlockIndirectlyGettingPowered(x, y, z);
        boolean wasPowered = !BlockHopper.func_149917_c(world.getBlockMetadata(x, y, z));
        if (!powered || wasPowered) {
            return;
        }

        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof PulseLockedHopper) {
            ((PulseLockedHopper) tile).riftflux$runPulseTransfer();
        }
    }
}