package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.ModConfig;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Relax survival rules for BlockBush so player-placed plants can stand on
 * any solid block (stone, glass, etc.), but never on air / liquids.
 *
 * Worldgen is unaffected because we do NOT touch canPlaceBlockOn, only
 * canBlockStay (i.e. "should this existing plant drop?").
 */
@Mixin(BlockBush.class)
public abstract class MixinBlockBush_AnySupport {

    @Inject(method = "canBlockStay", at = @At("HEAD"), cancellable = true)
    private void riftflux$allowBushStayOnAnySolid(World world, int x, int y, int z,
                                                  CallbackInfoReturnable<Boolean> cir) {
        // Config gate – if feature is off, use vanilla logic.
        if (!ModConfig.allowPlantsOnAnyBlock) {
            return;
        }

        // Block directly under the plant
        Block ground = world.getBlock(x, y - 1, z);

        // If there's no block / air below, it should NOT stay (no floating plants).
        if (ground == null || ground == Blocks.air) {
            cir.setReturnValue(false);
            return;
        }

        // If the block below is solid, we allow the plant to stay.
        // This covers stone, glass, slabs, etc., but still excludes water, leaves, etc.
        if (ground.getMaterial().isSolid()) {
            cir.setReturnValue(true);
            return;
        }

        // Otherwise (non-solid support like water/leaves), fall back to vanilla logic.
        // Just don't setReturnValue, so the original canBlockStay runs.
    }
}
