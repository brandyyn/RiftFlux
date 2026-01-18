package com.voidsrift.riftflux.tweaks.ladder;

import com.voidsrift.riftflux.ModConfig;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;

/**
 * Registers the ladder-related content for RiftFlux.
 */
public final class RiftFluxLadderContent {

    private RiftFluxLadderContent() {}

    public static Block floatingLadderBlock;

    public static void init() {
        if (!ModConfig.enableHangingLadders) {
            return;
        }

        if (floatingLadderBlock == null) {
            floatingLadderBlock = new BlockFloatingLadder();
            GameRegistry.registerBlock(floatingLadderBlock, "floating_ladder");
        }
    }
}
