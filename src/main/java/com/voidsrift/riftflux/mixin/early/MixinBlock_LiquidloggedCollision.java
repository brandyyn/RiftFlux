package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.waterlogging.RiftFluxFluidloggedLookup;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Block.class)
public abstract class MixinBlock_LiquidloggedCollision {
    @Inject(method = "onEntityCollidedWithBlock", at = @At("HEAD"))
    private void riftflux$forwardLiquidloggedCollision(World world, int x, int y, int z, Entity entity,
                                                        CallbackInfo ci) {
        Block host = (Block) (Object) this;
        int metadata = world.getBlockMetadata(x, y, z);
        if (!RiftFluxFluidloggedLookup.isSupportedFluidHost(host, metadata)) {
            return;
        }
        Block fluid = RiftFluxFluidloggedLookup.getSupportedFluidBlock(world, x, y, z);
        if (fluid != null && fluid != host) {
            fluid.onEntityCollidedWithBlock(world, x, y, z, entity);
        }
    }
}
