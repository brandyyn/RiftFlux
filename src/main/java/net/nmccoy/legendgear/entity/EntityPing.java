/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.registry.IEntityAdditionalSpawnData
 *  io.netty.buffer.ByteBuf
 *  net.minecraft.entity.Entity
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.world.World
 */
package net.nmccoy.legendgear.entity;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class EntityPing
extends Entity
implements IEntityAdditionalSpawnData {
    public int energy = 10;
    public int color = 0;
    public int age = 0;

    public EntityPing(World world) {
        super(world);
        this.ignoreFrustumCheck = true;
        this.renderDistanceWeight = 1000.0;
    }

    public EntityPing(World world, double x, double y, double z, int charge, int color) {
        this(world);
        this.color = color;
        this.posX = x;
        this.posY = y;
        this.posZ = z;
        this.energy = charge;
        this.color = color;
    }

    public void writeSpawnData(ByteBuf buffer) {
        buffer.writeInt(this.color);
        buffer.writeInt(this.energy);
        buffer.writeInt(this.age);
    }

    public void readSpawnData(ByteBuf buffer) {
        this.color = buffer.readInt();
        this.energy = buffer.readInt();
        this.age = buffer.readInt();
    }

    protected void entityInit() {
    }

    public void onUpdate() {
        if (this.age == 0) {
            this.worldObj.playSoundAtEntity((Entity)this, "legendgear:chime.0", 20.0f, 1.0f);
            this.worldObj.playSoundAtEntity((Entity)this, "legendgear:chime.0", 20.0f, 1.5f);
        }
        ++this.age;
        --this.energy;
        if (this.energy <= 0) {
            this.setDead();
        }
    }

    protected void readEntityFromNBT(NBTTagCompound tag) {
        this.color = tag.getInteger("color");
        this.energy = tag.getInteger("energy");
        this.age = tag.getInteger("age");
    }

    protected void writeEntityToNBT(NBTTagCompound tag) {
        tag.setInteger("color", this.color);
        tag.setInteger("energy", this.energy);
        tag.setInteger("age", this.age);
    }
}

