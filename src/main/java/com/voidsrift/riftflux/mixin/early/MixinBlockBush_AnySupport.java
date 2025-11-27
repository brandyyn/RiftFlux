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
 * Relax survival / placement rules for BlockBush so player-placed plants can
 * stand on any solid block (stone, glass, etc.), but never on air / liquids.
 *
 * Worldgen and bonemeal stay vanilla by detecting calls coming from
 * WorldGen* / WorldGenerator classes and skipping our override in that case.
 */
@Mixin(BlockBush.class)
public abstract class MixinBlockBush_AnySupport {

    /**
     * Returns true if this canBlockStay() call is happening from a vanilla
     * world-gen / bonemeal generator (WorldGenFlowers, WorldGenTallGrass, etc.).
     *
     * We just scan the current stack trace for those classes; this avoids any
     * global flags and keeps behaviour local to the call.
     */
    private static boolean riftflux$isWorldGenOrBonemealCall() {
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        // Skip the first few frames (getStackTrace, this helper, our inject)
        for (int i = 3; i < stack.length; i++) {
            String cls = stack[i].getClassName();

            // Vanilla worldgen / bonemeal generators live here in 1.7.10:
            //   net.minecraft.world.gen.feature.WorldGen*
            //   net.minecraft.world.gen.feature.WorldGenerator
            if (cls.startsWith("net.minecraft.world.gen.feature.WorldGen")
                    || cls.equals("net.minecraft.world.gen.feature.WorldGenerator")) {
                return true;
            }

            // Safety: if some mod subclasses vanilla WorldGen* in its own package,
            // this will still catch many of them:
            if (cls.contains("WorldGen") && cls.contains("Flower")) return true;
            if (cls.contains("WorldGen") && cls.contains("TallGrass")) return true;
        }
        return false;
    }

    @Inject(method = "canBlockStay", at = @At("HEAD"), cancellable = true)
    private void riftflux$allowBushStayOnAnySolid(World world, int x, int y, int z,
                                                  CallbackInfoReturnable<Boolean> cir) {
        // Config gate – if feature is off, use vanilla logic.
        if (!ModConfig.allowPlantsOnAnyBlock) {
            return;
        }

        // If this is being called from world-gen or bonemeal generators,
        // do NOT change behaviour – let vanilla canBlockStay decide.
        if (riftflux$isWorldGenOrBonemealCall()) {
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
        // This covers stone, glass, slabs, logs, etc., but still excludes water, leaves, etc.
        if (ground.getMaterial().isSolid()) {
            cir.setReturnValue(true);
            return;
        }

        // Otherwise (non-solid support like water/leaves), fall back to vanilla logic.
        // Just don't setReturnValue, so the original canBlockStay runs.
    }
}
