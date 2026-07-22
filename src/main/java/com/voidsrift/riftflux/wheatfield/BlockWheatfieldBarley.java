package com.voidsrift.riftflux.wheatfield;

import com.voidsrift.riftflux.Constants;
import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.util.RFPlantContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemShears;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;

import java.util.ArrayList;
import java.util.Random;

public class BlockWheatfieldBarley extends BlockBush implements IShearable {
    private IIcon icon;

    public BlockWheatfieldBarley() {
        setBlockName("wheatfieldBarley");
        setHardness(0.0F);
        setStepSound(soundTypeGrass);
        setCreativeTab(CreativeTabs.tabDecorations);
        setBlockBounds(0.125F, 0.0F, 0.125F, 0.875F, 1.0F, 0.875F);
    }

    @Override
    public int getRenderType() {
        return WheatfieldRenderIds.barleyRenderId >= 0 ? WheatfieldRenderIds.barleyRenderId : 6;
    }

    @Override
    protected boolean canPlaceBlockOn(Block block) {
        return block == net.minecraft.init.Blocks.grass || block == net.minecraft.init.Blocks.dirt;
    }

    @Override
    public boolean canBlockStay(World world, int x, int y, int z) {
        Block below = world.getBlock(x, y - 1, z);
        if (ModConfig.allowPlantsOnAnyBlock && RFPlantContext.isPlayerPlaced(world, x, y, z)) {
            return below != null && below != Blocks.air && below.getMaterial().isSolid();
        }
        return canPlaceBlockOn(below);
    }

    @Override
    public boolean isReplaceable(IBlockAccess world, int x, int y, int z) {
        return true;
    }

    @Override
    public Item getItemDropped(int meta, Random random, int fortune) {
        return Item.getItemFromBlock(this);
    }

    @Override
    public int quantityDropped(Random random) {
        return shouldDropWhenBroken(random) ? 1 : 0;
    }

    @Override
    public void harvestBlock(World world, net.minecraft.entity.player.EntityPlayer player, int x, int y, int z, int meta) {
        if (player != null) {
            player.addStat(net.minecraft.stats.StatList.mineBlockStatArray[Block.getIdFromBlock(this)], 1);
            player.addExhaustion(0.025F);
        }
        if (world == null || world.isRemote || player == null) {
            return;
        }

        ItemStack held = player.getCurrentEquippedItem();
        if (held != null && held.getItem() instanceof ItemShears) {
            return;
        }
        if (ModConfig.wheatfieldBarleyOnlyDropsWhenSheared) {
            return;
        }
        if (EnchantmentHelper.getSilkTouchModifier(player)) {
            dropBlockAsItem(world, x, y, z, new ItemStack(this));
            return;
        }

        if (shouldDropWhenBroken(world.rand)) {
            dropBlockAsItem(world, x, y, z, new ItemStack(this));
        }
    }

    private boolean shouldDropWhenBroken(Random random) {
        if (ModConfig.wheatfieldBarleyOnlyDropsWhenSheared) {
            return false;
        }

        int chance = Math.max(0, Math.min(100, ModConfig.wheatfieldBarleyFistDropChancePercent));
        return chance > 0 && random.nextInt(100) < chance;
    }

    @Override
    public boolean isShearable(ItemStack item, IBlockAccess world, int x, int y, int z) {
        return true;
    }

    @Override
    public ArrayList<ItemStack> onSheared(ItemStack item, IBlockAccess world, int x, int y, int z, int fortune) {
        ArrayList<ItemStack> drops = new ArrayList<ItemStack>();
        drops.add(new ItemStack(this));
        return drops;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
        icon = iconRegister.registerIcon(Constants.MODID + ":wheatfield/barley");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        return icon;
    }
}
