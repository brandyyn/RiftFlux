package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.util.RFPlantContext;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockDoublePlant.class)
public abstract class MixinBlockDoublePlant_AnySupport {

    private static boolean riftflux$isItemPlacementCall() {
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        for (int i = 3; i < stack.length; i++) {
            String cls = stack[i].getClassName();
            if (cls.startsWith("net.minecraft.item.ItemBlock")
                    || cls.startsWith("net.minecraft.item.ItemDoublePlant")
                    || cls.endsWith(".ItemBlock")
                    || cls.endsWith(".ItemDoublePlant")
                    || cls.contains("ItemBlock")) {
                return true;
            }
        }
        return false;
    }

    @Inject(method = "canPlaceBlockAt", at = @At("HEAD"), cancellable = true)
    private void riftflux$allowDoublePlantPlaceOnAnySolid(World world, int x, int y, int z,
                                                          CallbackInfoReturnable<Boolean> cir) {
        if (!ModConfig.allowPlantsOnAnyBlock) {
            return;
        }
        if (!riftflux$isItemPlacementCall()) {
            return;
        }
        if (world == null || y < 0 || y >= 255) {
            return;
        }

        Block ground = world.getBlock(x, y - 1, z);
        if (ground == null || ground == Blocks.air || !ground.getMaterial().isSolid()) {
            return;
        }

        Block target = world.getBlock(x, y, z);
        Block above = world.getBlock(x, y + 1, z);
        if (!target.isReplaceable(world, x, y, z) || !above.isReplaceable(world, x, y + 1, z)) {
            return;
        }

        cir.setReturnValue(true);
    }

    @Inject(method = "canBlockStay", at = @At("HEAD"), cancellable = true)
    private void riftflux$allowDoublePlantStayOnAnySolid(World world, int x, int y, int z,
                                                         CallbackInfoReturnable<Boolean> cir) {
        if (!ModConfig.allowPlantsOnAnyBlock || world == null) {
            return;
        }

        Block self = (Block) (Object) this;
        int meta = world.getBlockMetadata(x, y, z);
        boolean topHalf = (meta & 8) != 0;

        if (topHalf) {
            if (world.getBlock(x, y - 1, z) != self) {
                cir.setReturnValue(false);
                return;
            }

            Block ground = world.getBlock(x, y - 2, z);
            if (ground == null || ground == Blocks.air) {
                cir.setReturnValue(false);
                return;
            }

            if (ground.getMaterial().isSolid()) {
                cir.setReturnValue(true);
            }
            return;
        }

        if (world.getBlock(x, y + 1, z) != self) {
            cir.setReturnValue(false);
            return;
        }

        Block ground = world.getBlock(x, y - 1, z);
        if (ground == null || ground == Blocks.air) {
            cir.setReturnValue(false);
            return;
        }

        if (ground.getMaterial().isSolid()) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "onBlockPlacedBy", at = @At("TAIL"))
    private void riftflux$markDoublePlantPlacement(World world, int x, int y, int z,
                                                   EntityLivingBase placer, ItemStack stack,
                                                   CallbackInfo ci) {
        boolean shouldMarkFacing = ModConfig.directionalCrossedPlantRenderingByPlacement
                && ModConfig.directionalCrossedPlantFacePlayerOnPlacement;
        if (!ModConfig.allowPlantsOnAnyBlock && !shouldMarkFacing) {
            return;
        }
        if (!(placer instanceof EntityPlayer)) {
            return;
        }

        RFPlantContext.markPlayerPlaced(world, x, y, z);
        RFPlantContext.markPlayerPlaced(world, x, y + 1, z);
        if (shouldMarkFacing) {
            int lowerMeta = world.getBlockMetadata(x, y, z) & 7;
            if (lowerMeta == 0) {
                int sunflowerFacing = ((MathHelper.floor_double((double) (placer.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3) + 3) % 4;
                world.setBlockMetadataWithNotify(x, y + 1, z, 8 | sunflowerFacing, 2);
            }
            RFPlantContext.markCrossedPlantFacingFromPlacer(world, x, y, z, placer);
            RFPlantContext.markCrossedPlantFacingFromPlacer(world, x, y + 1, z, placer);
        }
    }
}
