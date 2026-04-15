package com.voidsrift.riftflux.palaria.entity;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.palaria.PalariaMobDrops;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class EntityCowasaurus extends EntityMob {
    public EntityCowasaurus(World world) {
        super(world);
        setSize(2.2F, 2.2F);
        getNavigator().setCanSwim(true);
        getNavigator().setBreakDoors(true);
        experienceValue = 70;
        tasks.addTask(0, new EntityAISwimming(this));
        tasks.addTask(1, new EntityAIAttackOnCollide(this, EntityEnderWalker.class, 0.4D, true));
        tasks.addTask(2, new EntityAIAttackOnCollide(this, EntityPlayer.class, 0.35D, false));
        tasks.addTask(3, new EntityAIAttackOnCollide(this, EntityVillager.class, 0.35D, false));
        tasks.addTask(4, new EntityAIAttackOnCollide(this, EntityRaptorChicken.class, 0.35D, true));
        tasks.addTask(5, new EntityAIAttackOnCollide(this, EntityAnimal.class, 0.35D, false));
        tasks.addTask(6, new EntityAIWander(this, 0.35D));
        tasks.addTask(7, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        tasks.addTask(8, new EntityAILookIdle(this));
        targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
        targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 0, true));
        targetTasks.addTask(3, new EntityAINearestAttackableTarget(this, EntityVillager.class, 0, true));
        targetTasks.addTask(4, new EntityAINearestAttackableTarget(this, EntityRaptorChicken.class, 0, true));
        targetTasks.addTask(5, new EntityAINearestAttackableTarget(this, EntityAnimal.class, 0, true));
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(ModConfig.palariaCowasaurusMaxHealth);
        getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.35D);
        getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(60.0D);
    }

    @Override
    public EnumCreatureAttribute getCreatureAttribute() {
        return EnumCreatureAttribute.UNDEFINED;
    }

    @Override
    protected String getLivingSound() {
        return "riftflux:palaria_cowasaurus_say";
    }

    @Override
    protected String getHurtSound() {
        return "riftflux:palaria_cowasaurus_hurt";
    }

    @Override
    protected String getDeathSound() {
        return "riftflux:palaria_cowasaurus_death";
    }

    @Override
    protected void func_145780_a(int x, int y, int z, Block block) {
        playSound("riftflux:palaria_cowasaurus_step", 1.0F, 1.0F);
    }

    @Override
    protected void dropFewItems(boolean recentlyHit, int looting) {
        PalariaMobDrops.dropConfigured(this, ModConfig.palariaCowasaurusDropEntries);
    }

    @Override
    public boolean attackEntityAsMob(Entity target) {
        return super.attackEntityAsMob(target);
    }
}
