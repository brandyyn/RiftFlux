package com.voidsrift.riftflux.pumpkinpastures;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.Random;

public class BlockSuspiciousPumpkin extends Block {
    @SideOnly(Side.CLIENT)
    private IIcon iconTop;
    @SideOnly(Side.CLIENT)
    private IIcon iconBottom;
    @SideOnly(Side.CLIENT)
    private IIcon iconSide;

    public BlockSuspiciousPumpkin() {
        super(Material.gourd);
        setBlockName("suspicious_pumpkin");
        setStepSound(soundTypeWood);
        setHardness(1.0F);
        setCreativeTab(CreativeTabs.tabBlock);
        setTickRandomly(true);
    }

    @Override
    public void onBlockAdded(World world, int x, int y, int z) {
        super.onBlockAdded(world, x, y, z);
        world.scheduleBlockUpdate(x, y, z, this, 1);
    }

    @Override
    public void updateTick(World world, int x, int y, int z, Random random) {
        if (world == null || world.isRemote) {
            return;
        }

        EntityPlayer nearby = world.getClosestPlayer(x + 0.5D, y + 0.5D, z + 0.5D, 7.0D);
        if (nearby != null) {
            EntityPumpkinCreeper creeper = new EntityPumpkinCreeper(world);
            creeper.setLocationAndAngles(x + 0.5D, y + 0.05D, z + 0.5D, world.rand.nextFloat() * 360.0F, 0.0F);
            creeper.onSpawnWithEgg(null);
            world.spawnEntityInWorld(creeper);

            for (int i = 0; i < 10; i++) {
                world.spawnParticle(
                        "explode",
                        x + 0.5D + (world.rand.nextDouble() - 0.5D) * 0.6D,
                        y + 0.5D + world.rand.nextDouble() * 0.6D,
                        z + 0.5D + (world.rand.nextDouble() - 0.5D) * 0.6D,
                        0.0D,
                        0.0D,
                        0.0D
                );
            }
            world.setBlockToAir(x, y, z);
            return;
        }

        if (world.isDaytime()) {
            world.setBlockToAir(x, y, z);
            return;
        }

        world.scheduleBlockUpdate(x, y, z, this, 1);
    }

    @Override
    public int getLightOpacity(IBlockAccess world, int x, int y, int z) {
        return 15;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register) {
        this.iconTop = register.registerIcon("riftflux:pumpkinpastures/pumpkin_j_top");
        this.iconBottom = register.registerIcon("riftflux:pumpkinpastures/pumpkin_j_bottom");
        this.iconSide = register.registerIcon("riftflux:pumpkinpastures/pumpkin_j_side");
        this.blockIcon = this.iconSide;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        if (side == 1) {
            return iconTop;
        }
        if (side == 0) {
            return iconBottom;
        }
        return iconSide;
    }
}
