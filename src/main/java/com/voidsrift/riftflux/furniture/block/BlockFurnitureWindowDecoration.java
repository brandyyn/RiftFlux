package com.voidsrift.riftflux.furniture.block;

import com.voidsrift.riftflux.furniture.FurnitureRenderIds;
import com.voidsrift.riftflux.furniture.util.FurnitureBlockHelper;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockFurnitureWindowDecoration extends Block {
    private final boolean blind;
    private final String iconName;
    private final int curtainColor;

    public BlockFurnitureWindowDecoration(boolean blind, String iconName) {
        this(blind, iconName, 14);
    }

    public BlockFurnitureWindowDecoration(boolean blind, String iconName, int curtainColor) {
        super(Material.cloth);
        this.blind = blind;
        this.iconName = iconName;
        this.curtainColor = curtainColor;
        this.setStepSound(soundTypeCloth);
        this.setHardness(0.2F);
        this.setLightOpacity(0);
        this.useNeighborBrightness = true;
        this.setCreativeTab(CreativeTabs.tabDecorations);
        this.setBlockBounds(0.0F, 0.0F, 0.9F, 1.0F, 1.0F, 1.0F);
    }

    public boolean isBlind() {
        return this.blind;
    }

    public int getCurtainColor() {
        return this.curtainColor;
    }

    public boolean isClosed(int metadata) {
        return (metadata & 4) != 0;
    }

    public int getRotation(int metadata) {
        return metadata & 3;
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
        return FurnitureRenderIds.windowDecorationRenderId;
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
    public void addCollisionBoxesToList(World world,
                                        int x,
                                        int y,
                                        int z,
                                        AxisAlignedBB axisAlignedBB,
                                        List list,
                                        Entity entity) {
        this.applyBounds(this.getRotation(world.getBlockMetadata(x, y, z)));
        super.addCollisionBoxesToList(world, x, y, z, axisAlignedBB, list, entity);
    }

    @Override
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
        this.applyBounds(this.getRotation(world.getBlockMetadata(x, y, z)));
        return super.getSelectedBoundingBoxFromPool(world, x, y, z);
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
        this.applyBounds(this.getRotation(world.getBlockMetadata(x, y, z)));
    }

    private void applyBounds(int rotation) {
        switch (rotation & 3) {
            case 0:
                this.setBlockBounds(0.0F, 0.0F, 0.9F, 1.0F, 1.0F, 1.0F);
                break;
            case 1:
                this.setBlockBounds(0.0F, 0.0F, 0.0F, 0.1F, 1.0F, 1.0F);
                break;
            case 2:
                this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.1F);
                break;
            case 3:
                this.setBlockBounds(0.9F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
                break;
            default:
                this.setBlockBounds(0.0F, 0.0F, 0.9F, 1.0F, 1.0F, 1.0F);
                break;
        }
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
        if (world.isRemote) {
            return true;
        }

        int metadata = world.getBlockMetadata(x, y, z);
        if (this.blind) {
            if (this.isClosed(metadata)) {
                world.setBlockMetadataWithNotify(x, y, z, metadata & 3, 3);
            }
            return true;
        }

        boolean closed = !this.isClosed(metadata);
        this.setCurtainClosedState(world, x, y, z, closed);
        this.setConnectedCurtainState(world, x, y, z, this.getRotation(metadata), FurnitureBlockHelper.CURTAIN_LEFT, closed);
        this.setConnectedCurtainState(world, x, y, z, this.getRotation(metadata), FurnitureBlockHelper.CURTAIN_RIGHT, closed);
        return true;
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack stack) {
        world.setBlockMetadataWithNotify(x, y, z, FurnitureBlockHelper.getRotationMeta(entity), 2);
    }

    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z) {
        return new ItemStack(this);
    }

    @Override
    public void registerBlockIcons(IIconRegister register) {
        this.blockIcon = register.registerIcon(this.iconName);
    }

    private void setCurtainClosedState(World world, int x, int y, int z, boolean closed) {
        int metadata = world.getBlockMetadata(x, y, z);
        int targetMetadata = (metadata & 3) | (closed ? 4 : 0);
        if (metadata != targetMetadata) {
            world.setBlockMetadataWithNotify(x, y, z, targetMetadata, 3);
        }
    }

    private void setConnectedCurtainState(World world, int x, int y, int z, int rotation, int direction, boolean closed) {
        int[] offset = FurnitureBlockHelper.getCurtainOffset(rotation, direction);
        int currentX = x + offset[0];
        int currentZ = z + offset[1];

        while (this.isConnectedCurtain(world, currentX, y, currentZ, rotation)) {
            BlockFurnitureWindowDecoration decoration =
                    (BlockFurnitureWindowDecoration) world.getBlock(currentX, y, currentZ);
            decoration.setCurtainClosedState(world, currentX, y, currentZ, closed);
            currentX += offset[0];
            currentZ += offset[1];
        }
    }

    private boolean isConnectedCurtain(World world, int x, int y, int z, int rotation) {
        Block block = world.getBlock(x, y, z);
        if (!(block instanceof BlockFurnitureWindowDecoration)) {
            return false;
        }

        BlockFurnitureWindowDecoration decoration = (BlockFurnitureWindowDecoration) block;
        if (decoration.isBlind()) {
            return false;
        }

        return decoration.getRotation(world.getBlockMetadata(x, y, z)) == rotation;
    }
}
