package com.voidsrift.riftflux.vortex.block;

import com.voidsrift.riftflux.ModConfig;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemBlockGlowCarpet extends ItemBlock {

    public ItemBlockGlowCarpet(Block block) {
        super(block);
    }

    @Override
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z,
            int side, float hitX, float hitY, float hitZ, int metadata) {
        Block block = this.field_150939_a;
        if (block == ModBlocks.glowCarpet && ModConfig.randomizeGlowCarpetTextureOnPlacement) {
            block = ModBlocks.getRandomGlowCarpetVariant(world.rand);
        }

        if (!world.setBlock(x, y, z, block, metadata, 3)) {
            return false;
        }

        if (world.getBlock(x, y, z) == block) {
            block.onBlockPlacedBy(world, x, y, z, (EntityLivingBase) player, stack);
            block.onPostBlockPlaced(world, x, y, z, metadata);
        }

        return true;
    }
}