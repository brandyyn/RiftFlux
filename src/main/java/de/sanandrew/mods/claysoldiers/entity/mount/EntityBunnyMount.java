/*
 * Decompiled with CFR 0.152.
 */
package de.sanandrew.mods.claysoldiers.entity.mount;

import de.sanandrew.core.manpack.util.EnumNbtTypes;
import de.sanandrew.mods.claysoldiers.entity.EntityClayMan;
import de.sanandrew.mods.claysoldiers.entity.mount.IMount;
import de.sanandrew.mods.claysoldiers.entity.projectile.ISoldierProjectile;
import de.sanandrew.mods.claysoldiers.network.ParticlePacketSender;
import de.sanandrew.mods.claysoldiers.util.IDisruptable;
import de.sanandrew.mods.claysoldiers.util.mount.EnumBunnyType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class EntityBunnyMount
extends EntityCreature
implements IMount,
IDisruptable {
    protected static final int DW_TYPE = 20;
    public boolean spawnedFromNexus = false;
    public ItemStack dollItem = null;

    public EntityBunnyMount(World world) {
        super(world);
        this.stepHeight = 0.1f;
        this.renderDistanceWeight = 5.0;
        this.setSize(0.35f, 0.7f);
    }

    public EntityBunnyMount(World world, EnumBunnyType type) {
        this(world);
        this.setType(type);
        this.worldObj.playSoundAtEntity(this, "step.cloth", 0.8f, ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2f + 1.0f) * 0.9f);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataWatcher.addObject(20, (short)0);
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(20.0);
    }

    @Override
    public float getAIMoveSpeed() {
        return 0.6f;
    }

    @Override
    public double getMountedYOffset() {
        return super.getMountedYOffset() - 0.3;
    }

    @Override
    public void updateEntityActionState() {
        if (this.riddenByEntity == null || !(this.riddenByEntity instanceof EntityClayMan)) {
            super.updateEntityActionState();
        } else {
            EntityClayMan rider = (EntityClayMan)this.riddenByEntity;
            this.isJumping = true;
            this.moveForward = rider.moveForward;
            this.moveStrafing = rider.moveStrafing;
            this.rotationYaw = this.prevRotationYaw = rider.rotationYaw;
            this.rotationPitch = this.prevRotationPitch = rider.rotationPitch;
            rider.renderYawOffset = this.renderYawOffset;
            this.riddenByEntity.fallDistance = 0.0f;
            if (rider.isDead || rider.getHealth() <= 0.0f) {
                rider.mountEntity(null);
            }
        }
    }

    @Override
    protected void jump() {
        this.motionY = 0.4;
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setBoolean("fromNexus", this.spawnedFromNexus);
        nbt.setShort("bunnyType", (short)this.getType());
        if (this.dollItem != null) {
            NBTTagCompound itemNBT = new NBTTagCompound();
            this.dollItem.writeToNBT(itemNBT);
            nbt.setTag("dollItem", itemNBT);
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        this.spawnedFromNexus = nbt.getBoolean("fromNexus");
        this.setType(EnumBunnyType.VALUES[nbt.getShort("bunnyType")]);
        if (nbt.hasKey("dollItem", EnumNbtTypes.NBT_COMPOUND.ordinal())) {
            this.dollItem = ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("dollItem"));
        }
    }

    @Override
    public void knockBack(Entity entity, float f, double motionShiftX, double motionShiftZ) {
        super.knockBack(entity, f, motionShiftX, motionShiftZ);
        if (entity != null && entity instanceof EntityClayMan) {
            this.motionX *= 0.6;
            this.motionY *= 0.75;
            this.motionZ *= 0.6;
        }
    }

    @Override
    public boolean isOnLadder() {
        return false;
    }

    @Override
    public boolean interact(EntityPlayer e) {
        return false;
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float damage) {
        if (source == IDisruptable.DISRUPT_DAMAGE) {
            return super.attackEntityFrom(source, damage);
        }
        Entity entity = source.getSourceOfDamage();
        if (!(entity instanceof EntityClayMan) && !source.isFireDamage()) {
            damage = 999.0f;
        }
        if (this.riddenByEntity instanceof EntityClayMan && source.getEntity() instanceof ISoldierProjectile) {
            EntityClayMan clayMan = (EntityClayMan)this.riddenByEntity;
            ISoldierProjectile projectile = (ISoldierProjectile)((Object)source.getEntity());
            if (clayMan.getClayTeam().equals(projectile.getTrowingTeam())) {
                return false;
            }
        }
        return super.attackEntityFrom(source, damage);
    }

    @Override
    public void disrupt() {
        this.attackEntityFrom(IDisruptable.DISRUPT_DAMAGE, 99999.0f);
    }

    @Override
    public EntityBunnyMount setSpawnedFromNexus() {
        this.spawnedFromNexus = true;
        return this;
    }

    @Override
    public int getType() {
        return this.dataWatcher.getWatchableObjectShort(20);
    }

    @Override
    public void setSpecial() {
    }

    @Override
    public boolean isSpecial() {
        return false;
    }

    @Override
    protected boolean canDespawn() {
        return false;
    }

    public void setType(EnumBunnyType type) {
        this.dataWatcher.updateObject(20, (short)type.ordinal());
    }

    public ResourceLocation getBunnyTexture() {
        return EnumBunnyType.VALUES[this.getType()].texture;
    }

    @Override
    protected String getHurtSound() {
        return "step.cloth";
    }

    @Override
    protected String getDeathSound() {
        return "step.cloth";
    }

    @Override
    protected void dropFewItems(boolean flag, int i) {
        if (!this.spawnedFromNexus && this.dollItem != null) {
            this.entityDropItem(this.dollItem, 0.0f);
        }
    }

    @Override
    protected void onDeathUpdate() {
        this.deathTime = 20;
        this.setDead();
        ParticlePacketSender.sendBunnyDeathFx(this.posX, this.posY, this.posZ, this.dimension, (byte)this.getType());
    }
}

