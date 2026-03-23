package com.voidsrift.riftflux.furniture.block;

import com.voidsrift.riftflux.Constants;
import com.voidsrift.riftflux.furniture.FurnitureRenderIds;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockDoorBell extends Block {
    public BlockDoorBell() {
        super(Material.circuits);
        this.setHardness(0.5F);
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
        return FurnitureRenderIds.doorBellRenderId;
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
    public int tickRate(World world) {
        return 20;
    }

    @Override
    public boolean canPlaceBlockOnSide(World world, int x, int y, int z, int side) {
        ForgeDirection dir = ForgeDirection.getOrientation(side);
        return dir == ForgeDirection.NORTH && world.isSideSolid(x, y, z + 1, ForgeDirection.NORTH)
                || dir == ForgeDirection.SOUTH && world.isSideSolid(x, y, z - 1, ForgeDirection.SOUTH)
                || dir == ForgeDirection.WEST && world.isSideSolid(x + 1, y, z, ForgeDirection.WEST)
                || dir == ForgeDirection.EAST && world.isSideSolid(x - 1, y, z, ForgeDirection.EAST);
    }

    @Override
    public boolean canPlaceBlockAt(World world, int x, int y, int z) {
        return world.isSideSolid(x - 1, y, z, ForgeDirection.EAST)
                || world.isSideSolid(x + 1, y, z, ForgeDirection.WEST)
                || world.isSideSolid(x, y, z - 1, ForgeDirection.SOUTH)
                || world.isSideSolid(x, y, z + 1, ForgeDirection.NORTH);
    }

    @Override
    public int onBlockPlaced(World world,
                             int x,
                             int y,
                             int z,
                             int side,
                             float hitX,
                             float hitY,
                             float hitZ,
                             int metadata) {
        if (side == 2 && world.isSideSolid(x, y, z + 1, ForgeDirection.NORTH)) {
            return 0;
        }
        if (side == 3 && world.isSideSolid(x, y, z - 1, ForgeDirection.SOUTH)) {
            return 2;
        }
        if (side == 4 && world.isSideSolid(x + 1, y, z, ForgeDirection.WEST)) {
            return 3;
        }
        if (side == 5 && world.isSideSolid(x - 1, y, z, ForgeDirection.EAST)) {
            return 1;
        }
        if (world.isSideSolid(x, y, z + 1, ForgeDirection.NORTH)) {
            return 0;
        }
        if (world.isSideSolid(x, y, z - 1, ForgeDirection.SOUTH)) {
            return 2;
        }
        if (world.isSideSolid(x - 1, y, z, ForgeDirection.EAST)) {
            return 1;
        }
        if (world.isSideSolid(x + 1, y, z, ForgeDirection.WEST)) {
            return 3;
        }
        return 0;
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
        if (!this.canPlaceBlockAt(world, x, y, z)) {
            this.notifyDoorBellNeighbors(world, x, y, z, world.getBlockMetadata(x, y, z));
            this.dropBlockAsItem(world, x, y, z, 0, 0);
            world.setBlockToAir(x, y, z);
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
        if (player.isSneaking()) {
            return true;
        }
        if (world.isRemote) {
            return true;
        }

        int metadata = world.getBlockMetadata(x, y, z);
        if ((metadata & 8) != 0) {
            return true;
        }

        world.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, Constants.MODID + ":doorbell", 0.75F, 1.0F);
        world.setBlockMetadataWithNotify(x, y, z, metadata | 8, 3);
        this.notifyDoorBellNeighbors(world, x, y, z, metadata | 8);
        world.markBlockRangeForRenderUpdate(x, y, z, x, y, z);
        world.scheduleBlockUpdate(x, y, z, this, this.tickRate(world));
        return true;
    }

    @Override
    public void updateTick(World world, int x, int y, int z, Random random) {
        if (!world.isRemote) {
            int metadata = world.getBlockMetadata(x, y, z);
            if ((metadata & 8) != 0) {
                world.setBlockMetadataWithNotify(x, y, z, metadata & 7, 3);
                this.notifyDoorBellNeighbors(world, x, y, z, metadata & 7);
                world.markBlockRangeForRenderUpdate(x, y, z, x, y, z);
            }
        }
    }

    @Override
    public boolean canProvidePower() {
        return true;
    }

    @Override
    public int isProvidingWeakPower(IBlockAccess world, int x, int y, int z, int side) {
        return (world.getBlockMetadata(x, y, z) & 8) != 0 ? 15 : 0;
    }

    @Override
    public int isProvidingStrongPower(IBlockAccess world, int x, int y, int z, int side) {
        int metadata = world.getBlockMetadata(x, y, z);
        if ((metadata & 8) == 0) {
            return 0;
        }
        return side == this.getPoweredSide(metadata) ? 15 : 0;
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
        this.applyBounds(world.getBlockMetadata(x, y, z));
    }

    private void applyBounds(int metadata) {
        int direction = metadata & 7;
        boolean pressed = (metadata & 8) != 0;
        float minY = 0.3F;
        float maxY = 0.7F;
        float halfWidth = 0.1F;
        float depth = pressed ? 0.11F : 0.15F;

        if (direction == 1) {
            this.setBlockBounds(0.0F, minY, 0.5F - halfWidth, depth, maxY, 0.5F + halfWidth);
        } else if (direction == 3) {
            this.setBlockBounds(1.0F - depth, minY, 0.5F - halfWidth, 1.0F, maxY, 0.5F + halfWidth);
        } else if (direction == 2) {
            this.setBlockBounds(0.5F - halfWidth, minY, 0.0F, 0.5F + halfWidth, maxY, depth);
        } else {
            this.setBlockBounds(0.5F - halfWidth, minY, 1.0F - depth, 0.5F + halfWidth, maxY, 1.0F);
        }
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        return null;
    }

    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z) {
        return new ItemStack(this);
    }

    @Override
    public void registerBlockIcons(IIconRegister register) {
        this.blockIcon = register.registerIcon("riftflux:furniture_doorbell");
    }

    private void notifyDoorBellNeighbors(World world, int x, int y, int z, int metadata) {
        if (world == null) {
            return;
        }

        world.notifyBlocksOfNeighborChange(x, y, z, this);
        int direction = metadata & 7;
        if (direction == 0) {
            world.notifyBlocksOfNeighborChange(x, y, z + 1, this);
        } else if (direction == 1) {
            world.notifyBlocksOfNeighborChange(x - 1, y, z, this);
        } else if (direction == 2) {
            world.notifyBlocksOfNeighborChange(x, y, z - 1, this);
        } else if (direction == 3) {
            world.notifyBlocksOfNeighborChange(x + 1, y, z, this);
        }
    }

    private int getPoweredSide(int metadata) {
        int direction = metadata & 7;
        if (direction == 0) {
            return 2;
        }
        if (direction == 1) {
            return 5;
        }
        if (direction == 2) {
            return 3;
        }
        if (direction == 3) {
            return 4;
        }
        return 0;
    }
}
