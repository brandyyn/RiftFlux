package com.voidsrift.riftflux.terramine;

import com.voidsrift.riftflux.ModConfig;
import net.minecraft.block.Block;
import net.minecraft.world.World;

public class ItemCaneOfSomaria extends ItemIceRod {
    public ItemCaneOfSomaria() {
        super("riftflux:cane_of_somaria", Math.max(0, ModConfig.caneOfSomariaDurability));
    }

    @Override
    protected int getConfiguredDurability() {
        return Math.max(0, ModConfig.caneOfSomariaDurability);
    }

    @Override
    protected double getConfiguredSpawnDistance() {
        return Math.max(1.0D, (double) ModConfig.caneOfSomariaSpawnDistance);
    }

    @Override
    public boolean isPlacementPreviewEnabled() {
        return ModConfig.caneOfSomariaPlacementPreviewEnabled;
    }

    @Override
    public boolean isLegendGearManaEnabled() {
        return ModConfig.caneOfSomariaUseLegendGearMana;
    }

    @Override
    public float getConfiguredLegendGearManaCost() {
        return Math.max(0.0F, ModConfig.caneOfSomariaLegendGearManaCost);
    }

    @Override
    protected Block getPlacementBlock() {
        return TerrariaContent.somariaBlock;
    }

    @Override
    protected Block getPlacementEffectBlock() {
        return TerrariaContent.somariaBlock;
    }

    @Override
    protected void playPlacementEffect(World world, int x, int y, int z) {
        super.playPlacementEffect(world, x, y, z);
        world.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, "riftflux:somaria_place", 1.0F, 1.0F);
    }
}
