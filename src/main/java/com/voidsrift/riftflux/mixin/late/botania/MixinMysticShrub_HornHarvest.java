package com.voidsrift.riftflux.mixin.late.botania;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.nmccoy.legendgear.legacy.blocks.MysticShrub;
import org.spongepowered.asm.mixin.Mixin;
import vazkii.botania.api.item.IHornHarvestable;

@Mixin(value = MysticShrub.class, remap = false)
public abstract class MixinMysticShrub_HornHarvest implements IHornHarvestable {

    @Override
    public boolean canHornHarvest(
            World world,
            int x,
            int y,
            int z,
            ItemStack stack,
            EnumHornType hornType
    ) {
        return hornType == EnumHornType.WILD && world.getBlockMetadata(x, y, z) < 2;
    }

    @Override
    public boolean hasSpecialHornHarvest(
            World world,
            int x,
            int y,
            int z,
            ItemStack stack,
            EnumHornType hornType
    ) {
        return true;
    }

    @Override
    public void harvestByHorn(
            World world,
            int x,
            int y,
            int z,
            ItemStack stack,
            EnumHornType hornType
    ) {
        int metadata = world.getBlockMetadata(x, y, z);
        if (hornType != EnumHornType.WILD || metadata >= 2) {
            return;
        }

        Block shrub = (Block) (Object) this;
        shrub.dropBlockAsItemWithChance(world, x, y, z, metadata, 1.0F, 0);
        world.setBlock(x, y, z, shrub, 2, 3);
    }
}
