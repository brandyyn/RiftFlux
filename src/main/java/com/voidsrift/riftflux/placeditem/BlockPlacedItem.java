package com.voidsrift.riftflux.placeditem;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Facing;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.util.Vec3;

public class BlockPlacedItem extends Block {
    public BlockPlacedItem() {
        super(Material.circuits);
        setHardness(0.66F);
        setResistance(20.0F);
        setBlockName("riftfluxPlacedItem");
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerBlockIcons(IIconRegister iconRegister) {
        blockIcon = iconRegister.registerIcon("minecraft:stone");
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
        AxisAlignedBB bounds = getItemBounds(world, x, y, z);
        if (bounds != null) {
            setBlockBounds(
                    (float) (bounds.minX - x),
                    (float) (bounds.minY - y),
                    (float) (bounds.minZ - z),
                    (float) (bounds.maxX - x),
                    (float) (bounds.maxY - y),
                    (float) (bounds.maxZ - z)
            );
            return;
        }
        int meta = world.getBlockMetadata(x, y, z);
        switch (meta) {
            case 0:
                setBlockBounds(0.0F, 0.8F, 0.0F, 1.0F, 1.0F, 1.0F);
                break;
            case 1:
                setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.2F, 1.0F);
                break;
            case 2:
                setBlockBounds(0.0F, 0.0F, 0.8F, 1.0F, 1.0F, 1.0F);
                break;
            case 3:
                setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.2F);
                break;
            case 4:
                setBlockBounds(0.8F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
                break;
            case 5:
                setBlockBounds(0.0F, 0.0F, 0.0F, 0.2F, 1.0F, 1.0F);
                break;
            default:
                super.setBlockBoundsBasedOnState(world, x, y, z);
                break;
        }
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
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(x, y, z);
        TilePlacedItem tile = te instanceof TilePlacedItem ? (TilePlacedItem) te : null;
        if (tile != null && tile.getStack() != null && tile.getStack().getItem() instanceof ItemBlock) {
            setBlockBoundsBasedOnState(world, x, y, z);
            return super.getCollisionBoundingBoxFromPool(world, x, y, z);
        }
        return null;
    }

