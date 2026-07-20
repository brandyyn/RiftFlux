package com.voidsrift.riftflux.mixin.early;

import net.minecraft.block.BlockPumpkin;
import net.minecraft.block.material.Material;
import net.minecraft.world.IBlockAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BlockPumpkin.class)
public abstract class MixinBlockPumpkin_AnySupport {

    @Redirect(
            method = "canPlaceBlockAt(Lnet/minecraft/world/World;III)Z",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/World;doesBlockHaveSolidTopSurface(Lnet/minecraft/world/IBlockAccess;III)Z"
            )
    )
    private boolean riftflux$allowAnyNonAirPumpkinSupport(IBlockAccess world, int x, int y, int z) {
        return world.getBlock(x, y, z).getMaterial() != Material.air;
    }
}
