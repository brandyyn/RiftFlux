package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.client.photomode.PhotoModeBlockRenderContext;
import com.voidsrift.riftflux.client.photomode.IsometricPhotoModeController;
import com.voidsrift.riftflux.waterlogging.RiftFluxFluidloggedLookup;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
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
        if (side >= 2
                && side <= 5
                && PhotoModeBlockRenderContext.shouldForceHorizontalNeighbor(x, y, z)) {
            return false;
        }
        if (side >= 2
                && side <= 5
                && IsometricPhotoModeController.instance().isActive()) {
            Minecraft minecraft = Minecraft.getMinecraft();
            World realWorld = minecraft == null ? null : minecraft.theWorld;
            if (realWorld != null
                    && realWorld.blockExists(x, y, z)
                    && RiftFluxFluidloggedLookup.isSameFluid(
                            fluid,
                            RiftFluxFluidloggedLookup.getFluidOrBlock(realWorld, x, y, z)
                    )) {
                return false;
            }
        }
        Block loggedFluid = RiftFluxFluidloggedLookup.getSupportedFluidBlock(world, x, y, z);
        if (RiftFluxFluidloggedLookup.isSameFluid(fluid, loggedFluid)) {
            return false;
        }
        return fluid.shouldSideBeRendered(world, x, y, z, side);
    }
}
