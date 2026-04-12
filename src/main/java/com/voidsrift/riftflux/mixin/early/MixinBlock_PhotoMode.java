package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.client.photomode.IsometricPhotoModeController;
import net.minecraft.block.Block;
import net.minecraft.world.IBlockAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(net.minecraft.block.Block.class)
public abstract class MixinBlock_PhotoMode {

    @Inject(method = "shouldSideBeRendered", at = @At("HEAD"), cancellable = true)
    private void riftflux$disableFaceCullingInPhotoMode(IBlockAccess world, int x, int y, int z, int side, CallbackInfoReturnable<Boolean> cir) {
        Block block = (Block) (Object) this;
        if (IsometricPhotoModeController.instance().isActive()
                && block.renderAsNormalBlock()
                && block.isOpaqueCube()
                && !block.getMaterial().isLiquid()) {
            Block adjacent = world.getBlock(x, y, z);
            cir.setReturnValue(Boolean.valueOf(!adjacent.isOpaqueCube()));
        }
    }
}
