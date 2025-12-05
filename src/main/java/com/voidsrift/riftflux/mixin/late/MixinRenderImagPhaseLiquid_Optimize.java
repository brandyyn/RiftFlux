package com.voidsrift.riftflux.mixin.late;

import cn.academy.crafting.client.render.block.RenderImagPhaseLiquid;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidClassic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = RenderImagPhaseLiquid.class, remap = false)
public abstract class MixinRenderImagPhaseLiquid_Optimize {

    // Max distance at which Imag Phase Liquid will even try to render.
    private static final double RIFTFLUX$MAX_RENDER_DIST_SQ = 16.0D * 16.0D;

    /**
     * HEAD inject into the actual runtime name:
     * func_147500_a(TileEntity, double, double, double, float)
     */
    @Inject(
            method = "func_147500_a",
            at = @At("HEAD"),
            cancellable = true
    )
    private void riftflux$skipHiddenOrFarImagLiquid(TileEntity te,
                                                    double x, double y, double z,
                                                    float partialTicks,
                                                    CallbackInfo ci) {
        if (te == null) {
            return;
        }

        // Only touch the AcademyCraft liquid TESR, ignore others
        if (!(te.getBlockType() instanceof BlockFluidClassic)) {
            return;
        }

        World world = te.getWorldObj();
        if (world == null) {
            return;
        }

        Block block = te.getBlockType();
        final int bx = te.xCoord;
        final int by = te.yCoord;
        final int bz = te.zCoord;

        // 1) Interior cull: if the block ABOVE is the same fluid, this tile's
        // surface is completely hidden and doesn't need to render at all.
        if (by < world.getHeight() - 1) {
            Block above = world.getBlock(bx, by + 1, bz);
            if (above == block) {
                ci.cancel();
                return;
            }
        }

        // 2) Distance-based culling to avoid rendering huge pools far away
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer player = (mc != null) ? mc.thePlayer : null;
        if (player == null) {
            return;
        }

        double distSq = player.getDistanceSq(
                bx + 0.5D,
                by + 0.5D,
                bz + 0.5D
        );

        if (distSq > RIFTFLUX$MAX_RENDER_DIST_SQ) {
            // Too far away – completely skip this TESR.
            ci.cancel();
        }
    }
}
