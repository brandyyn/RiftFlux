package com.voidsrift.riftflux.mixin.early;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLeavesBase;
import net.minecraft.block.material.Material;
import net.minecraft.world.IBlockAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Block.class)
public abstract class MixinBlock_LeafLightOpacity {

    @Inject(method = "getUseNeighborBrightness()Z", at = @At("HEAD"), cancellable = true, remap = false)
    private void riftflux$useNeighborBrightnessForLeaves(CallbackInfoReturnable<Boolean> cir) {
        if (riftflux$isLeafBlock()) {
            cir.setReturnValue(Boolean.TRUE);
        }
    }

    @Inject(method = "getAmbientOcclusionLightValue()F", at = @At("HEAD"), cancellable = true)
    private void riftflux$disableLeafAmbientOcclusion(CallbackInfoReturnable<Float> cir) {
        if (riftflux$isLeafBlock()) {
            cir.setReturnValue(Float.valueOf(1.0F));
        }
    }

    private boolean riftflux$isLeafBlock() {
        Block block = (Block) (Object) this;
        return block instanceof BlockLeavesBase || block.getMaterial() == Material.leaves;
    }
}
