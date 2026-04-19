/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityList
 *  net.minecraft.entity.projectile.EntityArrow
 *  net.minecraft.nbt.NBTBase
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.world.World
 */
package iDiamondhunter.morebows;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public final class d
extends Entity {
    private EntityArrow[] var_net_minecraft_entity_projectile_EntityArrow_arr_a;
    private float var_float_a;

    public d(World world) {
        super(world);
        this.noClip = true;
        this.preventEntitySpawning = false;
        this.isImmuneToFire = true;
    }

    public d(World world, double d2, double d3, double d4, float f, EntityArrow[] entityArrowArray) {
        this(world);
        this.var_float_a = f;
        this.posX = d2;
        this.posY = d3;
        this.posZ = d4;
        this.var_net_minecraft_entity_projectile_EntityArrow_arr_a = entityArrowArray;
    }

    protected final boolean canTriggerWalking() {
        return false;
    }

    protected final void entityInit() {
    }

    public final boolean isEntityInvulnerable() {
        return true;
    }

    public final void onUpdate() {
        if (this.ticksExisted > 61) {
            this.setDead();
            return;
        }
        if (!this.worldObj.isRemote) {
            if (this.ticksExisted == 1) {
                if (this.var_net_minecraft_entity_projectile_EntityArrow_arr_a == null || this.var_net_minecraft_entity_projectile_EntityArrow_arr_a.length != 6) {
                    this.setDead();
                    return;
                }
                this.worldObj.spawnEntityInWorld((Entity)this.var_net_minecraft_entity_projectile_EntityArrow_arr_a[0]);
            }
            if (this.ticksExisted == 61) {
                if (this.var_net_minecraft_entity_projectile_EntityArrow_arr_a == null || this.var_net_minecraft_entity_projectile_EntityArrow_arr_a.length != 6) {
                    this.setDead();
                    return;
                }
                this.worldObj.spawnEntityInWorld((Entity)this.var_net_minecraft_entity_projectile_EntityArrow_arr_a[1]);
                this.worldObj.playSoundAtEntity((Entity)this.var_net_minecraft_entity_projectile_EntityArrow_arr_a[1], "mob.endermen.portal", 0.5f, 1.0f / (this.rand.nextFloat() * 0.4f + 1.0f) + this.var_float_a * 0.4f);
                this.worldObj.spawnEntityInWorld((Entity)this.var_net_minecraft_entity_projectile_EntityArrow_arr_a[2]);
                this.var_net_minecraft_entity_projectile_EntityArrow_arr_a[2].posY += 1.0;
                this.var_net_minecraft_entity_projectile_EntityArrow_arr_a[2].posX -= 1.25;
                this.var_net_minecraft_entity_projectile_EntityArrow_arr_a[2].posZ += 1.75;
                this.worldObj.playSoundAtEntity((Entity)this.var_net_minecraft_entity_projectile_EntityArrow_arr_a[2], "random.bow", 1.0f, 1.0f / (this.rand.nextFloat() * 0.4f + 1.2f) + this.var_float_a * 0.5f);
                this.worldObj.spawnEntityInWorld((Entity)this.var_net_minecraft_entity_projectile_EntityArrow_arr_a[3]);
                this.var_net_minecraft_entity_projectile_EntityArrow_arr_a[3].posY += 1.45;
                this.var_net_minecraft_entity_projectile_EntityArrow_arr_a[3].posX -= 2.25;
                this.var_net_minecraft_entity_projectile_EntityArrow_arr_a[3].posZ -= 0.75;
                this.worldObj.playSoundAtEntity((Entity)this.var_net_minecraft_entity_projectile_EntityArrow_arr_a[3], "mob.endermen.portal", 0.25f, 1.0f / (this.rand.nextFloat() * 0.4f + 1.0f) + this.var_float_a * 0.3f);
                this.worldObj.spawnEntityInWorld((Entity)this.var_net_minecraft_entity_projectile_EntityArrow_arr_a[4]);
                this.var_net_minecraft_entity_projectile_EntityArrow_arr_a[4].posY += 2.0;
                this.var_net_minecraft_entity_projectile_EntityArrow_arr_a[4].posX += 0.25;
                this.var_net_minecraft_entity_projectile_EntityArrow_arr_a[4].posZ += 2.5;
                this.worldObj.playSoundAtEntity((Entity)this.var_net_minecraft_entity_projectile_EntityArrow_arr_a[4], "random.bow", 1.0f, 1.0f / (this.rand.nextFloat() * 0.4f + 1.2f) + this.var_float_a * 0.5f);
                this.worldObj.spawnEntityInWorld((Entity)this.var_net_minecraft_entity_projectile_EntityArrow_arr_a[5]);
                this.var_net_minecraft_entity_projectile_EntityArrow_arr_a[5].posY += 1.75;
                this.var_net_minecraft_entity_projectile_EntityArrow_arr_a[5].posX += 1.75;
                this.var_net_minecraft_entity_projectile_EntityArrow_arr_a[5].posZ += 1.5;
                this.worldObj.playSoundAtEntity((Entity)this.var_net_minecraft_entity_projectile_EntityArrow_arr_a[5], "mob.endermen.portal", 0.5f, 1.0f / (this.rand.nextFloat() * 0.4f + 1.0f) + this.var_float_a * 0.4f);
            }
        }
    }

    protected final void readEntityFromNBT(NBTTagCompound nBTTagCompound) {
        this.var_float_a = nBTTagCompound.getFloat("shotVelocity");
        this.var_net_minecraft_entity_projectile_EntityArrow_arr_a = new EntityArrow[nBTTagCompound.getInteger("arrowsAmount")];
        for (int i = 0; i < nBTTagCompound.getInteger("arrowsAmount"); ++i) {
            try {
                this.var_net_minecraft_entity_projectile_EntityArrow_arr_a[i] = (EntityArrow)EntityList.createEntityByName((String)nBTTagCompound.getCompoundTag("arrowsType").getString("arrow".concat(String.valueOf(i))), (World)this.worldObj);
                this.var_net_minecraft_entity_projectile_EntityArrow_arr_a[i].readFromNBT(nBTTagCompound.getCompoundTag("arrows").getCompoundTag("arrow".concat(String.valueOf(i))));
                continue;
            }
            catch (Exception exception) {
                exception.printStackTrace();
                this.var_net_minecraft_entity_projectile_EntityArrow_arr_a[i] = new EntityArrow(this.worldObj);
            }
        }
        this.ticksExisted = nBTTagCompound.getByte("ticksExisted");
    }

    protected final void writeEntityToNBT(NBTTagCompound nBTTagCompound) {
        nBTTagCompound.setFloat("shotVelocity", this.var_float_a);
        nBTTagCompound.setInteger("arrowsAmount", this.var_net_minecraft_entity_projectile_EntityArrow_arr_a.length);
        NBTTagCompound nBTTagCompound2 = new NBTTagCompound();
        NBTTagCompound nBTTagCompound3 = new NBTTagCompound();
        for (int i = 0; i < this.var_net_minecraft_entity_projectile_EntityArrow_arr_a.length; ++i) {
            NBTTagCompound nBTTagCompound4 = new NBTTagCompound();
            this.var_net_minecraft_entity_projectile_EntityArrow_arr_a[i].writeToNBT(nBTTagCompound4);
            nBTTagCompound2.setTag("arrow".concat(String.valueOf(i)), (NBTBase)nBTTagCompound4);
            nBTTagCompound3.setString("arrow".concat(String.valueOf(i)), EntityList.getEntityString((Entity)this.var_net_minecraft_entity_projectile_EntityArrow_arr_a[i]));
        }
        nBTTagCompound.setTag("arrows", (NBTBase)nBTTagCompound2);
        nBTTagCompound.setTag("arrowsType", (NBTBase)nBTTagCompound3);
        nBTTagCompound.setByte("ticksExisted", (byte)this.ticksExisted);
    }
}

