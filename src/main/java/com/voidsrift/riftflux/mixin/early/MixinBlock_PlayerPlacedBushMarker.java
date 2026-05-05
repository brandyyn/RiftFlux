package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.util.RFPlantContext;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Marks bush blocks explicitly placed by a real player for:
 * - allowPlantsOnAnyBlock survival checks
 * - optional directional crossed-plant rendering
 */
@Mixin(Block.class)
public abstract class MixinBlock_PlayerPlacedBushMarker {

    @Inject(method = "breakBlock", at = @At("HEAD"))
    private void riftflux$clearTrackedPlantState(World world, int x, int y, int z,
                                                 Block block, int meta,
                                                 CallbackInfo ci) {
        RFPlantContext.clearPlayerPlaced(world, x, y, z);
    }

    @Inject(method = "onBlockPlacedBy", at = @At("HEAD"))
    private void riftflux$markBushPlayerPlaced(World world, int x, int y, int z,
                                               EntityLivingBase placer, ItemStack stack,
                                               CallbackInfo ci) {
        boolean shouldMarkFacing = ModConfig.directionalCrossedPlantRenderingByPlacement
                && ModConfig.directionalCrossedPlantFacePlayerOnPlacement;
        if (!ModConfig.allowPlantsOnAnyBlock && !shouldMarkFacing) {
            return;
        }

        // Only care about *real* player placements
        if (!(placer instanceof EntityPlayer)) {
            return;
        }

        // Only mark bushes (flowers, saplings, etc.)
        if (!(((Object) this) instanceof BlockBush)) {
            return;
        }

        RFPlantContext.markPlayerPlaced(world, x, y, z);
        if (shouldMarkFacing) {
            RFPlantContext.markCrossedPlantFacingFromPlacer(world, x, y, z, placer);
        }
        if (((Object) this) instanceof BlockDoublePlant) {
            if (world != null
                    && world.getBlock(x, y, z) == (Block) (Object) this
                    && world.getBlock(x, y + 1, z) == (Block) (Object) this) {
                int lowerMeta = world.getBlockMetadata(x, y, z) & 7;
                if (lowerMeta == 0) {
                    int sunflowerFacing = ((MathHelper.floor_double((double) (placer.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3) + 3) % 4;
                    world.setBlockMetadataWithNotify(x, y + 1, z, 8 | sunflowerFacing, 2);
                }
            }
            RFPlantContext.markPlayerPlaced(world, x, y + 1, z);
            if (shouldMarkFacing) {
                RFPlantContext.markCrossedPlantFacingFromPlacer(world, x, y + 1, z, placer);
            }
        }
    }
}
