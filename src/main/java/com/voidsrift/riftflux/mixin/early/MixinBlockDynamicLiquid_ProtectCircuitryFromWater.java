package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.util.CircuitWaterProtection;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDynamicLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockDynamicLiquid.class)
public abstract class MixinBlockDynamicLiquid_ProtectCircuitryFromWater {
    @Inject(method = "func_149807_p", at = @At("HEAD"), cancellable = true)
    private void riftflux$protectCircuitryFromWater(World world, int x, int y, int z, CallbackInfoReturnable<Boolean> cir) {
        BlockDynamicLiquid self = (BlockDynamicLiquid) (Object) this;
        if (!ModConfig.protectCircuitryFromWater || self.getMaterial() != Material.water) {
            return;
        }

        Block block = world.getBlock(x, y, z);
        if (CircuitWaterProtection.isProtectedCircuit(block)) {
            cir.setReturnValue(true);
        }
    }
}
