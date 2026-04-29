package com.voidsrift.riftflux.terramine;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.net.sync.EntitySyncHelper;
import com.voidsrift.riftflux.net.sync.IEntitySyncData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityDemonEye extends EntityMob implements IEntitySyncData {
    private ChunkCoordinates currentFlightTarget;
    private byte batFlags;

    public EntityDemonEye(World world) {
        super(world);
        this.setSize(0.75F, 0.75F);
        this.setIsBatHanging(true);
        this.experienceValue = 8;
        this.isImmuneToFire = false;
    }

    @Override
    protected void entityInit() {
        super.entityInit();
    }

    @Override
    protected float getSoundVolume() {
        return 1.0F;
    }

    @Override
    protected float getSoundPitch() {
        return super.getSoundPitch() * 0.95F;
    }

    @Override
    protected String getLivingSound() {
        return "mob.ghast.moan";
    }

    @Override
    protected String getHurtSound() {
        return "mob.zombie.hurt";
    }

    @Override
    protected String getDeathSound() {
        return "mob.zombie.death";
    }

    @Override
    public boolean canBePushed() {
        return false;
    }

    @Override
    protected void collideWithEntity(Entity entity) {
    }

    @Override
    protected void collideWithNearbyEntities() {
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(Math.max(1.0D, ModConfig.demonEyeHealth));
        this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(40.0D);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.23D);
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(3.0D);
    }

    public boolean getIsBatHanging() {
        return (this.batFlags & 1) != 0;
    }

    public void setIsBatHanging(boolean hanging) {
        byte flags = this.batFlags;
        if (hanging) {
            flags = (byte) (flags | 1);
        } else {
            flags = (byte) (flags & 0xFFFFFFFE);
        }
        if (this.batFlags != flags) {
            this.batFlags = flags;
            EntitySyncHelper.sync(this);
        }
    }

    @Override
    protected boolean isAIEnabled() {
        return true;
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        if (!this.worldObj.isRemote && this.worldObj.isDaytime() && this.worldObj.canBlockSeeTheSky(
                MathHelper.floor_double(this.posX),
                MathHelper.floor_double(this.posY),
                MathHelper.floor_double(this.posZ)
        )) {
            this.setFire(8);
        }
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (this.getIsBatHanging()) {
            this.motionZ = 0.0D;
            this.motionY = 0.0D;
            this.motionX = 0.0D;
            this.posY = MathHelper.floor_double(this.posY) + 1.0D - this.height;
        } else {
            this.motionY *= 0.6D;
        }
    }

    @Override
    protected void updateAITasks() {
        super.updateAITasks();

        if (this.getIsBatHanging()) {
            if (!this.worldObj.getBlock(MathHelper.floor_double(this.posX), (int) this.posY + 1, MathHelper.floor_double(this.posZ)).isNormalCube()) {
                this.setIsBatHanging(false);
                this.worldObj.playAuxSFXAtEntity(null, 1015, (int) this.posX, (int) this.posY, (int) this.posZ, 0);
            } else {
                if (this.rand.nextInt(200) == 0) {
                    this.rotationYawHead = this.rand.nextInt(360);
                }
                if (this.worldObj.getClosestPlayerToEntity(this, 4.0D) != null) {
                    this.setIsBatHanging(false);
                    this.worldObj.playAuxSFXAtEntity(null, 1015, (int) this.posX, (int) this.posY, (int) this.posZ, 0);
                }
            }
        } else {
            if (!(this.currentFlightTarget == null || this.worldObj.isAirBlock(this.currentFlightTarget.posX, this.currentFlightTarget.posY, this.currentFlightTarget.posZ) && this.currentFlightTarget.posY >= 1)) {
                this.currentFlightTarget = null;
            }

            if (this.currentFlightTarget == null || this.rand.nextInt(30) == 0 || this.currentFlightTarget.getDistanceSquared((int) this.posX, (int) this.posY, (int) this.posZ) < 4.0F) {
                this.currentFlightTarget = new ChunkCoordinates(
                        (int) this.posX + this.rand.nextInt(7) - this.rand.nextInt(7),
                        (int) this.posY + this.rand.nextInt(6) - 2,
                        (int) this.posZ + this.rand.nextInt(7) - this.rand.nextInt(7)
                );
            }

            double d0 = this.currentFlightTarget.posX + 0.5D - this.posX;
            double d1 = this.currentFlightTarget.posY + 0.1D - this.posY;
            double d2 = this.currentFlightTarget.posZ + 0.5D - this.posZ;
            this.motionX += (Math.signum(d0) * 0.5D - this.motionX) * 0.1D;
            this.motionY += (Math.signum(d1) * 0.7D - this.motionY) * 0.1D;
            this.motionZ += (Math.signum(d2) * 0.5D - this.motionZ) * 0.1D;
            float f = (float) (Math.atan2(this.motionZ, this.motionX) * 180.0D / Math.PI) - 90.0F;
            float f1 = MathHelper.wrapAngleTo180_float(f - this.rotationYaw);
            this.moveForward = 0.5F;
            this.rotationYaw += f1;

            if (this.rand.nextInt(100) == 0 && this.worldObj.getBlock(MathHelper.floor_double(this.posX), (int) this.posY + 1, MathHelper.floor_double(this.posZ)).isNormalCube()) {
                this.setIsBatHanging(true);
            }
        }
    }

    @Override
    protected boolean canTriggerWalking() {
        return false;
    }

    @Override
    protected void fall(float distance) {
    }

    @Override
    protected void updateFallState(double y, boolean onGround) {
    }

    @Override
    public boolean doesEntityNotTriggerPressurePlate() {
        return true;
    }

    @Override
    protected Item getDropItem() {
        return TerrariaContent.lens;
    }

    @Override
    protected void dropFewItems(boolean recentlyHit, int looting) {
        float lensChance = clampChance(ModConfig.lensDropChance);
        if (this.rand.nextFloat() <= lensChance) {
            this.dropItem(TerrariaContent.lens, 1);
        }

        float blackLensChance = clampChance(ModConfig.blackLensDropChance);
        if (this.rand.nextFloat() <= blackLensChance) {
            this.dropItem(TerrariaContent.blackLens, 1);
        }
    }

    private static float clampChance(float chance) {
        if (chance < 0.0F) {
            return 0.0F;
        }
        if (chance > 1.0F) {
            return 1.0F;
        }
        return chance;
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (this.isEntityInvulnerable()) {
            return false;
        }
        if (!this.worldObj.isRemote && this.getIsBatHanging()) {
            this.setIsBatHanging(false);
        }
        return super.attackEntityFrom(source, amount);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
        this.batFlags = nbt.getByte("BatFlags");
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
        nbt.setByte("BatFlags", this.batFlags);
    }

    @Override
    public boolean getCanSpawnHere() {
        if (this.worldObj.isDaytime()) {
            return false;
        }
        return super.getCanSpawnHere();
    }

    @Override
    public void rf$writeSyncData(NBTTagCompound tag) {
        tag.setByte("BatFlags", this.batFlags);
    }

    @Override
    public void rf$readSyncData(NBTTagCompound tag) {
        this.batFlags = tag.getByte("BatFlags");
    }
}
