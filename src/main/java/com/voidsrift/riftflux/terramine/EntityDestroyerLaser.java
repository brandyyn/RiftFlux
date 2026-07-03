package com.voidsrift.riftflux.terramine;

import com.voidsrift.riftflux.ModConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class EntityDestroyerLaser extends EntityThrowable {
    private static final int WATCHER_CRITICAL = 16;

    private float damage = 4.5F;
    private int arrowShake;

    public EntityDestroyerLaser(World world) {
        super(world);
        this.setSize(0.5F, 0.5F);
    }

    public EntityDestroyerLaser(World world, EntityLivingBase thrower) {
        super(world, thrower);
        this.setSize(0.5F, 0.5F);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataWatcher.addObject(WATCHER_CRITICAL, Byte.valueOf((byte) 0));
    }

    @Override
    protected float getGravityVelocity() {
        return 0.0F;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (this.arrowShake > 0) {
            --this.arrowShake;
        }
        if (this.ticksExisted > Math.max(20, ModConfig.destroyerLaserLifetimeTicks)) {
            this.setDead();
            return;
        }

        for (int i = 0; i < (this.isStrong() ? 3 : 1); i++) {
            this.worldObj.spawnParticle(
                    "reddust",
                    this.posX + (this.rand.nextDouble() - 0.5D) * 0.25D,
                    this.posY + (this.rand.nextDouble() - 0.5D) * 0.25D,
                    this.posZ + (this.rand.nextDouble() - 0.5D) * 0.25D,
                    0.9D,
                    0.0D,
                    0.0D
            );
        }
    }

    @Override
    protected void onImpact(MovingObjectPosition hit) {
        if (this.worldObj.isRemote) {
            return;
        }

        if (hit != null && hit.entityHit != null && this.canHitEntity(hit.entityHit)) {
            EntityLivingBase thrower = this.getThrower();
            DamageSource source = DamageSource.causeThrownDamage(this, thrower == null ? this : thrower).setProjectile();
            hit.entityHit.attackEntityFrom(source, this.getImpactDamage(hit.entityHit));
            this.worldObj.playSoundAtEntity(hit.entityHit, "riftflux:destroyer_laserhit", 1.0F, 1.0F / (this.rand.nextFloat() * 0.2F + 0.9F));
        } else if (hit != null) {
            this.arrowShake = 7;
            this.worldObj.playSoundAtEntity(this, "riftflux:destroyer_laserhit", 1.0F, 1.0F / (this.rand.nextFloat() * 0.2F + 0.9F));
        }
        this.setDead();
    }

    private float getImpactDamage(Entity entity) {
        float amount = Math.max(0.0F, this.damage);
        if (this.isStrong() && entity instanceof EntityLivingBase) {
            amount += ((EntityLivingBase) entity).getMaxHealth() * Math.max(0.0F, ModConfig.destroyerHeadLaserMaxHealthPercent);
        }
        return amount;
    }

    private boolean canHitEntity(Entity entity) {
        if (entity == null || entity == this.getThrower()) {
            return false;
        }
        return !(entity instanceof EntityDestroyerBase) && !(entity instanceof EntityDestroyerProbe);
    }

    public void setDamage(float damage) {
        this.damage = damage;
    }

    public void setStrong(boolean strong) {
        byte current = this.dataWatcher.getWatchableObjectByte(WATCHER_CRITICAL);
        if (strong) {
            this.dataWatcher.updateObject(WATCHER_CRITICAL, Byte.valueOf((byte) (current | 1)));
        } else {
            this.dataWatcher.updateObject(WATCHER_CRITICAL, Byte.valueOf((byte) (current & -2)));
        }
    }

    public boolean isStrong() {
        return (this.dataWatcher.getWatchableObjectByte(WATCHER_CRITICAL) & 1) != 0;
    }

    public int getArrowShake() {
        return this.arrowShake;
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
    public void writeEntityToNBT(NBTTagCompound tag) {
        super.writeEntityToNBT(tag);
        tag.setFloat("Damage", this.damage);
        tag.setBoolean("Strong", this.isStrong());
        tag.setByte("Shake", (byte) this.arrowShake);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tag) {
        super.readEntityFromNBT(tag);
        this.damage = tag.getFloat("Damage");
        this.setStrong(tag.getBoolean("Strong"));
        this.arrowShake = tag.getByte("Shake") & 255;
    }
}
