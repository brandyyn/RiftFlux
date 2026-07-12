package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.waterlogging.RiftFluxFluidloggedAccess;
import com.voidsrift.riftflux.waterlogging.RiftFluxFluidloggedLookup;
import net.minecraft.block.Block;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.IBlockAccess;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ChunkCache.class)
public abstract class MixinChunkCache_WaterloggedFluidAccess implements RiftFluxFluidloggedAccess {
    public Block riftflux$getFluidBlock(int x, int y, int z) {
        return RiftFluxFluidloggedLookup.getSupportedFluidBlock((IBlockAccess) (Object) this, x, y, z);
    }
}
