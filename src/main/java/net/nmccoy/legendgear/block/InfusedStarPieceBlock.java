package net.nmccoy.legendgear.block;

import com.voidsrift.riftflux.ModConfig;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.nmccoy.legendgear.LegendGear2;
import net.nmccoy.legendgear.legacy.LegendGear;
import net.nmccoy.legendgear.legacy.entities.EntityGrindStar;

public class InfusedStarPieceBlock extends BlockContainer {

    public InfusedStarPieceBlock() {
        super(Material.glass);
        this.setBlockName("infusedStarPieceBlock");
        this.setBlockTextureName("legendgear:starPieceAnim");
        this.setLightLevel((float)Math.max(0, Math.min(15, ModConfig.legendGearPlacedStarPiecesLightLevel)) / 15.0f);
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
    public void randomDisplayTick(World world, int x, int y, int z, Random random) {
        double centerX = x + 0.5F;
        double centerY = y + 0.5F;
        double centerZ = z + 0.5F;
        double ox = random.nextGaussian() * 0.08D;
        double oy = random.nextGaussian() * 0.08D - 0.04D;
        double oz = random.nextGaussian() * 0.08D;
        LegendGear.proxy.addSparkleParticle(world, centerX + ox, centerY + oy, centerZ + oz, 0.0D, 0.0D, 0.0D, 0.5f);
    }

    @Override
    public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
        if (!LegendGear.infusedStarPiecesActAsStarbeamRails) {
            return;
        }
        if (!(entity instanceof EntityPlayer)) {
            return;
        }

        EntityPlayer player = (EntityPlayer) entity;
        if (player.fallDistance > 0.1f && player.ridingEntity == null && !world.isRemote) {
            this.startTravel(world, x, y, z, player);
        }
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        if (!LegendGear.infusedStarPiecesActAsStarbeamRails) {
            return false;
        }
        if (!LegendGear.starbeamRailRightClickTravel) {
            return false;
        }
        if (world.isRemote) {
            return true;
        }
        return this.startTravel(world, x, y, z, player);
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

    private boolean startTravel(World world, int x, int y, int z, EntityPlayer player) {
        if (player.ridingEntity != null) {
            return false;
        }

        EntityGrindStar star = EntityGrindStar.tryMakingStar(world, x, y, z, player, LegendGear.starbeamRailRightClickAnyDirection);
        if (star == null) {
            return false;
        }

        world.spawnEntityInWorld(star);
        player.mountEntity(star);
        return true;
    }
}
