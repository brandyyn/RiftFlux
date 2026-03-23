package com.voidsrift.riftflux.terramine;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public abstract class BlockTerraModel extends BlockContainer {
    protected BlockTerraModel(Material material) {
        super(material);
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
        return -1;
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase placer, ItemStack stack) {
        if (placer == null) {
            return;
        }
        world.setBlockMetadataWithNotify(x, y, z, metadataFromYaw(placer.rotationYaw), 2);
    }

    protected static int metadataFromYaw(float rotationYaw) {
        switch (MathHelper.floor_double((double) (rotationYaw * 4.0F / 360.0F) + 0.5D) & 3) {
            case 0:
                return 2;
            case 1:
                return 5;
            case 2:
                return 3;
            default:
                return 4;
        }
    }
}
