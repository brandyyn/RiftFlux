package com.voidsrift.riftflux.mixin.early.chromaticraft;

import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield;
import com.voidsrift.riftflux.ModConfig;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = BlockStructureShield.class, remap = false)
public abstract class MixinBlockStructureShield_NetherBreakable {

    @Inject(method = "getBlockHardness", at = @At("HEAD"), cancellable = true)
    private void riftflux$makeNetherShieldingStoneBreakLikeObsidian(World world, int x, int y, int z, CallbackInfoReturnable<Float> cir) {
        if (riftflux$isNetherShieldingStoneBreakable(world)) {
            cir.setReturnValue(Blocks.obsidian.getBlockHardness(world, x, y, z));
        }
    }

    @Inject(method = "isUnbreakable", at = @At("HEAD"), cancellable = true)
    private void riftflux$allowNetherShieldingStoneBreak(World world, int x, int y, int z, int meta, CallbackInfoReturnable<Boolean> cir) {
        if (riftflux$isNetherShieldingStoneBreakable(world)) {
            cir.setReturnValue(Boolean.FALSE);
        }
    }

    private static boolean riftflux$isNetherShieldingStoneBreakable(World world) {
        return ModConfig.chromatiCraftNetherStructureShieldBreakableLikeObsidian
                && world != null
                && world.provider != null
                && world.provider.dimensionId == -1;
    }
}
