package com.voidsrift.riftflux.avatar.appa;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

public class EntityBisonSeat extends Entity {
    private static final int DATA_PARENT = 20;
    private static final int DATA_INDEX = 21;
    private EntityBison cachedParent;

    public EntityBisonSeat(World world) {
        super(world);
        this.noClip = true;
        this.ignoreFrustumCheck = true;
        this.setSize(0.0f, 0.0f);
        this.width = 0.0f;
        this.height = 0.0f;
    }

    public EntityBisonSeat(World world, EntityBison parent, int seatIndex) {
        this(world);
        setParent(parent);
        setSeatIndex(seatIndex);
    }

    @Override
    protected void entityInit() {
        this.dataWatcher.addObject(DATA_PARENT, Integer.valueOf(-1));
        this.dataWatcher.addObject(DATA_INDEX, Integer.valueOf(0));
    }

    public void setParent(EntityBison parent) {
        this.cachedParent = parent;
        int id = parent != null ? parent.getEntityId() : -1;
        this.dataWatcher.updateObject(DATA_PARENT, Integer.valueOf(id));
    }

    public EntityBison getParent() {
        if (this.cachedParent != null && !this.cachedParent.isDead && this.cachedParent.worldObj == this.worldObj) {
            return this.cachedParent;
        }
        int id = this.dataWatcher.getWatchableObjectInt(DATA_PARENT);
        if (id < 0) {
            return null;
        }
        Entity entity = this.worldObj.getEntityByID(id);
        if (entity instanceof EntityBison) {
            this.cachedParent = (EntityBison) entity;
            return this.cachedParent;
        }
        return null;
    }

    public void setSeatIndex(int seatIndex) {
        this.dataWatcher.updateObject(DATA_INDEX, Integer.valueOf(seatIndex));
    }

    public int getSeatIndex() {
        return this.dataWatcher.getWatchableObjectInt(DATA_INDEX);
    }

    @Override
    public void onUpdate() {
        EntityBison parent = getParent();
        if (parent == null || parent.isDead) {
            if (this.riddenByEntity != null) {
                this.riddenByEntity.mountEntity(null);
                this.riddenByEntity = null;
            }
            if (this.ticksExisted > 100) {
                setDead();
            }
            return;
        }
        this.noClip = true;
        this.copyLocationAndAnglesFrom(parent);
    }

    @Override
    public boolean canBeCollidedWith() {
        return false;
    }

    @Override
    public AxisAlignedBB getBoundingBox() {
        return null;
    }

    @Override
    public AxisAlignedBB getCollisionBox(Entity entity) {
        return null;
    }

    @Override
    public boolean canBePushed() {
        return false;
    }

    @Override
    public boolean shouldRiderSit() {
        return true;
    }

    @Override
    public boolean isInvisible() {
        return true;
    }

    @Override
    public double getMountedYOffset() {
        return 0.0D;
    }

    @Override
    public void updateRiderPosition() {
        EntityBison parent = getParent();
        if (parent != null && this.riddenByEntity != null) {
            parent.updatePassenger(this.riddenByEntity);
        }
    }

    @Override
    public void setPositionAndRotation2(double x, double y, double z, float yaw, float pitch, int increments) {
        EntityBison parent = getParent();
        if (parent != null) {
            copyLocationAndAnglesFrom(parent);
            this.prevRotationYaw = parent.prevRotationYaw;
            this.prevRotationPitch = parent.prevRotationPitch;
            return;
        }
        super.setPositionAndRotation2(x, y, z, yaw, pitch, increments);
    }

    @Override
    public void setDead() {
        if (this.riddenByEntity != null) {
            this.riddenByEntity.mountEntity(null);
            this.riddenByEntity = null;
        }
        super.setDead();
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound tag) {
        if (tag.hasKey("Parent")) {
            this.dataWatcher.updateObject(DATA_PARENT, Integer.valueOf(tag.getInteger("Parent")));
        }
        if (tag.hasKey("SeatIndex")) {
            this.dataWatcher.updateObject(DATA_INDEX, Integer.valueOf(tag.getInteger("SeatIndex")));
        }
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound tag) {
        tag.setInteger("Parent", this.dataWatcher.getWatchableObjectInt(DATA_PARENT));
        tag.setInteger("SeatIndex", this.dataWatcher.getWatchableObjectInt(DATA_INDEX));
    }

    @Override
    public boolean writeToNBTOptional(NBTTagCompound tag) {
        return false;
    }

    @Override
    public void onEntityUpdate() {
    }

    @Override
    public void moveEntity(double x, double y, double z) {
    }

    @Override
    public void updateRidden() {
    }

    @Override
    public boolean isBurning() {
        return false;
    }

    @Override
    public boolean isRiding() {
        return false;
    }

    @Override
    public boolean isInvisibleToPlayer(EntityPlayer player) {
        return true;
    }

    @Override
    public void setInWeb() {
    }

    @Override
    public boolean hitByEntity(Entity entityIn) {
        return true;
    }

    @Override
    public boolean shouldRenderInPass(int pass) {
        return false;
    }
}
