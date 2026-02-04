package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.ModConfig;
import net.minecraft.block.BlockDoor;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BlockDoor.class)
public abstract class MixinBlockDoor_AirPlacement {

    @Redirect(
            method = "canPlaceBlockAt(Lnet/minecraft/world/World;III)Z",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/World;doesBlockHaveSolidTopSurface(Lnet/minecraft/world/IBlockAccess;III)Z"
            )
    )
    private boolean rf$allowDoorPlacementAnywhere(IBlockAccess world, int x, int y, int z) {
        if (ModConfig.enableDoorAirPlacement) {
            return true;
        }
        return World.doesBlockHaveSolidTopSurface(world, x, y, z);
    }

    @Redirect(
            method = "onNeighborBlockChange(Lnet/minecraft/world/World;IIILnet/minecraft/block/Block;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/World;doesBlockHaveSolidTopSurface(Lnet/minecraft/world/IBlockAccess;III)Z"
            )
    )
    private boolean rf$keepDoorWithoutSupport(IBlockAccess world, int x, int y, int z) {
        if (ModConfig.enableDoorAirPlacement) {
            return true;
        }
        return World.doesBlockHaveSolidTopSurface(world, x, y, z);
    }
}
