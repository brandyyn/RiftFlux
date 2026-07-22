/*
 * Decompiled with CFR 0.152.
 */
package de.sanandrew.mods.claysoldiers.entity.mount;

import de.sanandrew.core.manpack.util.EnumNbtTypes;
import de.sanandrew.core.manpack.util.javatuples.Triplet;
import de.sanandrew.mods.claysoldiers.entity.EntityClayMan;
import de.sanandrew.mods.claysoldiers.entity.mount.IMount;
import de.sanandrew.mods.claysoldiers.entity.projectile.ISoldierProjectile;
import de.sanandrew.mods.claysoldiers.util.IDisruptable;
import de.sanandrew.mods.claysoldiers.util.mount.EnumGeckoType;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class EntityGeckoMount
extends EntityCreature
implements IMount,
IDisruptable {
    protected static final int DW_TYPE = 20;
    protected static final int DW_CLIMBABLE = 21;
    public boolean spawnedFromNexus = false;
    public ItemStack dollItem = null;
    private float tmpFallDistance;

    public EntityGeckoMount(World world) {
        super(world);
        this.stepHeight = 0.1f;
        this.renderDistanceWeight = 5.0;
        this.setSize(0.35f, 0.7f);
    }

    public EntityGeckoMount(World world, EnumGeckoType type) {
        this(world);
        this.setType(type);
        this.worldObj.playSoundAtEntity(this, "step.wood", 0.8f, ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2f + 1.0f) * 0.9f);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataWatcher.addObject(20, (short)0);
        this.dataWatcher.addObject(21, (byte)0);
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(30.0);
    }

    @Override
    public float getAIMoveSpeed() {
        return 0.5f;
    }

    @Override
    public double getMountedYOffset() {
        return super.getMountedYOffset() - 0.42;
    }

    @Override
    public void updateEntityActionState() {
        if (this.riddenByEntity == null || !(this.riddenByEntity instanceof EntityClayMan)) {
            super.updateEntityActionState();
        } else {
            EntityClayMan rider = (EntityClayMan)this.riddenByEntity;
            this.isJumping = rider.isJumping();
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
        nbt.setShort("geckoType", (short)this.getType());
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
        this.setType(EnumGeckoType.VALUES[nbt.getShort("geckoType")]);
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
        return this.isBesideClimbableBlock();
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
    public EntityGeckoMount setSpawnedFromNexus() {
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
    public void onUpdate() {
        super.onUpdate();
        if (!this.worldObj.isRemote) {
            boolean hasGround = false;
            for (int i = 1; this.isCollidedHorizontally && !hasGround && i <= 4; ++i) {
                Triplet<Integer, Integer, Integer> blockCoords = Triplet.with(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY) - i, MathHelper.floor_double(this.posZ));
                Block block = this.worldObj.getBlock(blockCoords.getValue0(), blockCoords.getValue1(), blockCoords.getValue2());
                if (block == null || block.isAir(this.worldObj, blockCoords.getValue0(), blockCoords.getValue1(), blockCoords.getValue2())) continue;
                hasGround = true;
            }
            this.setBesideClimbableBlock(this.isCollidedHorizontally && hasGround);
        }
        this.tmpFallDistance = this.tmpFallDistance > 3.5f ? this.fallDistance + 3.5f : this.fallDistance;
        if (this.fallDistance > 3.5f && this.tmpFallDistance < 7.0f) {
            this.fallDistance -= 3.5f;
        }
    }

    @Override
    protected boolean canDespawn() {
        return false;
    }

    public void setType(EnumGeckoType type) {
        this.dataWatcher.updateObject(20, (short)type.ordinal());
    }

    public ResourceLocation[] getGeckoTexture() {
        return EnumGeckoType.VALUES[this.getType()].textures;
    }

    @Override
    protected String getHurtSound() {
        return "step.wood";
    }

    @Override
    protected String getDeathSound() {
        return "step.wood";
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
    }

    private boolean isBesideClimbableBlock() {
        return this.dataWatcher.getWatchableObjectByte(21) == 1;
    }

    private void setBesideClimbableBlock(boolean climbable) {
        this.dataWatcher.updateObject(21, (byte)(climbable ? 1 : 0));
    }
}

