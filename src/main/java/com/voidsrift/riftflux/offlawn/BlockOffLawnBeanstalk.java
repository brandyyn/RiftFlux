package com.voidsrift.riftflux.offlawn;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.IGrowable;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;

import java.util.ArrayList;
import java.util.Random;

public class BlockOffLawnBeanstalk extends BlockBush implements IGrowable, IShearable {
    @SideOnly(Side.CLIENT)
    private IIcon icon;

    public BlockOffLawnBeanstalk() {
        super();
        setBlockName("offlawn_beanstalk");
        setHardness(0.0F);
        setStepSound(soundTypeGrass);
        setTickRandomly(true);
        setCreativeTab(CreativeTabs.tabDecorations);
        setBlockBounds(0.15F, 0.0F, 0.15F, 0.85F, 1.0F, 0.85F);
    }

    @Override
    protected boolean canPlaceBlockOn(Block block) {
        if (block == null) {
            return false;
        }
        return block == this
                || block == OffLawnContent.lawnBlock
                || block == net.minecraft.init.Blocks.grass
                || block == net.minecraft.init.Blocks.dirt
                || block.getMaterial().isSolid();
    }

    @Override
    public boolean canPlaceBlockAt(World world, int x, int y, int z) {
        return canBlockStay(world, x, y, z);
    }

    @Override
    public boolean canBlockStay(World world, int x, int y, int z) {
        Block below = world.getBlock(x, y - 1, z);
        return below != null && canPlaceBlockOn(below);
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side,
                                    float hitX, float hitY, float hitZ) {
        if (player == null) {
            return false;
        }
        ItemStack held = player.getCurrentEquippedItem();
        if (held == null || held.getItem() != Item.getItemFromBlock(this)) {
            return false;
        }

        int topY = y;
        while (topY < 255 && world.getBlock(x, topY + 1, z) == this) {
            topY++;
        }
        if (topY >= 255 || !world.isAirBlock(x, topY + 1, z)) {
            return false;
        }

        if (!world.isRemote) {
            world.setBlock(x, topY + 1, z, this, 0, 3);
            world.playSoundEffect(
                    x + 0.5D,
                    topY + 1.5D,
                    z + 0.5D,
                    this.stepSound.getStepResourcePath(),
                    (this.stepSound.getVolume() + 1.0F) / 2.0F,
                    this.stepSound.getPitch() * 0.8F
            );
            if (!player.capabilities.isCreativeMode) {
                held.stackSize--;
                if (held.stackSize <= 0) {
                    player.setCurrentItemOrArmor(0, null);
                }
            }
        }

        return true;
    }

    @Override
    public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
        if (entity == null) {
            return;
        }
        entity.motionX = MathHelper.clamp_double(entity.motionX, -0.15D, 0.15D);
        entity.motionZ = MathHelper.clamp_double(entity.motionZ, -0.15D, 0.15D);
        entity.fallDistance = 0.0F;
        if (entity.motionY < -0.15D) {
            entity.motionY = -0.15D;
        }
        if (entity.isSneaking() && entity.motionY < 0.0D) {
            entity.motionY = 0.0D;
        }
        if (entity.isCollidedHorizontally) {
            entity.motionY = 0.2D;
        }
        if (entity instanceof EntityPlayer && entity.motionY > 0.0D) {
            entity.motionY = 0.2D;
        }
    }

    @Override
    public boolean isLadder(IBlockAccess world, int x, int y, int z, EntityLivingBase entity) {
        return true;
    }

    @Override
    public void updateTick(World world, int x, int y, int z, Random random) {
        super.updateTick(world, x, y, z, random);
        if (!world.isRemote) {
            func_149853_b(world, random, x, y, z);
        }
    }

    @Override
    public boolean func_149851_a(World world, int x, int y, int z, boolean isClient) {
        if (!canBlockStay(world, x, y, z)) {
            return false;
        }
        return world.isAirBlock(x, y + 1, z) && world.isAirBlock(x, y + 2, z);
    }

    @Override
    public boolean func_149852_a(World world, Random random, int x, int y, int z) {
        return true;
    }

    @Override
    public void func_149853_b(World world, Random random, int x, int y, int z) {
        if (world.isRemote || !func_149851_a(world, x, y, z, false)) {
            return;
        }

        if (world.isAirBlock(x, y + 3, z)) {
            world.setBlock(x, y + 1, z, this, 0, 2);
        } else if (world.getBlock(x, y + 1, z) != this && OffLawnContent.sunflowerBush instanceof BlockOffLawnSunflowerBush) {
            ((BlockOffLawnSunflowerBush) OffLawnContent.sunflowerBush).placeAt(world, x, y + 1, z, 2);
        }
    }

    @Override
    public Item getItemDropped(int meta, Random random, int fortune) {
        return Item.getItemFromBlock(this);
    }

    @Override
    public int quantityDropped(Random random) {
        return 1;
    }

    @Override
    public int getRenderType() {
        return 1;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register) {
        this.icon = register.registerIcon("riftflux:offlawn/beanstalk");
        this.blockIcon = this.icon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        return icon;
    }

    @Override
    public boolean isShearable(net.minecraft.item.ItemStack item, IBlockAccess world, int x, int y, int z) {
        return true;
    }

    @Override
    public ArrayList<net.minecraft.item.ItemStack> onSheared(net.minecraft.item.ItemStack item, IBlockAccess world, int x, int y, int z, int fortune) {
        ArrayList<net.minecraft.item.ItemStack> drops = new ArrayList<net.minecraft.item.ItemStack>();
        drops.add(new net.minecraft.item.ItemStack(this, 1, 0));
        return drops;
    }
}
