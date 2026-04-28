package com.voidsrift.riftflux.util;

import com.voidsrift.riftflux.mixin.accessor.angelica.WorldSliceAccessor;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

final class AngelicaWorldSliceBridge {
    private AngelicaWorldSliceBridge() {
    }

    static World getWorld(IBlockAccess access) {
        return ((WorldSliceAccessor) access).riftflux$getWorld();
    }
}
