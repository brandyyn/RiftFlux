package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.util.RFPlantContext;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
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

    @Inject(method = "onBlockPlacedBy", at = @At("HEAD"))
    private void riftflux$markBushPlayerPlaced(World world, int x, int y, int z,
                                               EntityLivingBase placer, ItemStack stack,
                                               CallbackInfo ci) {
        if (!ModConfig.allowPlantsOnAnyBlock && !ModConfig.directionalCrossedPlantRenderingByPlacement) {
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
        RFPlantContext.markCrossedPlantFacingFromPlacer(world, x, y, z, placer);
        if (((Object) this) instanceof BlockDoublePlant) {
            RFPlantContext.markPlayerPlaced(world, x, y + 1, z);
            RFPlantContext.markCrossedPlantFacingFromPlacer(world, x, y + 1, z, placer);
        }
    }
}
