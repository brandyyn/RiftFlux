package com.voidsrift.riftflux.terramine;

import com.voidsrift.riftflux.ModConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityFlying;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

public class EntityDestroyerProbe extends EntityFlying implements IMob {
    private int spawnerWormId;
    private int laserCooldown;
    private int aliveTime;
    private int attackTick;
    private int aggroCooldown;
    private EntityDestroyerHead spawnerHead;
    private EntityLivingBase target;

    public EntityDestroyerProbe(World world) {
        super(world);
        this.setSize(1.5F, 1.5F);
        this.isImmuneToFire = true;
        this.noClip = true;
        this.experienceValue = 12;
        this.aliveTime = Math.max(200, ModConfig.destroyerProbeLifetimeTicks);
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        setBaseAttribute(SharedMonsterAttributes.maxHealth, Math.max(1.0D, ModConfig.destroyerProbeHealth));
        setBaseAttribute(SharedMonsterAttributes.movementSpeed, 0.42D);
        setBaseAttribute(SharedMonsterAttributes.attackDamage, Math.max(0.0D, ModConfig.destroyerProbeContactDamage));
        setBaseAttribute(SharedMonsterAttributes.followRange, 96.0D);
    }

    private void setBaseAttribute(IAttribute attribute, double baseValue) {
        IAttributeInstance instance = this.getEntityAttribute(attribute);
        if (instance == null) {
            try {
                this.getAttributeMap().registerAttribute(attribute);
            } catch (IllegalArgumentException ignored) {
            }
            instance = this.getEntityAttribute(attribute);
        }
        if (instance != null) {
            instance.setBaseValue(baseValue);
        }
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        this.noClip = true;
        for (int i = 0; i < 2; ++i) {
            this.worldObj.spawnParticle(
                    "largesmoke",
                    this.posX + (this.rand.nextFloat() * this.width * 2.0F) - this.width,
                    this.posY + 0.1D + this.rand.nextFloat() * this.height,
                    this.posZ + (this.rand.nextFloat() * this.width * 2.0F) - this.width,
                    this.rand.nextGaussian() * 0.02D,
                    this.rand.nextGaussian() * 0.02D,
                    this.rand.nextGaussian() * 0.02D
            );
        }
        if (!this.worldObj.isRemote) {
            this.resolveSpawnerHead();
            if (!ModConfig.destroyerEnabled || this.worldObj.difficultySetting == EnumDifficulty.PEACEFUL || --this.aliveTime <= 0 || this.spawnerHead == null || this.spawnerHead.isDead || this.spawnerHead.getHealth() <= 0.0F) {
                this.setDead();
                return;
            }

            if (this.aggroCooldown > 0) {
                --this.aggroCooldown;
            }
            if (!this.isValidProbeTarget(this.target, 96.0D) || this.aggroCooldown <= 0) {
                EntityLivingBase headTarget = this.spawnerHead.getDestroyerTarget();
                this.target = this.isValidProbeTarget(headTarget, 128.0D) ? headTarget : this.findNearestPlayerIncludingCreative(96.0D);
                this.aggroCooldown = 20;
            }

            this.updateProbeMovement();
            if (this.laserCooldown > 0) {
                --this.laserCooldown;
            }
            if (this.attackTick > 0) {
                --this.attackTick;
            }
            if (this.target != null && this.attackTick <= 0 && this.getDistanceSqToEntity(this.target) < 16.0D && this.attackEntityAsMob(this.target)) {
                this.attackTick = 20;
            }
            if (this.target != null && this.laserCooldown <= 0 && this.getDistanceSqToEntity(this.target) < 80.0D * 80.0D) {
                this.shootLaser(this.target);
                this.laserCooldown = Math.max(5, ModConfig.destroyerProbeLaserCooldownTicks);
            }
        }
    }

    private void updateProbeMovement() {
        if (this.target == null) {
            this.motionY += Math.sin(this.ticksExisted * 0.15D) * 0.015D;
            this.moveEntity(this.motionX, this.motionY, this.motionZ);
            this.motionX *= 0.94D;
            this.motionY *= 0.94D;
            this.motionZ *= 0.94D;
            return;
        }

        double dx = this.target.posX - this.posX;
        double dy = this.target.posY + this.target.getEyeHeight() + 1.5D - this.posY;
        double dz = this.target.posZ - this.posZ;
        double distance = MathHelper.sqrt_double(dx * dx + dy * dy + dz * dz);
        if (distance < 0.001D) {
            distance = 0.001D;
        }
        double desired = 10.0D;
        double strength = distance > desired ? 0.08D : -0.035D;
        this.motionX += dx / distance * strength;
        this.motionY += dy / distance * strength;
        this.motionZ += dz / distance * strength;
        this.motionY += Math.sin(this.ticksExisted * 0.3D) * 0.01D;
        double speed = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
        double max = 0.75D;
        if (speed > max) {
            this.motionX = this.motionX / speed * max;
            this.motionY = this.motionY / speed * max;
            this.motionZ = this.motionZ / speed * max;
        }
        this.moveEntity(this.motionX, this.motionY, this.motionZ);
        this.motionX *= 0.92D;
        this.motionY *= 0.92D;
        this.motionZ *= 0.92D;
        this.rotationYaw = (float) (Math.atan2(this.motionZ, this.motionX) * 180.0D / Math.PI) - 90.0F;
        this.renderYawOffset = this.rotationYaw;
        this.rotationYawHead = this.rotationYaw;
    }

