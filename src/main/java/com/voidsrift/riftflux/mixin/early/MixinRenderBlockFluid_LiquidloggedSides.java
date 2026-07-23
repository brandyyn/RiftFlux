package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.waterlogging.RiftFluxFluidloggedLookup;
import net.minecraft.block.Block;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fluids.RenderBlockFluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = RenderBlockFluid.class, remap = false)
public abstract class MixinRenderBlockFluid_LiquidloggedSides {
    @Redirect(
            method = "renderWorldBlock",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/Block;shouldSideBeRendered(Lnet/minecraft/world/IBlockAccess;IIII)Z",
                    remap = true
            ),
            require = 0
    )
    private boolean riftflux$hideRealFluidSideAgainstLiquidloggedHost(Block fluid, IBlockAccess world,
                                                                      int x, int y, int z, int side) {
        Block loggedFluid = RiftFluxFluidloggedLookup.getSupportedFluidBlock(world, x, y, z);
        if (RiftFluxFluidloggedLookup.isSameFluid(fluid, loggedFluid)) {
            return false;
        }
        return fluid.shouldSideBeRendered(world, x, y, z, side);
    }
}
