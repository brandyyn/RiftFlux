package com.voidsrift.riftflux.client.render;

import com.voidsrift.riftflux.waterlogging.RiftFluxFluidloggedLookup;
import net.minecraft.world.IBlockAccess;

public final class WaterloggedBlockRenderAccess extends FullWaterBlockRenderer.WaterloggedBlockAccess {
    public WaterloggedBlockRenderAccess(IBlockAccess delegate) {
        super(delegate);
    }

    public boolean isWaterloggedAt(int x, int y, int z) {
        return RiftFluxFluidloggedLookup.hasSupportedFluidBlock(this.delegate, x, y, z);
    }
}
