package net.nmccoy.legendgear.block;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.nmccoy.legendgear.LegendGear2;

public class InfusedStarPieceBlock extends BlockContainer {

    public InfusedStarPieceBlock() {
        super(Material.glass);
        this.setBlockName("infusedStarPieceBlock");
        this.setBlockTextureName("legendgear:starPieceAnim");
        this.setLightLevel(1.0F);
        this.setHardness(0.2F);
        this.setStepSound(Block.soundTypeGlass);
        this.setBlockBounds(0.2F, 0.0F, 0.2F, 0.8F, 0.7F, 0.8F);
    }

    @Override
    public int getRenderType() {
        return -1;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public int getRenderBlockPass() {
        return 1;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityPlacedStar();
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        return null;
    }

    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z) {
        return new ItemStack(LegendGear2.starDust, 1, 4);
    }

    @Override
    public Item getItemDropped(int meta, Random random, int fortune) {
        return LegendGear2.starDust;
    }

    @Override
    public int damageDropped(int meta) {
        return 4;
    }
}
