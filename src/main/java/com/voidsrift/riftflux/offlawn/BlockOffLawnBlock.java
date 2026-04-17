package com.voidsrift.riftflux.offlawn;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.IGrowable;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.ColorizerGrass;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.Random;

public class BlockOffLawnBlock extends Block implements IGrowable {
    @SideOnly(Side.CLIENT)
    private IIcon icon;

    public BlockOffLawnBlock() {
        super(Material.grass);
        setBlockName("offlawn_lawn_block");
        setBlockTextureName("riftflux:offlawn/lawn_block");
        setHardness(0.6F);
        setStepSound(soundTypeGrass);
        setCreativeTab(CreativeTabs.tabBlock);
    }

    @Override
    public boolean canSustainPlant(IBlockAccess world, int x, int y, int z, ForgeDirection direction, IPlantable plantable) {
        Block plant = plantable.getPlant(world, x, y + 1, z);
        EnumPlantType type = plantable.getPlantType(world, x, y + 1, z);

        if (plant == Blocks.reeds) {
            return false;
        }

        if (plantable instanceof BlockBush) {
            return true;
        }

        if (type == EnumPlantType.Nether || type == EnumPlantType.Cave || type == EnumPlantType.Crop) {
            return false;
        }

        if (type == EnumPlantType.Beach) {
            return isBeachWithWater(world, x, y, z);
        }

        if (type == EnumPlantType.Plains) {
            return true;
        }

        return plant != null;
    }

    private static boolean isBeachWithWater(IBlockAccess world, int x, int y, int z) {
        return world.getBlock(x - 1, y, z).getMaterial() == Material.water
                || world.getBlock(x + 1, y, z).getMaterial() == Material.water
                || world.getBlock(x, y, z - 1).getMaterial() == Material.water
                || world.getBlock(x, y, z + 1).getMaterial() == Material.water;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getBlockColor() {
        return ColorizerGrass.getGrassColor(0.5D, 1.0D);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getRenderColor(int meta) {
        return getBlockColor();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int colorMultiplier(IBlockAccess world, int x, int y, int z) {
        return world.getBiomeGenForCoords(x, z).getBiomeGrassColor(x, y, z);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register) {
        this.icon = register.registerIcon("riftflux:offlawn/lawn_block");
        this.blockIcon = this.icon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        return this.icon;
    }

    @Override
    public boolean func_149851_a(World world, int x, int y, int z, boolean isClient) {
        return world.isAirBlock(x, y + 1, z);
    }

    @Override
    public boolean func_149852_a(World world, Random random, int x, int y, int z) {
        return true;
    }

    @Override
    public void func_149853_b(World world, Random random, int x, int y, int z) {
        int targetX = x;
        int targetY = y + 1;
        int targetZ = z;

        for (int i = 0; i < 64; i++) {
            targetX += random.nextInt(3) - 1;
            targetY += (random.nextInt(3) - 1) * random.nextInt(3) / 2;
            targetZ += random.nextInt(3) - 1;

            if (targetY <= 0 || targetY >= world.getActualHeight()) {
                continue;
            }
            if (!world.isAirBlock(targetX, targetY, targetZ)) {
                continue;
            }
            if (world.getBlock(targetX, targetY - 1, targetZ) != this) {
                continue;
            }

            if (random.nextInt(8) == 0) {
                world.getBiomeGenForCoords(targetX, targetZ).plantFlower(world, random, targetX, targetY, targetZ);
            } else {
                world.setBlock(
                        targetX,
                        targetY,
                        targetZ,
                        Blocks.tallgrass,
                        1,
                        3
                );
            }
        }
    }
}
