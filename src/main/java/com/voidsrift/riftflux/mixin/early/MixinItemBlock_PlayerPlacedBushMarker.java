package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.util.RFPlantContext;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.init.Blocks;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemBlock.class)
public abstract class MixinItemBlock_PlayerPlacedBushMarker {

    @Inject(method = "onItemUse", at = @At("RETURN"), require = 0)
    private void riftflux$markPlacedBush(ItemStack stack, EntityPlayer player, World world,
                                         int x, int y, int z, int side,
                                         float hitX, float hitY, float hitZ,
                                         CallbackInfoReturnable<Boolean> cir) {
        if (!Boolean.TRUE.equals(cir.getReturnValue())) {
            return;
        }
        boolean shouldMarkFacing = ModConfig.directionalCrossedPlantRenderingByPlacement
                && ModConfig.directionalCrossedPlantFacePlayerOnPlacement;
        if (!ModConfig.allowPlantsOnAnyBlock && !shouldMarkFacing) {
            return;
        }

        int placeX = x;
        int placeY = y;
        int placeZ = z;
        Block clicked = world.getBlock(x, y, z);
        if (clicked == Blocks.snow_layer && (world.getBlockMetadata(x, y, z) & 7) < 1) {
            side = 1;
        } else if (clicked != Blocks.vine
                && clicked != Blocks.tallgrass
                && clicked != Blocks.deadbush
                && !clicked.isReplaceable(world, x, y, z)) {
            if (side == 0) {
                --placeY;
            } else if (side == 1) {
                ++placeY;
            } else if (side == 2) {
                --placeZ;
            } else if (side == 3) {
                ++placeZ;
            } else if (side == 4) {
                --placeX;
            } else if (side == 5) {
                ++placeX;
            }
        }

        Block block = world.getBlock(placeX, placeY, placeZ);
        if (!(block instanceof BlockBush)) {
            return;
        }

        int lowerY = placeY;
        if (block instanceof BlockDoublePlant) {
            int placedMeta = world.getBlockMetadata(placeX, placeY, placeZ);
            if ((placedMeta & 8) != 0) {
                lowerY = placeY - 1;
            }
            if (player != null
                    && world.getBlock(placeX, lowerY, placeZ) == block
                    && world.getBlock(placeX, lowerY + 1, placeZ) == block) {
                int lowerMeta = world.getBlockMetadata(placeX, lowerY, placeZ) & 7;
                if (lowerMeta == 0) {
                    int sunflowerFacing = net.minecraft.util.MathHelper
                            .floor_double((double) (player.rotationYaw * 4.0F / 360.0F) + 0.5D);
                    sunflowerFacing = ((sunflowerFacing & 3) + 3) % 4;
                    world.setBlockMetadataWithNotify(placeX, lowerY + 1, placeZ, 8 | sunflowerFacing, 2);
                }
            }
        }

        RFPlantContext.markPlayerPlaced(world, placeX, lowerY, placeZ);
        if (shouldMarkFacing) {
            RFPlantContext.markCrossedPlantFacingFromPlacer(world, placeX, lowerY, placeZ, player);
        }

        if (block instanceof BlockDoublePlant || world.getBlock(placeX, lowerY + 1, placeZ) == block) {
            RFPlantContext.markPlayerPlaced(world, placeX, lowerY + 1, placeZ);
            if (shouldMarkFacing) {
                RFPlantContext.markCrossedPlantFacingFromPlacer(world, placeX, lowerY + 1, placeZ, player);
            }
        }
    }
}
