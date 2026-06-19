package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.ModConfig;
import net.minecraft.block.Block;
import net.minecraft.block.BlockReed;
import net.minecraft.init.Blocks;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Mixin(BlockReed.class)
public abstract class MixinBlockReed {

    @Inject(method = "colorMultiplier", at = @At("HEAD"), cancellable = true)
    private void riftflux$disableSugarcaneTint(IBlockAccess world, int x, int y, int z,
                                               CallbackInfoReturnable<Integer> cir) {
        if (ModConfig.disableTintedSugarcane) {
            cir.setReturnValue(0xFFFFFF);
        }
    }

    @Inject(method = "canPlaceBlockAt", at = @At("HEAD"), cancellable = true)
    private void riftflux$allowSugarcanePlacement(World world, int x, int y, int z,
                                                  CallbackInfoReturnable<Boolean> cir) {
        if (!ModConfig.allowSugarcaneOnAnyBlock && !ModConfig.allowHangingSugarcane) {
            return;
        }

        Block below = world.getBlock(x, y - 1, z);
        if (ModConfig.allowSugarcaneOnAnyBlock && below != null && below != Blocks.air) {
            cir.setReturnValue(true);
            return;
        }

        Block above = world.getBlock(x, y + 1, z);
        if (ModConfig.allowHangingSugarcane && above != null && above != Blocks.air) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "canBlockStay", at = @At("HEAD"), cancellable = true)
    private void riftflux$allowSugarcaneStay(World world, int x, int y, int z,
                                             CallbackInfoReturnable<Boolean> cir) {
        if (!ModConfig.allowSugarcaneOnAnyBlock && !ModConfig.allowHangingSugarcane) {
            return;
        }

        Block below = world.getBlock(x, y - 1, z);
        if (below == Blocks.reeds) {
            cir.setReturnValue(true);
            return;
        }
        if (ModConfig.allowSugarcaneOnAnyBlock && below != null && below != Blocks.air) {
            cir.setReturnValue(true);
            return;
        }

        Block above = world.getBlock(x, y + 1, z);
        if (ModConfig.allowHangingSugarcane && above != null && above != Blocks.air) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "updateTick", at = @At("HEAD"), cancellable = true)
    private void riftflux$controlledSugarcaneGrowth(World world, int x, int y, int z, Random random,
                                                   CallbackInfo ci) {
        if (!this.riftflux$hasSugarcaneGrowthTweaks()) {
            return;
        }

        if (!((BlockReed) (Object) this).canBlockStay(world, x, y, z)) {
            return;
        }

        ci.cancel();
        if (ModConfig.allowHangingSugarcane && this.riftflux$isHangingCane(world, x, y, z)) {
            this.riftflux$tryGrowHangingCane(world, x, y, z);
        } else {
            this.riftflux$tryGrowUpwardCane(world, x, y, z);
        }
    }

    private void riftflux$tryGrowUpwardCane(World world, int x, int y, int z) {
        if (!world.isAirBlock(x, y + 1, z)) {
            return;
        }

        int rootY = y;
        while (world.getBlock(x, rootY - 1, z) == Blocks.reeds) {
            --rootY;
        }
        if (!this.riftflux$hasVanillaAdjacentWater(world, x, rootY - 1, z)
                && !this.riftflux$canGrowFromBlockBelowSupport(world, x, rootY, z)) {
            return;
        }

        int height = 1;
        while (world.getBlock(x, y - height, z) == Blocks.reeds) {
            ++height;
        }
        if (height >= 3) {
            return;
        }

        this.riftflux$growAt(world, x, y, z, x, y + 1, z);
    }

    private void riftflux$tryGrowHangingCane(World world, int x, int y, int z) {
        if (!world.isAirBlock(x, y - 1, z)) {
            return;
        }

        int rootY = y;
        while (world.getBlock(x, rootY + 1, z) == Blocks.reeds) {
            ++rootY;
        }

        int supportY = rootY + 1;
        if (!ModConfig.hangingSugarcaneGrowsWithWaterAboveSupport) {
            return;
        }
        if (!this.riftflux$isWater(world.getBlock(x, supportY + 1, z))) {
            return;
        }

        int height = 1;
        while (world.getBlock(x, y + height, z) == Blocks.reeds) {
            ++height;
        }
        if (height >= 3) {
            return;
        }

        this.riftflux$growAt(world, x, y, z, x, y - 1, z);
    }

    private boolean riftflux$isHangingCane(World world, int x, int y, int z) {
        Block above = world.getBlock(x, y + 1, z);
        return above == Blocks.reeds || (above != null && above != Blocks.air && world.getBlock(x, y - 1, z) != Blocks.reeds);
    }

    private boolean riftflux$hasSugarcaneGrowthTweaks() {
        return ModConfig.allowSugarcaneOnAnyBlock
                || ModConfig.allowHangingSugarcane
                || ModConfig.sugarcaneGrowsWhenSupportHasBlockBelow;
    }

    private boolean riftflux$canGrowFromBlockBelowSupport(World world, int x, int rootY, int z) {
        if (!ModConfig.sugarcaneGrowsWhenSupportHasBlockBelow) {
            return false;
        }

        Block blockBelowSupport = world.getBlock(x, rootY - 2, z);
        return blockBelowSupport != null && blockBelowSupport != Blocks.air;
    }

    private void riftflux$growAt(World world, int sourceX, int sourceY, int sourceZ, int targetX, int targetY, int targetZ) {
        int meta = world.getBlockMetadata(sourceX, sourceY, sourceZ);
        if (meta >= 15) {
            world.setBlock(targetX, targetY, targetZ, (Block) (Object) this);
            world.setBlockMetadataWithNotify(sourceX, sourceY, sourceZ, 0, 4);
        } else {
            world.setBlockMetadataWithNotify(sourceX, sourceY, sourceZ, meta + 1, 4);
        }
    }

    private boolean riftflux$hasVanillaAdjacentWater(World world, int x, int y, int z) {
        return this.riftflux$isWater(world.getBlock(x - 1, y, z))
                || this.riftflux$isWater(world.getBlock(x + 1, y, z))
                || this.riftflux$isWater(world.getBlock(x, y, z - 1))
                || this.riftflux$isWater(world.getBlock(x, y, z + 1));
    }

    private boolean riftflux$isWater(Block block) {
        return block == Blocks.water || block == Blocks.flowing_water;
    }
}
