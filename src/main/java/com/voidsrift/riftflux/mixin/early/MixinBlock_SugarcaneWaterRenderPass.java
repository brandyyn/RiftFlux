package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.waterlogging.RiftFluxFluidloggedLookup;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.IBlockAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Block.class)
public abstract class MixinBlock_SugarcaneWaterRenderPass {
    @Inject(method = "getRenderBlockPass", at = @At("HEAD"), cancellable = true)
    private void riftflux$markSugarcaneForWaterRenderPass(CallbackInfoReturnable<Integer> cir) {
        if (ModConfig.allowSugarcaneInWater && (Object) this == Blocks.reeds) {
            cir.setReturnValue(1);
        }
    }

    @Inject(method = "canRenderInPass", at = @At("HEAD"), cancellable = true, remap = false)
    private void riftflux$allowSugarcaneWaterRenderPass(int pass, CallbackInfoReturnable<Boolean> cir) {
        if (ModConfig.allowSugarcaneInWater && (Object) this == Blocks.reeds) {
            cir.setReturnValue(pass == 0 || pass == 1);
        }
    }

    @Inject(method = "getLightOpacity", at = @At("HEAD"), cancellable = true, remap = false)
    private void riftflux$useWaterLightOpacityForWaterloggedSugarcane(IBlockAccess world, int x, int y, int z,
                                                                      CallbackInfoReturnable<Integer> cir) {
        if (ModConfig.allowSugarcaneInWater
                && (Object) this == Blocks.reeds
                && RiftFluxFluidloggedLookup.hasSupportedFluidBlock(world, x, y, z)) {
            cir.setReturnValue(Blocks.water.getLightOpacity(world, x, y, z));
        }
    }
}