    private void resolveSpawnerHead() {
        if (this.spawnerHead != null && !this.spawnerHead.isDead && this.spawnerHead.getUniqueWormId() == this.spawnerWormId) {
            return;
        }

        this.spawnerHead = null;
        java.util.List heads = this.worldObj.getEntitiesWithinAABB(EntityDestroyerHead.class, this.boundingBox.expand(128.0D, 128.0D, 128.0D));
        for (Object obj : heads) {
            EntityDestroyerHead head = (EntityDestroyerHead) obj;
            if (!head.isDead && (this.spawnerWormId == 0 || head.getUniqueWormId() == this.spawnerWormId)) {
                this.spawnerHead = head;
                this.spawnerWormId = head.getUniqueWormId();
                return;
            }
        }
    }

    private boolean isValidProbeTarget(EntityLivingBase target, double range) {
        return target != null
                && !target.isDead
                && target.getHealth() > 0.0F
                && !(target instanceof EntityDestroyerBase)
                && !(target instanceof EntityDestroyerProbe)
                && this.getDistanceSqToEntity(target) <= range * range;
    }

    private EntityPlayer findNearestPlayerIncludingCreative(double range) {
        double bestDistance = range * range;
        EntityPlayer best = null;
        for (Object obj : this.worldObj.playerEntities) {
            if (!(obj instanceof EntityPlayer)) {
                continue;
            }
            EntityPlayer player = (EntityPlayer) obj;
            if (player.isDead || player.getHealth() <= 0.0F) {
                continue;
            }
            double distance = this.getDistanceSqToEntity(player);
            if (distance <= bestDistance) {
                bestDistance = distance;
                best = player;
            }
        }
        return best;
    }

    private void shootLaser(EntityLivingBase target) {
        EntityDestroyerLaser laser = new EntityDestroyerLaser(this.worldObj, this);
        laser.setDamage(Math.max(0.0F, ModConfig.destroyerProbeLaserDamage));
        laser.setLocationAndAngles(this.posX, this.posY + this.height * 0.5D, this.posZ, this.rotationYaw, this.rotationPitch);
        laser.setThrowableHeading(target.posX - this.posX, target.posY + target.getEyeHeight() - laser.posY, target.posZ - this.posZ, 1.9F, 2.0F);
        this.worldObj.spawnEntityInWorld(laser);
        this.worldObj.playSoundAtEntity(this, "riftflux:destroyer_lasersmall", 3.0F, 0.9F + this.rand.nextFloat() * 0.2F);
    }

    @Override
    public int getBrightnessForRender(float partialTick) {
        return 0xF000F0;
    }

    @Override
    public float getBrightness(float partialTick) {
        return 1.0F;
    }

    @Override
    public boolean attackEntityAsMob(Entity entity) {
        if (!(entity instanceof EntityLivingBase) || entity instanceof EntityDestroyerBase || entity instanceof EntityDestroyerProbe) {
            return false;
        }
        EntityLivingBase living = (EntityLivingBase) entity;
        float damage = Math.max(0.0F, ModConfig.destroyerProbeContactDamage)
                + living.getHealth() * Math.max(0.0F, ModConfig.destroyerProbeContactHealthPercent);
        return living.attackEntityFrom(DamageSource.causeMobDamage(this), damage);
    }

    public void setSpawnerWormId(int spawnerWormId) {
        this.spawnerWormId = spawnerWormId;
    }

    public void setTarget(EntityLivingBase target) {
        this.target = target;
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tag) {
        super.writeEntityToNBT(tag);
        tag.setInteger("SpawnerWormID", this.spawnerWormId);
        tag.setInteger("AliveTime", this.aliveTime);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tag) {
        super.readEntityFromNBT(tag);
        this.spawnerWormId = tag.getInteger("SpawnerWormID");
        this.aliveTime = tag.getInteger("AliveTime");
    }

    @Override
    protected void despawnEntity() {
    }
}
