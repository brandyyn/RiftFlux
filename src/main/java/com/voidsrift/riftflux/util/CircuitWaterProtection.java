package com.voidsrift.riftflux.util;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

public final class CircuitWaterProtection {
    private CircuitWaterProtection() {
    }

    public static boolean isProtectedCircuit(Block block) {
        return block == Blocks.redstone_wire
                || block == Blocks.redstone_torch
                || block == Blocks.unlit_redstone_torch
                || block == Blocks.unpowered_repeater
                || block == Blocks.powered_repeater
                || block == Blocks.unpowered_comparator
                || block == Blocks.powered_comparator
                || block == Blocks.lever
                || block == Blocks.stone_button
                || block == Blocks.wooden_button
                || block == Blocks.stone_pressure_plate
                || block == Blocks.wooden_pressure_plate
                || block == Blocks.light_weighted_pressure_plate
                || block == Blocks.heavy_weighted_pressure_plate
                || block == Blocks.tripwire
                || block == Blocks.tripwire_hook
                || block == Blocks.rail
                || block == Blocks.golden_rail
                || block == Blocks.detector_rail
                || block == Blocks.activator_rail
                || block == Blocks.daylight_detector;
    }
}
