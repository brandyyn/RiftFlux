package com.voidsrift.riftflux.pumpkinpastures;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class EntityPumpkinSkeleton extends EntitySkeleton {
    public EntityPumpkinSkeleton(World world) {
        super(world);
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(24.0D);
        getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.25D);
        getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(30.0D);
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        if (!worldObj.isRemote && worldObj.isDaytime() && isBurning()) {
            extinguish();
        }
    }

    @Override
    protected void dropFewItems(boolean recentlyHit, int looting) {
        super.dropFewItems(recentlyHit, looting);
        PumpkinPasturesContent.dropSoulItems(this, looting);
    }

    @Override
    protected String getLivingSound() {
        return "mob.skeleton.say";
    }

    @Override
    protected String getHurtSound() {
        return "mob.skeleton.hurt";
    }

    @Override
    protected String getDeathSound() {
        return "mob.skeleton.death";
    }

    @Override
    protected void func_145780_a(int x, int y, int z, net.minecraft.block.Block block) {
        this.playSound("mob.skeleton.step", 0.15F, 1.0F);
    }

    @Override
    public void onDeath(DamageSource source) {
        super.onDeath(source);
    }
}
