package com.voidsrift.riftflux.offlawn;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.util.RFPlantContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.IGrowable;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BlockOffLawnSunflowerBush extends BlockDoublePlant implements IGrowable, net.minecraftforge.common.IShearable {
    @SideOnly(Side.CLIENT)
    private IIcon bottomIcon;
    @SideOnly(Side.CLIENT)
    private IIcon topIcon;
    private final String bottomTexture;
    private final String topTexture;

    public BlockOffLawnSunflowerBush() {
        this("offlawn_sunflower_bush", "riftflux:offlawn/sunflower_bush_bottom", "riftflux:offlawn/sunflower_bush_top");
    }

    public BlockOffLawnSunflowerBush(String blockName, String bottomTexture, String topTexture) {
        super();
        this.bottomTexture = bottomTexture;
        this.topTexture = topTexture;
        setBlockName(blockName);
        setStepSound(soundTypeGrass);
        setHardness(0.0F);
        setCreativeTab(CreativeTabs.tabDecorations);
        setTickRandomly(true);
    }

    @Override
    public boolean canPlaceBlockAt(World world, int x, int y, int z) {
        if (ModConfig.allowPlantsOnAnyBlock) {
            Block support = world.getBlock(x, y - 1, z);
            if (support != null && support.getMaterial().isSolid() && world.isAirBlock(x, y + 1, z)) {
                return true;
            }
        }
        return super.canPlaceBlockAt(world, x, y, z)
                || (canPlaceOnBeanstalk(world, x, y, z) && world.isAirBlock(x, y + 1, z));
    }

    @Override
    public boolean canBlockStay(World world, int x, int y, int z) {
        int meta = world.getBlockMetadata(x, y, z);
        boolean upper = BlockDoublePlant.func_149887_c(meta);
        if (upper) {
            return world.getBlock(x, y - 1, z) == this;
        }

        if (canPlaceOnBeanstalk(world, x, y, z)) {
            return world.getBlock(x, y + 1, z) == this;
        }
        if (ModConfig.allowPlantsOnAnyBlock) {
            Block support = world.getBlock(x, y - 1, z);
            if (support != null && support.getMaterial().isSolid()) {
                return world.getBlock(x, y + 1, z) == this;
            }
        }
        return super.canBlockStay(world, x, y, z);
    }

    @Override
    public Item getItemDropped(int meta, Random random, int fortune) {
        if (BlockDoublePlant.func_149887_c(meta)) {
            return null;
        }
        return OffLawnContent.sunSeed;
    }

    @Override
    public int quantityDropped(Random random) {
        return 1;
    }

    @Override
    public int damageDropped(int meta) {
        return 0;
    }

    @Override
    public boolean func_149851_a(World world, int x, int y, int z, boolean isClient) {
        return true;
    }

    @Override
    public boolean func_149852_a(World world, Random random, int x, int y, int z) {
        return true;
    }

    @Override
    public void func_149853_b(World world, Random random, int x, int y, int z) {
        dropBlockAsItem(world, x, y, z, new ItemStack(OffLawnContent.sunSeed, 1));
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase placer, ItemStack stack) {
        if (!BlockDoublePlant.func_149887_c(world.getBlockMetadata(x, y, z))) {
            world.setBlockMetadataWithNotify(x, y, z, 1, 2);
        }
        boolean shouldMarkFacing = ModConfig.directionalCrossedPlantRenderingByPlacement
                && ModConfig.directionalCrossedPlantFacePlayerOnPlacement;
        int topMeta = 8;
        if (shouldMarkFacing && placer != null) {
            int sunflowerFacing = ((net.minecraft.util.MathHelper
                    .floor_double((double) (placer.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3) + 3) % 4;
            topMeta = 8 | sunflowerFacing;
        }
        world.setBlock(x, y + 1, z, this, topMeta, 2);
        RFPlantContext.markPlayerPlaced(world, x, y, z);
        RFPlantContext.markPlayerPlaced(world, x, y + 1, z);
        if (shouldMarkFacing && placer != null) {
            RFPlantContext.markCrossedPlantFacingFromPlacer(world, x, y, z, placer);
            RFPlantContext.markCrossedPlantFacingFromPlacer(world, x, y + 1, z, placer);
        }
    }

    @Override
    public int getRenderType() {
        return OffLawnRenderIds.sunflowerBushRenderId >= 0 ? OffLawnRenderIds.sunflowerBushRenderId : 1;
    }

    @Override
    public void onBlockAdded(World world, int x, int y, int z) {
        super.onBlockAdded(world, x, y, z);
        normalizeTypeMetadata(world, x, y, z);
        scheduleAuraUpdate(world, x, y, z);
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block neighbor) {
        super.onNeighborBlockChange(world, x, y, z, neighbor);
        normalizeTypeMetadata(world, x, y, z);
    }

    public void placeAt(World world, int x, int y, int z, int flags) {
        // Use non-sunflower variant metadata to suppress vanilla sunflower head overlay rendering.
        func_149889_c(world, x, y, z, 1, flags);
        world.setBlockMetadataWithNotify(x, y, z, 1, 2);
    }

    @Override
    public void updateTick(World world, int x, int y, int z, Random random) {
        super.updateTick(world, x, y, z, random);
        if (world.isRemote || BlockDoublePlant.func_149887_c(world.getBlockMetadata(x, y, z))) {
            return;
        }
        OffLawnSunflowerAuraHandler.refreshAura(world, x, y, z, this == OffLawnContent.brightSunflower);
        scheduleAuraUpdate(world, x, y, z);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register) {
        // Initialize BlockDoublePlant's internal icon arrays used by RenderBlocks.
        super.registerBlockIcons(register);
        this.bottomIcon = register.registerIcon(this.bottomTexture);
        this.topIcon = register.registerIcon(this.topTexture);
        this.blockIcon = this.bottomIcon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        return BlockDoublePlant.func_149887_c(meta) ? topIcon : bottomIcon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon func_149888_a(boolean top, int type) {
        return top ? topIcon : bottomIcon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item item, CreativeTabs tab, List list) {
        list.add(new ItemStack(item, 1, 1));
    }

    @Override
    public boolean isShearable(ItemStack item, IBlockAccess world, int x, int y, int z) {
        int meta = world.getBlockMetadata(x, y, z);
        return BlockDoublePlant.func_149887_c(meta);
    }

    @Override
    public ArrayList<ItemStack> onSheared(ItemStack item, IBlockAccess world, int x, int y, int z, int fortune) {
        return new ArrayList<ItemStack>();
    }

    private static boolean canPlaceOnBeanstalk(World world, int x, int y, int z) {
        return world.getBlock(x, y - 1, z) == OffLawnContent.beanstalk;
    }

    private void scheduleAuraUpdate(World world, int x, int y, int z) {
        if (world == null || world.isRemote || BlockDoublePlant.func_149887_c(world.getBlockMetadata(x, y, z))) {
            return;
        }
        int interval = OffLawnSunflowerAuraHandler.getRefreshIntervalTicks(this == OffLawnContent.brightSunflower);
        if (interval > 0) {
            world.scheduleBlockUpdate(x, y, z, this, interval);
        }
    }

    private static void normalizeTypeMetadata(World world, int x, int y, int z) {
        int meta = world.getBlockMetadata(x, y, z);
        if (BlockDoublePlant.func_149887_c(meta)) {
            return;
        }
        if (BlockDoublePlant.func_149890_d(meta) == 0) {
            world.setBlockMetadataWithNotify(x, y, z, 1, 2);
        }
    }
}
