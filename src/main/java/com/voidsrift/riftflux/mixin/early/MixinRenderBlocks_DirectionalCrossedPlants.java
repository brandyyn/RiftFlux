package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.mixin.accessor.ChunkCacheAccessor;
import com.voidsrift.riftflux.util.RFPlantContext;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderBlocks.class)
public abstract class MixinRenderBlocks_DirectionalCrossedPlants {

    @Shadow
    public IBlockAccess blockAccess;

    @Inject(method = "drawCrossedSquares", at = @At("HEAD"), cancellable = true)
    private void riftflux$drawDirectionalPlacedPlant(IIcon icon, double x, double y, double z, float scale, CallbackInfo ci) {
        if (!ModConfig.directionalCrossedPlantRenderingByPlacement) {
            return;
        }
        if (icon == null || this.blockAccess == null) {
            return;
        }

        int blockX = MathHelper.floor_double(x + 0.5D);
        int blockY = MathHelper.floor_double(y + 0.5D);
        int blockZ = MathHelper.floor_double(z + 0.5D);
        int anchorY = this.riftflux$getFacingAnchorY(blockX, blockY, blockZ);
        int fallbackFacing = (int) (((long) (blockX * 73428767) ^ (long) (anchorY * 912367) ^ (long) (blockZ * 1315423911)) & 3L);
        World world = this.riftflux$getWorldFromBlockAccess(this.blockAccess);
        int facing = RFPlantContext.getCrossedPlantFacing(world, blockX, anchorY, blockZ, fallbackFacing);

        this.riftflux$drawDirectionalCross(icon, x, y, z, scale, facing);
        ci.cancel();
    }

    @Unique
    private World riftflux$getWorldFromBlockAccess(IBlockAccess access) {
        if (access instanceof World) {
            return (World) access;
        }
        if (access instanceof ChunkCache) {
            return ((ChunkCacheAccessor) access).riftflux$getWorldObj();
        }
        return null;
    }

    @Unique
    private int riftflux$getFacingAnchorY(int x, int y, int z) {
        if (this.blockAccess == null) {
            return y;
        }
        try {
            if (this.blockAccess.getBlock(x, y, z) instanceof BlockDoublePlant) {
                int meta = this.blockAccess.getBlockMetadata(x, y, z);
                if (BlockDoublePlant.func_149887_c(meta)) {
                    return y - 1;
                }
            }
        } catch (Throwable ignored) {
        }
        return y;
    }

    @Unique
    private void riftflux$drawDirectionalCross(IIcon icon, double x, double y, double z, float scale, int facing) {
        double minU = icon.getMinU();
        double minV = icon.getMinV();
        double maxU = icon.getMaxU();
        double maxV = icon.getMaxV();

        double half = 0.45D * scale;
        double centerX = x + 0.5D;
        double centerZ = z + 0.5D;
        double y0 = y;
        double y1 = y + scale;

        double angle = (double) (facing & 3) * (Math.PI / 2.0D) + (Math.PI / 4.0D);
        double dirX = Math.cos(angle);
        double dirZ = Math.sin(angle);

        this.riftflux$drawTwoSidedPlane(centerX, centerZ, y0, y1, half, dirX, dirZ, minU, minV, maxU, maxV);
        this.riftflux$drawTwoSidedPlane(centerX, centerZ, y0, y1, half, -dirZ, dirX, minU, minV, maxU, maxV);
    }

    @Unique
    private void riftflux$drawTwoSidedPlane(double centerX, double centerZ, double y0, double y1, double half,
                                            double dirX, double dirZ,
                                            double minU, double minV, double maxU, double maxV) {
        double x1 = centerX - dirX * half;
        double z1 = centerZ - dirZ * half;
        double x2 = centerX + dirX * half;
        double z2 = centerZ + dirZ * half;

        Tessellator tessellator = Tessellator.instance;

        this.riftflux$addQuad(
                tessellator,
                x1, y1, z1, minU, minV,
                x1, y0, z1, minU, maxV,
                x2, y0, z2, maxU, maxV,
                x2, y1, z2, maxU, minV
        );
        this.riftflux$addQuad(
                tessellator,
                x2, y1, z2, maxU, minV,
                x2, y0, z2, maxU, maxV,
                x1, y0, z1, minU, maxV,
                x1, y1, z1, minU, minV
        );
    }

    @Unique
    private void riftflux$addQuad(Tessellator tessellator,
                                  double x1, double y1, double z1, double u1, double v1,
                                  double x2, double y2, double z2, double u2, double v2,
                                  double x3, double y3, double z3, double u3, double v3,
                                  double x4, double y4, double z4, double u4, double v4) {
        tessellator.addVertexWithUV(x1, y1, z1, u1, v1);
        tessellator.addVertexWithUV(x2, y2, z2, u2, v2);
        tessellator.addVertexWithUV(x3, y3, z3, u3, v3);
        tessellator.addVertexWithUV(x4, y4, z4, u4, v4);
    }
}
