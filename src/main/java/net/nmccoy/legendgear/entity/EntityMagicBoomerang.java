/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.registry.IEntityAdditionalSpawnData
 *  io.netty.buffer.ByteBuf
 *  net.minecraft.block.Block
 *  net.minecraft.block.material.Material
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.EntityTracker
 *  net.minecraft.entity.item.EntityItem
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.projectile.EntityThrowable
 *  net.minecraft.init.Blocks
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTBase
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.server.S0DPacketCollectItem
 *  net.minecraft.util.DamageSource
 *  net.minecraft.util.MovingObjectPosition
 *  net.minecraft.util.MovingObjectPosition$MovingObjectType
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldServer
 */
package net.nmccoy.legendgear.entity;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import com.voidsrift.riftflux.ModConfig;
import io.netty.buffer.ByteBuf;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityTracker;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S0DPacketCollectItem;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.nmccoy.legendgear.PlayerEventHandler;
import net.nmccoy.legendgear.entity.EntityFallingStar;
import net.nmccoy.legendgear.entity.EntityHeart;

public class EntityMagicBoomerang
extends EntityThrowable
implements IEntityAdditionalSpawnData {
    public int return_time = MAX_THROW_TIME;
    public static int MAX_THROW_TIME = 10;
    public ItemStack boomerang_item;
    public static float BOOMERANG_SPEED = 1.5f;
    public static float CORNERING_SPEED = 0.2f;
    public static float BOOMERANG_DAMAGE = 6.0f;
    public Entity owner;
    public int thrown_from_slot;

    public EntityMagicBoomerang(World par1World) {
        super(par1World);
    }

    public EntityMagicBoomerang(World par1World, EntityLivingBase par2EntityLiving, ItemStack boomerangThrown) {
        super(par1World, par2EntityLiving);
        this.boomerang_item = boomerangThrown;
        this.noClip = false;
        this.owner = par2EntityLiving;
    }

    public void readEntityFromNBT(NBTTagCompound par1nbtTagCompound) {
        super.readEntityFromNBT(par1nbtTagCompound);
        if (this.owner == null) {
            this.owner = this.getThrower();
        }
        this.boomerang_item = ItemStack.loadItemStackFromNBT((NBTTagCompound)par1nbtTagCompound.getCompoundTag("Item"));
    }

    public void writeEntityToNBT(NBTTagCompound par1nbtTagCompound) {
        super.writeEntityToNBT(par1nbtTagCompound);
        if (this.boomerang_item != null) {
            par1nbtTagCompound.setTag("Item", (NBTBase)this.boomerang_item.writeToNBT(new NBTTagCompound()));
        }
    }

    public void writeSpawnData(ByteBuf data) {
        int id = this.getThrower().getEntityId();
        data.writeInt(id);
    }

    public void readSpawnData(ByteBuf data) {
        int id = data.readInt();
        this.owner = this.worldObj.getEntityByID(id);
    }

    protected void onImpact(MovingObjectPosition var1) {
        if (var1.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY) {
            if (var1.entityHit != null && var1.entityHit instanceof EntityLivingBase) {
                if (var1.entityHit == this.getThrower() && this.return_time <= 0 && var1.entityHit instanceof EntityPlayer) {
                    boolean success;
                    EntityPlayer ep = (EntityPlayer)var1.entityHit;
                    ItemStack crowded_item = ep.inventory.getStackInSlot(this.thrown_from_slot);
                    ep.inventory.mainInventory[this.thrown_from_slot] = this.boomerang_item;
                    if (crowded_item != null && !(success = ep.inventory.addItemStackToInventory(crowded_item))) {
                        ep.dropPlayerItemWithRandomChoice(crowded_item, true);
                    }
                    if (this.riddenByEntity != null) {
                        Entity passenger = this.riddenByEntity;
                        passenger.mountEntity(null);
                        passenger.posX = ep.posX;
                        passenger.posY = ep.posY;
                        passenger.posZ = ep.posZ;
                    }
                    if (!this.isDead && !this.worldObj.isRemote) {
                        this.playSound("random.pop", 0.2f, ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7f + 1.0f) * 2.0f);
                        EntityTracker entitytracker = ((WorldServer)this.worldObj).getEntityTracker();
                        entitytracker.func_151248_b((Entity)this, (Packet)new S0DPacketCollectItem(this.getEntityId(), this.getThrower().getEntityId()));
                    }
                    this.setDead();
                }
                if (var1.entityHit != this.getThrower() && this.getThrower() != null) {
                    EntityLivingBase el = (EntityLivingBase)var1.entityHit;
                    if (this.boomerang_item != null) {
                        if (!net.nmccoy.legendgear.LegendGear2.CONFIG_MAGIC_BOOMERANG_INFINITE_DURABILITY) {
                            this.boomerang_item.damageItem(1, this.getThrower());
                        }
                        if (this.boomerang_item.stackSize == 0) {
                            this.worldObj.playSoundAtEntity((Entity)this, "random.break", 1.0f, 0.8f);
                            this.setDead();
                        }
                    }
                    el.attackEntityFrom(DamageSource.causeThrownDamage((Entity)this, (Entity)this.getThrower()), BOOMERANG_DAMAGE);
                }
            }
        } else {
            boolean solid;
            int meta = this.worldObj.getBlockMetadata(var1.blockX, var1.blockY, var1.blockZ);
            Block block = this.worldObj.getBlock(var1.blockX, var1.blockY, var1.blockZ);
            int id = Block.getIdFromBlock((Block)block);
            Material mat = block.getMaterial();
            boolean bl = solid = block.getCollisionBoundingBoxFromPool(this.worldObj, var1.blockX, var1.blockY, var1.blockZ) != null;
            boolean canBreakPlants = (mat == Material.plants || mat == Material.vine) && ModConfig.legendGearMagicBoomerangBreakPlants;
            boolean canBreakGlass = mat == Material.glass;
            if (block.getBlockHardness(this.worldObj, var1.blockX, var1.blockY, var1.blockZ) == 0.0f && (canBreakPlants || canBreakGlass)) {
                if (!this.worldObj.isRemote && this.worldObj.func_147480_a(var1.blockX, var1.blockY, var1.blockZ, true)) {
                    block.onBlockDestroyedByPlayer(this.worldObj, var1.blockX, var1.blockY, var1.blockZ, meta);
                    if (block == Blocks.tallgrass && this.owner != null && this.owner instanceof EntityPlayer) {
                        PlayerEventHandler.considerGrassDrops((EntityPlayer)this.owner, this.worldObj, var1.blockX, var1.blockY, var1.blockZ, 0);
                    }
                }
                if (this.boomerang_item != null && this.getThrower() != null && this.boomerang_item.stackSize == 0) {
                    this.worldObj.playSoundAtEntity((Entity)this, "random.break", 1.0f, 0.8f);
                    this.setDead();
                }
            } else if (!this.noClip && solid) {
                this.return_time = Math.min(this.return_time, 0);
                this.noClip = true;
                this.motionX *= -1.0;
                this.motionY *= -1.0;
                this.motionZ *= -1.0;
            }
        }
    }

    private float updateRotationRadians(float par1, float par2, float par3) {
        float var4 = par2 - par1;
        if ((double)(var4 %= (float)Math.PI * 2) >= Math.PI) {
            var4 -= (float)Math.PI * 2;
        }
        if ((double)var4 < -Math.PI) {
            var4 += (float)Math.PI * 2;
        }
        if (var4 > par3) {
            var4 = par3;
        }
        if (var4 < -par3) {
            var4 = -par3;
        }
        return par1 + var4;
    }

    public void onUpdate() {
        EntityLivingBase thrower = null;
        if (this.owner instanceof EntityLivingBase) {
            thrower = (EntityLivingBase)this.owner;
        }
        if (thrower != null) {
            EntityPlayer ep = (EntityPlayer)thrower;
            if (ep.capabilities.isCreativeMode) {
                ItemStack hand = ep.inventory.mainInventory[ep.inventory.currentItem];
                if (this.boomerang_item != null && hand != null && this.boomerang_item.isItemEqual(hand)) {
                    ep.inventory.mainInventory[ep.inventory.currentItem] = null;
                }
            }
        }
        if (thrower != null && this.return_time % 3 == 0) {
            this.worldObj.playSoundAtEntity((Entity)this, "legendgear:boomerang", 2.0f, 1.0f + 1.0f / (1.0f + this.getDistanceToEntity((Entity)thrower) / 16.0f));
        }
        if (this.worldObj.isRemote) {
            for (int var9 = 0; var9 < 4; ++var9) {
                this.worldObj.spawnParticle("crit", this.posX + this.motionX * (double)var9 / 4.0, this.posY + this.motionY * (double)var9 / 4.0, this.posZ + this.motionZ * (double)var9 / 4.0, -this.motionX, -this.motionY + 0.2, -this.motionZ);
            }
        }
        if (!this.worldObj.isRemote && this.riddenByEntity == null) {
            List ents = this.worldObj.getEntitiesWithinAABBExcludingEntity((Entity)this, this.boundingBox.expand(1.0, 1.0, 1.0));
            for (Object o : ents) {
                Entity e = (Entity)o;
                if (!(e instanceof EntityItem) && !(e instanceof EntityFallingStar) && !(e instanceof EntityHeart)) continue;
                e.mountEntity((Entity)this);
                break;
            }
        }
        if (--this.return_time <= 0 && thrower != null) {
            float current_heading = (float)Math.atan2(this.motionZ, this.motionX);
            float heading_to_thrower = (float)Math.atan2(thrower.posZ - this.posZ, thrower.posX - this.posX);
            float curve_scale = (float)(-this.return_time) * 0.007f;
            float new_heading = this.updateRotationRadians(current_heading, heading_to_thrower, curve_scale);
            double current_pitch = Math.atan2(this.motionY, Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ));
            double target_pitch = Math.atan2(thrower.posY + (double)thrower.getEyeHeight() - this.posY, Math.sqrt((thrower.posX - this.posX) * (thrower.posX - this.posX) + (thrower.posZ - this.posZ) * (thrower.posZ - this.posZ)));
            float new_pitch = this.updateRotationRadians((float)current_pitch, (float)target_pitch, curve_scale * 0.3f);
            this.motionX = Math.cos(new_heading) * Math.cos(new_pitch);
            this.motionZ = Math.sin(new_heading) * Math.cos(new_pitch);
            this.motionY = Math.sin(new_pitch);
            this.setThrowableHeading(this.motionX, this.motionY, this.motionZ, BOOMERANG_SPEED, 0.0f);
        }
        if (this.return_time < -100 && !this.worldObj.isRemote) {
            if (this.boomerang_item != null) {
                this.worldObj.spawnEntityInWorld((Entity)new EntityItem(this.worldObj, this.posX, this.posY, this.posZ, this.boomerang_item));
            }
            this.setDead();
        }
        this.rotationPitch = this.return_time * 50;
        this.prevRotationPitch = (this.return_time + 1) * 50;
        super.onUpdate();
    }

    protected float getGravityVelocity() {
        return 0.0f;
    }
}
