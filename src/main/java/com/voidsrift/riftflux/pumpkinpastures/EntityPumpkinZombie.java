package com.voidsrift.riftflux.pumpkinpastures;

import net.minecraft.entity.Entity;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class EntityPumpkinZombie extends EntityZombie {
    public EntityPumpkinZombie(World world) {
        super(world);
        this.experienceValue = 6;
        this.setVillager(false);
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(30.0D);
        getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.25D);
        getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(2.0D);
        getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setBaseValue(0.8D);
        getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(30.0D);
    }

    @Override
    public boolean attackEntityAsMob(Entity target) {
        boolean hit = super.attackEntityAsMob(target);
        if (hit && target instanceof net.minecraft.entity.EntityLivingBase) {
            ((net.minecraft.entity.EntityLivingBase) target).addPotionEffect(new PotionEffect(Potion.wither.id, 200, 0));
        }
        return hit;
    }

    @Override
    public void onLivingUpdate() {
        if (this.isVillager()) {
            this.setVillager(false);
        }
        super.onLivingUpdate();
        if (!worldObj.isRemote && worldObj.isDaytime() && isBurning()) {
            extinguish();
        }
    }

    @Override
    public IEntityLivingData onSpawnWithEgg(IEntityLivingData data) {
        IEntityLivingData spawned = super.onSpawnWithEgg(data);
        this.setVillager(false);
        return spawned;
    }

    @Override
    protected void dropFewItems(boolean recentlyHit, int looting) {
        super.dropFewItems(recentlyHit, looting);
        PumpkinPasturesContent.dropSoulItems(this, looting);
    }

    @Override
    protected String getLivingSound() {
        return "mob.zombie.say";
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
    public void onDeath(DamageSource source) {
        super.onDeath(source);
    }
}
