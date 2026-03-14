package com.voidsrift.riftflux.furniture.block;

import com.voidsrift.riftflux.furniture.FurnitureRenderIds;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockFurnitureFence extends Block {
    private final boolean dark;
    private final String iconName;

    public BlockFurnitureFence(boolean dark, String iconName) {
        super(dark ? Material.iron : Material.wood);
        this.dark = dark;
        this.iconName = iconName;
        this.setHardness(2.0F);
        this.setStepSound(dark ? soundTypeMetal : soundTypeWood);
        this.setLightOpacity(0);
        this.useNeighborBrightness = true;
        this.setCreativeTab(CreativeTabs.tabDecorations);
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.6F, 1.0F);
    }

    public boolean isDark() {
        return this.dark;
    }

    public boolean connectsTo(IBlockAccess world, int x, int y, int z) {
        Block block = world.getBlock(x, y, z);
        return block instanceof BlockFurnitureFence
                || block == Blocks.fence
                || block == Blocks.nether_brick_fence
                || block == Blocks.fence_gate
                || block.isNormalCube();
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
        return FurnitureRenderIds.fenceRenderId;
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
        this.setBlockBounds(0.4375F, 0.0F, 0.4375F, 0.5625F, 1.5F, 0.5625F);
        super.addCollisionBoxesToList(world, x, y, z, axisAlignedBB, list, entity);

        if (this.connectsTo(world, x + 1, y, z)) {
            this.setBlockBounds(0.4375F, 0.0F, 0.4375F, 1.0F, 1.5F, 0.5625F);
            super.addCollisionBoxesToList(world, x, y, z, axisAlignedBB, list, entity);
        }
        if (this.connectsTo(world, x - 1, y, z)) {
            this.setBlockBounds(0.0F, 0.0F, 0.4375F, 0.5625F, 1.5F, 0.5625F);
            super.addCollisionBoxesToList(world, x, y, z, axisAlignedBB, list, entity);
        }
        if (this.connectsTo(world, x, y, z + 1)) {
            this.setBlockBounds(0.4375F, 0.0F, 0.4375F, 0.5625F, 1.5F, 1.0F);
            super.addCollisionBoxesToList(world, x, y, z, axisAlignedBB, list, entity);
        }
        if (this.connectsTo(world, x, y, z - 1)) {
            this.setBlockBounds(0.4375F, 0.0F, 0.0F, 0.5625F, 1.5F, 0.5625F);
            super.addCollisionBoxesToList(world, x, y, z, axisAlignedBB, list, entity);
        }
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
        float minX = 0.4375F;
        float maxX = 0.5625F;
        float minZ = 0.4375F;
        float maxZ = 0.5625F;

        if (this.connectsTo(world, x + 1, y, z)) {
            maxX = 1.0F;
        }
        if (this.connectsTo(world, x - 1, y, z)) {
            minX = 0.0F;
        }
        if (this.connectsTo(world, x, y, z + 1)) {
            maxZ = 1.0F;
        }
        if (this.connectsTo(world, x, y, z - 1)) {
            minZ = 0.0F;
        }

        this.setBlockBounds(minX, 0.0F, minZ, maxX, 1.1F, maxZ);
    }

    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z) {
        return new ItemStack(this);
    }

    @Override
    public void registerBlockIcons(IIconRegister register) {
        this.blockIcon = register.registerIcon(this.iconName);
    }
}
