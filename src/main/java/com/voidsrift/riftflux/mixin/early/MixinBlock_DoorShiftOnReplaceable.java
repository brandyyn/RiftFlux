package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.ModConfig;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Block.class)
public abstract class MixinBlock_DoorShiftOnReplaceable {

    @Inject(method = "onBlockAdded(Lnet/minecraft/world/World;III)V", at = @At("HEAD"))
    private void rf$shiftDoorDownIfReplaceable(World world, int x, int y, int z, CallbackInfo ci) {
        if (!ModConfig.enableDoorAirPlacement) return;
        if (world.isRemote) return;
        if (!((Object)this instanceof BlockDoor)) return;

        BlockDoor door = (BlockDoor)(Object)this;
        int meta = world.getBlockMetadata(x, y, z);
        if ((meta & 8) != 0) return; // only lower half

        Block above = world.getBlock(x, y + 1, z);
        if (above != door) return;
        int aboveMeta = world.getBlockMetadata(x, y + 1, z);
        if ((aboveMeta & 8) == 0) return;

        if (y <= 0) return;
        Block below = world.getBlock(x, y - 1, z);
        if (!rf$isReplaceableForDoor(world, x, y - 1, z, below)) return;

        world.setBlock(x, y - 1, z, door, meta, 2);
        world.setBlock(x, y, z, door, aboveMeta, 2);
        world.setBlockToAir(x, y + 1, z);
    }

    private static boolean rf$isReplaceableForDoor(World world, int x, int y, int z, Block block) {
        int meta = world.getBlockMetadata(x, y, z);
        if (block == Blocks.snow_layer && (meta & 7) < 1) return true;
        if (block == Blocks.vine || block == Blocks.tallgrass || block == Blocks.deadbush) return true;
        return block != Blocks.air && block.isReplaceable(world, x, y, z);
    }
}
