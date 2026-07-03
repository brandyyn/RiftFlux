package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.client.render.WaterloggingRenderDebug;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.IBlockAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Block.class)
public abstract class MixinBlock_SugarcaneWaterRenderPass {
    @Inject(method = "getRenderBlockPass", at = @At("HEAD"), cancellable = true)
    private void riftflux$markSugarcaneForWaterRenderPass(CallbackInfoReturnable<Integer> cir) {
        if (ModConfig.allowSugarcaneInWater && (Object) this == Blocks.reeds) {
            cir.setReturnValue(0);
        }
    }

    @Inject(method = "canRenderInPass", at = @At("HEAD"), cancellable = true, remap = false)
    private void riftflux$allowSugarcaneWaterRenderPass(int pass, CallbackInfoReturnable<Boolean> cir) {
        if (ModConfig.allowSugarcaneInWater && (Object) this == Blocks.reeds) {
            WaterloggingRenderDebug.sugarcanePassHook(pass);
            cir.setReturnValue(pass == 0 || pass == 1);
        }
    }

    @Inject(method = "getLightOpacity", at = @At("HEAD"), cancellable = true, remap = false)
    private void riftflux$useWaterLightOpacityForWaterloggedSugarcane(IBlockAccess world, int x, int y, int z,
                                                                      CallbackInfoReturnable<Integer> cir) {
        if (ModConfig.allowSugarcaneInWater
                && (Object) this == Blocks.reeds
                && this.riftflux$isWaterloggedSugarcane(world, x, y, z)) {
            cir.setReturnValue(Blocks.water.getLightOpacity(world, x, y, z));
        }
    }

    @Unique
    private boolean riftflux$isWaterloggedSugarcane(IBlockAccess world, int x, int y, int z) {
        return this.riftflux$isWater(world.getBlock(x, y + 1, z))
                || this.riftflux$isWater(world.getBlock(x - 1, y, z))
                || this.riftflux$isWater(world.getBlock(x + 1, y, z))
                || this.riftflux$isWater(world.getBlock(x, y, z - 1))
                || this.riftflux$isWater(world.getBlock(x, y, z + 1));
    }

    @Unique
    private boolean riftflux$isWater(Block block) {
        return block != null && block.getMaterial() == net.minecraft.block.material.Material.water;
    }
}
