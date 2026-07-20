package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.waterlogging.RiftFluxFluidloggedLookup;
import net.minecraft.block.Block;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(World.class)
public abstract class MixinWorld_SupportedFluidLookup {
    @Redirect(
            method = {"handleMaterialAcceleration", "isAABBInMaterial", "isMaterialInBB"},
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/World;getBlock(III)Lnet/minecraft/block/Block;"),
            require = 0
    )
    private Block riftflux$getSupportedFluidBlock(World world, int x, int y, int z) {
        return RiftFluxFluidloggedLookup.getSupportedFluidOrBlock(world, x, y, z);
    }

    @Redirect(
            method = {"handleMaterialAcceleration", "isAABBInMaterial", "isMaterialInBB"},
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/World;getBlockMetadata(III)I"),
            require = 0
    )
    private int riftflux$getSupportedFluidMetadata(World world, int x, int y, int z) {
        return RiftFluxFluidloggedLookup.getSupportedFluidMetaOrBlockMeta(world, x, y, z, 0);
    }
}
