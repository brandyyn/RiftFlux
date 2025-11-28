package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.util.RFPlantContext;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Marks bush blocks that are explicitly placed by a real player so that
 * allowPlantsOnAnyBlock can later distinguish them from world-generated plants.
 */
@Mixin(Block.class)
public abstract class MixinBlock_PlayerPlacedBushMarker {

    @Inject(method = "onBlockPlacedBy", at = @At("HEAD"))
    private void riftflux$markBushPlayerPlaced(World world, int x, int y, int z,
                                               EntityLivingBase placer, ItemStack stack,
                                               CallbackInfo ci) {
        if (!ModConfig.allowPlantsOnAnyBlock) {
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
    }
}
