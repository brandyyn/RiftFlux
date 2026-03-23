package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.util.CircuitWaterProtection;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBucket;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemBucket.class)
public abstract class MixinItemBucket_ProtectCircuitryFromWater {
    @Shadow @Final private Block isFull;

    @Inject(method = "tryPlaceContainedLiquid", at = @At("HEAD"), cancellable = true)
    private void riftflux$preventWaterBucketBreakingCircuitry(World world, int x, int y, int z, CallbackInfoReturnable<Boolean> cir) {
        if (!ModConfig.protectCircuitryFromWater) {
            return;
        }
        if (this.isFull != Blocks.flowing_water && this.isFull != Blocks.water) {
            return;
        }

        Block block = world.getBlock(x, y, z);
        if (CircuitWaterProtection.isProtectedCircuit(block)) {
            cir.setReturnValue(false);
        }
    }
}
