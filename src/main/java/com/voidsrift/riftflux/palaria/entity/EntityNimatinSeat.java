package com.voidsrift.riftflux.palaria.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import java.util.UUID;

public class EntityNimatinSeat extends Entity {
    private static final int DATA_PARENT = 20;
    private static final int DATA_INDEX = 21;
    private EntityNimatin cachedParent;
    private UUID parentUniqueId;

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
        parentUniqueId = parent == null ? null : parent.getUniqueID();
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
        if (parentUniqueId != null && worldObj != null && worldObj.loadedEntityList != null) {
            for (Object obj : worldObj.loadedEntityList) {
                if (obj instanceof EntityNimatin) {
                    EntityNimatin nimatin = (EntityNimatin) obj;
                    if (!nimatin.isDead && parentUniqueId.equals(nimatin.getUniqueID())) {
                        cachedParent = nimatin;
                        dataWatcher.updateObject(DATA_PARENT, Integer.valueOf(nimatin.getEntityId()));
                        return cachedParent;
                    }
                }
            }
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
            if (ticksExisted <= 100) {
                noClip = true;
                return;
            }
            if (riddenByEntity != null) {
                riddenByEntity.mountEntity(null);
                riddenByEntity = null;
            }
            setDead();
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
        if (tag.hasKey("ParentUUIDMost") && tag.hasKey("ParentUUIDLeast")) {
            parentUniqueId = new UUID(tag.getLong("ParentUUIDMost"), tag.getLong("ParentUUIDLeast"));
        }
        if (tag.hasKey("SeatIndex")) {
            dataWatcher.updateObject(DATA_INDEX, Integer.valueOf(tag.getInteger("SeatIndex")));
        }
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound tag) {
        tag.setInteger("Parent", dataWatcher.getWatchableObjectInt(DATA_PARENT));
        EntityNimatin parent = getParent();
        UUID uuid = parent == null ? parentUniqueId : parent.getUniqueID();
        if (uuid != null) {
            tag.setLong("ParentUUIDMost", uuid.getMostSignificantBits());
            tag.setLong("ParentUUIDLeast", uuid.getLeastSignificantBits());
        }
        tag.setInteger("SeatIndex", dataWatcher.getWatchableObjectInt(DATA_INDEX));
    }

    @Override
    public boolean writeToNBTOptional(NBTTagCompound tag) {
        return getParent() != null && super.writeToNBTOptional(tag);
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
