package com.voidsrift.riftflux.mixin.early.angelica;

import com.voidsrift.riftflux.waterlogging.RiftFluxFluidloggedAccess;
import com.voidsrift.riftflux.waterlogging.RiftFluxFluidloggedLookup;
import net.minecraft.block.Block;
import net.minecraft.world.IBlockAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;

@Pseudo
@Mixin(targets = "com.gtnewhorizons.angelica.rendering.celeritas.world.WorldSlice", remap = false)
public abstract class MixinWorldSlice_WaterloggedFluidAccess implements RiftFluxFluidloggedAccess {
    public Block riftflux$getFluidBlock(int x, int y, int z) {
        return RiftFluxFluidloggedLookup.getSupportedFluidBlock((IBlockAccess) (Object) this, x, y, z);
    }
}
