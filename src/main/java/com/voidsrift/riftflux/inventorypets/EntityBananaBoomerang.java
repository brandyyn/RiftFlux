package com.voidsrift.riftflux.inventorypets;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityTracker;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S0DPacketCollectItem;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.nmccoy.legendgear.entity.EntityFallingStar;
import net.nmccoy.legendgear.entity.EntityHeart;

import java.util.List;

public class EntityBananaBoomerang extends EntityThrowable implements IEntityAdditionalSpawnData {
    public static int MAX_THROW_TIME = 10;
    public static float BOOMERANG_SPEED = 1.5F;
    public static float BOOMERANG_DAMAGE = 6.0F;

    public int returnTime = MAX_THROW_TIME;
    private ItemStack thrownStack;
    private int sourceSlot;
    private Entity owner;

    public EntityBananaBoomerang(World world) {
        super(world);
    }

    public EntityBananaBoomerang(World world, EntityLivingBase thrower, ItemStack thrownStack, int sourceSlot) {
        super(world, thrower);
        this.thrownStack = thrownStack;
        this.sourceSlot = sourceSlot;
        this.noClip = false;
        this.owner = thrower;
    }

    @Override
    protected void onImpact(MovingObjectPosition hit) {
        if (hit == null) {
            return;
        }
        if (hit.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY) {
            if (hit.entityHit instanceof EntityLivingBase) {
                if (hit.entityHit == this.getThrower() && this.returnTime <= 0 && hit.entityHit instanceof EntityPlayer) {
                    EntityPlayer player = (EntityPlayer) hit.entityHit;
                    ItemStack crowded = player.inventory.getStackInSlot(this.sourceSlot);
                    player.inventory.mainInventory[this.sourceSlot] = this.thrownStack;
                    if (crowded != null && !player.inventory.addItemStackToInventory(crowded)) {
                        player.dropPlayerItemWithRandomChoice(crowded, true);
                    }
                    if (this.riddenByEntity != null) {
                        Entity passenger = this.riddenByEntity;
                        passenger.mountEntity(null);
                        passenger.posX = player.posX;
                        passenger.posY = player.posY;
                        passenger.posZ = player.posZ;
                    }
                    if (!this.isDead && !this.worldObj.isRemote) {
                        this.playSound("random.pop", 0.2F, ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
                        EntityTracker tracker = ((WorldServer) this.worldObj).getEntityTracker();
                        tracker.func_151248_b(this, (Packet) new S0DPacketCollectItem(this.getEntityId(), this.getThrower().getEntityId()));
                    }
                    this.setDead();
                }
                if (hit.entityHit != this.getThrower() && this.getThrower() != null) {
                    EntityLivingBase living = (EntityLivingBase) hit.entityHit;
                    living.attackEntityFrom(DamageSource.causeThrownDamage(this, this.getThrower()), BOOMERANG_DAMAGE);
                }
            }
        } else {
            boolean solid = this.worldObj.getBlock(hit.blockX, hit.blockY, hit.blockZ)
                    .getCollisionBoundingBoxFromPool(this.worldObj, hit.blockX, hit.blockY, hit.blockZ) != null;
            if (!this.noClip && solid) {
                this.returnTime = Math.min(this.returnTime, 0);
                this.noClip = true;
                this.motionX *= -1.0D;
                this.motionY *= -1.0D;
                this.motionZ *= -1.0D;
            }
        }
    }

    @Override
    public void onUpdate() {
        EntityLivingBase thrower = null;
        if (this.owner instanceof EntityLivingBase) {
            thrower = (EntityLivingBase) this.owner;
        }
        if (thrower != null) {
            EntityPlayer player = (EntityPlayer) thrower;
            if (player.capabilities.isCreativeMode) {
                ItemStack hand = player.inventory.mainInventory[player.inventory.currentItem];
                if (this.thrownStack != null && hand != null && this.thrownStack.isItemEqual(hand)) {
                    player.inventory.mainInventory[player.inventory.currentItem] = null;
                }
            }
        }

        if (thrower != null && this.returnTime % 3 == 0) {
            this.worldObj.playSoundAtEntity(this, "legendgear:boomerang", 2.0F, 1.0F + 1.0F / (1.0F + this.getDistanceToEntity(thrower) / 16.0F));
        }

        if (this.worldObj.isRemote) {
            for (int i = 0; i < 4; ++i) {
                this.worldObj.spawnParticle(
                        "crit",
                        this.posX + this.motionX * (double) i / 4.0D,
                        this.posY + this.motionY * (double) i / 4.0D,
                        this.posZ + this.motionZ * (double) i / 4.0D,
                        -this.motionX,
                        -this.motionY + 0.2D,
                        -this.motionZ
                );
            }
        }

        if (!this.worldObj.isRemote && this.riddenByEntity == null) {
            List entities = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox.expand(1.0D, 1.0D, 1.0D));
            for (Object object : entities) {
                Entity entity = (Entity) object;
                if (!(entity instanceof EntityItem) && !(entity instanceof EntityFallingStar) && !(entity instanceof EntityHeart)) {
                    continue;
                }
                entity.mountEntity(this);
                break;
            }
        }

        if (--this.returnTime <= 0 && thrower != null) {
            float currentHeading = (float) Math.atan2(this.motionZ, this.motionX);
            float headingToThrower = (float) Math.atan2(thrower.posZ - this.posZ, thrower.posX - this.posX);
            float curveScale = (-this.returnTime) * 0.007F;
            float newHeading = this.updateRotationRadians(currentHeading, headingToThrower, curveScale);
            double currentPitch = Math.atan2(this.motionY, Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ));
            double targetPitch = Math.atan2(
                    thrower.posY + thrower.getEyeHeight() - this.posY,
                    Math.sqrt((thrower.posX - this.posX) * (thrower.posX - this.posX) + (thrower.posZ - this.posZ) * (thrower.posZ - this.posZ))
            );
            float newPitch = this.updateRotationRadians((float) currentPitch, (float) targetPitch, curveScale * 0.3F);
            this.motionX = Math.cos(newHeading) * Math.cos(newPitch);
            this.motionZ = Math.sin(newHeading) * Math.cos(newPitch);
            this.motionY = Math.sin(newPitch);
            this.setThrowableHeading(this.motionX, this.motionY, this.motionZ, BOOMERANG_SPEED, 0.0F);
        }

