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
        if (!ModConfig.allowSugarcaneOnAnyBlock && !ModConfig.allowSugarcaneInWater && !ModConfig.allowHangingSugarcane) {
            return;
        }

        Block below = world.getBlock(x, y - 1, z);
        if (ModConfig.allowSugarcaneInWater && this.riftflux$isWater(world.getBlock(x, y, z))
                && (below == Blocks.reeds || this.riftflux$isValidSugarcaneSupport(below))) {
            cir.setReturnValue(true);
            return;
        }

        if (ModConfig.allowSugarcaneOnAnyBlock && this.riftflux$isValidSugarcaneSupport(below)) {
            cir.setReturnValue(true);
            return;
        }

        if (this.riftflux$isSupportedHangingCane(world, x, y, z)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "canBlockStay", at = @At("HEAD"), cancellable = true)
    private void riftflux$allowSugarcaneStay(World world, int x, int y, int z,
                                             CallbackInfoReturnable<Boolean> cir) {
        if (!ModConfig.allowSugarcaneOnAnyBlock && !ModConfig.allowSugarcaneInWater && !ModConfig.allowHangingSugarcane) {
            return;
        }

        Block below = world.getBlock(x, y - 1, z);
        if (below == Blocks.reeds) {
            cir.setReturnValue(true);
            return;
        }
        if (ModConfig.allowSugarcaneInWater && this.riftflux$isValidSugarcaneSupport(below)
                && this.riftflux$hasWaterBesideColumn(world, x, y, y, z)) {
            cir.setReturnValue(true);
            return;
        }
        if (ModConfig.allowSugarcaneOnAnyBlock && this.riftflux$isValidSugarcaneSupport(below)) {
            cir.setReturnValue(true);
            return;
        }

        if (this.riftflux$isSupportedHangingCane(world, x, y, z)) {
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
        int targetY = y + 1;
        if (!this.riftflux$canGrowInto(world, x, targetY, z)) {
            return;
        }

        int rootY = y;
        while (world.getBlock(x, rootY - 1, z) == Blocks.reeds) {
            --rootY;
        }
        if (!this.riftflux$hasUpwardGrowthWater(world, x, rootY, targetY, z)) {
            return;
        }

        if (this.riftflux$countCaneDownward(world, x, y, z) >= this.riftflux$getMaxTotalHeight()) {
            return;
        }
        if (!this.riftflux$canGrowAboveTopWater(world, x, rootY, targetY, z)) {
            return;
        }

        this.riftflux$growAt(world, x, y, z, x, targetY, z);
    }

    private void riftflux$tryGrowHangingCane(World world, int x, int y, int z) {
        int targetY = y - 1;
        if (!this.riftflux$canGrowInto(world, x, targetY, z)) {
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

        if (this.riftflux$countCaneUpward(world, x, y, z) >= this.riftflux$getMaxTotalHeight()) {
            return;
        }

        this.riftflux$growAt(world, x, y, z, x, targetY, z);
    }

    private boolean riftflux$isHangingCane(World world, int x, int y, int z) {
        return world.getBlock(x, y - 1, z) != Blocks.reeds && this.riftflux$isSupportedHangingCane(world, x, y, z);
    }

    private boolean riftflux$isSupportedHangingCane(World world, int x, int y, int z) {
        if (!ModConfig.allowHangingSugarcane) {
            return false;
        }

        int supportY = y + 1;
        while (world.getBlock(x, supportY, z) == Blocks.reeds) {
            ++supportY;
        }

        Block support = world.getBlock(x, supportY, z);
        return support != Blocks.reeds && this.riftflux$isValidSugarcaneSupport(support);
    }

    private boolean riftflux$hasSugarcaneGrowthTweaks() {
        return ModConfig.allowSugarcaneOnAnyBlock
                || ModConfig.allowSugarcaneInWater
                || ModConfig.sugarcaneMaxHeight != 3
                || ModConfig.sugarcaneMaxHeightAboveTopWaterBlock != 3
                || ModConfig.allowHangingSugarcane
                || ModConfig.sugarcaneGrowsWhenSupportHasBlockBelow;
    }

    private boolean riftflux$hasUpwardGrowthWater(World world, int x, int rootY, int targetY, int z) {
        return this.riftflux$hasVanillaAdjacentWater(world, x, rootY - 1, z)
                || this.riftflux$canGrowFromBlockBelowSupport(world, x, rootY, z)
                || (ModConfig.allowSugarcaneInWater && this.riftflux$hasWaterBesideColumn(world, x, rootY, targetY, z));
    }

    private boolean riftflux$canGrowAboveTopWater(World world, int x, int rootY, int targetY, int z) {
        if (!ModConfig.allowSugarcaneInWater) {
            return true;
        }

        int topWaterY = this.riftflux$getTopWaterYBesideColumn(world, x, rootY - 1, targetY, z);
        if (topWaterY == Integer.MIN_VALUE) {
            return true;
        }

        return Math.max(0, targetY - topWaterY) <= this.riftflux$getMaxHeightAboveTopWaterBlock();
    }

    private boolean riftflux$canGrowFromBlockBelowSupport(World world, int x, int rootY, int z) {
        if (!ModConfig.sugarcaneGrowsWhenSupportHasBlockBelow) {
            return false;
        }

        Block blockBelowSupport = world.getBlock(x, rootY - 2, z);
        return this.riftflux$isValidSugarcaneSupport(blockBelowSupport);
    }

    private boolean riftflux$isValidSugarcaneSupport(Block block) {
        return block != null && block != Blocks.air && !block.getMaterial().isLiquid();
    }

    private boolean riftflux$canGrowInto(World world, int x, int y, int z) {
        return world.isAirBlock(x, y, z) || (ModConfig.allowSugarcaneInWater && this.riftflux$isWater(world.getBlock(x, y, z)));
    }

    private int riftflux$countCaneDownward(World world, int x, int y, int z) {
        int height = 1;
        while (world.getBlock(x, y - height, z) == Blocks.reeds) {
            ++height;
        }
        return height;
    }

    private int riftflux$countCaneUpward(World world, int x, int y, int z) {
        int height = 1;
        while (world.getBlock(x, y + height, z) == Blocks.reeds) {
            ++height;
        }
        return height;
    }

    private int riftflux$getMaxTotalHeight() {
        return Math.max(1, ModConfig.sugarcaneMaxHeight);
    }

    private int riftflux$getMaxHeightAboveTopWaterBlock() {
        return Math.max(0, ModConfig.sugarcaneMaxHeightAboveTopWaterBlock);
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

    private boolean riftflux$hasWaterBesideColumn(World world, int x, int minY, int maxY, int z) {
        for (int y = minY; y <= maxY; ++y) {
            if (this.riftflux$isWater(world.getBlock(x, y, z)) || this.riftflux$hasVanillaAdjacentWater(world, x, y, z)) {
                return true;
            }
        }
        return false;
    }

    private int riftflux$getTopWaterYBesideColumn(World world, int x, int minY, int maxY, int z) {
        int topWaterY = Integer.MIN_VALUE;
        for (int y = minY; y <= maxY; ++y) {
            if (this.riftflux$isWater(world.getBlock(x, y, z)) || this.riftflux$hasVanillaAdjacentWater(world, x, y, z)) {
                topWaterY = y;
            }
        }
        return topWaterY;
    }

    private boolean riftflux$isWater(Block block) {
        return block == Blocks.water || block == Blocks.flowing_water;
    }
}