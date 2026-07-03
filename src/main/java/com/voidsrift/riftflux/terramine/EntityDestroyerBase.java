package com.voidsrift.riftflux.terramine;

import com.voidsrift.riftflux.ModConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

import java.util.List;

public abstract class EntityDestroyerBase extends EntityMob implements IMob {
    private static final int WATCHER_ARMORED = 17;

    protected int uniqueWormId;
    protected int partId;
    protected double waypointX;
    protected double waypointY;
    protected double waypointZ;
    protected int courseChangeCooldown;
    protected int contactAttackCooldown;
    protected int laserAttackCounter;
    private int burrowSoundCooldown;
    protected EntityLivingBase destroyerTarget;

    public EntityDestroyerBase(World world) {
        super(world);
        this.uniqueWormId = this.rand.nextInt(Integer.MAX_VALUE);
        this.experienceValue = 0;
        this.isImmuneToFire = true;
        this.noClip = true;
        this.ignoreFrustumCheck = true;
        this.setSize(2.0F, 2.0F);
        this.stepHeight = 2.0F;
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataWatcher.addObject(WATCHER_ARMORED, Byte.valueOf((byte) 0));
    }

    @Override
    protected boolean isAIEnabled() {
        return true;
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        setBaseAttribute(SharedMonsterAttributes.movementSpeed, 0.36D);
        setBaseAttribute(SharedMonsterAttributes.followRange, 160.0D);
        setBaseAttribute(SharedMonsterAttributes.attackDamage, this.getContactDamage());
    }

    protected void setBaseAttribute(IAttribute attribute, double baseValue) {
        if (attribute == null) {
            return;
        }
        IAttributeInstance instance = this.getEntityAttribute(attribute);
        if (instance == null) {
            try {
                this.getAttributeMap().registerAttribute(attribute);
            } catch (IllegalArgumentException ignored) {
                // The attribute may already be present on this entity class.
            }
            instance = this.getEntityAttribute(attribute);
        }
        if (instance != null) {
            instance.setBaseValue(baseValue);
        }
    }

    @Override
    public void onLivingUpdate() {
        if (!this.worldObj.isRemote && (!ModConfig.destroyerEnabled || this.worldObj.difficultySetting == EnumDifficulty.PEACEFUL)) {
            this.setDead();
            return;
        }

        this.noClip = true;
        this.updateBurrowBounds();
        super.onLivingUpdate();
        this.noClip = true;
        this.updateArmoredFlag();

        if (!this.worldObj.isRemote) {
            if (this.contactAttackCooldown > 0) {
                --this.contactAttackCooldown;
            }
            this.attackTouchingEntities();
        }

        this.renderYawOffset = this.rotationYaw;
        this.rotationYawHead = this.rotationYaw;
        this.prevRotationYawHead = this.rotationYaw;
    }

    private void updateBurrowBounds() {
        if (this.posY < -10.0D) {
            this.setPosition(this.posX, 128.0D, this.posZ);
            this.motionY = 0.0D;
        } else if (this.posY < 3.0D) {
            this.motionY = Math.max(this.motionY, 0.3D);
        }
    }

    protected void steerTowardWaypoint(double acceleration, double maxSpeedSq, double fallSpeed, boolean needsGround) {
        double dx = this.waypointX - this.posX;
        double dy = this.waypointY - this.posY;
        double dz = this.waypointZ - this.posZ;
        double distance = MathHelper.sqrt_double(dx * dx + dy * dy + dz * dz);
        if (distance < 0.001D) {
            distance = 0.001D;
        }

        if (this.courseChangeCooldown-- <= 0) {
            this.courseChangeCooldown = 2 + this.rand.nextInt(5);
            double speedSq = this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ;
            if (speedSq < maxSpeedSq) {
                double adjustedDistance = distance;
                if (needsGround && !this.isCourseTraversable()) {
                    adjustedDistance *= 8.0D;
                }
                this.motionX += dx / adjustedDistance * acceleration;
                this.motionY += dy / adjustedDistance * acceleration;
                this.motionZ += dz / adjustedDistance * acceleration;
            }
        }

        if (needsGround && !this.isCourseTraversable()) {
            this.motionY -= fallSpeed;
        }

        this.moveEntity(this.motionX, this.motionY, this.motionZ);
        this.playBurrowSoundIfNeeded();
        double drag = this.isCourseTraversable() ? 0.98D : 0.995D;
        this.motionX *= drag;
        this.motionY *= drag;
        this.motionZ *= drag;
        this.updateRotationFromMotion();
    }

    private void playBurrowSoundIfNeeded() {
        if (!(this instanceof EntityDestroyerHead) || this.worldObj.isRemote) {
            return;
        }
        if (this.burrowSoundCooldown > 0) {
            --this.burrowSoundCooldown;
        }
        double speedSq = this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ;
        if (this.burrowSoundCooldown <= 0 && speedSq > 0.0025D && this.isCourseTraversable()) {
            this.playSound("riftflux:destroyer_dig", this.getSoundVolume(), 1.0F);
            this.burrowSoundCooldown = 15;
        }
    }
    protected boolean isCourseTraversable() {
        AxisAlignedBB box = this.boundingBox;
        return box != null && !this.worldObj.getCollidingBoundingBoxes(this, box).isEmpty();
    }

