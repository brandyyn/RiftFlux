package com.voidsrift.riftflux.palaria.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

public class EntityNimatinSeat extends Entity {
    private static final int DATA_PARENT = 20;
    private static final int DATA_INDEX = 21;
    private EntityNimatin cachedParent;

    public EntityNimatinSeat(World world) {
        super(world);
        noClip = true;
        ignoreFrustumCheck = true;
        setSize(0.0F, 0.0F);
        width = 0.0F;
        height = 0.0F;
    }

    public EntityNimatinSeat(World world, EntityNimatin parent, int seatIndex) {
        this(world);
        setParent(parent);
        setSeatIndex(seatIndex);
    }

    @Override
    protected void entityInit() {
        dataWatcher.addObject(DATA_PARENT, Integer.valueOf(-1));
        dataWatcher.addObject(DATA_INDEX, Integer.valueOf(0));
    }

    public void setParent(EntityNimatin parent) {
        cachedParent = parent;
        dataWatcher.updateObject(DATA_PARENT, Integer.valueOf(parent == null ? -1 : parent.getEntityId()));
    }

    public EntityNimatin getParent() {
        if (cachedParent != null && !cachedParent.isDead && cachedParent.worldObj == worldObj) {
            return cachedParent;
        }
        int id = dataWatcher.getWatchableObjectInt(DATA_PARENT);
        if (id < 0) {
            return null;
        }
        Entity entity = worldObj.getEntityByID(id);
        if (entity instanceof EntityNimatin) {
            cachedParent = (EntityNimatin) entity;
            return cachedParent;
        }
        return null;
    }

    public void setSeatIndex(int seatIndex) {
        dataWatcher.updateObject(DATA_INDEX, Integer.valueOf(seatIndex));
    }

    public int getSeatIndex() {
        return dataWatcher.getWatchableObjectInt(DATA_INDEX);
    }

    @Override
    public void onUpdate() {
        EntityNimatin parent = getParent();
        if (parent == null || parent.isDead) {
            if (riddenByEntity != null) {
                riddenByEntity.mountEntity(null);
                riddenByEntity = null;
            }
            if (ticksExisted > 100) {
                setDead();
            }
            return;
        }
        noClip = true;
        copyLocationAndAnglesFrom(parent);
    }

    @Override
    public void updateRiderPosition() {
        EntityNimatin parent = getParent();
        if (parent != null && riddenByEntity != null) {
            parent.updatePassenger(riddenByEntity);
        }
    }

    @Override
    public void setPositionAndRotation2(double x, double y, double z, float yaw, float pitch, int increments) {
        EntityNimatin parent = getParent();
        if (parent != null) {
            copyLocationAndAnglesFrom(parent);
            prevRotationYaw = parent.prevRotationYaw;
            prevRotationPitch = parent.prevRotationPitch;
            return;
        }
        super.setPositionAndRotation2(x, y, z, yaw, pitch, increments);
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
    public void setDead() {
        if (riddenByEntity != null) {
            riddenByEntity.mountEntity(null);
            riddenByEntity = null;
        }
        super.setDead();
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound tag) {
        if (tag.hasKey("Parent")) {
            dataWatcher.updateObject(DATA_PARENT, Integer.valueOf(tag.getInteger("Parent")));
        }
        if (tag.hasKey("SeatIndex")) {
            dataWatcher.updateObject(DATA_INDEX, Integer.valueOf(tag.getInteger("SeatIndex")));
        }
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound tag) {
        tag.setInteger("Parent", dataWatcher.getWatchableObjectInt(DATA_PARENT));
        tag.setInteger("SeatIndex", dataWatcher.getWatchableObjectInt(DATA_INDEX));
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
    public boolean hitByEntity(Entity entity) {
        return true;
    }

    @Override
    public boolean shouldRenderInPass(int pass) {
        return false;
    }
}