        if (this.returnTime < -100 && !this.worldObj.isRemote) {
            if (this.thrownStack != null) {
                this.worldObj.spawnEntityInWorld(new EntityItem(this.worldObj, this.posX, this.posY, this.posZ, this.thrownStack));
            }
            this.setDead();
        }

        this.rotationPitch = this.returnTime * 50;
        this.prevRotationPitch = (this.returnTime + 1) * 50;
        super.onUpdate();
    }

    private float updateRotationRadians(float current, float target, float maxChange) {
        float delta = target - current;
        if ((double) (delta %= (float) Math.PI * 2) >= Math.PI) {
            delta -= (float) Math.PI * 2;
        }
        if ((double) delta < -Math.PI) {
            delta += (float) Math.PI * 2;
        }
        if (delta > maxChange) {
            delta = maxChange;
        }
        if (delta < -maxChange) {
            delta = -maxChange;
        }
        return current + delta;
    }

    @Override
    protected float getGravityVelocity() {
        return 0.0F;
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tag) {
        super.writeEntityToNBT(tag);
        if (this.thrownStack != null) {
            tag.setTag("Item", this.thrownStack.writeToNBT(new NBTTagCompound()));
        }
        tag.setInteger("SourceSlot", this.sourceSlot);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tag) {
        super.readEntityFromNBT(tag);
        if (this.owner == null) {
            this.owner = this.getThrower();
        }
        if (tag.hasKey("Item")) {
            this.thrownStack = ItemStack.loadItemStackFromNBT(tag.getCompoundTag("Item"));
        }
        this.sourceSlot = tag.getInteger("SourceSlot");
    }

    @Override
    public void writeSpawnData(ByteBuf buffer) {
        int id = this.getThrower() == null ? -1 : this.getThrower().getEntityId();
        buffer.writeInt(id);
    }

    @Override
    public void readSpawnData(ByteBuf buffer) {
        int id = buffer.readInt();
        if (id >= 0) {
            this.owner = this.worldObj.getEntityByID(id);
        }
    }
}
