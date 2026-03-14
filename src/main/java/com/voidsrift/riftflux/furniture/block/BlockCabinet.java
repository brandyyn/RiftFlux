package com.voidsrift.riftflux.furniture.block;

import com.voidsrift.riftflux.furniture.FurnitureGuiIds;
import com.voidsrift.riftflux.furniture.FurnitureRenderIds;
import com.voidsrift.riftflux.furniture.tileentity.TileEntityCabinet;
import com.voidsrift.riftflux.furniture.util.FurnitureBlockHelper;
import com.voidsrift.riftflux.furniture.util.FurnitureInventoryHelper;
import com.voidsrift.riftflux.riftflux;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockCabinet extends BlockContainer {
    private final Random random = new Random();

    public BlockCabinet() {
        super(Material.wood);
        this.setHardness(2.0F);
        this.setStepSound(soundTypeWood);
        this.setLightOpacity(0);
        this.useNeighborBrightness = true;
        this.setCreativeTab(CreativeTabs.tabDecorations);
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
    public int getRenderType() {
        return FurnitureRenderIds.cabinetRenderId;
    }

    @Override
    public int getRenderBlockPass() {
        return 1;
    }

    @Override
    public boolean canRenderInPass(int pass) {
        return pass == 1;
    }

    @Override
    public boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int side) {
        return true;
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int metadata) {
        if (!world.isRemote) {
            TileEntity tileEntity = world.getTileEntity(x, y, z);
            if (tileEntity instanceof TileEntityCabinet) {
                FurnitureInventoryHelper.dropInventory(world, x, y, z, (TileEntityCabinet) tileEntity, this.random);
            }
        }
        super.breakBlock(world, x, y, z, block, metadata);
    }

    @Override
    public boolean onBlockActivated(World world,
                                    int x,
                                    int y,
                                    int z,
                                    EntityPlayer player,
                                    int side,
                                    float hitX,
                                    float hitY,
                                    float hitZ) {
        if (player.isSneaking()) {
            return false;
        }
        if (!world.isRemote) {
            player.openGui(riftflux.instance, FurnitureGuiIds.CABINET, world, x, y, z);
        }
        return true;
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack stack) {
        world.setBlockMetadataWithNotify(x, y, z, FurnitureBlockHelper.getRotationMeta(entity), 2);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata) {
        return new TileEntityCabinet();
    }

    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z) {
        return new ItemStack(this);
    }

    @Override
    public void registerBlockIcons(IIconRegister register) {
        this.blockIcon = register.registerIcon("riftflux:furniture_cabinet");
    }
}