    @Override
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
        setBlockBoundsBasedOnState(world, x, y, z);
        AxisAlignedBB bounds = super.getSelectedBoundingBoxFromPool(world, x, y, z);
        if (bounds == null) {
            return null;
        }
        return bounds.offset(0.0D, -0.5D / 64.0D, 0.0D);
    }

    private AxisAlignedBB getItemBounds(IBlockAccess world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(x, y, z);
        TilePlacedItem tile = te instanceof TilePlacedItem ? (TilePlacedItem) te : null;
        int meta = world.getBlockMetadata(x, y, z);
        if (tile != null && tile.getStack() != null) {
            ItemStack stack = tile.getStack();
            boolean isBlock = stack.getItem() instanceof ItemBlock;
            if (isBlock) {
                int face = meta;
                if (face >= 2 && face < Facing.oppositeSide.length) {
                    face = Facing.oppositeSide[face];
                }
                AxisAlignedBB box = AxisAlignedBB.getBoundingBox(0.25D, 0.0D, 0.25D, 0.75D, 0.5D, 0.75D);
                box = rotateBoxForFace(box, face);
                return box.offset(x, y, z);
            } else {
                int face = (meta >= 0 && meta < Facing.oppositeSide.length)
                        ? Facing.oppositeSide[meta]
                        : meta;
                final double thickness = 0.09375D;
                switch (face) {
                    case 0:
                        return AxisAlignedBB.getBoundingBox(x, y + (1.0D - thickness), z, x + 1.0D, y + 1.0D, z + 1.0D);
                    case 1:
                        return AxisAlignedBB.getBoundingBox(x, y + 0.0D, z, x + 1.0D, y + thickness, z + 1.0D);
                    case 2:
                        return AxisAlignedBB.getBoundingBox(x, y, z + (1.0D - thickness), x + 1.0D, y + 1.0D, z + 1.0D);
                    case 3:
                        return AxisAlignedBB.getBoundingBox(x, y, z + 0.0D, x + 1.0D, y + 1.0D, z + thickness);
                    case 4:
                        return AxisAlignedBB.getBoundingBox(x + (1.0D - thickness), y, z, x + 1.0D, y + 1.0D, z + 1.0D);
                    case 5:
                        return AxisAlignedBB.getBoundingBox(x + 0.0D, y, z, x + thickness, y + 1.0D, z + 1.0D);
                    default:
                        break;
                }
            }
        }
        return null;
    }

    private static AxisAlignedBB rotateBoxForFace(AxisAlignedBB box, int meta) {
        switch (meta) {
            case 1:
                return rotateAabb(box, 0, 0, 2);
            case 2:
                return rotateAabb(box, 1, 0, 0);
            case 3:
                return rotateAabb(box, 1, 2, 0);
            case 4:
                return rotateAabb(box, 1, 1, 0);
            case 5:
                return rotateAabb(box, 1, 3, 0);
            default:
                return box;
        }
    }

    private static AxisAlignedBB rotateAabb(AxisAlignedBB box, int rotX, int rotY, int rotZ) {
        double[][] corners = new double[][]{
                {box.minX, box.minY, box.minZ},
                {box.minX, box.minY, box.maxZ},
                {box.minX, box.maxY, box.minZ},
                {box.minX, box.maxY, box.maxZ},
                {box.maxX, box.minY, box.minZ},
                {box.maxX, box.minY, box.maxZ},
                {box.maxX, box.maxY, box.minZ},
                {box.maxX, box.maxY, box.maxZ}
        };

        for (double[] corner : corners) {
            rotatePoint(corner, rotX, rotY, rotZ);
        }

        double minX = corners[0][0];
        double minY = corners[0][1];
        double minZ = corners[0][2];
        double maxX = corners[0][0];
        double maxY = corners[0][1];
        double maxZ = corners[0][2];
        for (int i = 1; i < corners.length; i++) {
            double[] corner = corners[i];
            if (corner[0] < minX) minX = corner[0];
            if (corner[1] < minY) minY = corner[1];
            if (corner[2] < minZ) minZ = corner[2];
            if (corner[0] > maxX) maxX = corner[0];
            if (corner[1] > maxY) maxY = corner[1];
            if (corner[2] > maxZ) maxZ = corner[2];
        }
        return AxisAlignedBB.getBoundingBox(minX, minY, minZ, maxX, maxY, maxZ);
    }

    private static void rotatePoint(double[] point, int rotX, int rotY, int rotZ) {
        double x = point[0] - 0.5D;
        double y = point[1] - 0.5D;
        double z = point[2] - 0.5D;

        for (int i = 0; i < rotX; i++) {
            double ny = z;
            double nz = -y;
            y = ny;
            z = nz;
        }
        for (int i = 0; i < rotY; i++) {
            double nx = z;
            double nz = -x;
            x = nx;
            z = nz;
        }
        for (int i = 0; i < rotZ; i++) {
            double nx = -y;
            double ny = x;
            x = nx;
            y = ny;
        }

        point[0] = x + 0.5D;
        point[1] = y + 0.5D;
        point[2] = z + 0.5D;
    }

    @Override
    public int getRenderType() {
        return -1;
    }

    @Override
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
        TileEntity te = world.getTileEntity(x, y, z);
        TilePlacedItem tile = te instanceof TilePlacedItem ? (TilePlacedItem) te : null;
        if (tile != null) {
            ItemStack stack = tile.getStack();
            if (stack != null && stack.getItem() != null) {
                if (stack.getItem() instanceof ItemBlock) {
                    Block block = Block.getBlockFromItem(stack.getItem());
                    if (block != null) {
                        return block.getIcon(side, stack.getItemDamage());
                    }
                } else {
                    IIcon icon = stack.getItem().getIcon(stack, 0);
                    if (icon != null) {
                        return icon;
                    }
                }
            }
        }
        return this.blockIcon;
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        return this.blockIcon;
    }

    @Override
    public boolean hasTileEntity(int meta) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, int meta) {
        return new TilePlacedItem();
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TilePlacedItem) {
            TilePlacedItem tile = (TilePlacedItem) te;
            if (tile.getStack() != null) {
                float fX = x + world.rand.nextFloat();
                float fY = y + world.rand.nextFloat();
                float fZ = z + world.rand.nextFloat();
                EntityItem entityItem = new EntityItem(world, fX, fY, fZ, tile.getStack());
                tile.setStack(null);
                float f = 0.05F;
                entityItem.motionX = (-0.5F + world.rand.nextFloat()) * f;
                entityItem.motionY = (4.0F + world.rand.nextFloat()) * f;
                entityItem.motionZ = (-0.5F + world.rand.nextFloat()) * f;
                world.spawnEntityInWorld(entityItem);
            }
        }
        super.breakBlock(world, x, y, z, block, meta);
    }

    @Override
    public Item getItemDropped(int meta, java.util.Random rand, int fortune) {
        return null;
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player,
                                    int side, float hitX, float hitY, float hitZ) {
        if (player.isSneaking()) {
            TileEntity te = world.getTileEntity(x, y, z);
            TilePlacedItem tile = te instanceof TilePlacedItem ? (TilePlacedItem) te : null;
            if (tile == null) {
                world.setBlockToAir(x, y, z);
            } else {
                tile.rotation = normalizeRotation(tile.rotation + 22.5F);
            }
        } else {
            if (!world.isRemote) {
                TileEntity te = world.getTileEntity(x, y, z);
                TilePlacedItem tile = te instanceof TilePlacedItem ? (TilePlacedItem) te : null;
                if (tile == null) {
                    world.setBlockToAir(x, y, z);
                } else {
                    ItemStack stack = tile.getStack();
                    tile.setStack(null);
                    if (stack != null) {
                        ItemStack held = player.inventory.getStackInSlot(player.inventory.currentItem);
                        if (held == null) {
                            player.inventory.setInventorySlotContents(player.inventory.currentItem, stack);
                            player.inventory.markDirty();
                        } else {
                            EntityItem entityItem = new EntityItem(world, x + 0.5D, y + 0.5D, z + 0.5D, stack);
                            entityItem.delayBeforeCanPickup = 0;
                            world.spawnEntityInWorld(entityItem);
                            entityItem.onCollideWithPlayer(player);
                        }
                    }
                    world.setBlockToAir(x, y, z);
                }
            }
        }
        world.markBlockForUpdate(x, y, z);
        return true;
    }

    private EntityItem spawnDrop(World world, int x, int y, int z, ItemStack stack) {
        if (stack == null) {
            return null;
        }
        float fX = x + world.rand.nextFloat();
        float fY = y + world.rand.nextFloat();
        float fZ = z + world.rand.nextFloat();
        EntityItem entityItem = new EntityItem(world, fX, fY, fZ, stack);
        float f = 0.05F;
        entityItem.motionX = (-0.5F + world.rand.nextFloat()) * f;
        entityItem.motionY = (4.0F + world.rand.nextFloat()) * f;
        entityItem.motionZ = (-0.5F + world.rand.nextFloat()) * f;
        world.spawnEntityInWorld(entityItem);
        return entityItem;
    }

    private static float normalizeRotation(float rotation) {
        float adjusted = rotation % 360.0F;
        if (adjusted < 0.0F) {
            adjusted += 360.0F;
        }
        return adjusted;
    }

    @Override
    public void onBlockClicked(World world, int x, int y, int z, EntityPlayer player) {
        // No special action on left click.
    }

    @Override
    public int getLightValue(IBlockAccess world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TilePlacedItem) {
            TilePlacedItem tile = (TilePlacedItem) te;
            ItemStack stack = tile.getStack();
            if (stack != null && stack.getItem() instanceof ItemBlock) {
                return Block.getBlockFromItem(stack.getItem()).getLightValue();
            }
        }
        return 0;
    }

    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TilePlacedItem) {
            return ((TilePlacedItem) te).getStack();
        }
        return null;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean addHitEffects(World world, MovingObjectPosition target, EffectRenderer effectRenderer) {
        ItemStack stack = getPlacedStack(world, target.blockX, target.blockY, target.blockZ);
        String particle = getParticleName(stack, false);
        if (particle == null) {
            return false;
        }
        Vec3 hitVec = target.hitVec;
        double px = hitVec != null ? hitVec.xCoord : target.blockX + 0.5D;
        double py = hitVec != null ? hitVec.yCoord : target.blockY + 0.5D;
        double pz = hitVec != null ? hitVec.zCoord : target.blockZ + 0.5D;
        world.spawnParticle(particle, px, py, pz, 0.0D, 0.0D, 0.0D);
        return true;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean addDestroyEffects(World world, int x, int y, int z, int meta, EffectRenderer effectRenderer) {
        ItemStack stack = getPlacedStack(world, x, y, z);
        String particle = getParticleName(stack, false);
        if (particle == null) {
            return false;
        }
        for (int i = 0; i < 1; i++) {
            double px = x + world.rand.nextDouble();
            double py = y + world.rand.nextDouble();
            double pz = z + world.rand.nextDouble();
            double mx = px - x - 0.5D;
            double my = py - y - 0.5D;
            double mz = pz - z - 0.5D;
            world.spawnParticle(particle, px, py, pz, mx, my, mz);
        }
        return true;
    }


    @SideOnly(Side.CLIENT)
    public boolean addRunningEffects(World world, int x, int y, int z, Entity entity) {
        ItemStack stack = getPlacedStack(world, x, y, z);
        if (stack == null) {
            return false;
        }
        if (!(stack.getItem() instanceof ItemBlock)) {
            return true;
        }
        String particle = getParticleName(stack, true);
        if (particle == null) {
            return false;
        }
        double px = entity.posX + (world.rand.nextDouble() - 0.5D) * entity.width;
        double py = entity.boundingBox.minY + 0.1D;
        double pz = entity.posZ + (world.rand.nextDouble() - 0.5D) * entity.width;
        double mx = (world.rand.nextDouble() - 0.5D) * 0.02D;
        double my = 0.0D;
        double mz = (world.rand.nextDouble() - 0.5D) * 0.02D;
        world.spawnParticle(particle, px, py, pz, mx, my, mz);
        return true;
    }

    @SideOnly(Side.CLIENT)
    public boolean addLandingEffects(World world, int x, int y, int z, EntityLivingBase entity, int numberOfParticles) {
        ItemStack stack = getPlacedStack(world, x, y, z);
        if (stack == null) {
            return false;
        }
        if (!(stack.getItem() instanceof ItemBlock)) {
            return true;
        }
        String particle = getParticleName(stack, true);
        if (particle == null) {
            return false;
        }
        int count = Math.min(6, numberOfParticles);
        for (int i = 0; i < count; i++) {
            double px = entity.posX + (world.rand.nextDouble() - 0.5D) * entity.width;
            double py = entity.boundingBox.minY + 0.1D;
            double pz = entity.posZ + (world.rand.nextDouble() - 0.5D) * entity.width;
            double mx = (world.rand.nextDouble() - 0.5D) * 0.02D;
            double my = 0.0D;
            double mz = (world.rand.nextDouble() - 0.5D) * 0.02D;
            world.spawnParticle(particle, px, py, pz, mx, my, mz);
        }
        return true;
    }

    @SideOnly(Side.CLIENT)
    private static ItemStack getPlacedStack(IBlockAccess world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TilePlacedItem) {
            return ((TilePlacedItem) te).getStack();
        }
        return null;
    }

    @SideOnly(Side.CLIENT)
    private static String getParticleName(ItemStack stack, boolean dust) {
        if (stack == null || stack.getItem() == null) {
            return null;
        }
        if (stack.getItem() instanceof ItemBlock) {
            Block stackBlock = Block.getBlockFromItem(stack.getItem());
            if (stackBlock == null) {
                return null;
            }
            int blockId = Block.getIdFromBlock(stackBlock);
            int meta = stack.getItemDamage();
            return (dust ? "blockdust_" : "blockcrack_") + blockId + "_" + meta;
        }
        int itemId = Item.getIdFromItem(stack.getItem());
        if (itemId < 0) {
            return null;
        }
        return "iconcrack_" + itemId + "_" + stack.getItemDamage();
    }

}
