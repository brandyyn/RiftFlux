package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.waterlogging.RiftFluxFluidloggedLookup;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(RenderBlocks.class)
public abstract class MixinRenderBlocks_WaterloggedFluidLookup {
    @Redirect(
            method = "getLiquidHeight",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/IBlockAccess;getBlock(III)Lnet/minecraft/block/Block;"),
            require = 0)
    private Block riftflux$getFluidOrBlockForLiquidHeight(IBlockAccess world, int x, int y, int z) {
        return RiftFluxFluidloggedLookup.getFluidOrBlock(world, x, y, z);
    }

    @Redirect(
            method = "getLiquidHeight",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/IBlockAccess;getBlockMetadata(III)I"),
            require = 0)
    private int riftflux$getFluidMetaForLiquidHeight(IBlockAccess world, int x, int y, int z) {
        return RiftFluxFluidloggedLookup.getFluidMetaOrBlockMeta(world, x, y, z, 0);
    }
}
