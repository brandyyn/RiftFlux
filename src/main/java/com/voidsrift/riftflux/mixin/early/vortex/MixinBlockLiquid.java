package com.voidsrift.riftflux.mixin.early.vortex;

import com.voidsrift.riftflux.waterlogging.RiftFluxFluidloggedLookup;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.world.IBlockAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({BlockLiquid.class})
public abstract class MixinBlockLiquid {
    @Inject(method = "shouldSideBeRendered", at = @At("HEAD"), cancellable = true, require = 0)
    private void riftflux$hideSideAgainstWaterloggedFluid(IBlockAccess world, int x, int y, int z, int side,
                                                          CallbackInfoReturnable<Boolean> cir) {
        Block fluid = RiftFluxFluidloggedLookup.getSupportedFluidBlock(world, x, y, z);
        if (fluid != null) {
            Material material = ((Block) (Object) this).getMaterial();
            if (fluid.getMaterial() == material) {
                cir.setReturnValue(false);
            }
        }
    }

    @Redirect(
            method = "getEffectiveFlowDecay",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/IBlockAccess;getBlock(III)Lnet/minecraft/block/Block;"),
            require = 0)
    private Block riftflux$getFluidOrBlockForFlowDecay(IBlockAccess world, int x, int y, int z) {
        return RiftFluxFluidloggedLookup.getFluidOrBlock(world, x, y, z);
    }

    @Redirect(
            method = "getEffectiveFlowDecay",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/IBlockAccess;getBlockMetadata(III)I"),
            require = 0)
    private int riftflux$getFluidMetaForFlowDecay(IBlockAccess world, int x, int y, int z) {
        return RiftFluxFluidloggedLookup.getFluidMetaOrBlockMeta(world, x, y, z, 0);
    }
}
