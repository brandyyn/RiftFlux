package com.voidsrift.riftflux.mixin.late.geostrata;

import Reika.GeoStrata.Blocks.BlockDecoGen;
import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.waterlogging.RiftFluxCrystalSpikeAccess;
import com.voidsrift.riftflux.waterlogging.RiftFluxFluidloggedLookup;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = BlockDecoGen.class, remap = false)
public abstract class MixinBlockDecoGen_CrystalSpikeOrientation implements RiftFluxCrystalSpikeAccess {
    @Unique
    private static final int riftflux$ORIENTATION_MASK = 14;
    @Unique
    private static final int riftflux$ORIENTATION_NORTH = 2;
    @Unique
    private static final int riftflux$ORIENTATION_SOUTH = 4;
    @Unique
    private static final int riftflux$ORIENTATION_WEST = 6;
    @Unique
    private static final int riftflux$ORIENTATION_DOWN = 8;
    @Unique
    private static final int riftflux$ORIENTATION_EAST = 10;
    @Unique
    private static final double riftflux$BASE_MIN = 0.1875D;
    @Unique
    private static final double riftflux$BASE_MAX = 0.8125D;
    @Unique
    private static final double riftflux$MID_MIN = 0.25D;
    @Unique
    private static final double riftflux$MID_MAX = 0.75D;
    @Unique
    private static final double riftflux$TIP_MIN = 0.375D;
    @Unique
    private static final double riftflux$TIP_MAX = 0.625D;

    @Shadow
    @Final
    private IIcon[] icons;

    @Inject(
            method = {
                    "getIcon(II)Lnet/minecraft/util/IIcon;",
                    "func_149691_a(II)Lnet/minecraft/util/IIcon;"
            },
            at = @At("HEAD"),
            cancellable = true,
            require = 0
    )
    private void riftflux$getMaskedIcon(int side, int meta, CallbackInfoReturnable<IIcon> cir) {
        int baseMeta = riftflux$getBaseMeta(meta);
        if (baseMeta != meta && baseMeta >= 0 && baseMeta < this.icons.length) {
            cir.setReturnValue(this.icons[baseMeta]);
        }
    }

    @Inject(method = "isSubmergeable", at = @At("HEAD"), cancellable = true)
    private void riftflux$maskSubmergeableMeta(IBlockAccess world, int x, int y, int z,
                                               CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(riftflux$getBaseMeta(world.getBlockMetadata(x, y, z)) == 0
                && riftflux$hasWaterloggingSource(world, x, y, z));
    }

    @Inject(method = "renderLiquid", at = @At("HEAD"), cancellable = true)
    private void riftflux$maskRenderLiquidMeta(int meta, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(riftflux$getBaseMeta(meta) == 0);
    }

    public int onBlockPlaced(World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int meta) {
        return riftflux$getPlacedMeta(side, meta);
    }

    public int func_149660_a(World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int meta) {
        return riftflux$getPlacedMeta(side, meta);
    }

    @Unique
    private static int riftflux$getPlacedMeta(int side, int meta) {
        int baseMeta = riftflux$getBaseMeta(meta);
        if (ModConfig.enableGeoStrataCeilingCrystalSpikes && baseMeta == 0 && side == 0) {
            return baseMeta | riftflux$ORIENTATION_DOWN;
        }
        if (ModConfig.enableGeoStrataWallCrystalSpikes && baseMeta == 0) {
            int orientation = riftflux$getWallOrientation(side);
            if (orientation != 0) {
                return baseMeta | orientation;
            }
        }
        return baseMeta;
    }

    public int damageDropped(int meta) {
        return riftflux$getBaseMeta(meta);
    }

    public int getDamageValue(World world, int x, int y, int z) {
        return riftflux$getBaseMeta(world.getBlockMetadata(x, y, z));
    }

