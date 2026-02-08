package com.voidsrift.riftflux.placeditem;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.client.particle.EffectRenderer;

public class BlockPlacedItem extends Block {
    public BlockPlacedItem() {
        super(Material.circuits);
        setHardness(0.5F);
        setResistance(20.0F);
        setBlockName("riftfluxPlacedItem");
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerBlockIcons(IIconRegister iconRegister) {
        blockIcon = Blocks.air.getIcon(0, 0);
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
        setBlockBoundsBasedOnState(world, x, y, z);
        return super.getCollisionBoundingBoxFromPool(world, x, y, z);
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
                double blockYOffset = 0.0D;
                switch (meta) {
                    case 0:
                        return AxisAlignedBB.getBoundingBox(x + 0.25D, y + 0.5D + blockYOffset, z + 0.25D, x + 0.75D, y + 1.0D + blockYOffset, z + 0.75D);
                    case 1:
                        return AxisAlignedBB.getBoundingBox(x + 0.25D, y + 0.0D + blockYOffset, z + 0.25D, x + 0.75D, y + 0.5D + blockYOffset, z + 0.75D);
                    case 2:
                        return AxisAlignedBB.getBoundingBox(x + 0.25D, y + 0.25D + blockYOffset, z + 0.5D, x + 0.75D, y + 0.75D + blockYOffset, z + 1.0D);
                    case 3:
                        return AxisAlignedBB.getBoundingBox(x + 0.25D, y + 0.25D + blockYOffset, z + 0.0D, x + 0.75D, y + 0.75D + blockYOffset, z + 0.5D);
                    case 4:
                        return AxisAlignedBB.getBoundingBox(x + 0.5D, y + 0.25D + blockYOffset, z + 0.25D, x + 1.0D, y + 0.75D + blockYOffset, z + 0.75D);
                    case 5:
                        return AxisAlignedBB.getBoundingBox(x + 0.0D, y + 0.25D + blockYOffset, z + 0.25D, x + 0.5D, y + 0.75D + blockYOffset, z + 0.75D);
                    default:
                        break;
                }
            } else {
                final double pixel = 1.0D / 16.0D;
                switch (meta) {
                    case 0:
                        return AxisAlignedBB.getBoundingBox(x, y + (1.0D - pixel), z, x + 1.0D, y + 1.0D, z + 1.0D);
                    case 1:
                        return AxisAlignedBB.getBoundingBox(x, y + 0.0D, z, x + 1.0D, y + pixel, z + 1.0D);
                    case 2:
                        return AxisAlignedBB.getBoundingBox(x, y, z + (1.0D - pixel), x + 1.0D, y + 1.0D, z + 1.0D);
                    case 3:
                        return AxisAlignedBB.getBoundingBox(x, y, z + 0.0D, x + 1.0D, y + 1.0D, z + pixel);
                    case 4:
                        return AxisAlignedBB.getBoundingBox(x + (1.0D - pixel), y, z, x + 1.0D, y + 1.0D, z + 1.0D);
                    case 5:
                        return AxisAlignedBB.getBoundingBox(x + 0.0D, y, z, x + pixel, y + 1.0D, z + 1.0D);
                    default:
                        break;
                }
            }
        }
        return null;
    }

    @Override
    public int getRenderType() {
        return -1;
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
                tile.rotation = normalizeRotation(tile.rotation + 45.0F);
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
        return true;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean addDestroyEffects(World world, int x, int y, int z, int meta, EffectRenderer effectRenderer) {
        return true;
    }
}
