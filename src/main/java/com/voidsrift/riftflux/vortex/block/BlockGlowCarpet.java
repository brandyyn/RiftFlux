package com.voidsrift.riftflux.vortex.block;

import com.voidsrift.riftflux.ModConfig;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.Random;

public class BlockGlowCarpet extends Block {

    public BlockGlowCarpet() {
        super(Material.cloth);
    }

    @Override
    public int onBlockPlaced(World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int meta) {
        return 0;
    }

    @Override
    public void onBlockAdded(World world, int x, int y, int z) {
        super.onBlockAdded(world, x, y, z);
        if (world.isRemote || world.getBlock(x, y, z) != this || this != ModBlocks.glowCarpet) {
            return;
        }

        Block block = ModConfig.randomizeGlowCarpetTextureOnPlacement
                ? ModBlocks.getRandomGlowCarpetVariant(world.rand)
                : this;
        int metadata = ModConfig.randomizeGlowCarpetRotation ? world.rand.nextInt(4) : 0;
        if (block != this) {
            world.setBlock(x, y, z, block, metadata, 3);
        } else if (world.getBlockMetadata(x, y, z) != metadata) {
            world.setBlockMetadataWithNotify(x, y, z, metadata, 3);
        }
    }

    @Override
    public Item getItemDropped(int meta, Random rand, int fortune) {
        return Item.getItemFromBlock(ModBlocks.glowCarpet);
    }

    @Override
    public int damageDropped(int meta) {
        return 0;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public Item getItem(World world, int x, int y, int z) {
        return Item.getItemFromBlock(ModBlocks.glowCarpet);
    }

    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z, EntityPlayer player) {
        return new ItemStack(ModBlocks.glowCarpet);
    }

    @Override
    public int getRenderType() {
        if (ModConfig.randomizeGlowCarpetRotation && ModBlocks.glowCarpetRenderId >= 0) {
            return ModBlocks.glowCarpetRenderId;
        }
        return super.getRenderType();
    }

    @Override
    public int getLightValue(IBlockAccess world, int x, int y, int z) {
        return ModConfig.glowCarpetLightLevel;
    }

    @Override
    public int getLightValue() {
        return ModConfig.glowCarpetLightLevel;
    }
}