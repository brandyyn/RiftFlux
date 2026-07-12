package com.voidsrift.riftflux.mixin.early.fluidlogged;

import com.voidsrift.riftflux.waterlogging.RiftFluxFluidloggedLookup;
import net.minecraft.block.Block;
import net.minecraft.world.IBlockAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "mega.fluidlogged.internal.FLUtil", remap = false)
public abstract class MixinFLUtil_WaterloggedLookup {
    @Inject(method = "getFluidOrBlock", at = @At("HEAD"), cancellable = true, require = 0)
    private static void riftflux$getFluidOrWaterloggedBlock(IBlockAccess world, int x, int y, int z,
                                                            CallbackInfoReturnable<Block> cir) {
        Block fluid = RiftFluxFluidloggedLookup.getSupportedFluidBlock(world, x, y, z);
        if (fluid != null) {
            cir.setReturnValue(fluid);
        }
    }

    @Inject(method = "getFluidMeta", at = @At("HEAD"), cancellable = true, require = 0)
    private static void riftflux$getWaterloggedFluidMeta(IBlockAccess world, int x, int y, int z, int defaultFluidMeta,
                                                         CallbackInfoReturnable<Integer> cir) {
        if (RiftFluxFluidloggedLookup.hasSupportedFluidBlock(world, x, y, z)) {
            cir.setReturnValue(Integer.valueOf(defaultFluidMeta));
        }
    }
}
