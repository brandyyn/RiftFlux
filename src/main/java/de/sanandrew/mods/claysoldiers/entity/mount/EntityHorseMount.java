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
import de.sanandrew.mods.claysoldiers.util.mount.EnumHorseType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class EntityHorseMount
extends EntityCreature
implements IMount,
IDisruptable {
    protected static final int DW_TYPE = 20;
    protected static final int DW_TEXTURE = 21;
    public boolean spawnedFromNexus = false;
    public boolean specialDeath = false;
    public ItemStack dollItem = null;
    protected float moveSpeed;

    public EntityHorseMount(World world) {
        super(world);
        this.stepHeight = 0.1f;
        this.moveSpeed = 0.6f;
        this.renderDistanceWeight = 5.0;
        this.setSize(0.35f, 0.7f);
    }

    public EntityHorseMount(World world, EnumHorseType horseType) {
        this(world);
        if (this.rand.nextInt(8192) == 0) {
            this.setSpecial();
        } else {
            this.setType(horseType);
        }
        this.worldObj.playSoundAtEntity(this, "step.gravel", 0.8f, ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2f + 1.0f) * 0.9f);
    }

    @Override
    public void updateEntityActionState() {
        if (this.riddenByEntity == null || !(this.riddenByEntity instanceof EntityClayMan)) {
            super.updateEntityActionState();
        } else {
            EntityClayMan rider = (EntityClayMan)this.riddenByEntity;
            this.isJumping = rider.isJumping() || this.handleWaterMovement();
            this.moveForward = rider.moveForward;
            this.moveStrafing = rider.moveStrafing;
            this.rotationYaw = this.prevRotationYaw = rider.rotationYaw;
            this.rotationPitch = this.prevRotationPitch = rider.rotationPitch;
            rider.renderYawOffset = this.renderYawOffset;
            rider.fallDistance = 0.0f;
            if (rider.isDead || rider.getHealth() <= 0.0f) {
                rider.mountEntity(null);
            }
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setBoolean("fromNexus", this.spawnedFromNexus);
        nbt.setShort("horseType", (short)this.getType());
        nbt.setShort("texture", this.dataWatcher.getWatchableObjectShort(21));
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
        this.setType(EnumHorseType.VALUES[nbt.getShort("horseType")]);
        this.dataWatcher.updateObject(21, nbt.getShort("texture"));
        if (nbt.hasKey("dollItem", EnumNbtTypes.NBT_COMPOUND.ordinal())) {
            this.dollItem = ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("dollItem"));
        }
    }

    @Override
    public double getMountedYOffset() {
        return super.getMountedYOffset() - 0.3;
    }

    @Override
    public void mountEntity(Entity entity) {
        if (entity == null || !(entity instanceof EntityMinecart)) {
            super.mountEntity(entity);
        }
    }

    @Override
    public boolean canBreatheUnderwater() {
        short horseType = this.dataWatcher.getWatchableObjectShort(20);
        return horseType == EnumHorseType.LAPIS.ordinal() || horseType == EnumHorseType.CLAY.ordinal() || horseType == EnumHorseType.CARROT.ordinal();
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float damage) {
        boolean damageSuccess;
        if (source == IDisruptable.DISRUPT_DAMAGE) {
            return super.attackEntityFrom(source, damage);
        }
        boolean shouldSpawnSpecial = this.rand.nextInt(16) == 0;
        this.specialDeath = source.isFireDamage() && !this.isSpecial() && shouldSpawnSpecial;
        Entity entity = source.getSourceOfDamage();
        if (!(entity instanceof EntityClayMan) && !source.isFireDamage()) {
            damage = 999.0f;
        } else if (source.isFireDamage() && this.isSpecial()) {
            return false;
        }
        if (this.riddenByEntity instanceof EntityClayMan && source.getEntity() instanceof ISoldierProjectile) {
            EntityClayMan clayMan = (EntityClayMan)this.riddenByEntity;
            ISoldierProjectile projectile = (ISoldierProjectile)((Object)source.getEntity());
            if (clayMan.getClayTeam().equals(projectile.getTrowingTeam())) {
                return false;
            }
        }
        if ((damageSuccess = super.attackEntityFrom(source, damage)) && this.getHealth() <= 0.0f && source.isFireDamage() && !this.isSpecial() && shouldSpawnSpecial) {
            EntityHorseMount specialHorse = new EntityHorseMount(this.worldObj, EnumHorseType.VALUES[this.getType()]);
            specialHorse.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
            specialHorse.setSpecial();
            specialHorse.chooseTexture();
            specialHorse.setHorseSpecs();
            this.worldObj.spawnEntityInWorld(specialHorse);
        }
        return damageSuccess;
    }

    @Override
    public void knockBack(Entity entity, float f, double motionShiftX, double motionShiftZ) {
        super.knockBack(entity, f, motionShiftX, motionShiftZ);
        if (entity instanceof EntityClayMan) {
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
    public float getAIMoveSpeed() {
        return this.moveSpeed;
    }

    @Override
    public EntityHorseMount setSpawnedFromNexus() {
        this.spawnedFromNexus = true;
        return this;
    }

    @Override
    public int getType() {
        return this.dataWatcher.getWatchableObjectShort(20);
    }

    @Override
    public void setSpecial() {
        this.setType(EnumHorseType.NIGHTMARE);
    }

    @Override
    public boolean isSpecial() {
        return this.getType() == EnumHorseType.NIGHTMARE.ordinal();
    }

    @Override
    public void disrupt() {
        this.attackEntityFrom(IDisruptable.DISRUPT_DAMAGE, 99999.0f);
    }

    @Override
    public boolean interact(EntityPlayer e) {
        return false;
    }

    @Override
    protected void onDeathUpdate() {
        this.deathTime = 20;
        this.setDead();
        ParticlePacketSender.sendHorseDeathFx(this.posX, this.posY, this.posZ, this.dimension, (byte)this.getType());
    }

    @Override
    protected String getHurtSound() {
        return "step.gravel";
    }

    @Override
    protected String getDeathSound() {
        return "step.gravel";
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(30.0);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataWatcher.addObject(20, (short)0);
        this.dataWatcher.addObject(21, (short)0);
    }

    @Override
    protected void jump() {
        this.motionY = 0.4;
        this.isAirBorne = true;
    }

    @Override
    protected void dropFewItems(boolean flag, int i) {
        if (!this.spawnedFromNexus && !this.specialDeath && this.dollItem != null) {
            this.entityDropItem(this.dollItem, 0.0f);
        }
    }

    @Override
    protected boolean canDespawn() {
        return false;
    }

    public void setHorseSpecs() {
        EnumHorseType type = EnumHorseType.VALUES[this.getType()];
        this.updateHealth(type.health);
        this.moveSpeed = type.moveSpeed;
    }

    public void setType(EnumHorseType type) {
        this.dataWatcher.updateObject(20, (short)type.ordinal());
        this.chooseTexture();
        this.setHorseSpecs();
    }

    public ResourceLocation getHorseTexture() {
        return EnumHorseType.VALUES[this.getType()].textures[this.dataWatcher.getWatchableObjectShort(21)];
    }

    protected void chooseTexture() {
        int textureId = this.rand.nextInt(EnumHorseType.VALUES[this.getType()].textures.length);
        this.dataWatcher.updateObject(21, (short)textureId);
    }

    private void updateHealth(float health) {
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(health);
        this.setHealth(health);
    }
}