    public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB mask, List list,
                                        Entity entity) {
        riftflux$addCollisionBoxes(world, x, y, z, mask, list);
    }

    public void func_149743_a(World world, int x, int y, int z, AxisAlignedBB mask, List list, Entity entity) {
        riftflux$addCollisionBoxes(world, x, y, z, mask, list);
    }

    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        return riftflux$getCollisionBoundingBox(world, x, y, z);
    }

    public AxisAlignedBB func_149668_a(World world, int x, int y, int z) {
        return riftflux$getCollisionBoundingBox(world, x, y, z);
    }

    public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
        return riftflux$getSelectedBoundingBox(world, x, y, z);
    }

    public AxisAlignedBB func_149633_g(World world, int x, int y, int z) {
        return riftflux$getSelectedBoundingBox(world, x, y, z);
    }

    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
        riftflux$setBoundsForWorld((Block)(Object)this, world, x, y, z);
    }

    public void func_149719_a(IBlockAccess world, int x, int y, int z) {
        riftflux$setBoundsForWorld((Block)(Object)this, world, x, y, z);
    }

    @Unique
    private static void riftflux$addCollisionBoxes(World world, int x, int y, int z, AxisAlignedBB mask, List list) {
        int meta = world.getBlockMetadata(x, y, z);
        if (!ModConfig.enableGeoStrataCrystalSpikeMatchedHitboxes || !riftflux$isCrystalSpike(meta)) {
            riftflux$addBox(x, y, z, mask, list, 0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
            return;
        }

        riftflux$addCrystalSpikeCollisionBoxes(world, x, y, z, meta, mask, list);
    }

    @Unique
    private static AxisAlignedBB riftflux$getCollisionBoundingBox(World world, int x, int y, int z) {
        int meta = world.getBlockMetadata(x, y, z);
        return ModConfig.enableGeoStrataCrystalSpikeMatchedHitboxes && riftflux$isCrystalSpike(meta)
                ? riftflux$getCrystalSpikeSelectionBox(meta, x, y, z)
                : riftflux$getWorldBox(x, y, z, 0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
    }

    @Unique
    private static AxisAlignedBB riftflux$getSelectedBoundingBox(World world, int x, int y, int z) {
        int meta = world.getBlockMetadata(x, y, z);
        return ModConfig.enableGeoStrataCrystalSpikeMatchedHitboxes && riftflux$isCrystalSpike(meta)
                ? riftflux$getCrystalSpikeSelectionBox(meta, x, y, z)
                : riftflux$getWorldBox(x, y, z, 0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
    }

    @Unique
    private static void riftflux$setBoundsForWorld(Block block, IBlockAccess world, int x, int y, int z) {
        int meta = world.getBlockMetadata(x, y, z);
        if (ModConfig.enableGeoStrataCrystalSpikeMatchedHitboxes) {
            riftflux$setCrystalSpikeSelectionBounds(block, meta);
            return;
        }
        block.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    }

    @Unique
    private static int riftflux$getBaseMeta(int meta) {
        return meta & ~riftflux$ORIENTATION_MASK;
    }

    @Unique
    private static int riftflux$getOrientation(int meta) {
        return meta & riftflux$ORIENTATION_MASK;
    }

    @Unique
    private static boolean riftflux$isCrystalSpike(int meta) {
        return riftflux$getBaseMeta(meta) == 0;
    }

    @Unique
    private static int riftflux$getWallOrientation(int side) {
        if (side == 2) {
            return riftflux$ORIENTATION_NORTH;
        }
        if (side == 3) {
            return riftflux$ORIENTATION_SOUTH;
        }
        if (side == 4) {
            return riftflux$ORIENTATION_WEST;
        }
        if (side == 5) {
            return riftflux$ORIENTATION_EAST;
        }
        return 0;
    }

    @Unique
    private static boolean riftflux$hasWaterloggingSource(IBlockAccess world, int x, int y, int z) {
        return RiftFluxFluidloggedLookup.isFluidBlock(world.getBlock(x, y + 1, z))
                || RiftFluxFluidloggedLookup.isFluidBlock(world.getBlock(x - 1, y, z))
                || RiftFluxFluidloggedLookup.isFluidBlock(world.getBlock(x + 1, y, z))
                || RiftFluxFluidloggedLookup.isFluidBlock(world.getBlock(x, y, z - 1))
                || RiftFluxFluidloggedLookup.isFluidBlock(world.getBlock(x, y, z + 1));
    }

    @Unique
    private static AxisAlignedBB riftflux$getCrystalSpikeSelectionBox(int meta, int x, int y, int z) {
        int orientation = riftflux$getOrientation(meta);
        if (orientation == riftflux$ORIENTATION_EAST || orientation == riftflux$ORIENTATION_WEST) {
            return riftflux$getWorldBox(x, y, z, 0.0D, riftflux$BASE_MIN, riftflux$BASE_MIN,
                    1.0D, riftflux$BASE_MAX, riftflux$BASE_MAX);
        }
        if (orientation == riftflux$ORIENTATION_NORTH || orientation == riftflux$ORIENTATION_SOUTH) {
            return riftflux$getWorldBox(x, y, z, riftflux$BASE_MIN, riftflux$BASE_MIN, 0.0D,
                    riftflux$BASE_MAX, riftflux$BASE_MAX, 1.0D);
        }
        return riftflux$getWorldBox(x, y, z, riftflux$BASE_MIN, 0.0D, riftflux$BASE_MIN,
                riftflux$BASE_MAX, 1.0D, riftflux$BASE_MAX);
    }

    @Unique
    private static void riftflux$setCrystalSpikeSelectionBounds(Block block, int meta) {
        if (!ModConfig.enableGeoStrataCrystalSpikeMatchedHitboxes || !riftflux$isCrystalSpike(meta)) {
            block.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
            return;
        }

        int orientation = riftflux$getOrientation(meta);
        if (orientation == riftflux$ORIENTATION_EAST || orientation == riftflux$ORIENTATION_WEST) {
            block.setBlockBounds(0.0F, (float)riftflux$BASE_MIN, (float)riftflux$BASE_MIN,
                    1.0F, (float)riftflux$BASE_MAX, (float)riftflux$BASE_MAX);
            return;
        }
        if (orientation == riftflux$ORIENTATION_NORTH || orientation == riftflux$ORIENTATION_SOUTH) {
            block.setBlockBounds((float)riftflux$BASE_MIN, (float)riftflux$BASE_MIN, 0.0F,
                    (float)riftflux$BASE_MAX, (float)riftflux$BASE_MAX, 1.0F);
            return;
        }
        block.setBlockBounds((float)riftflux$BASE_MIN, 0.0F, (float)riftflux$BASE_MIN,
                (float)riftflux$BASE_MAX, 1.0F, (float)riftflux$BASE_MAX);
    }

    @Unique
    private static void riftflux$addCrystalSpikeCollisionBoxes(World world, int x, int y, int z, int meta,
                                                              AxisAlignedBB mask, List list) {
        int orientation = riftflux$getOrientation(meta);
        if (orientation == riftflux$ORIENTATION_EAST || orientation == riftflux$ORIENTATION_WEST) {
            riftflux$addAxisCollisionBoxes(world, x, y, z, meta, mask, list, true,
                    orientation == riftflux$ORIENTATION_EAST);
            return;
        }
        if (orientation == riftflux$ORIENTATION_NORTH || orientation == riftflux$ORIENTATION_SOUTH) {
            riftflux$addAxisCollisionBoxes(world, x, y, z, meta, mask, list, false,
                    orientation == riftflux$ORIENTATION_SOUTH);
            return;
        }

        riftflux$addVerticalCollisionBoxes(world, x, y, z, meta, mask, list,
                orientation == riftflux$ORIENTATION_DOWN);
    }

    @Unique
    private static void riftflux$addVerticalCollisionBoxes(World world, int x, int y, int z, int meta,
                                                          AxisAlignedBB mask, List list, boolean downward) {
        boolean hasBaseNeighbor = downward
                ? riftflux$isSameSpike(world, x, y + 1, z, meta)
                : riftflux$isSameSpike(world, x, y - 1, z, meta);
        boolean hasTipNeighbor = downward
                ? riftflux$isSameSpike(world, x, y - 1, z, meta)
                : riftflux$isSameSpike(world, x, y + 1, z, meta);

        if (downward) {
            riftflux$addCrossSectionBox(x, y, z, mask, list, 0.0D, 0.375D,
                    hasTipNeighbor ? riftflux$MID_MIN : riftflux$TIP_MIN,
                    hasTipNeighbor ? riftflux$MID_MAX : riftflux$TIP_MAX);
            riftflux$addCrossSectionBox(x, y, z, mask, list, 0.375D, 0.75D,
                    riftflux$MID_MIN, riftflux$MID_MAX);
            riftflux$addCrossSectionBox(x, y, z, mask, list, 0.75D, 1.0D,
                    hasBaseNeighbor ? riftflux$MID_MIN : riftflux$BASE_MIN,
                    hasBaseNeighbor ? riftflux$MID_MAX : riftflux$BASE_MAX);
            return;
        }

        riftflux$addCrossSectionBox(x, y, z, mask, list, 0.0D, 0.25D,
                hasBaseNeighbor ? riftflux$MID_MIN : riftflux$BASE_MIN,
                hasBaseNeighbor ? riftflux$MID_MAX : riftflux$BASE_MAX);
        riftflux$addCrossSectionBox(x, y, z, mask, list, 0.25D, 0.625D,
                riftflux$MID_MIN, riftflux$MID_MAX);
        riftflux$addCrossSectionBox(x, y, z, mask, list, 0.625D, 1.0D,
                hasTipNeighbor ? riftflux$MID_MIN : riftflux$TIP_MIN,
                hasTipNeighbor ? riftflux$MID_MAX : riftflux$TIP_MAX);
    }

    @Unique
    private static void riftflux$addAxisCollisionBoxes(World world, int x, int y, int z, int meta,
                                                      AxisAlignedBB mask, List list, boolean xAxis,
                                                      boolean positiveDirection) {
        int baseOffset = positiveDirection ? -1 : 1;
        int tipOffset = positiveDirection ? 1 : -1;
        boolean hasBaseNeighbor = xAxis
                ? riftflux$isSameSpike(world, x + baseOffset, y, z, meta)
                : riftflux$isSameSpike(world, x, y, z + baseOffset, meta);
        boolean hasTipNeighbor = xAxis
                ? riftflux$isSameSpike(world, x + tipOffset, y, z, meta)
                : riftflux$isSameSpike(world, x, y, z + tipOffset, meta);

        if (positiveDirection) {
            riftflux$addAxisBox(x, y, z, mask, list, 0.0D, 0.25D,
                    hasBaseNeighbor ? riftflux$MID_MIN : riftflux$BASE_MIN,
                    hasBaseNeighbor ? riftflux$MID_MAX : riftflux$BASE_MAX, xAxis);
            riftflux$addAxisBox(x, y, z, mask, list, 0.25D, 0.625D,
                    riftflux$MID_MIN, riftflux$MID_MAX, xAxis);
            riftflux$addAxisBox(x, y, z, mask, list, 0.625D, 1.0D,
                    hasTipNeighbor ? riftflux$MID_MIN : riftflux$TIP_MIN,
                    hasTipNeighbor ? riftflux$MID_MAX : riftflux$TIP_MAX, xAxis);
            return;
        }

        riftflux$addAxisBox(x, y, z, mask, list, 0.0D, 0.375D,
                hasTipNeighbor ? riftflux$MID_MIN : riftflux$TIP_MIN,
                hasTipNeighbor ? riftflux$MID_MAX : riftflux$TIP_MAX, xAxis);
        riftflux$addAxisBox(x, y, z, mask, list, 0.375D, 0.75D,
                riftflux$MID_MIN, riftflux$MID_MAX, xAxis);
        riftflux$addAxisBox(x, y, z, mask, list, 0.75D, 1.0D,
                hasBaseNeighbor ? riftflux$MID_MIN : riftflux$BASE_MIN,
                hasBaseNeighbor ? riftflux$MID_MAX : riftflux$BASE_MAX, xAxis);
    }

    @Unique
    private static void riftflux$addCrossSectionBox(int x, int y, int z, AxisAlignedBB mask, List list,
                                                    double minY, double maxY, double min, double max) {
        riftflux$addBox(x, y, z, mask, list, min, minY, min, max, maxY, max);
    }

    @Unique
    private static void riftflux$addAxisBox(int x, int y, int z, AxisAlignedBB mask, List list,
                                            double minAxis, double maxAxis, double minCross,
                                            double maxCross, boolean xAxis) {
        if (xAxis) {
            riftflux$addBox(x, y, z, mask, list, minAxis, minCross, minCross, maxAxis, maxCross, maxCross);
            return;
        }
        riftflux$addBox(x, y, z, mask, list, minCross, minCross, minAxis, maxCross, maxCross, maxAxis);
    }

    @Unique
    private static boolean riftflux$isSameSpike(World world, int x, int y, int z, int meta) {
        Block block = world.getBlock(x, y, z);
        if (!(block instanceof BlockDecoGen)) {
            return false;
        }
        int otherMeta = world.getBlockMetadata(x, y, z);
        return riftflux$getBaseMeta(otherMeta) == 0
                && riftflux$getOrientation(otherMeta) == riftflux$getOrientation(meta);
    }

    @Unique
    private static void riftflux$addBox(int x, int y, int z, AxisAlignedBB mask, List list,
                                        double minX, double minY, double minZ,
                                        double maxX, double maxY, double maxZ) {
        AxisAlignedBB box = riftflux$getWorldBox(x, y, z, minX, minY, minZ, maxX, maxY, maxZ);
        if (mask == null || mask.intersectsWith(box)) {
            list.add(box);
        }
    }

    @Unique
    private static AxisAlignedBB riftflux$getWorldBox(int x, int y, int z,
                                                      double minX, double minY, double minZ,
                                                      double maxX, double maxY, double maxZ) {
        return AxisAlignedBB.getBoundingBox(
                x + minX,
                y + minY,
                z + minZ,
                x + maxX,
                y + maxY,
                z + maxZ
        );
    }
}
