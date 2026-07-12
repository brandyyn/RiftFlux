package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.ModConfig;
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
        if (riftflux$isProtectedCircuit(block)) {
            cir.setReturnValue(false);
        }
    }

    private static boolean riftflux$isProtectedCircuit(Block block) {
        return block == Blocks.redstone_wire
                || block == Blocks.redstone_torch
                || block == Blocks.unlit_redstone_torch
                || block == Blocks.unpowered_repeater
                || block == Blocks.powered_repeater
                || block == Blocks.unpowered_comparator
                || block == Blocks.powered_comparator
                || block == Blocks.lever
                || block == Blocks.stone_button
                || block == Blocks.wooden_button
                || block == Blocks.stone_pressure_plate
                || block == Blocks.wooden_pressure_plate
                || block == Blocks.light_weighted_pressure_plate
                || block == Blocks.heavy_weighted_pressure_plate
                || block == Blocks.tripwire
                || block == Blocks.tripwire_hook
                || block == Blocks.rail
                || block == Blocks.golden_rail
                || block == Blocks.detector_rail
                || block == Blocks.activator_rail
                || block == Blocks.daylight_detector;
    }
}
