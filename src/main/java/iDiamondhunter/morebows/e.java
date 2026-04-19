/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.registry.IEntityAdditionalSpawnData
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  io.netty.buffer.ByteBuf
 *  net.minecraft.block.Block
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.projectile.EntityArrow
 *  net.minecraft.init.Blocks
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.util.MathHelper
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 *  net.minecraftforge.common.util.ForgeDirection
 */
package iDiamondhunter.morebows;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import iDiamondhunter.morebows.MoreBows;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public final class e
extends EntityArrow
implements IEntityAdditionalSpawnData {
    private boolean var_boolean_a = true;
    private byte b = (byte)-1;
    public byte var_byte_a = 0;

    public e(World world) {
        super(world);
    }

    public e(World world, double d2, double d3, double d4) {
        super(world, d2, d3, d4);
    }

    public e(World world, EntityLivingBase entityLivingBase, EntityLivingBase entityLivingBase2, float f, float f2) {
        super(world, entityLivingBase, entityLivingBase2, f, f2);
    }

    public e(World world, EntityLivingBase entityLivingBase, float f) {
        super(world, entityLivingBase, f);
    }

    public e(World world, EntityLivingBase entityLivingBase, float f, byte by) {
        super(world, entityLivingBase, f);
        this.var_byte_a = by;
    }

    @SideOnly(value=Side.CLIENT)
    public final boolean getIsCritical() {
        if (this.var_byte_a == 3) {
            return false;
        }
        return super.getIsCritical();
    }

    public final void onUpdate() {
        super.onUpdate();
        if (this.var_byte_a == 3) {
            if (this.ticksExisted == 1 && MoreBows.var_boolean_a) {
                this.isImmuneToFire = true;
                this.extinguish();
            }
            if (this.arrowShake == 7) {
                this.b = 0;
                this.canBePickedUp = 0;
            }
            if (this.b >= 0) {
                this.b = (byte)(this.b + 1);
                if (this.var_boolean_a && this.worldObj.isRemote && MoreBows.var_boolean_b) {
                    this.setSize(0.1f, 0.1f);
                    e e2 = this;
                    e2.setPosition(e2.posX, this.posY, this.posZ);
                    this.var_boolean_a = false;
                }
                if (this.b <= 2) {
                    this.worldObj.spawnParticle("snowballpoof", this.posX, this.posY, this.posZ, 0.0, 0.0, 0.0);
                }
                if (this.b <= 30) {
                    this.worldObj.spawnParticle("splash", this.posX, this.posY - 0.3, this.posZ, 0.0, 0.0, 0.0);
                }
                if (this.b == 64) {
                    int n;
                    int n2;
                    int n3 = MathHelper.floor_double((double)this.posX);
                    Block block = this.worldObj.getBlock(n3, n2 = MathHelper.floor_double((double)this.posY), n = MathHelper.floor_double((double)this.posZ));
                    if (block == Blocks.air && this.worldObj.getBlock(n3, n2 - 1, n).isSideSolid((IBlockAccess)this.worldObj, n3, n2 - 1, n, ForgeDirection.UP)) {
                        this.worldObj.setBlock(n3, n2, n, Blocks.snow_layer);
                    } else if (block == Blocks.snow_layer) {
                        int n4 = this.worldObj.getBlockMetadata(n3, n2, n);
                        int n5 = n4 & 7;
                        if (n5 <= 6 && this.worldObj.checkNoEntityCollision(block.getCollisionBoundingBoxFromPool(this.worldObj, n3, n2, n))) {
                            this.worldObj.setBlockMetadataWithNotify(n3, n2, n, n5 + 1 | n4 & 0xFFFFFFF8, 2);
                        }
                    } else if (block == Blocks.water) {
                        this.worldObj.setBlock(n3, n2, n, Blocks.ice);
                    }
                }
                if (this.b >= 64) {
                    this.setDead();
                    return;
                }
            } else if (super.getIsCritical()) {
                for (int i = 0; i < 4; ++i) {
                    this.worldObj.spawnParticle("splash", this.posX + this.motionX * (double)i / 4.0, this.posY + this.motionY * (double)i / 4.0, this.posZ + this.motionZ * (double)i / 4.0, -this.motionX, -this.motionY + 0.2, -this.motionZ);
                }
            }
        }
    }

    public final void readEntityFromNBT(NBTTagCompound nBTTagCompound) {
        super.readEntityFromNBT(nBTTagCompound);
        this.b = nBTTagCompound.getByte("inTicks");
        this.var_byte_a = nBTTagCompound.getByte("type");
    }

    public final void readSpawnData(ByteBuf byteBuf) {
        this.b = byteBuf.readByte();
        this.var_byte_a = byteBuf.readByte();
        Entity shooter = this.worldObj.getEntityByID(byteBuf.readInt());
        if (shooter instanceof EntityLivingBase) {
            this.shootingEntity = shooter;
        }
    }

    public final void writeEntityToNBT(NBTTagCompound nBTTagCompound) {
        super.writeEntityToNBT(nBTTagCompound);
        nBTTagCompound.setByte("inTicks", this.b);
        nBTTagCompound.setByte("type", this.var_byte_a);
    }

    public final void writeSpawnData(ByteBuf byteBuf) {
        byteBuf.writeByte((int)this.b);
        byteBuf.writeByte((int)this.var_byte_a);
        byteBuf.writeInt(this.shootingEntity != null ? this.shootingEntity.getEntityId() : -1);
    }
}
