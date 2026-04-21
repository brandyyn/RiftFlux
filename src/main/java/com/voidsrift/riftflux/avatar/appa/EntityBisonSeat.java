package com.voidsrift.riftflux.avatar.appa;

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

public class EntityBisonSeat extends Entity implements IEntitySyncData, IEntityAdditionalSpawnData {
    private EntityBison cachedParent;
    private UUID parentUniqueId;
    private int parentEntityId = -1;
    private int seatIndex;

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
    }

    public void setParent(EntityBison parent) {
        UUID newParentUniqueId = parent == null ? null : parent.getUniqueID();
        int newParentEntityId = parent == null ? -1 : parent.getEntityId();
        boolean changed = this.cachedParent != parent
                || this.parentEntityId != newParentEntityId
                || !sameUuid(this.parentUniqueId, newParentUniqueId);
        this.cachedParent = parent;
        this.parentUniqueId = newParentUniqueId;
        this.parentEntityId = newParentEntityId;
        if (changed) {
            EntitySyncHelper.sync(this);
        }
    }

    public EntityBison getParent() {
        if (this.cachedParent != null && !this.cachedParent.isDead && this.cachedParent.worldObj == this.worldObj) {
            return this.cachedParent;
        }
        if (this.parentEntityId >= 0) {
            Entity entity = this.worldObj.getEntityByID(this.parentEntityId);
            if (entity instanceof EntityBison) {
                this.cachedParent = (EntityBison) entity;
                return this.cachedParent;
            }
        }
        if (this.parentUniqueId != null && this.worldObj != null && this.worldObj.loadedEntityList != null) {
            for (Object obj : this.worldObj.loadedEntityList) {
                if (!(obj instanceof EntityBison)) {
                    continue;
                }
                EntityBison bison = (EntityBison) obj;
                if (!bison.isDead && this.parentUniqueId.equals(bison.getUniqueID())) {
                    this.cachedParent = bison;
                    this.parentEntityId = bison.getEntityId();
                    EntitySyncHelper.sync(this);
                    return this.cachedParent;
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
        return this.seatIndex;
    }

    @Override
    public void onUpdate() {
        EntityBison parent = getParent();
        if (parent == null || parent.isDead) {
            if (this.ticksExisted <= 100) {
                this.noClip = true;
                return;
            }
            if (this.riddenByEntity != null) {
                this.riddenByEntity.mountEntity(null);
                this.riddenByEntity = null;
            }
            setDead();
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
            this.parentEntityId = tag.getInteger("Parent");
            this.cachedParent = null;
        }
        if (tag.hasKey("ParentUUIDMost") && tag.hasKey("ParentUUIDLeast")) {
            this.parentUniqueId = new UUID(tag.getLong("ParentUUIDMost"), tag.getLong("ParentUUIDLeast"));
        }
        if (tag.hasKey("SeatIndex")) {
            this.seatIndex = tag.getInteger("SeatIndex");
        }
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound tag) {
        tag.setInteger("Parent", this.parentEntityId);
        EntityBison parent = getParent();
        UUID uuid = parent == null ? this.parentUniqueId : parent.getUniqueID();
        if (uuid != null) {
            tag.setLong("ParentUUIDMost", uuid.getMostSignificantBits());
            tag.setLong("ParentUUIDLeast", uuid.getLeastSignificantBits());
        }
        tag.setInteger("SeatIndex", this.seatIndex);
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
    public boolean hitByEntity(Entity entityIn) {
        return true;
    }

    @Override
    public boolean shouldRenderInPass(int pass) {
        return false;
    }

    @Override
    public void rf$writeSyncData(NBTTagCompound tag) {
        tag.setInteger("Parent", this.parentEntityId);
        tag.setInteger("SeatIndex", this.seatIndex);
    }

    @Override
    public void rf$readSyncData(NBTTagCompound tag) {
        this.parentEntityId = tag.getInteger("Parent");
        this.seatIndex = tag.getInteger("SeatIndex");
        this.cachedParent = null;
    }

    @Override
    public void writeSpawnData(ByteBuf buffer) {
        buffer.writeInt(this.parentEntityId);
        buffer.writeInt(this.seatIndex);
        writeUuid(buffer, this.parentUniqueId);
    }

    @Override
    public void readSpawnData(ByteBuf buffer) {
        this.parentEntityId = buffer.readInt();
        this.seatIndex = buffer.readInt();
        this.parentUniqueId = readUuid(buffer);
        this.cachedParent = null;
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
