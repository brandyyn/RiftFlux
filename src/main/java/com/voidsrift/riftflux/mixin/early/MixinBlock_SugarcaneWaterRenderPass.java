package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.waterlogging.RiftFluxFluidloggedLookup;
import gravestone.block.BlockGSGraveStone;
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
        if (this.riftflux$usesWaterRenderPass()) {
            cir.setReturnValue(1);
        }
    }

    @Inject(method = "canRenderInPass", at = @At("HEAD"), cancellable = true, remap = false)
    private void riftflux$allowSugarcaneWaterRenderPass(int pass, CallbackInfoReturnable<Boolean> cir) {
        if (this.riftflux$usesWaterRenderPass()) {
            cir.setReturnValue(pass == 0 || pass == 1);
        }
    }

    @Inject(method = "getLightOpacity", at = @At("HEAD"), cancellable = true, remap = false)
    private void riftflux$useWaterLightOpacityForWaterloggedSugarcane(IBlockAccess world, int x, int y, int z,
                                                                      CallbackInfoReturnable<Integer> cir) {
        if (this.riftflux$usesWaterRenderPass()) {
            Block fluid = RiftFluxFluidloggedLookup.getSupportedFluidBlock(world, x, y, z);
            if (fluid != null) {
                cir.setReturnValue(fluid.getLightOpacity(world, x, y, z));
            }
        }
    }

    private boolean riftflux$usesWaterRenderPass() {
        return ModConfig.allowSugarcaneInWater && (Object) this == Blocks.reeds
                || ModConfig.enableGravestoneModule
                && ModConfig.waterlogGravestones
                && (Object) this instanceof BlockGSGraveStone;
    }
}
