package com.voidsrift.riftflux.mixin.early.legendgear;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.BlockRedstoneComparator;
import net.minecraft.util.Direction;
import net.minecraft.world.World;
import net.nmccoy.legendgear.legacy.LegendGear;
import net.nmccoy.legendgear.legacy.blocks.BlockSwordPedestal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockRedstoneComparator.class)
public class MixinBlockRedstoneComparator_LegacyPedestal {
    @Inject(method = "getInputStrength(Lnet/minecraft/world/World;IIII)I", at = @At("RETURN"), cancellable = true)
    private void riftflux$readLegacyPedestalAboveInput(World world, int x, int y, int z, int metadata, CallbackInfoReturnable<Integer> cir) {
        if (LegendGear.blockPedestal == null || cir.getReturnValueI() >= 15) {
            return;
        }

        int direction = BlockDirectional.getDirection(metadata);
        int inputX = x + Direction.offsetX[direction];
        int inputZ = z + Direction.offsetZ[direction];
        int signal = this.getPedestalSignalAbove(world, inputX, y, inputZ);

        Block inputBlock = world.getBlock(inputX, y, inputZ);
        if (signal < 15 && inputBlock.isNormalCube()) {
            signal = this.getPedestalSignalAbove(
                    world,
                    inputX + Direction.offsetX[direction],
                    y,
                    inputZ + Direction.offsetZ[direction]
            );
        }

        if (signal > cir.getReturnValueI()) {
            cir.setReturnValue(signal);
        }
    }

    private int getPedestalSignalAbove(World world, int x, int y, int z) {
        for (int dy = 1; dy <= 2; dy++) {
            if (world.getBlock(x, y + dy, z) == LegendGear.blockPedestal) {
                return BlockSwordPedestal.getPedestalComparatorSignal(world, x, y + dy, z);
            }
        }
        return 0;
    }
}
