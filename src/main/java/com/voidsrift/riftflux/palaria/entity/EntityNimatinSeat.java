package com.voidsrift.riftflux.palaria.entity;

import com.voidsrift.riftflux.net.sync.EntitySyncHelper;
import com.voidsrift.riftflux.net.sync.IEntitySyncData;
import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import java.util.UUID;

public class EntityNimatinSeat extends Entity implements IEntitySyncData, IEntityAdditionalSpawnData {
    private EntityNimatin cachedParent;
    private UUID parentUniqueId;
    private int parentEntityId = -1;
    private int seatIndex;

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
    }

    public void setParent(EntityNimatin parent) {
        UUID newParentUniqueId = parent == null ? null : parent.getUniqueID();
        int newParentEntityId = parent == null ? -1 : parent.getEntityId();
        boolean changed = cachedParent != parent
                || parentEntityId != newParentEntityId
                || !sameUuid(parentUniqueId, newParentUniqueId);
        cachedParent = parent;
        parentUniqueId = newParentUniqueId;
        parentEntityId = newParentEntityId;
        if (changed) {
            EntitySyncHelper.sync(this);
        }
    }

    public EntityNimatin getParent() {
        if (cachedParent != null && !cachedParent.isDead && cachedParent.worldObj == worldObj) {
            return cachedParent;
        }
        if (parentEntityId >= 0) {
            Entity entity = worldObj.getEntityByID(parentEntityId);
            if (entity instanceof EntityNimatin) {
                cachedParent = (EntityNimatin) entity;
                return cachedParent;
            }
        }
        if (parentUniqueId != null && worldObj != null && worldObj.loadedEntityList != null) {
            for (Object obj : worldObj.loadedEntityList) {
                if (obj instanceof EntityNimatin) {
                    EntityNimatin nimatin = (EntityNimatin) obj;
                    if (!nimatin.isDead && parentUniqueId.equals(nimatin.getUniqueID())) {
                        cachedParent = nimatin;
                        parentEntityId = nimatin.getEntityId();
                        EntitySyncHelper.sync(this);
                        return cachedParent;
                    }
                }
            }
        }
        return null;
    }

    public void setSeatIndex(int seatIndex) {
        if (this.seatIndex == seatIndex) {
            return;
        }
        this.seatIndex = seatIndex;
        EntitySyncHelper.sync(this);
    }

    public int getSeatIndex() {
        return seatIndex;
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
            parentEntityId = tag.getInteger("Parent");
            cachedParent = null;
        }
        if (tag.hasKey("ParentUUIDMost") && tag.hasKey("ParentUUIDLeast")) {
            parentUniqueId = new UUID(tag.getLong("ParentUUIDMost"), tag.getLong("ParentUUIDLeast"));
        }
        if (tag.hasKey("SeatIndex")) {
            seatIndex = tag.getInteger("SeatIndex");
        }
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound tag) {
        tag.setInteger("Parent", parentEntityId);
        EntityNimatin parent = getParent();
        UUID uuid = parent == null ? parentUniqueId : parent.getUniqueID();
        if (uuid != null) {
            tag.setLong("ParentUUIDMost", uuid.getMostSignificantBits());
            tag.setLong("ParentUUIDLeast", uuid.getLeastSignificantBits());
        }
        tag.setInteger("SeatIndex", seatIndex);
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

    @Override
    public void rf$writeSyncData(NBTTagCompound tag) {
        tag.setInteger("Parent", parentEntityId);
        tag.setInteger("SeatIndex", seatIndex);
    }

    @Override
    public void rf$readSyncData(NBTTagCompound tag) {
        parentEntityId = tag.getInteger("Parent");
        seatIndex = tag.getInteger("SeatIndex");
        cachedParent = null;
    }

    @Override
    public void writeSpawnData(ByteBuf buffer) {
        buffer.writeInt(parentEntityId);
        buffer.writeInt(seatIndex);
        writeUuid(buffer, parentUniqueId);
    }

    @Override
    public void readSpawnData(ByteBuf buffer) {
        parentEntityId = buffer.readInt();
        seatIndex = buffer.readInt();
        parentUniqueId = readUuid(buffer);
        cachedParent = null;
    }

    private static void writeUuid(ByteBuf buffer, UUID uuid) {
        buffer.writeBoolean(uuid != null);
        if (uuid != null) {
            buffer.writeLong(uuid.getMostSignificantBits());
            buffer.writeLong(uuid.getLeastSignificantBits());
        }
    }

    private static UUID readUuid(ByteBuf buffer) {
        if (!buffer.readBoolean()) {
            return null;
        }
        return new UUID(buffer.readLong(), buffer.readLong());
    }

    private static boolean sameUuid(UUID left, UUID right) {
        return left == right || (left != null && left.equals(right));
    }
}
