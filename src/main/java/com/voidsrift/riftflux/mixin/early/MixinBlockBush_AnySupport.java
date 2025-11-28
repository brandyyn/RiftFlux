package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.util.RFPlantContext;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * allowPlantsOnAnyBlock:
 *
 * - Affects all BlockBush (vanilla + modded).
 * - Placement is relaxed only when the call comes from ItemBlock (player right-click).
 * - Survival is relaxed only for bushes that were actually placed by a player.
 *
 * Worldgen / modded foliage generators (no ItemBlock, no player placer) keep
 * vanilla placement and survival, so they cannot spam on trees / ice / stone
 * because of this feature.
 */
@Mixin(BlockBush.class)
public abstract class MixinBlockBush_AnySupport {

    /**
     * Detects calls originating from ItemBlock placement (player right-click).
     * Worldgen code (WorldGen*, custom generators) typically does NOT go
     * through ItemBlock, so they won't hit this.
     */
    private static boolean riftflux$isItemPlacementCall() {
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        // Skip first few frames: getStackTrace, this helper, our inject, etc.
        for (int i = 3; i < stack.length; i++) {
            String cls = stack[i].getClassName();
            if (cls.startsWith("net.minecraft.item.ItemBlock")
                    || cls.endsWith(".ItemBlock")
                    || cls.contains("ItemBlock")) {
                return true;
            }
        }
        return false;
    }

    /**
     * Placement hook:
     *
     * - Only when allowPlantsOnAnyBlock is enabled.
     * - Only when the call is coming from ItemBlock (player placement).
     * - Allows placement if the block below is any solid block and the target
     *   position is replaceable.
     *
     * Worldgen / bonemeal / custom generators that don't use ItemBlock
     * keep vanilla canPlaceBlockAt behaviour.
     */
    @Inject(method = "canPlaceBlockAt", at = @At("HEAD"), cancellable = true)
    private void riftflux$allowPlaceOnAnySolid(World world, int x, int y, int z,
                                               CallbackInfoReturnable<Boolean> cir) {
        if (!ModConfig.allowPlantsOnAnyBlock) {
            return;
        }

        // Only relax placement for ItemBlock-based (i.e. player) placement
        if (!riftflux$isItemPlacementCall()) {
            return;
        }

        Block ground = world.getBlock(x, y - 1, z);
        if (ground == null || ground == Blocks.air) {
            // No support at all -> keep vanilla behaviour
            return;
        }

        // Target block must be replaceable (air, tall grass, etc.)
        Block target = world.getBlock(x, y, z);
        if (!target.isReplaceable(world, x, y, z)) {
            return;
        }

        // Any solid block underneath counts as valid support for player placements
        if (ground.getMaterial().isSolid()) {
            cir.setReturnValue(true);
        }
    }

    /**
     * Survival hook:
     *
     * - Only when allowPlantsOnAnyBlock is enabled.
     * - Only when this bush was explicitly placed by a player (tagged via
     *   Block.onBlockPlacedBy mixin).
     *
     * Worldgen / modded foliage generators still use vanilla canBlockStay and
     * will be culled on invalid supports like ice / tree tops / etc.
     */
    @Inject(method = "canBlockStay", at = @At("HEAD"), cancellable = true)
    private void riftflux$allowStayOnAnySolid(World world, int x, int y, int z,
                                              CallbackInfoReturnable<Boolean> cir) {
        if (!ModConfig.allowPlantsOnAnyBlock) {
            return;
        }

        // Only relax survival for *player-placed* bushes
        if (!RFPlantContext.isPlayerPlaced(world, x, y, z)) {
            return;
        }

        Block ground = world.getBlock(x, y - 1, z);

        // Still forbid floating plants: no support or air below -> drop
        if (ground == null || ground == Blocks.air) {
            cir.setReturnValue(false);
            return;
        }

        // If the block below is solid, we allow the plant to stay.
        if (ground.getMaterial().isSolid()) {
            cir.setReturnValue(true);
        }
        // Non-solid (water, leaves, etc.) fall back to vanilla by not touching cir.
    }
}
