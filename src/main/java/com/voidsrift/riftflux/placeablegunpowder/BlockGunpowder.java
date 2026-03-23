package com.voidsrift.riftflux.placeablegunpowder;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.tweaks.ladder.client.RFRenderIds;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IIcon;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class BlockGunpowder extends Block {

    public static BlockGunpowder instance;

    private final Set<ChunkPosition> neighboursToNotify = new HashSet<ChunkPosition>();

    @SideOnly(Side.CLIENT)
    private IIcon crossIcon;
    @SideOnly(Side.CLIENT)
    private IIcon lineIcon;
    @SideOnly(Side.CLIENT)
    private IIcon crossOverlayIcon;
    @SideOnly(Side.CLIENT)
    private IIcon lineOverlayIcon;

    public BlockGunpowder() {
        super(Material.circuits);
        instance = this;
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.0625F, 1.0F);
        this.setHardness(0.1F);
        this.disableStats();
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        return null;
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
        return RFRenderIds.placeableGunpowderRenderId;
    }

    @Override
    public boolean canProvidePower() {
        return ModConfig.placeableGunpowderEmitsRedstone;
    }

    @Override
    public int isProvidingWeakPower(IBlockAccess world, int x, int y, int z, int side) {
        return ModConfig.placeableGunpowderEmitsRedstone && world.getBlockMetadata(x, y, z) > 0 ? 15 : 0;
    }

    @Override
    public boolean canConnectRedstone(IBlockAccess world, int x, int y, int z, int side) {
        return ModConfig.placeableGunpowderEmitsRedstone && side != -1;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public int colorMultiplier(IBlockAccess world, int x, int y, int z) {
        return 0x646464;
    }

    @Override
    public boolean canPlaceBlockAt(World world, int x, int y, int z) {
        return World.doesBlockHaveSolidTopSurface(world, x, y - 1, z) || world.getBlock(x, y - 1, z) == Blocks.glowstone;
    }

    private void sendNotifications(World world, int x, int y, int z) {
        ArrayList<ChunkPosition> positions = new ArrayList<ChunkPosition>(this.neighboursToNotify);
        this.neighboursToNotify.clear();
        for (ChunkPosition position : positions) {
            world.notifyBlocksOfNeighborChange(position.chunkPosX, position.chunkPosY, position.chunkPosZ, this);
        }
    }

    private void notifyNeighbours(World world, int x, int y, int z) {
        if (world.getBlock(x, y, z) != this) {
            return;
        }

        world.notifyBlocksOfNeighborChange(x, y, z, this);
        world.notifyBlocksOfNeighborChange(x - 1, y, z, this);
        world.notifyBlocksOfNeighborChange(x + 1, y, z, this);
        world.notifyBlocksOfNeighborChange(x, y, z - 1, this);
        world.notifyBlocksOfNeighborChange(x, y, z + 1, this);
        world.notifyBlocksOfNeighborChange(x, y - 1, z, this);
        world.notifyBlocksOfNeighborChange(x, y + 1, z, this);
    }

    @Override
    public void updateTick(World world, int x, int y, int z, Random rand) {
        int meta = world.getBlockMetadata(x, y, z);
        if (meta > 0) {
            if (meta >= 8) {
                world.setBlockToAir(x, y, z);
                explode(world, x, y, z);
            } else {
                if (meta >= 4) {
                    this.igniteNeighbours(world, x, y, z);
                }
                world.setBlockMetadataWithNotify(x, y, z, meta + 1, 2);
                world.scheduleBlockUpdate(x, y, z, this, 1);
            }
        }
    }

    public static void explode(World world, double x, double y, double z) {
        GunpowderExplosion explosion = new GunpowderExplosion(world, null, x, y, z, 0.5F);
        explosion.isFlaming = false;
        explosion.isSmoking = true;
        if (!ForgeEventFactory.onExplosionStart(world, explosion)) {
            explosion.doExplosion();
        }
    }

    @Override
    public void onBlockAdded(World world, int x, int y, int z) {
        super.onBlockAdded(world, x, y, z);
        if (!world.isRemote) {
            this.sendNotifications(world, x, y, z);
            world.notifyBlocksOfNeighborChange(x, y + 1, z, this);
            world.notifyBlocksOfNeighborChange(x, y - 1, z, this);
            this.notifyNeighbours(world, x - 1, y, z);
            this.notifyNeighbours(world, x + 1, y, z);
            this.notifyNeighbours(world, x, y, z - 1);
            this.notifyNeighbours(world, x, y, z + 1);
            if (world.getBlock(x - 1, y, z).isNormalCube()) {
                this.notifyNeighbours(world, x - 1, y + 1, z);
            } else {
                this.notifyNeighbours(world, x - 1, y - 1, z);
            }
            if (world.getBlock(x + 1, y, z).isNormalCube()) {
                this.notifyNeighbours(world, x + 1, y + 1, z);
            } else {
                this.notifyNeighbours(world, x + 1, y - 1, z);
            }
            if (world.getBlock(x, y, z - 1).isNormalCube()) {
                this.notifyNeighbours(world, x, y + 1, z - 1);
            } else {
                this.notifyNeighbours(world, x, y - 1, z - 1);
            }
            if (world.getBlock(x, y, z + 1).isNormalCube()) {
                this.notifyNeighbours(world, x, y + 1, z + 1);
            } else {
                this.notifyNeighbours(world, x, y - 1, z + 1);
            }
        }
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
        super.breakBlock(world, x, y, z, block, meta);
        if (!world.isRemote) {
            world.notifyBlocksOfNeighborChange(x, y + 1, z, this);
            world.notifyBlocksOfNeighborChange(x, y - 1, z, this);
            world.notifyBlocksOfNeighborChange(x + 1, y, z, this);
            world.notifyBlocksOfNeighborChange(x - 1, y, z, this);
            world.notifyBlocksOfNeighborChange(x, y, z + 1, this);
            world.notifyBlocksOfNeighborChange(x, y, z - 1, this);
            this.sendNotifications(world, x, y, z);
            this.notifyNeighbours(world, x - 1, y, z);
            this.notifyNeighbours(world, x + 1, y, z);
            this.notifyNeighbours(world, x, y, z - 1);
            this.notifyNeighbours(world, x, y, z + 1);
            if (world.getBlock(x - 1, y, z).isNormalCube()) {
                this.notifyNeighbours(world, x - 1, y + 1, z);
            } else {
                this.notifyNeighbours(world, x - 1, y - 1, z);
            }
            if (world.getBlock(x + 1, y, z).isNormalCube()) {
                this.notifyNeighbours(world, x + 1, y + 1, z);
            } else {
                this.notifyNeighbours(world, x + 1, y - 1, z);
            }
            if (world.getBlock(x, y, z - 1).isNormalCube()) {
                this.notifyNeighbours(world, x, y + 1, z - 1);
            } else {
                this.notifyNeighbours(world, x, y - 1, z - 1);
            }
            if (world.getBlock(x, y, z + 1).isNormalCube()) {
                this.notifyNeighbours(world, x, y + 1, z + 1);
            } else {
                this.notifyNeighbours(world, x, y - 1, z + 1);
            }
        }
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
        if (!world.isRemote) {
            if (this.canPlaceBlockAt(world, x, y, z)) {
                this.sendNotifications(world, x, y, z);
            } else {
                this.dropBlockAsItem(world, x, y, z, 0, 0);
                world.setBlockToAir(x, y, z);
            }
            super.onNeighborBlockChange(world, x, y, z, block);

            if (world.getBlockMetadata(x, y, z) > 0) {
                HbmExplosiveCompat.tryIgniteAdjacentExplosiveBarrels(world, x, y, z);
            }
        }

        if (this.checkNeighboursForFire(world, x, y, z)) {
            this.ignite(world, x, y, z);
        }
    }

    private boolean checkNeighboursForFire(World world, int x, int y, int z) {
        return world.getBlock(x - 1, y, z) == Blocks.fire
                || world.getBlock(x + 1, y, z) == Blocks.fire
                || world.getBlock(x, y - 1, z) == Blocks.fire
                || world.getBlock(x, y + 1, z) == Blocks.fire
                || world.getBlock(x, y, z - 1) == Blocks.fire
                || world.getBlock(x, y, z + 1) == Blocks.fire;
    }

    @Override
    public Item getItemDropped(int meta, Random rand, int fortune) {
        return Items.gunpowder;
    }

    public static boolean canConnect(IBlockAccess world, int x, int y, int z, int side) {
        return world.getBlock(x, y, z) == instance;
    }

    @Override
    public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
        if (entity instanceof EntityArrow && !world.isRemote && ((EntityArrow) entity).isBurning()) {
            this.ignite(world, x, y, z);
        }
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        if (player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().getItem() == Items.flint_and_steel) {
            this.ignite(world, x, y, z);
            player.getCurrentEquippedItem().damageItem(1, player);
            return true;
        }
        return super.onBlockActivated(world, x, y, z, player, side, hitX, hitY, hitZ);
    }

    @Override
    public void onBlockDestroyedByExplosion(World world, int x, int y, int z, Explosion explosion) {
        if (!world.isRemote && this.canPlaceBlockAt(world, x, y, z)) {
            world.setBlock(x, y, z, this);
            this.ignite(world, x, y, z);
        }
    }

    public void ignite(World world, int x, int y, int z) {
        if (world.getBlock(x, y, z) != this || world.getBlockMetadata(x, y, z) != 0) {
            return;
        }

        world.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, "gunpowder:gunpowder.ignite", 1.0F, 1.9F + world.rand.nextFloat() * 0.1F);
        world.setBlockMetadataWithNotify(x, y, z, 1, 2);
        if (!world.isRemote) {
            this.notifyNeighbours(world, x, y, z);
            HbmExplosiveCompat.tryIgniteAdjacentExplosiveBarrels(world, x, y, z);
        }
        world.scheduleBlockUpdate(x, y, z, this, 1);
    }

    public void igniteNeighbours(World world, int x, int y, int z) {
        this.ignite(world, x, y + 1, z);
        this.ignite(world, x, y - 1, z);
        this.ignite(world, x + 1, y, z);
        this.ignite(world, x - 1, y, z);
        this.ignite(world, x, y, z + 1);
        this.ignite(world, x, y, z - 1);
        if (world.getBlock(x - 1, y, z).isNormalCube()) {
            this.ignite(world, x - 1, y + 1, z);
        } else {
            this.ignite(world, x - 1, y - 1, z);
        }
        if (world.getBlock(x + 1, y, z).isNormalCube()) {
            this.ignite(world, x + 1, y + 1, z);
        } else {
            this.ignite(world, x + 1, y - 1, z);
        }
        if (world.getBlock(x, y, z - 1).isNormalCube()) {
            this.ignite(world, x, y + 1, z - 1);
        } else {
            this.ignite(world, x, y - 1, z - 1);
        }
        if (world.getBlock(x, y, z + 1).isNormalCube()) {
            this.ignite(world, x, y + 1, z + 1);
        } else {
            this.ignite(world, x, y - 1, z + 1);
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
        int meta = world.getBlockMetadata(x, y, z);
        if (meta > 0) {
            double particleX = x + 0.5D + (rand.nextFloat() - 0.5D) * 0.5D;
            double particleY = y + 0.0625D;
            double particleZ = z + 0.5D + (rand.nextFloat() - 0.5D) * 0.5D;
            float progress = (float) meta / 15.0F;
            float velocityY = progress * 0.03F;
            float velocityX = rand.nextFloat() * 0.02F - 0.01F;
            float velocityZ = rand.nextFloat() * 0.02F - 0.01F;
            world.spawnParticle("flame", particleX, particleY, particleZ, velocityX, velocityY, velocityZ);
            world.spawnParticle("largesmoke", particleX, particleY, particleZ, velocityX, velocityY, velocityZ);
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public Item getItem(World world, int x, int y, int z) {
        return Items.gunpowder;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerBlockIcons(IIconRegister registry) {
        this.crossIcon = registry.registerIcon(this.getTextureName() + "_cross");
        this.lineIcon = registry.registerIcon(this.getTextureName() + "_line");
        this.crossOverlayIcon = registry.registerIcon(this.getTextureName() + "_cross_overlay");
        this.lineOverlayIcon = registry.registerIcon(this.getTextureName() + "_line_overlay");
        this.blockIcon = this.crossIcon;
    }

    @SideOnly(Side.CLIENT)
    public static IIcon getGunpowderIcon(String iconName) {
        if ("cross".equals(iconName)) {
            return instance.crossIcon;
        }
        if ("line".equals(iconName)) {
            return instance.lineIcon;
        }
        if ("cross_overlay".equals(iconName)) {
            return instance.crossOverlayIcon;
        }
        if ("line_overlay".equals(iconName)) {
            return instance.lineOverlayIcon;
        }
        return null;
    }

    static {
        BlockDispenser.dispenseBehaviorRegistry.putObject(Items.gunpowder, new BehaviorDefaultDispenseItem() {
            @Override
            protected ItemStack dispenseStack(IBlockSource blockSource, ItemStack stack) {
                EnumFacing facing = BlockDispenser.func_149937_b(blockSource.getBlockMetadata());
                IPosition position = BlockDispenser.func_149939_a(blockSource);
                int x = blockSource.getXInt() + facing.getFrontOffsetX();
                int y = blockSource.getYInt() + facing.getFrontOffsetY();
                int z = blockSource.getZInt() + facing.getFrontOffsetZ();

                if (instance.canPlaceBlockAt(blockSource.getWorld(), x, y, z)
                        && blockSource.getWorld().getBlock(x, y, z).isReplaceable(blockSource.getWorld(), x, y, z)) {
                    blockSource.getWorld().setBlock(x, y, z, instance);
                    stack.splitStack(1);
                }

                return stack;
            }
        });
    }
}
