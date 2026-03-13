/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityTracker
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.server.S0DPacketCollectItem
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldServer
 */
package net.nmccoy.legendgear.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityTracker;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S0DPacketCollectItem;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.nmccoy.legendgear.LegendGear2;

public class EntityHeart
extends Entity {
    public static final int MAX_LIFE = 600;
    public int pickupDelay = 10;
    public int lifeTicksLeft;

    public EntityHeart(World world, double x, double y, double z) {
        super(world);
        this.setPosition(x, y, z);
        this.lifeTicksLeft = 600;
        this.setSize(0.5f, 0.5f);
        this.motionY = 0.5;
        this.motionX = world.rand.nextGaussian() * (double)0.3f;
        this.motionZ = world.rand.nextGaussian() * (double)0.3f;
    }

    public EntityHeart(World world) {
        super(world);
        this.lifeTicksLeft = 600;
        this.setSize(0.25f, 0.25f);
    }

    protected void entityInit() {
    }

    public void onUpdate() {
        super.onUpdate();
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        this.motionY -= (double)0.04f;
        --this.pickupDelay;
        float drag = 0.8f;
        this.motionX *= (double)drag;
        this.motionY *= (double)drag;
        this.motionZ *= (double)drag;
        this.moveEntity(this.motionX, this.motionY, this.motionZ);
        if (this.onGround) {
            this.motionY *= -0.5;
        }
        --this.lifeTicksLeft;
        if (this.lifeTicksLeft <= 0 && !this.worldObj.isRemote) {
            this.setDead();
        }
    }

    protected void readEntityFromNBT(NBTTagCompound tag) {
        this.lifeTicksLeft = tag.getInteger("life");
    }

    public void onCollideWithPlayer(EntityPlayer player) {
        if (!this.worldObj.isRemote && !this.isDead && this.pickupDelay <= 0) {
            player.heal(2.0f);
            EntityTracker entitytracker = ((WorldServer)this.worldObj).getEntityTracker();
            entitytracker.func_151248_b((Entity)this, (Packet)new S0DPacketCollectItem(this.getEntityId(), player.getEntityId()));
            this.worldObj.playSoundAtEntity((Entity)player, "legendgear:heart", LegendGear2.CONFIG_PICKUP_SOUND_VOLUME, 1.0f);
            this.setDead();
        }
    }

    protected void writeEntityToNBT(NBTTagCompound tag) {
        tag.setInteger("life", this.lifeTicksLeft);
    }
}

