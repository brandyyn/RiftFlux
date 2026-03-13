/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.registry.IEntityAdditionalSpawnData
 *  io.netty.buffer.ByteBuf
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.projectile.EntityThrowable
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.util.AxisAlignedBB
 *  net.minecraft.util.MovingObjectPosition
 *  net.minecraft.util.MovingObjectPosition$MovingObjectType
 *  net.minecraft.util.Vec3
 *  net.minecraft.world.World
 */
package net.nmccoy.legendgear.entity;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.nmccoy.legendgear.entity.EntitySpellEffect;
import net.nmccoy.legendgear.item.StarglassOrb;

public class EntityThrownOrb
extends EntityThrowable
implements IEntityAdditionalSpawnData {
    public int orbIndex;

    public EntityThrownOrb(World p_i1776_1_) {
        super(p_i1776_1_);
    }

    public EntityThrownOrb(World p_i1777_1_, EntityLivingBase p_i1777_2_) {
        super(p_i1777_1_, p_i1777_2_);
    }

    public EntityThrownOrb(World p_i1778_1_, double p_i1778_2_, double p_i1778_4_, double p_i1778_6_) {
        super(p_i1778_1_, p_i1778_2_, p_i1778_4_, p_i1778_6_);
    }

    private void impactSpellSpawn(MovingObjectPosition mop, EntitySpellEffect.SpellType id, double radius, double power, boolean critical) {
        if (!this.worldObj.isRemote) {
            EntityPlayer thrower = null;
            if (this.getThrower() instanceof EntityPlayer) {
                thrower = (EntityPlayer)this.getThrower();
            }
            double tx = this.posX;
            double ty = this.posY;
            double tz = this.posZ;
            if (mop.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                tx = mop.hitVec.xCoord;
                ty = mop.hitVec.yCoord;
                tz = mop.hitVec.zCoord;
            } else if (mop.entityHit != null && mop.entityHit.boundingBox != null) {
                AxisAlignedBB bb = mop.entityHit.boundingBox;
                tx = Math.max(tx, bb.minX);
                tx = Math.min(tx, bb.maxX);
                ty = Math.max(ty, bb.minY);
                ty = Math.min(ty, bb.maxY);
                tz = Math.max(tz, bb.minZ);
                tz = Math.min(tz, bb.maxZ);
            }
            this.worldObj.spawnEntityInWorld((Entity)new EntitySpellEffect(this.worldObj, id, thrower, Vec3.createVectorHelper((double)tx, (double)ty, (double)tz), radius, power, critical));
        }
    }

    protected void onImpact(MovingObjectPosition mop) {
        if (this.orbIndex == StarglassOrb.OrbTypes.blast.ordinal()) {
            this.impactSpellSpawn(mop, EntitySpellEffect.SpellType.OrbExplosion, 1.5, 6.0, true);
        }
        if (this.orbIndex == StarglassOrb.OrbTypes.twinkle.ordinal()) {
            this.impactSpellSpawn(mop, EntitySpellEffect.SpellType.Twinkle, 6.0, 8.0, true);
        }
        if (this.orbIndex == StarglassOrb.OrbTypes.zap.ordinal()) {
            this.impactSpellSpawn(mop, EntitySpellEffect.SpellType.Lightning1, 6.0, 10.0, true);
        }
        if (this.orbIndex == StarglassOrb.OrbTypes.fire.ordinal()) {
            this.impactSpellSpawn(mop, EntitySpellEffect.SpellType.Fire1, 6.0, 10.0, true);
        }
        if (this.orbIndex == StarglassOrb.OrbTypes.ice.ordinal()) {
            this.impactSpellSpawn(mop, EntitySpellEffect.SpellType.Ice1, 6.0, 10.0, true);
        }
        if (this.orbIndex == StarglassOrb.OrbTypes.water.ordinal()) {
            this.impactSpellSpawn(mop, EntitySpellEffect.SpellType.WaterFlood, 1.5, 0.0, false);
        }
        if (this.orbIndex == StarglassOrb.OrbTypes.lava.ordinal()) {
            this.impactSpellSpawn(mop, EntitySpellEffect.SpellType.LavaFlood, 1.0, 0.0, false);
        }
        if (!this.worldObj.isRemote) {
            this.worldObj.playSoundAtEntity((Entity)this, "game.potion.smash", 1.0f, 1.0f);
            this.setDead();
        }
    }

    public void writeEntityToNBT(NBTTagCompound p_70014_1_) {
        super.writeEntityToNBT(p_70014_1_);
        p_70014_1_.setInteger("orbType", this.orbIndex);
    }

    public void readEntityFromNBT(NBTTagCompound p_70037_1_) {
        super.readEntityFromNBT(p_70037_1_);
        this.orbIndex = p_70037_1_.getInteger("orbType");
    }

    public void writeSpawnData(ByteBuf buffer) {
        buffer.writeInt(this.orbIndex);
    }

    public void readSpawnData(ByteBuf additionalData) {
        this.orbIndex = additionalData.readInt();
    }
}

