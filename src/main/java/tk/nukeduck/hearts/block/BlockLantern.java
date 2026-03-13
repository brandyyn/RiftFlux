/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.block.Block
 *  net.minecraft.block.ITileEntityProvider
 *  net.minecraft.block.material.Material
 *  net.minecraft.client.renderer.texture.IIconRegister
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.ItemStack
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.MathHelper
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 *  net.minecraftforge.common.util.ForgeDirection
 */
package tk.nukeduck.hearts.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import tk.nukeduck.hearts.HeartCrystal;
import tk.nukeduck.hearts.block.TileEntityHeartLantern;
import tk.nukeduck.hearts.network.ClientProxy;
import tk.nukeduck.hearts.registry.HeartsBlocks;

public class BlockLantern
extends Block
implements ITileEntityProvider {
    public static final float CHARGE_TAKEN = 0.1f;

    public BlockLantern(Material material) {
        super(material);
        this.setLightLevel(0.8125f);
        this.setBlockBounds(0.3125f, 0.0f, 0.3125f, 0.6875f, 0.5f, 0.6875f);
    }

    @SideOnly(value=Side.CLIENT)
    public void randomDisplayTick(World world, int x, int y, int z, Random random) {
        TileEntity tileEntity = world.getTileEntity(x, y, z);
        if (!(tileEntity instanceof TileEntityHeartLantern)) {
            return;
        }
        TileEntityHeartLantern lantern = (TileEntityHeartLantern)tileEntity;
        float charge = lantern.chargeLevel;
        for (int i = 0; i < (int)(HeartCrystal.random.nextFloat() * charge * 4.0f); ++i) {
            double xRand = (double)x + this.getBlockBoundsMinX() + random.nextDouble() * (this.getBlockBoundsMaxX() - this.getBlockBoundsMinX());
            double yRand = (double)y + this.getBlockBoundsMinX() + random.nextDouble() * (this.getBlockBoundsMaxX() - this.getBlockBoundsMinX());
            double zRand = (double)z + this.getBlockBoundsMinX() + random.nextDouble() * (this.getBlockBoundsMaxX() - this.getBlockBoundsMinX());
            world.spawnParticle(HeartsBlocks.crystal.getParticle(), xRand, yRand, zRand, random.nextDouble() * 0.7 + 0.3, 0.0, 0.0);
        }
    }

    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int p_149727_6_, float p_149727_7_, float p_149727_8_, float p_149727_9_) {
        TileEntity tileEntity = world.getTileEntity(x, y, z);
        if (!(tileEntity instanceof TileEntityHeartLantern)) {
            return false;
        }
        TileEntityHeartLantern lantern = (TileEntityHeartLantern)tileEntity;
        if (lantern.chargeLevel >= CHARGE_TAKEN && player.getHealth() < player.getMaxHealth()) {
            if (world.isRemote) {
                this.spawnHealParticles(world, x, y, z);
                return true;
            }
            player.heal(1.0f);
            lantern.chargeLevel = Math.max(0.0f, lantern.chargeLevel - CHARGE_TAKEN);
            lantern.markDirty();
            world.markBlockForUpdate(x, y, z);
            return true;
        }
        return false;
    }

    private void spawnHealParticles(World world, int x, int y, int z) {
        for (int i = 0; i < 2; ++i) {
            double xRand = (double)x + HeartCrystal.random.nextDouble();
            double yRand = (double)y + HeartCrystal.random.nextDouble() - 0.5;
            double zRand = (double)z + HeartCrystal.random.nextDouble();
            world.spawnParticle("heart", xRand, yRand, zRand, HeartCrystal.random.nextDouble() * 0.5, HeartCrystal.random.nextDouble() * 0.5, HeartCrystal.random.nextDouble() * 0.5);
        }
    }

    public int onBlockPlaced(World p_149660_1_, int p_149660_2_, int p_149660_3_, int p_149660_4_, int p_149660_5_, float p_149660_6_, float p_149660_7_, float p_149660_8_, int p_149660_9_) {
        if (p_149660_5_ == 0 && this.hasAttachmentAbove(p_149660_1_, p_149660_2_, p_149660_3_, p_149660_4_)) {
            return 4;
        }
        return 0;
    }

    public void onBlockPlacedBy(World p_149689_1_, int p_149689_2_, int p_149689_3_, int p_149689_4_, EntityLivingBase p_149689_5_, ItemStack p_149689_6_) {
        int l = MathHelper.floor_double((double)((double)(p_149689_5_.rotationYaw * 4.0f / 360.0f) + 0.5)) & 3;
        p_149689_1_.setBlockMetadataWithNotify(p_149689_2_, p_149689_3_, p_149689_4_, p_149689_1_.getBlockMetadata(p_149689_2_, p_149689_3_, p_149689_4_) + l, 2);
    }

    public boolean canPlaceBlockOnSide(World world, int x, int y, int z, int side) {
        if (side == 0) {
            return this.hasAttachmentAbove(world, x, y, z);
        }
        if (side >= 1 && side <= 5) {
            return true;
        }
        return false;
    }

    public boolean canPlaceBlockAt(World world, int x, int y, int z) {
        return true;
    }

    public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
    }

    private boolean hasAttachmentAbove(World world, int x, int y, int z) {
        return !world.isAirBlock(x, y + 1, z);
    }

    @SideOnly(value=Side.CLIENT)
    public void registerBlockIcons(IIconRegister icon) {
        this.blockIcon = icon.registerIcon("hearts:lantern");
    }

    public boolean isOpaqueCube() {
        return false;
    }

    public boolean renderAsNormalBlock() {
        return false;
    }

    @SideOnly(value=Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockAccess iblockaccess, int i, int j, int k, int l) {
        return false;
    }

    @SideOnly(value=Side.CLIENT)
    public int getRenderType() {
        return ClientProxy.renderId;
    }

    public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
        return new TileEntityHeartLantern();
    }
}
