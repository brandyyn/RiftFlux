package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.ModConfig;
import net.minecraft.block.Block;
import net.minecraft.block.BlockTorch;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockTorch.class)
public abstract class MixinBlockTorch_AnySupport {

    @Inject(method = "canPlaceBlockAt", at = @At("HEAD"), cancellable = true)
    private void riftflux$canPlaceOnAnyAdjacentBlock(World world, int x, int y, int z,
                                                     CallbackInfoReturnable<Boolean> cir) {
        if (ModConfig.allowTorchesOnAnyBlock && this.riftflux$hasAnySupport(world, x, y, z)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "onBlockPlaced", at = @At("HEAD"), cancellable = true)
    private void riftflux$placeAgainstAnySupport(World world, int x, int y, int z, int side,
                                                 float hitX, float hitY, float hitZ, int meta,
                                                 CallbackInfoReturnable<Integer> cir) {
        if (!ModConfig.allowTorchesOnAnyBlock || !this.riftflux$hasSupportForSide(world, x, y, z, side)) {
            return;
        }

        if (side == 1) {
            cir.setReturnValue(5);
        } else if (side == 2) {
            cir.setReturnValue(4);
        } else if (side == 3) {
            cir.setReturnValue(3);
        } else if (side == 4) {
            cir.setReturnValue(2);
        } else if (side == 5) {
            cir.setReturnValue(1);
        }
    }

    @Inject(method = "onBlockAdded", at = @At("HEAD"), cancellable = true)
    private void riftflux$chooseAnySupportOnAdd(World world, int x, int y, int z, CallbackInfo ci) {
        if (!ModConfig.allowTorchesOnAnyBlock || world.getBlockMetadata(x, y, z) != 0) {
            return;
        }

        int meta = this.riftflux$getFirstSupportMeta(world, x, y, z);
        if (meta > 0) {
            world.setBlockMetadataWithNotify(x, y, z, meta, 2);
            ci.cancel();
        }
    }

    @Inject(method = "onNeighborBlockChange", at = @At("HEAD"), cancellable = true)
    private void riftflux$stayOnAnySupport(World world, int x, int y, int z, Block neighbor,
                                           CallbackInfo ci) {
        if (ModConfig.allowTorchesOnAnyBlock && this.riftflux$hasSupportForMeta(world, x, y, z)) {
            ci.cancel();
        }
    }

    @Unique
    private boolean riftflux$hasAnySupport(World world, int x, int y, int z) {
        return this.riftflux$isSupportBlock(world, x - 1, y, z)
                || this.riftflux$isSupportBlock(world, x + 1, y, z)
                || this.riftflux$isSupportBlock(world, x, y, z - 1)
                || this.riftflux$isSupportBlock(world, x, y, z + 1)
                || this.riftflux$isSupportBlock(world, x, y - 1, z);
    }

    @Unique
    private boolean riftflux$hasSupportForSide(World world, int x, int y, int z, int side) {
        if (side == 1) {
            return this.riftflux$isSupportBlock(world, x, y - 1, z);
        }
        if (side == 2) {
            return this.riftflux$isSupportBlock(world, x, y, z + 1);
        }
        if (side == 3) {
            return this.riftflux$isSupportBlock(world, x, y, z - 1);
        }
        if (side == 4) {
            return this.riftflux$isSupportBlock(world, x + 1, y, z);
        }
        if (side == 5) {
            return this.riftflux$isSupportBlock(world, x - 1, y, z);
        }
        return false;
    }

    @Unique
    private int riftflux$getFirstSupportMeta(World world, int x, int y, int z) {
        if (this.riftflux$isSupportBlock(world, x - 1, y, z)) {
            return 1;
        }
        if (this.riftflux$isSupportBlock(world, x + 1, y, z)) {
            return 2;
        }
        if (this.riftflux$isSupportBlock(world, x, y, z - 1)) {
            return 3;
        }
        if (this.riftflux$isSupportBlock(world, x, y, z + 1)) {
            return 4;
        }
        if (this.riftflux$isSupportBlock(world, x, y - 1, z)) {
            return 5;
        }
        return 0;
    }

    @Unique
    private boolean riftflux$hasSupportForMeta(World world, int x, int y, int z) {
        int meta = world.getBlockMetadata(x, y, z);
        if (meta == 1) {
            return this.riftflux$isSupportBlock(world, x - 1, y, z);
        }
        if (meta == 2) {
            return this.riftflux$isSupportBlock(world, x + 1, y, z);
        }
        if (meta == 3) {
            return this.riftflux$isSupportBlock(world, x, y, z - 1);
        }
        if (meta == 4) {
            return this.riftflux$isSupportBlock(world, x, y, z + 1);
        }
        return this.riftflux$isSupportBlock(world, x, y - 1, z);
    }

    @Unique
    private boolean riftflux$isSupportBlock(World world, int x, int y, int z) {
        if (world == null || y < 0 || y >= world.getHeight()) {
            return false;
        }
        Block block = world.getBlock(x, y, z);
        return block != null && block != Blocks.air;
    }
}