    protected void updateRotationFromMotion() {
        double horizontal = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
        if (horizontal > 0.001D || Math.abs(this.motionY) > 0.001D) {
            this.rotationYaw = (float) (Math.atan2(this.motionZ, this.motionX) * 180.0D / Math.PI) - 90.0F;
            this.rotationPitch = (float) (-(Math.atan2(this.motionY, horizontal) * 180.0D / Math.PI));
        }
    }

    protected void attackTouchingEntities() {
        if (this.contactAttackCooldown > 0 || this.boundingBox == null) {
            return;
        }

        List entities = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox.expand(0.5D, 0.5D, 0.5D));
        boolean attacked = false;
        for (Object obj : entities) {
            if (!(obj instanceof EntityLivingBase)) {
                continue;
            }
            EntityLivingBase living = (EntityLivingBase) obj;
            if (!this.canAttackDestroyerTarget(living)) {
                continue;
            }
            attacked |= this.attackDestroyerTarget(living, this.getContactDamage());
        }

        if (attacked) {
            this.contactAttackCooldown = 10;
        }
    }

    protected boolean canAttackDestroyerTarget(EntityLivingBase living) {
        if (living == null || living.isDead || living == this || living instanceof EntityDestroyerBase || living instanceof EntityDestroyerProbe) {
            return false;
        }
        return true;
    }

    protected boolean attackDestroyerTarget(EntityLivingBase target, float damage) {
        boolean hit = target.attackEntityFrom(DamageSource.causeMobDamage(this), damage);
        if (hit) {
            double dx = target.posX - this.posX;
            double dy = target.posY - this.posY;
            double dz = target.posZ - this.posZ;
            double distanceSq = dx * dx + dy * dy + dz * dz + 0.1D;
            target.addVelocity(dx / distanceSq * 2.0D, dy / distanceSq * 1.0D + 0.1D, dz / distanceSq * 2.0D);
            target.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 80, 1));
        }
        return hit;
    }

    protected boolean canSeeTarget(EntityLivingBase target) {
        return target != null && !target.isDead && this.getDistanceSqToEntity(target) <= this.getLaserRange() * this.getLaserRange();
    }

    protected boolean updateLaserAttackCounter(boolean canFire, int warmupTicks, int recoveryTicks) {
        if (canFire) {
            ++this.laserAttackCounter;
            if (this.laserAttackCounter >= Math.max(1, warmupTicks)) {
                this.laserAttackCounter = -Math.max(0, recoveryTicks);
                return true;
            }
        } else if (this.laserAttackCounter > 0) {
            --this.laserAttackCounter;
        }
        return false;
    }
    protected void shootDestroyerLaser(EntityLivingBase target, float damage, float speed, float deviation, boolean strong) {
        if (target == null || this.worldObj.isRemote) {
            return;
        }

        EntityDestroyerLaser laser = new EntityDestroyerLaser(this.worldObj, this);
        laser.setDamage(damage);
        laser.setStrong(strong);
        laser.setLocationAndAngles(this.posX, this.posY + this.getEyeHeight(), this.posZ, this.rotationYaw, this.rotationPitch);
        double dx = target.posX - this.posX;
        double dy = target.posY + target.getEyeHeight() - laser.posY;
        double dz = target.posZ - this.posZ;
        laser.setThrowableHeading(dx, dy, dz, speed, deviation);
        this.worldObj.spawnEntityInWorld(laser);
        this.worldObj.playSoundAtEntity(this, "riftflux:destroyer_laser", 3.0F, 0.9F + this.rand.nextFloat() * 0.2F);
    }

    protected EntityDestroyerHead findDestroyerHead() {
        if (this instanceof EntityDestroyerHead) {
            return (EntityDestroyerHead) this;
        }
        List heads = this.worldObj.getEntitiesWithinAABB(EntityDestroyerHead.class, this.boundingBox.expand(192.0D, 192.0D, 192.0D));
        for (Object obj : heads) {
            EntityDestroyerHead head = (EntityDestroyerHead) obj;
            if (!head.isDead && head.getUniqueWormId() == this.getUniqueWormId()) {
                return head;
            }
        }
        return null;
    }

    protected void setArmoredFlag(boolean armored) {
        this.dataWatcher.updateObject(WATCHER_ARMORED, Byte.valueOf((byte) (armored ? 1 : 0)));
    }

    protected void updateArmoredFlag() {
        EntityDestroyerHead head = this.findDestroyerHead();
        this.setArmoredFlag(head != null && head.isArmored());
    }

    public boolean isArmoredClient() {
        return this.dataWatcher.getWatchableObjectByte(WATCHER_ARMORED) != 0;
    }

    @Override
    public int getBrightnessForRender(float partialTick) {
        return 0xF000F0;
    }

    @Override
    public float getBrightness(float partialTick) {
        return 1.0F;
    }

    protected float getLaserRange() {
        return 150.0F;
    }

    public int getUniqueWormId() {
        return this.uniqueWormId;
    }

    public void setUniqueWormId(int uniqueWormId) {
        this.uniqueWormId = uniqueWormId;
    }

    public int getPartId() {
        return this.partId;
    }

    public void setPartId(int partId) {
        this.partId = partId;
    }

    @Override
    protected String getHurtSound() {
        return "riftflux:destroyer_hurt";
    }

    @Override
    protected String getDeathSound() {
        return "riftflux:destroyer_explode";
    }

    @Override
    protected float getSoundVolume() {
        return 5.0F;
    }

    public ResourceLocation getTexture() {
        return null;
    }

    protected abstract float getContactDamage();
}
